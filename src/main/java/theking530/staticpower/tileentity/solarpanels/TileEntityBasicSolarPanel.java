package theking530.staticpower.tileentity.solarpanels;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import theking530.staticpower.assists.Tier;
import theking530.staticpower.power.PowerDistributor;
import theking530.staticpower.power.StaticEnergyStorage;
import theking530.staticpower.tileentity.BaseTileEntity;

public class TileEntityBasicSolarPanel extends TileEntity implements ITickable, IEnergyHandler, IEnergyProvider {
	
	public StaticEnergyStorage STORAGE; 
	public PowerDistributor POWER_DIST;
	
	public TileEntityBasicSolarPanel() {
		initializeSolarPanel();
		POWER_DIST = new PowerDistributor(this, STORAGE);
	}
	public void initializeSolarPanel() {
		STORAGE = new StaticEnergyStorage(64);
		STORAGE.setMaxTransfer(10);
		STORAGE.setMaxReceive(10);
		STORAGE.setMaxExtract(10*2);
	}	
	@Override
	public void update() {
		if(!worldObj.isRemote) {
			generateRF();
			if(STORAGE.getEnergyStored() > 0) {
				POWER_DIST.provideRF(EnumFacing.DOWN, STORAGE.getMaxReceive());		
			}
		}
	}	
	//Functionality
	public void generateRF() {
		if(!worldObj.canBlockSeeSky(pos)) {
		}else if(lightRatio() > 0 && STORAGE.getEnergyStored() < STORAGE.getMaxEnergyStored()) {
			STORAGE.receiveEnergy(STORAGE.getMaxReceive(), false);
		}
	}
	public boolean isGenerating() {
		if(!worldObj.canBlockSeeSky(pos)) {
			return false;
		}else if(lightRatio() > 0 && STORAGE.getEnergyStored() < STORAGE.getMaxEnergyStored()) {
			return true;
		}
		return false;
	}
	
	//Light
	public float lightRatio() {
		return calculateLightRatio(worldObj, pos);
	}
	public float calculateLightRatio(World world, BlockPos pos) {
		int lightValue = world.getLightFor(EnumSkyBlock.SKY, pos) - world.getSkylightSubtracted();
		float sunAngle = world.getCelestialAngleRadians(1.0F);

		if(sunAngle < (float) Math.PI) {
		  sunAngle += (0.0F - sunAngle) * 0.2F;
		} else {
			sunAngle += (((float) Math.PI * 2F) - sunAngle) * 0.2F;
		}

		lightValue = Math.round(lightValue * MathHelper.cos(sunAngle));

		lightValue = MathHelper.clamp_int(lightValue, 0, 15);
		return lightValue / 15f;
	}
	
	//NBT & UPDATE			
	@Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
    }		
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        return nbt;
	}	    

	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		if(from == EnumFacing.DOWN) {
			return true;
		}
		return false;
	}
	@Override
	public int getEnergyStored(EnumFacing from) {
		return STORAGE.getEnergyStored();
	}
	@Override
	public int getMaxEnergyStored(EnumFacing from) {
		return STORAGE.getMaxEnergyStored();
	}
	@Override
	public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
		if(from == EnumFacing.DOWN) {
			return STORAGE.extractEnergy(maxExtract, simulate);
		}
		return 0;
	}
}
