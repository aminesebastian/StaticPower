package theking530.staticpower.integration.TOP;

import mcjty.theoneprobe.api.IProbeConfig;
import mcjty.theoneprobe.api.IProbeConfigProvider;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeHitEntityData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.NumberFormat;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import theking530.api.heat.CapabilityHeatable;
import theking530.staticcore.gui.text.GuiTextUtilities;
import theking530.staticcore.utilities.SDColor;
import theking530.staticpower.StaticPower;
import theking530.staticpower.integration.JADE.JadePluginImplementation;

public class HeatInfoProvider implements IProbeInfoProvider, IProbeConfigProvider {
	private static final ResourceLocation ID = new ResourceLocation(StaticPower.MOD_ID, "heat_info");

	@Override
	public ResourceLocation getID() {
		return ID;
	}

	@Override
	public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, Player player, Level world, BlockState blockState, IProbeHitData data) {
		BlockEntity be = world.getBlockEntity(data.getPos());
		if (be == null) {
			return;
		}

		be.getCapability(CapabilityHeatable.HEAT_STORAGE_CAPABILITY).ifPresent(heatStorage -> {
			float current = heatStorage.getCurrentTemperature();
			float max = heatStorage.getMaximumTemperature();

			MutableComponent suffix = GuiTextUtilities.formatHeatToString(current, false, true);
			suffix.append("/");
			suffix.append(GuiTextUtilities.formatHeatToString(max));

			probeInfo.progress((int) current, (int) max,
					probeInfo.defaultProgressStyle().filledColor(JadePluginImplementation.MAIN_HEAT_COLOR.encodeInInteger())
							.alternateFilledColor(JadePluginImplementation.ALT_HEAT_COLOR.encodeInInteger()).numberFormat(NumberFormat.NONE)
							.borderColor(SDColor.EIGHT_BIT_GREY.encodeInInteger()).suffix(suffix));
		});
	}

	@Override
	public void getProbeConfig(IProbeConfig config, Player player, Level world, Entity entity, IProbeHitEntityData data) {
	}

	@Override
	public void getProbeConfig(IProbeConfig config, Player player, Level world, BlockState blockState, IProbeHitData data) {

	}
}