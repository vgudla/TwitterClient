package com.groupon.vgudla.tclient.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TwitterErrorMessageHelper {

    private static final String ERROR_STRING = "errors";
    private static final String MESSAGE_STRING = "message";

    public static String getErrorMessage(JSONObject errorObject) {
        StringBuffer errors = new StringBuffer();
        try {
            JSONArray errorArray = errorObject.getJSONArray(ERROR_STRING);
            for (int i = 0; i < errorArray.length(); i++) {
                JSONObject jsonObject = errorArray.getJSONObject(i);
                errors.append(jsonObject.getString(MESSAGE_STRING));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return errors.toString();
    }
}
