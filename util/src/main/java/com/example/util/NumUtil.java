package com.example.util;


import java.math.BigDecimal;

/**
 * 数字格式化，把 10000 转成 1w
 */
public class NumUtil {
    /**
     * @param num 格式化的数字
     * @return
     */
    public static String formatNum(String num) {
        StringBuffer sb = new StringBuffer();
        if (!isNumeric(num))
            return "0";
        BigDecimal b10Thousand = new BigDecimal("10000");
        BigDecimal bThousand = new BigDecimal("1000");
        BigDecimal b1 = new BigDecimal("1");
        BigDecimal b3 = new BigDecimal(num);
        String formatNumStr;
        String nuit = "w";

        // 以万为单位处理
        // 如果小于万就直接显示
        if (b3.compareTo(b10Thousand) == -1) {
            return b3.toString();
        } else if (b3.compareTo(b10Thousand) == 0) {
            return "1w";
        } else {
            //判断求余部分是否 >= 1，如果是要加1000
            if (b3.divideAndRemainder(b10Thousand)[1].compareTo(b1) >= 0) {
                b3 = b3.add(bThousand);
            }
            formatNumStr = b3.divide(b10Thousand).toString();
        }

        if (!"".equals(formatNumStr)) {
            int i = formatNumStr.indexOf(".");
            //如果没有小数点，则直接返回
            if (i == -1) {
                sb.append(formatNumStr).append(nuit);
            } else {
                i = i + 1;
                String v = formatNumStr.substring(i, i + 1);
                if (!v.equals("0")) {
                    sb.append(formatNumStr.substring(0, i + 1)).append(nuit);
                } else {
                    sb.append(formatNumStr.substring(0, i - 1)).append(nuit);
                }
            }
        }
        if (sb.length() == 0)
            return "0";
        return sb.toString();
    }

    private static boolean isNumeric(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}