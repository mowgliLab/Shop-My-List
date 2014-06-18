package be.mowglilab.shopmylistwithwidget.appwidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import be.mowglilab.shopmylistwithwidget.data.models.ArticleModel;
import be.mowglilab.shopmylistwithwidget.utils.ArticleEntityManager;

public class ShoppingAppWidgetProvider extends AppWidgetProvider {

	public static final String EXTRA_ITEM_PARCELABLE = "be.mowglilab.shopmylistwithwidget.EXTRA_ITEM_PARCELABLE";
	public static final String EXTRA_ITEM = "be.mowglilab.shopmylistwithwidget.EXTRA_ITEM";
	public static final String CHECK_ACTION = "be.mowglilab.shopmylistwithwidget.CHECK_ACTION";

	@Override
	public void onReceive(Context context, Intent intent) {

		if (intent.getAction().equals(CHECK_ACTION)) {
			Bundle b = intent.getBundleExtra(EXTRA_ITEM);
			ArticleModel model = b.getParcelable(EXTRA_ITEM_PARCELABLE);

			model.setChecked(!model.isChecked());
			ArticleEntityManager articleManager = new ArticleEntityManager(context, model);
			articleManager.update(model);
			
			Intent refreshWidgets = new Intent(context,
					ShoppingAppWidgetDisplayService.class);
			refreshWidgets
					.setAction(ShoppingAppWidgetDisplayService.ACTION_REFRESH_WIDGET);
			context.startService(refreshWidgets);
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
