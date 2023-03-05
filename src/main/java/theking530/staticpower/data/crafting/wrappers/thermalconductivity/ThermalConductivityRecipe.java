package theking530.staticpower.data.crafting.wrappers.thermalconductivity;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;
import theking530.staticpower.data.crafting.AbstractStaticPowerRecipe;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerOutputItem;
import theking530.staticpower.init.ModRecipeSerializers;
import theking530.staticpower.init.ModRecipeTypes;
import theking530.staticpower.init.tags.ModBlockTags;

public class ThermalConductivityRecipe extends AbstractStaticPowerRecipe {
	public static final String ID = "thermal_conducitity";

	public static final Codec<ThermalConductivityRecipe> CODEC = RecordCodecBuilder
			.create(instance -> instance.group(ResourceLocation.CODEC.optionalFieldOf("id", null).forGetter(recipe -> recipe.getId()),
					ResourceLocation.CODEC.listOf().fieldOf("blocks").forGetter(recipe -> recipe.getBlockTags()),
					ResourceLocation.CODEC.listOf().fieldOf("fluids").forGetter(recipe -> recipe.getFluidTags()),

					Codec.INT.fieldOf("overheat_temperature").forGetter(recipe -> recipe.getOverheatedTemperature()),
					BlockState.CODEC.fieldOf("overheated_block").forGetter(recipe -> recipe.getOverheatedBlock()),
					StaticPowerOutputItem.CODEC.fieldOf("overheated_item").forGetter(recipe -> recipe.getOverheatedItem()),

					Codec.INT.fieldOf("freezing_temperature").forGetter(recipe -> recipe.getFreezingTemperature()),
					BlockState.CODEC.fieldOf("frozen_block").forGetter(recipe -> recipe.getFreezingBlock()),
					StaticPowerOutputItem.CODEC.fieldOf("frozen_item").forGetter(recipe -> recipe.getFreezingItem()),

					Codec.INT.fieldOf("temperature").forGetter(recipe -> recipe.getTemperature()),
					Codec.BOOL.fieldOf("has_active_temperature").forGetter(recipe -> recipe.hasActiveTemperature()),

					Codec.FLOAT.fieldOf("conductivity").forGetter(recipe -> recipe.getConductivity())).apply(instance, ThermalConductivityRecipe::new));

	private final List<ResourceLocation> blocks;
	private final List<ResourceLocation> fluids;

	private final int overheatTemperature;
	private final BlockState overheatedBlock;
	private final StaticPowerOutputItem overheatedItemStack;

	private final int freezingTemperature;
	private final BlockState freezingBlock;
	private final StaticPowerOutputItem freezingItemStack;

	private final int temperature;
	private final boolean hasActiveTemperature;

	private final float conductivity;
	private final boolean isAirRecipe;

	public ThermalConductivityRecipe(ResourceLocation name, List<ResourceLocation> blocks, List<ResourceLocation> fluids, int overheatTemperature, BlockState overheatedBlock,
			StaticPowerOutputItem overheatedItemStack, int freezingTemperature, BlockState freezingBlock, StaticPowerOutputItem freezingItemStack, int temperature,
			boolean hasActiveTemperature, float conductivity) {
		super(name);
		this.blocks = blocks;
		this.fluids = fluids;
		this.overheatTemperature = overheatTemperature;
		this.overheatedBlock = overheatedBlock;
		this.overheatedItemStack = overheatedItemStack;
		this.freezingTemperature = freezingTemperature;
		this.freezingBlock = freezingBlock;
		this.freezingItemStack = freezingItemStack;
		this.temperature = temperature;
		this.hasActiveTemperature = hasActiveTemperature;
		this.conductivity = conductivity;

		boolean flagIsAirRecipe = false;
		for (ResourceLocation loc : blocks) {
			if (loc.toString().equals("minecraft:air")) {
				flagIsAirRecipe = true;
				break;
			}
		}
		this.isAirRecipe = flagIsAirRecipe;
	}

	public int getTemperature() {
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

	public List<ResourceLocation> getBlockTags() {
		return blocks;
	}

	public List<ResourceLocation> getFluidTags() {
		return fluids;
	}

	public BlockState getOverheatedBlock() {
		return overheatedBlock;
	}

	public boolean hasOverheatedBlock() {
		return overheatedBlock != Blocks.VOID_AIR.defaultBlockState();
	}

	public StaticPowerOutputItem getOverheatedItem() {
		return overheatedItemStack;
	}

	public boolean hasOverheatedItem() {
		return !overheatedItemStack.isEmpty();
	}

	public int getOverheatedTemperature() {
		return overheatTemperature;
	}

	public boolean hasOverheatingBehaviour() {
		return hasOverheatedBlock() || hasOverheatedItem();
	}

	public BlockState getFreezingBlock() {
		return freezingBlock;
	}

	public boolean hasFreezingBlock() {
		return freezingBlock != Blocks.VOID_AIR.defaultBlockState();
	}

	public StaticPowerOutputItem getFreezingItem() {
		return freezingItemStack;
	}

	public boolean hasFreezingItem() {
		return !freezingItemStack.isEmpty();
	}

	public int getFreezingTemperature() {
		return freezingTemperature;
	}

	public boolean hasFreezeBehaviour() {
		return hasFreezingBlock() || hasFreezingItem();
	}

	@Override
	public boolean isValid(RecipeMatchParameters matchParams) {
		// Check for fluid match.
		if (fluids != null && fluids.size() > 0) {
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
				for (ResourceLocation fluidTag : fluids) {
					if (ForgeRegistries.FLUIDS.getKey(fluid).equals(fluidTag)) {
						return true;
					}
				}
			}
		}

		// Check for block match.
		if (blocks != null && blocks.size() > 0 && matchParams.hasBlocks()) {
			Block block = matchParams.getBlocks()[0].getBlock();
			for (ResourceLocation blockTag : blocks) {
				TagKey<Block> tag = ForgeRegistries.BLOCKS.tags().createTagKey(blockTag);
				if (ForgeRegistries.BLOCKS.getKey(block).equals(blockTag) || ModBlockTags.matches(tag, block)) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public RecipeSerializer<ThermalConductivityRecipe> getSerializer() {
		return ModRecipeSerializers.THERMAL_CONDUCTIVITY_SERIALIZER.get();
	}

	@Override
	public RecipeType<ThermalConductivityRecipe> getType() {
		return ModRecipeTypes.THERMAL_CONDUCTIVITY_RECIPE_TYPE.get();
	}
}
