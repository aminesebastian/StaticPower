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
import theking530.api.heat.IHeatStorage.HeatTransferAction;
import theking530.staticcore.crafting.RecipeMatchParameters;
import theking530.staticcore.crafting.thermal.ThermalConductivityRecipe;
import theking530.staticcore.init.StaticCoreRecipeTypes;
import theking530.staticcore.utilities.math.SDMath;
import theking530.staticcore.world.WorldUtilities;

public class HeatStorageUtilities {
	public static final float HEATING_RATE = 1 / 1000f;

	/**
	 * Transfers the heat stored in this storage to adjacent blocks. The transfered
	 * amount is equal to the thermal conductivity of the adjacent block multiplied
	 * by the thermal conductivity of the heat storage.
	 * 
	 * @param storage    The heat storage that provides the heat.
	 * @param world      The world access.
	 * @param currentPos The position of this heat storage.
	 */

	public static int transferHeatWithSurroundings(IHeatStorage storage, Level world, BlockPos currentPos,
			HeatTransferAction action) {
		int appliedDelta = 0;

		for (Direction side : Direction.values()) {
			// Get the temperature and conductivity on this side.
			float tempOnSide = HeatStorageUtilities.getThermalPowerOnSide(world, currentPos, side, storage);
			float conductivityOnSide = HeatStorageUtilities.getConductivityOnSide(world, currentPos, side, storage);

			// If we're simulating and want to see max efficiency, set the temp on the side
			// to the overheat temp, as that is the point of maximum efficiency.
			if (action == HeatTransferAction.SIMULATE_MAX_EFFICIENCY) {
				tempOnSide = storage.getOverheatThreshold();
			}

			// Skip any other entities with heat storage capability, we'll deal with those
			// after.
			BlockEntity be = world.getBlockEntity(currentPos.relative(side));
			if (be != null) {
				if (be.getCapability(CapabilityHeatable.HEAT_STORAGE_CAPABILITY, side.getOpposite()).isPresent()) {
					continue;
				}
			}

			// The proportion of the delta we can move this call.
			// A value of 1 will move us instantly to the target.
			float heatingCoefficient = Math.min(conductivityOnSide * storage.getConductivity(), 1.0f / HEATING_RATE)
					* HEATING_RATE;

			// Heat transfer is MOST efficient at the overheat threshold and falls off
			// linearly under that temp. Efficiency over the overheat is worse by a
			// factor of 4 and curved on a quintic scale.
			if (action == HeatTransferAction.EXECUTE) {
				float efficiencyMultiplier = 1;
				if (storage.getCurrentHeat() > storage.getOverheatThreshold()) {
					float totalRange = storage.getMaximumHeat() - storage.getOverheatThreshold();
					float relativeValue = storage.getCurrentHeat() - storage.getOverheatThreshold();
					efficiencyMultiplier = 1.5f - (float) relativeValue / totalRange;
					efficiencyMultiplier = (float) Math.pow(efficiencyMultiplier, 4) / 4;
				} else {
					float totalRange = storage.getOverheatThreshold() - IHeatStorage.MINIMUM_TEMPERATURE;
					float relativeValue = storage.getCurrentHeat() - IHeatStorage.MINIMUM_TEMPERATURE;
					efficiencyMultiplier = (float) relativeValue / totalRange;
				}
				heatingCoefficient *= efficiencyMultiplier;
			}

			// Calculate the delta to the target temperature.
			float delta = tempOnSide - storage.getCurrentHeat();

			// How much can we move towards the target.
			float amountToApply = delta * heatingCoefficient;
			if (Math.abs(amountToApply - delta) < 100) {
				amountToApply = delta;
			}

			// Move towards the targetTemperature amountToApply degrees.
			if (amountToApply > 0) {
				appliedDelta += storage.heat(amountToApply, action);
			} else {
				appliedDelta -= storage.cool(-amountToApply, action);
			}
		}

		for (Direction side : Direction.values()) {
			BlockEntity be = world.getBlockEntity(currentPos.relative(side));
			if (be != null) {
				IHeatStorage otherStorage = be
						.getCapability(CapabilityHeatable.HEAT_STORAGE_CAPABILITY, side.getOpposite()).orElse(null);
				if (otherStorage != null && otherStorage != storage) {

					// The proportion of the delta we can move this call.
					// A value of 1 will move us instantly to the target.
					float heatingCoefficient = Math.min(otherStorage.getConductivity() * storage.getConductivity(),
							1.0f / HEATING_RATE) * HEATING_RATE;

					// Calculate the delta to the target temperature.
					float tempTarget = otherStorage.getCurrentHeat();
					float delta = tempTarget - storage.getCurrentHeat();

					// How much can we move towards the target.
					float amountToApply = delta * heatingCoefficient;
					if (Math.abs(amountToApply - delta) < 100) {
						amountToApply = delta;
					}

					// Move towards the targetTemperature amountToApply degrees.
					if (amountToApply > 0) {
						float heatApplied = storage.heat(amountToApply, action);
						otherStorage.cool(heatApplied, action);
						appliedDelta += heatApplied;
					} else {
						float heatApplied = storage.cool(-amountToApply, action);
						otherStorage.heat(heatApplied, action);
						appliedDelta -= heatApplied;
					}

				}
			}
		}

		// Process any overheating on each side.
		if (action == HeatTransferAction.EXECUTE) {
			for (Direction dir : Direction.values()) {
				HeatStorageUtilities.handleOverheatingOnSide(world, currentPos, dir, storage);
			}
		}

		return appliedDelta;
	}

	public static float getBiomeAmbientTemperature(Level world, BlockPos currentPos) {
		// Get the current biome we're in.
		Holder<Biome> biome = world.getBiome(currentPos);

		float ambientHeat = IHeatStorage.ROOM_TEMPERATURE;
		if (biome.containsTag(Tags.Biomes.IS_HOT)) {
			ambientHeat += 15;
		}

		if (biome.containsTag(Tags.Biomes.IS_COLD)) {
			ambientHeat -= -15;
		}

		if (biome.containsTag(Tags.Biomes.IS_DRY)) {
			ambientHeat += 5;
		}

		if (biome.containsTag(Tags.Biomes.IS_WET)) {
			ambientHeat -= 5;
		}

		if (biome.containsTag(Tags.Biomes.IS_SANDY)) {
			ambientHeat -= 5;
		}

		if (biome.containsTag(Tags.Biomes.IS_WATER)) {
			ambientHeat -= 1;
		}

		if (!world.canSeeSky(currentPos.above())) {
			ambientHeat -= 10;
		}

		if (world.isRaining()) {
			ambientHeat -= 5;
		}

		return ambientHeat;
	}

	public static float getThermalPowerOnSide(Level world, BlockPos currentPos, Direction side, IHeatStorage storage) {
		// Get the offset position.
		BlockPos offsetPos = currentPos.relative(side);
		BlockEntity be = world.getBlockEntity(offsetPos);
		if (be != null) {
			IHeatStorage otherStorage = be.getCapability(CapabilityHeatable.HEAT_STORAGE_CAPABILITY, side.getOpposite())
					.orElse(null);
			if (otherStorage != null) {
				return otherStorage.getCurrentHeat();
			}
		}

		// Get the block and fluid states at the offset pos.
		FluidState fluidState = world.getFluidState(offsetPos);
		BlockState blockstate = world.getBlockState(offsetPos);

		// If there is a recipe for thermal conductivity for this block
		ThermalConductivityRecipe recipe = world.getRecipeManager()
				.getRecipeFor(StaticCoreRecipeTypes.THERMAL_CONDUCTIVITY_RECIPE_TYPE.get(),
						new RecipeMatchParameters(blockstate).setFluids(new FluidStack(fluidState.getType(), 1000)),
						world)
				.orElse(null);

		// Get the temperature on that side.
		if (recipe != null && recipe.hasActiveTemperature()) {
			return recipe.getTemperature();
		} else {
			return getBiomeAmbientTemperature(world, offsetPos);
		}
	}

	public static float getConductivityOnSide(Level world, BlockPos pos, Direction side, IHeatStorage storage) {
		// Get the offset position.
		BlockPos offsetPos = pos.relative(side);
		BlockEntity be = world.getBlockEntity(offsetPos);
		if (be != null) {
			IHeatStorage otherStorage = be.getCapability(CapabilityHeatable.HEAT_STORAGE_CAPABILITY, side.getOpposite())
					.orElse(null);
			if (otherStorage != null) {
				return otherStorage.getConductivity();
			}
		}

		// Get the block and fluid states at the offset pos.
		FluidState fluidState = world.getFluidState(offsetPos);
		BlockState blockstate = world.getBlockState(offsetPos);

		// If there is a recipe for thermal conductivity for this block
		ThermalConductivityRecipe recipe = world.getRecipeManager()
				.getRecipeFor(StaticCoreRecipeTypes.THERMAL_CONDUCTIVITY_RECIPE_TYPE.get(),
						new RecipeMatchParameters(blockstate).setFluids(new FluidStack(fluidState.getType(), 1000)),
						world)
				.orElse(null);

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
		ThermalConductivityRecipe recipe = world.getRecipeManager()
				.getRecipeFor(StaticCoreRecipeTypes.THERMAL_CONDUCTIVITY_RECIPE_TYPE.get(),
						new RecipeMatchParameters(blockstate).setFluids(new FluidStack(fluidState.getType(), 1000)),
						world)
				.orElse(null);

		// Perform the transfer.
		if (recipe != null) {
			// Check if there is an overheating behaviour.
			if (recipe.hasOverheatingBehaviour()) {
				if (storage.getCurrentHeat() >= recipe.getOverheatingBehaviour().getTemperature()) {
					// Add a little random in there.
					if (SDMath.diceRoll(0.025)) {
						// Perform the overheating with a block.
						if (recipe.getOverheatingBehaviour().hasBlock()
								&& recipe.getOverheatingBehaviour().getBlockState() != world.getBlockState(offsetPos)) {
							world.setBlockAndUpdate(offsetPos, recipe.getOverheatingBehaviour().getBlockState());
						}

						// If an overheated item is established, spawn it.
						if (!recipe.getOverheatingBehaviour().hasItem()) {
							ItemStack output = recipe.getOverheatingBehaviour().getItem().calculateOutput();
							if (!output.isEmpty()) {
								WorldUtilities.dropItem(world, offsetPos, output);
							}
						}
						world.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.AMBIENT, 0.5f,
								SDMath.getRandomIntInRange(8, 12) / 10.0f);
						world.addParticle(ParticleTypes.SMOKE, pos.getX() + 0.5f, pos.getY() + 1.0f, pos.getZ() + 0.5f,
								0.0f, 0.01f, 0.0f);
					}
				}
			}
		}
	}

	public static boolean canFullyAbsorbHeat(IHeatStorage storage, float heatAmount) {
		return storage.getCurrentHeat() + heatAmount <= storage.getMaximumHeat();
	}

}
