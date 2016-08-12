package theking530.staticpower.conduits.fluidconduit;

import java.util.ArrayList;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import theking530.staticpower.conduits.TileEntityBaseConduit;
import theking530.staticpower.utils.Vector3;
import theking530.staticpower.utils.WorldUtilities;

public class TileEntityFluidConduit extends TileEntityBaseConduit implements IFluidHandler{
	
	static final int conduitVolume = 250;
	public FluidTank TANK = new FluidTank(conduitVolume);
	
	public int MAX_EXTRACT = 60;
	public int MAX_IO = 100;
	public int PACKET_OUTPUT=0;
	public int PACKET_INPUT=0;

	public boolean INITIALIZED = false;
	
	public TileEntityFluidConduit() {	
		
	}
	
	public void updateEntity() {

		updateConduitRenderConnections();
		updateRecieverRenderConnections();	
	}	

	public int getAdjustedVolume(int adjustment) {
		return (int)(((float)TANK.getFluidAmount()/(float)TANK.getCapacity())*adjustment);
	}
	
	//NBT and Sync
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		TANK.readFromNBT(nbt);
	}		
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		TANK.writeToNBT(nbt);
		return nbt;
	}	    	
	
	//Conduit Functionality
	@Override
	public IFluidTankProperties[] getTankProperties() {
		return TANK.getTankProperties();
	}
	@Override
	public int fill(FluidStack resource, boolean doFill) {
		return TANK.fill(resource, doFill);
	}
	@Override
	public FluidStack drain(FluidStack resource, boolean doDrain) {
		return TANK.drain(resource, doDrain);
	}
	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		return TANK.drain(maxDrain, doDrain);
	}
}
