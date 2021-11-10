package org.gronia.plugin.swap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

public class SwapTask extends BukkitRunnable {

    private final SwapPlugin plugin;

    public SwapTask(SwapPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        BukkitScheduler scheduler = this.plugin.getServer().getScheduler();
        for (int k = 10; k > 0; k--) {
            int finalK = 11 - k;
            scheduler.scheduleSyncDelayedTask(this.plugin.getPlugin(), () -> plugin.getServer().broadcastMessage("[Swap] " + ChatColor.RED + "Teleporting in " + finalK + " seconds."), k * 20L);
        }

        scheduler.scheduleSyncDelayedTask(this.plugin.getPlugin(), () -> {
            Location[] locations = new Location[plugin.players.size()];
            int i = 0;
            for (Player player : plugin.players) {
                locations[i] = player.getLocation();
                i++;
            }

            for (int j = 0; j < i; j++) {
                plugin.players.get(j).teleport(locations[(j + 1) % i], PlayerTeleportEvent.TeleportCause.PLUGIN);
            }

            this.plugin.getServer().broadcastMessage("[Swap] " + ChatColor.GREEN + "Players teleported.");
        }, 11 * 20);
    }
}
