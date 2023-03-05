package theking530.staticpower.data.research;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.Codec;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import theking530.staticcore.utilities.SDColor;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeSerializer;

public class ResearchSerializer extends StaticPowerRecipeSerializer<Research> {
	@Override
	public Codec<Research> getCodec() {
		return Research.CODEC;
	}

	@Override
	public Research fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		String title = buffer.readUtf();
		String description = buffer.readUtf();
		Vector2D visualOffset = Vector2D.fromBuffer(buffer);
		int sortOrder = buffer.readInt();
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
			requirements.add(StaticPowerIngredient.readFromBuffer(buffer));
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

		// Advancements.
		List<ResourceLocation> advacements = new ArrayList<ResourceLocation>();
		byte advacementsCount = buffer.readByte();
		for (int i = 0; i < advacementsCount; i++) {
			advacements.add(new ResourceLocation(buffer.readUtf()));
		}

		boolean hidden = buffer.readBoolean();
		SDColor color = SDColor.fromBuffer(buffer);

		// Create the recipe.
		return new Research(recipeId, title, description, visualOffset, sortOrder, unlocks, icon, prerequisites, requirements, rewards, advacements, hidden, color);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, Research recipe) {
		buffer.writeUtf(recipe.getTitle());
		buffer.writeUtf(recipe.getDescription());
		recipe.getVisualOffset().toBuffer(buffer);
		buffer.writeInt(recipe.getSortOrder());
		recipe.getIcon().toBuffer(buffer);

		// Prerequisites.
		buffer.writeByte(recipe.getPrerequisites().size());
		for (ResourceLocation prereq : recipe.getPrerequisites()) {
			buffer.writeUtf(prereq.toString());
		}

		// Requirements.
		buffer.writeByte(recipe.getRequirements().size());
		for (StaticPowerIngredient req : recipe.getRequirements()) {
			req.writeToBuffer(buffer);
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
