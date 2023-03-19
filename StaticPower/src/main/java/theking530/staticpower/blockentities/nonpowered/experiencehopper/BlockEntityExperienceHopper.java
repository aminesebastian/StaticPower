package theking530.staticpower.blockentities.nonpowered.experiencehopper;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.blockentity.BlockEntityBase;
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticcore.blockentity.components.fluids.FluidOutputServoComponent;
import theking530.staticcore.blockentity.components.fluids.FluidTankComponent;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticcore.utilities.math.SDMath;
import theking530.staticpower.client.rendering.blockentity.BlockEntityRenderHopper;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModFluids;

public class BlockEntityExperienceHopper extends BlockEntityBase {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityExperienceHopper> TYPE = new BlockEntityTypeAllocator<>("experience_hopper",
			(type, pos, state) -> new BlockEntityExperienceHopper(pos, state), ModBlocks.ExperienceHopper);

	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setTileEntitySpecialRenderer(BlockEntityRenderHopper::new);
		}
	}

	public final FluidTankComponent internalTank;
	public final SideConfigurationComponent ioSideConfiguration;

	public BlockEntityExperienceHopper(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);
		registerComponent(internalTank = new FluidTankComponent("InputFluidTank", 100).setCapabilityExposedModes(MachineSideMode.Output).setAutoSyncPacketsEnabled(true));
		registerComponent(new FluidOutputServoComponent("FluidOutputServoComponent", 100, internalTank, MachineSideMode.Output));
		registerComponent(ioSideConfiguration = new SideConfigurationComponent("SideConfiguration", ExperienceHopperPreset.INSTANCE));
	}

	@Override
	public void process() {
		// Handle the upgrade tick on the server.
		if (!level.isClientSide) {
			// Create the AABB to search within.
			AABB aabb = new AABB(worldPosition.getX() + 0.1, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.1, worldPosition.getX() + 0.9, worldPosition.getY() + 1.05,
					worldPosition.getZ() + 0.9);

			// Track how much we filled with XP.
			int filled = 0;

			// Drain XP from players.
			if (!internalTank.isFull() && SDMath.diceRoll(0.75f)) {
				// Calculate the max amount of XP we can take.
				int remainingCapacity = internalTank.getCapacity() - internalTank.getFluidAmount();
				filled = takeXPFromPlayers(aabb, Math.min(2, remainingCapacity));
			}

			// Capture experience from orbs.
			filled += suckXPOrbs(aabb);

			// Play a sound and synchronize if there were any experience filled.
			if (filled > 0) {
				getLevel().playSound(null, worldPosition, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 0.15F, (getLevel().getRandom().nextFloat() + 1) / 2);
			}
		}
	}

	protected int takeXPFromPlayers(AABB bounds, int amountToTake) {
		// Take experience from players.
		int filled = 0;
		List<Player> players = getLevel().getEntitiesOfClass(Player.class, bounds);
		for (Player player : players) {
			// Check if the player has experience.
			if (player.totalExperience > 0) {
				// Get the XP amount to drain.
				int playerDrainAmount = SDMath.clamp(amountToTake, 0, player.totalExperience);

				// Fill with the XP
				internalTank.fill(new FluidStack(ModFluids.LiquidExperience.getSource().get(), playerDrainAmount), FluidAction.EXECUTE);
				filled = playerDrainAmount;

				// Drain the XP.
				player.giveExperiencePoints(-playerDrainAmount);
			}
		}
		return filled;
	}

	protected int suckXPOrbs(AABB bounds) {
		// Vacuum experience if requested.
		int filled = 0;
		List<ExperienceOrb> xpOrbs = getLevel().getEntitiesOfClass(ExperienceOrb.class, bounds);
		for (ExperienceOrb orb : xpOrbs) {
			int tempFilled = internalTank.fill(new FluidStack(ModFluids.LiquidExperience.getSource().get(), orb.value), FluidAction.SIMULATE);
			if (tempFilled != orb.value) {
				break;
			} else {
				filled += tempFilled;
				internalTank.fill(new FluidStack(ModFluids.LiquidExperience.getSource().get(), orb.value), FluidAction.EXECUTE);
				orb.remove(RemovalReason.DISCARDED);
			}
		}
		return filled;
	}
}
