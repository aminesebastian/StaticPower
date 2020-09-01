package theking530.staticpower.integration.TOP;

import java.util.function.Function;

import javax.annotation.Nullable;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ITheOneProbe;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.InterModComms;
import theking530.staticpower.StaticPower;

public class PluginTOP {

	private static boolean registered;

	public static void register() {
		if (registered) {
			return;
		}
		registered = true;
		InterModComms.sendTo("theoneprobe", "getTheOneProbe", GetTheOneProbe::new);
	}

	public static class GetTheOneProbe implements Function<ITheOneProbe, Void> {

		public static ITheOneProbe PROBE;

		@Nullable
		@Override
		public Void apply(ITheOneProbe theOneProbe) {
			PROBE = theOneProbe;

			PROBE.registerProvider(new IProbeInfoProvider() {
				@Override
				public String getID() {
					return StaticPower.MOD_ID + ":TOPPlugin";
				}

				@Override
				public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
					if (blockState.getBlock() instanceof IProbeInfoProvider) {
						IProbeInfoProvider provider = (IProbeInfoProvider) blockState.getBlock();
						if (provider != null) {
							provider.addProbeInfo(mode, probeInfo, player, world, blockState, data);
						}
					}

				}
			});
			return null;
		}
	}
}