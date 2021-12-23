package theking530.api.heat;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.utilities.SDMath;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerRecipeRegistry;
import theking530.staticpower.data.crafting.wrappers.thermalconductivity.ThermalConductivityRecipe;
import theking530.staticpower.utilities.WorldUtilities;

public class HeatStorageUtilities {

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
	 * Transfers the heat stored in this storage to adjacent blocks passively. The
	 * transfered amount is equal to the thermal conductivity of the adjacent
	 * block/fluid multiplied by the thermal conductivity of this storage.
	 * 
	 * @param world   The world access.
	 * @param pos     The position at which to transfer the heat.
	 * @param side    The side from which to transfer heat from (the side of the
	 *                heat storage).
	 * @param storage The heat storage.
	 */
	public static double transferHeatPassivelyWithBlockFromDirection(Level world, BlockPos pos, Direction side, IHeatStorage storage) {
		// Capture the total transfered amount.
		double cooledAmount = 0;

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
					}
				}
			}

			// Perform any passive heating.
			if (recipe.getHeatAmount() > 0 && storage.getCurrentHeat() < storage.getMaximumHeat()) {
				cooledAmount -= storage.heat(recipe.getHeatAmount() * storage.getConductivity(), false);
			}

			// Perform passive cooling.
			if (storage.getCurrentHeat() > 0) {
				cooledAmount += storage.cool(recipe.getThermalConductivity() * storage.getConductivity(), false);
			}
		}

		return cooledAmount;
	}

	/**
	 * Transfers the heat stored in this storage to adjacent tile entities actively.
	 * The transfered amount is equal to the thermal conductivity of the adjacent
	 * heat storage multiplied by the thermal conductivity of this storage.
	 * 
	 * @param world   The world access.
	 * @param pos     The position at which to transfer the heat.
	 * @param side    The side from which to transfer heat from (the side of the
	 *                heat storage).
	 * @param storage The heat storage.
	 */
	public static double transferHeatActivelyWithBlockFromDirection(Level world, BlockPos pos, Direction side, IHeatStorage storage) {
		// Capture the total transfered amount.
		double cooledAmount = 0;

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
				cooledAmount += transferHeatActivelyWithOtherStorage(storage, otherStorage);
			}
		}
		return cooledAmount;
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
		double cooled = otherStorage.heat(storage.getCurrentHeat() * storage.getConductivity() * otherStorage.getConductivity(), false);
		storage.cool(cooled, false);
		return cooled;
	}
}
