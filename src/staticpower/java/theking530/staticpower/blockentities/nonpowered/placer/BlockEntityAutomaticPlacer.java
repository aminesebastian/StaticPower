package theking530.staticpower.blockentities.nonpowered.placer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import theking530.staticcore.blockentity.BlockEntityBase;
import theking530.staticcore.blockentity.components.control.RedstonePulseReactorComponent;
import theking530.staticcore.blockentity.components.items.InventoryComponent;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticcore.utilities.item.InventoryUtilities;
import theking530.staticpower.init.ModBlocks;

public class BlockEntityAutomaticPlacer extends BlockEntityBase {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityAutomaticPlacer> TYPE = new BlockEntityTypeAllocator<BlockEntityAutomaticPlacer>("automatic_placer",
			(type, pos, state) -> new BlockEntityAutomaticPlacer(pos, state), ModBlocks.AutomaticPlacer);
	public static final int PLACE_DELAY = 4;

	public final InventoryComponent inventory;
	public final RedstonePulseReactorComponent pulseControl;

	public BlockEntityAutomaticPlacer(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);
		registerComponent(inventory = new InventoryComponent("Inventory", 9).setShiftClickEnabled(true));
		registerComponent(pulseControl = new RedstonePulseReactorComponent("PulseControl", PLACE_DELAY, this::place).shouldControlOnState(true)
				.setProcessingGate(() -> !InventoryUtilities.isInventoryEmpty(inventory)));
	}

	protected boolean place() {
		int slotToUse = InventoryUtilities.getRandomSlotWithItemFromInventory(inventory, 1);
		ItemStack itemToPlace = inventory.getStackInSlot(slotToUse);
		if (!itemToPlace.isEmpty() && itemToPlace.getItem() instanceof BlockItem) {
			// Check to make sure the block we're placing is not a tile entity.
			Block blockToPlace = ((BlockItem) itemToPlace.getItem()).getBlock();
			if (blockToPlace instanceof EntityBlock) {
				return false;
			}

			// Create a fake player to place the block. This handles all the checks for
			// that particular block by placing it using the block item's own placing
			// method.
			FakePlayer player = FakePlayerFactory.getMinecraft((ServerLevel) getLevel());
			player.setItemInHand(InteractionHand.MAIN_HAND, itemToPlace.copy());
			InteractionResult placementResult = itemToPlace.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND,
					new BlockHitResult(new Vec3(0.0f, 1.0f, 0.0f), Direction.DOWN, worldPosition.relative(getFacingDirection()), false)));

			// If successful, extract the item. Otherwise, just return false.
			if (placementResult.consumesAction()) {
				getLevel().playSound(null, getBlockPos(), SoundEvents.DISPENSER_DISPENSE, SoundSource.BLOCKS, 0.5f, 1.0f);
				inventory.extractItem(slotToUse, 1, false);
				return true;
			} else {
				getLevel().playSound(null, getBlockPos(), SoundEvents.DISPENSER_FAIL, SoundSource.BLOCKS, 0.5f, 1.0f);
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean shouldSerializeWhenBroken(Player player) {
		return false;
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerAutomaticPlacer(windowId, inventory, this);
	}
}
