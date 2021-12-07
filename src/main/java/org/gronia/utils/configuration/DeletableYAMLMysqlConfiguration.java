package org.gronia.utils.configuration;

import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DeletableYAMLMysqlConfiguration extends YAMLMysqlConfiguration {
    private final List<String> deletedList = new ArrayList<>();

    @Override
    public void set(@NotNull String path, Object value) {
        super.set(path, value);
        this.setDirty();
        if (value == null) {
            if (!this.deletedList.contains(path)) {
                this.deletedList.add(path);
            }
        }
    }

    @Override
    public void onSaveCompleted() {
        if (this.deletedList.size() == 0) {
            super.onSaveCompleted();
            return;
        }

        try {
            var st = prepareStatement("DELETE FROM " + name + " WHERE `key` = ?");
            for (var key : this.deletedList) {
                st.setString(1, key);
                st.addBatch();
                st.clearParameters();
            }

            st.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        this.deletedList.clear();
        super.onSaveCompleted();
    }
}
