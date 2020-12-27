package theking530.staticpower.tileentities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import theking530.staticcore.initialization.tileentity.TileEntityTypeAllocator;
import theking530.staticpower.tileentities.components.control.RedstoneControlComponent;
import theking530.staticpower.tileentities.components.control.redstonecontrol.RedstoneMode;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationUtilities;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.tileentities.components.items.CompoundInventoryComponent;
import theking530.staticpower.tileentities.components.items.InventoryComponent;

public class TileEntityConfigurable extends TileEntityBase {
	public final SideConfigurationComponent ioSideConfiguration;
	public final RedstoneControlComponent redstoneControlComponent;

	public TileEntityConfigurable(TileEntityTypeAllocator<? extends TileEntityConfigurable> allocator) {
		super(allocator);
		registerComponent(ioSideConfiguration = new SideConfigurationComponent("SideConfiguration", this::onSidesConfigUpdate, this::checkSideConfiguration, getDefaultSideConfiguration()));
		registerComponent(redstoneControlComponent = new RedstoneControlComponent("RedstoneControlComponent", RedstoneMode.Ignore));
	}

	@Override
	protected void postInit() {
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

	@Override
	public boolean shouldSerializeWhenBroken() {
		return true;
	}

	@Override
	public boolean shouldDeserializeWhenPlaced(CompoundNBT nbt, World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		return true;
	}

	protected MachineSideMode[] getDefaultSideConfiguration() {
		return new MachineSideMode[] { MachineSideMode.Input, MachineSideMode.Input, MachineSideMode.Output, MachineSideMode.Output, MachineSideMode.Output, MachineSideMode.Output };
	}
}
