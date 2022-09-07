package theking530.staticpower.tileentities.powered.chargingstation;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.tileentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticpower.client.rendering.tileentity.TileEntityRenderChargingStation;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.items.utilities.EnergyHandlerItemStackUtilities;
import theking530.staticpower.tileentities.TileEntityMachine;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.components.items.BatteryInventoryComponent;
import theking530.staticpower.tileentities.components.items.InputServoComponent;
import theking530.staticpower.tileentities.components.items.InventoryComponent;
import theking530.staticpower.tileentities.components.items.ItemStackHandlerFilter;
import theking530.staticpower.tileentities.components.items.OutputServoComponent;
import theking530.staticpower.tileentities.components.items.UpgradeInventoryComponent;
import theking530.staticpower.utilities.InventoryUtilities;

public class TileEntityChargingStation extends TileEntityMachine {
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityChargingStation> TYPE = new BlockEntityTypeAllocator<TileEntityChargingStation>(
			(type, pos, state) -> new TileEntityChargingStation(pos, state), ModBlocks.ChargingStation);

	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setTileEntitySpecialRenderer(TileEntityRenderChargingStation::new);
		}
	}

	public final InventoryComponent unchargedInventory;
	public final InventoryComponent chargedInventory;
	public final BatteryInventoryComponent batteryInventory;
	public final UpgradeInventoryComponent upgradesInventory;

	public TileEntityChargingStation(BlockPos pos, BlockState state) {
		super(TYPE, pos, state, StaticPowerTiers.ENERGIZED);

		// Add the input inventory that only takes energy storing items.
		registerComponent(
				unchargedInventory = new InventoryComponent("unchargedInventory", 4, MachineSideMode.Input).setShiftClickEnabled(true).setFilter(new ItemStackHandlerFilter() {
					public boolean canInsertItem(int slot, ItemStack stack) {
						return EnergyHandlerItemStackUtilities.isEnergyContainer(stack);
					}
				}));

		// Add the rest of the inventories.
		registerComponent(chargedInventory = new InventoryComponent("chargedInventory", 4, MachineSideMode.Output));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", powerStorage));

		// Create the item i/o servos.
		registerComponent(new OutputServoComponent("OutputServo", chargedInventory));
		registerComponent(new InputServoComponent("InputServo", unchargedInventory));

		// Set the energy storage upgrade inventory.
		powerStorage.setUpgradeInventory(upgradesInventory);
	}

	@Override
	public void process() {
		if (!getLevel().isClientSide()) {
			// Charge up to four items simultaneously.
			if (powerStorage.getStoredPower() > 0) {
				// Capture the count of chargeable items.
				int count = getCountOfChargeableItems();

				// If there are no items, return early.
				if (count == 0) {
					return;
				}

				// Get the amount of power to apply to each item.
				double maxOutput = (powerStorage.getVoltageOutput() * powerStorage.getMaximumCurrentOutput()) / count;

				// Attempt to charge each item.
				for (int i = 0; i < unchargedInventory.getSlots(); i++) {
					// Get the item to charge.
					ItemStack stack = unchargedInventory.getStackInSlot(i);
					// If it's not empty and is an energy storing item.
					if (stack != ItemStack.EMPTY && EnergyHandlerItemStackUtilities.isEnergyContainer(stack)) {
						if (EnergyHandlerItemStackUtilities.getStoredPower(stack) < EnergyHandlerItemStackUtilities.getCapacity(stack)) {
							double charged = EnergyHandlerItemStackUtilities.addPower(stack, powerStorage.getVoltageOutput(), maxOutput, false);
							powerStorage.usePowerIgnoringVoltageLimitations(charged);
						} else {
							moveChargedItemToOutputs(i);
						}
					}
				}
			}
		}
	}

	/**
	 * Attempt to move the charged {@link ItemStack} from the fromSlot of the
	 * unchargedInventory into any open slot in the chargedInventory.
	 * 
	 * @param fromSlot The slot that contains the charged {@link ItemStack}.
	 */
	public void moveChargedItemToOutputs(int fromSlot) {
		// Iterate through each of the output slots.
		for (int i = 0; i < 4; i++) {
			// If we can place the charged item into an output slot, do so. There's no need
			// to check the result of the call to insertItem as we already know the result
			// is going to be empty.
			if (InventoryUtilities.canFullyInsertStackIntoSlot(chargedInventory, i, unchargedInventory.getStackInSlot(fromSlot))) {
				ItemStack stack = unchargedInventory.extractItem(fromSlot, 1, false);
				chargedInventory.insertItem(i, stack, false);
			}
		}
	}

	public int getCountOfChargeableItems() {
		// Capture the count of chargeable items.
		int count = 0;
		for (int i = 0; i < unchargedInventory.getSlots(); i++) {
			ItemStack stack = unchargedInventory.getStackInSlot(i);
			if (stack != ItemStack.EMPTY && EnergyHandlerItemStackUtilities.isEnergyContainer(stack)) {
				if (EnergyHandlerItemStackUtilities.getStoredPower(stack) < EnergyHandlerItemStackUtilities.getCapacity(stack)) {
					count++;
				}
			}
		}
		return count;
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerChargingStation(windowId, inventory, this);
	}

	@Override
	public Component getDisplayName() {
		return new TranslatableComponent(ModBlocks.ChargingStation.get().getDescriptionId());
	}
}
