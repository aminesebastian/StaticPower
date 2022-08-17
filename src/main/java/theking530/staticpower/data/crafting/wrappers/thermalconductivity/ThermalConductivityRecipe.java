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
import theking530.staticpower.init.ModTags;

public class ThermalConductivityRecipe extends AbstractStaticPowerRecipe {
	public static final RecipeType<ThermalConductivityRecipe> RECIPE_TYPE = RecipeType.register("thermal_conducitity");

	private final ResourceLocation[] blocks;
	private final ResourceLocation[] fluids;
	private final BlockState overheatedBlock;
	private final ProbabilityItemStackOutput overheatedItemStack;
	private final float overheatedTemperature;
	private final float thermalConductivity;
	private final float heatAmount;
	private final boolean isAirRecipe;

	public ThermalConductivityRecipe(ResourceLocation name, ResourceLocation[] blocks, ResourceLocation[] fluids,
			BlockState overheatedBlock, ProbabilityItemStackOutput overheatedItemStack, float overheatedTemperature,
			float thermalConductivity, float heatAmount) {
		super(name);
		this.blocks = blocks;
		this.fluids = fluids;
		this.thermalConductivity = thermalConductivity;
		this.heatAmount = heatAmount;
		this.overheatedBlock = overheatedBlock;
		this.overheatedItemStack = overheatedItemStack;
		this.overheatedTemperature = overheatedTemperature;

		boolean flagIsAirRecipe = false;
		for (ResourceLocation loc : blocks) {
			if (loc.toString().equals("minecraft:air")) {
				flagIsAirRecipe = true;
				break;
			}
		}
		this.isAirRecipe = flagIsAirRecipe;
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

	public float getThermalConductivity() {
		return thermalConductivity;
	}

	public float getHeatAmount() {
		return heatAmount;
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

	public float getOverheatedTemperature() {
		return overheatedTemperature;
	}

	public boolean hasOverheatingBehaviour() {
		return hasOverheatedBlock() || hasOverheatedItem();
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
					if (fluid.getRegistryName().equals(fluidTag)) {
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
				if (block.getRegistryName().equals(blockTag) || ModTags.tagContainsBlock(tag, block)) {
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
