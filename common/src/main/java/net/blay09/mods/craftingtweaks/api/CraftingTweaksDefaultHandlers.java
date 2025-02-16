package net.blay09.mods.craftingtweaks.api;

import net.minecraft.world.inventory.AbstractContainerMenu;

import java.lang.reflect.InvocationTargetException;

public class CraftingTweaksDefaultHandlers {

    private static final InternalMethods internalMethods = loadInternalMethods();

    private static InternalMethods loadInternalMethods() {
        try {
            return (InternalMethods) Class.forName("net.blay09.mods.craftingtweaks.api.impl.InternalMethodsImpl").getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                 ClassNotFoundException e) {
            throw new RuntimeException("Failed to load Crafting Tweaks API", e);
        }
    }

    public static GridTransferHandler<AbstractContainerMenu> defaultTransferHandler() {
        return internalMethods.defaultTransferHandler();
    }

    public static GridBalanceHandler<AbstractContainerMenu> defaultBalanceHandler() {
        return internalMethods.defaultBalanceHandler();
    }

    public static GridClearHandler<AbstractContainerMenu> defaultClearHandler() {
        return internalMethods.defaultClearHandler();
    }

    public static GridRotateHandler<AbstractContainerMenu> defaultRotateHandler() {
        return internalMethods.defaultRotateHandler();
    }

    public static GridRotateHandler<AbstractContainerMenu> defaultFourByFourRotateHandler() {
        return internalMethods.defaultFourByFourRotateHandler();
    }

    public static GridRotateHandler<AbstractContainerMenu> defaultRectangularRotateHandler() {
        return internalMethods.defaultRectangularRotateHandler();
    }
}
