package org.gronia.plugin.chunk;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.gronia.plugin.SubTabCompleter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ChunkTabCompleter extends SubTabCompleter<ChunkPlugin> {
    private final List<String> output = new ArrayList<>();

    public ChunkTabCompleter(ChunkPlugin plugin) {
        super(plugin);
    }

    @Override
    public List<String> onSubTabComplete(CommandSender commandSender, Command cmd, String s, String[] args) {
        output.clear();
        if (!(commandSender instanceof Player)) {
            return output;
        }

        Player player = (Player) commandSender;

        if (args.length == 1) {
            output.add("list");
            output.add("tp");
            output.add("add");
            output.add("remove");
        }

        if (args.length == 2) {
            String command = args[0];
            if (command.equalsIgnoreCase("tp") || command.equalsIgnoreCase("remove")) {

                ConfigurationSection config = this.getPlugin().getConfig().getConfigurationSection(player.getWorld().getName());
                if (config != null) {
                    Set<String> keys = config.getKeys(false);
                    this.output.addAll(keys);
                }
            }
        }

        return output;
    }
}
