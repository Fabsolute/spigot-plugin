package org.gronia.plugin.fatigue;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.gronia.plugin.SubListener;

public class FatigueListener extends SubListener<FatiguePlugin> {
    public FatigueListener(FatiguePlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.getPlugin().getUtil().onDisconnect(event);
    }
}
