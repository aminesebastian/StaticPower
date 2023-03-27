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
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticcore.blockentity.components.control.sideconfiguration.presets.AllSidesInput;
import theking530.staticcore.data.StaticCoreTiers;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.blockentities.machines.refinery.BaseRefineryBlockEntity;
import theking530.staticpower.init.ModBlocks;

public class BlockEntityRefineryItemInput extends BaseRefineryBlockEntity {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityRefineryItemInput> TYPE = new BlockEntityTypeAllocator<BlockEntityRefineryItemInput>("refinery_item_input",
			(type, pos, state) -> new BlockEntityRefineryItemInput(pos, state), ModBlocks.RefineryItemInput);

	public final SideConfigurationComponent ioSideConfiguration;

	public BlockEntityRefineryItemInput(BlockPos pos, BlockState state) {
		super(TYPE, pos, state, StaticCoreTiers.ADVANCED);
		registerComponent(ioSideConfiguration = new SideConfigurationComponent("SideConfiguration", AllSidesInput.INSTANCE));
	}

	@Override
	public void process() {

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
