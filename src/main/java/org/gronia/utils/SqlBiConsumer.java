package org.gronia.utils;

import java.sql.SQLException;

public interface SqlBiConsumer<T, U> {
    void accept(T t, U u) throws SQLException;
}
