package theking530.staticpower.blockentities.machines.refinery;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.blockentity.BlockEntityBase;
import theking530.staticcore.blockentity.components.multiblock.MultiblockComponent;
import theking530.staticcore.blockentity.components.multiblock.MultiblockState;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticpower.blockentities.machines.refinery.controller.BlockEntityRefineryController;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.init.ModMultiblocks;

public class BaseRefineryBlockEntity extends BlockEntityBase {
	public final MultiblockComponent multiblockComponent;

	public BaseRefineryBlockEntity(BlockEntityTypeAllocator<? extends BaseRefineryBlockEntity> allocator, BlockPos pos,
			BlockState state) {
		super(allocator, pos, state);
		registerComponent(
				multiblockComponent = new MultiblockComponent("MultiblockComponent", ModMultiblocks.REFINERY.get()));
		multiblockComponent.setStateChangedCallback(this::multiblockStateChanged);
	}

	public boolean hasController() {
		return multiblockComponent.isWellFormed();
	}

	public BlockEntityRefineryController getController() {
		return (BlockEntityRefineryController) getLevel().getBlockEntity(multiblockComponent.getMasterPosition());
	}

	public void multiblockStateChanged(MultiblockState state) {

	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		if (hasController()) {
			return getController().createMenu(windowId, inventory, player);
		}
		return null;
	}

	@Override
	public ResourceLocation getTier() {
		return StaticPowerTiers.STATIC;
	}
}
