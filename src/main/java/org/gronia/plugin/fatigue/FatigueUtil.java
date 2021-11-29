package org.gronia.plugin.fatigue;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.gronia.plugin.Gronia;

public class FatigueUtil {
    private static final int MAX_FATIGUE = 300;
    private static final String RESTNESS_KEY = "gronia.restness";
    private static final String EXHAUSTED_KEY = "gronia.exhausted";

    public void changeRestness(Player player, int change) {
        var fatigue = getFatigue(player);
        var oldFatigue = fatigue;

        fatigue += change;
        if (fatigue > MAX_FATIGUE) {
            fatigue = MAX_FATIGUE;
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
        } else if (fatigue == MAX_FATIGUE) {
            player.sendTitle(ChatColor.GREEN + "RESTED",
                    ChatColor.GOLD + "You are fully rested.",
                    1,
                    20,
                    1);
        }

        if (fatigue > (MAX_FATIGUE / 4)) {
            if (player.hasMetadata(EXHAUSTED_KEY)) {
                player.sendTitle(ChatColor.YELLOW + "RESTED",
                        ChatColor.GOLD + "You are rested.",
                        1,
                        20,
                        1);
                player.removeMetadata(EXHAUSTED_KEY, Gronia.getInstance());
            }
        }

        player.sendActionBar(new TextComponent(ChatColor.BLUE + "" + fatigue + " / " + MAX_FATIGUE));

        player.setMetadata(RESTNESS_KEY, new FixedMetadataValue(Gronia.getInstance(), fatigue));
    }

    public int getFatigue(Player player) {
        if (player.hasMetadata(RESTNESS_KEY)) {
            var l = player.getMetadata(RESTNESS_KEY);
            for (var ll : l) {
                return ll.asInt();
            }
        }

        return MAX_FATIGUE;
    }

    public boolean canBreak(Player player, int length) {
        if (player.hasMetadata(EXHAUSTED_KEY)) {
            return false;
        }

        return getFatigue(player) >= length;
    }
}
