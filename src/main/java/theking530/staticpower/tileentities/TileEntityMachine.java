package theking530.staticpower.tileentities;

import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import theking530.staticpower.tileentities.components.EnergyStorageComponent;
import theking530.staticpower.tileentities.components.RedstoneControlComponent;
import theking530.staticpower.tileentities.components.SideConfigurationComponent;
import theking530.staticpower.tileentities.utilities.MachineSideMode;
import theking530.staticpower.tileentities.utilities.RedstoneModeList.RedstoneMode;
import theking530.staticpower.tileentities.utilities.SideConfigurationUtilities;
import theking530.staticpower.tileentities.utilities.SideConfigurationUtilities.BlockSide;

/**
 * @author Amine
 *
 */
public abstract class TileEntityMachine extends TileEntityBase {

	public static final int DEFAULT_RF_CAPACITY = 100000;

	public int defaultEnergyTransferPerTick;
	public int defaultEnergyCapacity;

	public final EnergyStorageComponent energyStorage;
	public final SideConfigurationComponent ioSideConfiguration;
	public final RedstoneControlComponent redstoneControlComponent;

	public int defaultPowerUse;
	public int defaultProcessingTime;

	public int processingTime;
	public int moveSpeed;

	public boolean isUpdateQueued = true;

	public TileEntityMachine(TileEntityType<?> tileEntityType, int upgradeSlotCount, int defaultEnergyCapacity, int defaultPowerUsePerTick, int defaultPowerTransferPerTick,
			int defaultProcessingTime) {
		super(tileEntityType);
		disableFaceInteraction();
		this.defaultEnergyCapacity = defaultEnergyCapacity;
		this.defaultEnergyTransferPerTick = defaultPowerTransferPerTick;
		this.defaultProcessingTime = defaultProcessingTime;
		this.defaultPowerUse = defaultPowerUsePerTick;

		this.processingTime = defaultProcessingTime;
		this.moveSpeed = 4;

		registerComponent(energyStorage = new EnergyStorageComponent("MainEnergyStorage", defaultEnergyCapacity));
		registerComponent(ioSideConfiguration = new SideConfigurationComponent("SideConfiguration", this::onSidesConfigUpdate));
		registerComponent(redstoneControlComponent = new RedstoneControlComponent("RedstoneControlComponent", RedstoneMode.Ignore));

		energyStorage.setMaxReceive(defaultPowerTransferPerTick);
		energyStorage.setMaxExtract(defaultPowerTransferPerTick);
		energyStorage.setCapacity(defaultEnergyCapacity);
	}

	/* Side Control */
	public void onSidesConfigUpdate(Direction worldSpaceSide, MachineSideMode newMode) {
		Direction relativeSpaceSide = SideConfigurationUtilities.getDirectionFromSide(BlockSide.FRONT, getFacingDirection());
		if (disableFaceInteraction && ioSideConfiguration.getWorldSpaceDirectionConfiguration(relativeSpaceSide) != MachineSideMode.Never) {
			ioSideConfiguration.setWorldSpaceDirectionConfiguration(SideConfigurationUtilities.getDirectionFromSide(BlockSide.FRONT, getFacingDirection()), MachineSideMode.Never);
		}
	}

	/* PROCESSING */
	public int getProcessingEnergy() {
		return defaultPowerUse;
	}

	/* ENERGY */
	public float getEnergyPercent() {
		float amount = energyStorage.getEnergyStored();
		float capacity = energyStorage.getMaxEnergyStored();
		float volume = (amount / capacity);
		return volume;
	}

	public boolean hasPower() {
		return energyStorage.getEnergyStored() > 0 ? true : false;
	}

	public int useEnergy(int energyCost) {
		return 0; // extractEnergy(null, energyCost, false);
	}
}
