package com.example.util;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 解析String的工具类
 */
public class StringUtils {

    static String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#¥%⋯⋯&;*（）——+|{}【】‘；：”“’。，、？]";
    static String regEnNumEx = "aAbBcCdDeEfFgGhHiIjJkKlLmMnNoOpPqQrRsStTuUvVwWxXyYzZ0123456789_";

    /**
     * 转码utf-8
     *
     * @param utf8Str
     * @return
     */
    public static String getUtf8(String utf8Str) {
        String utf8StrChange = "";
        try {
            utf8StrChange = URLEncoder.encode(utf8Str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return utf8StrChange;

    }

    /**
     * 过滤换行字符
     */
    public static String replaceTsChar(String str) {
        if (str == null || str.equals("")) {
            return str + "";
        }
        return ""
                + str.replace("&It;br", "").replace("/&gt;", "")
                .replace("&it;br", "").replace("&amp;It;br", "")
                .replace("/&amp;gt;", "").replace("<br />", "");
        // return ""+str;
    }

    /**
     * 判断是否含有特殊符号，除中英文和数字
     *
     * @param str
     * @param context
     * @return
     */
    public static boolean checkIsHaveTs(String str, Context context) {
        boolean isHaveTs = false;
        String afterStr = "";
        if (str == null || str.length() == 0) {
            return isHaveTs;
        }
        // 是否包含空格
        if (str.contains(" ")) {
            isHaveTs = true;
        }
        // 是否包含特殊符号
        for (int i = 0; i < regEx.length(); i++) {
            if ((i + 1) <= regEx.length()) {
                if (str.contains(regEx.substring(i, i + 1))) {
                    // Log.d("--------------------",
                    // "-----------"+str.substring(i, i+1)+"---------");
                    isHaveTs = true;
                }
            }
        }
        // 去掉英文、数字
        for (int i = 0; i < str.length(); i++) {
            if ((i + 1) <= str.length()) {
                if (regEnNumEx.contains(str.substring(i, i + 1))) {
                    // Log.d("--------------------",
                    // "-----------"+str.substring(i, i+1)+"---------");
                    // isHaveTs = true;
                } else {
                    afterStr = afterStr + str.substring(i, i + 1);
                }
            }
        }
        // 判断是否全是中文
        if (afterStr != null && !afterStr.equals("") && afterStr.length() > 0) {
            char[] cTemp = afterStr.toCharArray();
            for (int i = 0; i < afterStr.length(); i++) {
                if (!isChinese(cTemp[i])) {
                    isHaveTs = true;
                    break;
                }
            }
        }

        if (isHaveTs) {
            Toast.makeText(context, "格式不正确", Toast.LENGTH_SHORT).show();
        }
        return isHaveTs;
    }

    /**
     * 判定输入汉字
     *
     * @param c
     * @return
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    /**
     * 字符串转int
     */
    public static int extractNumber(String num) {
        if (TextUtils.isEmpty(num)) {
            return 0;
        }
        int i;
        try {
            String number = num.replaceAll("[^\\d]", "");
            i = Integer.parseInt(number);
        } catch (Exception e) {
            i = 0;
        }
        return i;
    }
}
