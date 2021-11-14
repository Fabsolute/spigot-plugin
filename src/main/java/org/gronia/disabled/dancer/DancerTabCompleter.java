package org.gronia.disabled.dancer;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gronia.plugin.SubTabCompleter;

import java.util.ArrayList;
import java.util.List;

public class DancerTabCompleter extends SubTabCompleter<DancerPlugin> {
    private final List<String> output = new ArrayList<>();

    public DancerTabCompleter(DancerPlugin plugin) {
        super(plugin);
    }

    @Override
    public List<String> onSubTabComplete(CommandSender commandSender, Command cmd, String s, String[] args) {
        output.clear();
        if (!(commandSender instanceof Player)) {
            return output;
        }

        if (args.length == 1) {
            output.add("enable");
            output.add("disable");
        }

        return output;
    }
}
