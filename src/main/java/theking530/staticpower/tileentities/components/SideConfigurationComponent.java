package theking530.staticpower.tileentities.components;

import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

import javax.annotation.Nonnull;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import theking530.staticpower.tileentities.utilities.MachineSideMode;

/**
 * Tile entity component responsible for provided side configuration.
 * 
 * @author Amine Sebastian
 *
 */
public class SideConfigurationComponent extends AbstractTileEntityComponent {
	public enum SideIncrementDirection {
		FORWARD, BACKWARDS;
	}

	private final MachineSideMode[] configuration;
	private final MachineSideMode[] defaultConfiguration;
	private final BiConsumer<Direction, MachineSideMode> callback;
	private final BiPredicate<Direction, MachineSideMode> sideModeFilter;

	public SideConfigurationComponent(String name, BiConsumer<Direction, MachineSideMode> onConfigurationChangedCallback, BiPredicate<Direction, MachineSideMode> sideModeFilter) {
		this(name, onConfigurationChangedCallback, sideModeFilter,
				new MachineSideMode[] { MachineSideMode.Regular, MachineSideMode.Regular, MachineSideMode.Regular, MachineSideMode.Regular, MachineSideMode.Regular, MachineSideMode.Regular });
	}

	public SideConfigurationComponent(String name, BiConsumer<Direction, MachineSideMode> onConfigurationChangedCallback, BiPredicate<Direction, MachineSideMode> sideModeFilter,
			MachineSideMode[] defaultConfiguration) {
		super(name);
		this.callback = onConfigurationChangedCallback;
		this.sideModeFilter = sideModeFilter;
		this.defaultConfiguration = defaultConfiguration;
		configuration = new MachineSideMode[6];

		// Initialize to the defaults but supress the event as we are still in the
		// constructor and don't know if the creator is in a good state to receive the
		// event.
		setToDefault(true);
	}

	/**
	 * This method returns the configuration array in world space. Meaning each
	 * entry should map 1:1 to the values in the {@link Direction} enum.
	 * 
	 * @return
	 */
	public MachineSideMode[] getWorldSpaceConfiguration() {
		return configuration;
	}

	/**
	 * Gets the side for a particular block side given the block's facing direction.
	 * 
	 * @param facing The world space side of the block to get.
	 * @return The mode that block side is set to.
	 */
	public MachineSideMode getWorldSpaceDirectionConfiguration(@Nonnull Direction facing) {
		return configuration[facing.ordinal()];
	}

	/**
	 * Sets the side configuration for a particular block's world space side.
	 * 
	 * @param facing  The world space side of the block to change.
	 * @param newMode The new mode for that side.
	 */
	public void setWorldSpaceDirectionConfiguration(@Nonnull Direction facing, @Nonnull MachineSideMode newMode) {
		configuration[facing.ordinal()] = newMode;
		callback.accept(facing, newMode);
		getTileEntity().markTileEntityForSynchronization();
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

		MachineSideMode newMode;
		// Loop until we hit an acceptable side mode.
		do {
			currentModeIndex = (currentModeIndex + 1) % (MachineSideMode.values().length);
			newMode = MachineSideMode.values()[currentModeIndex];
			configuration[side.ordinal()] = newMode;
		} while (!sideModeFilter.test(side, newMode) && newMode != originalMode);

		// Finally, raise the changed callback.
		callback.accept(side, newMode);
		getTileEntity().markTileEntityForSynchronization();
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
		for (int i = 0; i < configuration.length; i++) {
			configuration[i] = defaultConfiguration[i];
			if (!suppressEvent) {
				callback.accept(Direction.values()[i], defaultConfiguration[i]);
				getTileEntity().markTileEntityForSynchronization();
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
		for (MachineSideMode sideMode : configuration) {
			if (sideMode == mode) {
				count++;
			}
		}
		return count;
	}

	@Override
	public CompoundNBT serializeUpdateNbt(CompoundNBT nbt) {
		for (int i = 0; i < 6; i++) {
			nbt.putInt("SIDEMODE" + i, configuration[i].ordinal());
		}
		return nbt;
	}

	@Override
	public void deserializeUpdateNbt(CompoundNBT nbt) {
		for (int i = 0; i < 6; i++) {
			configuration[i] = MachineSideMode.values()[nbt.getInt("SIDEMODE" + i)];
		}
	}

	@Override
	public String toString() {
		return "SideConfiguration [configuration=" + Arrays.toString(configuration) + "]";
	}
}
