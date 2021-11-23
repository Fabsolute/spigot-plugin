package org.gronia.plugin.sack;

import org.bukkit.configuration.MemoryConfiguration;
import org.gronia.utils.GroniaMysqlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SackConfiguration extends GroniaMysqlConfiguration {
    @Override
    public String prepareUpsertQuery() {
        return "INSERT INTO " + name + " (`player`,`key`,`value`) VALUES(?,?,?) ON DUPLICATE KEY UPDATE `value` = ?";
    }

    @Override
    public void serialize(PreparedStatement st, Map.Entry<String, Object> kv) throws SQLException {
        var configuration = (SackMemoryConfiguration) kv.getValue();

        var player = kv.getKey();
        for (var key : configuration.getKeys(false)) {
            if (!configuration.isDirty(key)) {
                continue;
            }

            var value = configuration.getInt(key);
            st.setString(1, player);
            st.setString(2, key);
            st.setInt(3, value);
            st.setInt(4, value);
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
                "  `value` int(7) NOT NULL DEFAULT 0,\n" +
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

            configuration.set(rs.getString("key"), rs.getInt("value"));
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
                var cf = (SackMemoryConfiguration) kv.getValue();
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

    public SackMemoryConfiguration createConfiguration(String name) {
        var config = new SackMemoryConfiguration();
        this.set(name, config);
        return config;
    }

    public class SackMemoryConfiguration extends MemoryConfiguration {
        private final Set<String> dirtyList = new HashSet<>();
        public final Set<String> deletedList = new HashSet<>();

        @Override
        public void set(@NotNull String path, Object value) {
            super.set(path, value);
            SackConfiguration.this.setDirty();
            dirtyList.add(path);
            if (value == null) {
                deletedList.add(path);
            }
        }

        boolean isDirty(String path) {
            return this.dirtyList.contains(path);
        }

        void onSaveCompleted() {
            this.dirtyList.clear();
            this.deletedList.clear();
        }
    }
}
