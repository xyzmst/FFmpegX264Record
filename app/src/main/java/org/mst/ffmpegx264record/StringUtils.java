//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.mst.ffmpegx264record;

import android.text.TextPaint;
import android.text.TextUtils;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;

public class StringUtils {
    public static final String EMPTY = "";
    private static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";
    private static final String DEFAULT_DATETIME_PATTERN = "yyyy-MM-dd hh:mm:ss";
    private static final String DEFAULT_FILE_PATTERN = "yyyy-MM-dd-HH-mm-ss";
    private static final double KB = 1024.0D;
    private static final double MB = 1048576.0D;
    private static final double GB = 1.073741824E9D;
    public static final SimpleDateFormat DATE_FORMAT_PART = new SimpleDateFormat("HH:mm");

    public StringUtils() {
    }

    public static String currentTimeString() {
        return DATE_FORMAT_PART.format(Calendar.getInstance().getTime());
    }

    public static char chatAt(String pinyin, int index) {
        return pinyin != null && pinyin.length() > 0?pinyin.charAt(index):' ';
    }

    public static float GetTextWidth(String Sentence, float Size) {
        if(isEmpty(Sentence)) {
            return 0.0F;
        } else {
            TextPaint FontPaint = new TextPaint();
            FontPaint.setTextSize(Size);
            return FontPaint.measureText(Sentence.trim()) + (float)((int)((double)Size * 0.1D));
        }
    }

    public static String formatDate(Date date, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    public static String formatDate(long date, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(new Date(date));
    }

    public static String formatDate(Date date) {
        return formatDate(date, "yyyy-MM-dd");
    }

    public static String formatDate(long date) {
        return formatDate(new Date(date), "yyyy-MM-dd");
    }

    public static String getDate() {
        return formatDate(new Date(), "yyyy-MM-dd");
    }

    public static String createFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        return format.format(date);
    }

    public static String getDateTime() {
        return formatDate(new Date(), "yyyy-MM-dd hh:mm:ss");
    }

    public static String formatDateTime(Date date) {
        return formatDate(date, "yyyy-MM-dd hh:mm:ss");
    }

    public static String formatDateTime(long date) {
        return formatDate(new Date(date), "yyyy-MM-dd hh:mm:ss");
    }

    public static String formatGMTDate(String gmt) {
        TimeZone timeZoneLondon = TimeZone.getTimeZone(gmt);
        return formatDate(Calendar.getInstance(timeZoneLondon).getTimeInMillis());
    }

    public static String join(ArrayList<String> array, String separator) {
        StringBuffer result = new StringBuffer();
        if(array != null && array.size() > 0) {
            Iterator var4 = array.iterator();

            while(var4.hasNext()) {
                String str = (String)var4.next();
                result.append(str);
                result.append(separator);
            }

            result.delete(result.length() - 1, result.length());
        }

        return result.toString();
    }

    public static String join(Iterator<String> iter, String separator) {
        StringBuffer result = new StringBuffer();
        if(iter != null) {
            while(true) {
                if(!iter.hasNext()) {
                    if(result.length() > 0) {
                        result.delete(result.length() - 1, result.length());
                    }
                    break;
                }

                String key = (String)iter.next();
                result.append(key);
                result.append(separator);
            }
        }

        return result.toString();
    }

    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0 || str.equalsIgnoreCase("null");
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static String trim(String str) {
        return str == null?"":str.trim();
    }

    public static String generateTime(long time) {
        int totalSeconds = (int)(time / 1000L);
        int seconds = totalSeconds % 60;
        int minutes = totalSeconds / 60 % 60;
        int hours = totalSeconds / 3600;
        return hours > 0?String.format("%02d:%02d:%02d", new Object[]{Integer.valueOf(hours), Integer.valueOf(minutes), Integer.valueOf(seconds)}):String.format("%02d:%02d", new Object[]{Integer.valueOf(minutes), Integer.valueOf(seconds)});
    }

    public static boolean isBlank(String s) {
        return TextUtils.isEmpty(s);
    }

    public static String gennerTime(int totalSeconds) {
        int seconds = totalSeconds % 60;
        int minutes = totalSeconds / 60 % 60;
        return String.format("%02d:%02d", new Object[]{Integer.valueOf(minutes), Integer.valueOf(seconds)});
    }

    public static String generateFileSize(long size) {
        String fileSize;
        if((double)size < 1024.0D) {
            fileSize = size + "B";
        } else if((double)size < 1048576.0D) {
            fileSize = String.format("%.1f", new Object[]{Double.valueOf((double)size / 1024.0D)}) + "KB";
        } else if((double)size < 1.073741824E9D) {
            fileSize = String.format("%.1f", new Object[]{Double.valueOf((double)size / 1048576.0D)}) + "MB";
        } else {
            fileSize = String.format("%.1f", new Object[]{Double.valueOf((double)size / 1.073741824E9D)}) + "GB";
        }

        return fileSize;
    }

    public static String findString(String search, String start, String end) {
        int start_len = start.length();
        int start_pos = isEmpty(start)?0:search.indexOf(start);
        if(start_pos > -1) {
            int end_pos = isEmpty(end)?-1:search.indexOf(end, start_pos + start_len);
            if(end_pos > -1) {
                return search.substring(start_pos + start.length(), end_pos);
            }
        }

        return "";
    }

    public static String substring(String search, String start, String end, String defaultValue) {
        int start_len = start.length();
        int start_pos = isEmpty(start)?0:search.indexOf(start);
        if(start_pos > -1) {
            int end_pos = isEmpty(end)?-1:search.indexOf(end, start_pos + start_len);
            return end_pos > -1?search.substring(start_pos + start.length(), end_pos):search.substring(start_pos + start.length());
        } else {
            return defaultValue;
        }
    }

    public static String substring(String search, String start, String end) {
        return substring(search, start, end, "");
    }

    public static String concat(String... strs) {
        StringBuffer result = new StringBuffer();
        if(strs != null) {
            String[] var5 = strs;
            int var4 = strs.length;

            for(int var3 = 0; var3 < var4; ++var3) {
                String str = var5[var3];
                if(str != null) {
                    result.append(str);
                }
            }
        }

        return result.toString();
    }

    public static String makeSafe(String s) {
        return s == null?"":s;
    }
}
