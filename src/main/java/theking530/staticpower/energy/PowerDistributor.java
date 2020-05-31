package theking530.staticpower.energy;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import theking530.staticpower.tileentities.utilities.MachineSideMode;
import theking530.staticpower.tileentities.utilities.interfaces.ISideConfigurable;

public class PowerDistributor {

	private TileEntity poweredTileEntity;
	private ISideConfigurable sideConfigurable;
	private IEnergyStorage energyStorage;
	
	public PowerDistributor(TileEntity tileEntity, IEnergyStorage energyStorage) {
		poweredTileEntity = tileEntity;
		this.energyStorage = energyStorage;
		if(poweredTileEntity instanceof ISideConfigurable) {
			sideConfigurable = (ISideConfigurable)poweredTileEntity;
		}
	}
	public void distributePower() {
		if(energyStorage != null && poweredTileEntity != null) { 
			if(energyStorage.getEnergyStored() > 0) {
				for(Direction facing : Direction.values()) {
					if(sideConfigurable == null || (sideConfigurable != null && sideConfigurable.getSideConfiguration(facing) == MachineSideMode.Output)) {
						int maxExtract = energyStorage.extractEnergy(Integer.MAX_VALUE, true);
						provideRF(facing, Math.min(maxExtract, energyStorage.getEnergyStored()));
					}
				}	
			}	
		}
	}
	public int provideRF(Direction facing, int amount) {
		return provideRF(poweredTileEntity.getPos().offset(facing), facing, amount);
	}
	/**
	 * @param pos - The position of the block to send energy to.
	 * @param facing - The side of the block we are sending energy to.
	 * @param amount - The amount of energy to send.
	 * @return - The actual amount of energy that was sent.
	 */
	public int provideRF(BlockPos pos, Direction facing, int amount) {
		IEnergyStorage handler = getEnergyHandlerPosition(pos, facing.getOpposite());

		if(handler != null) {
			if(handler.getEnergyStored() < handler.getMaxEnergyStored() && handler.canReceive()) {
				int provided = handler.receiveEnergy(amount, false);
				energyStorage.extractEnergy(provided, false);
				return provided;
			}
		}
		return 0;
	}
	public IEnergyStorage getEnergyHandlerPosition(BlockPos pos, Direction facing) {
		TileEntity te = poweredTileEntity.getWorld().getTileEntity(pos);
		if (te != null) {	
			LazyOptional<IEnergyStorage> energyStorageOptional = te.getCapability(CapabilityEnergy.ENERGY, facing);
			if(energyStorageOptional.isPresent()) {
				return energyStorageOptional.orElse(null);
			}
		}
		return null;
	}
}
