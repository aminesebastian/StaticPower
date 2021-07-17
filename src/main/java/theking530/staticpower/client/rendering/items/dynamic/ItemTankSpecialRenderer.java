package theking530.staticpower.client.rendering.items.dynamic;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.client.rendering.BlockModel;

@OnlyIn(Dist.CLIENT)
public class ItemTankSpecialRenderer extends AbstractStaticPowerItemStackRenderer {
	/** Default basic cube renderer. */
	protected static final BlockModel CUBE_MODEL = new BlockModel();

	@Override
	public void render(ItemStack stack, IBakedModel defaultModel, ItemCameraTransforms.TransformType transformType, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight,
			int combinedOverlay) {
		// Check to see if the stack has the serialized nbt.
		if (stack.hasTag() && stack.getTag().contains("SerializableNbt")) {
			// If it does, get the tank tag and then the subsequent fluid tag.
			CompoundNBT tankTag = stack.getTag().getCompound("SerializableNbt").getCompound("FluidTank");
			CompoundNBT fluidTank = tankTag.getCompound("fluidStorage");

			// Get the fluid.
			FluidStack fluid = FluidStack.loadFluidStackFromNBT(fluidTank.getCompound("tank"));

			// Get the fluid attributes.
			TextureAtlasSprite sprite = GuiDrawUtilities.getStillFluidSprite(fluid);
			Color fluidColor = GuiDrawUtilities.getFluidColor(fluid);

			// Calculate the height and determine if we should raise the fluid to the top
			// if it is a gas.
			float height = (float) fluid.getAmount() / fluidTank.getInt("capacity");
			boolean isGas = fluid.getFluid().getAttributes().isGaseous();
			float yPosition = isGas ? 14.0f * TEXEL - (11.98f * TEXEL * height) : 2.01f * TEXEL;

			// Render the fluid.
			CUBE_MODEL.drawPreviewCube(new Vector3f(TEXEL * 2.01f, yPosition, TEXEL * 2.01f), new Vector3f(TEXEL * 11.98f, TEXEL * 11.98f * height, TEXEL * 11.98f), fluidColor, matrixStack,
					sprite, new Vector3D(1.0f, height, 1.0f));
		}
	}
}
