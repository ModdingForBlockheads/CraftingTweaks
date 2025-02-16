package net.blay09.mods.craftingtweaks.api;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

import java.util.function.Consumer;

public interface GridGuiHandler {
    /**
     * Called to add buttons to the GUI. May not be called if buttons are disabled in the configuration.
     * Use CraftingTweaksAPI.create***Button() to create tweak buttons, then add them to the buttonList.
     *
     * @param screen        the gui container the buttons are being added to
     * @param addWidgetFunc function to call for adding widgets to the screen
     */
    void createButtons(AbstractContainerScreen<?> screen, CraftingGrid grid, Consumer<AbstractWidget> addWidgetFunc);
}
