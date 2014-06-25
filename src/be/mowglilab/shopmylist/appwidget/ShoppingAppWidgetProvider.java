package be.mowglilab.shopmylist.appwidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import be.mowglilab.shopmylist.data.models.ArticleModel;
import be.mowglilab.shopmylist.utils.ArticleEntityManager;

public class ShoppingAppWidgetProvider extends AppWidgetProvider {

	public static final String EXTRA_ITEM_PARCELABLE = "be.mowglilab.shopmylist.EXTRA_ITEM_PARCELABLE";
	public static final String EXTRA_ITEM = "be.mowglilab.shopmylist.EXTRA_ITEM";
	public static final String CHECK_ACTION = "be.mowglilab.shopmylist.CHECK_ACTION";
	public static final String OPEN_LIST_ACTION = "be.mowglilab.shopmylist.OPEN_LIST_ACTION";

	@Override
	public void onReceive(Context context, Intent intent) {

		if (intent.getAction().equals(CHECK_ACTION)) {
			Bundle b = intent.getBundleExtra(EXTRA_ITEM);
			ArticleModel model = b.getParcelable(EXTRA_ITEM_PARCELABLE);

			model.setChecked(!model.isChecked());
			ArticleEntityManager articleManager = new ArticleEntityManager(
					context, model);
			articleManager.update(model);

			Intent refreshWidgets = new Intent(context,
					ShoppingAppWidgetDisplayService.class);
			refreshWidgets
					.setAction(ShoppingAppWidgetDisplayService.ACTION_REFRESH_WIDGET);
			context.startService(refreshWidgets);
		} else if (intent.getAction().equals(OPEN_LIST_ACTION)) {
			Intent openList = new Intent(context,
					ShoppingAppWidgetDisplayService.class);
			openList.setAction(ShoppingAppWidgetDisplayService.ACTION_OPEN_LIST);
			context.startService(openList);
		}

		super.onReceive(context, intent);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {

		Intent intent = new Intent(context,
				ShoppingAppWidgetDisplayService.class);
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
		intent.setAction(ShoppingAppWidgetDisplayService.ACTION_CREATE_WIDGET);
		context.startService(intent);

		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

}
