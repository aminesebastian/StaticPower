package theking530.staticpower.integration.JEI.categories.smithing;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.matrix.MatrixStack;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import theking530.api.attributes.capability.CapabilityAttributable;
import theking530.api.attributes.capability.IAttributable;
import theking530.api.attributes.defenitions.AbstractAttributeDefenition;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.progressbars.AutoSmithProgressBar;
import theking530.staticcore.gui.widgets.valuebars.GuiFluidBarUtilities;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarUtilities;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.StaticPower;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.integration.JEI.BaseJEIRecipeCategory;
import theking530.staticpower.integration.JEI.categories.smithing.SmithingRecipeProvider.AutoSmithRecipeJEIWrapper;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.utilities.MetricConverter;

public class SmithingRecipeCategory extends BaseJEIRecipeCategory<AutoSmithRecipeJEIWrapper> {
	public static final ResourceLocation UID = new ResourceLocation(StaticPower.MOD_ID, "auto_smith");
	private static final int INTPUT_SLOT = 0;
	private static final int MODIFIER_SLOT = 1;
	private static final int FLUID_MODIFIER_SLOT = 2;
	private static final int OUTPUT_SLOT = 3;

	private final TranslationTextComponent locTitle;
	private final IDrawable background;
	private final IDrawable icon;

	private ITickTimer powerTimer;
	private ITickTimer processingTimer;
	private final AutoSmithProgressBar pBar;

	public SmithingRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper);
		locTitle = new TranslationTextComponent(ModBlocks.AutoSmith.getTranslationKey());
		background = guiHelper.createBlankDrawable(170, 60);
		icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.AutoSmith));
		pBar = new AutoSmithProgressBar(49, 19);
	}

	@Override
	@Nonnull
	public ResourceLocation getUid() {
		return UID;
	}

	@Override
	@Nonnull
	public String getTitle() {
		return locTitle.getString();
	}

	@Override
	@Nonnull
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public Class<? extends AutoSmithRecipeJEIWrapper> getRecipeClass() {
		return AutoSmithRecipeJEIWrapper.class;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void draw(AutoSmithRecipeJEIWrapper recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
		GuiDrawUtilities.drawSlot(matrixStack, 50, 0, 16, 16, 0);
		GuiDrawUtilities.drawSlot(matrixStack, 80, 20, 16, 16, 0);
		GuiDrawUtilities.drawSlot(matrixStack, 48, 40, 20, 20, 0);

		GuiDrawUtilities.drawSlot(matrixStack, 102, 0, 68, 60, 0);
		GuiDrawUtilities.drawColoredRectangle(matrixStack, 102, 0, 68, 60, 0.0f, Color.DARK_GREY);

		// This doesn't actually draw the fluid, just the bars.
		if (!recipe.getRecipe().getModifierFluid().isEmpty()) {
			GuiFluidBarUtilities.drawFluidBar(matrixStack, recipe.getRecipe().getModifierFluid(), 0, 0, 77, 56, 1.0f, 16, 52, MachineSideMode.Never, true);
		}

		// Draw the power bar.
		GuiPowerBarUtilities.drawPowerBar(matrixStack, 8, 54, 16, 48, 1.0f, powerTimer.getValue(), powerTimer.getMaxValue());

		// Draw the arrow progress bar.
		pBar.setCurrentProgress(processingTimer.getValue());
		pBar.setMaxProgress(processingTimer.getMaxValue());
		pBar.renderBehindItems(matrixStack, (int) mouseX, (int) mouseY, 0.0f);

		// Draw the attribute title.
		Minecraft.getInstance().fontRenderer.drawStringWithShadow(matrixStack, new TranslationTextComponent("gui.staticpower.attributes").appendString(": ").getString(), 104.5f, 2,
				Color.EIGHT_BIT_WHITE.encodeInInteger());

		// Create a copy of the input.
		ItemStack copy = recipe.getInputItem().copy();

		// Get the attributable of the copy and apply the recipe. Then, list the new
		// values.
		IAttributable originalAttributable = recipe.getInputItem().getCapability(CapabilityAttributable.ATTRIBUTABLE_CAPABILITY).orElse(null);
		IAttributable copyAttributable = copy.getCapability(CapabilityAttributable.ATTRIBUTABLE_CAPABILITY).orElse(null);
		if (originalAttributable != null && copyAttributable != null) {
			// Apply the recipe to the copy.
			recipe.getRecipe().applyToItemStack(copy);

			// Set the initial offset.
			float yOffset = 13;

			// Shrink the font.
			matrixStack.push();
			matrixStack.scale(0.9f, 0.9f, 0.9f);

			// If this recipe repairs, list the repair amount as well.
			if (recipe.getRecipe().performsRepair()) {
				MetricConverter repairMetric = new MetricConverter(recipe.getRecipe().getRepairAmount());
				String repairString = "+" + repairMetric.getValueAsString(true);
				Minecraft.getInstance().fontRenderer.drawStringWithShadow(matrixStack, "Repair", 116, yOffset, TextFormatting.WHITE.getColor());
				Minecraft.getInstance().fontRenderer.drawStringWithShadow(matrixStack, repairString, 151, yOffset, TextFormatting.GREEN.getColor());
				yOffset += 9.5f;
			}

			// List the attribute changes.
			for (ResourceLocation attribId : copyAttributable.getAllAttributes()) {
				AbstractAttributeDefenition<?, ?> originalAttribute = originalAttributable.getAttribute(attribId);
				AbstractAttributeDefenition<?, ?> copyAttribute = copyAttributable.getAttribute(attribId);
				IFormattableTextComponent differenceLabel = copyAttribute.getDifferenceLabel(originalAttribute);

				if (differenceLabel != null) {
					Minecraft.getInstance().fontRenderer.drawStringWithShadow(matrixStack, differenceLabel.getString(), 116, yOffset, originalAttribute.getColor().getColor());
					yOffset += 9.5f;
				}
			}
			matrixStack.pop();
		}
	}

	@Override
	public List<ITextComponent> getTooltipStrings(AutoSmithRecipeJEIWrapper recipe, double mouseX, double mouseY) {
		List<ITextComponent> output = new ArrayList<ITextComponent>();
		if (mouseX > 8 && mouseX < 24 && mouseY < 54 && mouseY > 4) {
			output.add(new StringTextComponent("Usage: ").append(GuiTextUtilities.formatEnergyToString(recipe.getRecipe().getPowerCost() * recipe.getRecipe().getProcessingTime())));
		}

		// Render the progress bar tooltip.
		Vector2D mouse = new Vector2D((float) mouseX, (float) mouseY);
		if (pBar.isPointInsideBounds(mouse)) {
			List<ITextComponent> tooltips = new ArrayList<ITextComponent>();
			pBar.getTooltips(mouse, tooltips, false);
			for (ITextComponent tooltip : tooltips) {
				output.add(tooltip);
			}
		}

		return output;
	}

	@Override
	public void setIngredients(AutoSmithRecipeJEIWrapper recipe, IIngredients ingredients) {
		// Add the input item.
		List<Ingredient> input = new ArrayList<Ingredient>();
		input.add(recipe.getInput());
		input.add(recipe.getRecipe().getModifierMaterial().getIngredient());
		ingredients.setInputIngredients(input);

		// Set the filled bottle output itemstack.
		ingredients.setOutput(VanillaTypes.ITEM, recipe.getRecipeOutput());

		// Set the input fluid.
		ingredients.setInput(VanillaTypes.FLUID, recipe.getRecipe().getModifierFluid());
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, AutoSmithRecipeJEIWrapper recipe, IIngredients ingredients) {
		// Add the input and output slots.
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		guiItemStacks.init(INTPUT_SLOT, true, 49, -1);
		guiItemStacks.init(MODIFIER_SLOT, true, 79, 19);
		guiItemStacks.init(OUTPUT_SLOT, false, 49, 41);

		// Set the items.
		guiItemStacks.set(ingredients);

		// Add the fluid.
		IGuiFluidStackGroup fluids = recipeLayout.getFluidStacks();
		if (!recipe.getRecipe().getModifierFluid().isEmpty()) {
			fluids.init(FLUID_MODIFIER_SLOT, true, 77, 4, 16, 52, getFluidTankDisplaySize(recipe.getRecipe().getModifierFluid()), false, null);
			fluids.set(ingredients);
		}

		powerTimer = guiHelper.createTickTimer(recipe.getRecipe().getProcessingTime(), (int) (recipe.getRecipe().getProcessingTime() * recipe.getRecipe().getPowerCost()), true);
		processingTimer = guiHelper.createTickTimer(recipe.getRecipe().getProcessingTime(), recipe.getRecipe().getProcessingTime(), false);
	}
}
