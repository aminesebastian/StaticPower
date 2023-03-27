package theking530.staticcore.container.slots;

import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class PlayerArmorItemSlot extends Slot {

	public PlayerArmorItemSlot(Container inventory, int index, int xPosition, int yPosition, EquipmentSlot slot) {
		super(inventory, index, xPosition, yPosition);
	}

	protected static ItemStack getItemStackForSlot(EquipmentSlot slot) {
		switch (slot) {
		case HEAD:
			return new ItemStack(Items.IRON_HELMET);
		case CHEST:
			return new ItemStack(Items.IRON_CHESTPLATE);
		case LEGS:
			return new ItemStack(Items.IRON_LEGGINGS);
		case FEET:
			return new ItemStack(Items.IRON_BOOTS);
		default:
			return new ItemStack(Items.IRON_SWORD);
		}
	}
}
