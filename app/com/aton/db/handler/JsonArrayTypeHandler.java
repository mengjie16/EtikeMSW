package com.aton.db.handler;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
public class JsonArrayTypeHandler<T> implements TypeHandler<List<T>> {
    private final Class<T> entityClass;

    /**
     * sub class use this constructs
     * Constructs a <code>JsonTypeHandler</code>
     *
     * @since v1.0
     */
    public JsonArrayTypeHandler() {
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
    public JsonArrayTypeHandler(T en) {
        if (en == null)
            throw new IllegalArgumentException("Type argument cannot be null");
        entityClass = (Class<T>) en.getClass();
    }

    @Override
    public List<T> getResult(ResultSet rs, String columnIndex) throws SQLException {
        // TODO Auto-generated method stub
        List<T> result = new ArrayList<T>();
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
            return result;
        }
        try {
            if (sqlStr.startsWith("[{")) { // json array
                JSONArray arrObj = new JSONArray(sqlStr);
                for (int i = 0; i < arrObj.length(); i++) {
                    JSONObject obj = new JSONObject();
                    obj = arrObj.getJSONObject(i);
                    T prv = JsonUtil.toBean(obj.toString(), entityClass);
                    result.add(prv);
                }
            } else {
                JSONObject obj = new JSONObject(sqlStr);
                T prv = JsonUtil.toBean(obj.toString(), entityClass);
                result.add(prv);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public List<T> getResult(ResultSet rs, int columnIndex) throws SQLException {
        List<T> result = new ArrayList<T>();
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
            return result;
        }

        if (Strings.isNullOrEmpty(sqlStr) || Objects.equal("null", sqlStr) || Objects.equal("NULL", sqlStr)) {
            return result;
        }
        try {
            if (sqlStr.startsWith("[{")) { // json array
                JSONArray arrObj = new JSONArray(sqlStr);
                for (int i = 0; i < arrObj.length(); i++) {
                    JSONObject obj = new JSONObject();
                    obj = arrObj.getJSONObject(i);
                    T prv = JsonUtil.toBean(obj.toString(), entityClass);
                    result.add(prv);
                }
            } else {
                JSONObject obj = new JSONObject(sqlStr);
                T prv = JsonUtil.toBean(obj.toString(), entityClass);
                result.add(prv);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public List<T> getResult(CallableStatement cs, int columnIndex)
            throws SQLException {
        List<T> result = new ArrayList<T>();
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
            return result;
        }
        if (Strings.isNullOrEmpty(sqlStr) || Objects.equal("null", sqlStr) || Objects.equal("NULL", sqlStr)) {
            return result;
        }
        try {
            if (sqlStr.startsWith("[{")) { // json array
                JSONArray arrObj = new JSONArray(sqlStr);
                for (int i = 0; i < arrObj.length(); i++) {
                    JSONObject obj = new JSONObject();
                    obj = arrObj.getJSONObject(i);
                    T prv = JsonUtil.toBean(obj.toString(), entityClass);
                    result.add(prv);
                }
            } else {
                JSONObject obj = new JSONObject(sqlStr);
                T prv = JsonUtil.toBean(obj.toString(), entityClass);
                result.add(prv);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void setParameter(PreparedStatement ps, int columnIndex, List<T> input,
            JdbcType jdbcType) throws SQLException {
        ps.setString(columnIndex, input == null || input.size() == 0 ? null : JSON.toJSONString(input));
    }
}
