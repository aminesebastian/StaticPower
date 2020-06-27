package theking530.staticpower.tileentities.components;

import java.util.Random;
import java.util.function.Predicate;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import theking530.staticpower.tileentities.StaticPowerTileEntityBlock;
import theking530.staticpower.tileentities.utilities.MachineSideMode;
import theking530.staticpower.tileentities.utilities.SideConfigurationUtilities;
import theking530.staticpower.tileentities.utilities.SideConfigurationUtilities.BlockSide;

public class InputServoComponent extends AbstractTileEntityComponent {

	private Random randomGenerator = new Random();

	private int inputTimer;
	private int inputTime;
	private InventoryComponent inventory;
	private int[] slots;
	private MachineSideMode inputMode;
	private Predicate<ItemStack> filter;

	public InputServoComponent(String name, int inputTime, InventoryComponent inventory, MachineSideMode mode, Predicate<ItemStack> filter, int... slots) {
		super(name);
		this.inputTime = inputTime;
		this.inventory = inventory;
		if (slots.length == 0) {
			this.slots = new int[inventory.getSlots()];
			for (int i = 0; i < inventory.getSlots(); i++) {
				this.slots[i] = i;
			}
		} else {
			this.slots = slots;
		}

		this.inputMode = mode;
	}

	public InputServoComponent(String name, int inputTime, InventoryComponent inventory, MachineSideMode mode, int... slots) {
		this(name, inputTime, inventory, mode, null, slots);
	}

	public InputServoComponent(String name, int inputTime, InventoryComponent inventory, Predicate<ItemStack> filter, int... slots) {
		this(name, inputTime, inventory, MachineSideMode.Input, filter, slots);
	}

	public InputServoComponent(String name, int inputTime, InventoryComponent inventory, int... slots) {
		this(name, inputTime, inventory, MachineSideMode.Input, slots);
	}

	@Override
	public String getComponentName() {
		return "Input Servo";
	}

	public MachineSideMode getMode() {
		return inputMode;
	}

	@Override
	public void preProcessUpdate() {
		if (!getTileEntity().getWorld().isRemote) {
			if (inventory != null && inventory.getSlots() > 0 && getTileEntity() != null) {
				int randomIndex = randomGenerator.nextInt(slots.length);
				int slot = slots[randomIndex];
				int rand = randomGenerator.nextInt(5);
				inputTimer++;
				if (inputTimer > 0) {
					if (inputTimer >= inputTime) {
						switch (rand) {
						case 0:
							inputItem(slot, BlockSide.TOP, 0);
							break;
						case 1:
							inputItem(slot, BlockSide.BOTTOM, 0);
							break;
						case 2:
							inputItem(slot, BlockSide.RIGHT, 0);
							break;
						case 3:
							inputItem(slot, BlockSide.LEFT, 0);
							break;
						case 4:
							inputItem(slot, BlockSide.BACK, 0);
							break;
						default:
							break;
						}
						inputTimer = 0;
					}
				}
			}
		}
	}

	public void inputItem(int inputSlot, BlockSide blockSide, int startSlot) {
		if (canInputFromSide(blockSide)) {
			Direction facing = getTileEntity().getWorld().getBlockState(getTileEntity().getPos()).get(StaticPowerTileEntityBlock.FACING);
			TileEntity te = getTileEntity().getWorld().getTileEntity(getTileEntity().getPos().offset(SideConfigurationUtilities.getDirectionFromSide(blockSide, facing)));
			if (te != null) {
				te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, SideConfigurationUtilities.getDirectionFromSide(blockSide, facing).getOpposite()).ifPresent((IItemHandler instance) -> {
					for (int currentTESlot = startSlot; currentTESlot < instance.getSlots(); currentTESlot++) {
						// Attempt to pull the item, but only simulate it.
						ItemStack actualPull = instance.extractItem(currentTESlot, 64, true);

						// If the item is not empty, continue checking.
						if (!actualPull.isEmpty()) {
							// If there is a filter supplied, use it to check the item that was simulated.
							// If it does not pass, skip this item.
							if (filter != null && !filter.test(actualPull)) {
								continue;
							}

							// If we made it this far, attempt to insert the item and see what's remaining.
							ItemStack remaining = inventory.insertItem(inputSlot, actualPull.copy(), false);

							// Based on what is remaining, extract that from the input inventory.
							instance.extractItem(currentTESlot, actualPull.getCount() - remaining.getCount(), false);
							break;
						}
					}
				});
			}
		}
	}

	public boolean canInputFromSide(BlockSide blockSide) {
		if (getTileEntity().hasComponentOfType(SideConfigurationComponent.class)) {
			return getTileEntity().getComponent(SideConfigurationComponent.class).getWorldSpaceDirectionConfiguration(SideConfigurationUtilities.getDirectionFromSide(blockSide, getTileEntity().getFacingDirection())) == inputMode;
		}
		return true;
	}
}
