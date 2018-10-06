package com.gcr.acm.common.utils;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Utilities class.
 *
 * @author Razvan Dani
 */
public class Utilities {

    public static boolean nullSafeEquals(Object object1, Object object2) {
        return (object1 != null && object1.equals(object2)) || (object1 == null && object2 == null);
    }

    public static boolean isEmptyOrNull(String str) {
        return str == null || str.equals("");
    }

    public static String getIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-FORWARDED-FOR");

        if (ipAddress != null) {
            String[] ipAddressArray = ipAddress.split(",");
            ipAddress = ipAddressArray[0];
        }

        if (ipAddress == null) {
            ipAddress = request.getHeader("ClientIpAddress");
        }

        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }

        return ipAddress;
    }

    public static String nullSafeToString(Object o) {
        return o == null ? "" : o.toString();
    }

    public static String getListAsString(Collection collection, String separator) {
        return getListAsString(collection, separator,null);
    }

    public static String getListAsString(Collection collection, String separator, String delimiter) {
        StringBuilder builder = new StringBuilder();

        for (Object obj : collection) {
            if (builder.length() > 0) {
                builder.append(separator);
            }

            if (delimiter != null) {
                builder.append(delimiter);
            }

            builder.append(obj);

            if (delimiter != null) {
                builder.append(delimiter);
            }
        }

        return builder.toString();
    }

    public static <T> List<List<T>> splitList(List<T> list, Integer maxElementsInSublist) {
        List<List<T>> sublistList = new ArrayList<>();
        List<T> currentSublist = new ArrayList<>();
        sublistList.add(currentSublist);

        for (T object : list) {
            if (currentSublist.size() == maxElementsInSublist) {
                currentSublist = new ArrayList<>();
                sublistList.add(currentSublist);
            }

            currentSublist.add(object);
        }

        return sublistList;
    }

    public static <T> List<List<T>> splitListWithMaxTotalSublists(List<T> list, Integer maxTotalSublists) {
        Integer maxSublistSize = new BigDecimal(list.size()).divide(new BigDecimal(maxTotalSublists), 0, BigDecimal.ROUND_CEILING).intValue();

        return splitList(list, maxSublistSize);
    }

    public static <T> T initializeIfNull(T object, T initialValue) {
        return object == null ? initialValue : object;
    }

    public static boolean isBigInteger(String str) {
        try {
            new BigInteger(str);
        } catch (Exception e) {
            return false;
        }

        return true;
    }
}
