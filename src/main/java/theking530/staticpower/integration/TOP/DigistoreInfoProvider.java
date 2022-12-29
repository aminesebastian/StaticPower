package theking530.staticpower.integration.TOP;

import java.util.Optional;

import mcjty.theoneprobe.api.IProbeConfig;
import mcjty.theoneprobe.api.IProbeConfigProvider;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeHitEntityData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticpower.StaticPower;
import theking530.staticpower.blockentities.components.ComponentUtilities;
import theking530.staticpower.cables.digistore.DigistoreCableProviderComponent;

public class DigistoreInfoProvider implements IProbeInfoProvider, IProbeConfigProvider {
	private static final ResourceLocation ID = new ResourceLocation(StaticPower.MOD_ID, "digistore");

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

		Optional<DigistoreCableProviderComponent> digistoreComponent = ComponentUtilities.getComponent(DigistoreCableProviderComponent.class, be);
		if (digistoreComponent.isPresent()) {
			if (digistoreComponent.get().isManagerPresent()) {
				probeInfo.text(Component.literal(ChatFormatting.GREEN.toString()).append(Component.translatable("gui.staticpower.manager_present")));
			} else {
				probeInfo.text(Component.literal(ChatFormatting.GREEN.toString()).append(Component.translatable("gui.staticpower.manager_missing")));
			}
		}
	}

	@Override
	public void getProbeConfig(IProbeConfig config, Player player, Level world, Entity entity, IProbeHitEntityData data) {
	}

	@Override
	public void getProbeConfig(IProbeConfig config, Player player, Level world, BlockState blockState, IProbeHitData data) {

	}
}