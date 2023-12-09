package com.angelbroking.smartapi.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class Utils {
    private static final ObjectMapper mapper = new ObjectMapper();

    private Utils() {
    }

    public static boolean isEmpty(final Integer nm) {
        return nm == null || nm.equals(0);
    }

    public static boolean areCharArraysEqual(char[] a, char[] b) {
        if (a == null && b == null) {
            return true;
        }

        if (a != null && b != null) {
            if (a.length == b.length) {
                for (int i = 0; i < a.length; i++) {
                    if (a[i] != b[i]) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public static boolean areByteArraysEqual(byte[] a, byte[] b) {
        if (a == null && b == null) {
            return true;
        }

        if (a != null && b != null) {
            if (a.length == b.length) {
                for (int i = 0; i < a.length; i++) {
                    if (a[i] != b[i]) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public static <T> boolean validateInputNullCheck(T input) {
        return input == null;
    }

    public static <T> boolean validateInputNotNullCheck(T input) {
        return input != null;
    }

    public static <T> T convertJsonObject(JSONObject jsonObject, Class<T> targetClass) throws Exception {
        return mapper.readValue(jsonObject.toString(), targetClass);
    }

    public static <T> List<T> convertJsonArrayToList(JSONArray jsonArray, Class<T> targetClass) throws Exception {
        return mapper.readValue(jsonArray.toString(), mapper.getTypeFactory().constructCollectionType(List.class, targetClass));
    }

}
