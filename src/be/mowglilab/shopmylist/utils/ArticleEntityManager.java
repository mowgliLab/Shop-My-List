package be.mowglilab.shopmylist.utils;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import be.mowglilab.shopmylist.data.models.ArticleModel;
import be.mowglilab.shopmylist.data.models.ShoppingListModel;
import be.mowglilab.shopmylist.data.utils.ArticleDAO;
import be.mowglilab.shopmylist.interfaces.ListOrganiser;
import be.mowglilab.shopmylist.interfaces.Observer;
import be.mowglilab.shopmylist.interfaces.Subject;

public class ArticleEntityManager implements Subject,
		ListOrganiser<ArticleModel> {

	private ArrayList<Observer> observers;
	private ShoppingListModel shopListModel;

	private Context context;

	public ArticleEntityManager(Context context, ArticleModel model) {
		this.observers = new ArrayList<Observer>();
		this.context = context;

		this.shopListModel = new ShoppingListModel();
		this.shopListModel.setId(model.getIdShoppinglist());
		this.shopListModel.getArticles().add(model);

	}

	public ArticleEntityManager(Context context,
			ShoppingListModel currentShoppingList) {
		this.observers = new ArrayList<Observer>();
		this.context = context;

		this.shopListModel = currentShoppingList;
		// this.populateArticles();
	}

	// private void populateArticles() {
	// ArticleDAO artDAO = new ArticleDAO(context);
	//
	// try {
	// artDAO.openForRead();
	// shopListModel.setArticles(artDAO
	// .getAllForShoppingList(shopListModel));
	// } catch (Exception e) {
	// Log.e(this.getClass().getName(),
	// "ShoppingList populate error" + e.getMessage());
	// } finally {
	// artDAO.close();
	// }
	//
	// this.notifyObserver();
	// }

	public ShoppingListModel getShoppingList() {
		return shopListModel;
	}

	public List<ArticleModel> getArticles() {
		return this.shopListModel.getArticles();
	}

	@Override
	public boolean add(ArticleModel s) {
		boolean result = shopListModel.getArticles().add(s);

		ArticleDAO artDAO = new ArticleDAO(context);

		try {
			artDAO.openForWrite();
			artDAO.insert(s);
		} catch (Exception e) {
			Log.e(this.getClass().getName(),
					"ShoppingList add error" + e.getMessage());
		} finally {
			artDAO.close();
		}

		this.notifyObserver();
		return result;
	}

	@Override
	public ArticleModel remove(ArticleModel s) {
		int objectIndex = shopListModel.getArticles().indexOf(s);

		ArticleDAO artDAO = new ArticleDAO(context);

		try {
			artDAO.openForWrite();
			artDAO.delete(s);
		} catch (Exception e) {
			Log.e(this.getClass().getName(),
					"ShoppingList remove error" + e.getMessage());
		} finally {
			artDAO.close();
		}

		ArticleModel result = shopListModel.getArticles().remove(objectIndex);
		this.notifyObserver();

		return result;
	}

	public void removeAll() {
		ArticleDAO artDAO = new ArticleDAO(context);

		try {
			artDAO.openForWrite();
			for (ArticleModel articleModel : shopListModel.getArticles()) {
				artDAO.delete(articleModel);
			}
		} catch (Exception e) {
			Log.e(this.getClass().getName(),
					"ShoppingList remove error" + e.getMessage());
		} finally {
			artDAO.close();
		}
		shopListModel.setArticles(new ArrayList<ArticleModel>());

		this.notifyObserver();
	}

	@Override
	public ArticleModel update(ArticleModel s) {
		ArticleDAO artDAO = new ArticleDAO(context);
		ArticleModel result = new ArticleModel();

		try {
			artDAO.openForWrite();
			result = artDAO.update(s);
		} catch (Exception e) {
			Log.e(this.getClass().getName(),
					"ShoppingList Update error" + e.getMessage());
		} finally {
			artDAO.close();
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
	public void addAtIndex(int index, ArticleModel list) {
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
