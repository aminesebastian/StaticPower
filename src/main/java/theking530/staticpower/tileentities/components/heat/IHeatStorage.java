package theking530.staticpower.tileentities.components.heat;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILightReader;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerRecipeRegistry;
import theking530.staticpower.data.crafting.wrappers.thermalconductivity.ThermalConductivityRecipe;

public interface IHeatStorage {
	/**
	 * Returns the amount of heat currently stored in this heatable entity.
	 * 
	 * @return
	 */
	public float getCurrentHeat();

	/**
	 * Returns the maximum amount of heat that can be stored in this heatable
	 * entity.
	 * 
	 * @return
	 */
	public float getMaximumHeat();

	/**
	 * Gets the maximum rate that this heatable entity can transfer thermal energy.
	 * 
	 * @return
	 */
	public float getConductivity();

	/**
	 * Adds heat to this heatable entity.
	 * 
	 * @param heatToRecieve
	 * @param simulate
	 * @return
	 */
	public float heat(float amountToHeat, boolean simulate);

	/**
	 * Cools down this heatable entity.
	 * 
	 * @param amountToCool
	 * @param simulate
	 * @return
	 */
	public float cool(float amountToCool, boolean simulate);

	/**
	 * Transfers the heat stored in this storage to adjacent blocks. The transfered
	 * amount is equal to the thermal conductivity of the adjacent block multiplied
	 * by the maximum heat transfer rate of this storage.
	 * 
	 * @param reader     The world access.
	 * @param currentPos The position of this heat storage.
	 */
	public default void transferWithSurroundings(ILightReader reader, BlockPos currentPos) {
		// Cool the storage off using the surrounding blocks. If the surrounding is a
		// regular block, just dissipate the heat. Otherwise, transfer it if there is a
		// capable tile entity.
		for (Direction dir : Direction.values()) {
			TileEntity te = reader.getTileEntity(currentPos.offset(dir));
			if (te != null && te.getCapability(CapabilityHeatable.HEAT_STORAGE_CAPABILITY, dir.getOpposite()).isPresent()) {
				te.getCapability(CapabilityHeatable.HEAT_STORAGE_CAPABILITY, dir.getOpposite()).ifPresent(capability -> {
					float cooled = capability.heat(getCurrentHeat(), false);
					cool(cooled, false);
				});
			} else {
				IFluidState fluidState = reader.getFluidState(currentPos.offset(dir));
				BlockState blockstate = reader.getBlockState(currentPos.offset(dir));
				StaticPowerRecipeRegistry
						.getRecipe(ThermalConductivityRecipe.RECIPE_TYPE, new RecipeMatchParameters(new ItemStack(blockstate.getBlock())).setFluids(new FluidStack(fluidState.getFluid(), 1)))
						.ifPresent((recipe) -> {
							cool(recipe.getThermalConductivity() * getConductivity(), false);
						});
			}
		}
	}
}