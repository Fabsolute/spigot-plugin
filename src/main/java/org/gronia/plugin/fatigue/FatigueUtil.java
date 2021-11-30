package org.gronia.plugin.fatigue;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.gronia.plugin.Gronia;

public class FatigueUtil {
    public static final int MAX_RESTNESS = 300;
    private static final String RESTNESS_KEY = "gronia.restness";
    private static final String EXHAUSTED_KEY = "gronia.exhausted";
    private static final String STEROID_KEY = "gronia.steroid";

    public void changeRestness(Player player, int change) {
        if (change <= 0) {
            if (player.hasMetadata(STEROID_KEY)) {
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
            player.sendTitle(ChatColor.RED + "EXHAUSTED",
                    ChatColor.GOLD + "You are exhausted.",
                    1,
                    20,
                    1);
            player.setMetadata(EXHAUSTED_KEY, new FixedMetadataValue(Gronia.getInstance(), true));
        } else if (fatigue == MAX_RESTNESS) {
            player.sendTitle(ChatColor.GREEN + "RESTED",
                    ChatColor.GOLD + "You are fully rested.",
                    1,
                    20,
                    1);
        }

        if (fatigue > (MAX_RESTNESS / 4)) {
            if (player.hasMetadata(EXHAUSTED_KEY)) {
                player.sendTitle(ChatColor.YELLOW + "RESTED",
                        ChatColor.GOLD + "You are rested.",
                        1,
                        20,
                        1);
                player.removeMetadata(EXHAUSTED_KEY, Gronia.getInstance());
            }
        }

        player.sendActionBar(new TextComponent(ChatColor.BLUE + "" + fatigue + " / " + MAX_RESTNESS));

        player.setMetadata(RESTNESS_KEY, new FixedMetadataValue(Gronia.getInstance(), fatigue));
    }

    public void changeSteroid(Player player, int change) {
        var steroid = getSteroid(player);
        var oldSteroid = steroid;

        steroid += change;
        if (steroid < 0) {
            steroid = 0;
        }

        if (oldSteroid == steroid) {
            return;
        }

        player.sendActionBar(new TextComponent(ChatColor.GOLD + "" + steroid));

        if (steroid == 0) {
            player.sendTitle(ChatColor.RED + "TIMES UP",
                    ChatColor.GOLD + "You are not tireless anymore.",
                    1,
                    20,
                    1);
            player.removeMetadata(STEROID_KEY, Gronia.getInstance());
        } else {
            player.setMetadata(STEROID_KEY, new FixedMetadataValue(Gronia.getInstance(), steroid));
        }
    }

    public int getFatigue(Player player) {
        if (player.hasMetadata(RESTNESS_KEY)) {
            var l = player.getMetadata(RESTNESS_KEY);
            for (var ll : l) {
                return ll.asInt();
            }
        }

        return MAX_RESTNESS;
    }

    public int getSteroid(Player player) {
        if (player.hasMetadata(STEROID_KEY)) {
            var l = player.getMetadata(STEROID_KEY);
            for (var ll : l) {
                return ll.asInt();
            }
        }

        return 0;
    }

    public boolean canBreak(Player player, int length) {
        if (player.hasMetadata(STEROID_KEY)) {
            return true;
        }

        if (player.hasMetadata(EXHAUSTED_KEY)) {
            return false;
        }

        return getFatigue(player) >= length;
    }
}
