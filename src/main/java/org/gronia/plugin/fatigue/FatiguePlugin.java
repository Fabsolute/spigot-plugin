package org.gronia.plugin.fatigue;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.gronia.plugin.*;

public class FatiguePlugin extends SubPlugin<FatiguePlugin> {
    private final FatigueUtil util;
    private int taskId;

    public FatiguePlugin(JavaPlugin plugin) {
        super(plugin);
        this.util = new FatigueUtil();
    }

    @Override
    public String getName() {
        return "fatigue";
    }

    @Override
    public SubListener<FatiguePlugin> getListener() {
        return null;
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
        this.taskId = Bukkit.getScheduler().scheduleAsyncRepeatingTask(Gronia.getInstance(), this::increaseFatigue, 100L, 100L);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        Bukkit.getScheduler().cancelTask(this.taskId);
    }

    private void increaseFatigue() {
        for (var player : Bukkit.getOnlinePlayers()) {
            this.getUtil().changeFatigue(player, 1);
        }
    }
}
