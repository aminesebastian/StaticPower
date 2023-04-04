package theking530.staticcore.crafting.thermal;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.crafting.AbstractStaticPowerRecipe;
import theking530.staticcore.crafting.RecipeMatchParameters;
import theking530.staticcore.crafting.thermal.ThermalConductivityBehaviours.FreezingBehaviour;
import theking530.staticcore.crafting.thermal.ThermalConductivityBehaviours.OverheatingBehaviour;
import theking530.staticcore.fluid.FluidIngredient;
import theking530.staticcore.init.StaticCoreRecipeSerializers;
import theking530.staticcore.init.StaticCoreRecipeTypes;
import theking530.staticcore.utilities.JsonUtilities;

public class ThermalConductivityRecipe extends AbstractStaticPowerRecipe {

	public static final String ID = "thermal_conducitity";

	public static final Codec<ThermalConductivityRecipe> CODEC = RecordCodecBuilder.create(instance -> instance
			.group(ResourceLocation.CODEC.optionalFieldOf("id", null).forGetter(recipe -> recipe.getId()),
					JsonUtilities.INGREDIENT_CODEC.optionalFieldOf("blocks", Ingredient.EMPTY)
							.forGetter(recipe -> recipe.getBlocks()),
					FluidIngredient.CODEC.optionalFieldOf("fluids", FluidIngredient.EMPTY)
							.forGetter(recipe -> recipe.getFluids()),

					Codec.BOOL.optionalFieldOf("has_active_temperature", false)
							.forGetter(recipe -> recipe.hasActiveTemperature()),
					Codec.FLOAT.optionalFieldOf("temperature", 0.0f).forGetter(recipe -> recipe.getTemperature()),
					Codec.FLOAT.fieldOf("conductivity").forGetter(recipe -> recipe.getConductivity()),

					OverheatingBehaviour.CODEC.optionalFieldOf("overheating_behaviour")
							.forGetter(recipe -> Optional.ofNullable(recipe.getOverheatingBehaviour())),
					FreezingBehaviour.CODEC.optionalFieldOf("freezing_behaviour")
							.forGetter(recipe -> Optional.ofNullable(recipe.getFreezingBehaviour())))
			.apply(instance, (id, blocks, fluids, hasActiveTemp, temp, conductivity, overheatingBehaviour,
					freezingBehaviour) -> {
				return new ThermalConductivityRecipe(id, blocks, fluids, hasActiveTemp, temp, conductivity,
						overheatingBehaviour.isPresent() ? overheatingBehaviour.get() : null,
						freezingBehaviour.isPresent() ? freezingBehaviour.get() : null);
			}));

	private final Ingredient blocks;
	private final FluidIngredient fluids;

	private final float temperature;
	private final boolean hasActiveTemperature;
	private final float conductivity;
	private final boolean isAirRecipe;

	private final OverheatingBehaviour overheatingBehaviour;
	private final FreezingBehaviour freezingBehaviour;

	public ThermalConductivityRecipe(ResourceLocation id, Ingredient blocks, FluidIngredient fluids,
			boolean hasActiveTemperature, float temperature, float conductivity,
			OverheatingBehaviour overheatingBehaviour, FreezingBehaviour freezingBehaviour) {
		super(id);
		this.blocks = blocks;
		this.fluids = fluids;
		this.temperature = temperature;
		this.hasActiveTemperature = hasActiveTemperature;
		this.conductivity = conductivity;
		this.overheatingBehaviour = overheatingBehaviour;
		this.freezingBehaviour = freezingBehaviour;
		this.isAirRecipe = fluids.isEmpty() && blocks.isEmpty();
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

	public boolean isAirRecipe() {
		return isAirRecipe;
	}

	public Ingredient getBlocks() {
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
		if (!fluids.isEmpty()) {
			// Allocate the fluid.
			Fluid fluid = null;

			// Match by fluidstate or by fluid.
			if (matchParams.hasFluids()) {
				fluid = matchParams.getFluids()[0].getFluid();
			} else if (matchParams.hasBlocks()) {
				fluid = matchParams.getBlocks()[0].getFluidState().getType();
			}

			// Check the fluid.
			if (fluid != null) {
				if (fluids.test(new FluidStack(fluid, 1))) {
					return true;
				}
			}
		}

		// Check for block match.
		if (blocks != null && !blocks.isEmpty() && matchParams.hasBlocks()) {
			Block block = matchParams.getBlocks()[0].getBlock();
			if (blocks.test(new ItemStack(block, 1))) {
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
