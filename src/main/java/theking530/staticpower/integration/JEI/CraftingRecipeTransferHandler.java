package theking530.staticpower.integration.JEI;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IGuiIngredient;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import theking530.staticpower.network.StaticPowerMessageHandler;

public class CraftingRecipeTransferHandler<T extends Container> implements IRecipeTransferHandler<T> {

	private final Class<T> containerClass;
	private final int inputItems;

	CraftingRecipeTransferHandler(Class<T> containerClass, int inputItems) {
		this.containerClass = containerClass;
		this.inputItems = inputItems;
	}

	@Override
	public Class<T> getContainerClass() {
		return this.containerClass;
	}

	@Nullable
	@Override
	public IRecipeTransferError transferRecipe(T container, IRecipeLayout recipeLayout, PlayerEntity player, boolean maxTransfer, boolean doTransfer) {
		if (!doTransfer) {
			return null;
		}

		Map<Integer, ? extends IGuiIngredient<ItemStack>> ingredients = recipeLayout.getItemStacks().getGuiIngredients();

		final CompoundNBT recipe = new CompoundNBT();

		int slotIndex = 0;
		for (Map.Entry<Integer, ? extends IGuiIngredient<ItemStack>> ingredientEntry : ingredients.entrySet()) {
			IGuiIngredient<ItemStack> ingredient = ingredientEntry.getValue();
			if (!ingredient.isInput()) {
				continue;
			}

			for (final Slot slot : container.inventorySlots) {
				if (slot.getSlotIndex() == slotIndex) {
					final ListNBT tags = new ListNBT();
					final List<ItemStack> list = new ArrayList<>();
					final ItemStack displayed = ingredient.getDisplayedIngredient();

					// prefer currently displayed item
					if (displayed != null && !displayed.isEmpty()) {
						list.add(displayed);
					}

					// Add the additional items.
					for (ItemStack stack : ingredient.getAllIngredients()) {
						list.add(stack);
					}

					for (final ItemStack is : list) {
						final CompoundNBT tag = new CompoundNBT();
						is.write(tag);
						tags.add(tag);
					}

					recipe.put("#" + slot.getSlotIndex(), tags);
					break;
				}
			}

			slotIndex++;
		}

		// Send the packet to the server to update the crafting grid.
		StaticPowerMessageHandler.MAIN_PACKET_CHANNEL.sendToServer(new JEIRecipeTransferPacket(container.windowId, inputItems, recipe));

		return null;
	}
}
