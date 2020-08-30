package theking530.staticpower.container.slots;

import javax.annotation.Nonnull;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import theking530.staticpower.items.utilities.EnergyHandlerItemStackUtilities;

public class PlayerArmorItemSlot extends Slot {

	public PlayerArmorItemSlot(IInventory inventory, int index, int xPosition, int yPosition, EquipmentSlotType slot) {
		super(inventory, index, xPosition, yPosition);
	}

	@Override
	public boolean isItemValid(@Nonnull ItemStack stack) {
		return EnergyHandlerItemStackUtilities.isEnergyContainer(stack);
	}

	protected static ItemStack getItemStackForSlot(EquipmentSlotType slot) {
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
