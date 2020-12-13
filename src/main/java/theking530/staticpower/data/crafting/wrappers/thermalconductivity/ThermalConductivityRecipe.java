package theking530.staticpower.data.crafting.wrappers.thermalconductivity;

import net.minecraft.block.Block;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.data.crafting.AbstractStaticPowerRecipe;
import theking530.staticpower.data.crafting.RecipeMatchParameters;

public class ThermalConductivityRecipe extends AbstractStaticPowerRecipe {
	public static final IRecipeType<ThermalConductivityRecipe> RECIPE_TYPE = IRecipeType.register("thermal_conducitity");

	private final ResourceLocation[] blocks;
	private final FluidStack fluid;
	private final float thermalConductivity;
	private final float heatAmount;

	public ThermalConductivityRecipe(ResourceLocation name, ResourceLocation[] blocks, FluidStack fluid, float thermalConductivity, float heatAmount) {
		super(name);
		this.blocks = blocks;
		this.fluid = fluid;
		this.thermalConductivity = thermalConductivity;
		this.heatAmount = heatAmount;
	}

	public ResourceLocation[] getBlockTags() {
		return blocks;
	}

	public FluidStack getFluid() {
		return fluid;
	}

	public float getThermalConductivity() {
		return thermalConductivity;
	}

	public float getHeatAmount() {
		return heatAmount;
	}

	@Override
	public boolean isValid(RecipeMatchParameters matchParams) {
		// Check for block match first.
		if (blocks != null && blocks.length > 0 && matchParams.hasBlocks()) {
			Block block = matchParams.getBlocks()[0].getBlock();
			for (ResourceLocation blockTag : blocks) {
				//System.out.println(blockTag + "    " + block.getRegistryName());
				if (block.getRegistryName().equals(blockTag) || block.getTags().contains(blockTag)) {
					return true;
				}
			}
		}

		if (matchParams.hasBlocks()) {
			//System.out.println(matchParams.getBlocks()[0].getBlock().getRegistryName());
		}

		// Then check for a fluid match.
		if (!fluid.isEmpty() && matchParams.hasFluids() && fluid.isFluidEqual(matchParams.getFluids()[0])) {
			return true;
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
