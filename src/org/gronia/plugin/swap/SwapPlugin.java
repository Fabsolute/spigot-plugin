package org.gronia.plugin.swap;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.gronia.plugin.*;

import java.util.ArrayList;
import java.util.List;

public class SwapPlugin extends SubPlugin<SwapPlugin> {
    public final List<Player> players = new ArrayList<>();
    public SwapTask task = null;
    public boolean started = false;

    public SwapPlugin(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (task != null) {
            task.cancel();
        }
    }

    @Override
    public String getName() {
        return "swap";
    }

    @Override
    public SubListener<SwapPlugin> getListener() {
        return null;
    }

    @Override
    public SubCommandExecutor<SwapPlugin> getExecutor() {
        return new SwapCommand(this);
    }

    @Override
    public SubTabCompleter<SwapPlugin> getTabCompleter() {
        return new SwapTabCompleter(this);
    }
}
