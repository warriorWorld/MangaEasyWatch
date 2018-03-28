package com.warrior.hangsu.administrator.mangaeasywatch.utils;

import java.math.RoundingMode;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016-06-17.
 */
public class NumUtils {
    /**
     * 判断是否是数字?
     *
     * @param str
     * @return
     */
//    public static boolean isNumeric(String str) {
//        for (int i = str.length(); --i >= 0; ) {
//            if (!Character.isDigit(str.charAt(i))) {
//                return false;
//            }
//        }
//        return true;
//    }
    public static String cutInt(String str) {
        String res = str;
        if (isFloatNum(str) && isNumeric(str)) {
            int i = (int) (Float.valueOf(str) * 1);
            res = "" + i;
        }
        return res;
    }

    /*
  * 判断是否为整数
  * @param str 传入的字符串
  * @return 是整数返回true,否则返回false
*/
//    public static boolean isInteger(String str) {
//        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
//        return pattern.matcher(str).matches();
//    }

    //    public static boolean isNumeric(String str) {
//        for (int i = str.length(); --i >= 0; ) {
//            int chr = str.charAt(i);
//            if (chr < 48 || chr > 57)
//                return false;
//        }
//        return true;
//    }
    public static boolean isFloatNum(String str) {
        Pattern pattern = Pattern.compile("-?[0-9]+.?[0-9]+");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    public static boolean isIntNum(String str) {

        Pattern pattern = Pattern.compile("[0-9]*");

        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {

            return false;

        }
        return true;

    }

    private static boolean isNumeric(String str) {
        boolean res = false;
        if (isFloatNum(str)) {
            if (str.contains(".")
                    && Integer.valueOf(str
                    .substring(str.indexOf(".") + 1))
                    == 0
                    ) {
                res = true;
            } else if (!str.contains(".")) {
                res = true;
            }
        }
        return res;
    }

    public static String doubleDecimals(double num) {
        try {
            java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
            df.setRoundingMode(RoundingMode.FLOOR);
            return df.format(num);
        } catch (Exception e) {
            Logger.d(e + ":doubleDecimals");
            return num + "";
        }
    }

    public static String doubleDecimals(String num) {
        try {
            return doubleDecimals(Double.valueOf(num));
        } catch (NumberFormatException e) {
            return num;
        }
    }
}
