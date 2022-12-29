package theking530.staticpower.integration.TOP;

import mcjty.theoneprobe.api.IProbeConfig;
import mcjty.theoneprobe.api.IProbeConfigProvider;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeHitEntityData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.NumberFormat;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import theking530.api.energy.CapabilityStaticPower;
import theking530.api.energy.CurrentType;
import theking530.staticcore.gui.text.PowerTextFormatting;
import theking530.staticcore.utilities.SDColor;
import theking530.staticpower.StaticPower;
import theking530.staticpower.integration.JADE.JadePluginImplementation;

public class StaticEnergyInfoProvider implements IProbeInfoProvider, IProbeConfigProvider {
	private static final ResourceLocation ID = new ResourceLocation(StaticPower.MOD_ID, "static_energy_info");

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

		be.getCapability(CapabilityStaticPower.STATIC_VOLT_CAPABILITY, data.getSideHit()).ifPresent(powerStorage -> {
			if (powerStorage.canOutputExternalPower()) {
				probeInfo.text(Component.literal(ChatFormatting.GRAY.toString()).append(
						Component.translatable("gui.staticpower.output_voltage").append(": ").append(PowerTextFormatting.formatVoltageToString(powerStorage.getOutputVoltage()))));
			}

			if (powerStorage.canAcceptExternalPower()) {
				probeInfo.text(Component.literal(ChatFormatting.GRAY.toString()).append(Component.translatable("gui.staticpower.input_voltage").append(": ")
						.append(PowerTextFormatting.formatVoltageRangeToString(powerStorage.getInputVoltageRange()))));
			}

			if (powerStorage.getOutputCurrentType() == CurrentType.ALTERNATING) {
				probeInfo.text(Component.literal(ChatFormatting.GRAY.toString()).append(Component.translatable("~")));
			}

			double current = powerStorage.getStoredPower();
			double max = powerStorage.getCapacity();

			MutableComponent suffix = PowerTextFormatting.formatPowerToString(current, true, true);
			suffix.append("/");
			suffix.append(PowerTextFormatting.formatPowerToString(max, true, true));

			probeInfo.progress((int) current, (int) max,
					probeInfo.defaultProgressStyle().filledColor(JadePluginImplementation.MAIN_SV_COLOR.encodeInInteger())
							.alternateFilledColor(JadePluginImplementation.ALT_SV_COLOR.encodeInInteger()).numberFormat(NumberFormat.NONE)
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