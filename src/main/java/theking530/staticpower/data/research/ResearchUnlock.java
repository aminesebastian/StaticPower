package theking530.staticpower.data.research;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import theking530.staticpower.client.gui.GuiTextures;
import theking530.staticpower.data.crafting.StaticPowerRecipeRegistry;

public class ResearchUnlock {
	public enum ResearchUnlockType {
		CRAFTING
	}

	private final ResourceLocation target;
	private final String description;
	private final ResearchUnlockType type;
	private ResearchIcon icon;

	public ResearchUnlock(ResearchUnlockType type, ResourceLocation target, ResearchIcon icon, String description) {
		this.type = type;
		this.target = target;
		if (icon == null) {
			this.icon = new ResearchIcon(null, GuiTextures.BLANK);
		} else {
			this.icon = icon;
		}

		this.description = description;
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
	}

	public static ResearchUnlock fromBuffer(FriendlyByteBuf buffer) {
		ResourceLocation target = new ResourceLocation(buffer.readUtf());
		ResearchUnlockType type = ResearchUnlockType.values()[buffer.readByte()];
		ResearchIcon icon = null;
		if (buffer.readBoolean()) {
			icon = ResearchIcon.fromBuffer(buffer);
		}

		String description = null;
		if (buffer.readBoolean()) {
			description = buffer.readUtf();
		}

		return new ResearchUnlock(type, target, icon, description);
	}

	public static ResearchUnlock fromJson(JsonElement element) {
		JsonObject input = element.getAsJsonObject();

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

		return new ResearchUnlock(type, target, icon, description);
	}
}
