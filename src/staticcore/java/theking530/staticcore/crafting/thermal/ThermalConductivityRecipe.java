package theking530.staticcore.crafting.thermal;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import theking530.staticcore.block.BlockStateIngredient;
import theking530.staticcore.crafting.AbstractStaticPowerRecipe;
import theking530.staticcore.crafting.RecipeMatchParameters;
import theking530.staticcore.crafting.thermal.ThermalConductivityBehaviours.FreezingBehaviour;
import theking530.staticcore.crafting.thermal.ThermalConductivityBehaviours.OverheatingBehaviour;
import theking530.staticcore.fluid.FluidIngredient;
import theking530.staticcore.init.StaticCoreRecipeSerializers;
import theking530.staticcore.init.StaticCoreRecipeTypes;

public class ThermalConductivityRecipe extends AbstractStaticPowerRecipe {

	public static final String ID = "thermal_conducitity";

	public static final Codec<ThermalConductivityRecipe> CODEC = RecordCodecBuilder.create(instance -> instance
			.group(ResourceLocation.CODEC.optionalFieldOf("id", null).forGetter(recipe -> recipe.getId()),
					BlockStateIngredient.CODEC.optionalFieldOf("block", BlockStateIngredient.EMPTY)
							.forGetter(recipe -> recipe.getBlocks()),
					FluidIngredient.CODEC.optionalFieldOf("fluid", FluidIngredient.EMPTY)
							.forGetter(recipe -> recipe.getFluids()),

					Codec.BOOL.optionalFieldOf("has_active_temperature", false)
							.forGetter(recipe -> recipe.hasActiveTemperature()),
					Codec.FLOAT.optionalFieldOf("temperature", 0.0f).forGetter(recipe -> recipe.getTemperature()),
					Codec.FLOAT.optionalFieldOf("thermalMass", 0.0f).forGetter(recipe -> recipe.getThermalMass()),
					Codec.FLOAT.fieldOf("conductivity").forGetter(recipe -> recipe.getConductivity()),

					OverheatingBehaviour.CODEC.optionalFieldOf("overheating_behaviour")
							.forGetter(recipe -> Optional.ofNullable(recipe.getOverheatingBehaviour())),
					FreezingBehaviour.CODEC.optionalFieldOf("freezing_behaviour")
							.forGetter(recipe -> Optional.ofNullable(recipe.getFreezingBehaviour())))
			.apply(instance, (id, blocks, fluids, hasActiveTemp, temp, mass, conductivity, overheatingBehaviour,
					freezingBehaviour) -> {
				return new ThermalConductivityRecipe(id, blocks, fluids, hasActiveTemp, temp, mass, conductivity,
						overheatingBehaviour.isPresent() ? overheatingBehaviour.get() : null,
						freezingBehaviour.isPresent() ? freezingBehaviour.get() : null);
			}));

	private final BlockStateIngredient blocks;
	private final FluidIngredient fluids;

	private final float thermalMass;
	private final float temperature;
	private final boolean hasActiveTemperature;
	private final float conductivity;

	private final OverheatingBehaviour overheatingBehaviour;
	private final FreezingBehaviour freezingBehaviour;

	public ThermalConductivityRecipe(ResourceLocation id, BlockStateIngredient blocks, FluidIngredient fluids,
			boolean hasActiveTemperature, float temperature, float thermalMass, float conductivity,
			OverheatingBehaviour overheatingBehaviour, FreezingBehaviour freezingBehaviour) {
		super(id);
		this.blocks = blocks;
		this.fluids = fluids;
		this.temperature = temperature;
		this.hasActiveTemperature = hasActiveTemperature;
		this.thermalMass = thermalMass;
		this.conductivity = conductivity;
		this.overheatingBehaviour = overheatingBehaviour;
		this.freezingBehaviour = freezingBehaviour;
	}

	public float getThermalMass() {
		return thermalMass;
	}

	public float getTemperature() {
		return temperature;
	}

	public boolean hasActiveTemperature() {
		return hasActiveTemperature;
	}

	public float getConductivity() {
		return conductivity;
	}

	public BlockStateIngredient getBlocks() {
		return blocks;
	}

	public FluidIngredient getFluids() {
		return fluids;
	}

	public OverheatingBehaviour getOverheatingBehaviour() {
		return overheatingBehaviour;
	}

	public boolean hasOverheatingBehaviour() {
		return overheatingBehaviour != null;
	}

	public FreezingBehaviour getFreezingBehaviour() {
		return freezingBehaviour;
	}

	public boolean hasFreezeBehaviour() {
		return freezingBehaviour != null;
	}

	@Override
	public boolean matches(RecipeMatchParameters matchParams, Level worldIn) {
		// Check for fluid match.
		if (!fluids.isEmpty() && matchParams.hasFluids() && !matchParams.getFluids()[0].isEmpty()) {
			if (fluids.test(matchParams.getFluids()[0])) {
				return true;
			}
		}

		// Check for block match.
		if (!blocks.isEmpty() && matchParams.hasBlocks()) {
			Block block = matchParams.getBlocks()[0].getBlock();
			if (blocks.test(block)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public RecipeSerializer<ThermalConductivityRecipe> getSerializer() {
		return StaticCoreRecipeSerializers.THERMAL_CONDUCTIVITY_SERIALIZER.get();
	}

	@Override
	public RecipeType<ThermalConductivityRecipe> getType() {
		return StaticCoreRecipeTypes.THERMAL_CONDUCTIVITY_RECIPE_TYPE.get();
	}
}
