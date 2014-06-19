package be.mowglilab.shopmylist.appwidget;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.widget.RemoteViews;
import be.mowglilab.shopmylist.activities.DetailListActivity;
import be.mowglilab.shopmylist.activities.MainListActivity;
import be.mowglilab.shopmylist.data.models.ShoppingListModel;
import be.mowglilab.shopmylist.utils.ShoppingListEntityManager;
import be.mowglilab.shopmylist.R;

public class ShoppingAppWidgetDisplayService extends Service {

	public static final String ACTION_CREATE_WIDGET = "be.mowglilab.shopmylist.ACTION_CREATE_WIDGET";
	public static final String ACTION_REFRESH_WIDGET = "be.mowglilab.shopmylist.ACTION_REFRESH_WIDGET";

	private ShoppingListEntityManager shoppingListEntityManager;
	private ShoppingListModel currentShoppingList;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		// Creation of the variables we need
		int[] appWidgetIds;
		AppWidgetManager mgr = AppWidgetManager.getInstance(this
				.getApplicationContext());
		String actionToPerform = intent.getAction();

		// The extra action determine behavior of the service
		if (actionToPerform.equals(ACTION_CREATE_WIDGET)) {
			// If we create a new widget, appWidgetIds is content in the intent
			appWidgetIds = intent
					.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);

			// We construct and define the view
			this.completeView(appWidgetIds, mgr);

			// Stop the service
			stopSelf();
			return START_NOT_STICKY;

		} else if (actionToPerform.equals(ACTION_REFRESH_WIDGET)) {
			// Refresh Widget is call when my activity is hiding, so we find
			// widgetsIds with the appwidgetManager and ComponentName
			ComponentName thisWidget = new ComponentName(
					getApplicationContext(), ShoppingAppWidgetProvider.class);
			appWidgetIds = mgr.getAppWidgetIds(thisWidget);

			// Updating widget informations.
			RemoteViews remoteView = new RemoteViews(this
					.getApplicationContext().getPackageName(),
					R.layout.widget_main_ui);
			remoteView.setTextViewText(R.id.widget_title,
					currentShoppingList.getName());
			remoteView.setProgressBar(R.id.progressBar, currentShoppingList
					.getArticles().size(), currentShoppingList
					.getTotalCheckedArticlesCount(), false);

			mgr.partiallyUpdateAppWidget(appWidgetIds, remoteView);
			mgr.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.my_list);

			stopSelf();
			return START_NOT_STICKY;
		}

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onCreate() {
		if (shoppingListEntityManager == null) {
			shoppingListEntityManager = new ShoppingListEntityManager(
					this.getApplicationContext());
		}
		currentShoppingList = shoppingListEntityManager
				.getOnWidgetShoppingList();

		super.onCreate();
	}

	@Override
	public void onDestroy() {
		shoppingListEntityManager = null;
		currentShoppingList = null;
	}

	private void completeView(int[] appWidgetIds, AppWidgetManager mgr) {
		for (int i = 0; i < appWidgetIds.length; i++) {
			int appWidgetId = appWidgetIds[i];

			Intent remoteViewIntent = new Intent(this.getApplicationContext(),
					ShoppingAppWidgetService.class);
			remoteViewIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
					appWidgetId);

			remoteViewIntent.setData(Uri.parse(remoteViewIntent
					.toUri(Intent.URI_INTENT_SCHEME)));
			RemoteViews rv = new RemoteViews(this.getApplicationContext()
					.getPackageName(), R.layout.widget_main_ui);

			// Configure our widget
			rv.setTextViewText(R.id.widget_title, currentShoppingList.getName());
			rv.setProgressBar(R.id.progressBar, currentShoppingList
					.getArticles().size(), currentShoppingList
					.getTotalCheckedArticlesCount(), false);

			rv.setRemoteAdapter(R.id.my_list, remoteViewIntent);

			rv.setEmptyView(R.id.my_list, android.R.id.empty);

			// Configure on click on list Elements
			Intent CheckIntent = new Intent(this.getApplicationContext(),
					ShoppingAppWidgetProvider.class);
			CheckIntent.setAction(ShoppingAppWidgetProvider.CHECK_ACTION);
			CheckIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
					appWidgetId);
			remoteViewIntent.setData(Uri.parse(remoteViewIntent
					.toUri(Intent.URI_INTENT_SCHEME)));
			PendingIntent checkPendingIntent = PendingIntent.getBroadcast(
					this.getApplicationContext(), 0, CheckIntent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			rv.setPendingIntentTemplate(R.id.my_list, checkPendingIntent);

			// Configure on click on title
			Intent openAppIntent = new Intent(this.getApplicationContext(),
					DetailListActivity.class);
			openAppIntent.putExtra(MainListActivity.EXTRA_SHOPPING_LIST,
					currentShoppingList);
			PendingIntent openListPendingIntent = PendingIntent.getActivity(
					this.getApplicationContext(), 0, openAppIntent, 0);
			rv.setOnClickPendingIntent(R.id.widget_title, openListPendingIntent);

			mgr.updateAppWidget(appWidgetId, rv);

		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}
