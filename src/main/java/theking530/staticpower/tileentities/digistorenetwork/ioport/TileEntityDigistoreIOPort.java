package theking530.staticpower.tileentities.digistorenetwork.ioport;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockRayTraceResult;
import theking530.staticpower.cables.digistore.DigistoreNetworkModule;
import theking530.staticpower.cables.network.CableNetworkModuleTypes;
import theking530.staticpower.init.ModTileEntityTypes;
import theking530.staticpower.tileentities.digistorenetwork.BaseDigistoreTileEntity;

public class TileEntityDigistoreIOPort extends BaseDigistoreTileEntity {
	public TileEntityDigistoreIOPort() {
		super(ModTileEntityTypes.DIGISTORE_IO_PORT);
		registerComponent(new DigitstoreIOPortInventoryComponent("IONetwork"));
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		if (getWorld().isRemote) {
			return ActionResultType.CONSUME;
		}

		digistoreCableProvider.<DigistoreNetworkModule>getNetworkModule(CableNetworkModuleTypes.DIGISTORE_NETWORK_MODULE).ifPresent(module -> {
			// Do nothing if there is no manager.
			if (!module.isManagerPresent()) {
				return;
			}

			// Keep track of if any items changed.
			boolean itemInserted = false;

			// Loop through the whole inventory.
			for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
				// Skip empty slots.
				if (player.inventory.getStackInSlot(i).isEmpty()) {
					continue;
				}
				// Get the item in the slot.
				ItemStack currentItem = player.inventory.getStackInSlot(i).copy();

				// Skip any items that are not currently in the digistore system.
				if (!module.containsItem(currentItem)) {
					continue;
				}

				// Insert it into the network.
				ItemStack remaining = module.insertItem(currentItem, false);

				// Update the slot contents.
				if (currentItem.getCount() != remaining.getCount()) {
					itemInserted = true;
					player.inventory.setInventorySlotContents(i, remaining);
				}
			}

			// IF an item was inserted and the world is remote, play a sound.
			if (world.isRemote && itemInserted) {
				world.playSound(player, pos, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.PLAYERS, 1.0f, 1.0f);
			}
		});

		return ActionResultType.SUCCESS;
	}
}
