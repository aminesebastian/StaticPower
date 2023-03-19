package theking530.staticpower.cables.digistore.crafting;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import theking530.staticpower.cables.digistore.crafting.recipes.CraftingStepsBundle;

public class CraftingRequestResponse {
	private final long id;
	private final int craftableAmount;
	private int currentCraftingStep;
	private final ItemStack craftingTarget;
	private final CraftingStepsBundle steps; // This is included here to access the steps stored internally. I know there's
												// data redundancy here, but this is the safer approach.
	private MutableComponent currentBlocker;

	public CraftingRequestResponse(long id, int craftableAmount, ItemStack craftingTarget, CraftingStepsBundle steps) {
		this.id = id;
		this.craftableAmount = craftableAmount;
		ItemStack itemToSerialize = craftingTarget.copy();
		itemToSerialize.setCount(1);
		this.craftingTarget = itemToSerialize;
		this.steps = steps;
		this.currentCraftingStep = 0;
		this.currentBlocker = null;
	}

	public CraftingStepsBundle getStepsBundle() {
		return steps;
	}

	public long getId() {
		return id;
	}

	public ItemStack getCraftingItem() {
		return craftingTarget;
	}

	public int getCraftableAmount() {
		return craftableAmount;
	}

	public AutoCraftingStep peekTopStep() {
		return steps.getSteps().get(currentCraftingStep);
	}

	public void completeCurrentStep() {
		currentCraftingStep++;
	}

	public void setBlocker(MutableComponent currentCraftingError) {
		this.currentBlocker = currentCraftingError;
	}

	public void clearBlocker() {
		currentBlocker = null;
	}

	public boolean isBlocked() {
		return currentBlocker != null;
	}

	public MutableComponent getBlockerMessage() {
		return currentBlocker;
	}

	public boolean isDone() {
		return currentCraftingStep >= steps.getSteps().size();
	}

	public int getCurrentCraftingStepIndex() {
		return currentCraftingStep;
	}

	public RequiredAutoCraftingMaterials getBillOfMaterials() {
		return steps.getBillOfMaterials();
	}

	public CompoundTag serialize() {
		// Create the output.
		CompoundTag output = new CompoundTag();

		// Store the ID.
		output.putLong("id", id);

		// Store the amount.
		output.putInt("amount", craftableAmount);

		// Store the current step.
		output.putInt("current_step", currentCraftingStep);

		// Store the crafting target.
		CompoundTag craftingTargetNbt = new CompoundTag();
		craftingTarget.save(craftingTargetNbt);
		output.put("target", craftingTargetNbt);

		// Store the steps.
		output.put("steps", steps.serialize());

		// Store the blocker if it exists.
		if (currentBlocker != null) {
			output.putString("blocker", Component.Serializer.toJson(currentBlocker));
		}

		return output;
	}

	public static CraftingRequestResponse read(CompoundTag nbt) {
		// Read the ID.
		long id = nbt.getLong("id");

		// Read the amount.
		int amount = nbt.getInt("amount");

		// Read the current step.
		int currentCraftingStep = nbt.getInt("current_step");

		// Read the crafting target.
		ItemStack target = ItemStack.of(nbt.getCompound("target"));

		// Read the steps.
		CraftingStepsBundle steps = CraftingStepsBundle.read(nbt.getCompound("steps"));

		// Create the output.
		CraftingRequestResponse output = new CraftingRequestResponse(id, amount, target, steps);
		output.currentCraftingStep = currentCraftingStep;

		// Read the blocker if it was supplied.
		if (nbt.contains("blocker")) {
			output.currentBlocker = (MutableComponent) Component.Serializer.fromJson(nbt.getString("blocker"));
		}

		return output;
	}
}
