package theking530.staticpower.blockentities.digistorenetwork.severrack;

import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.blockentities.BlockEntityUpdateRequest;
import theking530.staticpower.blockentities.components.items.InventoryComponent.InventoryChangeType;
import theking530.staticpower.blockentities.digistorenetwork.BaseDigistoreTileEntity;
import theking530.staticpower.blockentities.digistorenetwork.digistore.DigistoreInventoryComponent;
import theking530.staticpower.init.ModBlocks;

public class TileEntityDigistoreServerRack extends BaseDigistoreTileEntity {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityDigistoreServerRack> TYPE = new BlockEntityTypeAllocator<TileEntityDigistoreServerRack>(
			(type, pos, state) -> new TileEntityDigistoreServerRack(pos, state), ModBlocks.DigistoreServerRack);

	/** KEEP IN MIND: This is purely cosmetic and on the client side. */
	public static final ModelProperty<ServerRackRenderingState> CARD_RENDERING_STATE = new ModelProperty<ServerRackRenderingState>();
	public final DigistoreInventoryComponent inventory;

	public TileEntityDigistoreServerRack(BlockPos pos, BlockState state) {
		super(TYPE, pos, state, 5000);
		registerComponent(inventory = (DigistoreInventoryComponent) new DigistoreInventoryComponent("Inventory", 8).setShiftClickEnabled(true));
		inventory.setModifiedCallback((type, stack, comp) -> {
			if (type != InventoryChangeType.MODIFIED) {
				if (!getLevel().isClientSide()) {
					addUpdateRequest(BlockEntityUpdateRequest.syncDataOnly(true), true);
				}
			}
		});
	}

	@Nonnull
	@Override
	public IModelData getModelData() {
		ModelDataMap.Builder builder = new ModelDataMap.Builder();
		return builder.withInitial(CARD_RENDERING_STATE, new ServerRackRenderingState(getCards())).build();
	}

	protected ItemStack[] getCards() {
		ItemStack[] cards = new ItemStack[inventory.getSlots()];
		for (int i = 0; i < inventory.getSlots(); i++) {
			cards[i] = inventory.getStackInSlot(i);
		}
		return cards;
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerDigistoreServerRack(windowId, inventory, this);
	}
}
