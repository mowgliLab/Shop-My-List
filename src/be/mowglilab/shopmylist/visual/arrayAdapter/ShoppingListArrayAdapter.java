package be.mowglilab.shopmylist.visual.arrayAdapter;

import java.util.List;

import android.R.color;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import be.mowglilab.shopmylist.data.models.ShoppingListModel;
import be.mowglilab.shopmylist.interfaces.MultiChoiceArrayAdapter;
import be.mowglilab.shopmylist.R;

public class ShoppingListArrayAdapter extends ArrayAdapter<ShoppingListModel>
		implements MultiChoiceArrayAdapter {

	ShoppingListArrayAdapterListener callback;
	private int viewResource;
	private int indexOfWidgetList;

	private SparseBooleanArray mSelectedItemsIds;

	public ShoppingListArrayAdapter(Context context, int resource,
			List<ShoppingListModel> collection) {
		super(context, resource, collection);

		// Initializing class variables
		this.mSelectedItemsIds = new SparseBooleanArray();
		this.viewResource = resource;

		// Finding the checked item
		this.indexOfWidgetList = -1;
		int cpt = 0;
		while (indexOfWidgetList == -1 && cpt < collection.size()) {
			if (collection.get(cpt).isOnWidget()) {
				indexOfWidgetList = cpt;
			}
			cpt++;
		}

		// Initializing our callback object
		try {
			callback = (ShoppingListArrayAdapterListener) context;
		} catch (Exception e) {
			Log.e(this.getClass().getName(),
					"Activities whose use this adapter must implement adapter's listener Interface");
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// Getting ShoppingListModel to fill the row and instantiating
		// ViewHolder
		final ShoppingListModel slm = (ShoppingListModel) getItem(position);
		ViewHolder holder;

		// If it is the first time our row is displayed
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(viewResource, parent, false);

			// Getting a new holder and finding views
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.txt_name);
			holder.status = (ProgressBar) convertView
					.findViewById(R.id.progressBar);
			holder.widgetDisplay = (CheckBox) convertView
					.findViewById(R.id.btn_showonlockscreen);

			// Set the holder to the cell
			convertView.setTag(holder);
		} else {

			// Else getting the holder from the tag.
			holder = (ViewHolder) convertView.getTag();
		}

		// Filling informations in our holder's views
		holder.name.setText(slm.getName());

		holder.status.setMax(slm.getArticles().size());
		holder.status.setProgress(slm.getTotalCheckedArticlesCount());

		holder.widgetDisplay.setTag(position);
		holder.widgetDisplay.setChecked(slm.isOnWidget());
		holder.widgetDisplay
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {

						// When the row is hide by scroll or when the view is
						// refresh, the check box become unchecked what cause an
						// new event.
						// This test allow to know if the user really pressed
						// the view or If the state change is the result of
						// a graphical treatment.
						if (buttonView.isPressed()) {

							// We can set its state to our object. Because I
							// want only 1 or 0 item checked, I keep the
							// position of the current checked object in
							// indexOfWidgetList class variable.
							slm.setOnWidget(isChecked);
							int indexCurrentObject = getPosition(slm);

							// If a new object is checked it will also cause
							// that the old checked object will be unchecked.
							ShoppingListModel listToUncheck = null;

							if (isChecked) {
								// Was there an object checked before that I
								// have to uncheck?
								if (indexOfWidgetList != -1) {
									listToUncheck = getItem(indexOfWidgetList);
									listToUncheck.setOnWidget(false);
								}

								// Refresh the position of the current checked
								// object.
								indexOfWidgetList = indexCurrentObject;
							} else {
								// Else if we uncheck our current selected
								// object, indexCurrentObject receive -1 value
								// (! Careful it can cause exceptions !)
								if (indexCurrentObject == indexOfWidgetList) {
									indexOfWidgetList = -1; // nothing is
															// checked
								}
							}
							// We notify observers to refresh the display
							notifyDataSetChanged();

							// We send the action to our attached activity which
							// update datas in the databasse
							if (listToUncheck != null) {
								callback.updateDataInDB(slm, listToUncheck);
							} else {
								callback.updateDataInDB(slm);
							}
						}
					}
				});

		// Colors of background change if the object is selected for the
		// contextual action bar (cab)
		Resources res = this.getContext().getResources();
		convertView.setBackgroundColor(mSelectedItemsIds.get(position) ? res
				.getColor(R.color.list_item_selected) : color.transparent);

		return convertView;
	}

	/*
	 * A way to keep the selected item for the CHOICE_MODE_MULTIPLE_MODAL
	 */
	@Override
	public void togglesSelection(int position) {
		selectView(position, !mSelectedItemsIds.get(position));
	}

	@Override
	public void selectView(int position, boolean value) {
		if (value) {
			mSelectedItemsIds.put(position, value);
		} else {
			mSelectedItemsIds.delete(position);
		}
		notifyDataSetChanged();
	}

	@Override
	public void clearSelection() {
		mSelectedItemsIds = new SparseBooleanArray();
		notifyDataSetChanged();
	}

	@Override
	public int getSelectedCount() {
		return mSelectedItemsIds.size();
	}

	@Override
	public SparseBooleanArray getSelectedIds() {
		return mSelectedItemsIds;
	}

	// The ViewHolder Class is used to keep references to View objects of our
	// row
	private static class ViewHolder {
		TextView name;
		ProgressBar status;
		CheckBox widgetDisplay;
	}

	/**
	 * This interface must be implement in activity which use this adapter. It
	 * allow the adapter to callback the activity to upload data in data base.
	 * 
	 * @author Mowgli
	 * 
	 */
	public interface ShoppingListArrayAdapterListener {
		public void updateDataInDB(ShoppingListModel... shoppingLists);
	}

}
