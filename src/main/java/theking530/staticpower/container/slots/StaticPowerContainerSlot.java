package theking530.staticpower.container.slots;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.valuebars.GuiFluidBarUtilities;
import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.blockentities.components.items.InventoryComponent;

public class StaticPowerContainerSlot extends SlotItemHandler {
	private @Nullable InventoryComponent inventoryComponent;
	private ItemStack previewItem;
	private float previewAlpha;
	private MachineSideMode mode;
	private boolean drawFluidContainerAsFluid;
	private boolean isEnabled;

	public StaticPowerContainerSlot(@Nonnull ItemStack previewItem, float previewAlpha, @Nonnull IItemHandler itemHandler, int index, int xPosition, int yPosition, @Nonnull MachineSideMode mode) {
		super(itemHandler, index, xPosition, yPosition);
		this.previewItem = previewItem;
		this.previewAlpha = previewAlpha;
		this.mode = mode;
		this.isEnabled = true;

		// If this item handler is an inventory component, cache that too.
		if (itemHandler instanceof InventoryComponent) {
			this.inventoryComponent = (InventoryComponent) itemHandler;
		}
	}

	public StaticPowerContainerSlot(@Nonnull ItemStack previewItem, float previewAlpha, @Nonnull IItemHandler itemHandler, int index, int xPosition, int yPosition) {
		this(previewItem, previewAlpha, itemHandler, index, xPosition, yPosition, MachineSideMode.Never);
	}

	public StaticPowerContainerSlot(@Nonnull ItemStack previewItem, @Nonnull IItemHandler itemHandler, int index, int xPosition, int yPosition) {
		this(previewItem, 0.25f, itemHandler, index, xPosition, yPosition, MachineSideMode.Never);
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

	public StaticPowerContainerSlot setPreviewItem(ItemStack newItem) {
		previewItem = newItem;
		return this;
	}

	public StaticPowerContainerSlot setPreviewAlpha(float newAlpha) {
		previewAlpha = newAlpha;
		return this;
	}

	public StaticPowerContainerSlot renderFluidContainerAsFluid() {
		drawFluidContainerAsFluid = true;
		return this;
	}

	public MachineSideMode getMode() {
		return inventoryComponent != null ? inventoryComponent.getMode() : mode;
	}

	public void drawSlotOverlay(int guiLeft, int guiTop, int slotSize, int slotPosOffset) {

	}

	public StaticPowerContainerSlot setEnabledState(boolean enabled) {
		this.isEnabled = enabled;
		return this;
	}

	@OnlyIn(Dist.CLIENT)
	public boolean isActive() {
		return isEnabled;
	}

	public void drawBeforeItem(PoseStack matrixStack, int guiLeft, int guiTop, int slotSize, int slotPosOffset) {
		if (!getPreviewItem().isEmpty()) {
			GuiDrawUtilities.drawItem(matrixStack, getPreviewItem(), x, y, 0.0f, getPreviewAlpha());
		}
		if (drawFluidContainerAsFluid) {
			IFluidHandlerItem fluidItem = getItem().getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).orElse(null);
			if (fluidItem != null) {
				if (fluidItem.getTanks() > 0) {
					RenderSystem.enableDepthTest();
					FluidStack fluid = fluidItem.getFluidInTank(0);
					GuiFluidBarUtilities.drawFluidBar(matrixStack, fluid, 1, 1, guiLeft + x, guiTop + y + 16.0f, 500.0f, 16.0f, 16.0f, false);
					RenderSystem.disableDepthTest();
				}
			}
		}
	}
}
