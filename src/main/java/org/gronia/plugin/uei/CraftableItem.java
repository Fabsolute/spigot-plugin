package org.gronia.plugin.uei;

public interface CraftableItem<T extends CustomRecipe> {
    void fillRecipe(T recipe);

    default boolean isShaped() {
        return false;
    }
}
