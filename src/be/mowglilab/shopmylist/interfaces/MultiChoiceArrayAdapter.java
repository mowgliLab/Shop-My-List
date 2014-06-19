package be.mowglilab.shopmylist.interfaces;

import android.util.SparseBooleanArray;

/**
 * This interface works with a SparseBooleanArray as class variable. It is use
 * to keep all selected values when a ListView use CHOICE_MODE_MULTIPLE_MODAL
 * and these few methods allow to manage it from the activity.
 * 
 * This sparse boolean array also allow you to change appearance of your row. If
 * you have the position, you know the state of the row.
 * 
 * @author Mowgli
 * 
 */
public interface MultiChoiceArrayAdapter {

	/**
	 * Check or Uncheck the position passed in parameter
	 * 
	 * @param position
	 */
	public void togglesSelection(int position);

	/**
	 * With the position and the value, this method manage our
	 * SparseBooleanArray. (Don't forget to call notifyDataSetChanged())
	 * 
	 * @param position
	 * @param value
	 */
	public void selectView(int position, boolean value);

	/**
	 * Remove all entries in our SparseBooleanArray. (Don't forget to call
	 * notifyDataSetChanged())
	 */
	public void clearSelection();

	/**
	 * @return the size of our SparceBooleanArray.
	 */
	public int getSelectedCount();

	/**
	 * @return our SparseBooleanArray
	 */
	public SparseBooleanArray getSelectedIds();
}
