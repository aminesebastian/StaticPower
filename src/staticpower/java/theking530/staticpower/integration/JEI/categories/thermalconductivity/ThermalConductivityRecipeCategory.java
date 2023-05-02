package theking530.staticpower.integration.JEI.categories.thermalconductivity;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.text.GuiTextUtilities;
import theking530.staticcore.utilities.SDColor;
import theking530.staticpower.StaticPower;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.integration.JEI.BaseJEIRecipeCategory;
import theking530.staticpower.integration.JEI.PluginJEI;

public class ThermalConductivityRecipeCategory extends BaseJEIRecipeCategory<ThermalConductivityJEIRecipeWrapper> {
	public static final RecipeType<ThermalConductivityJEIRecipeWrapper> TYPE = new RecipeType<>(
			new ResourceLocation(StaticPower.MOD_ID, "thermal_conductivity"),
			ThermalConductivityJEIRecipeWrapper.class);

	private final MutableComponent locTitle;
	private final IDrawable background;
	private final IDrawable icon;

	public ThermalConductivityRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper);
		locTitle = Component.translatable("gui.staticpower.heat");
		background = guiHelper.createBlankDrawable(170, 45);
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
				new ItemStack(ModBlocks.CopperHeatSink.get()));
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
	public RecipeType<ThermalConductivityJEIRecipeWrapper> getRecipeType() {
		return TYPE;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void draw(ThermalConductivityJEIRecipeWrapper recipe, IRecipeSlotsView recipeSlotsView,
			PoseStack matrixStack, double mouseX, double mouseY) {
		// TODO: Clean this up to support the new thermal system.
		GuiDrawUtilities.drawSlot(matrixStack, 20, 20, 5, 5, 0);
		GuiDrawUtilities.drawSlot(matrixStack, 135, 35, 30, 5, 0);

		int yPos = 14;
		int xPos = 160;

		if (recipe.getRecipe().hasActiveTemperature()) {
			String temperature = Component.literal("Temperature: ").append(ChatFormatting.GOLD.toString())
					.append(GuiTextUtilities.formatHeatRateToString(recipe.getRecipe().getTemperature())).getString();
			GuiDrawUtilities.drawString(matrixStack, temperature, xPos, yPos, 0.0f, 1.0f, SDColor.EIGHT_BIT_WHITE,
					true);
			yPos += 11;
		}

		String temperature = Component.literal("Conductivity: ").append(ChatFormatting.GREEN.toString())
				.append(GuiTextUtilities.formatConductivityToString(recipe.getRecipe().getConductivity())).getString();
		GuiDrawUtilities.drawString(matrixStack, temperature, xPos, yPos, 0.0f, 1.0f, SDColor.EIGHT_BIT_WHITE, true);
		yPos += 11;

		if (recipe.getRecipe().hasOverheatingBehaviour()) {
			String overheatTemp = Component.literal("<- Overheat: ").append(ChatFormatting.RED.toString())
					.append(GuiTextUtilities
							.formatHeatToString(recipe.getRecipe().getOverheatingBehaviour().getTemperature()))
					.getString();
			GuiDrawUtilities.drawSlot(matrixStack, 20, 20, 35, 16, 0);
			GuiDrawUtilities.drawString(matrixStack, overheatTemp, xPos, yPos, 0.0f, 1.0f, SDColor.EIGHT_BIT_WHITE,
					true);
			yPos += 11;
		}

		if (recipe.getRecipe().hasFreezeBehaviour()) {
			String overheatTemp = Component.literal("<- Freeze: ").append(ChatFormatting.RED.toString())
					.append(GuiTextUtilities
							.formatHeatToString(recipe.getRecipe().getFreezingBehaviour().getTemperature()))
					.getString();
			GuiDrawUtilities.drawSlot(matrixStack, 20, 20, 35, 16, 0);
			GuiDrawUtilities.drawString(matrixStack, overheatTemp, xPos, yPos, 0.0f, 1.0f, SDColor.EIGHT_BIT_WHITE,
					true);
		}

//		if (!recipe.getFluidInput().isEmpty()) {
//			if (ForgeRegistries.FLUIDS.getKey(recipe.getFluidInput().getFluid()).toString().contains("flowing")) {
//				GuiDrawUtilities.drawStringWithSize(matrixStack, "(Flowing)", 26f, 31, 0.0f, 0.5f, ChatFormatting.BLUE,
//						false);
//			} else {
//				GuiDrawUtilities.drawStringWithSize(matrixStack, "(Still)", 21.5f, 31, 0.0f, 0.5f, ChatFormatting.BLUE,
//						false);
//			}
//		}
//
//		if (!recipe.getOutputFluid().isEmpty()) {
//			if (ForgeRegistries.FLUIDS.getKey(recipe.getOutputFluid().getFluid()).toString().contains("flowing")) {
//				GuiDrawUtilities.drawStringWithSize(matrixStack, "(Flowing)", 56f, 13, 0.0f, 0.5f, ChatFormatting.WHITE,
//						false);
//			} else {
//				GuiDrawUtilities.drawStringWithSize(matrixStack, "(Still)", 51.5f, 13, 0.0f, 0.5f, ChatFormatting.WHITE,
//						false);
//			}
//		}

		// If the input or output is fire, manually render it.
		if (recipe.getIsFireInput() || recipe.getHasFireOutput()) {
			Minecraft mc = Minecraft.getInstance();
			BlockRenderDispatcher blockRenderer = mc.getBlockRenderer();
			PoseStack blockStack = new PoseStack();

			blockStack.pushPose();
			if (recipe.getIsFireInput()) {
				blockStack.translate(1.8, -13.8f, -9.25f);
			} else if (recipe.getHasFireOutput()) {
				blockStack.translate(1.4, -1.0f, -9.25f);
			}
			blockStack.scale(0.7f, 0.7f, 0.7f);
			blockStack.mulPose(new Quaternion(32, 45, 0, true));
			BlockState fireState = Blocks.FIRE.defaultBlockState();
			blockRenderer.renderSingleBlock(fireState, blockStack, mc.renderBuffers().bufferSource(), 15728880,
					OverlayTexture.NO_OVERLAY);
			blockStack.popPose();
		}
	}

	@Override
	public List<Component> getTooltipStrings(ThermalConductivityJEIRecipeWrapper recipe,
			IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
		List<Component> output = new ArrayList<Component>();
		return output;
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, ThermalConductivityJEIRecipeWrapper recipe,
			IFocusGroup ingredients) {
		// Set the input.
		if (!recipe.getRecipe().isAirRecipe()) {
			builder.addSlot(RecipeIngredientRole.INPUT, 6, 6).addIngredients(recipe.getBlocks());
		}

		// Set the input fluid.
		if (!recipe.getFluidInput().isEmpty()) {
			addFluidIngredientSlot(builder, 5, 5, 20, 20, recipe.getFluidInput());
		}

		// Set the overheated block output.
		if (recipe.getRecipe().hasOverheatingBehaviour()) {
			ConcretizedThermalConductivityBehaviour overheat = recipe.getConcretizedOverheat();
			if (!overheat.block().isEmpty()) {
				builder.addSlot(RecipeIngredientRole.OUTPUT, 37, 20).addIngredient(VanillaTypes.ITEM_STACK,
						overheat.block());
			}

			if (!overheat.fluid().isEmpty()) {
				builder.addSlot(RecipeIngredientRole.OUTPUT, 35, 16)
						.addIngredient(ForgeTypes.FLUID_STACK, overheat.fluid())
						.setFluidRenderer(getFluidTankDisplaySize(overheat.fluid()), false, 16, 48);
			}

			if (!overheat.item().isEmpty()) {
				builder.addSlot(RecipeIngredientRole.OUTPUT, 37, 18).addIngredient(PluginJEI.PROBABILITY_ITEM_STACK,
						overheat.item());
			}
		}

		// Set the frozen block output.
		if (recipe.getRecipe().hasFreezeBehaviour()) {
			ConcretizedThermalConductivityBehaviour freeze = recipe.getConcretizedFreeze();
			if (!freeze.block().isEmpty()) {
				builder.addSlot(RecipeIngredientRole.OUTPUT, 37, 20).addIngredient(VanillaTypes.ITEM_STACK,
						freeze.block());
			}

			if (!freeze.fluid().isEmpty()) {
				builder.addSlot(RecipeIngredientRole.OUTPUT, 35, 16)
						.addIngredient(ForgeTypes.FLUID_STACK, freeze.fluid())
						.setFluidRenderer(getFluidTankDisplaySize(freeze.fluid()), false, 16, 48);
			}

			if (!freeze.item().isEmpty()) {
				builder.addSlot(RecipeIngredientRole.OUTPUT, 37, 18).addIngredient(PluginJEI.PROBABILITY_ITEM_STACK,
						freeze.item());
			}
		}
	}
}
