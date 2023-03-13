package theking530.staticpower.integration.JEI;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import theking530.staticpower.network.StaticPowerMessageHandler;

public class CraftingRecipeTransferHandler<T extends AbstractContainerMenu, R> implements IRecipeTransferHandler<T, R> {

	private final Class<T> containerClass;
	private final RecipeType<R> recipeClass;
	private final int inputItems;

	CraftingRecipeTransferHandler(Class<T> containerClass, RecipeType<R> recipeClass, int inputItems) {
		this.containerClass = containerClass;
		this.recipeClass = recipeClass;
		this.inputItems = inputItems;
	}

	@Override
	public Class<T> getContainerClass() {
		return this.containerClass;
	}

	@Override
	public RecipeType<R> getRecipeType() {
		return this.recipeClass;
	}

	@Override
	public Optional<MenuType<T>> getMenuType() {
		return Optional.empty();
	}

	@Nullable
	@Override
	public IRecipeTransferError transferRecipe(T container, R recipeInstance, IRecipeSlotsView recipeSlots, Player player, boolean maxTransfer, boolean doTransfer) {
		if (!doTransfer) {
			return null;
		}

		final CompoundTag recipe = new CompoundTag();
		int slotIndex = 0;
		for (IRecipeSlotView ingredientEntry : recipeSlots.getSlotViews()) {

			if (ingredientEntry.getRole() != RecipeIngredientRole.INPUT) {
				continue;
			}

			for (final Slot slot : container.slots) {
				if (slot.getSlotIndex() == slotIndex) {
					ListTag tags = new ListTag();
					List<ItemStack> list = new ArrayList<>();
					ItemStack displayed = null;
					if (ingredientEntry.getDisplayedIngredient().isPresent()) {
						if (ingredientEntry.getDisplayedIngredient().get().getType() == VanillaTypes.ITEM_STACK) {
							displayed = ingredientEntry.getDisplayedIngredient().get().getItemStack().get();
						}
					}

					// prefer currently displayed item
					if (displayed != null && !displayed.isEmpty()) {
						list.add(displayed);
					}

					// Add the additional items.
					for (ItemStack stack : ingredientEntry.getItemStacks().toList()) {
						list.add(stack);
					}

					for (final ItemStack is : list) {
						final CompoundTag tag = new CompoundTag();
						is.save(tag);
						tags.add(tag);
					}

					recipe.put("#" + slot.getSlotIndex(), tags);
					break;
				}
			}

			slotIndex++;
		}

		// Send the packet to the server to update the crafting grid.
		StaticPowerMessageHandler.MAIN_PACKET_CHANNEL.sendToServer(new JEIRecipeTransferPacket(container.containerId, inputItems, recipe));

		return null;
	}

}
