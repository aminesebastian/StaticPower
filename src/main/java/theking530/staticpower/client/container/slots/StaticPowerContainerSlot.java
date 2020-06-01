package theking530.staticpower.client.container.slots;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import theking530.staticpower.tileentities.utilities.MachineSideMode;

public class StaticPowerContainerSlot extends SlotItemHandler {

	private ItemStack previewItem;
	private float previewAlpha;
	private MachineSideMode mode;

	public StaticPowerContainerSlot(@Nonnull ItemStack previewItem, float previewAlpha, @Nonnull IItemHandler itemHandler, int index, int xPosition, int yPosition, @Nonnull MachineSideMode mode) {
		super(itemHandler, index, xPosition, yPosition);
		this.previewItem = previewItem;
		this.previewAlpha = previewAlpha;
		this.mode = mode;
	}

	public StaticPowerContainerSlot(@Nonnull ItemStack previewItem, float previewAlpha, @Nonnull IItemHandler itemHandler, int index, int xPosition, int yPosition) {
		this(previewItem, previewAlpha, itemHandler, index, xPosition, yPosition, MachineSideMode.Never);
	}

	public StaticPowerContainerSlot(@Nonnull IItemHandler itemHandler, int index, int xPosition, int yPosition, @Nonnull MachineSideMode mode) {
		this(ItemStack.EMPTY, 0.0f, itemHandler, index, xPosition, yPosition, mode);
	}

	public StaticPowerContainerSlot(@Nonnull IItemHandler itemHandler, int index, int xPosition, int yPosition) {
		this(ItemStack.EMPTY, 0.0f, itemHandler, index, xPosition, yPosition, MachineSideMode.Never);
	}

	public ItemStack getPreviewItem() {
		return previewItem;
	}

	public float getPreviewAlpha() {
		return previewAlpha;
	}

	public void setPreviewItem(ItemStack newItem) {
		previewItem = newItem;
	}

	public void settPreviewAlpha(float newAlpha) {
		previewAlpha = newAlpha;
	}

	public MachineSideMode getMode() {
		return mode;
	}
}
