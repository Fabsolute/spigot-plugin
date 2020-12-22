package org.gronia.plugin.swap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gronia.plugin.SubTabCompleter;

import java.util.ArrayList;
import java.util.List;

public class SwapTabCompleter extends SubTabCompleter<SwapPlugin> {
    private final List<String> output = new ArrayList<>();

    public SwapTabCompleter(SwapPlugin plugin) {
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
            if (this.getPlugin().players.contains(player)) {
                output.add("deregister");
            } else {
                output.add("register");
            }

            if (this.getPlugin().started) {
                output.add("end");
            } else {
                output.add("start");
            }
        }

        return output;
    }
}
