package be.mowglilab.shopmylistwithwidget.appwidget;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import be.mowglilab.shopmylistwithwidget.R;
import be.mowglilab.shopmylistwithwidget.data.models.ArticleModel;
import be.mowglilab.shopmylistwithwidget.data.models.ShoppingListModel;
import be.mowglilab.shopmylistwithwidget.utils.ShoppingListEntityManager;

public class ShoppingAppWidgetService extends RemoteViewsService {

	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) {
		return new ShoppingListRemoteViewFactory(this.getApplicationContext(),
				intent);
	}
	

	class ShoppingListRemoteViewFactory implements RemoteViewsFactory {
		
		private Context mContext;

		private ShoppingListEntityManager mShoppingListEntityManager;
		private ShoppingListModel currentShoppingList;

		public ShoppingListRemoteViewFactory(Context context, Intent intent) {
			this.mContext = context;
		}

		@Override
		public int getCount() {
			return currentShoppingList.getArticles().size();
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public RemoteViews getLoadingView() {
			return null;
		}

		@Override
		public RemoteViews getViewAt(int position) {

			RemoteViews rv = null;

			if (position < getCount()) {
				rv = new RemoteViews(mContext.getPackageName(),
						R.layout.item_widget_article);

				ArticleModel article = currentShoppingList.getArticles().get(
						position);

				if (article.isChecked()) {
					rv.setInt(R.id.txt_name, "setBackgroundResource",
							R.drawable.bg_strikethrough);
				} else {
					rv.setInt(R.id.txt_name, "setBackgroundColor",
							android.R.color.transparent);
				}
				rv.setTextViewText(R.id.txt_name, article.getName());
				rv.setTextViewText(R.id.txt_description,
						article.getDescription());

				Bundle extras = new Bundle();
				extras.putParcelable(
						ShoppingAppWidgetProvider.EXTRA_ITEM_PARCELABLE,
						article);
				Intent fillInIntent = new Intent();
				fillInIntent.putExtra(ShoppingAppWidgetProvider.EXTRA_ITEM,
						extras);
				rv.setOnClickFillInIntent(R.id.item_widget_article,
						fillInIntent);

			}

			return rv;
		}

		@Override
		public int getViewTypeCount() {
			return 1;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public void onCreate() {
		}

		@Override
		public void onDataSetChanged() {
			mShoppingListEntityManager = new ShoppingListEntityManager(mContext);
			currentShoppingList = mShoppingListEntityManager
					.getOnWidgetShoppingList();

		}

		@Override
		public void onDestroy() {
			mShoppingListEntityManager = null;
			currentShoppingList = null;
		}

	}
}
