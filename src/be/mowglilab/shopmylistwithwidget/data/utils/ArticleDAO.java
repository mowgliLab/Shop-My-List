package be.mowglilab.shopmylistwithwidget.data.utils;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import be.mowglilab.shopmylistwithwidget.data.models.ArticleModel;
import be.mowglilab.shopmylistwithwidget.data.models.ShoppingListModel;
import be.mowglilab.shopmylistwithwidget.utils.DateParser;

public class ArticleDAO extends GenericDAO<ArticleModel> {

	public static final String TABLE_ARTICLES = "table_articles";
	public static final String COL_ID = "ID_ARTICLE";
	public static final String COL_NAME = "NAME_ARTICLE";
	public static final String COL_DESCRIPTION = "DESCRIPTION_ARTICLE";
	public static final String COL_DATE = "DATE_ARTICLE";
	public static final String COL_IS_CHECKED = "IS_CHECKED_ARTICLE";
	public static final String COL_FK_SHOPPINGLIST = "FK_SHOPPINGLIST";

	public static final String CREATE_TABLE_ARTICLES = "CREATE TABLE "
			+ TABLE_ARTICLES + " (" + COL_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_NAME
			+ " TEXT NOT NULL, " + COL_DESCRIPTION + " TEXT NOT NULL, "
			+ COL_DATE + " TEXT NOT NULL, " + COL_IS_CHECKED
			+ " INTEGER NOT NULL, " + COL_FK_SHOPPINGLIST
			+ " INTEGER NOT NULL, " + "FOREIGN KEY (" + COL_FK_SHOPPINGLIST
			+ ") REFERENCES " + ShoppingListDAO.TABLE_SHOPPINGLIST + " ("
			+ ShoppingListDAO.COL_ID + ") ON DELETE CASCADE);";

	public ArticleDAO(Context context) {
		super(context);
	}

	@Override
	public ArticleModel insert(ArticleModel c) {
		ContentValues values = this.getCommonContentValues(c);

		c.setId(db.insert(TABLE_ARTICLES, null, values));
		return c;
	}

	@Override
	public ArticleModel update(ArticleModel c) {
		ContentValues values = this.getCommonContentValues(c);

		db.update(TABLE_ARTICLES, values, COL_ID + "=" + c.getId(), null);
		return c;
	}

	@Override
	public boolean delete(ArticleModel c) {
		int row = db.delete(TABLE_ARTICLES, COL_ID + "=" + c.getId(), null);

		return row != 0;
	}

	@Override
	public List<ArticleModel> getAll() {
		List<ArticleModel> results = new ArrayList<ArticleModel>();
		String[] columns = new String[] { COL_ID, COL_NAME, COL_DESCRIPTION,
				COL_DATE, COL_IS_CHECKED, COL_FK_SHOPPINGLIST };
		Cursor cursor = db.query(TABLE_ARTICLES, columns, null, null, null,
				null, COL_NAME + " ASC");

		while (cursor.moveToNext()) {
			results.add(buildObject(cursor));
		}
		return results;
	}

	public List<ArticleModel> getAllForShoppingList(ShoppingListModel s) {
		List<ArticleModel> results = new ArrayList<ArticleModel>();
		String[] columns = new String[] { COL_ID, COL_NAME, COL_DESCRIPTION,
				COL_DATE, COL_IS_CHECKED, COL_FK_SHOPPINGLIST };
		Cursor cursor = db.query(TABLE_ARTICLES, columns, COL_FK_SHOPPINGLIST
				+ "=" + s.getId(), null, null, null, COL_NAME + " ASC");

		while (cursor.moveToNext()) {
			results.add(buildObject(cursor));
		}
		return results;
	}

	public static ArticleModel buildObject(Cursor cursor) {
		ArticleModel am = new ArticleModel();
		am.setId(cursor.getInt(cursor.getColumnIndex(COL_ID)));
		am.setName(cursor.getString(cursor.getColumnIndex(COL_NAME)));
		am.setDescription(cursor.getString(cursor
				.getColumnIndex(COL_DESCRIPTION)));
		am.setCreationDate(DateParser.parseDate(cursor.getString(cursor
				.getColumnIndex(COL_DATE))));
		am.setChecked(cursor.getInt(cursor.getColumnIndex(COL_IS_CHECKED)));
		am.setIdShoppinglist(cursor.getInt(cursor
				.getColumnIndex(COL_FK_SHOPPINGLIST)));

		return am;
	}

	private ContentValues getCommonContentValues(ArticleModel c) {
		ContentValues values = new ContentValues();

		values.put(COL_NAME, c.getName());
		values.put(COL_DESCRIPTION, c.getDescription());
		values.put(COL_DATE, DateParser.formatDate(c.getCreationDate()));
		values.put(COL_IS_CHECKED, c.isCheckedToInt());
		values.put(COL_FK_SHOPPINGLIST, c.getIdShoppinglist());

		return values;
	}

}
