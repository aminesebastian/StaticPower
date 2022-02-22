package theking530.staticpower.data.research;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class ResearchUnlock {
	public enum ResearchUnlockType {
		CRAFTING
	}

	private final ResearchIcon icon;
	private final ResourceLocation target;
	private final String description;
	private final ResearchUnlockType type;

	public ResearchUnlock(ResearchUnlockType type, ResourceLocation target, ResearchIcon icon, String description) {
		this.type = type;
		this.target = target;
		this.icon = icon;
		this.description = description;
	}

	public ResearchIcon getIcon() {
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
