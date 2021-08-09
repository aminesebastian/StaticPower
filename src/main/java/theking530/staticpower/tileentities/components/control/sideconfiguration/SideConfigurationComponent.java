package theking530.staticpower.tileentities.components.control.sideconfiguration;

import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

import javax.annotation.Nonnull;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import theking530.staticpower.blocks.tileentity.StaticPowerTileEntityBlock;
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
	static {
		DEFAULT_SIDE_CONFIGURATION.setSide(BlockSide.TOP, true, MachineSideMode.Input);
		DEFAULT_SIDE_CONFIGURATION.setSide(BlockSide.BOTTOM, true, MachineSideMode.Output);
		DEFAULT_SIDE_CONFIGURATION.setSide(BlockSide.FRONT, true, MachineSideMode.Input);
		DEFAULT_SIDE_CONFIGURATION.setSide(BlockSide.BACK, true, MachineSideMode.Input);
		DEFAULT_SIDE_CONFIGURATION.setSide(BlockSide.LEFT, true, MachineSideMode.Input);
		DEFAULT_SIDE_CONFIGURATION.setSide(BlockSide.RIGHT, true, MachineSideMode.Input);
	}

	private MachineSideMode[] configuration;
	private boolean[] enabledState;
	private DefaultSideConfiguration defaultConfiguration;
	private final BiConsumer<Direction, MachineSideMode> callback;
	private final BiPredicate<Direction, MachineSideMode> sideModeFilter;

	public SideConfigurationComponent(String name, MachineSideMode[] defaultConfiguration) {
		this(name, (dir, side) -> {
		}, (dir, side) -> true, DEFAULT_SIDE_CONFIGURATION);
	}

	public SideConfigurationComponent(String name, BiConsumer<Direction, MachineSideMode> onConfigurationChangedCallback, BiPredicate<Direction, MachineSideMode> sideModeFilter) {
		this(name, onConfigurationChangedCallback, sideModeFilter, DEFAULT_SIDE_CONFIGURATION);
	}

	public SideConfigurationComponent(String name, BiConsumer<Direction, MachineSideMode> onConfigurationChangedCallback, BiPredicate<Direction, MachineSideMode> sideModeFilter,
			DefaultSideConfiguration defaultConfiguration) {
		super(name);
		this.callback = onConfigurationChangedCallback;
		this.sideModeFilter = sideModeFilter;
		this.defaultConfiguration = defaultConfiguration;
		configuration = new MachineSideMode[] { MachineSideMode.Never, MachineSideMode.Never, MachineSideMode.Never, MachineSideMode.Never, MachineSideMode.Never, MachineSideMode.Never };
		enabledState = new boolean[] { false, false, false, false, false, false };
	}

	@Override
	public void onInitializedInWorld(World world, BlockPos pos, boolean firstTimePlaced) {
		super.onInitializedInWorld(world, pos, firstTimePlaced);
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
		return configuration;
	}

	/**
	 * Gets the side for a particular block side given the block's facing direction.
	 * 
	 * @param facing The world space side of the block to get.
	 * @return The mode that block side is set to.
	 */
	public MachineSideMode getWorldSpaceDirectionConfiguration(@Nonnull Direction facing) {
		if (facing == null) {
			return MachineSideMode.Disabled;
		}
		return configuration[facing.ordinal()];
	}

	public boolean getWorldSpaceEnabledState(@Nonnull Direction direction) {
		return enabledState[direction.ordinal()];
	}

	public boolean getBlockSideEnabledState(@Nonnull BlockSide side) {
		// Get the block state and update the configuration.
		BlockState currentBlockState = getTileEntity().getBlockState();
		if (currentBlockState.hasProperty(StaticPowerTileEntityBlock.FACING)) {
			Direction currentFacingDirection = currentBlockState.get(StaticPowerTileEntityBlock.FACING);
			Direction worldSpaceSide = SideConfigurationUtilities.getDirectionFromSide(side, currentFacingDirection);
			return getWorldSpaceEnabledState(worldSpaceSide);
		}
		return false;
	}

	public SideConfigurationComponent setWorldSpaceEnabledState(@Nonnull Direction direction, boolean enabled) {
		enabledState[direction.ordinal()] = enabled;
		return this;
	}

	public SideConfigurationComponent setBlockSideEnabledState(@Nonnull BlockSide side, boolean enabled) {
		// Get the block state and update the configuration.
		BlockState currentBlockState = getTileEntity().getBlockState();
		if (currentBlockState.hasProperty(StaticPowerTileEntityBlock.FACING)) {
			Direction currentFacingDirection = currentBlockState.get(StaticPowerTileEntityBlock.FACING);
			Direction worldSpaceSide = SideConfigurationUtilities.getDirectionFromSide(side, currentFacingDirection);
			setWorldSpaceEnabledState(worldSpaceSide, enabled);
		}
		return this;
	}

	/**
	 * Sets the side configuration for a particular block's world space side.
	 * 
	 * @param facing  The world space side of the block to change.
	 * @param newMode The new mode for that side.
	 */
	public void setWorldSpaceDirectionConfiguration(@Nonnull Direction facing, @Nonnull MachineSideMode newMode) {
		configuration[facing.ordinal()] = newMode;
		getTileEntity().addUpdateRequest(TileEntityUpdateRequest.blockUpdateAndNotifyNeighborsAndRender(), true);
		callback.accept(facing, newMode);
	}

	/**
	 * Sets the side configuration for a particular block's block side.
	 * 
	 * @param side    The block side of the block to change.
	 * @param newMode The new mode for that side.
	 */
	public void setBlockSpaceConfiguration(@Nonnull BlockSide side, @Nonnull MachineSideMode newMode) {
		// Get the block state and update the configuration.
		BlockState currentBlockState = getTileEntity().getBlockState();
		if (currentBlockState.hasProperty(StaticPowerTileEntityBlock.FACING)) {
			Direction currentFacingDirection = currentBlockState.get(StaticPowerTileEntityBlock.FACING);
			Direction worldSpaceSide = SideConfigurationUtilities.getDirectionFromSide(side, currentFacingDirection);
			setWorldSpaceDirectionConfiguration(worldSpaceSide, newMode);
		}
	}

	public void setWorldSpaceConfiguration(@Nonnull MachineSideMode... modes) {
		if (modes.length != 6) {
			throw new RuntimeException("Attempted to update the world space side configuration with an array that was not of length 6.");
		}
		configuration = modes;
		getTileEntity().addUpdateRequest(TileEntityUpdateRequest.blockUpdateAndNotifyNeighborsAndRender(), true);
		callback.accept(null, null);
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
			configuration[side.ordinal()] = newMode;
		} while (!sideModeFilter.test(side, newMode) && newMode != originalMode);

		getTileEntity().addUpdateRequest(TileEntityUpdateRequest.blockUpdateAndNotifyNeighborsAndRender(), true);

		// Finally, raise the changed callback.
		callback.accept(side, newMode);

		return newMode;
	}

	/**
	 * Resets all the block faces to the default mode that was supplied in the
	 * constructor (or Regular, if no default was supplied).
	 */
	public void setToDefault() {
		setToDefault(false);
	}

	@Override
	public void onNeighborReplaced(BlockState state, Direction direction, BlockState facingState, BlockPos FacingPos) {
		//setToDefault(true);
	}

	private void setToDefault(boolean suppressEvent) {
		// Get the block state and update the configuration.
		BlockState currentBlockState = getTileEntity().getBlockState();
		if (currentBlockState.hasProperty(StaticPowerTileEntityBlock.FACING)) {
			Direction currentFacingDirection = currentBlockState.get(StaticPowerTileEntityBlock.FACING);
			for (Direction dir : Direction.values()) {
				BlockSide side = SideConfigurationUtilities.getBlockSide(dir, currentFacingDirection);
				configuration[dir.ordinal()] = defaultConfiguration.getSideDefaultMode(side);
				enabledState[dir.ordinal()] = defaultConfiguration.getSideDefaultEnabled(side);
				if (!suppressEvent) {
					callback.accept(dir, configuration[side.ordinal()]);
				}
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
	public CompoundNBT serializeUpdateNbt(CompoundNBT nbt, boolean fromUpdate) {
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
	public void deserializeUpdateNbt(CompoundNBT nbt, boolean fromUpdate) {
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
