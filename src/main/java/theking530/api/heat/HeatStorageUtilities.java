package theking530.api.heat;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.utilities.SDMath;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerRecipeRegistry;
import theking530.staticpower.data.crafting.wrappers.thermalconductivity.ThermalConductivityRecipe;
import theking530.staticpower.utilities.WorldUtilities;

public class HeatStorageUtilities {
	public static final double ACTIVE_ENERGY_TRANSFER_RATIO = 1 / 10000.0;

	/**
	 * Transfers the heat stored in this storage to adjacent blocks. The transfered
	 * amount is equal to the thermal conductivity of the adjacent block multiplied
	 * by the thermal conductivity of the heat storage.
	 * 
	 * @param storage    The heat storage that provides the heat.
	 * @param world      The world access.
	 * @param currentPos The position of this heat storage.
	 */

	public static void transferHeatWithSurroundings(IHeatStorage storage, Level world, BlockPos currentPos, boolean performOverheating) {
		for (Direction dir : Direction.values()) {
			HeatStorageUtilities.transferHeatPassivelyWithBlockFromDirection(world, currentPos, dir, storage);
			HeatStorageUtilities.transferHeatActivelyWithBlockFromDirection(world, currentPos, dir, storage);
		}
	}

	/**
	 * Transfers the heat stored in this storage to adjacent blocks passively. This
	 * is using ThermalConductivity recipes and applies the #thermalOffset. Also
	 * handles overheating.
	 * 
	 * @param world   The world access.
	 * @param pos     The position at which to transfer the heat.
	 * @param side    The side from which to transfer heat from (the side of the
	 *                heat storage).
	 * @param storage The heat storage.
	 */
	public static double transferHeatPassivelyWithBlockFromDirection(Level world, BlockPos pos, Direction side, IHeatStorage storage) {
		// Get the offset position.
		BlockPos offsetPos = pos.relative(side);

		// Get the block and fluid states at the offset pos.
		FluidState fluidState = world.getFluidState(offsetPos);
		BlockState blockstate = world.getBlockState(offsetPos);

		// If there is a recipe for thermal conductivity for this block
		ThermalConductivityRecipe recipe = StaticPowerRecipeRegistry
				.getRecipe(ThermalConductivityRecipe.RECIPE_TYPE, new RecipeMatchParameters(blockstate).setFluids(new FluidStack(fluidState.getType(), 1000))).orElse(null);

		// Perform the transfer.
		if (recipe != null) {
			// Check if there is an overheating behaviour.
			if (recipe.hasOverheatingBehaviour()) {
				if (storage.getCurrentHeat() >= recipe.getOverheatedTemperature()) {
					// Add a little random in there.
					if (SDMath.diceRoll(0.025)) {
						// Perform the overheating with a block.
						if (recipe.hasOverheatedBlock() && recipe.getOverheatedBlock() != world.getBlockState(offsetPos)) {
							world.setBlockAndUpdate(offsetPos, recipe.getOverheatedBlock());
						}

						// If an overheated item is established, spawn it.
						if (recipe.hasOverheatedItem()) {
							ItemStack output = recipe.getOverheatedItem().calculateOutput();
							if (!output.isEmpty()) {
								WorldUtilities.dropItem(world, offsetPos, output);
							}
						}
						world.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.AMBIENT, 0.5f, SDMath.getRandomIntInRange(8, 12) / 10.0f);
						world.addParticle(ParticleTypes.SMOKE, pos.getX() + 0.5f, pos.getY() + 1.0f, pos.getZ() + 0.5f, 0.0f, 0.01f, 0.0f);
					}
					return 0.0; // We overheated, do nothing.
				}
			}

			double delta = recipe.getThermalOffset() * storage.getConductivity();
			if (delta > 0) {
				return storage.heat(delta, false);
			} else {
				return storage.cool(-delta, false);
			}
		} else {
			// Everything without a recipe cools for 0.5 units (half of air).
			return storage.cool(1, false);
		}
	}

	/**
	 * Transfers the heat stored in this storage to adjacent tile entities actively.
	 * The transfered amount is equal to the thermal conductivity of the adjacent
	 * heat storage multiplied by the thermal conductivity of this storage
	 * multiplied by the heat in the storage. This is calculated per sq cm.
	 * 
	 * @param world   The world access.
	 * @param pos     The position at which to transfer the heat.
	 * @param side    The side from which to transfer heat from (the side of the
	 *                heat storage).
	 * @param storage The heat storage.
	 */
	public static double transferHeatActivelyWithBlockFromDirection(Level world, BlockPos pos, Direction side, IHeatStorage storage) {
		// Get the offset position.
		BlockPos offsetPos = pos.relative(side);

		// Get the tile entity at the position.
		BlockEntity te = world.getBlockEntity(offsetPos);

		// If it exists.
		if (te != null) {
			// Try to get the heat storage.
			IHeatStorage otherStorage = te.getCapability(CapabilityHeatable.HEAT_STORAGE_CAPABILITY, side.getOpposite()).orElse(null);

			// If that too exists, perform the transfer.
			if (otherStorage != null) {
				return transferHeatActivelyWithOtherStorage(storage, otherStorage);
			}
		}
		return 0;
	}

	/**
	 * Performs a basic transfer between one source (the storage) to another source
	 * (the otherstorage).
	 * 
	 * @param storage      The heat storage.
	 * @param otherStorage The heat destination.
	 * @return
	 */
	public static double transferHeatActivelyWithOtherStorage(IHeatStorage storage, IHeatStorage otherStorage) {
		if (storage == otherStorage) {
			return 0.0;
		}

		double energyToTransfer = storage.getConductivity() * otherStorage.getConductivity() * storage.getCurrentHeat();
		double cooled = otherStorage.heat(energyToTransfer, false);
		storage.cool(cooled, false);
		return cooled;
	}
}
