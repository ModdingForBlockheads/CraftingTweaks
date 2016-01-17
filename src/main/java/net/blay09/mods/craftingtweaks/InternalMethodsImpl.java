package net.blay09.mods.craftingtweaks;

import net.blay09.mods.craftingtweaks.api.*;
import net.blay09.mods.craftingtweaks.client.GuiTweakButton;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class InternalMethodsImpl implements InternalMethods {

    @Override
    public <T extends Container> void registerProvider(Class<T> containerClass, TweakProvider<T> provider) {
        CraftingTweaks.instance.registerProvider(containerClass, provider);
    }

    @Override
    public <T extends Container> SimpleTweakProvider<T> registerSimpleProvider(String modid, Class<T> containerClass) {
        SimpleTweakProvider<T> simpleTweakProvider = new SimpleTweakProviderImpl<>(modid);
        CraftingTweaks.instance.registerProvider(containerClass, simpleTweakProvider);
        return simpleTweakProvider;
    }

    @Override
    public DefaultProvider createDefaultProvider() {
        return new DefaultProviderImpl();
    }

    @Override
    public DefaultProviderV2 createDefaultProviderV2() {
        return new DefaultProviderV2Impl();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiButton createBalanceButton(int id, int x, int y) {
        return new GuiTweakButton(x, y, 48, 0, GuiTweakButton.TweakOption.Balance, id);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiButton createRotateButton(int id, int x, int y) {
        return new GuiTweakButton(x, y, 16, 0, GuiTweakButton.TweakOption.Rotate, id);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiButton createClearButton(int id, int x, int y) {
        return new GuiTweakButton(x, y, 32, 0, GuiTweakButton.TweakOption.Clear, id);
    }

}
