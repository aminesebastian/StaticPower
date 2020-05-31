package theking530.staticpower.tileentities.components;

import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import theking530.staticpower.tileentities.StaticPowerTileEntityBlock;
import theking530.staticpower.tileentities.utilities.MachineSideMode;
import theking530.staticpower.tileentities.utilities.SideUtilities;
import theking530.staticpower.tileentities.utilities.SideUtilities.BlockSide;
import theking530.staticpower.tileentities.utilities.interfaces.ISideConfigurable;

public class TileEntityInputServoComponent implements ITileEntityComponent {

	private Random randomGenerator = new Random();

	private int inputTimer;
	private int inputTime;
	private ISideConfigurable sideConfigurable;
	private TileEntity tileEntity;
	private TileEntityInventoryComponent inventory;
	private int[] slots;
	private boolean isEnabled;
	private MachineSideMode inputMode;

	public TileEntityInputServoComponent(TileEntity tileEntity, int inputTime, TileEntityInventoryComponent inventory, MachineSideMode mode, int... slots) {
		this.tileEntity = tileEntity;
		this.inputTime = inputTime;
		this.inventory = inventory;
		this.slots = slots;
		this.inputMode = mode;

		if (tileEntity instanceof ISideConfigurable) {
			sideConfigurable = (ISideConfigurable) tileEntity;
		}
	}

	public TileEntityInputServoComponent(TileEntity tileEntity, int inputTime, TileEntityInventoryComponent inventory, int... slots) {
		this(tileEntity, inputTime, inventory, MachineSideMode.Input, slots);
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
		if (!tileEntity.getWorld().isRemote) {
			if (inventory != null && inventory.getSlots() > 0 && tileEntity != null) {
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
		if (sideConfigurable == null || sideConfigurable.getSideConfiguration(blockSide) == inputMode) {
			Direction facing = tileEntity.getWorld().getBlockState(tileEntity.getPos()).get(StaticPowerTileEntityBlock.FACING);
			TileEntity te = tileEntity.getWorld().getTileEntity(tileEntity.getPos().offset(SideUtilities.getDirectionFromSide(blockSide, facing)));
			if (te != null) {
				te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, SideUtilities.getDirectionFromSide(blockSide, facing).getOpposite()).ifPresent((IItemHandler instance) -> {
					if (te instanceof ISideConfigurable) {
						ISideConfigurable configurable = (ISideConfigurable) te;
						if (configurable.isSideConfigurable()) {
							if (configurable.getSideConfiguration(blockSide.getOpposite()) != MachineSideMode.Regular) {
								return;
							}
						}
					}
					for (int currentTESlot = startSlot; currentTESlot < instance.getSlots(); currentTESlot++) {
						ItemStack actualPull = instance.extractItem(currentTESlot, 64, true);
						if (!actualPull.isEmpty()) {
							ItemStack remaining = inventory.insertItem(inputSlot, actualPull.copy(), false);
							instance.extractItem(currentTESlot, actualPull.getCount() - remaining.getCount(), false);
							break;
						}
					}
				});
			}
		}
	}

	@Override
	public boolean isEnabled() {
		return isEnabled;
	}

	@Override
	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	@Override
	public void postProcessUpdate() {
		// TODO Auto-generated method stub

	}
}
