package net.blay09.mods.craftingtweaks.crafting;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.container.ContainerUtils;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Collection;

public class ContainerIngredientProvider implements IngredientProvider {

    private final Container container;

    public ContainerIngredientProvider(Container container) {
        this.container = container;
    }

    @Override
    public IngredientToken findIngredient(Ingredient ingredient, Collection<IngredientToken> ingredientTokens, IngredientCacheHint cacheHint) {
        if (cacheHint instanceof ContainerIngredientToken containerIngredientToken) {
            final var slotStack = container.getItem(containerIngredientToken.slot);
            if (ingredient.test(slotStack) && hasUsesLeft(containerIngredientToken.slot, slotStack, ingredientTokens)) {
                return containerIngredientToken;
            }
        }

        for (int i = 0; i < container.getContainerSize(); i++) {
            final var slotStack = container.getItem(i);
            if (ingredient.test(slotStack) && hasUsesLeft(i, slotStack, ingredientTokens)) {
                return new ContainerIngredientToken(i);
            }
        }
        return null;
    }

    @Override
    public IngredientToken findIngredient(ItemStack itemStack, Collection<IngredientToken> ingredientTokens, IngredientCacheHint cacheHint) {
        if (cacheHint instanceof ContainerIngredientToken containerIngredientToken) {
            final var slotStack = container.getItem(containerIngredientToken.slot);
            if (ItemStack.isSameItemSameComponents(slotStack, itemStack) && hasUsesLeft(containerIngredientToken.slot, slotStack, ingredientTokens)) {
                return containerIngredientToken;
            }
        }

        for (int i = 0; i < container.getContainerSize(); i++) {
            final var slotStack = container.getItem(i);
            if (ItemStack.isSameItemSameComponents(slotStack, itemStack) && hasUsesLeft(i, slotStack, ingredientTokens)) {
                return new ContainerIngredientToken(i);
            }
        }
        return null;
    }

    protected int getUsesLeft(int slot, ItemStack slotStack, Collection<IngredientToken> ingredientTokens) {
        var usesLeft = slotStack.getCount();
        for (IngredientToken ingredientToken : ingredientTokens) {
            if (ingredientToken instanceof ContainerIngredientToken containerIngredientToken) {
                if (containerIngredientToken.slot == slot) {
                    usesLeft--;
                }
            }
        }

        return usesLeft;
    }

    @Override
    public IngredientCacheHint getCacheHint(IngredientToken ingredientToken) {
        return ingredientToken instanceof ContainerIngredientToken containerIngredientToken ? containerIngredientToken : IngredientCacheHint.NONE;
    }

    private boolean hasUsesLeft(int slot, ItemStack slotStack, Collection<IngredientToken> ingredientTokens) {
        return getUsesLeft(slot, slotStack, ingredientTokens) > 0;
    }

    public class ContainerIngredientToken implements IngredientToken, IngredientCacheHint {
        private final int slot;
        private boolean returnRemainder;

        public ContainerIngredientToken(int slot) {
            this.slot = slot;
        }

        @Override
        public ItemStack peek() {
            return container.getItem(slot);
        }

        @Override
        public ItemStack consume() {
            final var consumed = ContainerUtils.extractItem(container, slot, 1, false);
            if (returnRemainder) {
                final var remainingItem = Balm.getHooks().getCraftingRemainingItem(consumed);
                ContainerUtils.insertItem(container, slot, remainingItem, false);
            }
            return consumed;
        }

        @Override
        public ItemStack restore(ItemStack itemStack) {
            final var restItem = ContainerUtils.insertItem(container, slot, itemStack, false);
            if (!restItem.isEmpty()) {
                return ContainerUtils.insertItemStacked(container, restItem, false);
            }

            return ItemStack.EMPTY;
        }
    }
}
