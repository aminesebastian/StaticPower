package theking530.staticpower.tileentities.nonpowered.placer;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import theking530.staticcore.initialization.tileentity.TileEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.TileEntityConfigurable;
import theking530.staticpower.tileentities.components.control.RedstonePulseReactorComponent;
import theking530.staticpower.tileentities.components.items.InventoryComponent;
import theking530.staticpower.utilities.InventoryUtilities;

public class TileEntityAutomaticPlacer extends TileEntityConfigurable {
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityAutomaticPlacer> TYPE = new TileEntityTypeAllocator<TileEntityAutomaticPlacer>((type) -> new TileEntityAutomaticPlacer(),
			ModBlocks.AutomaticPlacer);
	public static final int PLACE_DELAY = 4;

	public final InventoryComponent inventory;
	public final RedstonePulseReactorComponent pulseControl;

	public TileEntityAutomaticPlacer() {
		super(TYPE);
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
			if (blockToPlace.hasTileEntity(blockToPlace.getDefaultState())) {
				return false;
			}

			// Create a fake player to place the block. This handles all the checks for
			// that particular block by placing it using the block item's own placing
			// method.
			FakePlayer player = FakePlayerFactory.getMinecraft((ServerWorld) getWorld());
			player.setHeldItem(Hand.MAIN_HAND, itemToPlace.copy());
			ActionResultType placementResult = itemToPlace
					.onItemUse(new ItemUseContext(player, Hand.MAIN_HAND, new BlockRayTraceResult(new Vector3d(0.0f, 1.0f, 0.0f), Direction.DOWN, pos.offset(getFacingDirection()), false)));

			// If successful, extract the item. Otherwise, just return false.
			if (placementResult.isSuccessOrConsume()) {
				getWorld().playSound(null, getPos(), SoundEvents.BLOCK_DISPENSER_DISPENSE, SoundCategory.BLOCKS, 0.5f, 1.0f);
				inventory.extractItem(slotToUse, 1, false);
				return true;
			} else {
				getWorld().playSound(null, getPos(), SoundEvents.BLOCK_DISPENSER_FAIL, SoundCategory.BLOCKS, 0.5f, 1.0f);
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean shouldSerializeWhenBroken() {
		return false;
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerAutomaticPlacer(windowId, inventory, this);
	}
}
