package org.gronia.utils.configuration;

import org.bukkit.Bukkit;
import org.gronia.plugin.warehouse.WareHousePlugin;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;

public class InventoryMysqlConfiguration extends MysqlConfiguration {
    @Override
    protected void createTable() throws SQLException {
        var stmt = createStatement();
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS `" + this.name + "` (\n" +
                "  `case` varchar(256) NOT NULL,\n" +
                "  `item` varchar(256) NOT NULL,\n" +
                "  `hash` varchar(256) NOT NULL,\n" +
                "  `value` mediumtext NOT NULL,\n" +
                "  `count` int(7) NOT NULL DEFAULT 0,\n" +
                "  PRIMARY KEY (`case`, `item`, `hash`)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=latin1;");
    }

    @Override
    public String prepareUpsertQuery() {
        return "INSERT INTO " + name + " (`case`,`item`,`hash`,`value`,`count`) VALUES(?,?,?,?,?) ON DUPLICATE KEY UPDATE `count` = ?";
    }

    @Override
    public void serialize(PreparedStatement st, Map.Entry<String, Object> kv) throws SQLException {

        Bukkit.getLogger().log(Level.WARNING, "---Hmmm Kaaave---");
        Bukkit.getLogger().log(Level.WARNING, kv.getKey());
        var configuration = (CaseMemoryConfiguration) kv.getValue();

        var caseName = kv.getKey();
        for (var item : configuration.getKeys(false)) {

            var itemConfiguration = (CaseMemoryConfiguration) configuration.getConfigurationSection(item);
            assert itemConfiguration != null;

            for (var hash : itemConfiguration.getKeys(false)) {
                var hashConfiguration = (CaseMemoryConfiguration) itemConfiguration.getConfigurationSection(hash);
                assert hashConfiguration != null;

                if (!hashConfiguration.isDirty("count")) {
                    continue;
                }

                var value = WareHousePlugin.yaml.dump(hashConfiguration.getItemStack("item"));
                var count = hashConfiguration.getInt("count", 0);

                Bukkit.getLogger().log(Level.WARNING, "---Values---");
                Bukkit.getLogger().log(Level.WARNING, caseName);
                Bukkit.getLogger().log(Level.WARNING, item);
                Bukkit.getLogger().log(Level.WARNING, hash);
                Bukkit.getLogger().log(Level.WARNING, value);
                Bukkit.getLogger().log(Level.WARNING, Integer.toString(count));
                Bukkit.getLogger().log(Level.WARNING, "---End Values---");

                st.setString(1, caseName);
                st.setString(2, item);
                st.setString(3, hash);
                st.setString(4, value);
                st.setInt(5, count);
                st.setInt(6, count);

                st.addBatch();
                st.clearParameters();
            }
        }
    }

    @Override
    protected void loadFromResultSet(ResultSet rs) throws SQLException {
        while (rs.next()) {
            var caseName = rs.getString("case");
            var itemName = rs.getString("item");
            var hash = rs.getString("hash");

            var hashConfiguration = this.getConfig(caseName, itemName, hash);

            hashConfiguration.set("count", rs.getInt("count"));
            hashConfiguration.set("item", WareHousePlugin.yaml.load(rs.getString("value")));
        }
    }

    public CaseMemoryConfiguration createConfiguration(String name) {
        var config = new CaseMemoryConfiguration(this);
        this.set(name, config);
        return config;
    }

    public CaseMemoryConfiguration getConfig(String caseName) {
        var caseConfig = (CaseMemoryConfiguration) this.getConfigurationSection(caseName);
        if (caseConfig == null) {
            caseConfig = this.createConfiguration(caseName);
        }

        return caseConfig;
    }

    public CaseMemoryConfiguration getConfig(String caseName, String itemName) {
        var caseConfig = this.getConfig(caseName);

        var itemConfig = (CaseMemoryConfiguration) caseConfig.getConfigurationSection(itemName);
        if (itemConfig == null) {
            itemConfig = caseConfig.createConfiguration(itemName);
        }

        return itemConfig;
    }

    public CaseMemoryConfiguration getConfig(String caseName, String itemName, String hash) {
        var itemConfig = this.getConfig(caseName, itemName);

        var hashConfig = (CaseMemoryConfiguration) itemConfig.getConfigurationSection(hash);
        if (hashConfig == null) {
            hashConfig = itemConfig.createConfiguration(hash);
        }

        return hashConfig;
    }

    @Override
    public void onSaveCompleted() {
        super.onSaveCompleted();

        for (var key : this.getKeys(false)) {
            var configuration = this.getConfig(key);
            if (configuration == null) {
                continue;
            }

            configuration.onSaveCompleted();
        }
    }
}
