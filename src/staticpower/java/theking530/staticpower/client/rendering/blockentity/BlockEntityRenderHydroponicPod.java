package theking530.staticpower.client.rendering.blockentity;

import java.util.Optional;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.StemGrownBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.utilities.SDColor;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.blockentities.machines.hydroponics.pod.BlockEntityHydroponicPod;
import theking530.staticpower.client.rendering.BlockModel;

@OnlyIn(Dist.CLIENT)
public class BlockEntityRenderHydroponicPod extends StaticPowerBlockEntitySpecialRenderer<BlockEntityHydroponicPod> {
	public BlockEntityRenderHydroponicPod(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void renderTileEntityBase(BlockEntityHydroponicPod tileEntity, BlockPos pos, float partialTicks, PoseStack stack, MultiBufferSource buffer, int combinedLight,
			int combinedOverlay) {
		Minecraft.getInstance().getProfiler().push("StaticPowerBlockEntityRenderer.HydroponicPod");
		Optional<Block> block = tileEntity.getPlantBlockForDisplay();

		if (!block.isEmpty()) {
			Minecraft.getInstance().getProfiler().push("StaticPowerBlockEntityRenderer.HydroponicPod.Crop");
			float growthPercentage = tileEntity.getGrowthPercentage();
			if (block.get() instanceof CropBlock) {
				CropBlock crop = (CropBlock) block.get();
				int age = ((int) (growthPercentage * crop.getMaxAge())) % crop.getMaxAge();
				BlockState state = crop.getStateForAge(age);
				GuiDrawUtilities.drawBlockState(stack, state, pos, ModelData.EMPTY, new Vector3D(0.175f, 0.1f, 0.175f), new Vector3D(0, 45, 0), new Vector3D(0.65f, 0.65f, 0.65f));
			} else if (block.get() instanceof StemBlock) {
				float scale = (growthPercentage * 0.9f) + 0.1f;
				scale *= 0.5f;

				// Draw the stem.
				StemBlock stem = (StemBlock) block.get();
				int age = (int) (growthPercentage * StemBlock.MAX_AGE);
				BlockState stemState = stem.defaultBlockState();
				stemState = stemState.setValue(StemBlock.AGE, age);
				GuiDrawUtilities.drawBlockState(stack, stemState, pos, ModelData.EMPTY, new Vector3D(0.75f - (scale / 2), 0.1f, 0.75f - (scale / 2)), new Vector3D(0, 0, 0),
						new Vector3D(scale, scale, scale));

				// Draw the fruit.
				StemGrownBlock fruit = stem.getFruit();
				BlockState state = fruit.defaultBlockState();
				GuiDrawUtilities.drawBlockState(stack, state, pos, ModelData.EMPTY, new Vector3D(0.75f - scale, 0.1f, 0.75f - scale), new Vector3D(0, 45, 0),
						new Vector3D(scale, scale, scale));
			}
			Minecraft.getInstance().getProfiler().pop();
		}

		Minecraft.getInstance().getProfiler().push("StaticPowerBlockEntityRenderer.HydroponicPod.Fluid");
		if (tileEntity.hasWater()) {
			// Render the hydroponic fluid.
			FluidStack fluid = new FluidStack(Fluids.WATER, 1);
			TextureAtlasSprite sprite = GuiDrawUtilities.getStillFluidSprite(fluid);
			SDColor fluidColor = GuiDrawUtilities.getFluidColor(fluid);
			float height = 0.2f;
			BlockModel.drawCubeInWorld(stack, new Vector3f(2.1f * TEXEL, 2f * TEXEL, 2.1f * TEXEL), new Vector3f(11.8f * TEXEL, 12f * TEXEL * height, 11.8f * TEXEL), fluidColor,
					sprite, new Vector3D(1.0f, height, 1.0f));
		}
		Minecraft.getInstance().getProfiler().pop();
		Minecraft.getInstance().getProfiler().pop();
	}
}
