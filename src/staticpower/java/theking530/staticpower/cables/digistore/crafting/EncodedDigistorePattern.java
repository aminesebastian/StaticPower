package theking530.staticpower.cables.digistore.crafting;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import theking530.staticcore.crafting.StaticCoreRecipeManager;
import theking530.staticcore.utilities.IngredientUtilities;
import theking530.staticpower.cables.attachments.digistore.patternencoder.DigistorePatternEncoder.RecipeEncodingType;
import theking530.staticpower.items.DigistorePatternCard;

public class EncodedDigistorePattern {
	private final long id;
	private final ItemStack[] inputs;
	private final ItemStack output;
	private final RecipeEncodingType recipeType;
	private final List<EncodedIngredient> requiredItems;

	private final ResourceLocation craftingRecipeId;
	private final boolean isValid;

	public EncodedDigistorePattern(long id, ItemStack[] inputs, ItemStack output, RecipeEncodingType recipeType) {
		this.id = id;
		this.inputs = inputs;
		this.output = output;
		this.recipeType = recipeType;
		this.craftingRecipeId = null;
		this.requiredItems = new LinkedList<EncodedIngredient>();
		this.isValid = true;
		cacheRequiredItems(inputs);
	}

	public EncodedDigistorePattern(long id, ItemStack[] inputs, CraftingRecipe recipe) {
		this.id = id;
		this.requiredItems = new LinkedList<EncodedIngredient>();
		this.recipeType = RecipeEncodingType.CRAFTING_TABLE;

		if (recipe == null) {
			isValid = false;
			this.inputs = new ItemStack[0];
			this.craftingRecipeId = null;
			this.output = ItemStack.EMPTY;
		} else {
			isValid = true;
			this.craftingRecipeId = recipe.getId();
			this.inputs = inputs;
			this.output = recipe.getResultItem();
			cacheRequiredIngredients(recipe.getIngredients());
		}
	}

	public long getId() {
		return id;
	}

	public boolean isValid() {
		return isValid;
	}

	public ItemStack[] getInputs() {
		return inputs;
	}

	public ItemStack getOutput() {
		return output;
	}

	public RecipeEncodingType getRecipeType() {
		return recipeType;
	}

	public ResourceLocation getCraftingRecipeId() {
		return craftingRecipeId;
	}

	public List<EncodedIngredient> getRequiredItems() {
		return requiredItems;
	}

	protected void cacheRequiredItems(ItemStack... items) {
		for (ItemStack input : items) {
			// Skip empty items.
			if (input.isEmpty()) {
				continue;
			}

			// Check to see if we cached the item.
			boolean cached = false;

			// Check all required items to see if we already tracked this item. IF we do,
			// increment the count. Otherwise, add it.
			for (EncodedIngredient key : requiredItems) {
				if (key.representsIngrdient(Ingredient.of(input))) {
					key.grow(input.getCount());
					cached = true;
				}
			}

			// If we haven't cached this before, add it.
			if (!cached) {
				requiredItems.add(new EncodedIngredient(input, input.getCount()));
			}
		}
	}

	protected void cacheRequiredIngredients(List<Ingredient> ingredients) {
		for (Ingredient input : ingredients) {
			// Skip empty ingredients.
			if (input == Ingredient.EMPTY) {
				continue;
			}

			// Check to see if we cached the item.
			boolean cached = false;

			// Check all required items to see if we already tracked this item. IF we do,
			// increment the count. Otherwise, add it.
			for (EncodedIngredient key : requiredItems) {
				if (key.representsIngrdient(input)) {
					key.grow(1);
					cached = true;
				}
			}

			// If we haven't cached this before, add it.
			if (!cached) {
				requiredItems.add(new EncodedIngredient(input, 1));
			}
		}
	}

	@Nullable
	public static EncodedDigistorePattern readFromPatternCard(ItemStack patternCard) {
		if (DigistorePatternCard.hasPattern(patternCard)) {
			CompoundTag patternTag = patternCard.getTag().getCompound(DigistorePatternCard.ENCODED_PATTERN_TAG);
			if (!patternTag.contains("type") || !patternTag.contains("inputs") || !patternTag.contains("output")) {
				return null;
			}

			// Get the pattern.
			EncodedDigistorePattern output = read(patternTag);

			// If it is not valid (for example, the recipe was removed from the game), clear
			// the card and then return null. Otherwise, return the pattern.
			if (!output.isValid()) {
				patternCard.getTag().remove(DigistorePatternCard.ENCODED_PATTERN_TAG);
				if (patternCard.getTag().size() == 0) {
					patternCard.setTag(null);
				}
				return null;
			} else {
				return output;
			}
		}
		return null;
	}

	public static EncodedDigistorePattern read(CompoundTag nbt) {
		// Read the recipe type.
		RecipeEncodingType recipeType = RecipeEncodingType.values()[nbt.getInt("type")];

		// Read the pattern id.
		long id = nbt.getLong("id");

		// Read the recipe id.
		ResourceLocation craftingRecipeId = null;
		if (nbt.contains("crafting_id")) {
			craftingRecipeId = new ResourceLocation(nbt.getString("crafting_id"));
		}

		// Read the inputs.
		ItemStack[] inputStacks = new ItemStack[9];
		ListTag inputsNBT = nbt.getList("inputs", Tag.TAG_COMPOUND);
		for (int i = 0; i < inputsNBT.size(); i++) {
			CompoundTag inputTagNbt = (CompoundTag) inputsNBT.get(i);
			ItemStack stack = ItemStack.of(inputTagNbt);
			inputStacks[i] = stack;
		}

		// Read the output.
		ItemStack outputStack = ItemStack.of(nbt.getCompound("output"));

		// Create the recipe.
		if (recipeType == RecipeEncodingType.CRAFTING_TABLE) {
			Optional<CraftingRecipe> craftingRecipe = StaticCoreRecipeManager.getCraftingRecipe(craftingRecipeId);
			if (craftingRecipeId != null && craftingRecipe.isPresent()) {
				return new EncodedDigistorePattern(id, inputStacks, craftingRecipe.get());
			} else {
				return new EncodedDigistorePattern(id, inputStacks, null);
			}
		} else {
			return new EncodedDigistorePattern(id, inputStacks, outputStack, recipeType);
		}
	}

	@Override
	public String toString() {
		return "EncodedDigistorePattern [inputs=" + Arrays.toString(inputs) + ", output=" + output + ", recipeType=" + recipeType + ", requiredItems=" + requiredItems
				+ ", craftingRecipeId=" + craftingRecipeId + ", isValid=" + isValid + "]";
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof EncodedDigistorePattern)) {
			return false;
		}

		// We only need to check id here.
		EncodedDigistorePattern pattern = (EncodedDigistorePattern) object;
		if (id != pattern.id) {
			return false;
		}

		return true;
	}

	public CompoundTag serialize() {
		// Create the pattern tag.
		CompoundTag pattern = new CompoundTag();

		// Store the id.
		pattern.putLong("id", id);

		// Store the recipe type.
		pattern.putInt("type", recipeType.ordinal());

		// Store the recipe id.
		if (craftingRecipeId != null) {
			pattern.putString("crafting_id", craftingRecipeId.toString());
		}

		// Store the inputs.
		ListTag inputStacks = new ListTag();
		for (ItemStack stack : inputs) {
			CompoundTag inputTag = new CompoundTag();
			stack.save(inputTag);
			inputStacks.add(inputTag);
		}
		pattern.put("inputs", inputStacks);

		// Store the output.
		CompoundTag outputTag = new CompoundTag();
		output.save(outputTag);
		pattern.put("output", outputTag);

		return pattern;
	}

	public static class EncodedIngredient {
		protected final Ingredient ingredient;
		protected int count;

		public EncodedIngredient(Ingredient ingredient, int count) {
			this.ingredient = ingredient;
			this.count = count;
		}

		public EncodedIngredient(ItemStack item, int count) {
			this.ingredient = Ingredient.of(item);
			this.count = count;
		}

		public Ingredient getIngredient() {
			return ingredient;
		}

		public int getCount() {
			return count;
		}

		public void grow(int amount) {
			count += amount;
		}

		public boolean representsIngrdient(Ingredient other) {
			return IngredientUtilities.areIngredientsEqual(ingredient, other);
		}

		public boolean equalsIgnoringCount(EncodedIngredient other) {
			return other.ingredient == ingredient;
		}

		@Override
		public String toString() {
			return "EncodedIngredient [stacks=" + Arrays.toString(ingredient.getItems()) + "]";
		}

	}
}