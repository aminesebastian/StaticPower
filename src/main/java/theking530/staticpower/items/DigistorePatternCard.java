package theking530.staticpower.items;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.common.util.Constants;
import theking530.staticpower.blocks.interfaces.ICustomModelSupplier;
import theking530.staticpower.cables.attachments.digistore.digistorepatternencoder.DigistorePatternEncoder.RecipeEncodingType;
import theking530.staticpower.client.StaticPowerAdditionalModels;
import theking530.staticpower.client.rendering.items.PatternCardItemModel;

public class DigistorePatternCard extends StaticPowerItem implements ICustomModelSupplier {
	public static final String ENCODED_PATTERN_TAG = "pattern";

	public DigistorePatternCard(String name) {
		super(name);
	}

	@Override
	public ITextComponent getDisplayName(ItemStack stack) {
		// Get the item name.
		ITextComponent cardName = super.getDisplayName(stack);

		// If we have a pattern, mark this card as encoded.
		if (stack.hasTag() && stack.getTag().contains(ENCODED_PATTERN_TAG)) {
			cardName.appendSibling(new StringTextComponent(" (Encoded)"));
		}

		// Return the final name.
		return cardName;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void getAdvancedTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
		// Try to get the pattern.
		EncodedDigistorePattern pattern = EncodedDigistorePattern.readFromPatternCard(stack);

		// If there is no pattern, do nothing.
		if (pattern == null) {
			return;
		}

		// Add inputs.
		tooltip.add(new StringTextComponent("Inputs: "));
		for (ItemStack input : pattern.inputs) {
			if (!input.isEmpty()) {
				tooltip.add(new StringTextComponent("  •").appendSibling(input.getDisplayName()));
			}
		}

		// Add outputs.
		tooltip.add(new StringTextComponent("Outputs: "));
		for (ItemStack output : pattern.outputs) {
			if (!output.isEmpty()) {
				tooltip.add(new StringTextComponent("  •").appendSibling(output.getDisplayName()));
			}
		}

	}

	public ItemStack getBlankEncodedCardForPreview() {
		ItemStack output = new ItemStack(this);
		output.setTag(new CompoundNBT());
		output.getTag().put(ENCODED_PATTERN_TAG, new CompoundNBT());
		return output;
	}

	public ItemStack getEncodedRecipe(EncodedDigistorePattern recipe) {
		// Create the pattern stack.
		ItemStack output = new ItemStack(this);

		// Give it a tag.
		output.setTag(new CompoundNBT());

		// Put the pattern on the itemstack.
		output.getTag().put(ENCODED_PATTERN_TAG, recipe.serialize());

		// Return the encoded itemstack.
		return output;
	}

	public static boolean hasPattern(ItemStack card) {
		return card.getItem() instanceof DigistorePatternCard && card.hasTag() && card.getTag().contains(DigistorePatternCard.ENCODED_PATTERN_TAG);
	}

	@Override
	public boolean hasModelOverride(BlockState state) {
		return true;
	}

	@Override
	public IBakedModel getModelOverride(BlockState state, IBakedModel existingModel, ModelBakeEvent event) {
		IBakedModel encodedModel = event.getModelRegistry().get(StaticPowerAdditionalModels.DIGISTORE_PATTERN_CARD_ENCODED);
		return new PatternCardItemModel(existingModel, encodedModel);
	}

	public static class EncodedDigistorePattern {
		public final ItemStack[] inputs;
		public final ItemStack[] outputs;
		public final RecipeEncodingType recipeType;

		public EncodedDigistorePattern(ItemStack[] inputs, ItemStack[] outputs, RecipeEncodingType recipeType) {
			this.inputs = inputs;
			this.outputs = outputs;
			this.recipeType = recipeType;
		}

		@Nullable
		public static EncodedDigistorePattern readFromPatternCard(ItemStack patternCard) {
			if (hasPattern(patternCard)) {
				CompoundNBT patternTag = patternCard.getTag().getCompound(ENCODED_PATTERN_TAG);
				if (!patternTag.contains("type") || !patternTag.contains("inputs") || !patternTag.contains("outputs")) {
					return null;
				}
				return read(patternTag);
			}
			return null;
		}

		public static EncodedDigistorePattern read(CompoundNBT nbt) {
			// Read the recipe type.
			RecipeEncodingType recipeType = RecipeEncodingType.values()[nbt.getInt("type")];

			// Read the inputs.
			ItemStack[] inputStacks = new ItemStack[9];
			ListNBT inputsNBT = nbt.getList("inputs", Constants.NBT.TAG_COMPOUND);
			for (int i = 0; i < 9; i++) {
				CompoundNBT inputTagNbt = (CompoundNBT) inputsNBT.get(i);
				ItemStack stack = ItemStack.read(inputTagNbt);
				inputStacks[i] = stack;
			}

			// Read the outputs.
			ItemStack[] outputStacks = new ItemStack[9];
			ListNBT outputsNBT = nbt.getList("outputs", Constants.NBT.TAG_COMPOUND);
			for (int i = 0; i < 9; i++) {
				CompoundNBT outputTagNbt = (CompoundNBT) outputsNBT.get(i);
				ItemStack stack = ItemStack.read(outputTagNbt);
				outputStacks[i] = stack;
			}

			// Create the recipe.
			return new EncodedDigistorePattern(inputStacks, outputStacks, recipeType);
		}

		public CompoundNBT serialize() {
			// Create the pattern tag.
			CompoundNBT pattern = new CompoundNBT();

			// Store the recipe type.
			pattern.putInt("type", recipeType.ordinal());

			// Store the inputs.
			ListNBT inputStacks = new ListNBT();
			for (ItemStack stack : inputs) {
				CompoundNBT inputTag = new CompoundNBT();
				stack.write(inputTag);
				inputStacks.add(inputTag);
			}
			pattern.put("inputs", inputStacks);

			// Store the outputs.
			ListNBT outputStacks = new ListNBT();
			for (ItemStack stack : outputs) {
				CompoundNBT outputTag = new CompoundNBT();
				stack.write(outputTag);
				outputStacks.add(outputTag);
			}
			pattern.put("outputs", outputStacks);

			return pattern;
		}
	}
}
