package theking530.staticpower.integration.TOP;

import java.util.Optional;

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
import theking530.staticcore.blockentity.components.ComponentUtilities;
import theking530.staticcore.blockentity.components.control.processing.AbstractProcessingComponent;
import theking530.staticcore.blockentity.components.control.processing.Timer;
import theking530.staticcore.gui.text.GuiTextUtilities;
import theking530.staticcore.utilities.SDColor;
import theking530.staticpower.StaticPower;
import theking530.staticpower.integration.JADE.JadePluginImplementation;

public class ProgressInfoProvider implements IProbeInfoProvider, IProbeConfigProvider {
	private static final ResourceLocation ID = new ResourceLocation(StaticPower.MOD_ID, "progress");

	@Override
	public ResourceLocation getID() {
		return ID;
	}

	@Override
	public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, Player player, Level world, BlockState blockState,
			IProbeHitData data) {
		BlockEntity be = world.getBlockEntity(data.getPos());
		if (be == null) {
			return;
		}

		@SuppressWarnings("rawtypes")
		Optional<AbstractProcessingComponent> processing = ComponentUtilities
				.getComponent(AbstractProcessingComponent.class, be);
		if (processing.isPresent()) {
			if (processing.get().hasProcessingStarted()) {
				Timer processingTimer = processing.get().getProcessingTimer();
				int remaining = processingTimer.getMaxTime() - processingTimer.getCurrentTime();
				MutableComponent suffix = GuiTextUtilities.formatNumberAsString(remaining).append(" ")
						.append(Component.translatable("gui.staticcore.ticks_remaining"));
				probeInfo.progress(remaining, processingTimer.getMaxTime(),
						probeInfo.defaultProgressStyle()
								.filledColor(JadePluginImplementation.MAIN_PROCESSING_COLOR.encodeInInteger())
								.alternateFilledColor(JadePluginImplementation.ALT_PROCESSING_COLOR.encodeInInteger())
								.numberFormat(NumberFormat.NONE).borderColor(SDColor.EIGHT_BIT_GREY.encodeInInteger())
								.suffix(suffix));
			} else {
				probeInfo.progress(0, 0, probeInfo.defaultProgressStyle()
						.filledColor(JadePluginImplementation.MAIN_PROCESSING_COLOR.encodeInInteger())
						.alternateFilledColor(JadePluginImplementation.ALT_PROCESSING_COLOR.encodeInInteger())
						.numberFormat(NumberFormat.COMPACT).borderColor(SDColor.EIGHT_BIT_GREY.encodeInInteger())
						.suffix(Component.translatable("gui.staticcore.ticks_remaining")));
			}
		}
	}

	@Override
	public void getProbeConfig(IProbeConfig config, Player player, Level world, Entity entity,
			IProbeHitEntityData data) {
	}

	@Override
	public void getProbeConfig(IProbeConfig config, Player player, Level world, BlockState blockState,
			IProbeHitData data) {

	}
}