package theking530.staticpower.integration.JEI.poweredgrinder;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.gui.ingredient.ITooltipCallback;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import theking530.common.gui.GuiDrawUtilities;
import theking530.common.gui.widgets.progressbars.GrinderProgressBar;
import theking530.common.gui.widgets.valuebars.GuiPowerBarUtilities;
import theking530.common.utilities.Vector2D;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.wrappers.grinder.GrinderRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.integration.JEI.BaseJEIRecipeCategory;
import theking530.staticpower.utilities.MetricConverter;
import theking530.staticpower.utilities.Reference;

public class PoweredGrinderRecipeCategory extends BaseJEIRecipeCategory<GrinderRecipe> {
	public static final ResourceLocation GRINDER_UID = new ResourceLocation(Reference.MOD_ID, "grinder");
	private static final int INTPUT_SLOT = 0;
	private static final int PRIMARY_OUTPUT_SLOT = 1;
	private static final int SECONDARY_OUTPUT_SLOT = 2;
	private static final int TERTIARY_OUTPUT_SLOT = 3;

	private final TranslationTextComponent locTitle;
	private final IDrawable background;
	private final IDrawable icon;
	private final GrinderProgressBar pBar;

	private ITickTimer powerTimer;
	private ITickTimer processingTimer;

	public PoweredGrinderRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper);
		locTitle = new TranslationTextComponent(ModBlocks.PoweredGrinder.getTranslationKey());
		background = guiHelper.createBlankDrawable(150, 70);
		icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.PoweredGrinder));
		pBar = new GrinderProgressBar(79, 26);
	}

	@Override
	@Nonnull
	public ResourceLocation getUid() {
		return GRINDER_UID;
	}

	@Override
	@Nonnull
	public String getTitle() {
		return locTitle.getFormattedText();
	}

	@Override
	@Nonnull
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public Class<? extends GrinderRecipe> getRecipeClass() {
		return GrinderRecipe.class;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void draw(GrinderRecipe recipe, double mouseX, double mouseY) {
		GuiDrawUtilities.drawSlot(80, 6, 16, 16);

		GuiDrawUtilities.drawSlot(78, 46, 20, 20);
		GuiDrawUtilities.drawSlot(104, 32, 20, 20);
		GuiDrawUtilities.drawSlot(52, 32, 20, 20);

		GuiPowerBarUtilities.drawPowerBar(8, 54, 16, 48, 1.0f, powerTimer.getValue(), powerTimer.getMaxValue());

		pBar.setCurrentProgress(processingTimer.getValue());
		pBar.setMaxProgress(processingTimer.getMaxValue());
		pBar.renderBehindItems((int) mouseX, (int) mouseY, 0.0f);
	}

	@Override
	public List<String> getTooltipStrings(GrinderRecipe recipe, double mouseX, double mouseY) {
		List<String> output = new ArrayList<String>();
		if (mouseX > 8 && mouseX < 24 && mouseY < 54 && mouseY > 4) {
			String powerCost = new MetricConverter(recipe.getPowerCost() * recipe.getProcessingTime()).getValueAsString(true);
			output.add("Usage: " + powerCost + "FE");
		}

		// Render the progress bar tooltip.
		Vector2D mouse = new Vector2D((float) mouseX, (float) mouseY);
		if (pBar.isPointInsideBounds(mouse)) {
			List<ITextComponent> tooltips = new ArrayList<ITextComponent>();
			pBar.getTooltips(mouse, tooltips, false);
			for (ITextComponent tooltip : tooltips) {
				output.add(tooltip.getFormattedText());
			}
		}

		return output;
	}

	@Override
	public void setIngredients(GrinderRecipe recipe, IIngredients ingredients) {
		List<Ingredient> input = new ArrayList<Ingredient>();
		input.add(recipe.getInputIngredient().getIngredient());
		ingredients.setInputIngredients(input);

		List<ItemStack> outputs = new ArrayList<ItemStack>();
		for (ProbabilityItemStackOutput output : recipe.getOutputItems()) {
			outputs.add(output.getItem());
		}

		ingredients.setOutputs(VanillaTypes.ITEM, outputs);

	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, GrinderRecipe recipe, IIngredients ingredients) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		guiItemStacks.init(INTPUT_SLOT, true, 79, 5);
		
		guiItemStacks.init(PRIMARY_OUTPUT_SLOT, false, 79, 47);
		guiItemStacks.init(SECONDARY_OUTPUT_SLOT, false, 53, 33);
		guiItemStacks.init(TERTIARY_OUTPUT_SLOT, false, 105, 33);

		guiItemStacks.set(ingredients);

		// Add the outptu percentage to the tooltip for the ingredient.
		guiItemStacks.addTooltipCallback(new ITooltipCallback<ItemStack>() {
			@Override
			public void onTooltip(int slotIndex, boolean input, ItemStack ingredient, List<String> tooltip) {
				// Only perform for inputs.
				if (!input) {
					// Trasnfrom into the output index space.
					int outputIndex = slotIndex - 1;

					// Formulate the output percentage tooltip and then add it.
					String outputPercentage = new TranslationTextComponent("gui.staticpower.output_chance").appendText(": ").appendText(String.valueOf((int) (recipe.getOutputItems()[outputIndex].getOutputChance() * 100)) + "%").getFormattedText();
					tooltip.add(outputPercentage);
				}
			}
		});

		// Add the fluid.
		powerTimer = guiHelper.createTickTimer(recipe.getProcessingTime(), recipe.getProcessingTime() * recipe.getPowerCost(), true);
		processingTimer = guiHelper.createTickTimer(recipe.getProcessingTime(), recipe.getProcessingTime(), false);
	}
}
