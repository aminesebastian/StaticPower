package theking530.staticpower.tileentities.components;

import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import theking530.staticpower.tileentities.StaticPowerTileEntityBlock;
import theking530.staticpower.tileentities.utilities.MachineSideMode;
import theking530.staticpower.tileentities.utilities.SideConfigurationUtilities;
import theking530.staticpower.tileentities.utilities.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.utilities.InventoryUtilities;

public class OutputServoComponent extends AbstractTileEntityComponent {

	private Random randomGenerator = new Random();

	private int outputTimer;
	private int outputTime;
	private InventoryComponent inventory;
	private int[] slots;
	private MachineSideMode outputMode;

	public OutputServoComponent(String name, int outputTime, InventoryComponent inventory, MachineSideMode mode, int... slots) {
		super(name);
		this.outputTime = outputTime;
		this.inventory = inventory;
		this.slots = slots;
		this.outputMode = mode;
	}

	public OutputServoComponent(String name, int outputTime, InventoryComponent inventory, int... slots) {
		this(name,  outputTime, inventory, MachineSideMode.Output, slots);
	}

	@Override
	public void preProcessUpdate() {
		if (!getTileEntity().getWorld().isRemote) {
			if (inventory != null && inventory.getInventory().getSlots() > 0 && getTileEntity() != null) {
				int randomIndex = randomGenerator.nextInt(slots.length);
				int slot = slots[randomIndex];
				if (inventory.getInventory().getStackInSlot(slot) != ItemStack.EMPTY) {
					outputTimer++;
					if (outputTimer > 0) {
						int rand = randomGenerator.nextInt(5);
						if (outputTimer >= outputTime) {
							switch (rand) {
							case 0:
								outputItem(slot, BlockSide.TOP, 0, false);
								break;
							case 1:
								outputItem(slot, BlockSide.BOTTOM, 0, false);
								break;
							case 2:
								outputItem(slot, BlockSide.RIGHT, 0, false);
								break;
							case 3:
								outputItem(slot, BlockSide.LEFT, 0, false);
								break;
							case 4:
								outputItem(slot, BlockSide.BACK, 0, false);
								break;
							default:
								break;
							}
							outputTimer = 0;
						}
					}
				}
			}
		}
	}

	public void outputItem(int fromSlot, BlockSide blockSide, int startSlot, boolean backwards) {
		if (canOutputFromSide(blockSide)) {
			ItemStack stack = inventory.getInventory().getStackInSlot(fromSlot);
			if (!stack.isEmpty()) {
				Direction facing = getTileEntity().getWorld().getBlockState(getTileEntity().getPos()).get(StaticPowerTileEntityBlock.FACING);
				TileEntity te = getTileEntity().getWorld().getTileEntity(getTileEntity().getPos().offset(SideConfigurationUtilities.getDirectionFromSide(blockSide, facing)));
				if (te != null) {
					te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, SideConfigurationUtilities.getDirectionFromSide(blockSide, facing).getOpposite()).ifPresent((IItemHandler instance) -> {
						inventory.getInventory().setStackInSlot(fromSlot, InventoryUtilities.insertItemIntoInventory(instance, stack));
					});
				}
			}
		}
	}

	public boolean canOutputFromSide(BlockSide blockSide) {
		if (getTileEntity().hasComponentOfType(SideConfigurationComponent.class)) {
			return getTileEntity().getComponent(SideConfigurationComponent.class)
					.getWorldSpaceDirectionConfiguration(SideConfigurationUtilities.getDirectionFromSide(blockSide, getTileEntity().getFacingDirection())) == outputMode;
		}
		return true;
	}
}
