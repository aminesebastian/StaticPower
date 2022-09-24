package theking530.staticpower.data.loot;

import java.util.List;

import javax.annotation.Nonnull;

import com.google.gson.JsonObject;

import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import theking530.staticcore.utilities.SDMath;

/**
 * Loot modifier to drop a custom item from vanilla blocks.
 *
 */
public class StaticPowerLootModifier extends LootModifier {
	public final int minCount;
	public final int maxCount;
	public final float chance;
	public final ItemStack output;

	public StaticPowerLootModifier(ItemStack output, int minCount, int maxCount, float chance,
			LootItemCondition[] conditionsIn) {
		super(conditionsIn);
		this.output = output;
		this.minCount = minCount;
		this.maxCount = maxCount;
		this.chance = chance;
	}

	@Nonnull
	@Override
	public List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
		// Check if the chance check passes.
		if (SDMath.diceRoll(chance)) {
			// If it does, select a random value in the range of the min and the max.
			int amount = SDMath.getRandomIntInRange(minCount, maxCount);

			// Create the item to drop (clamp the size JUST in case).
			ItemStack newItem = output.copy();
			newItem.setCount(SDMath.clamp(amount, 0, Integer.MAX_VALUE));

			// Add the new item to the loot output.
			generatedLoot.add(newItem);
		}

		// Return the passed in value.
		return generatedLoot;
	}

	public static class Serializer extends GlobalLootModifierSerializer<StaticPowerLootModifier> {
		@Override
		public StaticPowerLootModifier read(ResourceLocation name, JsonObject json, LootItemCondition[] conditionsIn) {
			int minCount = json.getAsJsonPrimitive("minCount").getAsInt();
			int maxCount = json.getAsJsonPrimitive("maxCount").getAsInt();
			float chance = json.getAsJsonPrimitive("chance").getAsFloat();
			ItemStack output = ShapedRecipe.itemStackFromJson(json);
			return new StaticPowerLootModifier(output, minCount, maxCount, chance, conditionsIn);
		}

		@Override
		public JsonObject write(StaticPowerLootModifier instance) {
			JsonObject json = new JsonObject();
			json.add("conditions", SerializationContext.INSTANCE.serializeConditions(instance.conditions));
			json.addProperty("minCount", instance.minCount);
			json.addProperty("maxCount", instance.maxCount);
			json.addProperty("chance", instance.chance);
			json.addProperty("item", instance.output.serializeNBT().toString());
			return json;
		}
	}
}
