package com.syyk.electronicclass2.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


/**
 * Created by fei on 2018/1/2.
 */

public class JsonUtils {

    public static JSONArray getJsonArr(String json,String jsonArrKey){
        JSONObject jsonObject = JSON.parseObject(json);
        JSONArray jsonArray = jsonObject.getJSONArray(jsonArrKey);
        return jsonArray;
    }

    public static String getJsonObject(String json,String jsonObjKey){
        JSONObject jsonObject = JSON.parseObject(json);
        JSONObject resObject = jsonObject.getJSONObject(jsonObjKey);
        return resObject.toString();
    }

    public static String getJsonKey(String json,String jsonKey){
        JSONObject jsonObject = JSON.parseObject(json);
        return jsonObject.getString(jsonKey);
    }



}
