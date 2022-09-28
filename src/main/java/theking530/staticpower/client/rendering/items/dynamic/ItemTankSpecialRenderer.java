package theking530.staticpower.client.rendering.items.dynamic;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.utilities.SDColor;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.client.rendering.BlockModel;

@OnlyIn(Dist.CLIENT)
public class ItemTankSpecialRenderer extends AbstractStaticPowerItemStackRenderer {
	public ItemTankSpecialRenderer(BlockEntityRenderDispatcher p_172550_, EntityModelSet p_172551_) {
		super(p_172550_, p_172551_);
	}

	@Override
	public void render(ItemStack stack, BakedModel defaultModel, ItemTransforms.TransformType transformType, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight,
			int combinedOverlay) {
		// Check to see if the stack has the serialized nbt.
		if (stack.hasTag() && stack.getTag().contains("SerializableNbt")) {
			// If it does, get the tank tag and then the subsequent fluid tag.
			CompoundTag tankTag = stack.getTag().getCompound("SerializableNbt").getCompound("FluidTank");
			CompoundTag fluidTank = tankTag.getCompound("fluidStorage");

			// Get the fluid.
			FluidStack fluid = FluidStack.loadFluidStackFromNBT(fluidTank.getCompound("tank"));

			// Get the fluid attributes.
			TextureAtlasSprite sprite = GuiDrawUtilities.getStillFluidSprite(fluid);
			SDColor fluidColor = GuiDrawUtilities.getFluidColor(fluid);

			// Calculate the height and determine if we should raise the fluid to the top
			// if it is a gas.
			float height = (float) fluid.getAmount() / fluidTank.getInt("capacity");
			boolean isGas = fluid.getFluid().getAttributes().isGaseous();
			float yPosition = isGas ? 14.0f * TEXEL - (11.98f * TEXEL * height) : 2.01f * TEXEL;

			// Render the fluid.
			BlockModel.drawCubeInWorld(matrixStack, new Vector3f(TEXEL * 2.01f, yPosition, TEXEL * 2.01f), new Vector3f(TEXEL * 11.98f, TEXEL * 11.98f * height, TEXEL * 11.98f),
					fluidColor, sprite, new Vector3D(1.0f, height, 1.0f));
		}
	}
}
