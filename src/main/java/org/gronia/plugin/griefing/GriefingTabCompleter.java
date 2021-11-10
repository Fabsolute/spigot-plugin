package org.gronia.plugin.griefing;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gronia.plugin.SubTabCompleter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GriefingTabCompleter extends SubTabCompleter<GriefingPlugin> {
    private final List<String> output = new ArrayList<>();

    public GriefingTabCompleter(GriefingPlugin plugin) {
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
            output.add("add");
            output.add("remove");
        }

        if (args.length == 2) {
            String command = args[0];
            if (command.equalsIgnoreCase("add")) {
                Set<String> allowable = new HashSet<>(this.getPlugin().entityTypeSettingLookup.values());
                allowable.add("farmland");

                List<String> allowed = this.getPlugin().getConfig().getStringList("allowed");

                for (String block : allowed) {
                    allowable.remove(block);
                }

                this.output.addAll(allowable);
            }

            if (command.equalsIgnoreCase("remove")) {
                List<String> allowed = this.getPlugin().getConfig().getStringList("allowed");
                this.output.addAll(allowed);
            }
        }

        return output;
    }
}
