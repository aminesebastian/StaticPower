package theking530.staticpower.tileentities.components.heat;

import java.util.LinkedList;
import java.util.Queue;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILightReader;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerRecipeRegistry;
import theking530.staticpower.data.crafting.wrappers.thermalconductivity.ThermalConductivityRecipe;

public class HeatStorage implements IHeatStorage, INBTSerializable<CompoundNBT> {
	public static final int MAXIMUM_IO_CAPTURE_FRAMES = 5;
	protected float currentHeat;
	protected float maximumHeat;
	protected float maxTransferRate;

	protected boolean canHeat;
	protected boolean canCool;

	protected Queue<Float> ioCaptureFrames;
	protected Queue<Float> receiveCaptureFrames;
	protected Queue<Float> extractCaptureFrames;
	protected float currentFrameEnergyReceived;
	protected float currentFrameEnergyExtracted;
	protected float averageRecieved;
	protected float averageExtracted;

	public HeatStorage(float maximumHeat, float heatTransferRate) {
		this.maximumHeat = maximumHeat;
		this.maxTransferRate = heatTransferRate;
		canHeat = true;
		canCool = true;
		ioCaptureFrames = new LinkedList<Float>();
		receiveCaptureFrames = new LinkedList<Float>();
		extractCaptureFrames = new LinkedList<Float>();
	}

	@Override
	public float getCurrentHeat() {
		return currentHeat;
	}

	@Override
	public float getMaximumHeat() {
		return maximumHeat;
	}

	public void setMaximumHeat(float newMax) {
		maximumHeat = newMax;
		currentHeat = Math.min(maximumHeat, currentHeat);
	}

	@Override
	public float getMaximumHeatTransferRate() {
		return maxTransferRate;
	}

	@Override
	public float heat(float amountToHeat, boolean simulate) {
		if (!canHeat) {
			return 0.0f;
		}
		float clampedToTransferRate = Math.min(maxTransferRate, amountToHeat);
		float remainingHeatCapacity = maximumHeat - currentHeat;
		float actualHeatAmount = Math.min(remainingHeatCapacity, clampedToTransferRate);
		if (!simulate) {
			currentHeat += actualHeatAmount;
			currentFrameEnergyReceived += actualHeatAmount;
		}

		return actualHeatAmount;
	}

	@Override
	public float cool(float amountToCool, boolean simulate) {
		if (!canCool) {
			return 0.0f;
		}

		float clampedToTransferRate = Math.min(maxTransferRate, amountToCool);
		float actualCoolAmount = Math.min(currentHeat, clampedToTransferRate);
		if (!simulate) {
			currentHeat -= actualCoolAmount;
			currentFrameEnergyExtracted -= actualCoolAmount;
		}
		return actualCoolAmount;
	}

	public void addHeatIgnoreTransferRate(float heat) {
		float remainingHeatCapacity = maximumHeat - currentHeat;
		float actualHeatAmount = Math.min(remainingHeatCapacity, heat);
		
		currentHeat += actualHeatAmount;
		currentFrameEnergyReceived += actualHeatAmount;
	}

	public void coolIgnoreTransferRate(float cool) {
		float actualCoolAmount = Math.min(currentHeat, cool);
		
		currentHeat -= actualCoolAmount;
		currentFrameEnergyExtracted -= actualCoolAmount;
	}

	public boolean isAtMaxHeat() {
		return currentHeat == maximumHeat;
	}

	public boolean isEmpty() {
		return currentHeat == 0.0f;
	}

	public boolean canFullyAbsorbHeat(float heatAmount) {
		return currentHeat + heatAmount <= maximumHeat;
	}

	public boolean isCanHeat() {
		return canHeat;
	}

	public void setCanHeat(boolean canHeat) {
		this.canHeat = canHeat;
	}

	public boolean isCanCool() {
		return canCool;
	}

	public void setCanCool(boolean canCool) {
		this.canCool = canCool;
	}

	/**
	 * Caches the current heat IO metric and starts capturing a new one. This should
	 * be called once per tick.
	 */
	public void captureHeatTransferMetric() {
		// IO Capture
		float tranfered = currentFrameEnergyReceived + currentFrameEnergyExtracted;
		ioCaptureFrames.add(tranfered);
		if (ioCaptureFrames.size() > MAXIMUM_IO_CAPTURE_FRAMES) {
			ioCaptureFrames.poll();
		}

		// Capture Received Amounts
		receiveCaptureFrames.add(currentFrameEnergyReceived);
		if (receiveCaptureFrames.size() > MAXIMUM_IO_CAPTURE_FRAMES) {
			receiveCaptureFrames.poll();
		}

		// Capture Extracted Amounts
		extractCaptureFrames.add(currentFrameEnergyExtracted);
		if (extractCaptureFrames.size() > MAXIMUM_IO_CAPTURE_FRAMES) {
			extractCaptureFrames.poll();
		}

		// Cache the average extracted rate.
		averageExtracted = 0;
		for (float value : extractCaptureFrames) {
			averageExtracted += value;
		}
		averageExtracted /= Math.max(1, extractCaptureFrames.size());

		// Cache the average recieved rate.
		averageRecieved = 0;
		for (float value : receiveCaptureFrames) {
			averageRecieved += value;
		}
		averageRecieved /= Math.max(1, receiveCaptureFrames.size());

		// Reset the values.
		currentFrameEnergyReceived = 0;
		currentFrameEnergyExtracted = 0;
	}

	/**
	 * Gets the average heat IO for this storage over the last
	 * {@link #MAXIMUM_IO_CAPTURE_FRAMES} calls to {@link #captureEnergyMetric()}.
	 * 
	 * @return
	 */
	public float getHeatIO() {
		return averageExtracted + averageRecieved;
	}

	/**
	 * Gets the average extracted heat per tick for this storage over the last
	 * {@link #MAXIMUM_IO_CAPTURE_FRAMES} calls to {@link #captureEnergyMetric()}.
	 * 
	 * @return
	 */
	public float getCooledPerTick() {
		return averageExtracted;
	}

	/**
	 * Gets the average received heat per tick for this storage over the last
	 * {@link #MAXIMUM_IO_CAPTURE_FRAMES} calls to {@link #captureEnergyMetric()}.
	 * 
	 * @return
	 */
	public float getHeatPerTick() {
		return averageRecieved;
	}

	/**
	 * This is a helper method that returns the min between the amount of heat
	 * stored in this storage and the maximum amount that can be output per tick.
	 * For example, if our max extract is 4H/t and we have 2H left in this storage,
	 * this will return 2. Otherwise, if we have >4FE left in this storage, this
	 * will return 4FE.
	 * 
	 * @return The amount of heat that can be output by this storage on this tick.
	 */
	public float getCurrentMaximumHeatOutput() {
		return Math.min(currentHeat, maxTransferRate);
	}

	/**
	 * Transfers the heat stored in this storage to adjacent blocks.
	 * 
	 * @param reader            The world access.
	 * @param currentPos        The position of this heat storage.
	 * @param thermalMultiplier The thermal multiplier. This value should be 1.0f by
	 *                          default but can increase or decrease depending on if
	 *                          the owner of this heat storage is more or less
	 *                          conductive.
	 */
	public void transferWithSurroundings(ILightReader reader, BlockPos currentPos, float thermalMultiplier) {
		// Cool the storage off using the surrounding blocks. If the surrounding is a
		// regular block, just dissipate the heat. Otherwise, transfer it if there is a
		// capable tile entity.
		for (Direction dir : Direction.values()) {
			TileEntity te = reader.getTileEntity(currentPos.offset(dir));
			if (te != null && te.getCapability(CapabilityHeatable.HEAT_STORAGE_CAPABILITY, dir.getOpposite()).isPresent()) {
				te.getCapability(CapabilityHeatable.HEAT_STORAGE_CAPABILITY, dir.getOpposite()).ifPresent(capability -> {
					float coolable = getCurrentMaximumHeatOutput();
					float cooled = capability.heat(coolable, false);
					cool(cooled, false);
				});
			} else {
				IFluidState fluidState = reader.getFluidState(currentPos.offset(dir));
				BlockState blockstate = reader.getBlockState(currentPos.offset(dir));
				StaticPowerRecipeRegistry
						.getRecipe(ThermalConductivityRecipe.RECIPE_TYPE, new RecipeMatchParameters(new ItemStack(blockstate.getBlock())).setFluids(new FluidStack(fluidState.getFluid(), 1)))
						.ifPresent((recipe) -> {
							cool(recipe.getThermalConductivity() * thermalMultiplier, false);
						});
			}
		}
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		if (currentHeat > maximumHeat) {
			currentHeat = maximumHeat;
		}

		currentHeat = nbt.getFloat("current_heat");
		maximumHeat = nbt.getFloat("maximum_heat");
		maxTransferRate = nbt.getFloat("maximum_transfer_rate");
		averageRecieved = nbt.getFloat("recieved");
		averageExtracted = nbt.getFloat("extracted");
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT output = new CompoundNBT();

		if (currentHeat < 0) {
			currentHeat = 0;
		}

		output.putFloat("current_heat", currentHeat);
		output.putFloat("maximum_heat", maximumHeat);
		output.putFloat("maximum_transfer_rate", maxTransferRate);
		output.putFloat("recieved", averageRecieved);
		output.putFloat("extracted", averageExtracted);
		return output;
	}
}
