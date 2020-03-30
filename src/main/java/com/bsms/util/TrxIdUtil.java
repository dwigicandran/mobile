package com.bsms.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import com.bsms.cons.MbApiConstant;

public class TrxIdUtil {

	public static String getTransactionID(int count) {
		try {

			String result = MbDateFormatUtil.formatDateTrx(new Date());
			Random rand = new Random();
			String[] charset = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };
			StringBuffer sb = new StringBuffer();
			for (int n = 0; n < count; n++) {
				sb = sb.append(charset[rand.nextInt(10)]);
			}

			result += sb.toString();
			return result;
		} catch (Exception ex) {
			return "fail";
		}
	}
}
