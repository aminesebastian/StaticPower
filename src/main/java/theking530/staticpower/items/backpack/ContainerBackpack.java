package theking530.staticpower.items.backpack;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.container.StaticPowerItemContainer;
import theking530.staticpower.container.slots.StaticPowerContainerSlot;

public class ContainerBackpack extends StaticPowerItemContainer<Backpack> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerBackpack, GuiBackpack> TYPE = new ContainerTypeAllocator<>("backpack", ContainerBackpack::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiBackpack::new);
		}
	}

	public ItemStackHandler inventory;

	public ContainerBackpack(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, getHeldItemstack(inv, data));
	}

	public ContainerBackpack(int windowId, Inventory playerInventory, ItemStack owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		// Attempt to get the item filter inventory.
		getItemStack().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent((handler) -> {
			inventory = (ItemStackHandler) handler;
		});

		int xOffset = 0;
		int maxPerRow = 0;
		if (inventory.getSlots() == 9) {
			xOffset = 86;
			maxPerRow = 3;
		} else if (inventory.getSlots() == 12) {
			xOffset = 86;
			maxPerRow = 4;
		} else if (inventory.getSlots() == 15) {
			xOffset = 86;
			maxPerRow = 5;
		}

		this.addSlotsInGrid(inventory, 0, xOffset, 21, maxPerRow, (i, x, y) -> new StaticPowerContainerSlot(inventory, i, x, y) {
			@Override
			public void setChanged() {
				super.setChanged();

				// Update the backpack.
				int slot = getPlayerInventory().player.getInventory().selected;
				if (slot >= 0) {
					if (!getPlayerInventory().player.level.isClientSide) {
						ServerPlayer serverPlayer = (ServerPlayer) getPlayerInventory().player;
						serverPlayer.containerMenu.broadcastFullState();
					}
				}
			}
		});

		addAllPlayerSlots();
	}

	@Override
	public boolean canDragTo(Slot slot) {
		return false;
	}

	@Override
	protected boolean playerItemShiftClicked(ItemStack stack, Player player, Slot slot, int slotIndex) {
		boolean alreadyExists = false;
		int firstEmptySlot = -1;

		for (int i = 0; i < inventory.getSlots(); i++) {
			if (firstEmptySlot == -1 && inventory.getStackInSlot(i).isEmpty()) {
				firstEmptySlot = i;
			}
			if (ItemHandlerHelper.canItemStacksStack(inventory.getStackInSlot(i), stack)) {
				alreadyExists = true;
			}
		}
		if (!alreadyExists && !moveItemStackTo(stack, firstEmptySlot, firstEmptySlot + 1, false)) {
			return true;
		}
		return false;
	}

	@Override
	public void clicked(int slot, int dragType, ClickType clickTypeIn, Player player) {
		if (slot >= 0 && getSlot(slot) != null && getSlot(slot).getItem() == player.getMainHandItem()) {
			return;
		}
		super.clicked(slot, dragType, clickTypeIn, player);
	}
}