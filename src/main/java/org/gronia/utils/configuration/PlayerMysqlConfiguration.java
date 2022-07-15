package org.gronia.utils.configuration;

import org.bukkit.configuration.MemorySection;
import org.gronia.utils.SqlBiConsumer;
import org.gronia.utils.SqlFunction;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.function.BiFunction;

public class PlayerMysqlConfiguration extends MysqlConfiguration {
    public enum Type {
        INTEGER("int(7)",
                " DEFAULT 0",
                r -> r.getInt("value"),
                MemorySection::getInt,
                (s, o) -> {
                    s.setInt(3, (int) o);
                    s.setInt(4, (int) o);
                }),
        BOOLEAN("boolean",
                " DEFAULT false",
                r -> r.getBoolean("value"),
                MemorySection::getBoolean,
                (s, o) -> {
                    s.setBoolean(3, (boolean) o);
                    s.setBoolean(4, (boolean) o);
                }),
        STRING("varchar(256)",
                "",
                r -> r.getString("value"),
                MemorySection::getString,
                (s, o) -> {
                    s.setString(3, (String) o);
                    s.setString(4, (String) o);
                });

        private final String databaseType;
        private final String databaseDefault;
        private final SqlFunction<ResultSet, Object> readResult;
        private final BiFunction<PlayerMemoryConfiguration, String, Object> readMemory;
        private final SqlBiConsumer<PreparedStatement, Object> setStatement;

        Type(String databaseType,
             String databaseDefault,
             SqlFunction<ResultSet, Object> readResult,
             BiFunction<PlayerMemoryConfiguration, String, Object> readMemory,
             SqlBiConsumer<PreparedStatement, Object> setStatement) {
            this.databaseType = databaseType;
            this.databaseDefault = databaseDefault;
            this.readResult = readResult;
            this.readMemory = readMemory;
            this.setStatement = setStatement;
        }
    }

    private final Type type;

    public PlayerMysqlConfiguration() {
        this(Type.INTEGER);
    }

    public PlayerMysqlConfiguration(Type type) {
        this.type = type;
    }

    public Type getType() {
        return this.type;
    }

    @Override
    public String prepareUpsertQuery() {
        return "INSERT INTO " + name + " (`player`,`key`,`value`) VALUES(?,?,?) ON DUPLICATE KEY UPDATE `value` = ?";
    }

    @Override
    public void serialize(PreparedStatement st, Map.Entry<String, Object> kv) throws SQLException {
        var configuration = (PlayerMemoryConfiguration) kv.getValue();

        var player = kv.getKey();
        for (var key : configuration.getKeys(false)) {
            if (!configuration.isDirty(key)) {
                continue;
            }

            st.setString(1, player);
            st.setString(2, key);
            var val = type.readMemory.apply(configuration, key);
            type.setStatement.accept(st, val);

            st.addBatch();
            st.clearParameters();
        }
    }

    @Override
    protected void createTable() throws SQLException {
        var stmt = createStatement();
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS `" + this.name + "` (\n" +
                "  `player` varchar(256) NOT NULL,\n" +
                "  `key` varchar(256) NOT NULL,\n" +
                "  `value` " + type.databaseType + " NOT NULL" + type.databaseDefault + ",\n" +
                "  PRIMARY KEY (`player`, `key`)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=latin1;");
    }

    @Override
    protected void loadFromResultSet(ResultSet rs) throws SQLException {
        while (rs.next()) {
            var player = rs.getString("player");
            var configuration = this.getConfigurationSection(player);
            if (configuration == null) {
                configuration = this.createConfiguration(player);
            }

            configuration.set(rs.getString("key"), type.readResult.apply(rs));
        }
    }

    @Override
    public void set(@NotNull String path, Object value) {
        super.set(path, value);
        this.setDirty();
    }

    @Override
    public void onSaveCompleted() {
        try {
            var st = prepareStatement("DELETE FROM " + name + " WHERE `player` = ? and `key` = ?");
            for (var kv : this.getValues(false).entrySet()) {
                var player = kv.getKey();
                var cf = (PlayerMemoryConfiguration) kv.getValue();
                for (var deleted : cf.deletedList) {
                    st.setString(1, player);
                    st.setString(2, deleted);
                    st.addBatch();
                    st.clearParameters();
                }

                cf.onSaveCompleted();
            }

            st.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public PlayerMemoryConfiguration createConfiguration(String name) {
        var config = new PlayerMemoryConfiguration(this);
        this.set(name, config);
        return config;
    }
}
