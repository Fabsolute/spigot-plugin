package org.gronia.npc;

import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import org.bukkit.event.EventHandler;
import org.gronia.plugin.Gronia;
import org.gronia.plugin.warehouse.WareHousePlugin;

public class WareHouseWorkerTrait extends Trait {
    public WareHouseWorkerTrait() {
        super("WareHouseWorker");
    }

    @EventHandler
    public void click(NPCRightClickEvent event) {
        if (!event.getNPC().hasTrait(WareHouseWorkerTrait.class)) {
            return;
        }

        Gronia.getInstance().getSubPlugin(WareHousePlugin.class).executeListCommand(event.getClicker());
    }
}
