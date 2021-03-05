package ru.vtb.test.api.helper.db;

import lombok.extern.slf4j.Slf4j;
import org.aeonbits.owner.ConfigFactory;
import ru.vtb.test.api.helper.ConfigVars;

import java.sql.*;

@Slf4j
public class DBHelper {
    private ConfigVars var = ConfigFactory.create(ConfigVars.class);
    private Connection connection;

    private void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                connection = null;
            }
        } catch (SQLException e) {
            log.error("CLOSE CONNECTION ERROR!", e);
        }
    }

    private void createConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            return;
        }
        connection = DriverManager.getConnection(var.dbUrl(), var.dbUser(), var.dbPassword());
    }

    public String executeSelectOneValue(String sql) throws SQLException {
        createConnection();
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        String data = "";
        if (rs.next()) {
            data = rs.getString(1);
        }
        return data;
    }


}