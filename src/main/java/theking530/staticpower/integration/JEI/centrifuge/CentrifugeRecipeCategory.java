package theking530.staticpower.integration.JEI.centrifuge;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.matrix.MatrixStack;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.gui.ingredient.ITooltipCallback;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.progressbars.CentrifugeProgressBar;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarUtilities;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.StaticPower;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.wrappers.centrifuge.CentrifugeRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.integration.JEI.BaseJEIRecipeCategory;

public class CentrifugeRecipeCategory extends BaseJEIRecipeCategory<CentrifugeRecipe> {
	public static final ResourceLocation CENTRIFUGE_UID = new ResourceLocation(StaticPower.MOD_ID, "centrifuge");
	private static final int INTPUT_SLOT = 0;
	private static final int PRIMARY_OUTPUT_SLOT = 1;
	private static final int SECONDARY_OUTPUT_SLOT = 2;
	private static final int TERTIARY_OUTPUT_SLOT = 3;

	private final TranslationTextComponent locTitle;
	private final IDrawable background;
	private final IDrawable icon;
	private final CentrifugeProgressBar pBar;

	private ITickTimer powerTimer;
	private ITickTimer processingTimer;

	public CentrifugeRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper);
		locTitle = new TranslationTextComponent(ModBlocks.Centrifuge.getTranslationKey());
		background = guiHelper.createBlankDrawable(150, 70);
		icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.Centrifuge));
		pBar = new CentrifugeProgressBar(79, 26);
	}

	@Override
	@Nonnull
	public ResourceLocation getUid() {
		return CENTRIFUGE_UID;
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
	public Class<? extends CentrifugeRecipe> getRecipeClass() {
		return CentrifugeRecipe.class;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void draw(CentrifugeRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
		GuiDrawUtilities.drawSlot(80, 6, 16, 16);

		GuiDrawUtilities.drawSlot(78, 46, 20, 20);
		GuiDrawUtilities.drawSlot(104, 32, 20, 20);
		GuiDrawUtilities.drawSlot(52, 32, 20, 20);

		GuiPowerBarUtilities.drawPowerBar(8, 54, 16, 48, 1.0f, powerTimer.getValue(), powerTimer.getMaxValue());

		String rpmText = String.valueOf(recipe.getMinimumSpeed()) + " RPM";
		GuiDrawUtilities.drawColoredRectangle(103, 12, Minecraft.getInstance().fontRenderer.getStringWidth(rpmText) + 4,
				11, 0.0f, Color.GREY);
		Minecraft.getInstance().fontRenderer.drawStringWithShadow(matrixStack, rpmText, 105, 14,
				Color.EIGHT_BIT_WHITE.encodeInInteger());

		pBar.setCurrentProgress(processingTimer.getValue());
		pBar.setMaxProgress(processingTimer.getMaxValue());
		pBar.renderBehindItems(null, (int) mouseX, (int) mouseY, 0.0f);
	}

	@Override
	public List<ITextComponent> getTooltipStrings(CentrifugeRecipe recipe, double mouseX, double mouseY) {
		List<ITextComponent> output = new ArrayList<ITextComponent>();
		if (mouseX > 8 && mouseX < 24 && mouseY < 54 && mouseY > 4) {
			output.add(new StringTextComponent("Usage: ")
					.append(GuiTextUtilities.formatEnergyToString(recipe.getPowerCost() * recipe.getProcessingTime())));
		}

		// Render the progress bar tooltip.
		Vector2D mouse = new Vector2D((float) mouseX, (float) mouseY);
		if (pBar.isPointInsideBounds(mouse)) {
			List<ITextComponent> tooltips = new ArrayList<ITextComponent>();
			pBar.getTooltips(mouse, tooltips, false);
			for (ITextComponent tooltip : tooltips) {
				output.add(tooltip);
			}
			output.add(new StringTextComponent("Required RPM: " + recipe.getMinimumSpeed()));
		}

		return output;
	}

	@Override
	public void setIngredients(CentrifugeRecipe recipe, IIngredients ingredients) {
		List<Ingredient> input = new ArrayList<Ingredient>();
		input.add(recipe.getInput().getIngredient());
		ingredients.setInputIngredients(input);

		List<ItemStack> outputs = new ArrayList<ItemStack>();
		for (ProbabilityItemStackOutput output : recipe.getOutputs()) {
			outputs.add(output.getItem());
		}

		ingredients.setOutputs(VanillaTypes.ITEM, outputs);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, CentrifugeRecipe recipe, IIngredients ingredients) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		guiItemStacks.init(INTPUT_SLOT, true, 79, 5);

		guiItemStacks.init(PRIMARY_OUTPUT_SLOT, false, 79, 47);
		guiItemStacks.init(SECONDARY_OUTPUT_SLOT, false, 53, 33);
		guiItemStacks.init(TERTIARY_OUTPUT_SLOT, false, 105, 33);

		guiItemStacks.set(ingredients);

		// Add the output percentage to the tooltip for the ingredient.
		guiItemStacks.addTooltipCallback(new ITooltipCallback<ItemStack>() {
			@Override
			public void onTooltip(int slotIndex, boolean input, ItemStack ingredient, List<ITextComponent> tooltip) {
				// Only perform for inputs.
				if (!input) {
					// Transform into the output index space.
					int outputIndex = slotIndex - 1;

					// Formulate the output percentage tooltip and then add it.
					ITextComponent outputPercentage = new TranslationTextComponent("gui.staticpower.output_chance")
							.appendString(": ").appendString(
									String.valueOf((int) (recipe.getOutputs().get(outputIndex).getOutputChance() * 100))
											+ "%");
					tooltip.add(outputPercentage);
				}
			}
		});

		// Add the fluid.
		powerTimer = guiHelper.createTickTimer(recipe.getProcessingTime(),
				recipe.getProcessingTime() * recipe.getPowerCost(), true);
		processingTimer = guiHelper.createTickTimer(recipe.getProcessingTime(), recipe.getProcessingTime(), false);
	}
}
