package be.mowglilab.shopmylist.interfaces;

public interface ListOrganiser<T> {
	
	public boolean add(T s);
	public T remove(T s);
	public T update(T s);
	
	public void moveUp(int index);
	public void moveDown(int index);
	public boolean canMoveUp(int index);
	public boolean canMoveDown(int index);
	public void addAtIndex(int index, T list);
	public void moveTo(int indexFrom, int indexTo);

}
