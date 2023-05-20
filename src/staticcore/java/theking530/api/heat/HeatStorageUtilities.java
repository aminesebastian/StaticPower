package theking530.api.heat;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
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
	public record HeatQuery(float mass, float temperature, float conductivity) {
	}

	public static final float HEATING_RATE = 200.0f;
	public static final float THERMAL_BEHAVIOUR_DELTA_CHANCE_DIVISOR = 4000f;

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
			HeatQuery adjacentHeat = HeatStorageUtilities.getThermalPowerOnSide(world, currentPos, side, storage);

			// If we're simulating and want to see max efficiency, set the temp on the side
			// to the overheat temp, as that is the point of maximum efficiency.
			if (action == HeatTransferAction.SIMULATE_MAX_EFFICIENCY) {
				adjacentHeat = new HeatQuery(adjacentHeat.mass(), storage.getOverheatThreshold(),
						adjacentHeat.conductivity());
			}

			float heatAmountPerTick = calculateHeatTransfer(adjacentHeat,
					new HeatQuery(storage.getMass(), storage.getCurrentTemperature(), storage.getConductivity()));
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
					float heatAmountPerTick = calculateHeatTransfer(
							new HeatQuery(otherStorage.getMass(), otherStorage.getCurrentTemperature(),
									otherStorage.getConductivity()),
							new HeatQuery(storage.getMass(), storage.getCurrentTemperature(),
									storage.getConductivity()));

					if (heatAmountPerTick > 0) {
						float cooled = otherStorage.cool(heatAmountPerTick, HeatTransferAction.SIMULATE);
						float heated = storage.heat(cooled, action);
						otherStorage.cool(heated, action);
					} else {
						float heated = otherStorage.heat(-heatAmountPerTick, HeatTransferAction.SIMULATE);
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

		return new HeatQuery(IHeatStorage.DEFAULT_BLOCK_MASS, ambientHeat, 0.01f);
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
				return new HeatQuery(otherStorage.getMass(), otherStorage.getCurrentTemperature(),
						otherStorage.getConductivity());
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
			return new HeatQuery(recipe.getThermalMass(),
					recipe.hasActiveTemperature() ? recipe.getTemperature() : ambientTemperature.temperature(),
					recipe.getConductivity());
		} else {
			return new HeatQuery(IHeatStorage.DEFAULT_BLOCK_MASS, ambientTemperature.temperature(),
					ambientTemperature.conductivity());
		}
	}

	public static void handleOverheatingOnSide(Level world, BlockPos pos, Direction side, IHeatStorage storage) {
		BlockPos offsetPos = pos.relative(side);
		FluidState fluidState = world.getFluidState(offsetPos);
		BlockState blockstate = world.getBlockState(offsetPos);

		// Edgecase to handle waterlogged blocks for now.
		if (!fluidState.isEmpty() && blockstate.getBlock() != fluidState.createLegacyBlock().getBlock()) {
			return;
		}

		ThermalConductivityRecipe recipe = world.getRecipeManager()
				.getRecipeFor(StaticCoreRecipeTypes.THERMAL_CONDUCTIVITY_RECIPE_TYPE.get(),
						new RecipeMatchParameters(blockstate).setFluids(new FluidStack(fluidState.getType(), 1000)),
						world)
				.orElse(null);

		if (recipe == null) {
			return;
		}

		if (recipe.hasOverheatingBehaviour()) {
			float overheatAmount = storage.getCurrentTemperature() - recipe.getOverheatingBehaviour().getTemperature();
			if (overheatAmount > 0) {
				// Add a little random in there.
				if (SDMath.diceRoll(overheatAmount / THERMAL_BEHAVIOUR_DELTA_CHANCE_DIVISOR)) {
					if (recipe.getOverheatingBehaviour().shouldDestroyExisting()) {
						if (recipe.getOverheatingBehaviour().hasBlock()
								&& recipe.getOverheatingBehaviour().getBlockState() != world.getBlockState(offsetPos)) {
							world.setBlockAndUpdate(offsetPos, recipe.getOverheatingBehaviour().getBlockState());
						} else {
							world.setBlockAndUpdate(offsetPos, Blocks.AIR.defaultBlockState());
						}
					}

					// If an overheated item is established, spawn it.
					if (recipe.getOverheatingBehaviour().hasItem()) {
						ItemStack output = recipe.getOverheatingBehaviour().getItem().calculateOutput();
						if (!output.isEmpty()) {
							WorldUtilities.dropItem(world, offsetPos, output);
						}
					}
					world.playSound(null, offsetPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.AMBIENT, 0.25f,
							SDMath.getRandomIntInRange(8, 12) / 10.0f);

					if (!world.isClientSide()) {
						((ServerLevel) world).sendParticles(ParticleTypes.LARGE_SMOKE,
								offsetPos.getX() + world.random.nextFloat(), offsetPos.getY() + 1.0,
								offsetPos.getZ() + world.random.nextFloat(), 1, 0.0D, 0.1D, 0.0D, 0.0D);
					}
					storage.cool(recipe.getOverheatingBehaviour().getTemperature(), HeatTransferAction.EXECUTE);
					return;
				}
			}
		}

		if (recipe.hasFreezeBehaviour()) {
			float freezeAmount = storage.getCurrentTemperature() - recipe.getFreezingBehaviour().getTemperature();
			if (freezeAmount < 0) {
				// Add a little random in there.
				if (SDMath.diceRoll(Math.abs(freezeAmount) / THERMAL_BEHAVIOUR_DELTA_CHANCE_DIVISOR)) {
					if (recipe.getFreezingBehaviour().shouldDestroyExisting()) {
						if (recipe.getFreezingBehaviour().hasBlock()
								&& recipe.getFreezingBehaviour().getBlockState() != world.getBlockState(offsetPos)) {
							world.setBlockAndUpdate(offsetPos, recipe.getFreezingBehaviour().getBlockState());
						} else {
							world.destroyBlock(offsetPos, false);
						}
					}

					// If an overheated item is established, spawn it.
					if (recipe.getFreezingBehaviour().hasItem()) {
						ItemStack output = recipe.getFreezingBehaviour().getItem().calculateOutput();
						if (!output.isEmpty()) {
							WorldUtilities.dropItem(world, offsetPos, output);
						}
					}
					world.playSound(null, offsetPos, SoundEvents.BREWING_STAND_BREW, SoundSource.AMBIENT, 0.5f,
							SDMath.getRandomIntInRange(8, 12) / 10.0f);
					world.addParticle(ParticleTypes.SPLASH, offsetPos.getX() + 0.5f, offsetPos.getY() + 1.0f,
							offsetPos.getZ() + 0.5f, 0.0f, 0.01f, 0.0f);
					storage.heat(recipe.getFreezingBehaviour().getTemperature(), HeatTransferAction.EXECUTE);
					return;
				}
			}
		}
	}

	public static boolean canFullyAbsorbHeat(IHeatStorage storage, float heatAmount) {
		return storage.getCurrentTemperature() + heatAmount <= storage.getMaximumHeat();
	}

	public static float calculateHeatTransfer(HeatQuery source, HeatQuery target) {
		float totalMass = source.mass() + target.mass();
		float averageConductivity = (source.conductivity() + target.conductivity()) / 2.0f;
		float delta = source.temperature() - target.temperature();
		float heatAmount = averageConductivity * delta;
		return heatAmount / (totalMass * HEATING_RATE);
	}

}
