package theking530.staticpower.data.crafting.wrappers.thermalconductivity;

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
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeType;
import theking530.staticpower.init.ModTags;

public class ThermalConductivityRecipe extends AbstractStaticPowerRecipe {
	public static final String ID = "thermal_conducitity";
	public static final RecipeType<ThermalConductivityRecipe> RECIPE_TYPE = new StaticPowerRecipeType<ThermalConductivityRecipe>();

	private final ResourceLocation[] blocks;
	private final ResourceLocation[] fluids;

	private final int overheatTemperature;
	private final BlockState overheatedBlock;
	private final ProbabilityItemStackOutput overheatedItemStack;

	private final int freezingTemperature;
	private final BlockState freezingBlock;
	private final ProbabilityItemStackOutput freezingItemStack;

	private final int temperature;
	private final boolean hasActiveTemperature;

	private final float conductivity;
	private final boolean isAirRecipe;

	public ThermalConductivityRecipe(ResourceLocation name, ResourceLocation[] blocks, ResourceLocation[] fluids, int overheatTemperature, BlockState overheatedBlock,
			ProbabilityItemStackOutput overheatedItemStack, int freezingTemperature, BlockState freezingBlock, ProbabilityItemStackOutput freezingItemStack, int temperature,
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

	public ResourceLocation[] getBlockTags() {
		return blocks;
	}

	public ResourceLocation[] getFluidTags() {
		return fluids;
	}

	public BlockState getOverheatedBlock() {
		return overheatedBlock;
	}

	public boolean hasOverheatedBlock() {
		return overheatedBlock != Blocks.VOID_AIR.defaultBlockState();
	}

	public ProbabilityItemStackOutput getOverheatedItem() {
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

	public ProbabilityItemStackOutput getFreezingItem() {
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
		if (fluids != null && fluids.length > 0) {
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
		if (blocks != null && blocks.length > 0 && matchParams.hasBlocks()) {
			Block block = matchParams.getBlocks()[0].getBlock();
			for (ResourceLocation blockTag : blocks) {
				TagKey<Block> tag = ForgeRegistries.BLOCKS.tags().createTagKey(blockTag);
				if (ForgeRegistries.BLOCKS.getKey(block).equals(blockTag) || ModTags.tagContainsBlock(tag, block)) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return ThermalConductivityRecipeSerializer.INSTANCE;
	}

	@Override
	public RecipeType<?> getType() {
		return RECIPE_TYPE;
	}
}
