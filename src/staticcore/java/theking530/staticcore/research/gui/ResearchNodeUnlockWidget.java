package theking530.staticcore.research.gui;

import java.util.List;

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
import theking530.staticcore.gui.widgets.AbstractGuiWidget;
import theking530.staticcore.research.ResearchIcon;
import theking530.staticcore.research.ResearchUnlock;
import theking530.staticcore.research.ResearchUnlock.ResearchUnlockType;
import theking530.staticcore.utilities.math.Vector2D;
import theking530.staticpower.integration.JEI.PluginJEI;

public class ResearchNodeUnlockWidget extends AbstractGuiWidget<ResearchNodeUnlockWidget> {
	private ResearchUnlock unlock;

	public ResearchNodeUnlockWidget(ResearchUnlock unlock, int x, int y, int width, int height) {
		super(x, y, width, height);
		this.unlock = unlock;
	}

	public void getWidgetTooltips(Vector2D mousePosition, List<Component> tooltips, boolean showAdvanced) {
		super.getWidgetTooltips(mousePosition, tooltips, showAdvanced);
		tooltips.addAll(unlock.getTooltip(getLocalPlayer(),
				Screen.hasControlDown() ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL));
	}

	@Override
	public void renderWidgetBehindItems(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
		if (unlock.getIcon() != null) {
			ResearchIcon.draw(unlock.getIcon(), pose, 0, 0, 0, getWidth(), getHeight());
		}
	}

	@SuppressWarnings("resource")
	@Override
	public EInputResult keyPressed(int key, int scanCode, int modifiers) {
		if (unlock == null || unlock.getType() != ResearchUnlockType.CRAFTING) {
			return super.keyPressed(key, scanCode, modifiers);
		}

		ItemStack buttonStack = unlock.getAsRecipe().getResultItem();

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
}
