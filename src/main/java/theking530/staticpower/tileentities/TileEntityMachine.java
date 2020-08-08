package theking530.staticpower.tileentities;

import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import theking530.staticpower.tileentities.components.control.RedstoneControlComponent;
import theking530.staticpower.tileentities.components.control.SideConfigurationComponent;
import theking530.staticpower.tileentities.components.power.EnergyStorageComponent;
import theking530.staticpower.tileentities.utilities.MachineSideMode;
import theking530.staticpower.tileentities.utilities.RedstoneMode;
import theking530.staticpower.tileentities.utilities.SideConfigurationUtilities;
import theking530.staticpower.tileentities.utilities.SideConfigurationUtilities.BlockSide;

/**
 * @author Amine
 *
 */
public abstract class TileEntityMachine extends TileEntityBase {
	public static final int DEFAULT_RF_CAPACITY = 2500;
	public static final int DEFAULT_POWER_TRANSFER = 50;

	public final EnergyStorageComponent energyStorage;
	public final SideConfigurationComponent ioSideConfiguration;
	public final RedstoneControlComponent redstoneControlComponent;

	public boolean isUpdateQueued = true;

	public TileEntityMachine(TileEntityType<?> tileEntityType) {
		super(tileEntityType);
		disableFaceInteraction();

		registerComponent(energyStorage = new EnergyStorageComponent("MainEnergyStorage", DEFAULT_RF_CAPACITY, DEFAULT_POWER_TRANSFER, DEFAULT_POWER_TRANSFER));
		registerComponent(ioSideConfiguration = new SideConfigurationComponent("SideConfiguration", this::onSidesConfigUpdate, this::checkSideConfiguration));
		registerComponent(redstoneControlComponent = new RedstoneControlComponent("RedstoneControlComponent", RedstoneMode.Ignore));
	}

	/* Side Control */
	protected void onSidesConfigUpdate(Direction worldSpaceSide, MachineSideMode newMode) {
		Direction relativeSpaceSide = SideConfigurationUtilities.getDirectionFromSide(BlockSide.FRONT, getFacingDirection());
		if (DisableFaceInteraction && ioSideConfiguration.getWorldSpaceDirectionConfiguration(relativeSpaceSide) != MachineSideMode.Never) {
			ioSideConfiguration.setWorldSpaceDirectionConfiguration(SideConfigurationUtilities.getDirectionFromSide(BlockSide.FRONT, getFacingDirection()), MachineSideMode.Never);
		}
	}

	protected boolean isValidSideConfiguration(BlockSide side, MachineSideMode mode) {
		return mode == MachineSideMode.Disabled || mode == MachineSideMode.Regular || mode == MachineSideMode.Output || mode == MachineSideMode.Input;
	}

	private boolean checkSideConfiguration(Direction direction, MachineSideMode mode) {
		return isValidSideConfiguration(SideConfigurationUtilities.getBlockSide(direction, getFacingDirection()), mode);
	}

	/* ENERGY */
	public float getEnergyPercent() {
		float amount = energyStorage.getStorage().getEnergyStored();
		float capacity = energyStorage.getStorage().getMaxEnergyStored();
		return (amount / capacity);
	}

	public boolean hasPower() {
		return energyStorage.getStorage().getEnergyStored() > 0;
	}
}
