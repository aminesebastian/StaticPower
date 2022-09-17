package theking530.staticpower.blockentities.nonpowered.solderingtable;

import javax.annotation.Nullable;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.container.slots.SolderingTableOutputSlot;
import theking530.staticpower.data.crafting.wrappers.soldering.SolderingRecipe;
import theking530.staticpower.utilities.InventoryUtilities;
import theking530.staticpower.utilities.ItemUtilities;

public class ContainerSolderingTable extends AbstractContainerSolderingTable<BlockEntitySolderingTable> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerSolderingTable, GuiSolderingTable> TYPE = new ContainerTypeAllocator<>("soldering_table", ContainerSolderingTable::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiSolderingTable::new);
		}
	}

	private @Nullable SolderingTableOutputSlot outputSlot;
	private ResultContainer craftResult;

	public ContainerSolderingTable(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (BlockEntitySolderingTable) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerSolderingTable(int windowId, Inventory playerInventory, BlockEntitySolderingTable owner) {
		super(TYPE, windowId, playerInventory, owner);
		enableSolderingIronSlot = true;

	}

	@Override
	public void initializeContainer() {
		enableSolderingIronSlot = true;

		super.initializeContainer();

		// Initial update of the output slot.
		updateOutputSlot();
	}

	@Override
	protected void addOutputSlot() {
		// Craft result inventory.
		craftResult = new ResultContainer();
		// Output slot.
		addSlot(outputSlot = new SolderingTableOutputSlot(getPlayerInventory().player, craftResult, 0, 129, 38));
	}

	/**
	 * Handles checking for if the crafting pattern has changed.
	 */
	public void broadcastChanges() {
		super.broadcastChanges();
		updateOutputSlot();
	}

	@Override
	public void clicked(int slotId, int dragType, ClickType clickTypeIn, Player player) {
		// If we clicked on the output slot, do the crafting.
		if (slotId == 10) {
			// Get the recipe. If we dont currently have a valid recipe, just return an
			// empty itemstack.
			SolderingRecipe recipe = getTileEntity().getCurrentRecipe().orElse(null);
			if (recipe == null || !getTileEntity().hasRequiredItems(recipe)) {
				return;
			}

			if (clickTypeIn == ClickType.PICKUP) {
				// If the player clicked on the output and their held item does not stack with
				// the output, do nothing and return an empty itemstack.
				ItemStack heldItem = getCarried();
				if (!heldItem.isEmpty() && !ItemUtilities.areItemStacksStackable(heldItem, recipe.getResultItem())) {
					return;
				}

				// If crafting the item would result in a stack larger than the max stack size,
				// do nothing.
				if (recipe.getResultItem().getCount() + heldItem.getCount() > recipe.getResultItem().getMaxStackSize()) {
					return;
				}

				// Craft the output.
				ItemStack craftedResult = getTileEntity().craftItem(1);

				// Update the player's held item.
				if (getCarried().isEmpty()) {
					setCarried(craftedResult);
				} else {
					getCarried().grow(craftedResult.getCount());
				}

				// If on the server, update the held item.
				if (!getTileEntity().getLevel().isClientSide) {
					broadcastChanges();
				}

				// Tell the slot we crafted.
				outputSlot.onCrafted(player, craftedResult);

				// Update the output slot (do this even though we do it on tick to ensure we
				// dont display an item when its no longer craftable).
				updateOutputSlot();
			} else if (clickTypeIn == ClickType.QUICK_MOVE) {
				// Craft the output.
				if (!getTileEntity().getLevel().isClientSide) {
					int amount = 0;
					while (InventoryUtilities.canFullyInsertItemIntoPlayerInventory(recipe.getResultItem().copy(), player.getInventory())) {
						ItemStack craftedResult = getTileEntity().craftItem(1);
						player.addItem(craftedResult);
						// Tell the slot we crafted.
						outputSlot.onCrafted(player, craftedResult);
						amount++;
						// Break when we run out of items.
						if (!getTileEntity().hasRequiredItems(recipe)) {
							break;
						}
					}
					broadcastFullState();
					// Old Way ((ServerPlayer) player).refreshContainer(this, this.getItems());
				}
			}
		} else {
			super.clicked(slotId, dragType, clickTypeIn, player);
		}
	}

	/**
	 * Update the crafting output slot's contents.
	 * 
	 * @param slotIndex
	 * @param world
	 * @param player
	 * @param craftingInv
	 * @param outputInv
	 */
	protected void updateOutputSlot() {
		// Update the output slot if this is NOT the auto variant.
		if (!getPlayerInventory().player.level.isClientSide && getType() == TYPE.getType()) {
			ItemStack output = ItemStack.EMPTY;

			// Set the slot contents on the server.
			if (getTileEntity().getCurrentRecipe().isPresent()) {
				if (getTileEntity().hasRequiredItems(getTileEntity().getCurrentRecipe().get())) {
					output = getTileEntity().getCurrentRecipe().get().getResultItem().copy();
				}
			}

			craftResult.setItem(0, output);

			// Sync the slot.
			ServerPlayer serverplayerentity = (ServerPlayer) getPlayerInventory().player;
			serverplayerentity.connection.send(new ClientboundContainerSetSlotPacket(containerId, this.getStateId(), 10, output));
		}
	}

}
