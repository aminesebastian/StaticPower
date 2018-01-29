package theking530.staticpower.machines.tileentitycomponents;

import java.util.Random;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import theking530.staticpower.tileentity.ISideConfigurable;
import theking530.staticpower.utils.InventoryUtilities;
import theking530.staticpower.utils.SideModeList;
import theking530.staticpower.utils.SideUtilities;
import theking530.staticpower.utils.SideUtilities.BlockSide;

public class TileEntityInputServo implements ITileEntityComponent{

	private Random randomGenerator = new Random();
	
	private int inputTimer;
	private int inputTime;
	private ISideConfigurable sideConfigurable;
	private TileEntity tileEntity;
	private ItemStackHandler inventory;
	private int[] slots;
	private boolean isEnabled;
	
	public TileEntityInputServo(TileEntity tileEntity, int inputTime, ItemStackHandler inventory, int... slots) {
		this.tileEntity = tileEntity;
		this.inputTime = inputTime;
		this.inventory = inventory;
		this.slots = slots;
		
		if(tileEntity instanceof ISideConfigurable) {
			sideConfigurable = (ISideConfigurable)tileEntity;
		}
	}
	@Override
	public String getComponentName() {
		return "Input Servo";
	}
	@Override
	public void update() {
		if(!tileEntity.getWorld().isRemote) {
			if(inventory != null && inventory.getSlots() > 0 && tileEntity != null) {
				int randomIndex = randomGenerator.nextInt(slots.length);		
				int slot = slots[randomIndex];
				int rand = randomGenerator.nextInt(5);
				inputTimer++;
				if(inputTimer>0) {
					if(inputTimer >= inputTime) {
						switch(rand) {
						case 0: inputItem(slot, BlockSide.TOP, 0);
								break;
						case 1: inputItem(slot, BlockSide.BOTTOM, 0);
								break;
						case 2: inputItem(slot, BlockSide.RIGHT, 0);
								break;
						case 3: inputItem(slot, BlockSide.LEFT, 0);
								break;
						case 4: inputItem(slot, BlockSide.BACK, 0);
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
		if (sideConfigurable == null || sideConfigurable.getSideConfiguration(blockSide) == SideModeList.Mode.Input) {
			EnumFacing facing = tileEntity.getWorld().getBlockState(tileEntity.getPos()).getValue(BlockHorizontal.FACING);
			TileEntity te = tileEntity.getWorld().getTileEntity(tileEntity.getPos().offset(SideUtilities.getEnumFacingFromSide(blockSide, facing)));
			if (te != null && te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, SideUtilities.getEnumFacingFromSide(blockSide, facing))) {
				IItemHandler inv = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, SideUtilities.getEnumFacingFromSide(blockSide, facing));
				for(int currentTESlot = startSlot; currentTESlot < inv.getSlots(); currentTESlot++) {
					ItemStack stack = inv.getStackInSlot(currentTESlot).copy();
					if(stack != null) { //Find a way to check if input item is valid.
						if (InventoryUtilities.canInsertItemIntoSlot(inventory, inputSlot, stack)) {	
							System.out.println("can Insert");
							ItemStack returnedStack = inventory.insertItem(inputSlot, stack, false);
							inv.extractItem(currentTESlot, stack.getCount() - returnedStack.getCount(), false);																	
						}
					}
				}		
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
}
