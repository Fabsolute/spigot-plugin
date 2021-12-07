package org.gronia.utils.configuration;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class IntegerMysqlConfiguration extends MysqlConfiguration {
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