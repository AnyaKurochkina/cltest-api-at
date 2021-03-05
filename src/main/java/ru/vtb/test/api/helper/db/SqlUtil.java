package ru.vtb.test.api.helper.db;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;


public class SqlUtil {

    private final String url;
    private final String login;
    private final String password;

    /**
     * Examples
     * <p>
     * jdbc:oracle:thin:@host.tsftd.ru:1521:XE
     * jdbc:postgresql://host:5432/name
     * jdbc:sqlserver://host:1433;DatabaseName=name
     */
    public SqlUtil(String url, String login, String password) {
        this.url = url;
        this.login = login;
        this.password = password;
    }

    public int resultSetCount(String query) throws SQLException {
        return wrap(query, (rs) -> {
            rs.last();
            return rs.getRow();
        });
    }

    public String getFieldData(String query, String tableName) throws SQLException {
        return wrap(query, (rs) -> {
            rs.last();
            int count = rs.getRow();
            if (count == 1) {
                rs.first();
                return rs.getString(tableName);
            }
            throw new DataBaseException("Количество строк не равно 1!!!");
        });
    }

    public Map<String, String> getAllData(String query) throws SQLException {
        return wrap(query, (rs) -> {
            rs.last();
            int countRaw = rs.getRow();
            int columnCount = rs.getMetaData().getColumnCount();//количество столбцов
            rs.beforeFirst();
            if (countRaw != 1) {
                throw new DataBaseException("Количество строк не равно 1!!!");
            }
            Map<String, String> result = new HashMap<>();
            while (rs.next()) {
                //тянем все названия столбцов и их содержимое
                for (int x = 1; x <= columnCount; x++) {
                    result.put(rs.getMetaData().getColumnName(x), rs.getString(x));
                }
            }
            return result;
        });
    }

    public String select(String query) throws SQLException {
        return wrap(query, (rs) -> {
            int columnCount = rs.getMetaData().getColumnCount();
            rs.last();
            int count = rs.getRow();
            if (columnCount == 1 && count == 1) {
                rs.first();
                return rs.getString(1);
            }
            throw new DataBaseException("Запрос составлен не правильно!!!количество строк и/или столбцов не равны 1.");
        });
    }

    private <T> T wrap(String query, ThrowingFunction<T> function) throws SQLException {
        Connection connection = DriverManager.getConnection(url, login, password);
        Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet resultSet = statement.executeQuery(query);
        return function.execute(resultSet);
    }

}
