package org.gronia.plugin.npc;

import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.gronia.plugin.Gronia;
import org.gronia.plugin.storage.StoragePlugin;

public class StorageWorkerTrait extends Trait {
    public StorageWorkerTrait() {
        super("StorageWorker");
    }


    @EventHandler
    public void click(NPCRightClickEvent event) {
        if (!event.getNPC().hasTrait(StorageWorkerTrait.class)) {
            return;
        }

        Gronia.getInstance().getSubPlugin(StoragePlugin.class).executeListCommand(event.getClicker());
    }
}
