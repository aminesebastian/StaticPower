package theking530.staticpower.data.crafting;

import com.google.gson.JsonObject;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.ForgeRegistries;
import theking530.staticpower.StaticPower;

public class EnchantmentRecipeWrapper {
	private final ResourceLocation id;
	private final int level;
	private final Enchantment enchantment;

	public EnchantmentRecipeWrapper(ResourceLocation id, int level) {
		super();
		this.id = id;
		this.level = level;

		// Get the enchantment.
		if (ForgeRegistries.ENCHANTMENTS.containsKey(id)) {
			enchantment = ForgeRegistries.ENCHANTMENTS.getValue(id);
		} else {
			enchantment = null;
			StaticPower.LOGGER.error(String.format("Enchantment with id %1$s does not exist!", id.toString()));
		}
	}

	public ResourceLocation getId() {
		return id;
	}

	public int getLevel() {
		return level;
	}

	public Enchantment getEnchantment() {
		return enchantment;
	}

	public void writeToBuffer(FriendlyByteBuf buffer) {
		// Write the Id.
		CompoundTag id = new CompoundTag();
		id.putString("id", id.toString());
		id.putInt("level", level);
		buffer.writeNbt(id);
	}

	public static EnchantmentRecipeWrapper fromJson(JsonObject json) {
		return new EnchantmentRecipeWrapper(new ResourceLocation(json.get("id").getAsString()), json.get("level").getAsInt());
	}

	public static EnchantmentRecipeWrapper fromBuffer(FriendlyByteBuf buffer) {
		CompoundTag idTag = buffer.readNbt();
		ResourceLocation id = new ResourceLocation(idTag.getString("id"));
		int level = idTag.getInt("level");
		return new EnchantmentRecipeWrapper(id, level);
	}
}
