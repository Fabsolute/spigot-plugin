package org.gronia.plugin.fatigue;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataType;
import org.gronia.plugin.Gronia;
import org.gronia.plugin.SubUtil;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class FatigueUtil extends SubUtil<FatiguePlugin> {
    private final Map<String, BossBar> bossBarMap = new HashMap<>();

    public static final int MAX_RESTNESS = 300;
    private NamespacedKey RESTNESS_KEY;
    private NamespacedKey EXHAUSTED_KEY;
    private NamespacedKey STEROID_KEY;

    public FatigueUtil(FatiguePlugin plugin) {
        super(plugin);
    }

    public void onEnable() {
        this.RESTNESS_KEY = Gronia.getInstance().getKey("restness");
        this.EXHAUSTED_KEY = Gronia.getInstance().getKey("exhausted");
        this.STEROID_KEY = Gronia.getInstance().getKey("steroid");
    }

    public void onDisable() {
        for (var entry : bossBarMap.entrySet()) {
            var player = Gronia.getInstance().getServer().getPlayer(entry.getKey());
            if (player == null) {
                continue;
            }

            player.hideBossBar(entry.getValue());
        }

        bossBarMap.clear();
    }

    public void onDisconnect(PlayerQuitEvent event) {
        bossBarMap.remove(event.getPlayer().getName());
    }

    public void changeRestness(Player player, int change) {
        var container = player.getPersistentDataContainer();
        if (change <= 0) {
            if (container.has(STEROID_KEY, PersistentDataType.INTEGER)) {
                return;
            }
        }

        var fatigue = getFatigue(player);
        var oldFatigue = fatigue;

        fatigue += change;
        if (fatigue > MAX_RESTNESS) {
            fatigue = MAX_RESTNESS;
        } else if (fatigue < 0) {
            fatigue = 0;
        }

        if (oldFatigue == fatigue) {
            return;
        }


        if (fatigue == 0) {
            player.showTitle(Title.title(
                    Component.text("EXHAUSTED", NamedTextColor.RED),
                    Component.text("You are exhausted.", NamedTextColor.GOLD),
                    Title.Times.of(Duration.ZERO, Duration.ofSeconds(1), Duration.ZERO)
            ));
            container.set(EXHAUSTED_KEY, PersistentDataType.INTEGER, 1);
        } else if (fatigue == MAX_RESTNESS) {
            player.showTitle(Title.title(
                    Component.text("RESTED", NamedTextColor.GREEN),
                    Component.text("You are fully rested.", NamedTextColor.GOLD),
                    Title.Times.of(Duration.ZERO, Duration.ofSeconds(1), Duration.ZERO)
            ));
        }

        if (fatigue > (MAX_RESTNESS / 4)) {
            if (container.has(EXHAUSTED_KEY, PersistentDataType.INTEGER)) {
                player.showTitle(Title.title(
                        Component.text("RESTED", NamedTextColor.YELLOW),
                        Component.text("You are rested.", NamedTextColor.GOLD),
                        Title.Times.of(Duration.ZERO, Duration.ofSeconds(1), Duration.ZERO)
                ));
                container.remove(EXHAUSTED_KEY);
            }
        }

        player.sendActionBar(Component.text(fatigue + " / " + MAX_RESTNESS, NamedTextColor.BLUE));
        container.set(RESTNESS_KEY, PersistentDataType.INTEGER, fatigue);
    }

    public void changeSteroid(Player player, int change) {
        var container = player.getPersistentDataContainer();
        var steroid = getSteroid(player);
        var oldSteroid = steroid;

        steroid += change;
        if (steroid < 0) {
            steroid = 0;
        }

        if (oldSteroid == steroid) {
            return;
        }
        var bossBar = bossBarMap.get(player.getName());
        if (bossBar == null) {
            final Component name = Component.text("Tireless 02:00:00");
            bossBar = BossBar.bossBar(
                    name,
                    1f,
                    BossBar.Color.BLUE,
                    BossBar.Overlay.NOTCHED_12
            );

            player.showBossBar(bossBar);
            this.bossBarMap.put(player.getName(), bossBar);
        }

        bossBar.name(Component.text("Tireless " + this.formatTimer(steroid), NamedTextColor.RED));
        bossBar.progress(Math.min(1, steroid / 7200f));

        if (steroid == 0) {
            player.showTitle(Title.title(
                    Component.text("TIMES UP!", NamedTextColor.RED),
                    Component.text("You are not tireless anymore.", NamedTextColor.GOLD),
                    Title.Times.of(Duration.ZERO, Duration.ofSeconds(3), Duration.ZERO)
            ));

            player.hideBossBar(bossBar);
            this.bossBarMap.remove(player.getName());

            container.remove(STEROID_KEY);
        } else {
            container.set(STEROID_KEY, PersistentDataType.INTEGER, steroid);
        }
    }

    private String formatTimer(int second) {
        int minute = second / 60;
        second -= minute * 60;
        int hour = minute / 60;
        minute -= hour * 60;
        return String.format("%02d:%02d:%02d", hour, minute, second);
    }

    public int getFatigue(Player player) {
        var container = player.getPersistentDataContainer();
        return container.getOrDefault(RESTNESS_KEY, PersistentDataType.INTEGER, MAX_RESTNESS);
    }

    public int getSteroid(Player player) {
        var container = player.getPersistentDataContainer();
        return container.getOrDefault(STEROID_KEY, PersistentDataType.INTEGER, 0);
    }

    public boolean canBreak(Player player, int length) {
        var container = player.getPersistentDataContainer();
        if (container.has(STEROID_KEY, PersistentDataType.INTEGER)) {
            return true;
        }

        if (container.has(EXHAUSTED_KEY, PersistentDataType.INTEGER)) {
            return false;
        }

        return getFatigue(player) >= length;
    }
}
