package theking530.staticpower.cables.digistore.crafting;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.crafting.Ingredient;
import theking530.staticcore.utilities.IngredientUtilities;

public class AutoCraftingStep {
	private final Ingredient ingredientToCraft;
	private int storedAmount;
	private int amountToCraft;
	private int totalRequiredAmount;
	private boolean isAttemptingResolve;
	private boolean machineItemsSupplied;
	private EncodedDigistorePattern pattern;

	public AutoCraftingStep(Ingredient itemToCraft, int storedAmount, int amountToCraft, int totalRequiredAmount) {
		this.ingredientToCraft = itemToCraft;
		this.storedAmount = storedAmount;
		this.amountToCraft = amountToCraft;
		this.totalRequiredAmount = totalRequiredAmount;
		this.isAttemptingResolve = false;
	}

	public void setCraftingPattern(EncodedDigistorePattern pattern) {
		this.pattern = pattern;
	}

	public EncodedDigistorePattern getCraftingPattern() {
		return pattern;
	}

	public Ingredient getIngredientToCraft() {
		return ingredientToCraft;
	}

	public int getRequiredCraftingIterations() {
		float amount = ((float) amountToCraft / (float) pattern.getOutput().getCount());
		return (int) Math.ceil(amount);
	}

	public void markCraftingIterationCompleted() {
		amountToCraft -= pattern.getOutput().getCount();
		storedAmount += pattern.getOutput().getCount();
		if (amountToCraft < 0) {
			amountToCraft = 0;
		}
	}

	public int getAmountAlreadyCrafted() {
		return totalRequiredAmount - (storedAmount + amountToCraft);
	}

	public void setStoredAmount(int storedAmount) {
		this.storedAmount = storedAmount;
		if (amountToCraft < 0) {
			amountToCraft = 0;
		}
	}

	public void setAmountRemainingToCraft(int amountRemainingToCraft) {
		this.amountToCraft = amountRemainingToCraft;
		if (amountRemainingToCraft < 0) {
			amountRemainingToCraft = 0;
		}
	}

	public void setTotalRequiredAmount(int totalRequiredAmount) {
		this.totalRequiredAmount = totalRequiredAmount;
		if (amountToCraft < 0) {
			amountToCraft = 0;
		}
	}

	public void setAttemptingResolve(boolean isAttemptingResolve) {
		this.isAttemptingResolve = isAttemptingResolve;
	}

	public boolean areMachineCraftingItemsAlreadySupplied() {
		return machineItemsSupplied;
	}

	public void markMachineCraftingItemsAlreadySupplied() {
		machineItemsSupplied = true;
	}

	public boolean isCraftingCompleted() {
		return amountToCraft <= 0;
	}

	public boolean isCraftingStep() {
		return pattern != null && amountToCraft > 0;
	}

	public int getStoredAmount() {
		return storedAmount;
	}

	public int getAmountRemainingToCraft() {
		return amountToCraft;
	}

	public int getTotalRequiredAmount() {
		return totalRequiredAmount;
	}

	public void setResolving() {
		isAttemptingResolve = true;
	}

	public boolean isAttemptingResolve() {
		return isAttemptingResolve;
	}

	@Override
	public String toString() {
		return "AutoCraftingStep [requiredItem=" + (ingredientToCraft.isEmpty() ? ingredientToCraft : ingredientToCraft.getItems()[0]) + ", storedAmount=" + storedAmount
				+ ", amountToCraft=" + amountToCraft + ", amountCrafted=" + getAmountAlreadyCrafted() + ", totalRequiredAmount=" + totalRequiredAmount + ", isAttemptingResolve="
				+ isAttemptingResolve + ", pattern=" + pattern + "]";
	}

	public CompoundTag serialize() {
		CompoundTag output = new CompoundTag();
		output.putInt("stored_amount", storedAmount);
		output.putInt("amount_to_craft", amountToCraft);
		output.putInt("total_amount", totalRequiredAmount);
		output.putBoolean("is_attempting_resolve", isAttemptingResolve);
		output.putBoolean("machine_items_supplied", machineItemsSupplied);
		if (pattern != null) {
			output.put("pattern", pattern.serialize());
		}

		output.put("required_item", IngredientUtilities.serializeIngredient(ingredientToCraft));

		return output;
	}

	public static AutoCraftingStep read(CompoundTag nbt) {
		int ownedAmount = nbt.getInt("stored_amount");
		int amountCraftable = nbt.getInt("amount_to_craft");
		int totalAmount = nbt.getInt("total_amount");
		boolean isAttemptingResolve = nbt.getBoolean("is_attempting_resolve");
		boolean machineItemsSupplied = nbt.getBoolean("machine_items_supplied");

		EncodedDigistorePattern pattern = null;
		if (nbt.contains("pattern")) {
			pattern = EncodedDigistorePattern.read(nbt.getCompound("pattern"));
			if (pattern == null) {
				throw new RuntimeException("Unable to load encoded pattern with contents: " + nbt.getCompound("pattern")
						+ ". If a recipe was removed, first replace it an cancel all requests regarding the item before removing it.");
			}
		}

		// Read the ingredient.
		Ingredient requiredItem = IngredientUtilities.deserializeIngredient(nbt.getCompound("required_item"));

		// Create the output.
		AutoCraftingStep output = new AutoCraftingStep(requiredItem, ownedAmount, amountCraftable, totalAmount);
		if (pattern != null && pattern.isValid()) {
			output.setCraftingPattern(pattern);
		}
		if (isAttemptingResolve) {
			output.setResolving();
		}
		output.machineItemsSupplied = machineItemsSupplied;
		return output;
	}

}
