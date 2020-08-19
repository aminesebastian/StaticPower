package theking530.common.gui.widgets.tabs;

import net.minecraft.block.Blocks;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import theking530.common.gui.GuiTextures;
import theking530.common.gui.drawables.ItemDrawable;
import theking530.common.utilities.Color;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.tileentities.components.fluids.FluidTankComponent;

public class GuiMachineFluidTab extends AbstractInfoTab {
	protected FluidTankComponent fluidTank;

	public GuiMachineFluidTab(FluidTankComponent tank) {
		super("Fluid I/O", 80, GuiTextures.AQUA_TAB, new ItemDrawable(Blocks.CAULDRON), new Color(255, 255, 25));
		fluidTank = tank;
	}

	@Override
	public void updateData() {
		super.updateData();
		clear();
		addKeyValueTwoLiner(new StringTextComponent("Filled"), GuiTextUtilities.formatFluidRateToString(fluidTank.getStorage().getFilledPerTick()), TextFormatting.AQUA);
		addKeyValueTwoLiner(new StringTextComponent("Drained"), GuiTextUtilities.formatFluidRateToString(fluidTank.getStorage().getDrainedPerTick()), TextFormatting.GRAY);
	}
}
