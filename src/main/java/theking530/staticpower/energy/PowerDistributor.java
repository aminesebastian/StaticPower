package theking530.staticpower.energy;

import cofh.redstoneflux.impl.EnergyStorage;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import theking530.staticpower.utils.SideModeList.Mode;

public class PowerDistributor {

	private TileEntity TE;
	private EnergyStorage E_STORAGE;
	private Mode[] SIDE_SETTINGS;
	
	public PowerDistributor(TileEntity tileEntity, EnergyStorage energyStorage, Mode... modes) {
		TE = tileEntity;
		E_STORAGE = energyStorage;
		SIDE_SETTINGS = modes;
	}
	public void updateSideSettings(Mode... modes) {
		SIDE_SETTINGS = modes;
	}
	public void distributePower() {
		if(E_STORAGE != null && TE != null) { 
			if(!TE.getWorld().isRemote) {
				if(E_STORAGE.getEnergyStored() > 0) {
					for(int i=0; i<6; i++) {
						EnumFacing facing = EnumFacing.values()[i];
						if(SIDE_SETTINGS != null && SIDE_SETTINGS.length > i) {
							if(SIDE_SETTINGS[i] == Mode.Output || SIDE_SETTINGS[i] == Mode.Regular) {
								provideRF(facing, Math.min(E_STORAGE.getMaxExtract(), E_STORAGE.getEnergyStored()));
							}
						}
					}	
				}	
			}
		}
	}
	public int provideRF(EnumFacing facing, int amount) {
		return provideRF(TE.getPos().offset(facing), facing, amount);
	}
	/**
	 * @param pos - The position of the block to send energy to.
	 * @param facing - The side of the block we are sending energy to.
	 * @param amount - The amount of energy to send.
	 * @return - The actual amount of energy that was sent.
	 */
	public int provideRF(BlockPos pos, EnumFacing facing, int amount) {
		if(getEnergyHandlerPosition(pos, facing) != null) {
			IEnergyStorage handler = getEnergyHandlerPosition(pos, facing);
			if(handler.getEnergyStored() < handler.getMaxEnergyStored() && handler.canReceive()) {
				int provided = handler.receiveEnergy(amount, false);
				E_STORAGE.extractEnergy(provided, false);
				return provided;
			}
		}
		return 0;
	}
	public IEnergyStorage getEnergyHandlerPosition(EnumFacing facing) {
		return getEnergyHandlerPosition(TE.getPos().offset(facing), facing);
	}
	public IEnergyStorage getEnergyHandlerPosition(BlockPos pos, EnumFacing facing) {
		if (TE.getWorld().getTileEntity(pos) != null && TE.getWorld().getTileEntity(pos).hasCapability(CapabilityEnergy.ENERGY, facing)) {					
			TileEntity te = TE.getWorld().getTileEntity(pos);
			IEnergyStorage  handler = te.getCapability(CapabilityEnergy.ENERGY, facing);//access side must be opposite
			return handler;
		}
		return null;
	}
}
