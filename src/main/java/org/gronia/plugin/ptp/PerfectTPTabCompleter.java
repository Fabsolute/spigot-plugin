package org.gronia.plugin.ptp;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.gronia.plugin.SubTabCompleter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PerfectTPTabCompleter extends SubTabCompleter<PerfectTPPlugin> {
    private final List<String> output = new ArrayList<>();

    public PerfectTPTabCompleter(PerfectTPPlugin plugin) {
        super(plugin);
    }

    @Override
    public List<String> onSubTabComplete(CommandSender commandSender, Command cmd, String s, String[] args) {
        output.clear();
        if (!(commandSender instanceof Player)) {
            return output;
        }

        if (args.length == 1) {
            output.add("list");
            output.add("take");
            output.add("add");
            output.add("remove");
            output.add("icon");
        }

        if (args.length == 2) {
            String command = args[0];
            if (command.equalsIgnoreCase("remove") || command.equalsIgnoreCase("icon")) {
                ConfigurationSection config = this.getPlugin().getConfig();
                Set<String> keys = config.getKeys(false);
                this.output.addAll(keys);
                for (Player player : this.getPlugin().getServer().getOnlinePlayers()) {
                    this.output.add(player.getName());
                }
            }
        }

        if (args.length == 3) {
            String command = args[0];
            if (command.equalsIgnoreCase("icon")) {
                String filter = args[2];
                for (Material m : Material.values()) {
                    String material = m.name().toLowerCase();

                    if (!filter.equals("")) {
                        if (!material.contains(filter)) {
                            continue;
                        }
                    }

                    output.add(material);
                }
            }
        }

        return output;
    }
}
