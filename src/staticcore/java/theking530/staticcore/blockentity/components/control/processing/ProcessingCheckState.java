package theking530.staticcore.blockentity.components.control.processing;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class ProcessingCheckState {
	public enum ProcessingState {
		SKIP, ERROR, OK, CANCEL
	}

	private final ProcessingState state;
	private final MutableComponent errorMessage;

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

	public static ProcessingCheckState skip() {
		return new ProcessingCheckState(ProcessingState.SKIP, "");
	}

	public static ProcessingCheckState ok() {
		return new ProcessingCheckState(ProcessingState.OK, "");
	}

	public static ProcessingCheckState cancel() {
		return new ProcessingCheckState(ProcessingState.CANCEL, "");
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

	public static ProcessingCheckState notEnoughFluid() {
		return new ProcessingCheckState(ProcessingState.ERROR, "gui.staticcore.alert.not_enough_fluid");
	}

	public static ProcessingCheckState notEnoughPower(double requiredPower) {
		return new ProcessingCheckState(ProcessingState.ERROR, Component.translatable("gui.staticcore.alert.not_enough_power", requiredPower));
	}

	public static ProcessingCheckState notEnoughHeatCapacity(double requiredHeatCapacity) {
		return new ProcessingCheckState(ProcessingState.ERROR, Component.translatable("gui.staticcore.alert.not_enough_heat_capacity", requiredHeatCapacity));
	}

	public static ProcessingCheckState heatStorageTooHot(double mininumHeat) {
		return new ProcessingCheckState(ProcessingState.ERROR, Component.translatable("gui.staticcore.alert.heat_storage_too_hot", mininumHeat));
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
		return new ProcessingCheckState(ProcessingState.ERROR, "gui.staticcore.alert.machine_ouput_cannot_fit_recipe");
	}
}