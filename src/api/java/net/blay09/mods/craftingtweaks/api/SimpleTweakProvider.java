package net.blay09.mods.craftingtweaks.api;


import net.minecraft.inventory.Container;

public interface SimpleTweakProvider<T extends Container> extends TweakProvider<T> {

    /**
     * Sets the settings for the rotation tweak.
     * @param enabled if false, this tweak will not be executed and the button will not show up
     * @param buttonX the x position of the tweak button, relative to the top left corner of the GUI
     * @param buttonY the y position of the tweak button, relative to the top left corner of the GUI
     */
    void setTweakRotate(boolean enabled, boolean showButton, int buttonX, int buttonY);

    /**
     * Sets the settings for the balance tweak.
     * @param enabled if false, this tweak will not be executed and the button will not show up
     * @param buttonX the x position of the tweak button, relative to the top left corner of the GUI
     * @param buttonY the y position of the tweak button, relative to the top left corner of the GUI
     */
    void setTweakBalance(boolean enabled, boolean showButton, int buttonX, int buttonY);

    /**
     * Sets the settings for the clear tweak.
     * @param enabled if false, this tweak will not be executed and the button will not show up
     * @param buttonX the x position of the tweak button, relative to the top left corner of the GUI
     * @param buttonY the y position of the tweak button, relative to the top left corner of the GUI
     */
    void setTweakClear(boolean enabled, boolean showButton, int buttonX, int buttonY);

    /**
     * Sets the crafting grid index within the inventorySlots list in the container.
     * @param slotNumber the index of the first crafting matrix slot within the inventorySlots list (NOT within the inventory)
     * @param size the size of the crafting grid
     */
    void setGrid(int slotNumber, int size);

    /**
     * @param hideButtons true if tweak buttons should not be added to the GUI
     */
    void setHideButtons(boolean hideButtons);

    /**
     * @param phantomItems true if the crafting grid contains phantom items
     */
    void setPhantomItems(boolean phantomItems);
}
