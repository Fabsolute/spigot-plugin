package org.gronia.plugin.storage;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gronia.plugin.ItemUtils;
import org.gronia.plugin.SubTabCompleter;

import java.util.ArrayList;
import java.util.List;

public class StorageTabCompleter extends SubTabCompleter<StoragePlugin> {
    private final List<String> output = new ArrayList<>();

    public StorageTabCompleter(StoragePlugin plugin) {
        super(plugin);
    }

    @Override
    public List<String> onSubTabComplete(CommandSender commandSender, Command cmd, String s, String[] args) {
        output.clear();

        if (!(commandSender instanceof Player)) {
            return output;
        }

        if (args.length == 1) {
            output.add("open");
            output.add("take");
            output.add("list");
        }

        String command = args[0];

        if (!command.equalsIgnoreCase("take") && !command.equalsIgnoreCase("open")) {
            return output;
        }

        if (args.length == 2) {
            if (command.equalsIgnoreCase("take") || command.equalsIgnoreCase("open")) {
                String filter = args[1];
                for (String material : ItemUtils.getItemNames()) {
                    if (!filter.equals("")) {
                        if (!material.contains(filter)) {
                            continue;
                        }
                    }

                    output.add(material);
                }
            }
        } else if (args.length == 3) {
            if (command.equalsIgnoreCase("take")) {
                String item = args[1];
                var material = ItemUtils.getMaterialFor(item);
                if (material != null) {
                    output.add("" + material.getMaxStackSize());
                }
            }
        }

        return output;
    }
}
