package theking530.staticpower.blockentities.machines.hydroponicpod;

import java.util.Optional;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.StemGrownBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.EmptyModelData;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.progressbars.SquareProgressBar;
import theking530.staticcore.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticcore.utilities.SDColor;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;

public class GuiHydroponicPod extends StaticPowerTileEntityGui<ContainierHydroponicPod, BlockEntityHydroponicPod> {

	public GuiHydroponicPod(ContainierHydroponicPod container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 178);
	}

	@Override
	public void initializeGui() {
		getTabManager().registerTab(new GuiSideConfigTab(getTileEntity()));
		registerWidget(new SquareProgressBar(79, 50, 18, 2).bindToMachineProcessingComponent(getTileEntity().processingComponent).setZLevel(100));
		setOutputSlotSize(16);
	}

	@Override
	protected void drawBackgroundExtras(PoseStack stack, float partialTicks, int mouseX, int mouseY) {
		int bgWidth = 45;
		int bgHeight = 60;
		GuiDrawUtilities.drawSlotWithBorder(stack, bgWidth, bgHeight, ((getXSize() - bgWidth) / 2), 20, 0, MachineSideMode.Input.getColor());
		GuiDrawUtilities.drawRectangle(stack, bgWidth, bgHeight, ((getXSize() - bgWidth) / 2), 20, 0, SDColor.BLACK);
		GuiDrawUtilities.drawSlotWithBorder(stack, 16, 16, 80, 30, 100, MachineSideMode.Input.getColor());
	}

	@Override
	protected void drawBehindItems(PoseStack stack, float partialTicks, int mouseX, int mouseY) {
		long gameTime = getTileEntity().getLevel().getGameTime();
		Optional<Block> block = getTileEntity().getPlantBlockForDisplay();
		if (block.isEmpty()) {
			return;
		}

		if (block.get() instanceof CropBlock) {
			CropBlock crop = (CropBlock) block.get();
			int age = ((int) (getTileEntity().getGrowthPercentage() * crop.getMaxAge())) % crop.getMaxAge();
			BlockState state = crop.getStateForAge(age);
			GuiDrawUtilities.drawBlockState(stack, state, BlockPos.ZERO, EmptyModelData.INSTANCE, new Vector3D(72, 40, 1), new Vector3D(15, gameTime + partialTicks, 180),
					new Vector3D(32, 32, -1));
		} else if (block.get() instanceof StemBlock) {
			float scale = getTileEntity().getGrowthPercentage() * 13.0f + 13.0f;

			StemBlock stem = (StemBlock) block.get();
			StemGrownBlock fruit = stem.getFruit();
			BlockState state = fruit.defaultBlockState();
			GuiDrawUtilities.drawBlockState(stack, state, BlockPos.ZERO, EmptyModelData.INSTANCE, new Vector3D(88 - (scale / 2), 61 - (scale / 2), 1),
					new Vector3D(15, gameTime + partialTicks, 180), new Vector3D(scale, scale, -1));
		}
	}
}
