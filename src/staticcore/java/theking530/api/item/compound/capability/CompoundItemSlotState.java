package theking530.api.item.compound.capability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.item.ItemStack;
import theking530.api.item.compound.slot.CompoundItemSlot;
import theking530.staticcore.StaticCoreRegistries;
import theking530.staticcore.utilities.JsonUtilities;

public class CompoundItemSlotState {
	public static final CompoundItemSlotState EMPTY = new CompoundItemSlotState(-1, null, false, ItemStack.EMPTY);

	public static final Codec<CompoundItemSlotState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT.fieldOf("id").forGetter(state -> state.getId()), 
			StaticCoreRegistries.ItemSlot().getCodec().fieldOf("slot").forGetter(state -> state.getSlot()),
			Codec.BOOL.fieldOf("optional").forGetter(state -> state.isOptional()), 
			JsonUtilities.ITEMSTACK_CODEC.fieldOf("part").forGetter(state -> state.getPart()))
			.apply(instance, CompoundItemSlotState::new));

	public final int id;
	public final CompoundItemSlot slot;
	public final boolean optional;
	public ItemStack part;

	public CompoundItemSlotState(int id, CompoundItemSlot slot, boolean optional, ItemStack part) {
		this.id = id;
		this.slot = slot;
		this.optional = optional;
		this.part = part;
	}

	public ItemStack getPart() {
		return part;
	}

	public void setPart(ItemStack part) {
		this.part = part;
	}

	public int getId() {
		return id;
	}

	public CompoundItemSlot getSlot() {
		return slot;
	}

	public boolean isOptional() {
		return optional;
	}

	public boolean isOccupied() {
		return !part.isEmpty();
	}
}
