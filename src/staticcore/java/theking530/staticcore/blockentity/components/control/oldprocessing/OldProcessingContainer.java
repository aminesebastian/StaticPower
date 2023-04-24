package theking530.staticcore.blockentity.components.control.oldprocessing;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.blockentity.components.control.processing.ProcessingContainer.CaptureType;
import theking530.staticcore.utilities.NBTUtilities;

@Deprecated
public class OldProcessingContainer implements INBTSerializable<CompoundTag> {

	public record ProcessingItemWrapper(ItemStack item, CaptureType captureType, boolean isTemplateItem) {

		public static final ProcessingItemWrapper EMPTY = new ProcessingItemWrapper(ItemStack.EMPTY, CaptureType.NONE,
				true);
		public int getCount() {
			return item.getCount();
		}
	}

	public record ProcessingFluidWrapper(FluidStack fluid, CaptureType captureType, boolean isTemplateItem) {

		public static final ProcessingFluidWrapper EMPTY = new ProcessingFluidWrapper(FluidStack.EMPTY,
				CaptureType.NONE, true);
		public int getAmount() {
			return fluid.getAmount();
		}
	}

	private final List<ProcessingItemWrapper> inputItems;
	private final List<ProcessingItemWrapper> outputItems;
	private final List<ProcessingFluidWrapper> inputFluids;
	private final List<ProcessingFluidWrapper> outputFluids;
	@Nullable
	private ResourceLocation recipeId;
	private double inputPower;
	private double outputPower;
	private boolean closed;
	private CompoundTag customParameters;

	public OldProcessingContainer() {
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

	public boolean hasInputPower() {
		return inputPower > 0;
	}

	public double getInputPower() {
		return inputPower;
	}

	public void setInputPower(double inputPower) {
		if (isClosed()) {
			throw new RuntimeException(String.format(
					"Attempted to set the input power (%1$f) for a closed process output container.", inputPower));
		}
		this.inputPower = inputPower;
	}

	public boolean hasOutputPower() {
		return outputPower > 0;
	}

	public double getOutputPower() {
		return outputPower;
	}

	public void setOutputPower(double outputPower) {
		if (isClosed()) {
			throw new RuntimeException(String.format(
					"Attempted to set the output power (%1$f) for a closed process output container.", outputPower));
		}
		this.outputPower = outputPower;
	}

	public OldProcessingContainer addInputItem(ItemStack item, CaptureType captureType) {
		return addInputItem(item, captureType, false);
	}

	public OldProcessingContainer addInputItem(ItemStack item, CaptureType captureType, boolean isTemplateItem) {
		return addInputItem(item, item.getCount(), captureType, isTemplateItem);
	}

	public OldProcessingContainer addInputItem(ItemStack item, int amount, CaptureType captureType) {
		return addInputItem(item, amount, captureType);
	}

	public OldProcessingContainer addInputItem(ItemStack item, int amount, CaptureType captureType,
			boolean isTemplateItem) {
		if (isClosed()) {
			throw new RuntimeException(
					String.format("Attempted to add an input item to a clowed process output container."));
		}
		if (amount == 0) {
			return this;
		}
		// Don't check for empty items here because some recipes depend on empty items
		// to match shapes (eg: crafting).
		ItemStack itemCopy = item.copy();
		itemCopy.setCount(amount);
		inputItems.add(new ProcessingItemWrapper(itemCopy, captureType, isTemplateItem));
		return this;
	}

	public List<ProcessingItemWrapper> getInputItems() {
		return inputItems;
	}

	public ProcessingItemWrapper getInputItem(int index) {
		return inputItems.get(index);
	}

	public boolean hasInputItems() {
		return inputItems.size() > 0;
	}

	public OldProcessingContainer addOutputItem(ItemStack item, CaptureType captureType) {
		return addOutputItem(item, captureType, false);
	}

	public OldProcessingContainer addOutputItem(ItemStack item, CaptureType captureType, boolean isTemplateItem) {
		return addOutputItem(item, item.getCount(), captureType, isTemplateItem);
	}

	public OldProcessingContainer addOutputItem(ItemStack item, int amount, CaptureType captureType) {
		return addOutputItem(item, amount, captureType, false);
	}

	public OldProcessingContainer addOutputItem(ItemStack item, int amount, CaptureType captureType,
			boolean isTemplateItem) {
		if (isClosed()) {
			throw new RuntimeException(
					String.format("Attempted to add an output item to a clowed process output container."));
		}

		// Don't check for empty items here because some recipes depend on empty items
		// to match shapes (eg: crafting).
		ItemStack itemCopy = item.copy();
		itemCopy.setCount(amount);
		outputItems.add(new ProcessingItemWrapper(itemCopy, captureType, isTemplateItem));
		return this;
	}

	public List<ProcessingItemWrapper> getOutputItems() {
		return outputItems;
	}

	public ProcessingItemWrapper getOutputItem(int index) {
		return outputItems.get(index);
	}

	public boolean hasOutputItems() {
		return outputItems.size() > 0;
	}

	public OldProcessingContainer addInputFluid(FluidStack fluid, CaptureType captureType) {
		return addInputFluid(fluid, captureType, false);
	}

	public OldProcessingContainer addInputFluid(FluidStack fluid, CaptureType captureType, boolean isTemplateItem) {
		return addInputFluid(fluid, fluid.getAmount(), captureType, isTemplateItem);
	}

	public OldProcessingContainer addInputFluid(FluidStack fluid, int amount, CaptureType captureType) {
		return addInputFluid(fluid, amount, captureType, false);
	}

	public OldProcessingContainer addInputFluid(FluidStack fluid, int amount, CaptureType captureType,
			boolean isTemplateItem) {
		if (isClosed()) {
			throw new RuntimeException(
					String.format("Attempted to add an input fluid to a closed process output container."));
		}
		// Don't check for empty fluids here because some recipes depend on empty fluids
		// to match shapes (eg: refinery).
		if (!fluid.isEmpty()) {
			FluidStack fluidCopy = fluid.copy();
			fluidCopy.setAmount(amount);
			inputFluids.add(new ProcessingFluidWrapper(fluidCopy, captureType, isTemplateItem));
		}
		return this;
	}

	public List<ProcessingFluidWrapper> getInputFluids() {
		return inputFluids;
	}

	public ProcessingFluidWrapper getInputFluid(int index) {
		return inputFluids.get(index);
	}

	public boolean hasInputFluids() {
		return inputFluids.size() > 0;
	}

	public OldProcessingContainer addOutputFluid(FluidStack fluid, CaptureType captureType) {
		return addOutputFluid(fluid, captureType, false);
	}

	public OldProcessingContainer addOutputFluid(FluidStack fluid, CaptureType captureType, boolean isTemplateItem) {
		return addOutputFluid(fluid, fluid.getAmount(), captureType, isTemplateItem);
	}

	public OldProcessingContainer addOutputFluid(FluidStack fluid, int amount, CaptureType captureType) {
		return addOutputFluid(fluid, amount, captureType, false);
	}

	public OldProcessingContainer addOutputFluid(FluidStack fluid, int amount, CaptureType captureType,
			boolean isTemplateItem) {
		if (isClosed()) {
			throw new RuntimeException(
					String.format("Attempted to add an output fluid to a closed process output container."));
		}
		if (amount == 0) {
			return this;
		}
		if (!fluid.isEmpty()) {
			FluidStack fluidCopy = fluid.copy();
			fluidCopy.setAmount(amount);
			outputFluids.add(new ProcessingFluidWrapper(fluidCopy, captureType, isTemplateItem));
		}
		return this;
	}

	public List<ProcessingFluidWrapper> getOutputFluids() {
		return outputFluids;
	}

	public ProcessingFluidWrapper getOutputFluid(int index) {
		return outputFluids.get(index);
	}

	public boolean hasOutputFluids() {
		return outputFluids.size() > 0;
	}

	public CompoundTag getCustomParameterContainer() {
		return this.customParameters;
	}

	public void merge(OldProcessingContainer other) {
		if (isClosed()) {
			throw new RuntimeException(
					String.format("Attempted to add an input item to a clowed process output container."));
		}

		for (ProcessingItemWrapper itemWrapper : other.inputItems) {
			addInputItem(itemWrapper.item().copy(), itemWrapper.captureType(), itemWrapper.isTemplateItem());
		}

		for (ProcessingItemWrapper itemWrapper : other.outputItems) {
			addOutputItem(itemWrapper.item().copy(), itemWrapper.captureType(), itemWrapper.isTemplateItem());
		}

		for (ProcessingFluidWrapper fluidWrapper : other.inputFluids) {
			addInputFluid(fluidWrapper.fluid().copy(), fluidWrapper.captureType(), fluidWrapper.isTemplateItem());
		}

		for (ProcessingFluidWrapper fluidWrapper : other.outputFluids) {
			addOutputFluid(fluidWrapper.fluid().copy(), fluidWrapper.captureType(), fluidWrapper.isTemplateItem());
		}

		outputPower += other.outputPower;
		inputPower += other.inputPower;
	}

	public void open() {
		clear();
		closed = false;
	}

	public void setRecipeId(ResourceLocation recipeId) {
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

		ListTag serializedInputItems = NBTUtilities.serialize(inputItems, (item, tag) -> {
			item.item.save(tag);
			tag.putBoolean("t", item.isTemplateItem());
			tag.putByte("m", (byte) item.captureType.ordinal());
		});
		output.put("input_items", serializedInputItems);

		ListTag serializedOutputItems = NBTUtilities.serialize(outputItems, (item, tag) -> {
			item.item.save(tag);
			tag.putBoolean("t", item.isTemplateItem());
			tag.putByte("m", (byte) item.captureType.ordinal());
		});
		output.put("output_items", serializedOutputItems);

		ListTag serializedInputFluids = NBTUtilities.serialize(inputFluids, (fluid, tag) -> {
			fluid.fluid.writeToNBT(tag);
			tag.putBoolean("t", fluid.isTemplateItem());
			tag.putByte("m", (byte) fluid.captureType.ordinal());
		});
		output.put("input_fluids", serializedInputFluids);

		ListTag serializedOutputFluids = NBTUtilities.serialize(outputFluids, (fluid, tag) -> {
			fluid.fluid.writeToNBT(tag);
			tag.putBoolean("t", fluid.isTemplateItem());
			tag.putByte("m", (byte) fluid.captureType.ordinal());
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
			CompoundTag cTag = (CompoundTag) tag;
			return new ProcessingItemWrapper(ItemStack.of(cTag), CaptureType.values()[cTag.getByte("m")],
					cTag.getBoolean("t"));
		}));
		outputItems.addAll(NBTUtilities.deserialize(nbt.getList("output_items", ListTag.TAG_COMPOUND), (tag) -> {
			CompoundTag cTag = (CompoundTag) tag;
			return new ProcessingItemWrapper(ItemStack.of(cTag), CaptureType.values()[cTag.getByte("m")],
					cTag.getBoolean("t"));
		}));
		inputFluids.addAll(NBTUtilities.deserialize(nbt.getList("input_fluids", ListTag.TAG_COMPOUND), (tag) -> {
			CompoundTag cTag = (CompoundTag) tag;
			return new ProcessingFluidWrapper(FluidStack.loadFluidStackFromNBT(cTag),
					CaptureType.values()[cTag.getByte("m")], cTag.getBoolean("t"));
		}));
		outputFluids.addAll(NBTUtilities.deserialize(nbt.getList("output_fluids", ListTag.TAG_COMPOUND), (tag) -> {
			CompoundTag cTag = (CompoundTag) tag;
			return new ProcessingFluidWrapper(FluidStack.loadFluidStackFromNBT(cTag),
					CaptureType.values()[cTag.getByte("m")], cTag.getBoolean("t"));
		}));
	}
}
