package ru.vtb.test.api.helper.db;

import java.sql.ResultSet;
import java.sql.SQLException;


@FunctionalInterface
public interface ThrowingFunction<T> {

    T execute(ResultSet resultSet) throws SQLException;
}