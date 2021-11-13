package org.gronia.utils;

import com.google.gson.Gson;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;

public class GroniaMysqlConfiguration extends MemoryConfiguration {
    private boolean isDirty = false;
    private String name;
    private static Connection connection;
    private static Supplier<Connection> creator;

    public static void initialize(Supplier<Connection> creator) {
        GroniaMysqlConfiguration.creator = creator;
    }

    private static Connection getConnection() {
        while (GroniaMysqlConfiguration.connection == null) {
            GroniaMysqlConfiguration.connection = creator.get();
            if (GroniaMysqlConfiguration.connection == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        return GroniaMysqlConfiguration.connection;
    }

    private static PreparedStatement prepareStatement(String sql) throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = null;
        }

        return getConnection().prepareStatement(sql);
    }

    private static Statement createStatement() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = null;
        }

        return getConnection().createStatement();
    }

    public static GroniaMysqlConfiguration loadConfiguration(String name) {
        Validate.notNull(name, "Name cannot be null");
        GroniaMysqlConfiguration config = new GroniaMysqlConfiguration();
        config.name = name;

        try {
            config.load();
        } catch (SQLException ignored) {
            Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + name);
        }

        return config;
    }

    public void load() throws SQLException {
        this.createTable();

        var stmt = createStatement();
        var rs = stmt.executeQuery("SELECT `key`, `value` FROM " + this.name);
        Map<String, Object> input = new HashMap<>();
        Gson gson = new Gson();
        while (rs.next()) {
            var value = gson.fromJson(rs.getString("value"), Map.class);
            input.put(rs.getString("key"), value.get("value"));
        }

        this.convertMapsToSections(input, this);
    }

    public void save() throws SQLException {
        if (!this.isDirty) {
            return;
        }

        Gson gson = new Gson();

        String upsert = "INSERT INTO " + name + " (`key`,`value`) VALUES(?,?) ON DUPLICATE KEY UPDATE `value` = ?";
        var st = prepareStatement(upsert);

        for (var kv : this.getValues(false).entrySet()) {
            var key = kv.getKey();
            var value = gson.toJson(Map.of("value", serialize(kv.getValue())));
            st.setString(1, key);
            st.setString(2, value);
            st.setString(3, value);
            st.addBatch();
            st.clearParameters();
        }

        st.executeBatch();

        this.isDirty = false;
    }

    public void setDirty() {
        this.isDirty = true;
    }

    protected void convertMapsToSections(Map<?, ?> input, ConfigurationSection section) {
        for (var entry : input.entrySet()) {
            String key = entry.getKey().toString();
            Object value = entry.getValue();
            if (value instanceof Map) {
                this.convertMapsToSections((Map<?, ?>) value, section.createSection(key));
            } else {
                section.set(key, value);
            }
        }
    }

    protected void createTable() throws SQLException {
        var stmt = createStatement();
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS `" + name + "` (\n" +
                "  `key` varchar(256) NOT NULL,\n" +
                "  `value` mediumtext NOT NULL,\n" +
                "  PRIMARY KEY (`key`)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=latin1;");
    }

    private static Object serialize(Object value) {
        if (value instanceof Object[]) {
            value = Arrays.asList((Object[]) value);
        }

        if (value instanceof ConfigurationSection) {
            return buildMap(((ConfigurationSection) value).getValues(false));
        } else if (value instanceof Map) {
            return buildMap((Map) value);
        } else if (value instanceof List) {
            return buildList((List) value);
        } else if (value instanceof ConfigurationSerializable) {
            ConfigurationSerializable serializable = (ConfigurationSerializable) value;
            Map<String, Object> values = new LinkedHashMap<>();
            values.put(ConfigurationSerialization.SERIALIZED_TYPE_KEY, ConfigurationSerialization.getAlias(serializable.getClass()));
            values.putAll(serializable.serialize());
            return buildMap(values);
        } else {
            return value;
        }
    }

    private static Map<String, Object> buildMap(final Map<?, ?> map) {
        final Map<String, Object> result = new LinkedHashMap<>(map.size());
        try {
            for (final Map.Entry<?, ?> entry : map.entrySet()) {
                result.put(entry.getKey().toString(), serialize(entry.getValue()));
            }
        } catch (final Exception e) {
            Bukkit.getLogger().log(Level.WARNING, "Error while building configuration map.", e);
        }
        return result;
    }

    private static List<Object> buildList(final Collection<?> collection) {
        final List<Object> result = new ArrayList<Object>(collection.size());
        try {
            for (Object o : collection) {
                result.add(serialize(o));
            }
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.WARNING, "Error while building configuration list.", e);
        }
        return result;
    }

    @Override
    public ConfigurationSection getConfigurationSection(String path) {
        return super.getConfigurationSection(path);
    }
}
