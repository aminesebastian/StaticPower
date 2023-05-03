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
	public record HeatQuery(float temperature, float conductivity) {
	}

	public static final float HEATING_RATE = 1 / 50f;

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
		float totalPassive = 0;
		float totalApplied = 0;

		for (Direction side : Direction.values()) {
			// Skip any other entities with heat storage capability, we'll deal with those
			// after.
			BlockEntity be = world.getBlockEntity(currentPos.relative(side));
			if (be != null) {
				if (be.getCapability(CapabilityHeatable.HEAT_STORAGE_CAPABILITY, side.getOpposite()).isPresent()) {
					continue;
				}
			}

			// Get the temperature and conductivity on this side.
			HeatQuery heatAndConductivity = HeatStorageUtilities.getThermalPowerOnSide(world, currentPos, side,
					storage);

			// If we're simulating and want to see max efficiency, set the temp on the side
			// to the overheat temp, as that is the point of maximum efficiency.
			if (action == HeatTransferAction.SIMULATE_MAX_EFFICIENCY) {
				heatAndConductivity = new HeatQuery(storage.getOverheatThreshold(), heatAndConductivity.conductivity());
			}

			float averageConductivity = (heatAndConductivity.conductivity() + storage.getConductivity()) / 2.0f;
			float delta = heatAndConductivity.temperature() - storage.getCurrentHeat();
			float heatAmount = averageConductivity * delta;
			float heatAmountPerTick = heatAmount * 1 / 2000.0f;
			totalPassive += heatAmountPerTick;
		}

		if (Math.abs(totalPassive) < 0.001f) {
			totalPassive = 0;
		}

		if (totalPassive > 0) {
			totalApplied += storage.heat(totalPassive, action);
		} else if (totalPassive < 0) {
			totalApplied -= storage.cool(-totalPassive, action);
		}

		for (Direction side : Direction.values()) {
			BlockEntity be = world.getBlockEntity(currentPos.relative(side));
			if (be != null) {
				IHeatStorage otherStorage = be
						.getCapability(CapabilityHeatable.HEAT_STORAGE_CAPABILITY, side.getOpposite()).orElse(null);
				if (otherStorage != null && otherStorage != storage) {
					float averageConductivity = (otherStorage.getConductivity() + storage.getConductivity()) / 2.0f;
					float delta = otherStorage.getCurrentHeat() - storage.getCurrentHeat();
					float heatAmount = averageConductivity * delta * 1 / 200.0f;

					if (heatAmount > 0) {
						float cooled = otherStorage.cool(heatAmount, HeatTransferAction.SIMULATE);
						float heated = storage.heat(cooled, action);
						otherStorage.cool(heated, action);
					} else {
						float heated = otherStorage.heat(-heatAmount, HeatTransferAction.SIMULATE);
						float cooled = storage.cool(heated, action);
						otherStorage.heat(cooled, action);
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

		return (int) totalApplied;
	}

	public static HeatQuery getBiomeAmbientTemperature(Level world, BlockPos currentPos) {
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

		if (world.isRaining()) {
			ambientHeat -= 5;
		}

		return new HeatQuery(ambientHeat, 1.0f);
	}

	public static HeatQuery getThermalPowerOnSide(Level world, BlockPos currentPos, Direction side,
			IHeatStorage storage) {
		// Get the offset position.
		BlockPos offsetPos = currentPos.relative(side);
		BlockEntity be = world.getBlockEntity(offsetPos);
		if (be != null) {
			IHeatStorage otherStorage = be.getCapability(CapabilityHeatable.HEAT_STORAGE_CAPABILITY, side.getOpposite())
					.orElse(null);
			if (otherStorage != null) {
				return new HeatQuery(otherStorage.getCurrentHeat(), otherStorage.getConductivity());
			}
		}

		// Get the block and fluid states at the offset pos.
		FluidState fluidState = world.getFluidState(offsetPos);
		BlockState blockstate = world.getBlockState(offsetPos);
		HeatQuery ambientTemperature = getBiomeAmbientTemperature(world, offsetPos);

		// If there is a recipe for thermal conductivity for this block
		ThermalConductivityRecipe recipe = world.getRecipeManager()
				.getRecipeFor(StaticCoreRecipeTypes.THERMAL_CONDUCTIVITY_RECIPE_TYPE.get(),
						new RecipeMatchParameters(blockstate).setFluids(new FluidStack(fluidState.getType(), 1000)),
						world)
				.orElse(null);

		// Get the temperature on that side.
		if (recipe != null) {
			return new HeatQuery(
					recipe.hasActiveTemperature() ? recipe.getTemperature() : ambientTemperature.temperature(),
					recipe.getConductivity());
		} else {
			return new HeatQuery(ambientTemperature.temperature(), ambientTemperature.conductivity() / 10.0f);
		}
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
