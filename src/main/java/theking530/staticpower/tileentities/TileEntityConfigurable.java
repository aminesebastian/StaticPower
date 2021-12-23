package theking530.staticpower.tileentities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import theking530.staticcore.initialization.tileentity.BlockEntityTypeAllocator;
import theking530.staticpower.tileentities.components.control.RedstoneControlComponent;
import theking530.staticpower.tileentities.components.control.redstonecontrol.RedstoneMode;
import theking530.staticpower.tileentities.components.control.sideconfiguration.DefaultSideConfiguration;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationUtilities;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.tileentities.components.items.CompoundInventoryComponent;
import theking530.staticpower.tileentities.components.items.InventoryComponent;
import theking530.staticpower.tileentities.components.serialization.SaveSerialize;

public class TileEntityConfigurable extends TileEntityBase {
	public static final DefaultSideConfiguration DEFAULT_NO_FACE_SIDE_CONFIGURATION = new DefaultSideConfiguration();
	static {
		DEFAULT_NO_FACE_SIDE_CONFIGURATION.setSide(BlockSide.TOP, true, MachineSideMode.Input);
		DEFAULT_NO_FACE_SIDE_CONFIGURATION.setSide(BlockSide.BOTTOM, true, MachineSideMode.Output);
		DEFAULT_NO_FACE_SIDE_CONFIGURATION.setSide(BlockSide.FRONT, false, MachineSideMode.Never);
		DEFAULT_NO_FACE_SIDE_CONFIGURATION.setSide(BlockSide.BACK, true, MachineSideMode.Output);
		DEFAULT_NO_FACE_SIDE_CONFIGURATION.setSide(BlockSide.LEFT, true, MachineSideMode.Input);
		DEFAULT_NO_FACE_SIDE_CONFIGURATION.setSide(BlockSide.RIGHT, true, MachineSideMode.Output);
	}

	public final SideConfigurationComponent ioSideConfiguration;
	public final RedstoneControlComponent redstoneControlComponent;
	/**
	 * Indicates whether or not face interaction should be disabled on this tile
	 * entity.
	 */
	@SaveSerialize
	private boolean disableFaceInteraction;

	public TileEntityConfigurable(BlockEntityTypeAllocator<? extends TileEntityConfigurable> allocator) {
		super(allocator);
		disableFaceInteraction();
		registerComponent(ioSideConfiguration = new SideConfigurationComponent("SideConfiguration", this::onSidesConfigUpdate, this::checkSideConfiguration, getDefaultSideConfiguration()));
		registerComponent(redstoneControlComponent = new RedstoneControlComponent("RedstoneControlComponent", RedstoneMode.Ignore));
	}

	@Override
	protected void postInit(Level world, BlockPos pos, BlockState state) {
		// Get all inventories for this tile entitiy.
		List<InventoryComponent> inventories = getComponents(InventoryComponent.class);

		// Declare a map to contain all the organized inventories.
		Map<MachineSideMode, List<InventoryComponent>> modeList = new HashMap<MachineSideMode, List<InventoryComponent>>();

		// Create the three compound inventory categories.
		modeList.put(MachineSideMode.Input, new ArrayList<InventoryComponent>());
		modeList.put(MachineSideMode.Output, new ArrayList<InventoryComponent>());
		modeList.put(MachineSideMode.Regular, new ArrayList<InventoryComponent>());

		// Iterate through all the side modes and organize the inventories.
		for (InventoryComponent inv : inventories) {
			// Populate the input and output lists.
			if (inv.getMode().isInputMode()) {
				modeList.get(MachineSideMode.Input).add(inv);
			} else if (inv.getMode().isOutputMode()) {
				modeList.get(MachineSideMode.Output).add(inv);
			}

			// Add all non disabled modes to the regular list.
			if (!inv.getMode().isDisabledMode()) {
				modeList.get(MachineSideMode.Regular).add(inv);
			}
		}

		// Iterate through all the organized side modes and create compounds for the
		// ones with values.
		for (MachineSideMode mode : modeList.keySet()) {
			// Get all the inventories for that side mode.
			List<InventoryComponent> modeInvs = modeList.get(mode);
			// Skip empty lists or lists with just one value.
			if (modeInvs.size() <= 1) {
				continue;
			}

			// Create a compount inventory and register it for that side mode.
			registerComponentOverride(new CompoundInventoryComponent("CompoundInventory" + mode, mode, modeInvs));
		}
	}

	/* Side Control */
	protected void onSidesConfigUpdate(Direction worldSpaceSide, MachineSideMode newMode) {
		Direction relativeSpaceSide = SideConfigurationUtilities.getDirectionFromSide(BlockSide.FRONT, getFacingDirection());
		if (isFaceInteractionDisabled() && ioSideConfiguration.getWorldSpaceDirectionConfiguration(relativeSpaceSide) != MachineSideMode.Never) {
			ioSideConfiguration.setWorldSpaceDirectionConfiguration(SideConfigurationUtilities.getDirectionFromSide(BlockSide.FRONT, getFacingDirection()), MachineSideMode.Never);
		}
	}

	protected boolean isValidSideConfiguration(BlockSide side, MachineSideMode mode) {
		return mode == MachineSideMode.Disabled || mode == MachineSideMode.Regular || mode == MachineSideMode.Output || mode == MachineSideMode.Input;
	}

	private boolean checkSideConfiguration(Direction direction, MachineSideMode mode) {
		return isValidSideConfiguration(SideConfigurationUtilities.getBlockSide(direction, getFacingDirection()), mode);
	}

	/**
	 * Disables capability interaction with the face of this block.
	 */
	public void disableFaceInteraction() {
		// Setting this value will ensure the default side configuration returned by
		// this machine will have the face interaction disabled.
		disableFaceInteraction = true;

		// If the post init has run, then update the enabled state in the world.
		if (hasPostInitRun()) {
			ioSideConfiguration.setBlockSideEnabledState(BlockSide.FRONT, false);
		}
	}

	/**
	 * Enables capability interaction with the face of this block.
	 */
	public void enableFaceInteraction() {
		// Setting this value will ensure the default side configuration returned by
		// this machine will have the face interaction enabled.
		disableFaceInteraction = false;

		// If the post init has run, then update the enabled state in the world.
		if (hasPostInitRun()) {
			ioSideConfiguration.setBlockSideEnabledState(BlockSide.FRONT, true);
		}
	}

	/**
	 * Checks to see if face interaction is disabled.
	 * 
	 * @return
	 */
	public boolean isFaceInteractionDisabled() {
		return disableFaceInteraction;
	}

	@Override
	public boolean shouldSerializeWhenBroken() {
		return true;
	}

	@Override
	public boolean shouldDeserializeWhenPlaced(CompoundTag nbt, Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		return true;
	}

	protected DefaultSideConfiguration getDefaultSideConfiguration() {
		if (disableFaceInteraction) {
			return DEFAULT_NO_FACE_SIDE_CONFIGURATION;
		} else {
			return SideConfigurationComponent.DEFAULT_SIDE_CONFIGURATION;
		}
	}
}
