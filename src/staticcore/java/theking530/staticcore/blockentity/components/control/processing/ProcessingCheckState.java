package theking530.staticcore.blockentity.components.control.processing;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.common.util.INBTSerializable;
import theking530.staticcore.gui.text.PowerTextFormatting;

public class ProcessingCheckState implements INBTSerializable<CompoundTag> {
	public enum ProcessingState {
		IDLE, PENDING, SKIP, ERROR, OK, CANCEL
	}

	private ProcessingState state;
	private MutableComponent errorMessage;

	private ProcessingCheckState(ProcessingState state, String errorMessage) {
		this(state, Component.translatable(errorMessage));
	}

	private ProcessingCheckState(ProcessingState state, MutableComponent errorMessage) {
		this.state = state;
		this.errorMessage = errorMessage;
	}

	public ProcessingState getState() {
		return state;
	}

	public MutableComponent getErrorMessage() {
		return errorMessage;
	}

	public boolean isPending() {
		return state == ProcessingState.PENDING;
	}

	public boolean isOk() {
		return state == ProcessingState.OK;
	}

	public boolean isError() {
		return state == ProcessingState.ERROR;
	}

	public boolean isSkip() {
		return state == ProcessingState.SKIP;
	}

	public boolean isCancel() {
		return state == ProcessingState.CANCEL;
	}

	public boolean isIdle() {
		return state == ProcessingState.IDLE;
	}

	public static ProcessingCheckState pending() {
		return new ProcessingCheckState(ProcessingState.PENDING, "");
	}

	public static ProcessingCheckState skip() {
		return new ProcessingCheckState(ProcessingState.SKIP, "");
	}

	public static ProcessingCheckState ok() {
		return new ProcessingCheckState(ProcessingState.OK, "");
	}

	public static ProcessingCheckState cancel() {
		return new ProcessingCheckState(ProcessingState.CANCEL, "");
	}

	public static ProcessingCheckState idle() {
		return new ProcessingCheckState(ProcessingState.IDLE, "");
	}

	public static ProcessingCheckState error(String errorMessage) {
		return new ProcessingCheckState(ProcessingState.ERROR, errorMessage);
	}

	public static ProcessingCheckState error(MutableComponent errorMessage) {
		return new ProcessingCheckState(ProcessingState.ERROR, errorMessage);
	}

	public static ProcessingCheckState notCorrectFluid() {
		return new ProcessingCheckState(ProcessingState.ERROR, "gui.staticcore.alert.requires_different_input_fluid");
	}

	public static ProcessingCheckState doesNotPassRedstoneControlCheck() {
		return ProcessingCheckState.error(Component.literal("Redstone Control Mode Not Satisfied.").getString());
	}

	public static ProcessingCheckState notEnoughFluid() {
		return new ProcessingCheckState(ProcessingState.ERROR, "gui.staticcore.alert.not_enough_fluid");
	}

	public static ProcessingCheckState notEnoughPower(double requiredPower) {
		return new ProcessingCheckState(ProcessingState.ERROR,
				Component.translatable("gui.staticcore.alert.not_enough_power", requiredPower));
	}

	public static ProcessingCheckState powerUsageTooHigh(double requiredPower) {
		return ProcessingCheckState.error(Component.literal("Recipe's power per tick requirement (")
				.append(PowerTextFormatting.formatPowerRateToString(requiredPower))
				.append(") is larger than the amount this machine can handle!").getString());
	}

	public static ProcessingCheckState notEnoughHeatCapacity(double requiredHeatCapacity) {
		return new ProcessingCheckState(ProcessingState.ERROR,
				Component.translatable("gui.staticcore.alert.not_enough_heat_capacity", requiredHeatCapacity));
	}

	public static ProcessingCheckState heatStorageTooHot(double mininumHeat) {
		return new ProcessingCheckState(ProcessingState.ERROR,
				Component.translatable("gui.staticcore.alert.heat_storage_too_hot", mininumHeat));
	}

	public static ProcessingCheckState powerOutputFull() {
		return new ProcessingCheckState(ProcessingState.ERROR, "gui.staticcore.alert.power_output_full");
	}

	public static ProcessingCheckState fluidOutputFull() {
		return new ProcessingCheckState(ProcessingState.ERROR, "gui.staticcore.alert.fluid_output_full");
	}

	public static ProcessingCheckState outputFluidDoesNotMatch() {
		return new ProcessingCheckState(ProcessingState.ERROR, "gui.staticcore.alert.fluid_output_mismatch");
	}

	public static ProcessingCheckState internalBufferNotEmpty() {
		return new ProcessingCheckState(ProcessingState.ERROR, "Machine's internal buffer not empty.");
	}

	public static ProcessingCheckState outputsCannotTakeRecipe() {
		return new ProcessingCheckState(ProcessingState.ERROR, "gui.staticcore.alert.machine_output_cannot_fit_recipe");
	}

	public void toNetwork(FriendlyByteBuf buffer) {
		buffer.writeByte(state.ordinal());
		buffer.writeUtf(errorMessage.getString());
	}

	public static ProcessingCheckState fromNetwork(FriendlyByteBuf buffer) {
		ProcessingState state = ProcessingState.values()[buffer.readByte()];
		MutableComponent errorMessage = Component.translatable(buffer.readUtf());
		return new ProcessingCheckState(state, errorMessage);
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag output = new CompoundTag();
		output.putByte("state", (byte) state.ordinal());
		output.putString("error_message", errorMessage.getString());
		return output;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		state = ProcessingState.values()[nbt.getByte("state")];
		errorMessage = Component.translatable(nbt.getString("error_message"));
	}
}