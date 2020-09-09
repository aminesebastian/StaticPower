package theking530.staticpower.cables.digistore.crafting;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;

public class CraftingRequestResponse {
	private final long id;
	private final int craftableAmount;
	private int currentCraftingStep;
	private final ItemStack craftingTarget;
	private final List<AutoCraftingStep> steps;
	private final RequiredAutoCraftingMaterials billOfMaterials;

	public CraftingRequestResponse(long id, int craftableAmount, ItemStack craftingTarget, List<AutoCraftingStep> steps) {
		this.id = id;
		this.craftableAmount = craftableAmount;
		this.craftingTarget = craftingTarget;
		this.steps = steps;
		this.currentCraftingStep = 0;
		this.billOfMaterials = new RequiredAutoCraftingMaterials(steps);
	}

	public List<AutoCraftingStep> getSteps() {
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
		return steps.get(currentCraftingStep);
	}

	public void completeCurrentStep() {
		currentCraftingStep++;
	}

	public boolean isDone() {
		return currentCraftingStep >= steps.size();
	}

	public RequiredAutoCraftingMaterials getBillOfMaterials() {
		return billOfMaterials;
	}

	public CompoundNBT serialze() {
		// Create the output.
		CompoundNBT output = new CompoundNBT();

		// Store the ID.
		output.putLong("id", id);

		// Store the amount.
		output.putInt("amount", craftableAmount);

		// Store the current step.
		output.putInt("current_step", currentCraftingStep);

		// Store the crafting target.
		CompoundNBT craftingTargetNbt = new CompoundNBT();
		craftingTarget.write(craftingTargetNbt);
		output.put("target", craftingTargetNbt);

		// Store the steps.
		ListNBT stepNBTList = new ListNBT();
		for (AutoCraftingStep step : steps) {
			stepNBTList.add(step.serialize());
		}
		output.put("steps", stepNBTList);

		return output;
	}

	public static CraftingRequestResponse read(CompoundNBT nbt) {
		// Read the ID.
		long id = nbt.getLong("id");

		// Read the amount.
		int amount = nbt.getInt("amount");

		// Read the current step.
		int currentCraftingStep = nbt.getInt("current_step");

		// Read the crafting target.
		ItemStack target = ItemStack.read(nbt.getCompound("target"));

		// Read the steps.
		List<AutoCraftingStep> steps = new ArrayList<AutoCraftingStep>();
		ListNBT stepNBTList = nbt.getList("steps", Constants.NBT.TAG_COMPOUND);
		for (INBT step : stepNBTList) {
			CompoundNBT stepTag = (CompoundNBT) step;
			steps.add(AutoCraftingStep.read(stepTag));
		}

		CraftingRequestResponse output = new CraftingRequestResponse(id, amount, target, steps);
		output.currentCraftingStep = currentCraftingStep;
		return output;
	}
}
