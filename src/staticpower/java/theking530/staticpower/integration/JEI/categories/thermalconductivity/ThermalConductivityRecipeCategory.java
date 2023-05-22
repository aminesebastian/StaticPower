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
import net.minecraftforge.registries.ForgeRegistries;
import theking530.staticcore.crafting.thermal.ThermalConductivityRecipe;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.text.GuiTextUtilities;
import theking530.staticcore.utilities.SDColor;
import theking530.staticpower.StaticPower;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.integration.JEI.BaseJEIRecipeCategory;
import theking530.staticpower.integration.JEI.PluginJEI;

public class ThermalConductivityRecipeCategory extends BaseJEIRecipeCategory<ThermalConductivityRecipe> {
	public static final RecipeType<ThermalConductivityRecipe> TYPE = new RecipeType<>(
			new ResourceLocation(StaticPower.MOD_ID, "thermal_conductivity"), ThermalConductivityRecipe.class);

	private final MutableComponent locTitle;
	private final IDrawable background;
	private final IDrawable icon;

	public ThermalConductivityRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper);
		locTitle = Component.translatable("gui.staticcore.heat");
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
	public RecipeType<ThermalConductivityRecipe> getRecipeType() {
		return TYPE;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void draw(ThermalConductivityRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack matrixStack,
			double mouseX, double mouseY) {
		// TODO: Clean this up to support the new thermal system.
		GuiDrawUtilities.drawSlot(matrixStack, 20, 20, 5, 5, 0);
		GuiDrawUtilities.drawSlot(matrixStack, 135, 35, 30, 5, 0);

		int yPos = 14;
		int xPos = 160;

		if (recipe.hasActiveTemperature()) {
			String temperature = Component.literal("Temperature: ").append(ChatFormatting.GOLD.toString())
					.append(GuiTextUtilities.formatHeatRateToString(recipe.getTemperature())).getString();
			GuiDrawUtilities.drawString(matrixStack, temperature, xPos, yPos, 0.0f, 1.0f, SDColor.EIGHT_BIT_WHITE,
					true);
			yPos += 11;
		}

		String temperature = Component.literal("Conductivity: ").append(ChatFormatting.GREEN.toString())
				.append(GuiTextUtilities.formatConductivityToString(recipe.getConductivity())).getString();
		GuiDrawUtilities.drawString(matrixStack, temperature, xPos, yPos, 0.0f, 1.0f, SDColor.EIGHT_BIT_WHITE, true);
		yPos += 11;

		if (recipe.hasOverheatingBehaviour()) {
			String overheatTemp = Component.literal("<- Overheat: ").append(ChatFormatting.RED.toString())
					.append(GuiTextUtilities.formatHeatToString(recipe.getOverheatingBehaviour().getTemperature()))
					.getString();
			GuiDrawUtilities.drawSlot(matrixStack, 20, 20, 35, 16, 0);
			GuiDrawUtilities.drawString(matrixStack, overheatTemp, xPos, yPos, 0.0f, 1.0f, SDColor.EIGHT_BIT_WHITE,
					true);
			yPos += 11;
		}

		if (recipe.hasFreezeBehaviour()) {
			String overheatTemp = Component.literal("<- Freeze: ").append(ChatFormatting.RED.toString())
					.append(GuiTextUtilities.formatHeatToString(recipe.getFreezingBehaviour().getTemperature()))
					.getString();
			GuiDrawUtilities.drawSlot(matrixStack, 20, 20, 35, 16, 0);
			GuiDrawUtilities.drawString(matrixStack, overheatTemp, xPos, yPos, 0.0f, 1.0f, SDColor.EIGHT_BIT_WHITE,
					true);
		}

		// If the input or output is fire, manually render it.
		boolean isFireInput = recipe.getBlocks().test(Blocks.FIRE);
		boolean hasFireOutput = recipe.hasOverheatingBehaviour()
				&& recipe.getOverheatingBehaviour().getBlockState().getBlock() == Blocks.FIRE;
		if (isFireInput || hasFireOutput) {
			Minecraft mc = Minecraft.getInstance();
			BlockRenderDispatcher blockRenderer = mc.getBlockRenderer();
			PoseStack blockStack = new PoseStack();

			blockStack.pushPose();
			if (isFireInput) {
				blockStack.translate(1.8f, -7.4f, -9.25f);
			} else if (hasFireOutput) {
				blockStack.translate(-0.5f, 3.75f, 0);
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
	public List<Component> getTooltipStrings(ThermalConductivityRecipe recipe, IRecipeSlotsView recipeSlotsView,
			double mouseX, double mouseY) {
		List<Component> output = new ArrayList<Component>();
		return output;
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, ThermalConductivityRecipe recipe, IFocusGroup ingredients) {
		if (!recipe.getBlocks().isEmpty()) {
			addBlockStateIngredientSlot(builder, 7, 7, recipe.getBlocks());
		}
		if (!recipe.getFluids().isEmpty()) {
			addFluidIngredientSlot(builder, 5, 5, 20, 20, recipe.getFluids());
		}

		// Set the overheated block output.
		if (recipe.hasOverheatingBehaviour()) {
			ConcretizedThermalConductivityBehaviour overheat = ConcretizedThermalConductivityBehaviour
					.from(recipe.getOverheatingBehaviour());
			if (!overheat.block().isEmpty()) {
				builder.addSlot(RecipeIngredientRole.OUTPUT, 37, 20).addIngredient(VanillaTypes.ITEM_STACK,
						overheat.block());
			}

			if (!overheat.fluid().isEmpty()) {
				builder.addSlot(RecipeIngredientRole.OUTPUT, 35, 16)
						.addIngredient(ForgeTypes.FLUID_STACK, overheat.fluid()).setFluidRenderer(1000, false, 20, 20)
						.addTooltipCallback((recipeSlotView, tooltips) -> {
							if (ForgeRegistries.FLUIDS.getKey(overheat.fluid().getFluid()).toString()
									.contains("flowing")) {
								tooltips.add(Component.literal("(Flowing)"));
							}
						});
			}

			if (!overheat.item().isEmpty()) {
				builder.addSlot(RecipeIngredientRole.OUTPUT, 37, 17).addIngredient(PluginJEI.PROBABILITY_ITEM_STACK,
						overheat.item());
			}
		}

		// Set the frozen block output.
		if (recipe.hasFreezeBehaviour()) {
			ConcretizedThermalConductivityBehaviour freeze = ConcretizedThermalConductivityBehaviour
					.from(recipe.getFreezingBehaviour());
			if (!freeze.block().isEmpty()) {
				builder.addSlot(RecipeIngredientRole.OUTPUT, 37, 20).addIngredient(VanillaTypes.ITEM_STACK,
						freeze.block());
			}

			if (!freeze.fluid().isEmpty()) {
				builder.addSlot(RecipeIngredientRole.OUTPUT, 35, 16)
						.addIngredient(ForgeTypes.FLUID_STACK, freeze.fluid()).setFluidRenderer(1000, false, 20, 20)
						.addTooltipCallback((recipeSlotView, tooltips) -> {
							if (ForgeRegistries.FLUIDS.getKey(freeze.fluid().getFluid()).toString()
									.contains("flowing")) {
								tooltips.add(Component.literal("(Flowing)"));
							}
						});
			}

			if (!freeze.item().isEmpty()) {
				builder.addSlot(RecipeIngredientRole.OUTPUT, 37, 17).addIngredient(PluginJEI.PROBABILITY_ITEM_STACK,
						freeze.item());
			}
		}
	}
}
