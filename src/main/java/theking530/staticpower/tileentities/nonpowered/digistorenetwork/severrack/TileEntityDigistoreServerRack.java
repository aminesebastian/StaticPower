package theking530.staticpower.tileentities.nonpowered.digistorenetwork.severrack;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import theking530.staticpower.data.StaticPowerDataRegistry;
import theking530.staticpower.initialization.ModTileEntityTypes;
import theking530.staticpower.items.DigistoreCard;
import theking530.staticpower.tileentities.components.InventoryComponent;
import theking530.staticpower.tileentities.components.InventoryComponent.InventoryChangeType;
import theking530.staticpower.tileentities.nonpowered.digistorenetwork.BaseDigistoreTileEntity;
import theking530.staticpower.tileentities.nonpowered.digistorenetwork.digistore.DigistoreInventoryComponent;
import theking530.staticpower.tileentities.utilities.MachineSideMode;
import theking530.staticpower.tileentities.utilities.interfaces.ItemStackHandlerFilter;

public class TileEntityDigistoreServerRack extends BaseDigistoreTileEntity {
	public final InventoryComponent cardInventory;
	public final DigistoreInventoryComponent inventory;

	public TileEntityDigistoreServerRack() {
		super(ModTileEntityTypes.DIGISTORE_SERVER_RACK);
		registerComponent(cardInventory = new InventoryComponent("CardInventory", 8, MachineSideMode.Never).setFilter(new ItemStackHandlerFilter() {
			public boolean canInsertItem(int slot, ItemStack stack) {
				return !stack.isEmpty() && stack.getItem() instanceof DigistoreCard;
			}
		}));
		cardInventory.setModifiedCallback(this::onServerRackInventoryChanged);

		registerComponent(inventory = new DigistoreInventoryComponent("Inventory", 0, 0));
	}

	public void onServerRackInventoryChanged(InventoryChangeType changeType, ItemStack item, InventoryComponent inventory) {
		int capacity = 0;
		int cardCount = 0;
		for (ItemStack card : inventory) {
			if (card.getItem() instanceof DigistoreCard) {
				ResourceLocation tier = ((DigistoreCard) card.getItem()).tierType;
				capacity += StaticPowerDataRegistry.getTier(tier).getDigistoreCapacity();
				cardCount++;
			}
		}
		this.inventory.setMaximumStorage(capacity);
		this.inventory.setSupportedItemTypeCount(cardCount);

	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerDigistoreServerRack(windowId, inventory, this);
	}
}
