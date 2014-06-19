package be.mowglilab.shopmylist.data.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ShoppingListOpenHelper extends SQLiteOpenHelper {
	private static final String TAG_CLASS = "ShoppingListOpenHelper";
		
	public ShoppingListOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		Log.e(TAG_CLASS, "Constructor");
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.e(TAG_CLASS, "onCreate");
		db.execSQL(ShoppingListDAO.CREATE_TABLE_SHOPPINGLIST);
		db.execSQL(ArticleDAO.CREATE_TABLE_ARTICLES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.e(TAG_CLASS, "onUpgrade");
		db.execSQL("DROP TABLE " + ArticleDAO.TABLE_ARTICLES);
		db.execSQL("DROP TABLE " + ShoppingListDAO.TABLE_SHOPPINGLIST);
		onCreate(db);
	}
	
	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.e(TAG_CLASS, "onDowngrade");
		db.execSQL("DROP TABLE " + ArticleDAO.TABLE_ARTICLES);
		db.execSQL("DROP TABLE " + ShoppingListDAO.TABLE_SHOPPINGLIST);
		onCreate(db);
	}

}
