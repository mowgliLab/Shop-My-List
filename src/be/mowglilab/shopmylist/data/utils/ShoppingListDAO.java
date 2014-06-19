package be.mowglilab.shopmylist.data.utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import be.mowglilab.shopmylist.data.models.ArticleModel;
import be.mowglilab.shopmylist.data.models.ShoppingListModel;
import be.mowglilab.shopmylist.utils.DateParser;

public class ShoppingListDAO extends GenericDAO<ShoppingListModel> {

	public static final String TABLE_SHOPPINGLIST = "table_shoppinglist";
	public static final String COL_ID = "ID_SHOPPING_LIST";
	public static final String COL_NAME = "NAME_SHOPPING_LIST";
	public static final String COL_DATE = "DATE_SHOPPING_LIST";
	public static final String COL_IS_ON_WIDGET = "IS_ON_WIDGET_SHOPPING_LIST";

	public static final String CREATE_TABLE_SHOPPINGLIST = "CREATE TABLE "
			+ TABLE_SHOPPINGLIST + " (" + COL_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_NAME
			+ " TEXT NOT NULL, " + COL_DATE + " TEXT NOT NULL, "
			+ COL_IS_ON_WIDGET + " INTEGER NOT NULL);";

	public ShoppingListDAO(Context context) {
		super(context);
	}

	@Override
	public ShoppingListModel insert(ShoppingListModel c) {
		ContentValues values = this.getCommonContentValuesForObject(c);

		long id = db.insert(TABLE_SHOPPINGLIST, null, values);
		c.setId(id);
		return c;
	}

	@Override
	public ShoppingListModel update(ShoppingListModel c) {
		ContentValues values = this.getCommonContentValuesForObject(c);

		db.update(TABLE_SHOPPINGLIST, values, COL_ID + "=" + c.getId(), null);
		return c;
	}

	@Override
	public boolean delete(ShoppingListModel c) {
		int row = db.delete(TABLE_SHOPPINGLIST, COL_ID + "=" + c.getId(), null);
		return row != 0;
	}

	@Override
	public List<ShoppingListModel> getAll() {
		List<ShoppingListModel> result = new ArrayList<ShoppingListModel>();
		String[] columns = new String[] { COL_ID, COL_NAME, COL_DATE,
				COL_IS_ON_WIDGET };
		Cursor cursor = db.query(TABLE_SHOPPINGLIST, columns, null, null, null,
				null, COL_ID + " ASC");

		while (cursor.moveToNext()) {
			result.add(buildObject(cursor));
		}
		return result;
	}

	public List<ShoppingListModel> getAllWithList() {
		List<ShoppingListModel> result = new ArrayList<ShoppingListModel>();
		Map<Long, ShoppingListModel> hShoppingList = new LinkedHashMap<Long, ShoppingListModel>();

		String sqlQuery = "SELECT sl." + COL_ID + ", sl." + COL_NAME + ", sl."
				+ COL_DATE + ", sl." + COL_IS_ON_WIDGET + ", a."
				+ ArticleDAO.COL_ID + ", a." + ArticleDAO.COL_NAME + ", a."
				+ ArticleDAO.COL_DESCRIPTION + ", a." + ArticleDAO.COL_DATE
				+ ", a." + ArticleDAO.COL_IS_CHECKED + ", a."
				+ ArticleDAO.COL_FK_SHOPPINGLIST + " FROM "
				+ TABLE_SHOPPINGLIST + " sl LEFT JOIN "
				+ ArticleDAO.TABLE_ARTICLES + " a ON sl." + COL_ID + "=a."
				+ ArticleDAO.COL_FK_SHOPPINGLIST + " ORDER BY sl." + COL_ID + " ASC, a." + ArticleDAO.COL_NAME + " ASC";

		Cursor cursor = db.rawQuery(sqlQuery, null);

		while (cursor.moveToNext()) {
			long id = cursor.getInt(cursor.getColumnIndex(COL_ID));

			ShoppingListModel slm = hShoppingList.get(id);

			if (slm == null) {
				slm = buildObject(cursor);
				hShoppingList.put(id, slm);
			}

			if (cursor.getString(cursor.getColumnIndex(ArticleDAO.COL_NAME)) != null){
				ArticleModel article = ArticleDAO.buildObject(cursor);
				
				slm.getArticles().add(article);			
			}
		}

		result.addAll(hShoppingList.values());
		return result;
	}

	private ShoppingListModel buildObject(Cursor cursor) {
		ShoppingListModel slm = new ShoppingListModel();

		slm.setId(cursor.getInt(cursor.getColumnIndex(COL_ID)));
		slm.setName(cursor.getString(cursor.getColumnIndex(COL_NAME)));
		slm.setOnWidget(cursor.getInt(cursor.getColumnIndex(COL_IS_ON_WIDGET)));
		slm.setCreationDate(DateParser.parseDate(cursor.getString(cursor
				.getColumnIndex(COL_DATE))));

		return slm;
	}

	private ContentValues getCommonContentValuesForObject(ShoppingListModel c) {
		ContentValues values = new ContentValues();
		values.put(COL_NAME, c.getName());
		values.put(COL_DATE, DateParser.formatDate(c.getCreationDate()));
		values.put(COL_IS_ON_WIDGET, c.isOnWidgetToInt());
		return values;
	}

}
