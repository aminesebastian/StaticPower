package theking530.staticpower.blockentities.digistorenetwork.ioport;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.blockentities.digistorenetwork.BaseDigistoreTileEntity;
import theking530.staticpower.cables.digistore.DigistoreNetworkModule;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.cables.ModCableModules;

public class TileEntityDigistoreIOPort extends BaseDigistoreTileEntity {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityDigistoreIOPort> TYPE = new BlockEntityTypeAllocator<>((type, pos, state) -> new TileEntityDigistoreIOPort(pos, state),
			ModBlocks.DigistoreIOPort);

	public TileEntityDigistoreIOPort(BlockPos pos, BlockState state) {
		super(TYPE, pos, state, 5);
		registerComponent(new DigitstoreIOPortInventoryComponent("IONetwork"));
	}

	@Override
	public InteractionResult onBlockActivated(BlockState state, Player player, InteractionHand hand, BlockHitResult hit) {
		if (getLevel().isClientSide) {
			return InteractionResult.CONSUME;
		}

		digistoreCableProvider.<DigistoreNetworkModule>getNetworkModule(ModCableModules.Digistore.get()).ifPresent(module -> {
			// Do nothing if there is no manager.
			if (!module.isManagerPresent()) {
				return;
			}

			// Keep track of if any items changed.
			boolean itemInserted = false;

			// Loop through the whole inventory.
			for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
				// Skip empty slots.
				if (player.getInventory().getItem(i).isEmpty()) {
					continue;
				}
				// Get the item in the slot.
				ItemStack currentItem = player.getInventory().getItem(i).copy();

				// Skip any items that are not currently in the digistore system.
				if (!module.containsItem(currentItem)) {
					continue;
				}

				// Insert it into the network.
				ItemStack remaining = module.insertItem(currentItem, false);

				// Update the slot contents.
				if (currentItem.getCount() != remaining.getCount()) {
					itemInserted = true;
					player.getInventory().setItem(i, remaining);
				}
			}

			// IF an item was inserted and the world is remote, play a sound.
			if (level.isClientSide && itemInserted) {
				level.playSound(player, worldPosition, SoundEvents.ARMOR_EQUIP_LEATHER, SoundSource.PLAYERS, 1.0f, 1.0f);
			}
		});

		return InteractionResult.SUCCESS;
	}
}
