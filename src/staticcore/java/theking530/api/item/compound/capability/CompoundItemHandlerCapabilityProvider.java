package theking530.api.item.compound.capability;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import theking530.api.item.compound.part.ICompoundItemPart;
import theking530.api.item.compound.slot.CompoundItemSlot;
import theking530.staticcore.item.ISPItemCapabilityProvider;
import theking530.staticcore.utilities.NBTUtilities;

public class CompoundItemHandlerCapabilityProvider implements ICompoundItem, ISPItemCapabilityProvider {
	protected static final String NAME = "compound_item_capability";
	private final List<CompoundItemSlotState> slots;
	private final ItemStack owningStack;

	protected CompoundItemHandlerCapabilityProvider(ItemStack owningStack) {
		slots = new ArrayList<>();
		this.owningStack = owningStack;
	}

	@Override
	public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		if (cap == CapabilityCompoundItem.CAPABILITY_COMPOUND_ITEM) {
			return LazyOptional.of(() -> this).cast();
		}
		return LazyOptional.empty();
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag output = new CompoundTag();
		ListTag slotsTag = NBTUtilities.serialize(slots, (slot) -> {
			DataResult<Tag> encodedResult = CompoundItemSlotState.CODEC.encodeStart(NbtOps.INSTANCE, slot);
			return encodedResult.get().left().get();
		});
		output.put("slots", slotsTag);
		return output;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		slots.clear();
		slots.addAll(NBTUtilities.deserialize(nbt.getList("slots", Tag.TAG_COMPOUND), true, (tag) -> {
			DataResult<Pair<CompoundItemSlotState, Tag>> slotStateDecodeResult = CompoundItemSlotState.CODEC.decode(NbtOps.INSTANCE, tag);
			if (slotStateDecodeResult.get().left().isEmpty()) {
				return null;
			}

			Pair<CompoundItemSlotState, Tag> slotState = slotStateDecodeResult.get().left().get();
			return slotState.getFirst();
		}));
	}

	@Override
	public List<CompoundItemSlotState> getSlots() {
		return slots;
	}

	@Override
	public boolean canApplyPartToSlot(int slotIndex, ItemStack part) {
		if (isSlotPopulated(slotIndex)) {
			return false;
		}

		if (!(part.getItem() instanceof ICompoundItemPart)) {
			return false;
		}

		ICompoundItemPart partInterface = (ICompoundItemPart) part.getItem();
		CompoundItemSlot slotType = this.getSlot(slotIndex).getSlot();
		if (!partInterface.canApplyToItem(owningStack, part, slotType)) {
			return false;
		}

		return true;
	}

	@Override
	public boolean isSlotPopulated(int slotIndex) {
		return slots.get(slotIndex).isOccupied();
	}

	@Override
	public boolean tryApplyPartToSlot(int slotIndex, ItemStack part) {
		if (isSlotPopulated(slotIndex)) {
			return false;
		}

		if (!canApplyPartToSlot(slotIndex, part)) {
			return false;
		}

		slots.get(slotIndex).setPart(part);
		return true;
	}

	@Override
	public boolean canRemovePartFromSlot(int slotIndex) {
		if (!isSlotPopulated(slotIndex)) {
			return false;
		}
		return true;
	}

	@Override
	public ItemStack tryRemovePartFromSlot(int slotIndex) {
		if (!canRemovePartFromSlot(slotIndex)) {
			return ItemStack.EMPTY;
		}

		ItemStack partInSlot = slots.get(slotIndex).getPart();
		slots.get(slotIndex).setPart(ItemStack.EMPTY);
		return partInSlot;
	}

	@Override
	public boolean isComplete() {
		for (CompoundItemSlotState state : slots) {
			if (!state.isOptional() && !state.isOccupied()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int getDamage() {
		int durability = 0;

		if (!isComplete()) {
			return durability;
		}

		for (CompoundItemSlotState slot : slots) {
			if (!slot.isOccupied()) {
				continue;
			}
			ItemStack partInSlot = slot.getPart();
			durability = partInSlot.getDamageValue();
		}

		return durability;
	}

	@Override
	public int getMaxDamage() {
		int durability = 0;

		if (!isComplete()) {
			return durability;
		}

		for (CompoundItemSlotState slot : slots) {
			if (!slot.isOccupied()) {
				continue;
			}
			ItemStack partInSlot = slot.getPart();
			durability = partInSlot.getMaxDamage();
		}

		return durability;
	}

	@Override
	public ItemStack getBaseItem() {
		return owningStack;
	}

	@Override
	public ItemStack getPartInSlot(int slotIndex) {
		return slots.get(slotIndex).getPart();
	}

	@Override
	public CompoundItemSlotState getSlot(int slotIndex) {
		return slots.get(slotIndex);
	}

	public static class CompoundItemHandlerCapabilityBuilder {
		private final List<CompoundItemSlotState> slots;
		private BiFunction<Integer, ItemStack, Boolean> filter;

		public CompoundItemHandlerCapabilityBuilder() {
			slots = new ArrayList<>();
			filter = (slot, stack) -> true;
		}

		public CompoundItemHandlerCapabilityBuilder addSlot(CompoundItemSlot slot, boolean isOptional) {
			slots.add(new CompoundItemSlotState(slots.size(), slot, isOptional, ItemStack.EMPTY));
			return this;
		}

		public CompoundItemHandlerCapabilityBuilder setFilter(BiFunction<Integer, ItemStack, Boolean> filter) {
			this.filter = filter;
			return this;
		}

		public CompoundItemHandlerCapabilityProvider build(ItemStack owningStack) {
			CompoundItemHandlerCapabilityProvider output = new CompoundItemHandlerCapabilityProvider(owningStack) {
				@Override
				public boolean canApplyPartToSlot(int slotIndex, ItemStack part) {
					if (!filter.apply(slotIndex, part)) {
						return false;
					}
					return super.canApplyPartToSlot(slotIndex, part);
				}
			};
			for (CompoundItemSlotState slot : slots) {
				output.slots.add(slot);
			}
			return output;
		}
	}
}
