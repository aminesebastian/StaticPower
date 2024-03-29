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
import theking530.staticcore.blockentity.components.ComponentUtilities;
import theking530.staticcore.gui.text.PowerTextFormatting;
import theking530.staticcore.utilities.SDColor;
import theking530.staticpower.StaticPower;
import theking530.staticpower.cables.power.PowerCableComponent;
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
			boolean isPowerCable = ComponentUtilities.getComponent(PowerCableComponent.class, be).isPresent();

			if (powerStorage.canOutputExternalPower()) {
				String key = isPowerCable ? "gui.staticcore.current_voltage" : "gui.staticcore.output_voltage";
				probeInfo.text(Component.literal(ChatFormatting.GRAY.toString())
						.append(Component.translatable(key).append(": ").append(PowerTextFormatting.formatVoltageToString(powerStorage.getOutputVoltage())))
						.append(powerStorage.getOutputCurrentType() == CurrentType.ALTERNATING ? Component.literal("∿") : Component.literal("⎓")));
			}

			if (powerStorage.canAcceptExternalPower() && !isPowerCable) {
				probeInfo.text(Component.literal(ChatFormatting.GRAY.toString()).append(Component.translatable("gui.staticcore.input_voltage").append(": ")
						.append(PowerTextFormatting.formatVoltageRangeToString(powerStorage.getInputVoltageRange()))));
			}

			double current = powerStorage.getStoredPower();
			double max = powerStorage.getCapacity();

			if (max > 0) {
				MutableComponent suffix = PowerTextFormatting.formatPowerToString(current, true, true);
				suffix.append("/");
				suffix.append(PowerTextFormatting.formatPowerToString(max, true, true));

				probeInfo.progress((int) current, (int) max,
						probeInfo.defaultProgressStyle().filledColor(JadePluginImplementation.MAIN_SV_COLOR.encodeInInteger())
								.alternateFilledColor(JadePluginImplementation.ALT_SV_COLOR.encodeInInteger()).numberFormat(NumberFormat.NONE)
								.borderColor(SDColor.EIGHT_BIT_GREY.encodeInInteger()).suffix(suffix));
			}
		});
	}

	@Override
	public void getProbeConfig(IProbeConfig config, Player player, Level world, Entity entity, IProbeHitEntityData data) {
	}

	@Override
	public void getProbeConfig(IProbeConfig config, Player player, Level world, BlockState blockState, IProbeHitData data) {

	}
}