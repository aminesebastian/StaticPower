package theking530.staticcore.productivity.product.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.item.ItemStack;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.productivity.ProductMetricTileRenderer;
import theking530.staticcore.productivity.product.ProductType;
import theking530.staticcore.utilities.Vector2D;

public class ItemStackProductMetricRenderer extends ProductMetricTileRenderer<ItemStack, ProductType<ItemStack>> {

	@Override
	protected void drawIcon(ItemStack product, PoseStack pose, Vector2D mousePosition, float partialTicks, Vector2D tileSize, boolean isHovered) {
		GuiDrawUtilities.drawItem(pose, product, 2, 2, 10, 16, 16);
	}

	@Override
	protected ItemStack deserializeProduct(String serializedProduct) {
		try {
			CompoundTag tag = TagParser.parseTag(serializedProduct);
			tag.putByte("Count", (byte) 1);
			return ItemStack.of(tag);
		} catch (CommandSyntaxException e) {
			return ItemStack.EMPTY;
		}
	}
}
