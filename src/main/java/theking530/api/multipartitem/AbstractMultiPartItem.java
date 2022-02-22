package theking530.api.multipartitem;

import java.util.List;

import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import theking530.staticpower.items.StaticPowerItem;

public abstract class AbstractMultiPartItem extends StaticPowerItem {
	public AbstractMultiPartItem(String name, Item.Properties properties) {
		super(name, properties.stacksTo(1));
	}

	public abstract List<AbstractMultiPartSlot> getSlots(ItemStack stack);

	public abstract ItemStack getPartInSlot(ItemStack stack, AbstractMultiPartSlot slot);

	public boolean canAttachPartInSlot(ItemStack stack, ItemStack part, AbstractMultiPartSlot slot) {
		return !isSlotPopulated(stack, slot) && slot.canAcceptItem(part);
	}

	public boolean isSlotPopulated(ItemStack stack, AbstractMultiPartSlot slot) {
		return !getPartInSlot(stack, slot).isEmpty();
	}

	public boolean isComplete(ItemStack stack) {
		for (AbstractMultiPartSlot slot : getSlots(stack)) {
			if (!isSlotPopulated(stack, slot) && !slot.isOptional()) {
				return false;
			}
		}
		return true;
	}

	public int getTotalPartDurability(ItemStack stack) {
		int durability = 0;
		for (AbstractMultiPartSlot slot : getSlots(stack)) {
			ItemStack slotStack = getPartInSlot(stack, slot);
			durability += slotStack.getDamageValue();
		}
		return durability;
	}

	public int getTotalMaxPartDurability(ItemStack stack) {
		int durability = 0;
		for (AbstractMultiPartSlot slot : getSlots(stack)) {
			ItemStack slotStack = getPartInSlot(stack, slot);
			durability += slotStack.getMaxDamage();
		}
		return durability;
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		return isComplete(stack);
	}

	@Override
	public int getBarWidth(ItemStack stack) {
		int current = getTotalPartDurability(stack);
		int max = getTotalMaxPartDurability(stack);

		// Get the power ratio.
		return 13 - (int) (((double) current / max) * 13);
	}

	public int getBarColor(ItemStack stack) {
		float f = Math.max(0.0F, ((float) getTotalMaxPartDurability(stack) - (float) getTotalPartDurability(stack)) / (float) getTotalMaxPartDurability(stack));
		return Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
	}

}