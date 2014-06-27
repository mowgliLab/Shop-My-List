package be.mowglilab.shopmylist.activities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import be.mowglilab.shopmylist.appwidget.ShoppingAppWidgetDisplayService;
import be.mowglilab.shopmylist.data.models.ShoppingListModel;
import be.mowglilab.shopmylist.interfaces.Observer;
import be.mowglilab.shopmylist.utils.DateParser;
import be.mowglilab.shopmylist.utils.ShoppingListEntityManager;
import be.mowglilab.shopmylist.visual.arrayAdapter.ShoppingListArrayAdapter;
import be.mowglilab.shopmylist.visual.arrayAdapter.ShoppingListArrayAdapter.ShoppingListArrayAdapterListener;
import be.mowglilab.shopmylist.R;

/**
 * Activity controlling the first screen.
 * 
 * It is the list of all our shopping list allowing user to :
 * 
 * - Navigate to the details of the list
 * 
 * - Checking list to display it on the widget
 * 
 * - Manage list (organize, edit, add new, delete)
 * 
 * It implements OnItemClickListener to catch list item clicks, OnClickListener
 * to catch click on addbutton, ShoppingListArrayAdapter to send callback to my
 * custom ArrayAdapter (managing if list is display on widget or not) and
 * Observer which allow to refresh display when my database is uploaded.
 * 
 * @author Mowgli
 * 
 */
public class MainListActivity extends Activity implements
		AdapterView.OnItemClickListener, View.OnClickListener,
		ShoppingListArrayAdapterListener, Observer {

	// constant
	public static final String EXTRA_SHOPPING_LIST = "extra shopping list";
	private static final int ID_UNCOMPLETE_FIELD_WARN = 16894;

	// private Views
	private ListView myList;
	private ImageButton addButton;

	// privates attributes
	private ShoppingListEntityManager shoppingListEntityManager;
	private List<ShoppingListModel> shoppingLists;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_with_add_button);
		// TODO Check intent when saved state...
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.getMenuInflater().inflate(R.menu.option_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onStart() {
		super.onStart();

		// initializing attributes
		shoppingListEntityManager = new ShoppingListEntityManager(this);
		shoppingListEntityManager.register(this);
		shoppingLists = shoppingListEntityManager.getShoppingList();
		this.findPrivateView();

		// Personalizing view attribute's appearance
		this.setAppearance();
		this.setAdapter();

		// personalizing attribute's behavior
		this.addButton.setOnClickListener(this);
		this.myList.setOnItemClickListener(this);
		this.myList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		this.myList.setMultiChoiceModeListener(new MultiChoiceModeListener() {

			private ShoppingListArrayAdapter adapter;

			// Called when the action mode is created : inflate the menu, and
			// initialize the adapter
			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				// Inflate a menu resource providing context menu items
				MenuInflater inflater = mode.getMenuInflater();
				inflater.inflate(R.menu.context_menu_shoppinglist, menu);

				adapter = (ShoppingListArrayAdapter) myList.getAdapter();
				return true;
			}

			// Called each time the action mode is shown. Always called after
			// onCreateActionMode May be called multiple times if the mode is
			// invalidated.
			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				return false;// return false if nothing is done
				// Here you can perform updates to the CAB due to
				// an invalidate() request
			}

			// Called when the user selects a contextual menu item (an action to
			// perform)
			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				Resources res = MainListActivity.this.getResources();

				// Get an array with all selected Items in the list
				SparseBooleanArray selectedItemsPosition = adapter
						.getSelectedIds();
				ArrayList<ShoppingListModel> models = new ArrayList<ShoppingListModel>();

				if (adapter.getSelectedCount() > 1) {
					for (int i = 0; i < adapter.getSelectedCount(); i++) {
						models.add(adapter.getItem(selectedItemsPosition
								.keyAt(i)));
					}

				} else {
					models.add(adapter.getItem(selectedItemsPosition.keyAt(0)));
				}

				// perform the action
				switch (item.getItemId()) {
				case R.id.menuaction_edit:
					// Show a dialog filled with the object to edit
					MainListActivity.this.displayCreationDialog(models.get(0));
					mode.finish();
					return true;

					// case R.id.menuaction_moveUp:
					// // TODO unimplemented method
					// Toast.makeText(MainListActivity.this, "Move up",
					// Toast.LENGTH_SHORT).show();
					// return true;
					//
					// case R.id.menuaction_moveDown:
					// // TODO unimplemented method
					// Toast.makeText(MainListActivity.this, "Move down",
					// Toast.LENGTH_SHORT).show();
					// return true;

				case R.id.menuaction_delete:
					// Delete all selected items
					Toast.makeText(
							MainListActivity.this,
							res.getQuantityString(
									R.plurals.cab_toast_delete_list,
									adapter.getSelectedCount(),
									adapter.getSelectedCount()),
							Toast.LENGTH_SHORT).show();

					this.deleteList(models
							.toArray(new ShoppingListModel[adapter
									.getSelectedCount()]));

					mode.finish();
					return true;

				default:
					break;
				}
				return false;
			}

			@Override
			public void onItemCheckedStateChanged(ActionMode mode,
					int position, long id, boolean checked) {
				// Here you can do something when items are
				// selected/de-selected,
				// such as update the title in the CAB

				// Set the item selected in our adapter
				if (checked) {
					adapter.selectView(position, checked);
				} else {
					adapter.selectView(position, checked);
				}

				// Set the title of our cab
				Resources res = MainListActivity.this.getResources();
				mode.setTitle(res.getQuantityString(R.plurals.cab_list_title,
						adapter.getSelectedCount(), adapter.getSelectedCount()));

				// Set visibility of menu items : if there is only one item
				// selected, all the actions are available, otherwise, it is
				// only possible to delete objects.
				Menu menu = mode.getMenu();
				if (adapter.getSelectedCount() > 1) {
					menu.findItem(R.id.menuaction_edit).setVisible(false);
					// TODO uncomment when implementation is done
					// menu.findItem(R.id.menuaction_moveUp).setVisible(false);
					// menu.findItem(R.id.menuaction_moveDown).setVisible(false);
				} else if (!menu.findItem(R.id.menuaction_edit).isVisible()) {
					menu.findItem(R.id.menuaction_edit).setVisible(true);
					// menu.findItem(R.id.menuaction_moveUp).setVisible(true);
					// menu.findItem(R.id.menuaction_moveDown).setVisible(true);
				}
			}

			// Called when the user exits the action mode
			@Override
			public void onDestroyActionMode(ActionMode mode) {
				// Here you can make any necessary updates to the activity when
				// the CAB is removed. By default, selected items are
				// deselected/unchecked.

				// Clear array which count selected items in adapter
				adapter.clearSelection();
			}

			private void deleteList(ShoppingListModel... shoppingLists) {
				// Delete parameter ShoppingList in our database
				if (shoppingLists.length > 0) {
					for (ShoppingListModel shoppingListModel : shoppingLists) {
						shoppingListEntityManager.remove(shoppingListModel);
					}
				}
			}
		});

	}

	@Override
	protected void onStop() {

		// when screen is hiding, refresh the widget via a service
		Intent refreshWidgets = new Intent(this,
				ShoppingAppWidgetDisplayService.class);
		refreshWidgets
				.setAction(ShoppingAppWidgetDisplayService.ACTION_REFRESH_WIDGET);
		startService(refreshWidgets);

		super.onStop();
	}

	/**
	 * Initialize privates views
	 */
	private void findPrivateView() {
		myList = (ListView) this.findViewById(R.id.my_list);
		addButton = (ImageButton) this.findViewById(R.id.btn_add);
	}

	/**
	 * Give its appearance to add button (can also be used to define other views
	 * appearances)
	 */
	private void setAppearance() {

		Resources res = this.getResources();

		addButton.setContentDescription(res
				.getString(R.string.description_add_list));
		addButton.setImageDrawable(res
				.getDrawable(R.drawable.ic_button_new_list));
	}

	/**
	 * Set my custom adapter to ListView
	 */
	private void setAdapter() {
		ShoppingListArrayAdapter arrayAdapter = new ShoppingListArrayAdapter(
				this, R.layout.item_shoppinglist, shoppingLists);

		myList.setEmptyView(this.findViewById(android.R.id.empty));
		myList.setAdapter(arrayAdapter);
	}

	/**
	 * Display a dialog for creation of a new ShoppingList item
	 */
	private void displayCreationDialog() {
		this.displayCreationDialog(null);
	}

	/**
	 * Display a dialog for edition of a ShoppingList item
	 * 
	 * @param shoppingList
	 *            is the shopping List item to edit
	 */
	private void displayCreationDialog(ShoppingListModel shoppingList) {

		// Getting information about how to build the dialog
		final boolean isDialogForInsert;
		final ShoppingListModel modelToSave;

		if (shoppingList == null) {
			isDialogForInsert = true;
			modelToSave = new ShoppingListModel();
		} else {
			isDialogForInsert = false;
			modelToSave = shoppingList;
		}

		// Building and showing Dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = this.getLayoutInflater();

		builder.setView(inflater.inflate(R.layout.dialog_add_list, null));
		final AlertDialog dialog = builder.create();

		dialog.show();

		// Finding useful views
		EditText editName = (EditText) dialog.findViewById(R.id.edit_name);
		TextView editDate = (TextView) dialog
				.findViewById(R.id.txt_creationdate);

		// If dialog is made to create a new list
		if (isDialogForInsert) {
			// We set the focus on our first editText and show softKeyboard
			editName.setOnFocusChangeListener(new View.OnFocusChangeListener() {

				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
						dialog.getWindow()
								.setSoftInputMode(
										WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
					}
				}
			});

			// Complete creation date
			editDate.setText(DateParser.formatDate(Calendar.getInstance()));

		} else {
			// put information from our shopping list item inside fields
			editName.setText(modelToSave.getName());
			editDate.setText(DateParser.formatDate(modelToSave
					.getCreationDate()));
		}

		// Setting behavior to buttons
		Button okBtn = (Button) dialog.findViewById(R.id.btn_ok);
		okBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				EditText listName = (EditText) dialog
						.findViewById(R.id.edit_name);

				// Name should not be null!
				String name = listName.getText().toString();
				if (name != null && !name.isEmpty()) {
					modelToSave.setName(name);

					if (isDialogForInsert) {
						modelToSave.setCreationDate(Calendar.getInstance());
						shoppingListEntityManager.add(modelToSave);

					} else {
						shoppingListEntityManager.update(modelToSave);
					}

					dialog.dismiss();
				} else {
					// If name is null, we create a textField to warn the user
					// where are
					// the field he must fill
					TextView warnMessage = (TextView) dialog
							.findViewById(ID_UNCOMPLETE_FIELD_WARN);
					if (warnMessage == null) {
						warnMessage = new TextView(getApplicationContext());
						warnMessage.setId(ID_UNCOMPLETE_FIELD_WARN);
						warnMessage.setText(R.string.warnin_uncomplete_field);
						warnMessage.setTextAppearance(getApplicationContext(),
								R.style.MyTextWarn);
						LinearLayout parent = (LinearLayout) dialog
								.findViewById(R.id.parentView);
						parent.addView(warnMessage, 0);
					}
				}
			}
		});

		Button cancelBtn = (Button) dialog.findViewById(R.id.btn_cancel);
		cancelBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.cancel();
			}
		});
	}

	// Implementation of interfaces

	/**
	 * On Item click, we navigate to detail page to display our shopping list
	 * content.
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent(this, DetailListActivity.class);
		intent.putExtra(MainListActivity.EXTRA_SHOPPING_LIST,
				shoppingLists.get(position));

		startActivity(intent);
	}

	/**
	 * ShoppingListArrayAdapter is in charge to visually check or uncheck
	 * shoppingList items to display it on widget. He then callback this method
	 * to update data in database.
	 */
	@Override
	public void updateDataInDB(ShoppingListModel... shoppingLists) {

		if (shoppingLists.length > 0) {
			for (ShoppingListModel model : shoppingLists) {
				model = shoppingListEntityManager.update(model);

				if (model.isOnWidget()) {
					Resources res = this.getResources();
					Toast.makeText(
							this,
							String.format(
									res.getString(R.string.description_object_visible_on_widget),
									model.getName()), Toast.LENGTH_SHORT)
							.show();
				}
			}
		}

	}

	@Override
	public void onClick(View v) {
		if (v == addButton) {
			this.displayCreationDialog();
		}
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.option_about:
			Intent intent = new Intent(this, AboutActivity.class);
			startActivity(intent);
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	/**
	 * this method update our view when a change is done in DB.
	 */
	@Override
	public void update() {
		shoppingLists = shoppingListEntityManager.getShoppingList();

		ShoppingListArrayAdapter adapter = (ShoppingListArrayAdapter) myList
				.getAdapter();
		adapter.notifyDataSetChanged();
		// myList.setSelection(shoppingLists.size());
	}

}
