package org.gronia.utils;

import com.google.gson.Gson;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.sql.*;
import java.util.*;
import java.util.function.Supplier;
import java.util.logging.Level;

public abstract class GroniaMysqlConfiguration extends MemoryConfiguration {
    private boolean isDirty = false;
    protected String name;
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

    public static <T extends GroniaMysqlConfiguration> GroniaMysqlConfiguration loadConfiguration(Class<T> type, String name) {
        Validate.notNull(name, "Name cannot be null");
        GroniaMysqlConfiguration config = null;
        try {
            config = type.getDeclaredConstructor().newInstance();
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
        var rs = stmt.executeQuery("SELECT `key`, `value` FROM " + this.name);
        this.loadFromResultSet(rs);
    }

    public void save() throws SQLException {
        if (!this.isDirty) {
            return;
        }

        String upsert = "INSERT INTO " + name + " (`key`,`value`) VALUES(?,?) ON DUPLICATE KEY UPDATE `value` = ?";
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

    public static class JSON extends GroniaMysqlConfiguration {
        private final Gson gson;

        public JSON() {
            this.gson = new Gson();
        }

        @Override
        public void serialize(PreparedStatement st, Map.Entry<String, Object> kv) throws SQLException {
            var key = kv.getKey();
            var value = gson.toJson(Map.of("value", serializeComplex(kv.getValue())));
            st.setString(1, key);
            st.setString(2, value);
            st.setString(3, value);
            st.addBatch();
            st.clearParameters();
        }

        @Override
        protected void createTable() throws SQLException {
            var stmt = createStatement();
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS `" + this.name + "` (\n" +
                    "  `key` varchar(256) NOT NULL,\n" +
                    "  `value` mediumtext NOT NULL,\n" +
                    "  PRIMARY KEY (`key`)\n" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=latin1;");
        }

        @Override
        protected void loadFromResultSet(ResultSet rs) throws SQLException {
            Map<String, Object> input = new HashMap<>();
            while (rs.next()) {
                var value = gson.fromJson(rs.getString("value"), Map.class).get("value");
                input.put(rs.getString("key"), value);
            }

            this.convertMapsToSections(input, this);
        }

        protected void convertMapsToSections(Map<?, ?> input, ConfigurationSection section) {
            for (var entry : input.entrySet()) {
                String key = entry.getKey().toString();
                Object value = entry.getValue();
                if (value instanceof Map) {
                    if (((Map<?, ?>) value).containsKey(ConfigurationSerialization.SERIALIZED_TYPE_KEY)) {
                        section.set(key, ConfigurationSerialization.deserializeObject((Map<String, ?>) value));
                        continue;
                    }
                    this.convertMapsToSections((Map<?, ?>) value, section.createSection(key));
                } else {
                    section.set(key, value);
                }
            }
        }

        private static Object serializeComplex(Object value) {
            if (value instanceof Object[]) {
                value = Arrays.asList((Object[]) value);
            }

            if (value instanceof ConfigurationSection) {
                return buildMap(((ConfigurationSection) value).getValues(false));
            } else if (value instanceof Map mapValue) {
                return buildMap(mapValue);
            } else if (value instanceof List listValue) {
                return buildList(listValue);
            } else if (value instanceof ConfigurationSerializable serializable) {
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
                    result.put(entry.getKey().toString(), serializeComplex(entry.getValue()));
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
                    result.add(serializeComplex(o));
                }
            } catch (Exception e) {
                Bukkit.getLogger().log(Level.WARNING, "Error while building configuration list.", e);
            }
            return result;
        }
    }

    public static class Integer extends GroniaMysqlConfiguration {
        private final Set<String> dirtyList = new HashSet<>();

        @Override
        public void serialize(PreparedStatement st, Map.Entry<String, Object> kv) throws SQLException {
            if (!this.dirtyList.contains(kv.getKey())) {
                return;
            }

            var key = kv.getKey();
            var value = kv.getValue();
            st.setString(1, key);
            st.setInt(2, (int) value);
            st.setInt(3, (int) value);
            st.addBatch();
            st.clearParameters();
        }

        @Override
        protected void createTable() throws SQLException {
            var stmt = createStatement();
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS `" + this.name + "` (\n" +
                    "  `key` varchar(256) NOT NULL,\n" +
                    "  `value` int(7) NOT NULL DEFAULT 0,\n" +
                    "  PRIMARY KEY (`key`)\n" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=latin1;");
        }

        @Override
        protected void loadFromResultSet(ResultSet rs) throws SQLException {
            while (rs.next()) {
                this.set(rs.getString("key"), rs.getInt("value"));
            }
        }

        @Override
        public void set(String path, Object value) {
            super.set(path, value);
            this.setDirty();
            dirtyList.add(path);
        }

        @Override
        public void onSaveCompleted() {
            this.dirtyList.clear();
        }
    }
}
