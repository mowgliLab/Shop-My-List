package be.mowglilab.shopmylistwithwidget.utils;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import be.mowglilab.shopmylistwithwidget.R;
import be.mowglilab.shopmylistwithwidget.data.models.ShoppingListModel;
import be.mowglilab.shopmylistwithwidget.data.utils.ShoppingListDAO;
import be.mowglilab.shopmylistwithwidget.interfaces.ListOrganiser;
import be.mowglilab.shopmylistwithwidget.interfaces.Observer;
import be.mowglilab.shopmylistwithwidget.interfaces.Subject;

public class ShoppingListEntityManager implements Subject,
		ListOrganiser<ShoppingListModel> {

	private ArrayList<Observer> observers;
	private ArrayList<ShoppingListModel> mShoppingList;

	private Context context;

	public ShoppingListEntityManager(Context context) {
		observers = new ArrayList<Observer>();
		this.context = context;

		this.populateShoppingList();
	}

	private void populateShoppingList() {
		ShoppingListDAO shopListDB = new ShoppingListDAO(context);

		try {
			shopListDB.openForRead();
			mShoppingList = (ArrayList<ShoppingListModel>) shopListDB
					.getAllWithList();
		} catch (Exception e) {
			Log.e("ShoppingListEntityManager", "ShoppingList populating error"
					+ e.getMessage());
		} finally {
			shopListDB.close();
		}
	}

	public ArrayList<ShoppingListModel> getShoppingList() {
		return mShoppingList;
	}

	public ShoppingListModel getOnWidgetShoppingList() {
		for (ShoppingListModel model : mShoppingList) {
			if (model.isOnWidget()) {
				return model;
			}
		}
		Resources res = context.getResources();

		ShoppingListModel currentShoppingList = new ShoppingListModel();
		currentShoppingList
				.setName(res.getString(R.string.widget_defaulttitle));
		return currentShoppingList;
	}

	// public void moveUp(int index){
	// if(this.canMoveUp(index)){
	// Collections.swap(mList, index, index + 1);
	// }
	// }
	//
	// public void moveDown(int index){
	// if(this.canMoveDown(index)){
	// Collections.swap(mList, index, index - 1);
	// }
	// }
	//
	// public boolean canMoveUp(int index){
	// if(index >= mList.size()){
	// return false;
	// }
	// return true;
	// }
	//
	// public boolean canMoveDown(int index){
	// if(index <= 0){
	// return false;
	// }
	// return true;
	// }
	//
	//
	// public void addAtIndex(int index, ShoppingListModel list){
	// mList.add(index, list);
	// }
	//
	// public void moveTo(int indexFrom, int indexTo){
	// ShoppingListModel temp = this.remove(indexFrom);
	// this.add(indexTo, temp);
	// }
	//
	//
	//

	public boolean add(ShoppingListModel s) {
		boolean result = mShoppingList.add(s);

		// TODO extract to assync task
		ShoppingListDAO shopListDAO = new ShoppingListDAO(context);

		try {
			shopListDAO.openForWrite();
			shopListDAO.insert(s);
		} catch (Exception e) {
			Log.e(this.getClass().getName(),
					"ShoppingList add error" + e.getMessage());
		} finally {
			shopListDAO.close();
		}

		this.notifyObserver();
		return result;
	}

	public ShoppingListModel remove(ShoppingListModel s) {
		int objectIndex = mShoppingList.indexOf(s);

		ArticleEntityManager articleManager = new ArticleEntityManager(context,
				s);
		articleManager.removeAll();

		ShoppingListDAO shopListDAo = new ShoppingListDAO(context);
		try {
			shopListDAo.openForWrite();
			shopListDAo.delete(s);
		} catch (Exception e) {
			Log.e("ShoppingListEntityManager",
					"ShoppingList remove error" + e.getMessage());
		} finally {
			shopListDAo.close();
		}

		ShoppingListModel result = mShoppingList.remove(objectIndex);
		this.notifyObserver();

		return result;
	}

	@Override
	public ShoppingListModel update(ShoppingListModel s) {
		ShoppingListDAO shopListDAO = new ShoppingListDAO(context);
		ShoppingListModel result = new ShoppingListModel();

		try {
			shopListDAO.openForWrite();
			result = shopListDAO.update(s);
		} catch (Exception e) {
			Log.e("ShoppingListEntityManager",
					"ShoppingList update error" + e.getMessage());
		} finally {
			shopListDAO.close();
		}
		
		this.notifyObserver();
		return result;
	}

	@Override
	public void moveUp(int index) {
		// TODO Auto-generated method stub

	}

	@Override
	public void moveDown(int index) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean canMoveUp(int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canMoveDown(int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void addAtIndex(int index, ShoppingListModel list) {
		// TODO Auto-generated method stub

	}

	@Override
	public void moveTo(int indexFrom, int indexTo) {
		// TODO Auto-generated method stub

	}

	@Override
	public void register(Observer o) {
		observers.add(o);
	}

	@Override
	public void unregister(Observer o) {
		int observerIndex = observers.indexOf(o);
		observers.remove(observerIndex);
	}

	@Override
	public void notifyObserver() {
		for (Observer observer : observers) {
			observer.update();
		}
	}

}
