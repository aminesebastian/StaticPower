package theking530.staticcore.gui.widgets.tabs;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.gui.drawables.ItemDrawable;
import theking530.staticcore.utilities.SDColor;
import theking530.staticpower.blockentities.components.fluids.FluidTankComponent;
import theking530.staticpower.client.utilities.GuiTextUtilities;

@OnlyIn(Dist.CLIENT)
public class GuiMachineFluidTab extends AbstractInfoTab {
	protected FluidTankComponent fluidTank;

	public GuiMachineFluidTab(FluidTankComponent tank) {
		super("Fluid I/O", new SDColor(255, 255, 25), 85, new SDColor(0, 0.8f, 0.9f, 1), new ItemDrawable(Blocks.CAULDRON));
		fluidTank = tank;
	}

	@Override
	public void tick() {
		super.tick();
		clear();
		addKeyValueTwoLiner("Filled", Component.literal("Filled"), GuiTextUtilities.formatFluidRateToString(fluidTank.getStorage().getFilledPerTick()), ChatFormatting.AQUA);
		addKeyValueTwoLiner("Drained", Component.literal("Drained"), GuiTextUtilities.formatFluidRateToString(fluidTank.getStorage().getDrainedPerTick()), ChatFormatting.GRAY);
	}
}
