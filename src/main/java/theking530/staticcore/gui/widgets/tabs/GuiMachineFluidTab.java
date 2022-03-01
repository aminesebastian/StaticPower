package theking530.staticcore.gui.widgets.tabs;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.gui.drawables.ItemDrawable;
import theking530.staticcore.utilities.Color;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.tileentities.components.fluids.FluidTankComponent;

@OnlyIn(Dist.CLIENT)
public class GuiMachineFluidTab extends AbstractInfoTab {
	protected FluidTankComponent fluidTank;

	public GuiMachineFluidTab(FluidTankComponent tank) {
		super("Fluid I/O", new Color(255, 255, 25), 80, new Color(0, 0.8f, 0.9f, 1), new ItemDrawable(Blocks.CAULDRON));
		fluidTank = tank;
	}

	@Override
	public void tick() {
		super.tick();
		clear();
		addKeyValueTwoLiner("Filled", new TextComponent("Filled"), GuiTextUtilities.formatFluidRateToString(fluidTank.getStorage().getFilledPerTick()), ChatFormatting.AQUA);
		addKeyValueTwoLiner("Drained", new TextComponent("Drained"), GuiTextUtilities.formatFluidRateToString(fluidTank.getStorage().getDrainedPerTick()), ChatFormatting.GRAY);
	}
}
