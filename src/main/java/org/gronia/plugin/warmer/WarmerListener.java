package org.gronia.plugin.warmer;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.gronia.plugin.Gronia;
import org.gronia.plugin.SubListener;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;

public class WarmerListener extends SubListener<WarmerPlugin> {
    private BossBar warmer;

    public WarmerListener(WarmerPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerJoin(AsyncPlayerPreLoginEvent event) {
        var onlinePlayers = this.getPlugin().getServer().getOnlinePlayers();
        if (onlinePlayers.size() > 0) {
            if (!onlinePlayers.stream().findFirst().get().getName().equalsIgnoreCase(event.getName())) {
                return;
            }
        }

        OfflinePlayer nearest = null;
        for (var offline : this.getPlugin().getServer().getOfflinePlayers()) {
            if (nearest == null || nearest.getLastSeen() < offline.getLastSeen()) {
                nearest = offline;
            }
        }

        if (nearest == null) {
            return;
        }

        var time = new Date();
        var secondPassed = (time.getTime() - nearest.getLastSeen()) / 1000;

        if (secondPassed < 10 * 60) {
            return;
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(this.getPlugin().getPlugin(), () -> {
            this.warmServer(Math.min(secondPassed / 100, 60));
        }, 100);
    }

    private void warmServer(long speedTime) {
        if (this.warmer != null) {
            this.getPlugin().getServer().hideBossBar(warmer);
        }

        this.warmer = BossBar.bossBar(Component.text("Warming the server: " + speedTime), 0, BossBar.Color.RED, BossBar.Overlay.PROGRESS);
        Bukkit.getServer().showBossBar(this.warmer);


        var oldTick = changeRandomTickSpeedInOverWorld(300);
        if (oldTick == null) {
            oldTick = 3;
        }

        Bukkit.getServer().sendMessage(Component.text("Warming the server"));
        Integer finalOldTick = oldTick;
        Bukkit.getScheduler().scheduleSyncDelayedTask(this.getPlugin().getPlugin(), () -> {
            changeRandomTickSpeedInOverWorld(finalOldTick);
            Bukkit.getServer().sendMessage(Component.text("Warming completed."));
        }, speedTime * 20);

        var atomic = new AtomicLong(speedTime);
        new BukkitRunnable() {
            @Override
            public void run() {
                var now = atomic.addAndGet(-1);
                WarmerListener.this.warmer.name(Component.text("Warming the server: " + now));
                WarmerListener.this.warmer.progress((speedTime - now) / (float) speedTime);
                if (now <= 0) {
                    cancel();
                    Bukkit.getServer().hideBossBar(warmer);
                }
            }
        }.runTaskTimer(Gronia.getInstance(), 20, 20);
    }

    private Integer changeRandomTickSpeedInOverWorld(int value) {
        var world = this.getPlugin().getServer().getWorld("world");
        if (world == null) {
            Bukkit.getLogger().log(Level.WARNING, "World is null");
            return null;
        }

        var speed = world.getGameRuleValue(GameRule.RANDOM_TICK_SPEED);
        world.setGameRule(GameRule.RANDOM_TICK_SPEED, value);
        return speed;
    }
}
