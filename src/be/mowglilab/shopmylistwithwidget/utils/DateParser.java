package be.mowglilab.shopmylistwithwidget.utils;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import android.util.Log;


public class DateParser {
	
	private static final DateFormat dateTimeFormat = DateFormat.getDateTimeInstance();
	
	private static final DateFormat dateFormat = DateFormat.getDateInstance();
	
	public static Calendar parseDate(String date){
		return parse(date, dateFormat);
	}
	
	public static Calendar parseDateTime(String date){
		return parse(date, dateTimeFormat);
	}
	
	private static Calendar parse(String date, DateFormat parser){
		if(date==null){
			return null;
		}
		try{
			Date d = parser.parse(date);
			Calendar cal = Calendar.getInstance();
			cal.setTime(d);
			return cal;
		} catch (Exception e){
			Log.e("DateParser", e.getMessage(), e);
			return null;
		}
	}
	
	public static String formatDateTime(Calendar cal){
		return format(cal, dateTimeFormat);
	}
	
	public static String formatDate(Calendar cal){
		return format(cal, dateFormat);
	}
	
	private static String format(Calendar cal, DateFormat formatter){
		if(cal==null){
			return null;
		}
		String formattedDate = formatter.format(cal.getTime());
		return formattedDate;
	}

}
