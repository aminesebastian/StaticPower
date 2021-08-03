package theking530.staticpower.tileentities.nonpowered.experiencehopper;

import java.util.List;

import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.tileentity.TileEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticcore.utilities.SDMath;
import theking530.staticpower.client.rendering.tileentity.TileEntityRenderHopper;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModFluids;
import theking530.staticpower.tileentities.TileEntityConfigurable;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.tileentities.components.fluids.FluidOutputServoComponent;
import theking530.staticpower.tileentities.components.fluids.FluidTankComponent;

public class TileEntityExperienceHopper extends TileEntityConfigurable {
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityExperienceHopper> TYPE = new TileEntityTypeAllocator<>((type) -> new TileEntityExperienceHopper(), ModBlocks.ExperienceHopper);

	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setTileEntitySpecialRenderer(TileEntityRenderHopper::new);
		}
	}

	public final FluidTankComponent internalTank;

	public TileEntityExperienceHopper() {
		super(TYPE);
		registerComponent(internalTank = new FluidTankComponent("InputFluidTank", 100).setCanFill(true).setCapabilityExposedModes(MachineSideMode.Output).setAutoSyncPacketsEnabled(true));
		registerComponent(new FluidOutputServoComponent("FluidOutputServoComponent", 100, internalTank, MachineSideMode.Output));
	}

	@Override
	public void process() {
		// Handle the upgrade tick on the server.
		if (!world.isRemote) {
			// Create the AABB to search within.
			AxisAlignedBB aabb = new AxisAlignedBB(pos.getX() + 0.1, pos.getY() + 0.5, pos.getZ() + 0.1, pos.getX() + 0.9, pos.getY() + 1.05, pos.getZ() + 0.9);

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
				getWorld().playSound(null, pos, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 0.15F, (RANDOM.nextFloat() + 1) / 2);
			}
		}
	}

	protected int takeXPFromPlayers(AxisAlignedBB bounds, int amountToTake) {
		// Take experience from players.
		int filled = 0;
		List<PlayerEntity> players = getWorld().getEntitiesWithinAABB(PlayerEntity.class, bounds);
		for (PlayerEntity player : players) {
			// Check if the player has experience.
			if (player.experienceTotal > 0) {
				// Get the XP amount to drain.
				int playerDrainAmount = SDMath.clamp(amountToTake, 0, player.experienceTotal);

				// Fill with the XP
				internalTank.fill(new FluidStack(ModFluids.LiquidExperience.Fluid, playerDrainAmount), FluidAction.EXECUTE);
				filled = playerDrainAmount;

				// Drain the XP.
				player.giveExperiencePoints(-playerDrainAmount);
			}
		}
		return filled;
	}

	protected int suckXPOrbs(AxisAlignedBB bounds) {
		// Vacuum experience if requested.
		int filled = 0;
		List<ExperienceOrbEntity> xpOrbs = getWorld().getEntitiesWithinAABB(ExperienceOrbEntity.class, bounds);
		for (ExperienceOrbEntity orb : xpOrbs) {
			int tempFilled = internalTank.fill(new FluidStack(ModFluids.LiquidExperience.Fluid, orb.xpValue), FluidAction.SIMULATE);
			if (tempFilled != orb.xpValue) {
				break;
			} else {
				filled += tempFilled;
				internalTank.fill(new FluidStack(ModFluids.LiquidExperience.Fluid, orb.xpValue), FluidAction.EXECUTE);
				orb.remove();
			}
		}
		return filled;
	}

	protected boolean isValidSideConfiguration(BlockSide side, MachineSideMode mode) {
		if (side == BlockSide.BOTTOM) {
			return mode == MachineSideMode.Output;
		} else {
			return mode == MachineSideMode.Never;
		}
	}

	protected MachineSideMode[] getDefaultSideConfiguration() {
		return new MachineSideMode[] { MachineSideMode.Output, MachineSideMode.Never, MachineSideMode.Never, MachineSideMode.Never, MachineSideMode.Never, MachineSideMode.Never };
	}
}
