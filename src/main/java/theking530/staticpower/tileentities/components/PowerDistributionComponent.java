package theking530.staticpower.tileentities.components;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import theking530.staticpower.tileentities.utilities.MachineSideMode;

public class PowerDistributionComponent extends AbstractTileEntityComponent {

	private IEnergyStorage energyStorage;
	private MachineSideMode outputMode;

	public PowerDistributionComponent(String name, IEnergyStorage energyStorage) {
		this(name, energyStorage, MachineSideMode.Output);
	}

	public PowerDistributionComponent(String name, IEnergyStorage energyStorage, MachineSideMode mode) {
		super(name);
		this.energyStorage = energyStorage;
		this.outputMode = mode;
	}

	@Override
	public void preProcessUpdate() {
		if (getWorld().isRemote) {
			return;
		}
		if (energyStorage != null && getTileEntity() != null) {
			if (energyStorage.getEnergyStored() > 0) {
				for (Direction facing : Direction.values()) {
					if (canOutputFromSide(facing)) {
						int maxExtract = energyStorage.extractEnergy(Integer.MAX_VALUE, true);
						int providedRF = provideRF(facing, Math.min(maxExtract, energyStorage.getEnergyStored()));
						energyStorage.extractEnergy(providedRF, false);
					}
				}
			}
		}
	}

	public int provideRF(Direction facing, int amount) {
		return provideRF(getTileEntity().getPos().offset(facing), facing, amount);
	}

	/**
	 * @param pos    - The position of the block to send energy to.
	 * @param facing - The side of the block we are sending energy to.
	 * @param amount - The amount of energy to send.
	 * @return - The actual amount of energy that was sent.
	 */
	public int provideRF(BlockPos pos, Direction facing, int amount) {
		IEnergyStorage handler = getEnergyHandlerPosition(pos, facing.getOpposite());

		if (handler != null) {
			if (handler.getEnergyStored() < handler.getMaxEnergyStored() && handler.canReceive()) {
				int provided = handler.receiveEnergy(amount, false);
				energyStorage.extractEnergy(provided, false);
				return provided;
			}
		}
		return 0;
	}

	public IEnergyStorage getEnergyHandlerPosition(BlockPos pos, Direction facing) {
		TileEntity te = getTileEntity().getWorld().getTileEntity(pos);
		if (te != null) {
			LazyOptional<IEnergyStorage> energyStorageOptional = te.getCapability(CapabilityEnergy.ENERGY, facing);
			if (energyStorageOptional.isPresent()) {
				return energyStorageOptional.orElse(null);
			}
		}
		return null;
	}

	public boolean canOutputFromSide(Direction facing) {
		if (getTileEntity().hasComponentOfType(SideConfigurationComponent.class)) {
			return getTileEntity().getComponent(SideConfigurationComponent.class).getWorldSpaceDirectionConfiguration(facing) == outputMode;
		}
		return true;
	}
}
