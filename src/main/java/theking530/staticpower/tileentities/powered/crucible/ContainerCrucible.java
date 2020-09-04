package theking530.staticpower.tileentities.powered.crucible;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.container.StaticPowerTileEntityContainer;
import theking530.staticpower.container.slots.BatteryItemSlot;
import theking530.staticpower.container.slots.OutputSlot;
import theking530.staticpower.container.slots.PlayerArmorItemSlot;
import theking530.staticpower.container.slots.UpgradeItemSlot;
import theking530.staticpower.items.upgrades.BaseUpgrade;
import theking530.staticpower.items.utilities.EnergyHandlerItemStackUtilities;

public class ContainerCrucible extends StaticPowerTileEntityContainer<TileEntityCrucible> {
		@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerCrucible, GuiCrucible> TYPE = new ContainerTypeAllocator<>("machine_crucible", ContainerCrucible::new, GuiCrucible::new);

	public ContainerCrucible(int windowId, PlayerInventory inv, PacketBuffer data) {
		this(windowId, inv, (TileEntityCrucible) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerCrucible(int windowId, PlayerInventory playerInventory, TileEntityCrucible owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		// Input
		this.addSlot(new OutputSlot(getTileEntity().inputInventory, 0, 46, 52));

		// Output
		this.addSlot(new OutputSlot(getTileEntity().outputInventory, 0, 69, 52));

		// Upgrades
		this.addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 0, 152, 8));
		this.addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 1, 152, 30));
		this.addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 2, 152, 52));

		// Battery
		this.addSlot(new BatteryItemSlot(getTileEntity().batteryInventory, 0, 8, 54));

		// Armor
		this.addSlot(new PlayerArmorItemSlot(getPlayerInventory(), 39, -24, 14, EquipmentSlotType.HEAD));
		this.addSlot(new PlayerArmorItemSlot(getPlayerInventory(), 38, -24, 33, EquipmentSlotType.CHEST));
		this.addSlot(new PlayerArmorItemSlot(getPlayerInventory(), 37, -24, 52, EquipmentSlotType.LEGS));
		this.addSlot(new PlayerArmorItemSlot(getPlayerInventory(), 36, -24, 71, EquipmentSlotType.FEET));

		this.addPlayerInventory(getPlayerInventory(), 8, 84);
		this.addPlayerHotbar(getPlayerInventory(), 8, 142);
	}

	@Override
	protected boolean playerItemShiftClicked(ItemStack stack, PlayerEntity player, Slot slot, int slotIndex) {
		if (EnergyHandlerItemStackUtilities.isEnergyContainer(stack) && !mergeItemStack(stack, 0, 4, false)) {
			return true;
		}
		if (stack.getItem() instanceof BaseUpgrade && !mergeItemStack(stack, 8, 11, false)) {
			return true;
		}
		return false;
	}
}
