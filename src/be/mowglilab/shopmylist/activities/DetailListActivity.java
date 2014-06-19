package be.mowglilab.shopmylist.activities;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import be.mowglilab.shopmylist.appwidget.ShoppingAppWidgetDisplayService;
import be.mowglilab.shopmylist.data.models.ArticleModel;
import be.mowglilab.shopmylist.data.models.ShoppingListModel;
import be.mowglilab.shopmylist.interfaces.Observer;
import be.mowglilab.shopmylist.utils.ArticleEntityManager;
import be.mowglilab.shopmylist.visual.arrayAdapter.ArticleListArrayAdapter;
import be.mowglilab.shopmylist.R;

/**
 * Activity controlling the second screen.
 * 
 * It represent the content of a shopping list with all the articles allowing
 * user to :
 * 
 * - Check or Uncheck items when the user find them.
 * 
 * - Manage articles (organize, edit, add new, delete).
 * 
 * It implements OnItemClickListener to catch item click, OnClickListener to
 * catch click on addButton, and Observer which allow to refresh display when my
 * database is uploaded.
 * 
 * @author Mowgli
 * 
 */
public class DetailListActivity extends Activity implements
		OnItemClickListener, View.OnClickListener, Observer {

	// constant
	private static final int ID_UNCOMPLETE_FIELD_WARN = 16564;

	// private views
	private ListView myList;
	private ImageButton addButton;

	// private attributes
	private ShoppingListModel selectedList;
	private ArticleEntityManager articleEntityManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_with_add_button);
	}

	@Override
	protected void onStart() {
		super.onStart();

		// Get the selected list transfered by the navigation or saved when user
		// close the view
		// TODO Check intent when saved state...
		Intent i = this.getIntent();
		this.selectedList = i
				.getParcelableExtra(MainListActivity.EXTRA_SHOPPING_LIST);
		this.setTitle(selectedList.getName());
		this.getActionBar().setDisplayHomeAsUpEnabled(true);

		// Initializing attributes.
		articleEntityManager = new ArticleEntityManager(this, selectedList);
		articleEntityManager.register(this);
		this.findPrivateViews();

		// Personalizing view attribute's appearance
		this.setAppearance();
		this.setAdapter();

		// personalizing attribute's behavior
		this.addButton.setOnClickListener(this);
		this.myList.setOnItemClickListener(this);
		this.myList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		this.myList.setMultiChoiceModeListener(new MultiChoiceModeListener() {

			private ArticleListArrayAdapter adapter;

			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				MenuInflater inflater = mode.getMenuInflater();
				inflater.inflate(R.menu.context_menu_shoppinglist, menu);

				adapter = (ArticleListArrayAdapter) myList.getAdapter();
				return true;
			}

			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				return false;
			}

			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				Resources res = DetailListActivity.this.getResources();

				// Get an array with all selected Items in the list
				SparseBooleanArray selectedItemsPosition = adapter
						.getSelectedIds();
				ArrayList<ArticleModel> models = new ArrayList<ArticleModel>();

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
					DetailListActivity.this.displayCreationDialog(models.get(0));
					mode.finish();
					return true;

					// case R.id.menuaction_moveUp:
					// // TODO unimplemented method
					// Toast.makeText(DetailListActivity.this, "Move up",
					// Toast.LENGTH_SHORT).show();
					// return true;
					//
					// case R.id.menuaction_moveDown:
					// // TODO unimplemented method
					// Toast.makeText(DetailListActivity.this, "Move down",
					// Toast.LENGTH_SHORT).show();
					// return true;

				case R.id.menuaction_delete:
					// Delete all selected items
					Toast.makeText(
							DetailListActivity.this,
							res.getQuantityString(
									R.plurals.cab_toast_delete_article,
									adapter.getSelectedCount(),
									adapter.getSelectedCount()),
							Toast.LENGTH_SHORT).show();

					this.deleteList(models.toArray(new ArticleModel[adapter
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

				// Set the item selected in our adapter
				if (checked) {
					adapter.selectView(position, checked);
				} else {
					adapter.selectView(position, checked);
				}

				// Set the title of our cab
				Resources res = DetailListActivity.this.getResources();
				mode.setTitle(res.getQuantityString(
						R.plurals.cab_article_title,
						adapter.getSelectedCount(), adapter.getSelectedCount()));

				// Set visibility of menu items : if there is only one item
				// selected, all the actions are available, otherwise, it is
				// only possible to delete objects.
				Menu menu = mode.getMenu();
				if (adapter.getSelectedCount() > 1) {
					menu.findItem(R.id.menuaction_edit).setVisible(false);
					// TODO uncommment when implementation is done
					// menu.findItem(R.id.menuaction_moveUp).setVisible(false);
					// menu.findItem(R.id.menuaction_moveDown).setVisible(false);
				} else if (!menu.findItem(R.id.menuaction_edit).isVisible()) {
					menu.findItem(R.id.menuaction_edit).setVisible(true);
					// menu.findItem(R.id.menuaction_moveUp).setVisible(true);
					// menu.findItem(R.id.menuaction_moveDown).setVisible(true);
				}

			}

			@Override
			public void onDestroyActionMode(ActionMode mode) {
				// Clear array which count selected items in adapter
				adapter.clearSelection();
			}

			private void deleteList(ArticleModel... articles) {
				// Delete parameter ShoppingList in our database
				if (articles.length > 0) {
					for (ArticleModel articleModel : articles) {
						articleEntityManager.remove(articleModel);
					}
				}
			}
		});

	}

	@Override
	protected void onStop() {

		// When screen is hiding, refresh the widget via a service
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
	private void findPrivateViews() {
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
				.getString(R.string.description_add_product));
		addButton.setImageDrawable(res.getDrawable(R.drawable.ic_button_add));
	}

	/**
	 * Set my custom adapter to ListView
	 */
	private void setAdapter() {
		ArticleListArrayAdapter arrayAdapter = new ArticleListArrayAdapter(
				this, R.layout.item_article, selectedList.getArticles());

		myList.setEmptyView(this.findViewById(android.R.id.empty));
		myList.setAdapter(arrayAdapter);
	}

	/**
	 * Display a dialog for creation of a new Article item
	 */
	private void displayCreationDialog() {
		this.displayCreationDialog(null);
	}

	/**
	 * Display a dialog for edition of an Article item
	 * 
	 * @param article
	 *            is the article item to edit
	 */
	private void displayCreationDialog(ArticleModel article) {

		// Getting information about how to build the dialog
		final boolean isDialogForInsert;
		final ArticleModel modelToSave;

		if (article == null) {
			isDialogForInsert = true;
			modelToSave = new ArticleModel();
		} else {
			isDialogForInsert = false;
			modelToSave = article;
		}

		// Building and Showing dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = this.getLayoutInflater();

		builder.setView(inflater.inflate(R.layout.dialog_add_product, null));
		final AlertDialog dialog = builder.create();

		dialog.show();

		// Finding useful views
		EditText editName = (EditText) dialog.findViewById(R.id.edit_name);
		EditText editDescription = (EditText) dialog
				.findViewById(R.id.edit_description);

		// If dialog is made to create a new article
		if (isDialogForInsert) {
			// We set the focus on our first EditText and show softkeyboard
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
		} else {
			// Else, put information from our shopping list item inside fields.
			editName.setText(modelToSave.getName());
			editDescription.setText(modelToSave.getDescription());
		}

		// Setting behavior to buttons
		Button okBtn = (Button) dialog.findViewById(R.id.btn_ok);
		okBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialogAddProduct(isDialogForInsert, modelToSave, dialog, v);
			}
		});

		Button addBtn = (Button) dialog.findViewById(R.id.btn_add);
		addBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialogAddProduct(isDialogForInsert, modelToSave, dialog, v);
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

	private void dialogAddProduct(final boolean isDialogForInsert,
			final ArticleModel modelToSave, final AlertDialog dialog, View v) {
		EditText articleName = (EditText) dialog.findViewById(R.id.edit_name);
		EditText description = (EditText) dialog
				.findViewById(R.id.edit_description);

		// Name should not be null, Description can.
		String name = articleName.getText().toString();
		if (name != null && !name.isEmpty()) {
			modelToSave.setName(name);
			modelToSave.setDescription(description.getText().toString());

			if (isDialogForInsert) {
				modelToSave.setCreationDate(Calendar.getInstance());
				modelToSave.setIdShoppinglist(selectedList.getId());
				modelToSave.setChecked(false);

				articleEntityManager.add(modelToSave);
			} else {
				articleEntityManager.update(modelToSave);
			}
			if (v.getId() == R.id.btn_add) {
				this.displayCreationDialog();
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
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// Implementation of interfaces

	/**
	 * On item click, we change the checked state of our article in the view and
	 * DB
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		CheckedTextView check = (CheckedTextView) view
				.findViewById(R.id.txt_name);
		check.setChecked(!check.isChecked());

		ArticleModel model = (ArticleModel) parent.getItemAtPosition(position);
		model.setChecked(check.isChecked());
		articleEntityManager.update(model);

	}

	@Override
	public void onClick(View v) {
		if (v == addButton) {
			this.displayCreationDialog();
		}
	}

	/**
	 * this method update our view when a change is done in DB.
	 */
	@Override
	public void update() {
		selectedList.setArticles(articleEntityManager.getArticles());

		ArticleListArrayAdapter adapter = (ArticleListArrayAdapter) myList
				.getAdapter();
		adapter.notifyDataSetChanged();
		// myList.setSelection(selectedList.getArticles().size());
	}
}
