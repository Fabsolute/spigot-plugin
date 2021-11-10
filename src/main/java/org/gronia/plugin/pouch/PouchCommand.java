package org.gronia.plugin.pouch;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gronia.plugin.SubCommandExecutor;

public class PouchCommand extends SubCommandExecutor<PouchPlugin> {
    public PouchCommand(PouchPlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean onSubCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        if (args.length != 4) {
            return false;
        }

        String command = args[0];

        if (command.equalsIgnoreCase("debt")) {
            return this.handleDebt(args[1], args[2], args[3]);
        }

        return true;
    }

    private boolean handleDebt(String playerName, String materialName, String rawCount) {
        this.getPlugin().getUtils().addDebt(
                playerName,
                Material.valueOf(materialName.toUpperCase()),
                Integer.parseInt(rawCount)
        );
        return true;
    }
}
