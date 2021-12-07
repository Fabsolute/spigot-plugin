package org.gronia.utils.configuration;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConstructor;
import org.bukkit.configuration.file.YamlRepresenter;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.representer.Representer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class YAMLMysqlConfiguration extends MysqlConfiguration {
    private final LoaderOptions loaderOptions = new LoaderOptions();
    private final Yaml yaml;

    public YAMLMysqlConfiguration() {
        DumperOptions yamlOptions = new DumperOptions();
        Representer yamlRepresenter = new YamlRepresenter();
        this.yaml = new Yaml(new YamlConstructor(), yamlRepresenter, yamlOptions, this.loaderOptions);
    }

    @Override
    public void serialize(PreparedStatement st, Map.Entry<String, Object> kv) throws SQLException {
        var key = kv.getKey();

        var value = yaml.dump(Map.of("value", kv.getValue()));
        if (value.equals("{}\n")) {
            value = "";
        }

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
        this.loaderOptions.setMaxAliasesForCollections(2147483647);

        Map<Object, Object> input = new HashMap<>();
        while (rs.next()) {
            input.put(rs.getString("key"), ((Map) this.yaml.load(rs.getString("value"))).get("value"));
        }

        this.convertMapsToSections(input, this);
    }

    protected void convertMapsToSections(Map<?, ?> input, ConfigurationSection section) {
        for (var entry : input.entrySet()) {
            String key = entry.getKey().toString();
            Object value = entry.getValue();
            if (value instanceof Map map) {
                this.convertMapsToSections(map, section.createSection(key));
            } else {
                section.set(key, value);
            }
        }
    }
}
