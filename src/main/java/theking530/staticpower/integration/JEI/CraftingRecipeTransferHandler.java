package theking530.staticpower.integration.JEI;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IGuiIngredient;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import theking530.staticpower.network.StaticPowerMessageHandler;

public class CraftingRecipeTransferHandler<T extends AbstractContainerMenu, R> implements IRecipeTransferHandler<T, R> {

	private final Class<T> containerClass;
	private final Class<R> recipeClass;
	private final int inputItems;

	CraftingRecipeTransferHandler(Class<T> containerClass, Class<R> recipeClass, int inputItems) {
		this.containerClass = containerClass;
		this.recipeClass = recipeClass;
		this.inputItems = inputItems;
	}

	@Override
	public Class<T> getContainerClass() {
		return this.containerClass;
	}

	@Override
	public Class<R> getRecipeClass() {
		return this.recipeClass;
	}

	@Nullable
	@Override
	public IRecipeTransferError transferRecipe(T container, R recipeInstance, IRecipeLayout recipeLayout, Player player,
			boolean maxTransfer, boolean doTransfer) {
		if (!doTransfer) {
			return null;
		}

		Map<Integer, ? extends IGuiIngredient<ItemStack>> ingredients = recipeLayout.getItemStacks()
				.getGuiIngredients();

		final CompoundTag recipe = new CompoundTag();

		int slotIndex = 0;
		for (Map.Entry<Integer, ? extends IGuiIngredient<ItemStack>> ingredientEntry : ingredients.entrySet()) {
			IGuiIngredient<ItemStack> ingredient = ingredientEntry.getValue();
			if (!ingredient.isInput()) {
				continue;
			}

			for (final Slot slot : container.slots) {
				if (slot.getSlotIndex() == slotIndex) {
					final ListTag tags = new ListTag();
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
		StaticPowerMessageHandler.MAIN_PACKET_CHANNEL
				.sendToServer(new JEIRecipeTransferPacket(container.containerId, inputItems, recipe));

		return null;
	}
}
