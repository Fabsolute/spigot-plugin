package org.gronia.plugin.ge;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.gronia.plugin.Gronia;
import xyz.janboerman.guilib.api.GuiInventoryHolder;
import xyz.janboerman.guilib.api.GuiListener;

public class DragPage extends GuiInventoryHolder<Gronia> {
    public DragPage(GuiListener guiListener, Gronia plugin) {
        super(guiListener, plugin, 45);
    }

    public DragPage(Gronia plugin) {
        super(plugin, 45);
    }

    @Override
    public void onDrag(InventoryDragEvent ev) {
        ev.getView().getPlayer().sendMessage("Drag!");
        ev.setCancelled(false);
    }

    @Override
    public void onClick(InventoryClickEvent ev) {
        ev.getView().getPlayer().sendMessage("Click!");
        ev.setCancelled(false);
    }
}
