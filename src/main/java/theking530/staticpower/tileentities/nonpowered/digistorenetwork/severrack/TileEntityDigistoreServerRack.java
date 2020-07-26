package theking530.staticpower.tileentities.nonpowered.digistorenetwork.severrack;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;
import theking530.staticpower.init.ModTileEntityTypes;
import theking530.staticpower.tileentities.nonpowered.digistorenetwork.BaseDigistoreTileEntity;
import theking530.staticpower.tileentities.nonpowered.digistorenetwork.digistore.DigistoreInventoryComponent;

public class TileEntityDigistoreServerRack extends BaseDigistoreTileEntity {
	/** KEEP IN MIND: This is purely cosmetic and on the client side. */
	public static final ModelProperty<ServerRackRenderingState> CARD_RENDERING_STATE = new ModelProperty<ServerRackRenderingState>();
	public final DigistoreInventoryComponent inventory;

	public TileEntityDigistoreServerRack() {
		super(ModTileEntityTypes.DIGISTORE_SERVER_RACK);
		registerComponent(inventory = new DigistoreInventoryComponent("Inventory", 8));
		inventory.setModifiedCallback((type, stack, comp) -> {
			markTileEntityForSynchronization();
		});
	}

	@Nonnull
	@Override
	public IModelData getModelData() {
		ModelDataMap.Builder builder = new ModelDataMap.Builder();
		return builder.withInitial(CARD_RENDERING_STATE, new ServerRackRenderingState(getCards(), inventory.getFilledRatio())).build();
	}

	protected ItemStack[] getCards() {
		ItemStack[] cards = new ItemStack[inventory.getSlots()];
		for (int i = 0; i < inventory.getSlots(); i++) {
			cards[i] = inventory.getStackInSlot(i);
		}
		return cards;
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerDigistoreServerRack(windowId, inventory, this);
	}
}
