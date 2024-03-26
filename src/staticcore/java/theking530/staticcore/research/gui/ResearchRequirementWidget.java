package theking530.staticcore.research.gui;

import java.util.List;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import theking530.staticcore.crafting.StaticPowerIngredient;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.text.GuiTextUtilities;
import theking530.staticcore.gui.widgets.AbstractGuiWidget;
import theking530.staticcore.research.gui.ResearchManager.ResearchInstance;
import theking530.staticcore.utilities.SDColor;
import theking530.staticcore.utilities.math.Vector2D;
import theking530.staticpower.integration.JEI.PluginJEI;

public class ResearchRequirementWidget extends AbstractGuiWidget<ResearchRequirementWidget> {
	private StaticPowerIngredient requirement;
	private ResearchInstance instance;
	private int requirementRotationIndex;
	private int rotationTimer;

	public ResearchRequirementWidget(StaticPowerIngredient requirement, @Nullable ResearchInstance instance, int x,
			int y, int width, int height) {
		super(x, y, width, height);
		this.requirement = requirement;
		this.instance = instance;
		this.requirementRotationIndex = 0;
	}

	@Override
	public void tick() {
		super.tick();
		rotationTimer++;
		if (rotationTimer > 20) {
			rotationTimer = 0;
			requirementRotationIndex++;

			if (requirementRotationIndex >= requirement.getIngredient().getItems().length) {
				requirementRotationIndex = 0;
			}
		}
	}

	@Override
	public void getWidgetTooltips(Vector2D mousePosition, List<Component> tooltips, boolean showAdvanced) {
		super.getWidgetTooltips(mousePosition, tooltips, showAdvanced);
		ItemStack renderedItem = getItemToRender();
		tooltips.addAll(renderedItem.getTooltipLines(getLocalPlayer(),
				Screen.hasControlDown() ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL));
	}

	@Override
	public void renderWidgetBehindItems(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
		GuiDrawUtilities.drawItem(pose, getItemToRender(), 0, 0, 1, 8f, 8f);

		if (instance != null) {
			GuiDrawUtilities.drawStringCentered(pose,
					GuiTextUtilities
							.formatNumberAsString(
									requirement.getCount() - instance.getRequirementFullfillment(requirement))
							.getString(),
					8f, 6.5f, 10, 0.5f, SDColor.EIGHT_BIT_WHITE, true);
		} else {
			GuiDrawUtilities.drawStringCentered(pose,
					GuiTextUtilities.formatNumberAsString(requirement.getCount()).getString(), 8f, 6.5f, 10, 0.5f,
					SDColor.EIGHT_BIT_WHITE, true);
		}
	}

	@SuppressWarnings("resource")
	@Override
	public EInputResult keyPressed(int key, int scanCode, int modifiers) {
		if (requirement == null) {
			return super.keyPressed(key, scanCode, modifiers);
		}

		ItemStack buttonStack = getItemToRender();

		// Do a little extra work here to support JEI lookups.
		for (KeyMapping binding : Minecraft.getInstance().options.keyMappings) {
			if (binding.getKey().getValue() == key) {
				if (binding.getName() == "key.jei.showRecipe") {
					if (!buttonStack.isEmpty()) {
						IFocus<ItemStack> focus = PluginJEI.RUNTIME.getJeiHelpers().getFocusFactory()
								.createFocus(RecipeIngredientRole.OUTPUT, VanillaTypes.ITEM_STACK, buttonStack);
						PluginJEI.RUNTIME.getRecipesGui().show(focus);
						return EInputResult.HANDLED;
					}
				} else if (binding.getName() == "key.jei.showUses") {
					if (!buttonStack.isEmpty()) {
						IFocus<ItemStack> focus = PluginJEI.RUNTIME.getJeiHelpers().getFocusFactory()
								.createFocus(RecipeIngredientRole.INPUT, VanillaTypes.ITEM_STACK, buttonStack);
						PluginJEI.RUNTIME.getRecipesGui().show(focus);
						return EInputResult.HANDLED;
					}
				}
			}
		}

		// Return false (same as the super).
		return super.keyPressed(key, scanCode, modifiers);
	}

	private ItemStack getItemToRender() {
		if (requirement == null) {
			return ItemStack.EMPTY;
		}
		return requirement.getIngredient().getItems()[requirementRotationIndex];
	}
}
