package be.mowglilab.shopmylistwithwidget.data.models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;
import be.mowglilab.shopmylistwithwidget.data.utils.MyPublicConstant;
import be.mowglilab.shopmylistwithwidget.utils.DateParser;

public class ShoppingListModel implements Parcelable {

	private long id;
	private String name;
	private Calendar creationDate;
	private List<ArticleModel> articles;
	private boolean isOnWidget;

	public ShoppingListModel() {
		this.articles = new ArrayList<ArticleModel>();
	}

	public ShoppingListModel(long id, String name, Calendar creationDate,
			boolean isOnWidget) {
		super();
		this.id = id;
		this.name = name;
		this.creationDate = creationDate;
		this.articles = new ArrayList<ArticleModel>();
		this.isOnWidget = isOnWidget;
	}

	public ShoppingListModel(Parcel source) {
		this.id = source.readLong();
		this.name = source.readString();
		this.creationDate = DateParser.parseDate(source.readString());
		this.articles = new ArrayList<ArticleModel>();
		source.readList(articles, ShoppingListModel.class.getClassLoader());
		this.setOnWidget(source.readInt());
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Calendar getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Calendar creationDate) {
		this.creationDate = creationDate;
	}

	public List<ArticleModel> getArticles() {
		return articles;
	}

	public void setArticles(List<ArticleModel> articles) {
		this.articles = articles;
	}

	public boolean isOnWidget() {
		return isOnWidget;
	}

	public int isOnWidgetToInt() {
		return this.isOnWidget == true ? MyPublicConstant.TRUE_VALUE
				: MyPublicConstant.FALSE_VALUE;
	}

	public void setOnWidget(boolean isOnWidget) {
		this.isOnWidget = isOnWidget;
	}

	public void setOnWidget(int isOnWidget) {
		this.isOnWidget = isOnWidget == MyPublicConstant.TRUE_VALUE;
	}
	
	public int getTotalCheckedArticlesCount(){
		int result = 0;
		
		for (ArticleModel model : articles) {
			if (model.isChecked()){
				result++;
			}
		}
		
		return result;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ShoppingListModel [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", creationDate=");
		builder.append(creationDate);
		builder.append(", articles=");
		builder.append(articles);
		builder.append(", isOnWidget=");
		builder.append(isOnWidget);
		builder.append("]");
		return builder.toString();
	}

	
	// Parcelable
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(this.id);
		dest.writeString(this.name);
		dest.writeString(DateParser.formatDate(this.creationDate));
		dest.writeList(articles);
		dest.writeInt(this.isOnWidgetToInt());
	}

	public static final Parcelable.Creator<ShoppingListModel> CREATOR = new Parcelable.Creator<ShoppingListModel>() {

		@Override
		public ShoppingListModel createFromParcel(Parcel source) {
			return new ShoppingListModel(source);
		}

		@Override
		public ShoppingListModel[] newArray(int size) {
			return new ShoppingListModel[size];
		}
	};

}
