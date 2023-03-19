package theking530.staticpower.integration.TOP;

import mcjty.theoneprobe.api.IProbeConfig;
import mcjty.theoneprobe.api.IProbeConfigProvider;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeHitEntityData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.NumberFormat;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import theking530.api.fluid.IStaticPowerFluidHandler;
import theking530.staticcore.gui.GuiTextUtilities;
import theking530.staticcore.utilities.SDColor;
import theking530.staticpower.StaticPower;
import theking530.staticpower.cables.fluid.BlockEntityFluidCable;
import theking530.staticpower.integration.JADE.JadePluginImplementation;

public class FluidCableProvider implements IProbeInfoProvider, IProbeConfigProvider {
	private static final ResourceLocation ID = new ResourceLocation(StaticPower.MOD_ID, "head_pressure_info");

	@Override
	public ResourceLocation getID() {
		return ID;
	}

	@Override
	public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, Player player, Level world, BlockState blockState, IProbeHitData data) {
		BlockEntity entity = world.getBlockEntity(data.getPos());
		if (!(entity instanceof BlockEntityFluidCable)) {
			return;
		}

		BlockEntityFluidCable fluidCable = (BlockEntityFluidCable) world.getBlockEntity(data.getPos());
		if (fluidCable == null) {
			return;
		}

		float pressure = fluidCable.fluidCableComponent.getHeadPressure() / 2;
		int max = IStaticPowerFluidHandler.MAX_PRESSURE / 2;

		MutableComponent label = GuiTextUtilities.formatNumberAsStringOneDecimal(pressure);
		label.append(" ");

		probeInfo.progress((int) Math.round(pressure * 10), max * 10,
				probeInfo.defaultProgressStyle().filledColor(JadePluginImplementation.MAIN_PRESSURE_COLOR.encodeInInteger())
						.alternateFilledColor(JadePluginImplementation.ALT_PRESSURE_COLOR.encodeInInteger()).numberFormat(NumberFormat.NONE)
						.borderColor(SDColor.EIGHT_BIT_GREY.encodeInInteger()).prefix(label).suffix(Component.translatable("gui.staticpower.head_pressure")));
	}

	@Override
	public void getProbeConfig(IProbeConfig config, Player player, Level world, Entity entity, IProbeHitEntityData data) {
	}

	@Override
	public void getProbeConfig(IProbeConfig config, Player player, Level world, BlockState blockState, IProbeHitData data) {

	}
}