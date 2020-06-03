package theking530.staticpower.tileentities;

import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import theking530.staticpower.tileentities.components.EnergyStorageComponent;
import theking530.staticpower.tileentities.components.RedstoneControlComponent;
import theking530.staticpower.tileentities.components.SideConfigurationComponent;
import theking530.staticpower.tileentities.components.SideConfigurationComponent.SideIncrementDirection;
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

		registerComponent(energyStorage = new EnergyStorageComponent("MainEnergyStorage", DEFAULT_RF_CAPACITY));
		registerComponent(ioSideConfiguration = new SideConfigurationComponent("SideConfiguration", this::onSidesConfigUpdate));
		registerComponent(redstoneControlComponent = new RedstoneControlComponent("RedstoneControlComponent", RedstoneMode.Ignore));

		energyStorage.setMaxReceive(DEFAULT_POWER_TRANSFER);
		energyStorage.setMaxExtract(DEFAULT_POWER_TRANSFER);
	}

	/* Side Control */
	public void onSidesConfigUpdate(Direction worldSpaceSide, MachineSideMode newMode) {
		if (newMode != MachineSideMode.Input && newMode != MachineSideMode.Output && newMode != MachineSideMode.Disabled) {
			this.ioSideConfiguration.modulateWorldSpaceSideMode(worldSpaceSide, SideIncrementDirection.FORWARD);
			return;
		}
		Direction relativeSpaceSide = SideConfigurationUtilities.getDirectionFromSide(BlockSide.FRONT, getFacingDirection());
		if (disableFaceInteraction && ioSideConfiguration.getWorldSpaceDirectionConfiguration(relativeSpaceSide) != MachineSideMode.Never) {
			ioSideConfiguration.setWorldSpaceDirectionConfiguration(SideConfigurationUtilities.getDirectionFromSide(BlockSide.FRONT, getFacingDirection()), MachineSideMode.Never);
		}
		markTileEntityForSynchronization();
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

	@Override
	public ITextComponent getDisplayName() {
		if (getBlockState() != null && getBlockState().getBlock() != null) {
			return new TranslationTextComponent(getBlockState().getBlock().getTranslationKey());
		}
		return new StringTextComponent("**ERROR**");
	}
}
