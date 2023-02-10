package theking530.staticpower.blockentities.components.control.sideconfiguration;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

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

	private Map<BlockSide, MachineSideMode> configuration;
	private SideConfigurationPreset preset;

	public SideConfigurationComponent(String name, SideConfigurationPreset preset) {
		super(name);
		this.preset = preset;
		this.configuration = new HashMap<>();
		for (BlockSide side : BlockSide.values()) {
			configuration.put(side, preset.getSideDefaultMode(side));
		}
	}

	/**
	 * This method returns the configuration array in world space. Meaning each
	 * entry should map 1:1 to the values in the {@link Direction} enum.
	 * 
	 * @return
	 */
	public MachineSideMode[] getWorldSpaceConfiguration() {
		MachineSideMode[] output = new MachineSideMode[6];
		Direction facing = getBlockEntity().getFacingDirection();
		for (Direction dir : Direction.values()) {
			BlockSide side = SideConfigurationUtilities.getBlockSide(dir, facing);
			output[dir.ordinal()] = configuration.get(side);
		}

		return output;
	}

	public Map<BlockSide, MachineSideMode> getBlockSpaceConfiguration() {
		Map<BlockSide, MachineSideMode> output = new HashMap<>();
		for (BlockSide side : BlockSide.values()) {
			output.put(side, configuration.get(side));
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
		BlockSide worldSpaceSide = SideConfigurationUtilities.getBlockSide(direction, getBlockEntity().getFacingDirection());
		return getBlockSideConfiguration(worldSpaceSide);
	}

	public MachineSideMode getBlockSideConfiguration(BlockSide side) {
		return configuration.get(side);
	}

	/**
	 * Sets the side configuration for a particular block's world space side.
	 * 
	 * @param facing  The world space side of the block to change.
	 * @param newMode The new mode for that side.
	 */
	public void setWorldSpaceDirectionConfiguration(@Nonnull Direction facing, @Nonnull MachineSideMode newMode) {
		BlockSide worldSpaceSide = SideConfigurationUtilities.getBlockSide(facing, getBlockEntity().getFacingDirection());
		setBlockSpaceConfiguration(worldSpaceSide, newMode);
	}

	/**
	 * Sets the side configuration for a particular block's block side.
	 * 
	 * @param side    The block side of the block to change.
	 * @param newMode The new mode for that side.
	 */
	public boolean setBlockSpaceConfiguration(@Nonnull BlockSide side, @Nonnull MachineSideMode newMode) {
		Map<BlockSide, MachineSideMode> originalModes = getBlockSpaceConfiguration();
		if (preset.validate(side, newMode)) {
			configuration.put(side, newMode);
			preset.modifyAfterChange(side, configuration);
			onSideModeChanged(originalModes, getBlockSpaceConfiguration());
			getBlockEntity().addUpdateRequest(BlockEntityUpdateRequest.blockUpdateAndNotifyNeighborsAndRender(), true);
			return true;
		}
		return false;
	}

	public void setNewPreset(SideConfigurationPreset preset) {
		this.preset = preset;
		setToDefault();
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
		BlockSide blockSide = SideConfigurationUtilities.getBlockSide(side, getBlockEntity().getFacingDirection());

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
		} while (!setBlockSpaceConfiguration(blockSide, newMode) && newMode != originalMode);

		return newMode;
	}

	/**
	 * Resets all the block faces to the default mode that was supplied in the
	 * constructor (or Regular, if no default was supplied).
	 */
	public void setToDefault() {
		for (BlockSide side : BlockSide.values()) {
			MachineSideMode defaultPreset = preset.getSideDefaultMode(side);
			if (preset.validate(side, defaultPreset)) {
				setBlockSpaceConfiguration(side, defaultPreset);
			}
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
		for (BlockSide side : BlockSide.values()) {
			if (configuration.get(side) == mode) {
				count++;
			}
		}
		return count;
	}

	public void getModelData(ModelData.Builder builder) {
		builder.with(SIDE_CONFIG, getWorldSpaceConfiguration());
	}

	protected void onSideModeChanged(Map<BlockSide, MachineSideMode> originalModes, Map<BlockSide, MachineSideMode> newModes) {

	}

	@Override
	public CompoundTag serializeUpdateNbt(CompoundTag tag, boolean fromUpdate) {
		super.serializeUpdateNbt(tag, fromUpdate);
		for (BlockSide side : BlockSide.values()) {
			tag.putByte(side.toString(), (byte) configuration.get(side).ordinal());
		}
		return tag;
	}

	@Override
	public void deserializeUpdateNbt(CompoundTag tag, boolean fromUpdate) {
		super.deserializeUpdateNbt(tag, fromUpdate);
		for (BlockSide side : BlockSide.values()) {
			configuration.put(side, MachineSideMode.values()[tag.getByte(side.toString())]);
		}
	}

	@Override
	public String toString() {
		return "SideConfiguration [configuration=" + preset + "]";
	}
}
