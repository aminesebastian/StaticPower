package theking530.staticpower.data.research;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.registries.ForgeRegistryEntry;
import theking530.staticcore.utilities.Color;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.crafting.StaticPowerIngredient;

public class ResearchSerializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<Research> {
	public static final ResearchSerializer INSTANCE = new ResearchSerializer();

	private ResearchSerializer() {
		this.setRegistryName(new ResourceLocation(StaticPower.MOD_ID, "research"));
	}

	@Override
	public Research fromJson(ResourceLocation recipeId, JsonObject json) {
		// Get the title and description.
		String title = json.get("title").getAsString();
		String description = json.get("description").getAsString();

		// Capture all the prerequisites, and make sure there is an array provided.
		List<ResourceLocation> prerequisites = new ArrayList<ResourceLocation>();
		if (json.has("prerequisites")) {
			if (!json.get("prerequisites").isJsonArray()) {
				StaticPower.LOGGER.error(String.format("Research: %1$s's prerequisites must be an array!", recipeId.toString()));
				return null;
			}

			JsonArray prereqs = json.get("prerequisites").getAsJsonArray();
			for (JsonElement element : prereqs) {
				prerequisites.add(new ResourceLocation(element.getAsString()));
			}
		}

		// Capture all the requirements, and make sure there is an array provided.
		List<StaticPowerIngredient> requirements = new ArrayList<StaticPowerIngredient>();
		if (json.has("requirements")) {
			if (!json.get("requirements").isJsonArray()) {
				StaticPower.LOGGER.error(String.format("Research: %1$s's requirements must be an array!", recipeId.toString()));
				return null;
			}

			JsonArray reqs = json.get("requirements").getAsJsonArray();
			for (JsonElement element : reqs) {
				requirements.add(StaticPowerIngredient.deserialize(element));
			}
		} else {
			StaticPower.LOGGER.error(String.format("Research: %1$s is missing requirements!", recipeId.toString()));
			return null;
		}

		// Capture all the unlocks.
		List<ResearchUnlock> unlocks = new ArrayList<ResearchUnlock>();
		if (json.has("unlocks")) {
			if (!json.get("unlocks").isJsonArray()) {
				StaticPower.LOGGER.error(String.format("Research: %1$s's unlocks must be an array!", recipeId.toString()));
				return null;
			}

			JsonArray ulcks = json.get("unlocks").getAsJsonArray();
			for (JsonElement element : ulcks) {
				unlocks.add(ResearchUnlock.fromJson(element));
			}
		}

		// Capture all the rewards, and make sure there is an array provided.
		List<ItemStack> rewards = new ArrayList<ItemStack>();
		if (json.has("rewards")) {
			if (!json.get("rewards").isJsonArray()) {
				StaticPower.LOGGER.error(String.format("Research: %1$s's rewards must be an array!", recipeId.toString()));
				return null;
			}

			JsonArray rews = json.get("rewards").getAsJsonArray();
			for (JsonElement element : rews) {
				rewards.add(ShapedRecipe.itemStackFromJson(element.getAsJsonObject()));
			}
		}

		// Capture all the advancements, and make sure there is an array provided.
		List<ResourceLocation> advancements = new ArrayList<ResourceLocation>();
		if (json.has("advancements")) {
			if (!json.get("advancements").isJsonArray()) {
				StaticPower.LOGGER.error(String.format("Advancements: %1$s's rewards must be an array!", recipeId.toString()));
				return null;
			}

			JsonArray rews = json.get("advancements").getAsJsonArray();
			for (JsonElement element : rews) {
				advancements.add(new ResourceLocation(element.getAsString()));
			}
		}

		// Capture the appropriate icon.
		ResearchIcon icon = null;
		if (json.has("icon")) {
			icon = ResearchIcon.fromJson(json.get("icon"));
		} else {
			StaticPower.LOGGER.error(String.format("Research: %1$s is missing an icon!", recipeId.toString()));
			return null;
		}

		// Capture the hidden driver and color.
		boolean hidden = json.has("hiddenUntilAvailable") ? json.get("hiddenUntilAvailable").getAsBoolean() : false;
		Color color = null;
		if (json.has("color")) {
			color = Color.fromJson(json.get("color").getAsJsonObject());
		}

		int levelOffset = 0;
		if (json.has("levelOffset")) {
			levelOffset = json.get("levelOffset").getAsInt();
		}

		// Create the recipe.
		return new Research(recipeId, title, description, levelOffset, prerequisites, requirements, rewards, unlocks, advancements, icon, hidden, color);
	}

	@Override
	public Research fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		String title = buffer.readUtf();
		String description = buffer.readUtf();
		int levelOffset = buffer.readInt();
		ResearchIcon icon = ResearchIcon.fromBuffer(buffer);

		// Prerequisites.
		List<ResourceLocation> prerequisites = new ArrayList<ResourceLocation>();
		byte preReqCount = buffer.readByte();
		for (int i = 0; i < preReqCount; i++) {
			prerequisites.add(new ResourceLocation(buffer.readUtf()));
		}

		// Requirements.
		List<StaticPowerIngredient> requirements = new ArrayList<StaticPowerIngredient>();
		byte reqCount = buffer.readByte();
		for (int i = 0; i < reqCount; i++) {
			requirements.add(StaticPowerIngredient.read(buffer));
		}

		// Unlocks.
		List<ResearchUnlock> unlocks = new ArrayList<ResearchUnlock>();
		byte unlockCount = buffer.readByte();
		for (int i = 0; i < unlockCount; i++) {
			unlocks.add(ResearchUnlock.fromBuffer(buffer));
		}

		// Rewards.
		List<ItemStack> rewards = new ArrayList<ItemStack>();
		byte rewardCount = buffer.readByte();
		for (int i = 0; i < rewardCount; i++) {
			rewards.add(buffer.readItem());
		}

		// Advacements.
		List<ResourceLocation> advacements = new ArrayList<ResourceLocation>();
		byte advacementsCount = buffer.readByte();
		for (int i = 0; i < advacementsCount; i++) {
			advacements.add(new ResourceLocation(buffer.readUtf()));
		}

		boolean hidden = buffer.readBoolean();
		Color color = Color.fromBuffer(buffer);

		// Create the recipe.
		return new Research(recipeId, title, description, levelOffset, prerequisites, requirements, rewards, unlocks, advacements, icon, hidden, color);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, Research recipe) {
		buffer.writeUtf(recipe.getTitle());
		buffer.writeUtf(recipe.getDescription());
		buffer.writeInt(recipe.getLevelOffset());
		
		recipe.getIcon().toBuffer(buffer);

		// Prerequisites.
		buffer.writeByte(recipe.getPrerequisites().size());
		for (ResourceLocation prereq : recipe.getPrerequisites()) {
			buffer.writeUtf(prereq.toString());
		}

		// Requirements.
		buffer.writeByte(recipe.getRequirements().size());
		for (StaticPowerIngredient req : recipe.getRequirements()) {
			req.write(buffer);
		}

		// Unlocks.
		buffer.writeByte(recipe.getUnlocks().size());
		for (ResearchUnlock unlock : recipe.getUnlocks()) {
			unlock.toBuffer(buffer);
		}

		// Rewards.
		buffer.writeByte(recipe.getRewards().size());
		for (ItemStack reward : recipe.getRewards()) {
			buffer.writeItem(reward);
		}

		// Rewards.
		buffer.writeByte(recipe.getAdvancements().size());
		for (ResourceLocation advancement : recipe.getAdvancements()) {
			buffer.writeUtf(advancement.toString());
		}

		buffer.writeBoolean(recipe.isHiddenUntilAvailable());
		recipe.getColor().toBuffer(buffer);
	}
}