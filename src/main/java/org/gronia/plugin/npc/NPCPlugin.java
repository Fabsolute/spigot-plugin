package org.gronia.plugin.npc;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;
import org.bukkit.plugin.java.JavaPlugin;
import org.gronia.plugin.SubCommandExecutor;
import org.gronia.plugin.SubListener;
import org.gronia.plugin.SubPlugin;
import org.gronia.plugin.SubTabCompleter;

public class NPCPlugin extends SubPlugin<NPCPlugin> {
    public final String BLACKSMITH_TITLE = "Blacksmith";

    public NPCPlugin(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "g-npc";
    }

    @Override
    public SubListener<NPCPlugin> getListener() {
        return new NPCListener(this);
    }

    @Override
    public SubCommandExecutor<NPCPlugin> getExecutor() {
        return null;
    }

    @Override
    public SubTabCompleter<NPCPlugin> getTabCompleter() {
        return null;
    }

    @Override
    public void onEnable() {
        super.onEnable();

        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(BlacksmithTrait.class).withName("Blacksmith"));
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(StorageWorkerTrait.class).withName("StorageWorker"));
    }
}
