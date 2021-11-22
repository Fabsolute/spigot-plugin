package org.gronia.plugin.storage;

import java.util.List;

public record StorageCategory(boolean enabled, String name, String icon, List<String> items) {
}
