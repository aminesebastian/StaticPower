package theking530.staticcore.gui.widgets.tabs;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.gui.drawables.ItemDrawable;
import theking530.staticcore.utilities.Color;
import theking530.staticpower.client.gui.GuiTextures;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.tileentities.components.fluids.FluidTankComponent;

@OnlyIn(Dist.CLIENT)
public class GuiMachineFluidTab extends AbstractInfoTab {
	protected FluidTankComponent fluidTank;

	public GuiMachineFluidTab(FluidTankComponent tank) {
		super("Fluid I/O", new Color(255, 255, 25), 80, GuiTextures.AQUA_TAB, new ItemDrawable(Blocks.CAULDRON));
		fluidTank = tank;
	}

	@Override
	public void updateData() {
		super.updateData();
		clear();
		addKeyValueTwoLiner("Filled", new TextComponent("Filled"), GuiTextUtilities.formatFluidRateToString(fluidTank.getStorage().getFilledPerTick()), ChatFormatting.AQUA);
		addKeyValueTwoLiner("Drained", new TextComponent("Drained"), GuiTextUtilities.formatFluidRateToString(fluidTank.getStorage().getDrainedPerTick()), ChatFormatting.GRAY);
	}
}
