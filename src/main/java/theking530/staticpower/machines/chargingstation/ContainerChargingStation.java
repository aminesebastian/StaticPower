package theking530.staticpower.machines.chargingstation;

import cofh.redstoneflux.api.IEnergyContainerItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import theking530.staticpower.container.BaseContainer;
import theking530.staticpower.items.upgrades.BaseUpgrade;
import theking530.staticpower.machines.tileentitycomponents.slots.BatterySlot;
import theking530.staticpower.machines.tileentitycomponents.slots.OutputSlot;
import theking530.staticpower.machines.tileentitycomponents.slots.StaticPowerContainerSlot;
import theking530.staticpower.machines.tileentitycomponents.slots.UpgradeSlot;

public class ContainerChargingStation extends BaseContainer {

	public ContainerChargingStation(PlayerInventory invPlayer, TileEntityChargingStation teChargingStation) {	
		//Input
		this.addSlot(new StaticPowerContainerSlot(teChargingStation.slotsInput, 0, 51, 24) {
			@Override
	        public boolean isItemValid(ItemStack itemStack) {
		          return itemStack.getItem() instanceof IEnergyContainerItem;
		    }
		});	
		this.addSlot(new StaticPowerContainerSlot(teChargingStation.slotsInput, 1, 70, 24) {
			@Override
	        public boolean isItemValid(ItemStack itemStack) {
		          return itemStack.getItem() instanceof IEnergyContainerItem;
		    }
		});	
		this.addSlot(new StaticPowerContainerSlot(teChargingStation.slotsInput, 2, 89, 24) {
			@Override
	        public boolean isItemValid(ItemStack itemStack) {
		          return itemStack.getItem() instanceof IEnergyContainerItem;
		    }
		});	
		this.addSlot(new StaticPowerContainerSlot(teChargingStation.slotsInput, 3, 108, 24) {
			@Override
	        public boolean isItemValid(ItemStack itemStack) {
		          return itemStack.getItem() instanceof IEnergyContainerItem;
		    }
		});	
		
		//Output
		this.addSlot(new OutputSlot(teChargingStation.slotsOutput, 0, 46, 52));
		this.addSlot(new OutputSlot(teChargingStation.slotsOutput, 1, 69, 52));
		this.addSlot(new OutputSlot(teChargingStation.slotsOutput, 2, 92, 52));
		this.addSlot(new OutputSlot(teChargingStation.slotsOutput, 3, 115, 52));
		
		//Upgrades
		this.addSlot(new UpgradeSlot(teChargingStation.slotsUpgrades, 0, 152, 8));
		this.addSlot(new UpgradeSlot(teChargingStation.slotsUpgrades, 1, 152, 30));
		this.addSlot(new UpgradeSlot(teChargingStation.slotsUpgrades, 2, 152, 52));
		
		//Battery
		this.addSlot(new BatterySlot(teChargingStation.slotsInternal, 0, 8, 54));
		
		//Armor
		this.addSlot(new Slot(invPlayer, 39, -24, 14) {
			@Override
	        public boolean isItemValid(ItemStack itemStack) {
                return itemStack.getItem().isValidArmor(itemStack, EquipmentSlotType.HEAD, invPlayer.player);
		    }
		});	
		this.addSlot(new Slot(invPlayer, 38, -24, 33) {
			@Override
	        public boolean isItemValid(ItemStack itemStack) {
                return itemStack.getItem().isValidArmor(itemStack, EquipmentSlotType.CHEST, invPlayer.player);
		    }
		});	
		this.addSlot(new Slot(invPlayer, 37, -24, 52) {
			@Override
	        public boolean isItemValid(ItemStack itemStack) {
                return itemStack.getItem().isValidArmor(itemStack, EquipmentSlotType.LEGS, invPlayer.player);
		    }
		});	
		this.addSlot(new Slot(invPlayer, 36, -24, 71) {
			@Override
	        public boolean isItemValid(ItemStack itemStack) {
	            return itemStack.getItem().isValidArmor(itemStack, EquipmentSlotType.FEET, invPlayer.player);
		    }
		});	

		this.addPlayerInventory(invPlayer, 8, 84);
		this.addPlayerHotbar(invPlayer, 8, 142);
	}
	
	@Override
	protected boolean playerItemShiftClicked(ItemStack stack, PlayerEntity player, PlayerInventory invPlayer, Slot slot, int slotIndex) {
        if (stack.getItem() instanceof IEnergyContainerItem && !mergeItemStack(stack, 0, 4, false)) {
        	return true;
        }
        if (stack.getItem() instanceof BaseUpgrade && !mergeItemStack(stack, 8, 11, false)) {
        	return true;
        }
		return false;	
	}
}

