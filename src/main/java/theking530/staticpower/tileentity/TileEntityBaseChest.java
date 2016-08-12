package theking530.staticpower.tileentity;

import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import theking530.staticpower.machines.BaseTileEntity;
import theking530.staticpower.tileentity.staticchest.ContainerStaticChest;

public class TileEntityBaseChest extends BaseTileEntity{

    public float lidAngle;
    public float prevLidAngle;
    public int numPlayersUsing;
    public int ticksSinceSync;
	
	@Override
	public void update(){
		super.update();
        ++this.ticksSinceSync;
        float f;

        if (!this.worldObj.isRemote) {        
            this.numPlayersUsing = 0;
            f = 5.0F;
            List list = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB((pos.getX() - f), (pos.getY() - f), (pos.getZ() - f), (pos.getX() + 1 + f), (pos.getY() + 1 + f), (pos.getZ()+ 1 + f)));
            Iterator iterator = list.iterator();
            while (iterator.hasNext()) {
                EntityPlayer entityplayer = (EntityPlayer)iterator.next();

                if (entityplayer.openContainer instanceof ContainerStaticChest) {
                    IInventory iinventory = ((ContainerStaticChest)entityplayer.openContainer).getChestInventory();

                    if (iinventory == this || iinventory instanceof InventoryLargeChest && ((InventoryLargeChest)iinventory).isPartOfLargeChest(this)) {
                        ++this.numPlayersUsing;
                    }
                }
            }
        }

        this.prevLidAngle = this.lidAngle;
        f = 0.1F;
        double d2;

        if (this.numPlayersUsing > 0 && this.lidAngle == 0.0F) {
            double d1 = (double)this.pos.getX() + 0.5D;
            d2 = (double)this.pos.getZ() + 0.5D;
           // this.worldObj.playSound((EntityPlayer)null, d1, (double)j + 0.5D, d2, SoundEvents.BLOCK_CHEST_OPEN, SoundCategory.BLOCKS, 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
        }

        if (this.numPlayersUsing == 0 && this.lidAngle > 0.0F || this.numPlayersUsing > 0 && this.lidAngle < 1.0F) {
            float f1 = this.lidAngle;

            if (this.numPlayersUsing > 0) {
                this.lidAngle += f;
            } else {
                this.lidAngle -= f;
            }
            if (this.lidAngle > 1.0F) {
                this.lidAngle = 1.0F;
            }

            float f2 = 0.5F;

            if (this.lidAngle < f2 && f1 >= f2) {
                d2 = (double)this.pos.getX() + 0.5D;
                double d0 = (double)this.pos.getZ() + 0.5D;
                //this.worldObj.playSoundEffect(d2, (double)this.yCoord + 0.5D, d0, "random.chestclosed", 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
            }

            if (this.lidAngle < 0.0F) {
                this.lidAngle = 0.0F;
            }
        }
        if (this.worldObj.isRemote) {        
            this.numPlayersUsing = 0;
            f = 5.0F;
            List list = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB((pos.getX() - f), (pos.getY() - f), (pos.getZ() - f), (pos.getX() + 1 + f), (pos.getY() + 1 + f), (pos.getZ()+ 1 + f)));
            Iterator iterator = list.iterator();
            while (iterator.hasNext()) {
                EntityPlayer entityplayer = (EntityPlayer)iterator.next();

                if (entityplayer.openContainer instanceof ContainerStaticChest) {
                    IInventory iinventory = ((ContainerStaticChest)entityplayer.openContainer).getChestInventory();

                    if (iinventory == this || iinventory instanceof InventoryLargeChest && ((InventoryLargeChest)iinventory).isPartOfLargeChest(this)) {
                        ++this.numPlayersUsing;
                    }
                }
            }
        }  
	}
	//NBT
	@Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
    }		
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        return nbt;
	}	  	    

    public boolean receiveClientEvent(int i, int j) {
        if (i == 1) {
            this.numPlayersUsing = j;
            return true;
        }else{
            return super.receiveClientEvent(i, j);
        }
    }
    //IInventory
	public void setCustomName(String name) {
		this.CUSTOM_NAME = name;
	}
	public int getSizeInventory() {
		return slots.length;
	}
	@Override
	public ItemStack getStackInSlot(int i) {
		return slots[i];
	}
	@Override
	public ItemStack decrStackSize(int i, int j) {
		if (slots[i] !=null) {
			if (slots[i].stackSize <= j) {
				ItemStack itemstack = slots[i];
				slots[i] = null;
				return itemstack;
			}
			
			ItemStack itemstack1 = slots[i].splitStack(j);
			
			if(slots[i].stackSize == 0) {
				slots[i] = null;
			}
			
			return itemstack1;
			
		}else{
			
			return null;
		}
	}	
	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		slots[i] = itemstack;
		if (itemstack != null && itemstack.stackSize > getInventoryStackLimit()) {
			itemstack.stackSize = getInventoryStackLimit();
		}
	}
	@Override
	public boolean hasCustomName() {
		return this.CUSTOM_NAME != null && this.CUSTOM_NAME.length() > 0;
	}
	@Override
	public String getName() {
		return "container.StaticChest";
		
	}					
}
