package org.gronia.plugin.uei;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.gronia.plugin.*;

public class UltraEnchantedItemPlugin extends SubPlugin<UltraEnchantedItemPlugin> {
    public UltraEnchantedItemPlugin(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "uei";
    }

    @Override
    public SubListener<UltraEnchantedItemPlugin> getListener() {
        return new UltraEnchantedItemListener(this);
    }

    @Override
    public SubCommandExecutor<UltraEnchantedItemPlugin> getExecutor() {
        return new SubCommandExecutor<>(this) {
            @Override
            public boolean onSubCommand(CommandSender sender, Command cmd, String s, String[] args) {
                if (args.length != 1 || !(args[0].equalsIgnoreCase("dont") || args[0].equalsIgnoreCase("do"))) {
                    return false;
                }

                var player = (Player) sender;
                var item = player.getInventory().getItemInMainHand();

                if (args[0].equalsIgnoreCase("do")) {
                    CustomItem.setRepaired(item);
                    return false;
                }

                item.setDurability((short) 2030);
                return false;
            }
        };
    }

    @Override
    public SubTabCompleter<UltraEnchantedItemPlugin> getTabCompleter() {
        return null;
    }
}
