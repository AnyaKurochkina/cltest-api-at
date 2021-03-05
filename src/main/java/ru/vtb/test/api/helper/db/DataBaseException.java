package ru.vtb.test.api.helper.db;

import java.sql.SQLException;


public class DataBaseException extends RuntimeException{

    public DataBaseException(SQLException e) {
        super(e.getMessage(), e);
    }

    DataBaseException(String message) {
        super(message);
    }
}
