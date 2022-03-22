package theking530.staticpower.tileentities.components.control.sideconfiguration;

import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import theking530.staticpower.tileentities.TileEntityBase;
import theking530.staticpower.tileentities.TileEntityUpdateRequest;
import theking530.staticpower.tileentities.components.AbstractTileEntityComponent;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;

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

	public static final DefaultSideConfiguration DEFAULT_SIDE_CONFIGURATION = new DefaultSideConfiguration();
	public static final DefaultSideConfiguration ALL_SIDES_NEVER = new DefaultSideConfiguration();
	public static final DefaultSideConfiguration ALL_SIDES_OUTPUT = new DefaultSideConfiguration();
	public static final DefaultSideConfiguration TOP_SIDE_ONLY_OUTPUT = new DefaultSideConfiguration();
	static {
		DEFAULT_SIDE_CONFIGURATION.setSide(BlockSide.TOP, true, MachineSideMode.Input);
		DEFAULT_SIDE_CONFIGURATION.setSide(BlockSide.BOTTOM, true, MachineSideMode.Output);
		DEFAULT_SIDE_CONFIGURATION.setSide(BlockSide.FRONT, true, MachineSideMode.Input);
		DEFAULT_SIDE_CONFIGURATION.setSide(BlockSide.BACK, true, MachineSideMode.Input);
		DEFAULT_SIDE_CONFIGURATION.setSide(BlockSide.LEFT, true, MachineSideMode.Input);
		DEFAULT_SIDE_CONFIGURATION.setSide(BlockSide.RIGHT, true, MachineSideMode.Input);

		ALL_SIDES_NEVER.setSide(BlockSide.TOP, false, MachineSideMode.Never);
		ALL_SIDES_NEVER.setSide(BlockSide.BOTTOM, false, MachineSideMode.Never);
		ALL_SIDES_NEVER.setSide(BlockSide.FRONT, false, MachineSideMode.Never);
		ALL_SIDES_NEVER.setSide(BlockSide.BACK, false, MachineSideMode.Never);
		ALL_SIDES_NEVER.setSide(BlockSide.LEFT, false, MachineSideMode.Never);
		ALL_SIDES_NEVER.setSide(BlockSide.RIGHT, false, MachineSideMode.Never);
		
		ALL_SIDES_OUTPUT.setSide(BlockSide.TOP, true, MachineSideMode.Output);
		ALL_SIDES_OUTPUT.setSide(BlockSide.BOTTOM, true, MachineSideMode.Output);
		ALL_SIDES_OUTPUT.setSide(BlockSide.FRONT, true, MachineSideMode.Output);
		ALL_SIDES_OUTPUT.setSide(BlockSide.BACK, true, MachineSideMode.Output);
		ALL_SIDES_OUTPUT.setSide(BlockSide.LEFT, true, MachineSideMode.Output);
		ALL_SIDES_OUTPUT.setSide(BlockSide.RIGHT, true, MachineSideMode.Output);
		
		TOP_SIDE_ONLY_OUTPUT.setSide(BlockSide.TOP, true, MachineSideMode.Output);
		TOP_SIDE_ONLY_OUTPUT.setSide(BlockSide.BOTTOM, false, MachineSideMode.Never);
		TOP_SIDE_ONLY_OUTPUT.setSide(BlockSide.FRONT, false, MachineSideMode.Never);
		TOP_SIDE_ONLY_OUTPUT.setSide(BlockSide.BACK, false, MachineSideMode.Never);
		TOP_SIDE_ONLY_OUTPUT.setSide(BlockSide.LEFT, false, MachineSideMode.Never);
		TOP_SIDE_ONLY_OUTPUT.setSide(BlockSide.RIGHT, false, MachineSideMode.Never);
	}

	private MachineSideMode[] configuration;
	private boolean[] enabledState;
	private DefaultSideConfiguration defaultConfiguration;
	private final BiConsumer<BlockSide, MachineSideMode> callback;
	private final BiPredicate<BlockSide, MachineSideMode> sideModeFilter;


	public SideConfigurationComponent(String name, BiConsumer<BlockSide, MachineSideMode> onConfigurationChangedCallback, BiPredicate<BlockSide, MachineSideMode> sideModeFilter) {
		this(name, onConfigurationChangedCallback, sideModeFilter, DEFAULT_SIDE_CONFIGURATION);
	}

	public SideConfigurationComponent(String name, BiConsumer<BlockSide, MachineSideMode> onConfigurationChangedCallback, BiPredicate<BlockSide, MachineSideMode> sideModeFilter,
			DefaultSideConfiguration defaultConfiguration) {
		super(name);
		this.callback = onConfigurationChangedCallback;
		this.sideModeFilter = sideModeFilter;
		this.defaultConfiguration = defaultConfiguration;
		configuration = new MachineSideMode[] { MachineSideMode.Never, MachineSideMode.Never, MachineSideMode.Never, MachineSideMode.Never, MachineSideMode.Never, MachineSideMode.Never };
		enabledState = new boolean[] { false, false, false, false, false, false };
	}

	@Override
	public void onRegistered(TileEntityBase owner) {
		super.onRegistered(owner);

	}

	@Override
	public void onInitializedInWorld(Level world, BlockPos pos, boolean firstTimePlaced) {
		if (firstTimePlaced) {
			setToDefault(true);
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

		Direction facing = getOwningTileEntityFacing();
		for (Direction dir : Direction.values()) {
			BlockSide side = SideConfigurationUtilities.getBlockSide(dir, facing);
			output[dir.ordinal()] = configuration[side.ordinal()];
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
		BlockSide worldSpaceSide = SideConfigurationUtilities.getBlockSide(direction, getOwningTileEntityFacing());
		return getBlockSideConfiguration(worldSpaceSide);
	}

	public MachineSideMode getBlockSideConfiguration(BlockSide side) {
		return configuration[side.ordinal()];
	}

	public SideConfigurationComponent setWorldSpaceEnabledState(@Nonnull Direction direction, boolean enabled) {
		BlockSide worldSpaceSide = SideConfigurationUtilities.getBlockSide(direction, getOwningTileEntityFacing());
		setBlockSideEnabledState(worldSpaceSide, enabled);
		return this;
	}

	public boolean getWorldSpaceEnabledState(@Nonnull Direction direction) {
		BlockSide worldSpaceSide = SideConfigurationUtilities.getBlockSide(direction, getOwningTileEntityFacing());
		return getBlockSideEnabledState(worldSpaceSide);
	}

	public SideConfigurationComponent setBlockSideEnabledState(@Nonnull BlockSide side, boolean enabled) {
		enabledState[side.ordinal()] = enabled;
		return this;
	}

	public boolean getBlockSideEnabledState(@Nonnull BlockSide side) {
		return enabledState[side.ordinal()];
	}

	/**
	 * Sets the side configuration for a particular block's world space side.
	 * 
	 * @param facing  The world space side of the block to change.
	 * @param newMode The new mode for that side.
	 */
	public void setWorldSpaceDirectionConfiguration(@Nonnull Direction facing, @Nonnull MachineSideMode newMode) {
		BlockSide worldSpaceSide = SideConfigurationUtilities.getBlockSide(facing, getOwningTileEntityFacing());
		setBlockSpaceConfiguration(worldSpaceSide, newMode);
	}

	/**
	 * Sets the side configuration for a particular block's block side.
	 * 
	 * @param side    The block side of the block to change.
	 * @param newMode The new mode for that side.
	 */
	public void setBlockSpaceConfiguration(@Nonnull BlockSide side, @Nonnull MachineSideMode newMode) {
		configuration[side.ordinal()] = newMode;
		getTileEntity().addUpdateRequest(TileEntityUpdateRequest.blockUpdateAndNotifyNeighborsAndRender(), true);
		callback.accept(side, newMode);
	}

	public void setDefaultConfiguration(DefaultSideConfiguration configuration) {
		defaultConfiguration = configuration;
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
		BlockSide worldSpaceSide = SideConfigurationUtilities.getBlockSide(side, getOwningTileEntityFacing());

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
			configuration[worldSpaceSide.ordinal()] = newMode;
		} while (!sideModeFilter.test(worldSpaceSide, newMode) && newMode != originalMode);

		getTileEntity().addUpdateRequest(TileEntityUpdateRequest.blockUpdateAndNotifyNeighborsAndRender(), true);

		// Finally, raise the changed callback.
		callback.accept(worldSpaceSide, newMode);

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
			configuration[side.ordinal()] = defaultConfiguration.getSideDefaultMode(side);
			enabledState[side.ordinal()] = defaultConfiguration.getSideDefaultEnabled(side);
			if (!suppressEvent) {
				callback.accept(side, configuration[side.ordinal()]);
			}
		}

		// If we're not suspending the event, perform a block update.
		if (!suppressEvent) {
			getTileEntity().addUpdateRequest(TileEntityUpdateRequest.blockUpdateAndNotifyNeighborsAndRender(), true);
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
	public CompoundTag serializeUpdateNbt(CompoundTag nbt, boolean fromUpdate) {
		super.serializeUpdateNbt(nbt, fromUpdate);
		for (int i = 0; i < 6; i++) {
			nbt.putInt("mode" + i, configuration[i].ordinal());
		}
		for (int i = 0; i < 6; i++) {
			nbt.putBoolean("enabled" + i, enabledState[i]);
		}
		return nbt;
	}

	@Override
	public void deserializeUpdateNbt(CompoundTag nbt, boolean fromUpdate) {
		super.deserializeUpdateNbt(nbt, fromUpdate);
		for (int i = 0; i < 6; i++) {
			configuration[i] = MachineSideMode.values()[nbt.getInt("mode" + i)];
		}
		for (int i = 0; i < 6; i++) {
			enabledState[i] = nbt.getBoolean("enabled" + i);
		}
	}

	@Override
	public String toString() {
		return "SideConfiguration [configuration=" + Arrays.toString(configuration) + ", " + "enabledState=" + Arrays.toString(enabledState) + "]";
	}
}
