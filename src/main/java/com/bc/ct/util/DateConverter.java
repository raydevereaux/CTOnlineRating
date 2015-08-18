package com.bc.ct.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Custom date converter class for our JAXB needs.  
 */
public class DateConverter {

	private static final Locale locale = new Locale("en", "US");
	private static final String FORMAT_CT_STR = "yyyy-MM-dd";
	
	private static ThreadLocal<DateFormat> dateFormatCT = new ThreadLocal<DateFormat>(){
		/* This builds only one DateFormat instance per thread */
		
		/* This method gets called only once to initialize each thread's DateFormat */
		@Override
		protected DateFormat initialValue() {
			return new SimpleDateFormat(FORMAT_CT_STR, locale);
		}
	};

	public static Date parseDateCT(String dateString){
		if (dateString == null ){
			return null;
		} else {
			try {
				Date date = dateFormatCT.get().parse(dateString);
				return date;
			} catch (ParseException e) {
				throw new RuntimeException("Error in parsing date");
			}
		}
	}
	
	public static String printDateCT(Date date){
		if (date == null){
			return null;
		} else {
			return dateFormatCT.get().format(date.getTime());	
		}
	}
	
}
