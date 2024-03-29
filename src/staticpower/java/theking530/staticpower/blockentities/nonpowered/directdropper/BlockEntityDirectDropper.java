package theking530.staticpower.blockentities.nonpowered.directdropper;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.blockentity.BlockEntityBase;
import theking530.staticcore.blockentity.components.control.RedstonePulseReactorComponent;
import theking530.staticcore.blockentity.components.items.InventoryComponent;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticcore.utilities.item.InventoryUtilities;
import theking530.staticpower.init.ModBlocks;

public class BlockEntityDirectDropper extends BlockEntityBase {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityDirectDropper> TYPE = new BlockEntityTypeAllocator<BlockEntityDirectDropper>("direct_dropper",
			(type, pos, state) -> new BlockEntityDirectDropper(pos, state), ModBlocks.DirectDropper);
	public static final int DROP_DELAY = 4;

	public final InventoryComponent inventory;
	public final RedstonePulseReactorComponent pulseControl;

	public BlockEntityDirectDropper(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);
		registerComponent(inventory = new InventoryComponent("Inventory", 9).setShiftClickEnabled(true));
		registerComponent(pulseControl = new RedstonePulseReactorComponent("PulseControl", DROP_DELAY, this::drop).shouldControlOnState(true)
				.setProcessingGate(() -> !InventoryUtilities.isInventoryEmpty(inventory)));
	}

	protected boolean drop() {
		ItemStack itemToDrop = InventoryUtilities.getRandomItemStackFromInventory(inventory, 1, false);
		ItemEntity itemEntity = new ItemEntity(getLevel(), getBlockPos().getX() + 0.5, getBlockPos().getY() - 0.5, getBlockPos().getZ() + 0.5, itemToDrop);
		itemEntity.setDeltaMovement(0, 0, 0);
		getLevel().addFreshEntity(itemEntity);
		getLevel().playSound(null, getBlockPos(), SoundEvents.DISPENSER_DISPENSE, SoundSource.BLOCKS, 0.5f, 1.0f);
		return true;
	}

	@Override
	public boolean shouldSerializeWhenBroken(Player player) {
		return false;
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerDirectDropper(windowId, inventory, this);
	}
}
