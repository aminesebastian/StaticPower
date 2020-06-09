package theking530.staticpower.tileentities;

import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
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
	public static final int DEFAULT_RF_CAPACITY = 1000;
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
		if (disableFaceInteraction && ioSideConfiguration.getWorldSpaceDirectionConfiguration(relativeSpaceSide) != MachineSideMode.Never) {
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
		float amount = energyStorage.getEnergyStored();
		float capacity = energyStorage.getMaxEnergyStored();
		return (amount / capacity);
	}

	public boolean hasPower() {
		return energyStorage.getEnergyStored() > 0 ? true : false;
	}

	public int useEnergy(int energyCost) {
		return 0; // extractEnergy(null, energyCost, false);
	}

	@Override
	public ITextComponent getDisplayName() {
		if (getBlockState() != null && getBlockState().getBlock() != null) {
			return new TranslationTextComponent(getBlockState().getBlock().getTranslationKey());
		}
		return new StringTextComponent("**ERROR**");
	}
}
