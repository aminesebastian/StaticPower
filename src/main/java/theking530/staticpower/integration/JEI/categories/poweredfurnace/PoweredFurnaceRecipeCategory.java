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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.progressbars.ArrowProgressBar;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarUtilities;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.StaticPower;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.integration.JEI.BaseJEIRecipeCategory;
import theking530.staticpower.tileentities.powered.poweredfurnace.TileEntityPoweredFurnace;

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
		locTitle = new TranslatableComponent(ModBlocks.PoweredFurnace.getDescriptionId());
		background = guiHelper.createBlankDrawable(120, 60);
		icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.PoweredFurnace));
		pBar = new ArrowProgressBar(62, 19);
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
	public Class<? extends SmeltingRecipe> getRecipeClass() {
		return SmeltingRecipe.class;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void draw(SmeltingRecipe recipe, PoseStack matrixStack, double mouseX, double mouseY) {
		GuiDrawUtilities.drawSlot(matrixStack, 41, 19, 16, 16, 0);
		GuiDrawUtilities.drawSlot(matrixStack, 89, 17, 20, 20, 0);

		// This doesn't actually draw the fluid, just the bars.
		GuiPowerBarUtilities.drawPowerBar(matrixStack, 8, 54, 16, 48, 1.0f, powerTimer.getValue(), powerTimer.getMaxValue());

		pBar.setCurrentProgress(processingTimer.getValue());
		pBar.setMaxProgress(processingTimer.getMaxValue());
		pBar.renderBehindItems(matrixStack, (int) mouseX, (int) mouseY, 0.0f);
	}

	@Override
	public List<Component> getTooltipStrings(SmeltingRecipe recipe, double mouseX, double mouseY) {
		List<Component> output = new ArrayList<Component>();
		if (mouseX > 8 && mouseX < 24 && mouseY < 54 && mouseY > 4) {
			output.add(new TextComponent("Usage: ")
					.append(GuiTextUtilities.formatEnergyToString(TileEntityPoweredFurnace.getCookTime(recipe) * StaticPowerConfig.SERVER.poweredFurnacePowerUsage.get())));
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

		powerTimer = guiHelper.createTickTimer(TileEntityPoweredFurnace.getCookTime(recipe),
				(int) (TileEntityPoweredFurnace.getCookTime(recipe) * StaticPowerConfig.SERVER.poweredFurnacePowerUsage.get()), true);
		processingTimer = guiHelper.createTickTimer(TileEntityPoweredFurnace.getCookTime(recipe), TileEntityPoweredFurnace.getCookTime(recipe), false);
	}
}
