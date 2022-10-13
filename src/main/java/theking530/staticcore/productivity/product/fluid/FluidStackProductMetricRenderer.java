package theking530.staticcore.productivity.product.fluid;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.gui.widgets.valuebars.GuiFluidBarUtilities;
import theking530.staticcore.productivity.ProductMetricTileRenderer;
import theking530.staticcore.productivity.product.ProductType;
import theking530.staticcore.utilities.Vector2D;

public class FluidStackProductMetricRenderer extends ProductMetricTileRenderer<FluidStack, ProductType<FluidStack>> {

	@Override
	protected void drawIcon(FluidStack product, PoseStack pose, Vector2D mousePosition, float partialTicks, Vector2D tileSize, boolean isHovered) {
		GuiFluidBarUtilities.drawFluidBar(pose, product, 2, 1, 3, 17, 10, 15, 15, false);
	}

	@Override
	protected FluidStack deserializeProduct(String serializedProduct) {
		try {
			CompoundTag tag = TagParser.parseTag(serializedProduct);
			tag.putInt("Amount", (byte) 1);
			return FluidStack.loadFluidStackFromNBT(tag);
		} catch (CommandSyntaxException e) {
			return FluidStack.EMPTY;
		}
	}
}
