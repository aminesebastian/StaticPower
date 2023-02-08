package theking530.staticpower.blockentities.components.control.sideconfiguration;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import com.google.common.base.Joiner;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import theking530.staticpower.blockentities.BlockEntityUpdateRequest;
import theking530.staticpower.blockentities.components.AbstractBlockEntityComponent;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;

/**
 * Tile entity component responsible for provided side configuration.
 * 
 * @author Amine Sebastian
 *
 */
public class SideConfigurationComponent extends AbstractBlockEntityComponent {
	public enum SideIncrementDirection {
		FORWARD, BACKWARDS;
	}

	public static final ModelProperty<MachineSideMode[]> SIDE_CONFIG = new ModelProperty<>();

	private Map<BlockSide, SideConfiguration> configuration;
	private SideConfigurationPreset configurationPreset;

	public SideConfigurationComponent(String name, SideConfigurationPreset preset) {
		super(name);
		this.configurationPreset = preset;

		configuration = new HashMap<>();
		for (BlockSide side : BlockSide.values()) {
			configuration.put(side, new SideConfiguration(side, configurationPreset.getSideDefaultMode(side), configurationPreset.getSideDefaultEnabled(side)));
		}
	}

	/**
	 * This method returns the configuration array in world space. Meaning each
	 * entry should map 1:1 to the values in the {@link Direction} enum.
	 * 
	 * @return
	 */
	public MachineSideMode[] getWorldSpaceConfiguration() {
		// TODO: Cache this.
		MachineSideMode[] output = new MachineSideMode[6];

		Direction facing = getTileEntity().getFacingDirection();
		for (Direction dir : Direction.values()) {
			BlockSide side = SideConfigurationUtilities.getBlockSide(dir, facing);
			output[dir.ordinal()] = configuration.get(side).getMode();
		}

		return output;
	}

	/**
	 * Gets the side for a particular block side given the block's facing direction.
	 * 
	 * @param facing The world space side of the block to get.
	 * @return The mode that block side is set to.
	 */
	public MachineSideMode getWorldSpaceDirectionConfiguration(@Nonnull Direction direction) {
		BlockSide worldSpaceSide = SideConfigurationUtilities.getBlockSide(direction, getTileEntity().getFacingDirection());
		return getBlockSideConfiguration(worldSpaceSide);
	}

	public MachineSideMode getBlockSideConfiguration(BlockSide side) {
		return configuration.get(side).getMode();
	}

	public SideConfigurationComponent setWorldSpaceEnabledState(@Nonnull Direction direction, boolean enabled) {
		BlockSide worldSpaceSide = SideConfigurationUtilities.getBlockSide(direction, getTileEntity().getFacingDirection());
		setBlockSideEnabledState(worldSpaceSide, enabled);
		return this;
	}

	public boolean getWorldSpaceEnabledState(@Nonnull Direction direction) {
		BlockSide worldSpaceSide = SideConfigurationUtilities.getBlockSide(direction, getTileEntity().getFacingDirection());
		return getBlockSideEnabledState(worldSpaceSide);
	}

	public SideConfigurationComponent setBlockSideEnabledState(@Nonnull BlockSide side, boolean enabled) {
		configuration.get(side).setEnabled(enabled);
		return this;
	}

	public boolean getBlockSideEnabledState(@Nonnull BlockSide side) {
		return configuration.get(side).isEnabled();
	}

	/**
	 * Sets the side configuration for a particular block's world space side.
	 * 
	 * @param facing  The world space side of the block to change.
	 * @param newMode The new mode for that side.
	 */
	public void setWorldSpaceDirectionConfiguration(@Nonnull Direction facing, @Nonnull MachineSideMode newMode) {
		BlockSide worldSpaceSide = SideConfigurationUtilities.getBlockSide(facing, getTileEntity().getFacingDirection());
		setBlockSpaceConfiguration(worldSpaceSide, newMode);
	}

	/**
	 * Sets the side configuration for a particular block's block side.
	 * 
	 * @param side    The block side of the block to change.
	 * @param newMode The new mode for that side.
	 */
	public void setBlockSpaceConfiguration(@Nonnull BlockSide side, @Nonnull MachineSideMode newMode) {
		if (!isValidConfiguration(side, newMode)) {
			return;
		}

		configuration.get(side).setMode(newMode);
		getTileEntity().addUpdateRequest(BlockEntityUpdateRequest.blockUpdateAndNotifyNeighborsAndRender(), true);
		sideChanged(side);
	}

	public void setPreset(SideConfigurationPreset configuration, boolean setToDefault) {
		configurationPreset = configuration;
		if (setToDefault) {
			setToDefault();
		}
	}

	/**
	 * Increments the provided world space side in the direction of the requested
	 * direction.
	 * 
	 * @param side      The world space side of the block to increment.
	 * @param direction The direction to change the side configuration to.
	 * @return The resultant new mode. This is useful if the user wants to only
	 *         allow certain sides to be incremented to. If this returns a mode that
	 *         is not useful, call it again until you hit a useful mode.
	 */
	public MachineSideMode modulateWorldSpaceSideMode(Direction side, SideIncrementDirection direction) {
		int currentModeIndex = getWorldSpaceDirectionConfiguration(side).ordinal();
		// Capture the original side mode.
		MachineSideMode originalMode = getWorldSpaceDirectionConfiguration(side);
		BlockSide blockSide = SideConfigurationUtilities.getBlockSide(side, getTileEntity().getFacingDirection());

		MachineSideMode newMode;
		// Loop until we hit an acceptable side mode.
		do {
			int incrementAmount = direction == SideIncrementDirection.FORWARD ? 1 : -1;
			currentModeIndex = currentModeIndex + incrementAmount;

			// Handle the cases where we go out of range.
			if (currentModeIndex < 0) {
				currentModeIndex = MachineSideMode.values().length - 1;
			} else if (currentModeIndex > MachineSideMode.values().length - 1) {
				currentModeIndex = 0;
			}

			// Set the new mode.
			newMode = MachineSideMode.values()[currentModeIndex];
			configuration.get(blockSide).setMode(newMode);
		} while (!isValidConfiguration(blockSide, newMode) && newMode != originalMode);

		getTileEntity().addUpdateRequest(BlockEntityUpdateRequest.blockUpdateAndNotifyNeighborsAndRender(), true);

		// Finally, raise the changed callback.
		sideChanged(blockSide);

		return newMode;
	}

	/**
	 * Resets all the block faces to the default mode that was supplied in the
	 * constructor (or Regular, if no default was supplied).
	 */
	public void setToDefault() {
		setToDefault(false);
	}

	private void setToDefault(boolean suppressEvent) {
		// Get the block state and update the configuration.
		for (BlockSide side : BlockSide.values()) {
			configuration.get(side).setMode(configurationPreset.getSideDefaultMode(side));
			configuration.get(side).setEnabled(configurationPreset.getSideDefaultEnabled(side));
			if (!suppressEvent) {
				sideChanged(side);
			}
		}

		// If we're not suspending the event, perform a block update.
		if (!suppressEvent) {
			getTileEntity().addUpdateRequest(BlockEntityUpdateRequest.blockUpdateAndNotifyNeighborsAndRender(), true);
		}
	}

	/**
	 * Captures the number of sides set to the provided mode. Useful to check if
	 * there are any sides of the provided type by checking if the return value is >
	 * 0.
	 * 
	 * @param mode The {@link MachineSideMode} to check for.
	 * @return
	 */
	public int getCountOfSidesWithMode(MachineSideMode mode) {
		int count = 0;
		for (SideConfiguration config : configuration.values()) {
			if (config.getMode() == mode) {
				count++;
			}
		}
		return count;
	}

	public void getModelData(ModelData.Builder builder) {
		builder.with(SIDE_CONFIG, getWorldSpaceConfiguration());
	}

	@Override
	public CompoundTag serializeUpdateNbt(CompoundTag nbt, boolean fromUpdate) {
		super.serializeUpdateNbt(nbt, fromUpdate);
		for (BlockSide side : BlockSide.values()) {
			nbt.put(side.toString(), configuration.get(side).serialize());
		}
		return nbt;
	}

	@Override
	public void deserializeUpdateNbt(CompoundTag nbt, boolean fromUpdate) {
		super.deserializeUpdateNbt(nbt, fromUpdate);
		for (BlockSide side : BlockSide.values()) {
			configuration.get(side).deserialize(nbt.getCompound(side.toString()));
		}
	}

	@Override
	public String toString() {
		return "SideConfiguration [configuration=" + Joiner.on(",").withKeyValueSeparator("=").join(configuration) + "]";
	}

	public boolean isValidConfiguration(BlockSide side, MachineSideMode mode) {
		return configurationPreset.validate(side, new SideConfiguration(side, mode, getBlockSideEnabledState(side)));
	}

	private void sideChanged(BlockSide side) {
		if (configurationPreset != null) {
			configurationPreset.modifyAfterChange(this);
		}
	}
}
