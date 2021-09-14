package com.bsms.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.bsms.cons.MbApiConstant;

public class MbDateFormatUtil {

	public static String formatTime(Date date){
		SimpleDateFormat dateFormat = new SimpleDateFormat(MbApiConstant.TIME_FORMAT);
		return dateFormat.format(date);
	}
	
	public static String formatDate(Date date){
		SimpleDateFormat dateFormat = new SimpleDateFormat(MbApiConstant.DATE_FORMAT);
		return dateFormat.format(date);
	}
	
	public static String formatDateTrx(Date date){
		SimpleDateFormat dateFormat = new SimpleDateFormat(MbApiConstant.DATE_FORMAT_TRX);
		return dateFormat.format(date);
	}
	
	public static String dateTime() {
		String dateTimeFormat = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		return dateTimeFormat;
	}
	
}
