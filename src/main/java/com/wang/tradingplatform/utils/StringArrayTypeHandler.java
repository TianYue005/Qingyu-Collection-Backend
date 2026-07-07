package com.wang.tradingplatform.utils;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StringArrayTypeHandler extends BaseTypeHandler<String[]> {

    // Java数组 → 数据库逗号字符串
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String[] strings, JdbcType jdbcType) throws SQLException {
        if (strings == null || strings.length == 0) {
            ps.setString(i, "");
        } else {
            ps.setString(i, String.join(",", strings));
        }
    }

    // 数据库字符串 → Java数组
    @Override
    public String[] getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String str = rs.getString(columnName);
        return convertStrToArray(str);
    }

    @Override
    public String[] getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String str = rs.getString(columnIndex);
        return convertStrToArray(str);
    }

    @Override
    public String[] getNullableResult(java.sql.CallableStatement cs, int columnIndex) throws SQLException {
        String str = cs.getString(columnIndex);
        return convertStrToArray(str);
    }

    private String[] convertStrToArray(String str) {
        if (str == null || str.trim().isEmpty()) {
            return new String[0];
        }
        return str.split(",");
    }
}