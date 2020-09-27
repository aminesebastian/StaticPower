package theking530.staticpower.integration.TOP;

import java.util.function.Function;

import javax.annotation.Nullable;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ITheOneProbe;
import mcjty.theoneprobe.api.NumberFormat;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import theking530.api.power.CapabilityStaticVolt;
import theking530.staticpower.StaticPower;
import theking530.staticpower.cables.digistore.DigistoreCableProviderComponent;
import theking530.staticpower.tileentities.TileEntityBase;
import theking530.staticpower.tileentities.components.control.AbstractProcesingComponent;

public class StaticPowerTOPHandler implements IProbeInfoProvider {
	@Override
	public String getID() {
		return StaticPower.MOD_ID + ":basic_machine";
	}

	/**
	 * Add information for the probe info for the given block. This is always called
	 * server side. The given probeInfo object represents a vertical layout. So
	 * adding elements to that will cause them to be grouped vertically.
	 */
	@Override
	public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world,
			BlockState blockState, IProbeHitData data) {
		// The TE we are looking at if it exists.
		TileEntity te = world.getTileEntity(data.getPos());
		if (te == null) {
			return;
		}

		// Handle any static volts.
		world.getTileEntity(data.getPos()).getCapability(CapabilityStaticVolt.STATIC_VOLT_CAPABILITY)
				.ifPresent(handler -> {
					probeInfo.progress(handler.getStoredPower(), handler.getCapacity(),
							probeInfo.defaultProgressStyle().suffix("V").filledColor(0xff0099cc)
									.alternateFilledColor(0xff0075ff).borderColor(0xff999999)
									.numberFormat(NumberFormat.COMPACT));
				});

		// Get the base tile entity.
		if (!(te instanceof TileEntityBase)) {
			return;
		}
		TileEntityBase teBase = (TileEntityBase) te;

		// Add the info for the processing component.
		if (teBase.hasComponentOfType(AbstractProcesingComponent.class)) {
			AbstractProcesingComponent processingComponent = teBase.getComponent(AbstractProcesingComponent.class);
			if (processingComponent.isProcessing()) {
				probeInfo.progress(
						processingComponent.getMaxProcessingTime() - processingComponent.getCurrentProcessingTime(),
						processingComponent.getMaxProcessingTime(),
						probeInfo.defaultProgressStyle().suffix("Ticks Remaining").filledColor(0xffaaaaaa)
								.alternateFilledColor(0xffaaaaaa).borderColor(0xff999999)
								.numberFormat(NumberFormat.COMPACT));
			} else {
				probeInfo.progress(0, 0,
						probeInfo.defaultProgressStyle().suffix("Ticks Remaining").filledColor(0xffaaaaaa)
								.alternateFilledColor(0xffaaaaaa).borderColor(0xff999999)
								.numberFormat(NumberFormat.COMPACT));
			}

		}
		// Add the digistore component info.
		if (teBase.hasComponentOfType(DigistoreCableProviderComponent.class)) {
			DigistoreCableProviderComponent digistoreComponent = teBase
					.getComponent(DigistoreCableProviderComponent.class);
			if (!digistoreComponent.isManagerPresent()) {
				probeInfo.text(new StringTextComponent("Manager Not Present!"));
			}
		}
	}

	public static class TOPHandler implements Function<ITheOneProbe, Void> {
		public static StaticPowerTOPHandler Handler;
		public static ITheOneProbe PROBE;

		@Nullable
		@Override
		public Void apply(ITheOneProbe theOneProbe) {
			PROBE = theOneProbe;
			Handler = new StaticPowerTOPHandler();

			PROBE.registerProvider(new IProbeInfoProvider() {
				@Override
				public String getID() {
					return StaticPower.MOD_ID + ":TOPPlugin";
				}

				@Override
				public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world,
						BlockState blockState, IProbeHitData data) {
					Handler.addProbeInfo(mode, probeInfo, player, world, blockState, data);
				}
			});
			return null;
		}
	}

}
