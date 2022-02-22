package theking530.staticpower.integration.JEI.categories.thermalconductivity;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.utilities.Color;
import theking530.staticpower.StaticPower;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.integration.JEI.BaseJEIRecipeCategory;
import theking530.staticpower.integration.JEI.PluginJEI;
import theking530.staticpower.integration.JEI.categories.thermalconductivity.ThermalConductivityRecipeProvider.ThermalConductivityJEIRecipeWrapper;

public class ThermalConductivityRecipeCategory extends BaseJEIRecipeCategory<ThermalConductivityJEIRecipeWrapper> {
	public static final ResourceLocation UID = new ResourceLocation(StaticPower.MOD_ID, "thermal_conductivity");

	private final TranslatableComponent locTitle;
	private final IDrawable background;
	private final IDrawable icon;

	public ThermalConductivityRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper);
		locTitle = new TranslatableComponent("gui.staticpower.heat");
		background = guiHelper.createBlankDrawable(170, 65);
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM, new ItemStack(ModBlocks.CopperHeatSink));
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
	public Class<? extends ThermalConductivityJEIRecipeWrapper> getRecipeClass() {
		return ThermalConductivityJEIRecipeWrapper.class;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void draw(ThermalConductivityJEIRecipeWrapper recipe, PoseStack matrixStack, double mouseX, double mouseY) {
		GuiDrawUtilities.drawSlot(matrixStack, 20, 20, 5, 5, 0);
		GuiDrawUtilities.drawSlot(matrixStack, 135, 55, 30, 5, 0);

		String conductivity = new TextComponent("Heat Conductivity: ").append(ChatFormatting.BLUE.toString())
				.append(GuiTextUtilities.formatConductivityToString(recipe.getRecipe().getThermalConductivity())).getString();

		String heat = new TextComponent("Heat Generation: ").append(ChatFormatting.GOLD.toString())
				.append(GuiTextUtilities.formatHeatRateToString(recipe.getRecipe().getHeatAmount())).getString();

		String overheatTemp = new TextComponent("<- Overheat: ").append(ChatFormatting.RED.toString())
				.append(GuiTextUtilities.formatHeatToString(recipe.getRecipe().getOverheatedTemperature())).getString();

		int yPos = 15;
		int xPos = 160;

		if (recipe.getRecipe().getThermalConductivity() > 0) {
			GuiDrawUtilities.drawString(matrixStack, conductivity, xPos, yPos, 0.0f, 1.0f, Color.EIGHT_BIT_WHITE, true);
			yPos += 10;
		}

		if (recipe.getRecipe().getHeatAmount() > 0) {
			GuiDrawUtilities.drawString(matrixStack, heat, xPos, yPos, 0.0f, 1.0f, Color.EIGHT_BIT_WHITE, true);
		}

		if (recipe.getRecipe().hasOverheatingBehaviour()) {
			GuiDrawUtilities.drawSlot(matrixStack, 20, 20, 35, 32, 0);
			GuiDrawUtilities.drawString(matrixStack, overheatTemp, xPos, 44f, 0.0f, 1.0f, Color.EIGHT_BIT_WHITE, true);
		}

		if (!recipe.getFluidInput().isEmpty()) {
			if (recipe.getFluidInput().getFluid().getRegistryName().toString().contains("flowing")) {
				GuiDrawUtilities.drawStringWithSize(matrixStack, "(Flowing)", 26f, 31, 0.0f, 0.5f, ChatFormatting.BLUE, false);
			} else {
				GuiDrawUtilities.drawStringWithSize(matrixStack, "(Still)", 21.5f, 31, 0.0f, 0.5f, ChatFormatting.BLUE, false);
			}
		}

		if (!recipe.getOutputFluid().isEmpty()) {
			if (recipe.getOutputFluid().getFluid().getRegistryName().toString().contains("flowing")) {
				GuiDrawUtilities.drawStringWithSize(matrixStack, "(Flowing)", 56f, 58, 0.0f, 0.5f, ChatFormatting.WHITE, false);
			} else {
				GuiDrawUtilities.drawStringWithSize(matrixStack, "(Still)", 51.5f, 58, 0.0f, 0.5f, ChatFormatting.WHITE, false);
			}
		}

		// If the input or output is fire, manually render it.
		if (recipe.getIsFireInput() || recipe.getHasFireOutput()) {
			Minecraft mc = Minecraft.getInstance();
			BlockRenderDispatcher blockRenderer = mc.getBlockRenderer();
			PoseStack blockStack = new PoseStack();

			if (recipe.getIsFireInput()) {
				blockStack.pushPose();
				blockStack.translate(-0.5, 4.7f, 0.0f);
				blockStack.scale(0.7f, 0.7f, 0.7f);
				blockStack.mulPose(new Quaternion(32, 45, 0, true));

				BlockState fireState = Blocks.FIRE.defaultBlockState();
				blockRenderer.renderSingleBlock(fireState, blockStack, mc.renderBuffers().bufferSource(), 15728880, OverlayTexture.NO_OVERLAY);
				blockStack.popPose();
			} else if (recipe.getHasFireOutput()) {
				blockStack.pushPose();
				blockStack.translate(1.4, -2.0f, -1.0f);
				blockStack.scale(0.7f, 0.7f, 0.7f);
				blockStack.mulPose(new Quaternion(32, 45, 0, true));

				BlockState fireState = Blocks.FIRE.defaultBlockState();
				blockRenderer.renderSingleBlock(fireState, blockStack, mc.renderBuffers().bufferSource(), 15728880, OverlayTexture.NO_OVERLAY);
				blockStack.popPose();
			}
		}
	}

	@Override
	public List<Component> getTooltipStrings(ThermalConductivityJEIRecipeWrapper recipe, double mouseX, double mouseY) {
		List<Component> output = new ArrayList<Component>();
		return output;
	}

	@Override
	public void setIngredients(ThermalConductivityJEIRecipeWrapper recipe, IIngredients ingredients) {
		// Add the input ingredients.
		if (!recipe.getRecipe().isAirRecipe()) {
			List<Ingredient> input = new ArrayList<Ingredient>();
			input.add(recipe.getInput());
			ingredients.setInputIngredients(input);
		}

		if (!recipe.getFluidInput().isEmpty()) {
			// Set the input fluid.
			ingredients.setInput(VanillaTypes.FLUID, recipe.getFluidInput());
		}

		// Add the output fluid if one exists.
		if (!recipe.getOutputFluid().isEmpty()) {
			ingredients.setOutput(VanillaTypes.FLUID, recipe.getOutputFluid());
		}

		// Add the output items.
		if (!recipe.getOutputBlock().isEmpty()) {
			ingredients.setOutput(VanillaTypes.ITEM, recipe.getOutputBlock());
		}
		if (!recipe.getOutputItem().isEmpty()) {
			ingredients.setOutput(PluginJEI.PROBABILITY_ITEM_STACK, recipe.getOutputItem());
		}
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, ThermalConductivityJEIRecipeWrapper recipe, IIngredients ingredients) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

		// Set the input.
		if (!recipe.getRecipe().isAirRecipe()) {
			guiItemStacks.init(0, true, 6, 6);
		}

		// Set the input fluid.
		if (!recipe.getFluidInput().isEmpty()) {
			// Add the fluid.
			IGuiFluidStackGroup fluids = recipeLayout.getFluidStacks();
			fluids.init(1, true, 5, 5, 20, 20, recipe.getFluidInput().getAmount(), true, null);
			fluids.set(ingredients);
		}

		// Set the overheated block output.
		if (!recipe.getOutputBlock().isEmpty()) {
			guiItemStacks.init(2, false, 37, 34);
		}

		guiItemStacks.set(ingredients);

		// Set the overheated item output.
		if (!recipe.getOutputItem().isEmpty()) {
			IGuiIngredientGroup<ProbabilityItemStackOutput> probabilityStacks = recipeLayout.getIngredientsGroup(PluginJEI.PROBABILITY_ITEM_STACK);
			probabilityStacks.init(3, false, 37, 34);
			probabilityStacks.set(ingredients);
		}

		// Add the fluid.
		if (!recipe.getOutputFluid().isEmpty()) {
			IGuiFluidStackGroup fluids = recipeLayout.getFluidStacks();
			fluids.init(4, false, 35, 32, 20, 20, recipe.getOutputFluid().getAmount(), false, null);
			fluids.set(ingredients);
		}
	}
}
