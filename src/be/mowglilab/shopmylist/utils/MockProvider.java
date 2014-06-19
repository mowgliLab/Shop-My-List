package be.mowglilab.shopmylist.utils;

import java.util.ArrayList;
import java.util.List;

public class MockProvider {
	
	public static List<String> getShoppingListMock(){

		ArrayList<String> myArray= new ArrayList<String>();
		myArray.add("Colruyt1");
		myArray.add("Colruyt2");
		myArray.add("Colruyt3");
		
		return myArray;
	}
	
	public static List<String> getDetailedListMock(){
		
		ArrayList<String> myArray= new ArrayList<String>();
		myArray.add("Honney");
		myArray.add("Eggs");
		myArray.add("Potatoes");
		
		return myArray;
	}

}
