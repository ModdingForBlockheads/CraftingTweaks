package net.blay09.mods.craftingtweaks.network;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.craftingtweaks.*;
import net.blay09.mods.craftingtweaks.api.CraftingGrid;
import net.blay09.mods.craftingtweaks.config.CraftingTweaksConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.RecipeHolder;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.Objects;

public class CompressMessage {

    private final int slotNumber;
    private final CompressType type;

    public CompressMessage(int slotNumber, CompressType type) {
        this.slotNumber = slotNumber;
        this.type = type;
    }

    public static CompressMessage decode(FriendlyByteBuf buf) {
        int slotNumber = buf.readInt();
        CompressType type = CompressType.values()[buf.readByte()];
        return new CompressMessage(slotNumber, type);
    }

    public static void encode(CompressMessage message, FriendlyByteBuf buf) {
        buf.writeInt(message.slotNumber);
        buf.writeByte(message.type.ordinal());
    }

    public static void handle(ServerPlayer player, CompressMessage message) {
        if (player == null) {
            return;
        }

        AbstractContainerMenu menu = player.containerMenu;
        if (menu == null) {
            return;
        }

        CompressType compressType = message.type;
        Slot mouseSlot = menu.slots.get(message.slotNumber);
        if (!(mouseSlot.container instanceof Inventory)) {
            return;
        }

        ItemStack mouseStack = mouseSlot.getItem();
        if (mouseStack.isEmpty()) {
            return;
        }

        CraftingGrid grid = CraftingTweaksProviderManager.getDefaultCraftingGrid(menu).orElse(null);
        if (!CraftingTweaksConfig.getActive().common.compressAnywhere && grid == null) {
            return;
        }

        if (compressType == CompressType.DECOMPRESS_ALL || compressType == CompressType.DECOMPRESS_STACK || compressType == CompressType.DECOMPRESS_ONE) {
            boolean decompressAll = compressType != CompressType.DECOMPRESS_ONE;
            // Perform decompression on all valid slots
            for (Slot slot : menu.slots) {
                if (compressType != CompressType.DECOMPRESS_ALL && slot != mouseSlot) {
                    continue;
                }

                ItemStack slotStack = slot.getItem();
                if (slot.container instanceof Inventory && slot.hasItem() && ItemStack.isSame(slotStack, mouseSlot.getItem()) && ItemStack.tagMatches(slotStack, mouseSlot.getItem())) {
                    ItemStack result = findMatchingResult(new InventoryCraftingDecompress(menu, slotStack), player);
                    if (!result.isEmpty() && !isBlacklisted(result) && !slotStack.isEmpty() && slotStack.getCount() >= 1) {
                        do {
                            if (player.getInventory().add(result.copy())) {
                                giveLeftoverItems(player, slotStack, 1);
                                slot.remove(1);
                            } else {
                                break;
                            }
                        } while (decompressAll && slot.hasItem() && slotStack.getCount() >= 1 && slotStack.getItem() != result.getItem());
                    }
                }
            }
        } else {
            boolean compressAll = compressType != CompressType.COMPRESS_ONE;
            int size = grid != null ? grid.getGridSize(player, menu) : 9;
            // Perform decompression on all valid slots
            for (Slot slot : menu.slots) {
                if (compressType != CompressType.COMPRESS_ALL && slot != mouseSlot) {
                    continue;
                }

                final ItemStack slotStack = slot.getItem();
                if (slot.container instanceof Inventory && slot.hasItem() && ItemStack.isSame(slotStack, mouseSlot.getItem()) && ItemStack.tagMatches(slotStack, mouseSlot.getItem())) {
                    if (size == 9 && !slotStack.isEmpty() && slotStack.getCount() >= 9) {
                        ItemStack result = findMatchingResult(new InventoryCraftingCompress(menu, 3, slotStack), player);
                        if (!result.isEmpty() && !isBlacklisted(result)) {
                            do {
                                if (player.getInventory().add(result.copy())) {
                                    giveLeftoverItems(player, slotStack, 9);
                                    slot.remove(9);
                                } else {
                                    break;
                                }
                            }
                            while (compressAll && slot.hasItem() && slotStack.getCount() >= 9 && slotStack.getItem() != result.getItem());
                        } else {
                            result = findMatchingResult(new InventoryCraftingCompress(menu, 2, slotStack), player);
                            if (!result.isEmpty() && !isBlacklisted(result)) {
                                do {
                                    if (player.getInventory().add(result.copy())) {
                                        giveLeftoverItems(player, slotStack, 4);
                                        slot.remove(4);
                                    } else {
                                        break;
                                    }
                                }
                                while (compressAll && slot.hasItem() && slotStack.getCount() >= 4 && slotStack.getItem() != result.getItem());
                            }
                        }
                    } else if (size >= 4 && !slotStack.isEmpty() && slotStack.getCount() >= 4) {
                        ItemStack result = findMatchingResult(new InventoryCraftingCompress(menu, 2, slotStack), player);
                        if (!result.isEmpty() && !isBlacklisted(result)) {
                            do {
                                if (player.getInventory().add(result.copy())) {
                                    giveLeftoverItems(player, slotStack, 4);
                                    slot.remove(4);
                                } else {
                                    break;
                                }
                            }
                            while (compressAll && slot.hasItem() && slotStack.getCount() >= 4 && slotStack.getItem() != result.getItem());
                        }
                    }
                }
            }
        }
        menu.broadcastChanges();
    }

    private static void giveLeftoverItems(ServerPlayer player, ItemStack slotStack, int count) {
        for (int i = 0; i < count; i++) {
            // Must be inside loop as it's being shrunk in addItemStackToInventory
            final ItemStack containerItem = Balm.getHooks().getCraftingRemainingItem(slotStack);
            if (!player.addItem(containerItem)) {
                ItemEntity itemEntity = player.drop(containerItem, false);
                if (itemEntity != null) {
                    itemEntity.setNoPickUpDelay();
                    itemEntity.setOwner(player.getUUID());
                }
            }
        }
    }

    private static <T extends CraftingContainer & RecipeHolder> ItemStack findMatchingResult(T craftingInventory, ServerPlayer player) {
        RecipeManager recipeManager = Objects.requireNonNull(player.getServer()).getRecipeManager();
        CraftingRecipe recipe = recipeManager.getRecipeFor(RecipeType.CRAFTING, craftingInventory, player.level).orElse(null);
        if (recipe != null && craftingInventory.setRecipeUsed(player.level, player, recipe)) {
            return recipe.assemble(craftingInventory);
        }

        return ItemStack.EMPTY;
    }

    private static boolean isBlacklisted(ItemStack result) {
        ResourceLocation registryName = BuiltInRegistries.ITEM.getKey(result.getItem());
        return CraftingTweaksConfig.getActive().common.compressBlacklist.contains(registryName.toString());
    }
}
