package com.automationrockstars.gir.data.converter;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import org.apache.commons.beanutils.converters.DateTimeConverter;

import com.google.common.base.CharMatcher;

public class SmartDateConverter extends DateTimeConverter{

	public SmartDateConverter() {
		super();
		setPatterns(new String[] {"dd/MM/yyyy","dd-MM-yyyy","dd.MM.yyyy","MM/yyyy","MM-yyyy","MM.yyyy"});
	}
	@Override
	protected Class<?> getDefaultType() {
		return Date.class;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected <T> T convertToType(Class<T> targetType, Object value) throws Exception {
		final Calendar c = Calendar.getInstance();
		final Random rnd = new Random(14232345);
		c.setTime(new Date()); 
		if (targetType.equals(Date.class) && value != null){
			if ( "now".equals(value.toString().toLowerCase()) || "today".equals(value.toString().toLowerCase())){
				return (T) c.getTime();
			} else if ( "yesterday".equals(value.toString().toLowerCase())){
				c.add(Calendar.DATE, -1);
				return (T) c.getTime();
			} else if ( "tomorrow".equals(value.toString().toLowerCase())){
				c.add(Calendar.DATE, 1);
				return (T) c.getTime();
			} else if (value.toString().toLowerCase().startsWith("in ")){
				int amount = Integer.valueOf(CharMatcher.JAVA_DIGIT.retainFrom(value.toString()));
				int type = Calendar.DATE; 
				if (value.toString().toLowerCase().endsWith("months") || value.toString().toLowerCase().endsWith("month")){
					type = Calendar.MONTH;
				} else if (value.toString().toLowerCase().endsWith("year") || value.toString().toLowerCase().endsWith("years")){
					type = Calendar.YEAR;
				}
				c.add(type, amount);
				return (T) c.getTime();
			} else if ("next month".equals(value.toString().toLowerCase())){
				c.add(Calendar.MONTH, 1);
				return (T) c.getTime();
			} else if ("next year".equals(value.toString().toLowerCase())){
				c.add(Calendar.YEAR, 1);
				return (T) c.getTime();
			} else if ("random future".equals(value.toString().toLowerCase())){
				c.add(Calendar.DATE, rnd.nextInt(30));
				c.add(Calendar.MONTH, rnd.nextInt(12));
				c.add(Calendar.YEAR, rnd.nextInt(5));
				return (T) c.getTime();
			} else if ("random past".equals(value.toString().toLowerCase())){
				c.add(Calendar.DATE, -1 *rnd.nextInt(30));
				c.add(Calendar.MONTH, -1 * rnd.nextInt(12));
				c.add(Calendar.YEAR, -1 * rnd.nextInt(30));
				return (T) c.getTime();
			} else if ("random".equals(value.toString().toLowerCase())){
				if (rnd.nextBoolean()){
					c.add(Calendar.DATE, rnd.nextInt(30));
					c.add(Calendar.MONTH, rnd.nextInt(12));
					c.add(Calendar.YEAR, rnd.nextInt(30));
				} else {
					c.add(Calendar.DATE, -1 *rnd.nextInt(30));
					c.add(Calendar.MONTH, -1 * rnd.nextInt(12));
					c.add(Calendar.YEAR, -1 * rnd.nextInt(30));
				}
				return (T) c.getTime();
			} else if (value.toString().toLowerCase().endsWith("years ago")){
				int difference = Integer.valueOf(CharMatcher.JAVA_DIGIT.retainFrom((CharSequence) value));
				c.add(Calendar.YEAR, -1 * difference);
				if (value.toString().startsWith("over")){
					c.add(Calendar.YEAR, -1 * rnd.nextInt(10));	
				}
				return (T) c.getTime();
			} 
		}
		return super.convertToType(targetType, value);
	}

	public static String doubleDay(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return String.format("%1$td", cal);
	}
	public static String day(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return String.format("%1$te", cal);
		
	}
	public static String month(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return String.format("%1$tm", cal);
	}

	public static String year(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return String.format("%1$tY", cal);
	}
}
