package theking530.staticpower.integration.JEI.categories.poweredfurnace;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.text.PowerTextFormatting;
import theking530.staticcore.gui.widgets.progressbars.ArrowProgressBar;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarUtilities;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.RectangleBounds;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.StaticPower;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blockentities.machines.poweredfurnace.BlockEntityPoweredFurnace;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.integration.JEI.BaseJEIRecipeCategory;

public class PoweredFurnaceRecipeCategory extends BaseJEIRecipeCategory<SmeltingRecipe> {
	public static final ResourceLocation UID = new ResourceLocation(StaticPower.MOD_ID, "powered_furnace");
	private static final int INTPUT_SLOT = 0;
	private static final int OUTPUT_SLOT = 1;

	private final TranslatableComponent locTitle;
	private final IDrawable background;
	private final IDrawable icon;
	private final ArrowProgressBar pBar;

	private ITickTimer powerTimer;
	private ITickTimer processingTimer;

	public PoweredFurnaceRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper);
		locTitle = new TranslatableComponent(ModBlocks.PoweredFurnace.get().getDescriptionId());
		background = guiHelper.createBlankDrawable(120, 60);
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM, new ItemStack(ModBlocks.PoweredFurnace.get()));
		pBar = new ArrowProgressBar(62, 19);
	}

	@Override
	@Nonnull
	public ResourceLocation getUid() {
		return UID;
	}

	@Override
	@Nonnull
	public Component getTitle() {
		return locTitle;
	}

	@Override
	@Nonnull
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public Class<? extends SmeltingRecipe> getRecipeClass() {
		return SmeltingRecipe.class;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void draw(SmeltingRecipe recipe, PoseStack matrixStack, double mouseX, double mouseY) {
		GuiDrawUtilities.drawSlot(matrixStack, 16, 16, 41, 19, 0);
		GuiDrawUtilities.drawSlot(matrixStack, 20, 20, 89, 17, 0);

		// This doesn't actually draw the fluid, just the bars.
		GuiPowerBarUtilities.drawPowerBar(matrixStack, 5, 6, 16, 48, powerTimer.getValue(), powerTimer.getMaxValue());

		pBar.setCurrentProgress(processingTimer.getValue());
		pBar.setMaxProgress(processingTimer.getMaxValue());
		pBar.renderBehindItems(matrixStack, (int) mouseX, (int) mouseY, 0.0f, RectangleBounds.INFINITE_BOUNDS);
		
		float experience = recipe.getExperience();
		if (experience > 0) {
			TranslatableComponent experienceString = new TranslatableComponent("gui.staticpower.experience", experience);
			GuiDrawUtilities.drawStringCentered(matrixStack, experienceString.getString(), 100, 12, 0.0f, 0.9f, Color.EIGHT_BIT_GREY, false);
		}
	}

	@Override
	public List<Component> getTooltipStrings(SmeltingRecipe recipe, double mouseX, double mouseY) {
		List<Component> output = new ArrayList<Component>();
		if (mouseX > 8 && mouseX < 24 && mouseY < 54 && mouseY > 4) {
			output.add(
					new TextComponent("Usage: ").append(PowerTextFormatting.formatPowerToString(BlockEntityPoweredFurnace.getCookTime(recipe) * StaticPowerConfig.SERVER.poweredFurnacePowerUsage.get())));
		}

		// Render the progress bar tooltip.
		Vector2D mouse = new Vector2D((float) mouseX, (float) mouseY);
		if (pBar.isPointInsideBounds(mouse)) {
			List<Component> tooltips = new ArrayList<Component>();
			pBar.getTooltips(mouse, tooltips, false);
			for (Component tooltip : tooltips) {
				output.add(tooltip);
			}
		}

		return output;
	}

	@Override
	public void setIngredients(SmeltingRecipe recipe, IIngredients ingredients) {
		// Sanity Check
		if (recipe.getIngredients().size() != 1) {
			return;
		}

		// Add the input ingredients.
		List<Ingredient> input = new ArrayList<Ingredient>();
		input.add(recipe.getIngredients().get(0));
		ingredients.setInputIngredients(input);

		// Add the output item.
		List<ItemStack> outputs = new ArrayList<ItemStack>();
		outputs.add(recipe.getResultItem());
		ingredients.setOutputs(VanillaTypes.ITEM, outputs);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, SmeltingRecipe recipe, IIngredients ingredients) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		guiItemStacks.init(INTPUT_SLOT, true, 40, 18);
		guiItemStacks.init(OUTPUT_SLOT, false, 90, 18);
		guiItemStacks.set(ingredients);

		powerTimer = guiHelper.createTickTimer(BlockEntityPoweredFurnace.getCookTime(recipe),
				(int) (BlockEntityPoweredFurnace.getCookTime(recipe) * StaticPowerConfig.SERVER.poweredFurnacePowerUsage.get()), true);
		processingTimer = guiHelper.createTickTimer(BlockEntityPoweredFurnace.getCookTime(recipe), BlockEntityPoweredFurnace.getCookTime(recipe), false);
	}
}
