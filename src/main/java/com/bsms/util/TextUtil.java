package com.bsms.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class TextUtil {

    //mask string value
    public static String maskString(String strText, int start, int end, char maskChar)
            throws Exception {
        if (strText == null || strText.equals(""))
            return "";
        if (start < 0)
            start = 0;
        if (end > strText.length())
            end = strText.length();
        if (start > end)
            throw new Exception("End index cannot be greater than start index");
        int maskLength = end - start;
        if (maskLength == 0)
            return strText;
        StringBuilder sbMaskString = new StringBuilder(maskLength);
        for (int i = 0; i < maskLength; i++) {
            sbMaskString.append(maskChar);
        }
        return strText.substring(0, start)
                + sbMaskString.toString()
                + strText.substring(start + maskLength);
    }

    //change string data to rupiah currency
    public static String decimalFormater(String value1) {
        double d1 = Double.parseDouble(value1);

        DecimalFormat df2 = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setCurrencySymbol("");
        dfs.setMonetaryDecimalSeparator(',');
        dfs.setGroupingSeparator('.');
        df2.setDecimalFormatSymbols(dfs);

        if ((d1 - (int) d1) != 0) {
            return "Rp. " + df2.format(d1);
        } else {
            String value = df2.format(d1);
            return "Rp. " + value.substring(0, value.length() - 3);
        }
    }

}
