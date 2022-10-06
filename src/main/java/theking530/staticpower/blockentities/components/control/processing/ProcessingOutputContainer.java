package theking530.staticpower.blockentities.components.control.processing;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.utilities.NBTUtilities;

public class ProcessingOutputContainer implements INBTSerializable<CompoundTag> {
	private final List<ItemStack> inputItems;
	private final List<ItemStack> outputItems;
	private final List<FluidStack> inputFluids;
	private final List<FluidStack> outputFluids;
	@Nullable
	private ResourceLocation recipeId;
	private double inputPower;
	private double outputPower;
	private boolean closed;
	private CompoundTag customParameters;

	public ProcessingOutputContainer() {
		inputItems = new LinkedList<>();
		outputItems = new LinkedList<>();
		inputFluids = new LinkedList<>();
		outputFluids = new LinkedList<>();
		inputPower = 0;
		outputPower = 0;
		recipeId = null;
		closed = false;
		customParameters = new CompoundTag();
	}

	public ResourceLocation getRecipe() {
		return recipeId;
	}

	public double getInputPower() {
		return inputPower;
	}

	public void setInputPower(double inputPower) {
		if (isClosed()) {
			throw new RuntimeException(String.format("Attempted to set the input power (%1$f) for a closed process output container.", inputPower));
		}
		this.inputPower = inputPower;
	}

	public double getOutputPower() {
		return outputPower;
	}

	public void setOutputPower(double outputPower) {
		if (isClosed()) {
			throw new RuntimeException(String.format("Attempted to set the output power (%1$f) for a closed process output container.", outputPower));
		}
		this.outputPower = outputPower;
	}

	public ProcessingOutputContainer addInputItem(ItemStack item) {
		if (isClosed()) {
			throw new RuntimeException(String.format("Attempted to add an input item to a clowed process output container."));
		}

		if (!item.isEmpty()) {
			inputItems.add(item.copy());
		}
		return this;
	}

	public ProcessingOutputContainer addInputItem(ItemStack item, int amount) {
		item.setCount(amount);
		return addInputItem(item);
	}

	public List<ItemStack> getInputItems() {
		return inputItems;
	}

	public ItemStack getInputItem(int index) {
		return inputItems.get(index);
	}

	public boolean hasInputItems() {
		return inputItems.size() > 0;
	}

	public ProcessingOutputContainer addOutputItem(ItemStack item) {
		if (isClosed()) {
			throw new RuntimeException(String.format("Attempted to add an output item to a clowed process output container."));
		}
		if (!item.isEmpty()) {
			outputItems.add(item.copy());
		}
		return this;
	}

	public ProcessingOutputContainer addOutputItem(ItemStack item, int amount) {
		item.setCount(amount);
		return addOutputItem(item);
	}

	public List<ItemStack> getOutputItems() {
		return outputItems;
	}

	public ItemStack getOutputItem(int index) {
		return outputItems.get(index);
	}

	public boolean hasOutputItems() {
		return outputItems.size() > 0;
	}

	public ProcessingOutputContainer addInputFluid(FluidStack fluid) {
		if (isClosed()) {
			throw new RuntimeException(String.format("Attempted to add an input fluid to a clowed process output container."));
		}
		if (!fluid.isEmpty()) {
			inputFluids.add(fluid.copy());
		}
		return this;
	}

	public ProcessingOutputContainer addInputFluid(FluidStack fluid, int amount) {
		fluid.setAmount(amount);
		return addInputFluid(fluid);
	}

	public List<FluidStack> getInputFluids() {
		return inputFluids;
	}

	public FluidStack getInputFluid(int index) {
		return inputFluids.get(index);
	}

	public boolean hasInputFluids() {
		return inputFluids.size() > 0;
	}

	public ProcessingOutputContainer addOutputFluid(FluidStack fluid) {
		if (isClosed()) {
			throw new RuntimeException(String.format("Attempted to add an output fluid to a clowed process output container."));
		}
		if (!fluid.isEmpty()) {
			outputFluids.add(fluid.copy());
		}
		return this;
	}

	public ProcessingOutputContainer addOutputFluid(FluidStack fluid, int amount) {
		fluid.setAmount(amount);
		return addOutputFluid(fluid);
	}

	public List<FluidStack> getOutputFluids() {
		return outputFluids;
	}

	public FluidStack getOutputFluid(int index) {
		return outputFluids.get(index);
	}

	public boolean hasOutputFluids() {
		return outputFluids.size() > 0;
	}

	public CompoundTag getCustomParameterContainer() {
		return this.customParameters;
	}

	public void open(@Nullable ResourceLocation recipeId) {
		clear();
		closed = false;
		this.recipeId = recipeId;
	}

	public void close() {
		closed = true;
	}

	public boolean isClosed() {
		return closed;
	}

	public void clear() {
		recipeId = null;
		inputItems.clear();
		outputItems.clear();
		inputFluids.clear();
		outputFluids.clear();
		inputPower = 0;
		outputPower = 0;
		closed = true;
		customParameters = new CompoundTag();
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag output = new CompoundTag();
		if (recipeId != null) {
			output.putString("recipe", recipeId.toString());
		}

		output.putDouble("input_power", inputPower);
		output.putDouble("output_power", outputPower);
		output.put("custom_data", customParameters);

		ListTag serializedInputItems = NBTUtilities.serialize(inputItems, (item) -> {
			return item.serializeNBT();
		});
		output.put("input_items", serializedInputItems);

		ListTag serializedOutputItems = NBTUtilities.serialize(outputItems, (item) -> {
			return item.serializeNBT();
		});
		output.put("output_items", serializedOutputItems);

		ListTag serializedInputFluids = NBTUtilities.serialize(inputFluids, (fluid, tag) -> {
			fluid.writeToNBT(tag);
		});
		output.put("input_fluids", serializedInputFluids);

		ListTag serializedOutputFluids = NBTUtilities.serialize(outputFluids, (fluid, tag) -> {
			fluid.writeToNBT(tag);
		});
		output.put("output_fluids", serializedOutputFluids);

		return output;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		clear();
		if (nbt.contains("recipe")) {
			recipeId = new ResourceLocation(nbt.getString("recipe"));
		}

		inputPower = nbt.getDouble("input_power");
		outputPower = nbt.getDouble("output_power");
		customParameters = nbt.getCompound("custom_data");

		inputItems.addAll(NBTUtilities.deserialize(nbt.getList("input_items", ListTag.TAG_COMPOUND), (tag) -> {
			return ItemStack.of((CompoundTag) tag);
		}));
		outputItems.addAll(NBTUtilities.deserialize(nbt.getList("output_items", ListTag.TAG_COMPOUND), (tag) -> {
			return ItemStack.of((CompoundTag) tag);
		}));
		inputFluids.addAll(NBTUtilities.deserialize(nbt.getList("input_fluids", ListTag.TAG_COMPOUND), (tag) -> {
			return FluidStack.loadFluidStackFromNBT((CompoundTag) tag);
		}));
		outputFluids.addAll(NBTUtilities.deserialize(nbt.getList("output_fluids", ListTag.TAG_COMPOUND), (tag) -> {
			return FluidStack.loadFluidStackFromNBT((CompoundTag) tag);
		}));
	}
}
