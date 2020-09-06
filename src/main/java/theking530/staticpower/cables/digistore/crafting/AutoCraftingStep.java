package theking530.staticpower.cables.digistore.crafting;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;

public class AutoCraftingStep {
	private final Ingredient requiredItem;
	private final int ownedAmount;
	private int amountCraftable;
	private int amountCrafted;
	private final int totalAmount;
	private boolean isAttemptingResolve;
	private EncodedDigistorePattern pattern;

	public AutoCraftingStep(Ingredient requiredItem, int ownedAmount, int amountCraftable, int totalAmount) {
		this.requiredItem = requiredItem;
		this.ownedAmount = ownedAmount;
		this.amountCraftable = amountCraftable;
		this.amountCrafted = 0;
		this.totalAmount = totalAmount;
		this.isAttemptingResolve = false;
	}

	public void setPattern(EncodedDigistorePattern pattern) {
		this.pattern = pattern;
	}

	public EncodedDigistorePattern getPattern() {
		return pattern;
	}

	public Ingredient getRequiredItem() {
		return requiredItem;
	}

	public int getRequiredCraftingIterations() {
		float amount = ((float) amountCraftable / (float) pattern.getOutputs()[0].getCount());
		return (int) Math.ceil(amount);
	}

	public void markCraftingIterationCompleted() {
		amountCrafted += pattern.getOutputs()[0].getCount();
		amountCraftable -= pattern.getOutputs()[0].getCount();
	}

	public int getAmountCrafted() {
		return amountCrafted;
	}

	public boolean isCraftingCompleted() {
		return amountCraftable == 0;
	}

	public boolean isCraftingStep() {
		return pattern != null || amountCraftable > 0;
	}

	public int getOwnedAmount() {
		return ownedAmount;
	}

	public int getAmountCraftable() {
		return amountCraftable;
	}

	public int getTotalAmount() {
		return totalAmount;
	}

	public void setResolving() {
		isAttemptingResolve = true;
	}

	public boolean isAttemptingResolve() {
		return isAttemptingResolve;
	}

	@Override
	public String toString() {
		return "AutoCraftingStep [requiredItem=" + requiredItem + ", ownedAmount=" + ownedAmount + ", amountCraftable=" + amountCraftable + ", totalAmount=" + totalAmount + ", pattern=" + pattern
				+ ", isAttemptingResolve=" + isAttemptingResolve + "]";
	}

	public CompoundNBT serialize() {
		CompoundNBT output = new CompoundNBT();
		output.putInt("owned_mount", ownedAmount);
		output.putInt("amount_craftable", amountCraftable);
		output.putInt("total_amount", totalAmount);
		output.putBoolean("is_attempting_resolve", isAttemptingResolve);
		if (pattern != null) {
			output.put("pattern", pattern.serialize());
		}

		// Store the ingredients.
		ListNBT ingredientStacks = new ListNBT();
		for (ItemStack stack : requiredItem.getMatchingStacks()) {
			CompoundNBT outputTag = new CompoundNBT();
			stack.write(outputTag);
			ingredientStacks.add(outputTag);
		}
		output.put("ingredient_items", ingredientStacks);

		return output;
	}

	public static AutoCraftingStep read(CompoundNBT nbt) {
		int ownedAmount = nbt.getInt("owned_mount");
		int amountCraftable = nbt.getInt("amount_craftable");
		int totalAmount = nbt.getInt("total_amount");
		boolean isAttemptingResolve = nbt.getBoolean("is_attempting_resolve");
		EncodedDigistorePattern pattern = null;
		if (nbt.contains("pattern")) {
			pattern = EncodedDigistorePattern.read(nbt.getCompound("pattern"));
		}

		// Read the ingredients.
		ListNBT ingredientsNbt = nbt.getList("outputs", Constants.NBT.TAG_COMPOUND);
		ItemStack[] ingredientStacks = new ItemStack[ingredientsNbt.size()];
		for (int i = 0; i < ingredientsNbt.size(); i++) {
			CompoundNBT outputTagNbt = (CompoundNBT) ingredientsNbt.get(i);
			ItemStack stack = ItemStack.read(outputTagNbt);
			ingredientStacks[i] = stack;
		}
		Ingredient requiredItem = Ingredient.fromStacks(ingredientStacks);

		// Create the output.
		AutoCraftingStep output = new AutoCraftingStep(requiredItem, ownedAmount, amountCraftable, totalAmount);
		if (pattern != null && pattern.isValid()) {
			output.setPattern(pattern);
		}
		if (isAttemptingResolve) {
			output.setResolving();
		}
		return output;
	}

}
