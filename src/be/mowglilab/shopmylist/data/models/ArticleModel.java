package be.mowglilab.shopmylist.data.models;

import java.util.Calendar;

import be.mowglilab.shopmylist.data.utils.MyPublicConstant;
import be.mowglilab.shopmylist.utils.DateParser;
import android.os.Parcel;
import android.os.Parcelable;

public class ArticleModel implements Parcelable {

	private long id;
	private String name;
	private String description;
	private Calendar creationDate;
	private boolean isChecked;
	private long idShoppinglist;

	public ArticleModel() {
	}

	public ArticleModel(long id, String name, String description,
			Calendar creationDate, boolean isChecked) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.creationDate = creationDate;
		this.isChecked = isChecked;
	}

	public ArticleModel(Parcel source) {
		this.id = source.readLong();
		this.name = source.readString();
		this.description = source.readString();
		this.creationDate = DateParser.parseDate(source.readString());
		this.setChecked(source.readInt());
		this.idShoppinglist = source.readLong();
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Calendar getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Calendar creationDate) {
		this.creationDate = creationDate;
	}

	public boolean isChecked() {
		return this.isChecked;
	}

	public int isCheckedToInt() {
		return isChecked == true ? MyPublicConstant.TRUE_VALUE
				: MyPublicConstant.FALSE_VALUE;
	}

	public void setChecked(boolean checked) {
		this.isChecked = checked;
	}

	public void setChecked(int checked) {
		this.isChecked = checked == MyPublicConstant.TRUE_VALUE;
	}

	public long getIdShoppinglist() {
		return idShoppinglist;
	}

	public void setIdShoppinglist(long idShoppinglist) {
		this.idShoppinglist = idShoppinglist;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ArticleModel [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", description=");
		builder.append(description);
		builder.append(", creationDate=");
		builder.append(creationDate);
		builder.append(", isChecked=");
		builder.append(isChecked);
		builder.append(", idShoppinglist=");
		builder.append(idShoppinglist);
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
		dest.writeString(this.description);
		dest.writeString(DateParser.formatDate(creationDate));
		dest.writeInt(this.isCheckedToInt());
		dest.writeLong(this.idShoppinglist);
	}

	public static final Parcelable.Creator<ArticleModel> CREATOR = new Parcelable.Creator<ArticleModel>() {

		@Override
		public ArticleModel createFromParcel(Parcel source) {
			return new ArticleModel(source);
		}

		@Override
		public ArticleModel[] newArray(int size) {
			return new ArticleModel[size];
		}
	};

}
