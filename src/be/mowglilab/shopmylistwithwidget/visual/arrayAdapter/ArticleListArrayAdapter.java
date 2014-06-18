package be.mowglilab.shopmylistwithwidget.visual.arrayAdapter;

import java.util.List;

import be.mowglilab.shopmylistwithwidget.R;
import be.mowglilab.shopmylistwithwidget.data.models.ArticleModel;
import be.mowglilab.shopmylistwithwidget.interfaces.MultiChoiceArrayAdapter;
import android.R.color;
import android.content.Context;
import android.content.res.Resources;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

public class ArticleListArrayAdapter extends ArrayAdapter<ArticleModel> implements MultiChoiceArrayAdapter {

	private int viewResource;
	
	private SparseBooleanArray mSelectedItemsIds;
	
	public ArticleListArrayAdapter(Context context, int resource,
			List<ArticleModel> objects) {
		super(context, resource, objects);
		
		this.mSelectedItemsIds = new SparseBooleanArray();
		this.viewResource = resource;

	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ArticleModel am = (ArticleModel) getItem(position);
		
		ViewHolder holder;
		if(convertView == null){
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(viewResource, parent, false);
			
			holder = new ViewHolder();
			holder.name = (CheckedTextView) convertView.findViewById(R.id.txt_name);
			holder.description = (TextView) convertView.findViewById(R.id.txt_description);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.name.setText(am.getName());
		holder.name.setChecked(am.isChecked());
		holder.description.setText(am.getDescription());

		Resources res = this.getContext().getResources();
		convertView.setBackgroundColor(mSelectedItemsIds.get(position) ? res
				.getColor(R.color.list_item_selected) : color.transparent);
		
		return convertView;
	}
	
	@Override
	public void togglesSelection(int position) {
		selectView(position, !mSelectedItemsIds.get(position));		
	}

	@Override
	public void selectView(int position, boolean value) {
		if(value){
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

	private static class ViewHolder{
		CheckedTextView name;
		TextView description;
	}

}
