package org.gronia.plugin.uei;

import org.bukkit.event.Event;
import org.gronia.utils.pair.Pair;

import java.util.List;

public interface EventListenerItem {
    List<Pair<? extends Event>> getEventConsumers();
}
