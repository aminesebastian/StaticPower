package theking530.api.heat;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.utilities.SDMath;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerRecipeRegistry;
import theking530.staticpower.data.crafting.wrappers.thermalconductivity.ThermalConductivityRecipe;
import theking530.staticpower.utilities.WorldUtilities;

public class HeatStorageUtilities {
	public static final float HEATING_RATE = 1 / 6000f;

	/**
	 * Transfers the heat stored in this storage to adjacent blocks. The transfered
	 * amount is equal to the thermal conductivity of the adjacent block multiplied
	 * by the thermal conductivity of the heat storage.
	 * 
	 * @param storage    The heat storage that provides the heat.
	 * @param world      The world access.
	 * @param currentPos The position of this heat storage.
	 */

	public static void transferHeatWithSurroundings(IHeatStorage storage, Level world, BlockPos currentPos) {
		// Capture the target temperature and the surrounding conductivity.
		int targetTemperature = 0;
		float surroundingConductivity = 0.0f;
		for (Direction dir : Direction.values()) {
			targetTemperature += HeatStorageUtilities.getThermalPowerOnSide(world, currentPos, dir, storage);
			surroundingConductivity += HeatStorageUtilities.getConductivityOnSide(world, currentPos, dir, storage);
		}

		// Make the target temp the average of all 6 sides.
		targetTemperature /= 6.0f;

		// The proportion of the delta we can move this call.
		// A value of 1 will move us instantly to the target.
		float heatingCoefficient = Math.min(surroundingConductivity * storage.getConductivity(), 1.0f / HEATING_RATE) * HEATING_RATE;

		// Calculate the delta to the target temperature.
		int delta = targetTemperature - storage.getCurrentHeat();

		// How much can we move towards the target.
		int amountToApply = (int) (delta * heatingCoefficient);
		if (Math.abs(amountToApply - delta) < 100) {
			amountToApply = delta;
		}

		// Move towards the targetTemperature amountToApply degrees.
		if (amountToApply > 0) {
			storage.heat(amountToApply, false);
		} else {
			storage.cool(-amountToApply, false);
		}

		for (Direction dir : Direction.values()) {
			HeatStorageUtilities.transferHeatActivelyWithBlockFromDirection(world, currentPos, dir, storage);
		}
		// Process any overheating on each side.
		for (Direction dir : Direction.values()) {
			HeatStorageUtilities.handleOverheatingOnSide(world, currentPos, dir, storage);
		}
	}

	public static int getBiomeAmbientTemperature(IHeatStorage storage, Level world, BlockPos currentPos) {
		// Get the current biome we're in.
		Holder<Biome> biome = world.getBiome(currentPos);

		int ambientHeat = IHeatStorage.ROOM_TEMPERATURE;
		if (biome.containsTag(Tags.Biomes.IS_HOT)) {
			ambientHeat = CapabilityHeatable.convertHeatToMilliHeat(50);
		} else if (biome.containsTag(Tags.Biomes.IS_COLD)) {
			ambientHeat = CapabilityHeatable.convertHeatToMilliHeat(-10);
		}

		return ambientHeat;
	}

	public static int getThermalPowerOnSide(Level world, BlockPos currentPos, Direction side, IHeatStorage storage) {
		// Get the offset position.
		BlockPos offsetPos = currentPos.relative(side);
		BlockEntity be = world.getBlockEntity(offsetPos);
		if (be != null) {
			IHeatStorage otherStorage = be.getCapability(CapabilityHeatable.HEAT_STORAGE_CAPABILITY, side.getOpposite()).orElse(null);
			if (otherStorage != null) {
				return otherStorage.getCurrentHeat();
			}
		}

		// Get the block and fluid states at the offset pos.
		FluidState fluidState = world.getFluidState(offsetPos);
		BlockState blockstate = world.getBlockState(offsetPos);

		// If there is a recipe for thermal conductivity for this block
		ThermalConductivityRecipe recipe = StaticPowerRecipeRegistry
				.getRecipe(ThermalConductivityRecipe.RECIPE_TYPE, new RecipeMatchParameters(blockstate).setFluids(new FluidStack(fluidState.getType(), 1000))).orElse(null);

		// Get the temperature on that side.
		if (recipe != null && recipe.hasActiveTemperature()) {
			return (int) (recipe.getTemperature());
		} else {
			return getBiomeAmbientTemperature(storage, world, offsetPos);
		}
	}

	public static float getConductivityOnSide(Level world, BlockPos pos, Direction side, IHeatStorage storage) {
		// Get the offset position.
		BlockPos offsetPos = pos.relative(side);
		BlockEntity be = world.getBlockEntity(offsetPos);
		if (be != null) {
			IHeatStorage otherStorage = be.getCapability(CapabilityHeatable.HEAT_STORAGE_CAPABILITY, side.getOpposite()).orElse(null);
			if (otherStorage != null) {
				return otherStorage.getConductivity();
			}
		}

		// Get the block and fluid states at the offset pos.
		FluidState fluidState = world.getFluidState(offsetPos);
		BlockState blockstate = world.getBlockState(offsetPos);

		// If there is a recipe for thermal conductivity for this block
		ThermalConductivityRecipe recipe = StaticPowerRecipeRegistry
				.getRecipe(ThermalConductivityRecipe.RECIPE_TYPE, new RecipeMatchParameters(blockstate).setFluids(new FluidStack(fluidState.getType(), 1000))).orElse(null);

		// By default, everything has a conductivity of 1.
		float conductivity = 1;
		if (recipe != null) {
			conductivity = recipe.getConductivity();
		}
		return conductivity;
	}

	public static void handleOverheatingOnSide(Level world, BlockPos pos, Direction side, IHeatStorage storage) {
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
				}
			}
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
	public static int transferHeatActivelyWithOtherStorage(IHeatStorage storage, IHeatStorage otherStorage) {
		if (storage == otherStorage) {
			return 0;
		}

		int targetTemperature = (int) ((storage.getCurrentHeat() + otherStorage.getCurrentHeat()) / 2.0f);
		int delta = targetTemperature - storage.getCurrentHeat();
		float heatingCoefficient = Math.min(storage.getConductivity() * otherStorage.getConductivity(), 1);
		int amountToApply = (int) (delta * heatingCoefficient);
		int amountApplied;

		if (amountToApply > 0) {
			amountApplied = storage.heat(amountToApply, false);
		} else {
			amountApplied = storage.cool(-amountToApply, false);
		}

		if (amountApplied > 0) {
			return otherStorage.heat(-amountToApply, false);
		} else {
			return otherStorage.cool(amountToApply, false);
		}
	}
}
