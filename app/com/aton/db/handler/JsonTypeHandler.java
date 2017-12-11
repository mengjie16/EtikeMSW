package com.aton.db.handler;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.alibaba.fastjson.JSON;
import com.aton.util.JsonUtil;
import com.google.common.base.Objects;
import com.google.common.base.Strings;

/**
 * sql json string parse to javaType
 * 
 * @author Calm
 * @since v1.0
 * @created 2016年6月3日 下午8:17:15
 */
public class JsonTypeHandler<T> implements TypeHandler<T> {

    private final Class<T> entityClass;

    /**
     * sub class use this constructs
     * Constructs a <code>JsonTypeHandler</code>
     *
     * @since v1.0
     */
    public JsonTypeHandler() {
        Type genType = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        entityClass = (Class) params[0];
    }

    /**
     * use this constructs self
     * Constructs a <code>JsonTypeHandler</code>
     *
     * @since v1.0
     */
    public JsonTypeHandler(T en) {
        if (en == null)
            throw new IllegalArgumentException("Type argument cannot be null");
        entityClass = (Class<T>) en.getClass();
    }

    @Override
    public T getResult(ResultSet rs, String columnIndex) throws SQLException {
        // TODO Auto-generated method stub
        String sqlStr = null;
        try {
            String sqlRStr = rs.getString(columnIndex);
            if (Strings.isNullOrEmpty(sqlRStr)) {
                return null;
            }
            // sqlStr = new String(sqlRStr.getBytes("ISO-8859-1"), "utf-8");
            sqlStr = new String(sqlRStr.getBytes("utf-8"), "utf-8");
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        if (Strings.isNullOrEmpty(sqlStr) || Objects.equal("null", sqlStr) || Objects.equal("NULL", sqlStr)) {
            return null;
        }
        try {
            if (sqlStr.startsWith("[{")) { // json array
                JSONArray arrObj = new JSONArray(sqlStr);
                for (int i = 0; i < arrObj.length(); i++) {
                    JSONObject obj = new JSONObject();
                    obj = arrObj.getJSONObject(i);
                    T prv = JsonUtil.toBean(obj.toString(), entityClass);
                    return prv;
                }
            } else {
                JSONObject obj = new JSONObject(sqlStr);
                T prv = JsonUtil.toBean(obj.toString(), entityClass);
                return prv;
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public T getResult(ResultSet rs, int columnIndex) throws SQLException {
        // TODO Auto-generated method stub
        String sqlStr = null;
        try {
            String sqlRStr = rs.getString(columnIndex);
            if (Strings.isNullOrEmpty(sqlRStr)) {
                return null;
            }
            // sqlStr = new String(sqlRStr.getBytes("ISO-8859-1"), "utf-8");
            sqlStr = new String(sqlRStr.getBytes("utf-8"), "utf-8");
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        if (Strings.isNullOrEmpty(sqlStr) || Objects.equal("null", sqlStr) || Objects.equal("NULL", sqlStr)) {
            return null;
        }
        try {
            if (sqlStr.startsWith("[{")) { // json array
                JSONArray arrObj = new JSONArray(sqlStr);
                for (int i = 0; i < arrObj.length(); i++) {
                    JSONObject obj = new JSONObject();
                    obj = arrObj.getJSONObject(i);
                    T prv = JsonUtil.toBean(obj.toString(), entityClass);
                    return prv;
                }
            } else {
                JSONObject obj = new JSONObject(sqlStr);
                T prv = JsonUtil.toBean(obj.toString(), entityClass);
                return prv;
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public T getResult(CallableStatement cs, int columnIndex) throws SQLException {
        // TODO Auto-generated method stub
        String sqlStr = null;
        try {
            String sqlRStr = cs.getString(columnIndex);
            if (Strings.isNullOrEmpty(sqlRStr)) {
                return null;
            }
            // sqlStr = new String(sqlRStr.getBytes("ISO-8859-1"), "utf-8");
            sqlStr = new String(sqlRStr.getBytes("utf-8"), "utf-8");
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        if (Strings.isNullOrEmpty(sqlStr) || Objects.equal("null", sqlStr) || Objects.equal("NULL", sqlStr)) {
            return null;
        }
        try {
            if (sqlStr.startsWith("[{")) { // json array
                JSONArray arrObj = new JSONArray(sqlStr);
                for (int i = 0; i < arrObj.length(); i++) {
                    JSONObject obj = new JSONObject();
                    obj = arrObj.getJSONObject(i);
                    T prv = JsonUtil.toBean(obj.toString(), entityClass);
                    return prv;
                }
            } else {
                JSONObject obj = new JSONObject(sqlStr);
                T prv = JsonUtil.toBean(obj.toString(), entityClass);
                return prv;
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void setParameter(PreparedStatement ps, int columnIndex, T arg2, JdbcType arg3) throws SQLException {
        // TODO Auto-generated method stub
        ps.setString(columnIndex, arg2 == null ? null : JSON.toJSONString(arg2));
    }
}
