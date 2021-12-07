package org.gronia.utils.configuration;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

import java.sql.*;
import java.util.*;
import java.util.function.Supplier;
import java.util.logging.Level;

public abstract class MysqlConfiguration extends MemoryConfiguration {
    private boolean isDirty = false;
    protected String name;
    private static Connection connection;
    private static Supplier<Connection> creator;

    public static void initialize(Supplier<Connection> creator) {
        MysqlConfiguration.creator = creator;
    }

    private static Connection getConnection() {
        while (MysqlConfiguration.connection == null) {
            MysqlConfiguration.connection = creator.get();
            if (MysqlConfiguration.connection == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        return MysqlConfiguration.connection;
    }

    protected static PreparedStatement prepareStatement(String sql) throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = null;
        }

        return getConnection().prepareStatement(sql);
    }

    protected static Statement createStatement() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = null;
        }

        return getConnection().createStatement();
    }

    public static <T extends MysqlConfiguration> T loadConfiguration(Class<T> type, String name, Object... args) {
        Validate.notNull(name, "Name cannot be null");
        T config = null;
        try {
            config = type.getDeclaredConstructor(Arrays.stream(args).map(Object::getClass).toArray(Class<?>[]::new)).newInstance(args);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (config == null) {
            return null;
        }

        config.name = name;

        try {
            config.load();
        } catch (SQLException ignored) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not loaded " + name);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return loadConfiguration(type, name);
        }

        return config;
    }

    public void load() throws SQLException {
        this.createTable();

        var stmt = createStatement();
        var rs = stmt.executeQuery("SELECT * FROM " + this.name);
        this.loadFromResultSet(rs);
    }

    public String prepareUpsertQuery() {
        return "INSERT INTO " + name + " (`key`,`value`) VALUES(?,?) ON DUPLICATE KEY UPDATE `value` = ?";
    }

    public void save() throws SQLException {
        if (!this.isDirty) {
            return;
        }

        String upsert = prepareUpsertQuery();
        var st = prepareStatement(upsert);

        for (var kv : this.getValues(false).entrySet()) {
            this.serialize(st, kv);
        }

        st.executeBatch();

        this.isDirty = false;

        this.onSaveCompleted();
    }

    public void setDirty() {
        this.isDirty = true;
    }

    protected abstract void createTable() throws SQLException;

    protected abstract void loadFromResultSet(ResultSet rs) throws SQLException;

    public void onSaveCompleted() {
    }

    public abstract void serialize(PreparedStatement st, Map.Entry<String, Object> kv) throws SQLException;

    @Override
    public ConfigurationSection getConfigurationSection(String path) {
        return super.getConfigurationSection(path);
    }
}
