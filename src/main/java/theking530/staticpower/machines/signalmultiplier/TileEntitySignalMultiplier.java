package theking530.staticpower.machines.signalmultiplier;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import theking530.staticpower.tileentity.BaseTileEntity;

public class TileEntitySignalMultiplier extends BaseTileEntity{

	private String customName;
	
	public int INPUT_SIGNAL_LIMIT;
	public int OUTPUT_SIGNAL_STRENGTH;
	public int SIDE;
	public boolean CONFIRMED;

	public int getInputSignal() {
		Block redstone = worldObj.getBlockState(pos.offset(EnumFacing.NORTH)).getBlock();
		if(redstone == Blocks.REDSTONE_WIRE) {
			int strength = worldObj.getStrongPower(pos.offset(EnumFacing.NORTH));			
			return strength;
		}
		return 0;		
	}
	public void setOutputSignal() {
		Block redstone = worldObj.getBlockState(pos.offset(EnumFacing.SOUTH)).getBlock();
		if(redstone == Blocks.REDSTONE_WIRE) {
			if(INPUT_SIGNAL_LIMIT == this.getInputSignal()) {
				//worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, OUTPUT_SIGNAL_STRENGTH, 3);
			}
		}
	}
	
	public void reset() {
		if(INPUT_SIGNAL_LIMIT != this.getInputSignal()) {
			//worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 0, 3);
		}
	}
	public void update() {
		getInputSignal();
		setOutputSignal();
		reset();
	}	
	@Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
		OUTPUT_SIGNAL_STRENGTH = nbt.getShort("OUTPUT_SIGNAL_STRENGTH");
		INPUT_SIGNAL_LIMIT = nbt.getShort("INPUT_SIGNAL_LIMIT");
    }		
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
		nbt.setShort("OUTPUT_SIGNAL_STRENGTH", (short)OUTPUT_SIGNAL_STRENGTH);
		nbt.setShort("INPUT_SIGNAL_LIMIT", (short)INPUT_SIGNAL_LIMIT);
		return nbt;
	}	
	@Override
	public String getName() {
		return "container.SignalMultiplier";		
	}
	
}
