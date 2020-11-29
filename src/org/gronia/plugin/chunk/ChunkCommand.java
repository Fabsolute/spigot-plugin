package org.gronia.plugin.chunk;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.gronia.plugin.SubCommandExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ChunkCommand extends SubCommandExecutor<ChunkPlugin> {
    public ChunkCommand(ChunkPlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean onSubCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            return this.warnUser(player);
        }

        String command = args[0];

        if (command.equalsIgnoreCase("list")) {
            return this.handleList(player);
        }

        if (args.length < 2) {
            return this.warnUser(player);
        }

        if (command.equalsIgnoreCase("remove")) {
            return this.handleRemove(player, args[1].toLowerCase());
        }

        if (command.equalsIgnoreCase("add")) {
            return this.handleAdd(player, args[1].toLowerCase());
        }

        if (command.equalsIgnoreCase("tp")) {
            return this.handleTP(player, args[1].toLowerCase());
        }

        return this.warnUser(player);
    }

    public boolean handleList(Player player) {
        ConfigurationSection config = this.getPlugin().getConfig().getConfigurationSection(player.getWorld().getName());
        if (config == null) {
            player.sendMessage("[Chunk] " + ChatColor.RED + "There is no chunk to list.");
            return true;
        }

        Set<String> keys = config.getKeys(false);
        for (String name : keys) {
            List<Integer> coordinates = config.getIntegerList(name);
            player.sendMessage("[Chunk] " + ChatColor.GREEN + name + ChatColor.AQUA + " [" + coordinates.get(0) + ", " + coordinates.get(1) + "]");
        }
        return true;
    }

    public boolean handleAdd(Player player, String name) {
        ConfigurationSection config = this.getPlugin().getConfig().getConfigurationSection(player.getWorld().getName());
        if (config == null) {
            config = new MemoryConfiguration();
            this.getPlugin().getConfig().set(player.getWorld().getName(), config);
        }

        if (config.get(name, null) != null) {
            player.sendMessage("[Chunk] " + ChatColor.RED + "This chunk name is already exists.");
            return true;
        }

        Chunk chunk = player.getWorld().getChunkAt(player.getLocation());
        int x = chunk.getX();
        int z = chunk.getZ();

        Set<String> keys = config.getKeys(false);

        for (String key : keys) {
            List<Integer> list = config.getIntegerList(key);
            if (list.get(0) == x && list.get(1) == z) {
                player.sendMessage("[Chunk] " + ChatColor.RED + "This chunk is already exists.");
                return true;
            }
        }

        List<Integer> coords = new ArrayList<>();
        coords.add(x);
        coords.add(z);

        config.set(name, coords);

        this.activateChunk(chunk);

        player.sendMessage("[Chunk] " + ChatColor.GREEN + "Chunk is activated.");
        this.save();

        return true;
    }

    public boolean handleRemove(Player player, String name) {
        ConfigurationSection config = this.getPlugin().getConfig().getConfigurationSection(player.getWorld().getName());
        boolean found = false;

        if (config != null) {
            if (config.get(name, null) != null) {
                found = true;
            }
        }

        if (!found) {
            player.sendMessage("[Chunk] " + ChatColor.RED + "There is no chunk to remove.");
            return true;
        }
        List<Integer> list = config.getIntegerList(name);

        Chunk chunk = player.getWorld().getChunkAt(list.get(0), list.get(1));
        config.set(name, null);

        this.deactivateChunk(chunk);

        this.save();

        player.sendMessage("[Chunk] " + ChatColor.GREEN + "Chunk is deactivated.");
        return true;
    }

    public boolean handleTP(Player player, String name) {
        ConfigurationSection config = this.getPlugin().getConfig().getConfigurationSection(player.getWorld().getName());
        boolean found = false;

        if (config != null) {
            if (config.get(name, null) != null) {
                found = true;
            }
        }

        if (!found) {
            player.sendMessage("[Chunk] " + ChatColor.RED + "There is no chunk to teleport.");
            return true;
        }

        List<Integer> list = config.getIntegerList(name);

        int x = list.get(0) << 4;
        int z = list.get(1) << 4;
        int y = player.getWorld().getHighestBlockAt(x, z).getY() + 1;

        player.teleport(new Location(player.getWorld(), x, y, z), PlayerTeleportEvent.TeleportCause.COMMAND);
        return true;
    }

    private boolean warnUser(Player player) {
        player.sendMessage("[Chunk] " + ChatColor.WHITE + "Type " + ChatColor.GREEN + "/help chunk" + ChatColor.WHITE + " for help");
        return true;
    }

    private void activateChunk(Chunk chunk) {
        chunk.setForceLoaded(true);
        if (chunk.isLoaded()) {
            chunk.load();
        }
    }

    private void deactivateChunk(Chunk chunk) {
        chunk.setForceLoaded(false);
    }

    private void save() {
        this.getPlugin().saveConfig();
    }
}
