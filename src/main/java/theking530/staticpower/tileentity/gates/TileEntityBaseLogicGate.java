package theking530.staticpower.tileentity.gates;

import java.util.ArrayList;

import javax.annotation.Nullable;

import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import theking530.staticpower.utils.SideModeList;
import theking530.staticpower.utils.SideModeList.Mode;

public class TileEntityBaseLogicGate extends TileEntity implements ITickable{
	
	public int[] OUTPUT_SIGNALS = {0,0,0,0,0,0};
	public Mode SIDE_MODES[];
	
	public TileEntityBaseLogicGate() {
		SIDE_MODES = new Mode[]{Mode.Disabled, Mode.Disabled, Mode.Input, Mode.Output, Mode.Output, Mode.Output};
	}
	@Override
	public void update() {

	}
    @Override  
	public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        for(int i=0; i<6; i++) {
            OUTPUT_SIGNALS[i] = nbt.getInteger("OUTPUT" + i);	
        }
        for(int i=0; i<6; i++) {
        	SIDE_MODES[i] = Mode.values()[nbt.getInteger("OUTPUT_MODE" + i)];	
        }
    }		
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        for(int i=0; i<6; i++) {
            nbt.setInteger("OUTPUT" + i, OUTPUT_SIGNALS[i]);
        }
        for(int i=0; i<6; i++) {
            nbt.setInteger("OUTPUT_MODE" + i, SIDE_MODES[i].ordinal());
        }
		return nbt;	
	}
	@Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
	}
    @Nullable
    public SPacketUpdateTileEntity getUpdatePacket(){
    	NBTTagCompound tag = new NBTTagCompound();
    	writeToNBT(tag);
    	return new SPacketUpdateTileEntity(pos, getBlockMetadata(), tag);
    }
	public boolean isOn(){
		return false;
	}
	public ArrayList<EnumFacing> getInputSides(){
		ArrayList<EnumFacing> facingList = new ArrayList();
		for(int i=0; i<6; i++) {
			if(SIDE_MODES[i] == Mode.Input) {
				facingList.add(EnumFacing.values()[i]);
			}
		}
		return facingList;
	}
	public EnumFacing getInputSide(){
		for(int i=0; i<6; i++) {
			if(SIDE_MODES[i] == Mode.Input) {
				return EnumFacing.values()[i];
			}
		}
		return null;
	}
	public int getInputSignal(EnumFacing side) {
		if(side != null) {
			int strength = 0;
			if(worldObj.isBlockIndirectlyGettingPowered(pos.offset(side)) > 0) {
				strength = worldObj.getRedstonePower(pos.offset(side), side);
			}else{
				strength = worldObj.getStrongPower(pos.offset(side));
			}
			return strength;			
		}
		return 0;
	}
	public void setOutputSignal(EnumFacing side, int strength) {
		if(side == getInputSide()) {
			return;
		}else{
			if(SIDE_MODES[side.ordinal()] == Mode.Output) {
				OUTPUT_SIGNALS[side.ordinal()] = strength;
		        worldObj.notifyBlockOfStateChange(pos.offset(side), this.getBlockType());
		        worldObj.markAndNotifyBlock(pos.offset(side), worldObj.getChunkFromBlockCoords(pos.offset(side)), worldObj.getBlockState(pos.offset(side)), worldObj.getBlockState(pos.offset(side)), 2);		
			}       
		}
	}	
	public void setAllOutputs(int strength) {
		for(int i=2; i<6; i++) {
			if(getInputSide() != null && i == getInputSide().ordinal()) {

			}else{
				setOutputSignal(EnumFacing.values()[i], strength);
			}
		}
	}	
	public void reset(){
		for(int i=2; i<6; i++) {
			EnumFacing side = EnumFacing.values()[i];
			OUTPUT_SIGNALS[side.ordinal()] = 0;
	        worldObj.notifyBlockOfStateChange(pos.offset(side), this.getBlockType());
	        worldObj.markAndNotifyBlock(pos.offset(side), worldObj.getChunkFromBlockCoords(pos.offset(side)), worldObj.getBlockState(pos.offset(side)), worldObj.getBlockState(pos.offset(side)), 2);		
		}
	}
	public void updateGate() {
        worldObj.notifyBlockOfStateChange(pos, this.getBlockType());
        worldObj.markAndNotifyBlock(pos, worldObj.getChunkFromBlockCoords(pos), worldObj.getBlockState(pos), worldObj.getBlockState(pos), 2);
		blockType.neighborChanged(worldObj.getBlockState(pos), worldObj, pos, blockType);
        markDirty();
	}
	public int maxInputs(){
		return 1;
	}
	public int getInputCount() {
		int inputCount = 0;
		for(int i=0; i<6; i++) {
			if(SIDE_MODES[i] == Mode.Input) {
				inputCount++;
			}
		}
		return inputCount;
	}
	public void sideRightClicked(EnumFacing Side) {
		if(SIDE_MODES[Side.ordinal()] == Mode.Disabled) {
			if(getInputCount() + 1 <= maxInputs()) {
				SIDE_MODES[Side.ordinal()] = Mode.Input;		
			}else{
				SIDE_MODES[Side.ordinal()] = Mode.Output;		
			}
		}else if(SIDE_MODES[Side.ordinal()] == Mode.Input){
			SIDE_MODES[Side.ordinal()] = Mode.Output;	
		}else{
			SIDE_MODES[Side.ordinal()] = Mode.Disabled;
		}
		reset();
		updateGate();
	}
}
