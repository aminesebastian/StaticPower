package theking530.staticpower.cables.digistore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraftforge.common.util.Constants;
import theking530.staticpower.cables.digistore.crafting.AutoCraftingStep;

public class DigistoreCraftingRequest {
	public final long id;
	public final Queue<AutoCraftingStep> remainingSteps;
	public final List<AutoCraftingStep> completedSteps;

	public DigistoreCraftingRequest(long id, Collection<AutoCraftingStep> steps) {
		this.id = id;
		this.completedSteps = new ArrayList<AutoCraftingStep>();
		this.remainingSteps = new LinkedList<AutoCraftingStep>();
		this.remainingSteps.addAll(steps);
	}

	public DigistoreCraftingRequest(long id, Collection<AutoCraftingStep> pendingSteps, Collection<AutoCraftingStep> completedSteps) {
		this.id = id;
		this.completedSteps = new ArrayList<AutoCraftingStep>();
		this.completedSteps.addAll(completedSteps);
		this.remainingSteps = new LinkedList<AutoCraftingStep>();
		this.remainingSteps.addAll(pendingSteps);
	}

	public long getId() {
		return id;
	}

	public boolean isDone() {
		return remainingSteps.isEmpty();
	}

	public AutoCraftingStep peekTopStep() {
		return remainingSteps.peek();
	}

	public AutoCraftingStep completeCurrentStep() {
		AutoCraftingStep step = remainingSteps.poll();
		completedSteps.add(step);
		return step;
	}

	public CompoundTag serializeToNBT() {
		CompoundTag output = new CompoundTag();
		output.putLong("id", id);

		// Store the pending crafts.
		ListTag remainingStepsNbt = new ListTag();
		for (AutoCraftingStep step : remainingSteps) {
			remainingStepsNbt.add(step.serialize());
		}
		output.put("remaining_steps", remainingStepsNbt);

		// Store the completedCrafts.
		ListTag completedStepsNbt = new ListTag();
		for (AutoCraftingStep step : completedSteps) {
			completedStepsNbt.add(step.serialize());
		}
		output.put("completed_steps", completedStepsNbt);

		return output;
	}

	public static DigistoreCraftingRequest read(CompoundTag nbt) {
		long id = nbt.getLong("id");

		// Read the pending steps.
		ListTag remainingStepsNbt = nbt.getList("remaining_steps", Constants.NBT.TAG_COMPOUND);
		ArrayList<AutoCraftingStep> remainingSteps = new ArrayList<AutoCraftingStep>();
		for (int i = 0; i < remainingStepsNbt.size(); i++) {
			CompoundTag outputTagNbt = (CompoundTag) remainingStepsNbt.get(i);
			remainingSteps.add(AutoCraftingStep.read(outputTagNbt));
		}

		// Read the completed steps.
		ListTag completedStepsNbt = nbt.getList("completed_steps", Constants.NBT.TAG_COMPOUND);
		ArrayList<AutoCraftingStep> completedSteps = new ArrayList<AutoCraftingStep>();
		for (int i = 0; i < completedStepsNbt.size(); i++) {
			CompoundTag outputTagNbt = (CompoundTag) completedStepsNbt.get(i);
			completedSteps.add(AutoCraftingStep.read(outputTagNbt));
		}

		return new DigistoreCraftingRequest(id, remainingSteps, completedSteps);
	}
}
