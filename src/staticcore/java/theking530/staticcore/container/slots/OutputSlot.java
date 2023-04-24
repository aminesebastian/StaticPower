package theking530.staticcore.container.slots;

import javax.annotation.Nonnull;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import theking530.staticcore.blockentity.components.control.processing.recipe.RecipeProcessingComponent;

public class OutputSlot extends StaticPowerContainerSlot {
	private RecipeProcessingComponent<?> processingComponent;
	private boolean shouldApplyExperience;

	public OutputSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
		super(itemHandler, index, xPosition, yPosition);
	}

	public OutputSlot(IItemHandler itemHandler, Item item, int index, int xPosition, int yPosition) {
		super(new ItemStack(item), 0.3f, itemHandler, index, xPosition, yPosition);
	}

	public OutputSlot(IItemHandler itemHandler, ItemStack item, int index, int xPosition, int yPosition) {
		super(item, 0.3f, itemHandler, index, xPosition, yPosition);
	}

	@Override
	public boolean mayPlace(@Nonnull ItemStack stack) {
		return false;
	}

	public OutputSlot shouldApplyExperience(RecipeProcessingComponent<?> processingComponent) {
		this.processingComponent = processingComponent;
		this.shouldApplyExperience = true;
		return this;
	}

	@Override
	public void onTake(Player player, ItemStack stack) {
		super.onTake(player, stack);
		if (shouldApplyExperience) {
			processingComponent.applyExperience(player);
		}
	}
}
