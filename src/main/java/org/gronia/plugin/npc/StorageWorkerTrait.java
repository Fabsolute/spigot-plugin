package org.gronia.plugin.npc;

import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

public class StorageWorkerTrait extends Trait {
    public StorageWorkerTrait() {
        super("StorageWorker");
    }


    @EventHandler
    public void click(NPCRightClickEvent event) {
        if (!event.getNPC().hasTrait(StorageWorkerTrait.class)) {
            return;
        }

        Bukkit.getServer().dispatchCommand(event.getClicker(), "storage list 1");
    }
}
