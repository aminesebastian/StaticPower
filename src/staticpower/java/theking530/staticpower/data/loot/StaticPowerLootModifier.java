package theking530.staticpower.data.loot;

import java.util.function.Supplier;

import javax.annotation.Nonnull;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import theking530.staticcore.utilities.SDMath;

/**
 * Loot modifier to drop a custom item from vanilla blocks.
 *
 */
public class StaticPowerLootModifier extends LootModifier {
	public static final Supplier<Codec<StaticPowerLootModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.create(inst -> codecStart(inst)
			.and(inst.group(Codec.INT.fieldOf("minCount").forGetter(m -> m.minCount), Codec.INT.fieldOf("maxCount").forGetter(m -> m.maxCount),
					Codec.FLOAT.fieldOf("chance").forGetter(m -> m.chance), ItemStack.CODEC.fieldOf("output").forGetter(m -> m.output)))
			.apply(inst, StaticPowerLootModifier::new)));

	public final int minCount;
	public final int maxCount;
	public final float chance;
	public final ItemStack output;

	public StaticPowerLootModifier(LootItemCondition[] conditionsIn, int minCount, int maxCount, float chance, ItemStack output) {
		super(conditionsIn);
		this.output = output;
		this.minCount = minCount;
		this.maxCount = maxCount;
		this.chance = chance;

	}

	@Nonnull
	@Override
	public ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
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

	@Override
	public Codec<? extends IGlobalLootModifier> codec() {
		return CODEC.get();
	}
}
