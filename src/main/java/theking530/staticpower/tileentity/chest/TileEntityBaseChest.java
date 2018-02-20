package theking530.staticpower.tileentity.chest;

import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.items.CapabilityItemHandler;
import theking530.staticpower.assists.utilities.SideUtilities;
import theking530.staticpower.assists.utilities.SideUtilities.BlockSide;
import theking530.staticpower.tileentity.BaseTileEntity;
import theking530.staticpower.tileentity.chest.staticchest.ContainerStaticChest;

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

        if (!this.getWorld().isRemote) {        
            this.numPlayersUsing = 0;
            f = 5.0F;
            List<EntityPlayer> list = this.getWorld().getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB((pos.getX() - f), (pos.getY() - f), (pos.getZ() - f), (pos.getX() + 1 + f), (pos.getY() + 1 + f), (pos.getZ()+ 1 + f)));
            Iterator<EntityPlayer> iterator = list.iterator();
            while (iterator.hasNext()) {
                EntityPlayer entityplayer = (EntityPlayer)iterator.next();

                //if (entityplayer.openContainer instanceof ContainerStaticChest) {
                   // numPlayersUsing++;
               // }
            }
        }

        this.prevLidAngle = this.lidAngle;
        f = 0.1F;
        double d2;

        if (this.numPlayersUsing > 0 && this.lidAngle == 0.0F) {
            double d1 = (double)this.pos.getX() + 0.5D;
            d2 = (double)this.pos.getZ() + 0.5D;
           // this.getWorld().playSound((EntityPlayer)null, d1, (double)j + 0.5D, d2, SoundEvents.BLOCK_CHEST_OPEN, SoundCategory.BLOCKS, 0.5F, this.getWorld().rand.nextFloat() * 0.1F + 0.9F);
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
                //this.getWorld().playSoundEffect(d2, (double)this.yCoord + 0.5D, d0, "random.chestclosed", 0.5F, this.getWorld().rand.nextFloat() * 0.1F + 0.9F);
            }

            if (this.lidAngle < 0.0F) {
                this.lidAngle = 0.0F;
            }
        }
        if (this.getWorld().isRemote) {        
            this.numPlayersUsing = 0;
            f = 5.0F;
            List<EntityPlayer> list = this.getWorld().getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB((pos.getX() - f), (pos.getY() - f), (pos.getZ() - f), (pos.getX() + 1 + f), (pos.getY() + 1 + f), (pos.getZ()+ 1 + f)));
            Iterator<EntityPlayer> iterator = list.iterator();
            while (iterator.hasNext()) {
                EntityPlayer entityplayer = (EntityPlayer)iterator.next();

                if (entityplayer.openContainer instanceof ContainerStaticChest) {
                    IInventory iinventory = ((ContainerStaticChest)entityplayer.openContainer).getChestInventory();

                }
            }
        }  
	}

    public boolean receiveClientEvent(int i, int j) {
        if (i == 1) {
            this.numPlayersUsing = j;
            return true;
        }else{
            return super.receiveClientEvent(i, j);
        }
    }
	@Override
	public String getName() {
		return "container.StaticChest";
		
	}					
	@Override
	public boolean isSideConfigurable() {
		return false;
	}
	/* CAPABILITIES */
    public boolean hasCapability(net.minecraftforge.common.capabilities.Capability<?> capability, net.minecraft.util.EnumFacing facing){
    	if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
    		return true;
    	}
        return super.hasCapability(capability, facing);
    }
    public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, net.minecraft.util.EnumFacing facing){
    	if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
    		return (T) slotsOutput;
    	}
    	return super.getCapability(capability, facing);
    }  
}
