package com.example.util;

import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.widget.EditText;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditHelper {

    private static final String SPE_CHAT = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
    private static final String CHINESE_CHAT = "[\u4e00-\u9fa5]";
    private static final String EMOJI_CHAT = EmojiRegexUtil.getFullEmojiRegex();

    public static void setEditTextInhibitInput(EditText editText, int textLength) {
        editText.setFilters(new InputFilter[]{setEditTextInhibitInputSpace(),
                getTextLengthLimitInputFilter(textLength)});
    }

    public static void setEditTextMaxLength(EditText editText, int textLength) {
        editText.setFilters(new InputFilter[]{getTextLengthLimitInputFilter(textLength)});
    }

    /**
     * 禁止EditText输入空格
     */
    private static InputFilter setEditTextInhibitInputSpace() {
        return new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.equals(" ") || source.equals("\n"))
                    return "";
                else
                    return null;

            }
        };
    }

    /**
     * 禁止EditText输入特殊字符
     */
    private static InputFilter setEditTextInhibitInputSpeChat() {

        return new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                Pattern pattern = Pattern.compile(SPE_CHAT);
                Matcher matcher = pattern.matcher(source.toString());
                if (matcher.find()) return "";
                else return null;
            }
        };
    }


    private static InputFilter getTextLengthLimitInputFilter(final int textLength) {
        return new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                CharSequence input = source.subSequence(start, end);
                if (getStringLength(dest, dstart, dend, input) <= textLength) {
                    return input;
                }
                for (int i = end; i > start; i--) {
                    if (source.length() >= 2) {
                        CharSequence lastChar = source.subSequence(end - 2, end);
                        if (isEmoji(lastChar)) {
                            i -= 2;
                        }
                    }

                    input = source.subSequence(start, i);
                    if (getStringLength(dest, dstart, dend, input) <= textLength) {
                        return input;
                    }
                }
                return "";
            }
        };
    }

    private static int getEmojiCount(CharSequence source) {
        Pattern emoji = Pattern.compile(EMOJI_CHAT, Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
        Matcher emojiMatcher = emoji.matcher(source);

        int count = 0;
        while (emojiMatcher.find()) {
            count++;
        }
        return count;
    }

    private static boolean isEmoji(CharSequence source) {
        Pattern emoji = Pattern.compile(EMOJI_CHAT, Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
        Matcher emojiMatcher = emoji.matcher(source);
        return emojiMatcher.find();
    }

    private static int getChineseCount(CharSequence source) {
        Pattern chinese = Pattern.compile(CHINESE_CHAT, Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
        Matcher chineseMatcher = chinese.matcher(source);

        int count = 0;
        while (chineseMatcher.find()) {
            count++;
        }
        return count;
    }

    private static int getStringLength(Spanned dest, int dstart, int dend, CharSequence source) {
        SpannableStringBuilder builder = new SpannableStringBuilder(dest)
                .replace(dstart, dend, source);
        String s = builder.toString();

        int emojiCount = getEmojiCount(s);
        int chineseCount = getChineseCount(s);
        int otherCount = s.length() - emojiCount * 2 - chineseCount;
        return 4 * emojiCount + 2 * chineseCount + otherCount;
    }

    public static int getStringLength(String string) {
        int emojiCount = getEmojiCount(string);
        int chineseCount = getChineseCount(string);
        int otherCount = string.length() - emojiCount * 2 - chineseCount;
        return 4 * emojiCount + 2 * chineseCount + otherCount;
    }

    /**
     * 限制EditText最多输入字节数
     */
    private static InputFilter setEditTextInhibitInputSpeLength() {

        return new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                int len = 0;
                boolean more = false;
                do {
                    SpannableStringBuilder builder = new SpannableStringBuilder(dest).replace(dstart, dend, source.subSequence(start, end));
                    //此时一个汉字等于两个字节。如果想改成一个汉字和英文一样减1，那么可以改成len = builder.toString().length();
                    try {
                        len = builder.toString().getBytes("GBK").length;
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    more = len > 18;
                    //以下，如果字数大于限制数，那么不显示输入
                    if (more) {
                        end--;
                        source = source.subSequence(start, end);
                    }
                } while (more);
                return source;
            }
        };
    }

}
