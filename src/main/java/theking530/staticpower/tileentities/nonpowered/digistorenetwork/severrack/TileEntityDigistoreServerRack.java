package theking530.staticpower.tileentities.nonpowered.digistorenetwork.severrack;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;
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
	/** KEEP IN MIND: This is purely cosmetic and on the client side. */
	public static final ModelProperty<ItemStack[]> CARD_RENDERING_STATE = new ModelProperty<ItemStack[]>();
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
		this.inventory.getInventory().setMaximumStorage(capacity);
		this.inventory.getInventory().setSupportedItemTypeCount(cardCount);
		this.markTileEntityForSynchronization();
	}

	@Nonnull
	@Override
	public IModelData getModelData() {
		ModelDataMap.Builder builder = new ModelDataMap.Builder();
		return builder.withInitial(CARD_RENDERING_STATE, getCards()).build();
	}

	protected ItemStack[] getCards() {
		ItemStack[] cards = new ItemStack[cardInventory.getSlots()];
		for (int i = 0; i < cardInventory.getSlots(); i++) {
			cards[i] = cardInventory.getStackInSlot(i);
		}
		return cards;
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerDigistoreServerRack(windowId, inventory, this);
	}
}
