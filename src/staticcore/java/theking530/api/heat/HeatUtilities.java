package theking530.api.heat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
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
import theking530.staticcore.fluid.StaticCoreFluidType;
import theking530.staticcore.init.StaticCoreRecipeTypes;
import theking530.staticcore.utilities.math.SDMath;
import theking530.staticcore.world.WorldUtilities;

public class HeatUtilities {
	public record HeatInfo(float mass, float temperature, float conductivity, float specificHeat,
			IHeatStorage heatStorage) {

		public HeatInfo(IHeatStorage storage) {
			this(storage.getMass(), storage.getCurrentTemperature(), storage.getConductivity(),
					storage.getSpecificHeat(), storage);
		}

		public HeatInfo(float mass, float temperature, float conductivity, float specificHeat) {
			this(mass, temperature, conductivity, specificHeat, null);
		}
	}

	public static final float HEAT_TRANSFER_RATE = 100.0f;
	public static final float THERMAL_BEHAVIOUR_DELTA_CHANCE_DIVISOR = 4000f;

	public static float transferHeat(IHeatStorage storage, Level world, BlockPos currentPos,
			HeatTransferAction action) {

		float totalTransfered = 0.0f;
		List<Direction> randomDirections = new ArrayList<Direction>(6);
		for (Direction side : Direction.values()) {
			randomDirections.add(side);
		}
		Collections.shuffle(randomDirections);

		for (Direction side : randomDirections) {
			HeatInfo adjacentHeat = HeatUtilities.getHeatInfoOnSide(world, currentPos, side, storage);
			if (adjacentHeat.heatStorage() == storage) {
				continue;
			}

			HeatInfo source = new HeatInfo(storage);
			float flux = calculateHeatFluxTransfer(source, adjacentHeat);
			if (flux == 0) {
				continue;
			}

			if (adjacentHeat.heatStorage() != null) {
				if (flux > 0) {
					float actualFlux = storage.cool(flux, action);
					totalTransfered -= adjacentHeat.heatStorage().heat(actualFlux, action);
				} else {
					float actualFlux = storage.heat(-flux, action);
					totalTransfered += adjacentHeat.heatStorage().cool(actualFlux, action);
				}
			} else {
				if (flux > 0) {
					totalTransfered -= source.heatStorage().cool(flux, action);
				} else {
					totalTransfered += source.heatStorage().heat(-flux, action);
				}
			}
		}

		if (action == HeatTransferAction.EXECUTE) {
			for (Direction dir : Direction.values()) {
				HeatUtilities.handleOverheatingOnSide(world, currentPos, dir, storage);
			}
		}

		return totalTransfered;
	}

	public static float calculateTemperatureDelta(float heatPower, float specificHeat, float mass) {
		return heatPower / (specificHeat * mass);
	}

	public static float calculateHeatPowerPerTickRequired(float temperatureDelta, float specificHeat, float mass) {
		return (temperatureDelta * specificHeat * mass);
	}

	public static float calculateHeatFluxTransfer(HeatInfo source, HeatInfo target) {
		float conductivity = source.conductivity() + target.conductivity();
		float delta = source.temperature() - target.temperature();
		return (conductivity * delta) / 20;
	}

	public static HeatInfo getAmbientProperties(Level world, BlockPos currentPos) {
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

		return new HeatInfo(IHeatStorage.DEFAULT_BLOCK_MASS, ambientHeat, IHeatStorage.AIR_CONDUCTIVITY,
				IHeatStorage.AIR_SPECIFIC_HEAT, null);
	}

	public static HeatInfo getHeatInfoOnSide(Level world, BlockPos currentPos, Direction side, IHeatStorage storage) {
		// Get the offset position.
		BlockPos offsetPos = currentPos.relative(side);
		BlockEntity be = world.getBlockEntity(offsetPos);
		if (be != null) {
			IHeatStorage otherStorage = be.getCapability(CapabilityHeatable.HEAT_STORAGE_CAPABILITY, side.getOpposite())
					.orElse(null);
			if (otherStorage != null) {
				return new HeatInfo(otherStorage);
			}
		}

		FluidState fluidState = world.getFluidState(offsetPos);
		BlockState blockstate = world.getBlockState(offsetPos);
		ThermalConductivityRecipe recipe = world.getRecipeManager()
				.getRecipeFor(StaticCoreRecipeTypes.THERMAL_CONDUCTIVITY_RECIPE_TYPE.get(),
						new RecipeMatchParameters(blockstate).setFluids(new FluidStack(fluidState.getType(), 1000)),
						world)
				.orElse(null);

		HeatInfo ambientTemperature = getAmbientProperties(world, offsetPos);
		if (recipe == null) {
			return ambientTemperature;
		}

		return new HeatInfo(recipe.getMass(),
				recipe.hasActiveTemperature() ? recipe.getTemperature() : ambientTemperature.temperature(),
				recipe.getConductivity(), recipe.getSpecificHeat());
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

	public static Optional<ThermalConductivityRecipe> getThermalPropertiesForFluid(Level level, FluidStack fluid) {
		return level.getRecipeManager().getRecipeFor(StaticCoreRecipeTypes.THERMAL_CONDUCTIVITY_RECIPE_TYPE.get(),
				new RecipeMatchParameters(fluid), level);
	}

	public static Optional<ThermalConductivityRecipe> getThermalPropertiesForBlock(Level level, BlockState block) {
		return level.getRecipeManager().getRecipeFor(StaticCoreRecipeTypes.THERMAL_CONDUCTIVITY_RECIPE_TYPE.get(),
				new RecipeMatchParameters(block), level);
	}

	public static boolean canFullyAbsorbHeat(IHeatStorage storage, float heatAmount) {
		return storage.getCurrentTemperature() + heatAmount <= storage.getMaximumTemperature();
	}

	public static void setFluidTemperature(FluidStack fluid, float temperature) {
		CompoundTag tag = fluid.getOrCreateTag();
		tag.putFloat(StaticCoreFluidType.TEMPERATURE_TAG, temperature);
	}

	public static float getFluidTemperature(Level level, BlockPos pos, FluidStack fluid) {
		if (fluid.hasTag() && fluid.getTag().contains(StaticCoreFluidType.TEMPERATURE_TAG)) {
			return fluid.getTag().getFloat(StaticCoreFluidType.TEMPERATURE_TAG);
		}

		Optional<ThermalConductivityRecipe> recipe = getThermalPropertiesForFluid(level, fluid);
		if (recipe.isPresent() && recipe.get().hasActiveTemperature()) {
			return recipe.get().getTemperature();
		}

		return getAmbientProperties(level, pos).temperature();
	}
}
