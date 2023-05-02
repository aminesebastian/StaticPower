package theking530.staticpower.items;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelEvent;
import theking530.staticcore.client.ICustomModelProvider;
import theking530.staticpower.cables.digistore.crafting.EncodedDigistorePattern;
import theking530.staticpower.client.StaticPowerAdditionalModels;
import theking530.staticpower.client.rendering.items.PatternCardItemModel;

public class DigistorePatternCard extends StaticPowerItem implements ICustomModelProvider {
	public static final String ENCODED_PATTERN_TAG = "pattern";

	public DigistorePatternCard() {
		super();
	}

	@Override
	public Component getName(ItemStack stack) {
		// Get the item name.
		MutableComponent cardName = (MutableComponent) super.getName(stack);

		// If we have a pattern, mark this card as encoded.
		if (stack.hasTag() && stack.getTag().contains(ENCODED_PATTERN_TAG)) {
			cardName.append(Component.literal(" (Encoded)"));
		}

		// Return the final name.
		return cardName;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean showAdvanced) {
		if (showAdvanced) {
			// Try to get the pattern.
			EncodedDigistorePattern pattern = EncodedDigistorePattern.readFromPatternCard(stack);

			// If there is no pattern, do nothing.
			if (pattern == null) {
				return;
			}

			// Add inputs.
			tooltip.add(Component.literal("Inputs: "));
			for (ItemStack input : pattern.getInputs()) {
				if (!input.isEmpty()) {
					tooltip.add(Component.literal("  �").append(input.getHoverName()));
				}
			}

			// Add outputs.
			tooltip.add(Component.literal("Output: "));
			if (!pattern.getOutput().isEmpty()) {
				MutableComponent outputTooltip = Component.literal("  �").append(pattern.getOutput().getHoverName());
				if (pattern.getOutput().getCount() > 1) {
					outputTooltip.append(Component.literal(" x" + pattern.getOutput().getCount()));
				}
				tooltip.add(outputTooltip);
			}
		}
	}

	public ItemStack getBlankEncodedCardForPreview() {
		ItemStack output = new ItemStack(this);
		output.setTag(new CompoundTag());
		output.getTag().put(ENCODED_PATTERN_TAG, new CompoundTag());
		return output;
	}

	public ItemStack getPatternForRecipe(EncodedDigistorePattern recipe) {
		// Create the pattern stack.
		ItemStack output = new ItemStack(this);

		if (!recipe.isValid()) {
			return output;
		}

		// Give it a tag.
		output.setTag(new CompoundTag());

		// Put the pattern on the itemstack.
		output.getTag().put(ENCODED_PATTERN_TAG, recipe.serialize());

		// Return the encoded itemstack.
		return output;
	}

	public static boolean hasPattern(ItemStack card) {
		return card.getItem() instanceof DigistorePatternCard && card.hasTag()
				&& card.getTag().contains(DigistorePatternCard.ENCODED_PATTERN_TAG);
	}

	@Override
	public boolean hasModelOverride(BlockState state) {
		return true;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BakedModel getBlockModeOverride(BlockState state, BakedModel existingModel,
			ModelEvent.BakingCompleted event) {
		BakedModel encodedModel = event.getModels().get(StaticPowerAdditionalModels.DIGISTORE_PATTERN_CARD_ENCODED);
		return new PatternCardItemModel(existingModel, encodedModel);
	}

}
