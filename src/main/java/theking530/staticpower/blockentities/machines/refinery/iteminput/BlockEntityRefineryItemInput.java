package theking530.staticpower.blockentities.machines.refinery.iteminput;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationPreset;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationPresets;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.blockentities.machines.refinery.BaseRefineryBlockEntity;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.init.ModBlocks;

public class BlockEntityRefineryItemInput extends BaseRefineryBlockEntity {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityRefineryItemInput> TYPE = new BlockEntityTypeAllocator<BlockEntityRefineryItemInput>("refinery_item_input",
			(type, pos, state) -> new BlockEntityRefineryItemInput(pos, state), ModBlocks.RefineryItemInput);

	public BlockEntityRefineryItemInput(BlockPos pos, BlockState state) {
		super(TYPE, pos, state, StaticPowerTiers.ADVANCED);
		enableFaceInteraction();
	}

	@Override
	public void process() {

	}

	@Override
	protected boolean isValidSideConfiguration(BlockSide side, MachineSideMode mode) {
		return mode == MachineSideMode.Disabled || mode == MachineSideMode.Input;
	}

	@Override
	protected SideConfigurationPreset getDefaultSideConfiguration() {
		return SideConfigurationPresets.ALL_SIDES_INPUT;
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == ForgeCapabilities.ITEM_HANDLER && hasController()) {
			if ((side != null && ioSideConfiguration.getWorldSpaceDirectionConfiguration(side) == MachineSideMode.Input)) {
				return getController().catalystInventory.manuallyProvideCapability(cap, side);
			}
		}
		return LazyOptional.empty();
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerRefineryItemInput(windowId, inventory, this);
	}
}
