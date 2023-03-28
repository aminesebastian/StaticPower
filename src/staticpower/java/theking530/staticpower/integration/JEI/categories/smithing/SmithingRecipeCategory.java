package theking530.staticpower.integration.JEI.categories.smithing;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import theking530.api.attributes.AttributeInstance;
import theking530.api.attributes.capability.CapabilityAttributable;
import theking530.api.attributes.capability.IAttributable;
import theking530.api.attributes.type.AttributeType;
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.text.PowerTextFormatting;
import theking530.staticcore.gui.widgets.valuebars.GuiFluidBarUtilities;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarUtilities;
import theking530.staticcore.utilities.MetricConverter;
import theking530.staticcore.utilities.SDColor;
import theking530.staticcore.utilities.math.RectangleBounds;
import theking530.staticcore.utilities.math.Vector2D;
import theking530.staticpower.StaticPower;
import theking530.staticpower.client.gui.widgets.AutoSmithProgressBar;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.integration.JEI.BaseJEIRecipeCategory;

public class SmithingRecipeCategory extends BaseJEIRecipeCategory<SmithingRecipeJEIWrapper> {
	public static final RecipeType<SmithingRecipeJEIWrapper> TYPE = new RecipeType<>(new ResourceLocation(StaticPower.MOD_ID, "auto_smith"), SmithingRecipeJEIWrapper.class);

	private final MutableComponent locTitle;
	private final IDrawable background;
	private final IDrawable icon;

	private ITickTimer powerTimer;
	private ITickTimer processingTimer;
	private final AutoSmithProgressBar pBar;

	public SmithingRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper);
		locTitle = Component.translatable(ModBlocks.AutoSmith.get().getDescriptionId());
		background = guiHelper.createBlankDrawable(170, 60);
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.AutoSmith.get()));
		pBar = new AutoSmithProgressBar(49, 19);
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
	public RecipeType<SmithingRecipeJEIWrapper> getRecipeType() {
		return TYPE;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@SuppressWarnings("resource")
	@Override
	public void draw(SmithingRecipeJEIWrapper recipe, IRecipeSlotsView recipeSlotsView, PoseStack matrixStack, double mouseX, double mouseY) {
		GuiDrawUtilities.drawSlot(matrixStack, 16, 16, 50, 0, 0);
		GuiDrawUtilities.drawSlot(matrixStack, 16, 16, 80, 20, 0);
		GuiDrawUtilities.drawSlot(matrixStack, 20, 20, 48, 40, 0);

		GuiDrawUtilities.drawSlot(matrixStack, 68, 60, 102, 0, 0);
		GuiDrawUtilities.drawRectangle(matrixStack, 68, 60, 102, 0, 0.0f, SDColor.DARK_GREY);

		// This doesn't actually draw the fluid, just the bars.
		if (!recipe.getRecipe().getModifierFluid().isEmpty()) {
			GuiFluidBarUtilities.drawFluidBarOutline(matrixStack, 77, 56, 1.0f, 16, 52, MachineSideMode.Never, true);
		}

		// Draw the power bar.
		GuiPowerBarUtilities.drawPowerBar(matrixStack, 5, 6, 16, 48, powerTimer.getValue(), powerTimer.getMaxValue());

		// Draw the arrow progress bar.
		pBar.setCurrentProgress(processingTimer.getValue());
		pBar.setMaxProgress(processingTimer.getMaxValue());
		pBar.renderBehindItems(matrixStack, (int) mouseX, (int) mouseY, 0.0f, RectangleBounds.INFINITE_BOUNDS);

		// Draw the attribute title.
		Minecraft.getInstance().font.drawShadow(matrixStack, Component.translatable("gui.staticcore.attributes").append(": ").getString(), 104.5f, 2,
				SDColor.EIGHT_BIT_WHITE.encodeInInteger());

		// Create a copy of the input.
		ItemStack copy = recipe.getInputItem().copy();

		// Get the attributable of the copy and apply the recipe. Then, list the new
		// values.
		IAttributable originalAttributable = recipe.getInputItem().getCapability(CapabilityAttributable.CAPABILITY_ATTRIBUTABLE).orElse(null);
		IAttributable copyAttributable = copy.getCapability(CapabilityAttributable.CAPABILITY_ATTRIBUTABLE).orElse(null);
		if (originalAttributable != null && copyAttributable != null) {
			// Apply the recipe to the copy.
			recipe.getRecipe().applyToItemStack(copy);

			// Set the initial offset.
			float yOffset = 13;

			// Shrink the font.
			matrixStack.pushPose();
			matrixStack.scale(0.9f, 0.9f, 0.9f);

			// If this recipe repairs, list the repair amount as well.
			if (recipe.getRecipe().performsRepair()) {
				MetricConverter repairMetric = new MetricConverter(recipe.getRecipe().getRepairAmount());
				String repairString = "+" + repairMetric.getValueAsString(true);
				Minecraft.getInstance().font.drawShadow(matrixStack, "Repair", 116, yOffset, ChatFormatting.WHITE.getColor());
				Minecraft.getInstance().font.drawShadow(matrixStack, repairString, 151, yOffset, ChatFormatting.GREEN.getColor());
				yOffset += 9.5f;
			}

			// List the attribute changes.
			for (AttributeType<?> attribute : copyAttributable.getAllAttributes()) {
				MutableComponent differenceLabel = getDifferenceLabel(attribute, originalAttributable, copyAttributable);

				if (differenceLabel != null) {
					Minecraft.getInstance().font.drawShadow(matrixStack, differenceLabel.getString(), 116, yOffset, attribute.getColor().getColor());
					yOffset += 9.5f;
				}
			}
			matrixStack.popPose();
		}
	}

	@SuppressWarnings("resource")
	private <T> MutableComponent getDifferenceLabel(AttributeType<T> type, IAttributable originalAttributable, IAttributable copyAttributable) {
		AttributeInstance<T> originalAttribute = originalAttributable.getAttribute(type);
		AttributeInstance<T> copyAttribute = copyAttributable.getAttribute(type);
		return type.getDifferenceLabel(originalAttribute, copyAttribute);
	}

	@Override
	public List<Component> getTooltipStrings(SmithingRecipeJEIWrapper recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
		List<Component> output = new ArrayList<Component>();
		if (mouseX > 8 && mouseX < 24 && mouseY < 54 && mouseY > 4) {
			output.add(Component.literal("Usage: ").append(PowerTextFormatting.formatPowerToString(recipe.getRecipe().getPowerCost() * recipe.getRecipe().getProcessingTime())));
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
	public void setRecipe(IRecipeLayoutBuilder builder, SmithingRecipeJEIWrapper recipe, IFocusGroup ingredients) {
		builder.addSlot(RecipeIngredientRole.INPUT, 50, 0).addIngredients(recipe.getInput());
		builder.addSlot(RecipeIngredientRole.INPUT, 80, 20).addIngredients(recipe.getRecipe().getModifierMaterial().getIngredient());

		addFluidIngredientSlot(builder, 77, 4, 16, 52, recipe.getRecipe().getModifierFluid());

		builder.addSlot(RecipeIngredientRole.OUTPUT, 50, 42).addItemStack(recipe.getResultItem());

		powerTimer = guiHelper.createTickTimer(recipe.getRecipe().getProcessingTime(), (int) (recipe.getRecipe().getProcessingTime() * recipe.getRecipe().getPowerCost()), true);
		processingTimer = guiHelper.createTickTimer(recipe.getRecipe().getProcessingTime(), recipe.getRecipe().getProcessingTime(), false);
	}
}
