package theking530.staticpower.data.crafting.wrappers.thermalconductivity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import theking530.staticpower.data.crafting.AbstractStaticPowerRecipe;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.RecipeMatchParameters;

public class ThermalConductivityRecipe extends AbstractStaticPowerRecipe {
	public static final IRecipeType<ThermalConductivityRecipe> RECIPE_TYPE = IRecipeType.register("thermal_conducitity");

	private final ResourceLocation[] blocks;
	private final ResourceLocation[] fluids;
	private final BlockState overheatedBlock;
	private final ProbabilityItemStackOutput overheatedItemStack;
	private final float overheatedTemperature;
	private final float thermalConductivity;
	private final float heatAmount;

	public ThermalConductivityRecipe(ResourceLocation name, ResourceLocation[] blocks, ResourceLocation[] fluids, BlockState overheatedBlock, ProbabilityItemStackOutput overheatedItemStack,
			float overheatedTemperature, float thermalConductivity, float heatAmount) {
		super(name);
		this.blocks = blocks;
		this.fluids = fluids;
		this.thermalConductivity = thermalConductivity;
		this.heatAmount = heatAmount;
		this.overheatedBlock = overheatedBlock;
		this.overheatedItemStack = overheatedItemStack;
		this.overheatedTemperature = overheatedTemperature;
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
		return overheatedBlock != Blocks.VOID_AIR.getDefaultState();
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
		return overheatedTemperature != 0;
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
				fluid = matchParams.getBlocks()[0].getFluidState().getFluid();
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
				if (block.getRegistryName().equals(blockTag) || block.getTags().contains(blockTag)) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return ThermalConductivityRecipeSerializer.INSTANCE;
	}

	@Override
	public IRecipeType<?> getType() {
		return RECIPE_TYPE;
	}
}
