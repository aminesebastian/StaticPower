package theking530.staticpower.data.research;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sun.jna.platform.win32.Guid.GUID;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import theking530.staticpower.client.gui.GuiTextures;
import theking530.staticpower.data.crafting.StaticPowerRecipeRegistry;

public class ResearchUnlock {
	public enum ResearchUnlockType {
		DISPLAY_ONLY, CRAFTING, MACHINE_RECIPE
	}

	private final String displayKey;
	private final ResourceLocation target;
	private final List<Component> tooltip;
	private final String textTooltip;
	private final ItemStack itemTooltip;
	private final ResearchUnlockType type;
	private ResearchIcon icon;
	private boolean hidden;

	public ResearchUnlock(String displayKey, ResearchUnlockType type, ResourceLocation target, ResearchIcon icon, String textDescription, ItemStack itemDescription, boolean hidden) {
		this.displayKey = displayKey;
		this.type = type;
		this.target = target;
		if (icon == null) {
			this.icon = new ResearchIcon(null, GuiTextures.BLANK);
		} else {
			this.icon = icon;
		}
		this.textTooltip = textDescription;
		this.itemTooltip = itemDescription;
		this.tooltip = new ArrayList<Component>();
		if (textDescription != null) {
			tooltip.add(new TranslatableComponent(textDescription));
		}
		this.hidden = hidden;
	}

	public ResearchIcon getIcon() {
		// For crafting type unlocks, just return an icon of the item.
		if (type == ResearchUnlockType.CRAFTING) {
			CraftingRecipe recipe = StaticPowerRecipeRegistry.getRawRecipe(RecipeType.CRAFTING, target).orElse(null);
			if (recipe != null) {
				icon = new ResearchIcon(recipe.getResultItem(), null);
			}
		}
		return icon;
	}

	public String getDisplayKey() {
		return displayKey;
	}

	public boolean isHidden() {
		return hidden;
	}

	public List<Component> getTooltip(@Nullable Player player, @Nullable TooltipFlag flag) {
		// If this is a crafting recipe, get the tooltip from the output.
		if (type == ResearchUnlockType.CRAFTING) {
			CraftingRecipe recipe = StaticPowerRecipeRegistry.getRawRecipe(RecipeType.CRAFTING, target).orElse(null);
			if (recipe != null) {
				return recipe.getResultItem().getTooltipLines(player, flag);
			}
		}

		// If there is an item descrption provided, use the tooltips from that.
		if (!itemTooltip.isEmpty()) {
			return itemTooltip.getTooltipLines(player, flag);
		}

		// Otherwise, use the text descrption.
		return tooltip;
	}

	public ResourceLocation getTarget() {
		return target;
	}

	public ResearchUnlockType getType() {
		return type;
	}

	public Recipe<?> getAsRecipe() {
		return StaticPowerRecipeRegistry.getRawRecipe(RecipeType.CRAFTING, getTarget()).orElse(null);
	}

	public void toBuffer(FriendlyByteBuf buffer) {
		buffer.writeUtf(displayKey);
		buffer.writeUtf(target.toString());
		buffer.writeByte(type.ordinal());

		buffer.writeBoolean(textTooltip != null);
		if (textTooltip != null) {
			buffer.writeUtf(textTooltip);
		}

		buffer.writeBoolean(itemTooltip != null);
		if (itemTooltip != null) {
			buffer.writeItem(itemTooltip);
		}

		buffer.writeBoolean(icon != null);
		if (icon != null) {
			icon.toBuffer(buffer);
		}

		buffer.writeBoolean(isHidden());
	}

	public static ResearchUnlock fromBuffer(FriendlyByteBuf buffer) {
		String displayKey = buffer.readUtf();
		ResourceLocation target = new ResourceLocation(buffer.readUtf());
		ResearchUnlockType type = ResearchUnlockType.values()[buffer.readByte()];
		ResearchIcon icon = null;

		// Check & get the text tooltip.
		String description = null;
		if (buffer.readBoolean()) {
			description = buffer.readUtf();
		}

		// Check & get the item tooltip.
		ItemStack itemDescription = null;
		if (buffer.readBoolean()) {
			itemDescription = buffer.readItem();
		}

		if (buffer.readBoolean()) {
			icon = ResearchIcon.fromBuffer(buffer);
		}

		boolean hidden = buffer.readBoolean();
		return new ResearchUnlock(displayKey, type, target, icon, description, itemDescription, hidden);
	}

	public static ResearchUnlock fromJson(JsonElement element) {
		JsonObject input = element.getAsJsonObject();

		// Make the display key optional.
		String displayKey = GUID.newGuid().toGuidString();
		if (input.has("displayKey")) {
			displayKey = input.get("displayKey").getAsString();
		}

		// Get the unlock type.
		ResearchUnlockType type = null;
		String rawType = input.get("type").getAsString();
		for (ResearchUnlockType researchType : ResearchUnlockType.values()) {
			if (researchType.toString().toLowerCase().equals(rawType)) {
				type = researchType;
			}
		}

		if (type == null) {
			throw new RuntimeException(String.format("Provided unlock type: %1$s does not exist!", rawType));
		}

		// Get the target.
		ResourceLocation target;
		if (input.has("target")) {
			target = new ResourceLocation(input.get("target").getAsString());
		} else {
			target = new ResourceLocation("none");
		}

		// Get the icon.
		ResearchIcon icon = null;
		if (input.has("icon")) {
			icon = ResearchIcon.fromJson(input.get("icon"));
		}

		// Get the description.
		String description = null;
		ItemStack itemDescription = ItemStack.EMPTY;
		if (input.has("tooltip")) {
			if (input.get("tooltip").isJsonObject()) {
				itemDescription = ShapedRecipe.itemStackFromJson(input.get("tooltip").getAsJsonObject());
			} else {
				description = input.get("tooltip").getAsString();
			}
		}

		// Get if hidden.
		boolean hidden = false;
		if (input.has("hidden")) {
			hidden = input.get("hidden").getAsBoolean();
		}
		return new ResearchUnlock(displayKey, type, target, icon, description, itemDescription, hidden);
	}
}
