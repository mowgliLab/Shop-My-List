package be.mowglilab.shopmylistwithwidget.data.utils;

import java.util.List;
import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

public abstract class GenericDAO<T> {

	private static final int DB_VERSION = 1;
	private static final String DB_NAME = "shopping_list.db";

	protected SQLiteDatabase db;
	private ShoppingListOpenHelper dbOpenHelper;

	public GenericDAO(Context context) {
		dbOpenHelper = new ShoppingListOpenHelper(context, DB_NAME, null,
				DB_VERSION);
	}

	public abstract T insert(T c);

	public abstract T update(T c);

	public abstract boolean delete(T c);

	public abstract List<T> getAll();

	@SuppressLint("NewApi")
	public void openForWrite() {
		if (db == null || !db.isOpen()) {
			db = dbOpenHelper.getWritableDatabase();
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
				db.setForeignKeyConstraintsEnabled(true);
			}
		}
	}

	@SuppressLint("NewApi")
	public void openForRead() {
		if (db == null || !db.isOpen()) {
			db = dbOpenHelper.getReadableDatabase();
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
				db.setForeignKeyConstraintsEnabled(true);
			}
		}
	}

	public void close() {
		db.close();
	}

}
