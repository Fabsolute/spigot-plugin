package org.gronia.plugin.fatigue;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.gronia.plugin.*;

public class FatiguePlugin extends SubPlugin<FatiguePlugin> {
    private final FatigueUtil util;
    private int restTaskId;
    private int steroidTaskId;

    public FatiguePlugin(JavaPlugin plugin) {
        super(plugin);
        this.util = new FatigueUtil(this);
    }

    @Override
    public String getName() {
        return "fatigue";
    }

    @Override
    public SubListener<FatiguePlugin> getListener() {
        return new FatigueListener(this);
    }

    @Override
    public SubCommandExecutor<FatiguePlugin> getExecutor() {
        return null;
    }

    @Override
    public SubTabCompleter<FatiguePlugin> getTabCompleter() {
        return null;
    }

    public FatigueUtil getUtil() {
        return util;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.util.onEnable();
        this.restTaskId = Bukkit.getScheduler().scheduleAsyncRepeatingTask(Gronia.getInstance(), this::increaseRestness, 100L, 100L);
        this.steroidTaskId = Bukkit.getScheduler().scheduleAsyncRepeatingTask(Gronia.getInstance(), this::decreaseSteroid, 20L, 20L);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.util.onDisable();
        Bukkit.getScheduler().cancelTask(this.restTaskId);
        Bukkit.getScheduler().cancelTask(this.steroidTaskId);
    }

    private void increaseRestness() {
        for (var player : Bukkit.getOnlinePlayers()) {
            this.getUtil().changeRestness(player, 1);
        }
    }

    private void decreaseSteroid() {
        for (var player : Bukkit.getOnlinePlayers()) {
            this.getUtil().changeSteroid(player, -1);
        }
    }
}
