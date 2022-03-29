package theking530.staticpower.data.research;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sun.jna.platform.win32.Guid.GUID;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import theking530.staticpower.client.gui.GuiTextures;
import theking530.staticpower.data.crafting.StaticPowerRecipeRegistry;

public class ResearchUnlock {
	public enum ResearchUnlockType {
		CRAFTING, MACHINE_RECIPE
	}

	private final String displayKey;
	private final ResourceLocation target;
	private final String description;
	private final ResearchUnlockType type;
	private ResearchIcon icon;
	private boolean hidden;

	public ResearchUnlock(String displayKey, ResearchUnlockType type, ResourceLocation target, ResearchIcon icon, String description, boolean hidden) {
		this.displayKey = displayKey;
		this.type = type;
		this.target = target;
		if (icon == null) {
			this.icon = new ResearchIcon(null, GuiTextures.BLANK);
		} else {
			this.icon = icon;
		}

		this.description = description;
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

	public String getDescription() {
		return description;
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

		buffer.writeBoolean(description != null);
		if (description != null) {
			buffer.writeUtf(description);
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
		
		String description = null;
		if (buffer.readBoolean()) {
			description = buffer.readUtf();
		}

		if (buffer.readBoolean()) {
			icon = ResearchIcon.fromBuffer(buffer);
		}

		boolean hidden = buffer.readBoolean();
		return new ResearchUnlock(displayKey, type, target, icon, description, hidden);
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
		ResourceLocation target = new ResourceLocation(input.get("target").getAsString());

		// Get the icon.
		ResearchIcon icon = null;
		if (input.has("icon")) {
			icon = ResearchIcon.fromJson(input.get("icon"));
		}

		// Get the description.
		String description = null;
		if (input.has("description")) {
			description = input.get("description").getAsString();
		}

		// Get if hidden.
		boolean hidden = false;
		if (input.has("hidden")) {
			hidden = input.get("hidden").getAsBoolean();
		}
		return new ResearchUnlock(displayKey, type, target, icon, description, hidden);
	}
}
