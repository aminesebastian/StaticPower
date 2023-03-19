package theking530.staticpower.items.tools.chainsaw;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import theking530.staticcore.container.StaticPowerItemContainer;
import theking530.staticcore.container.slots.StaticPowerContainerSlot;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.init.ModItems;

public class ContainerChainsaw extends StaticPowerItemContainer<Chainsaw> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerChainsaw, GuiChainsaw> TYPE = new ContainerTypeAllocator<>(
			"chainsaw", ContainerChainsaw::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiChainsaw::new);
		}
	}

	public ItemStackHandler inventory;

	public ContainerChainsaw(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, getHeldItemstack(inv, data));
	}

	public ContainerChainsaw(int windowId, Inventory playerInventory, ItemStack owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		// Attempt to get the item filter inventory.
		getItemStack().getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent((handler) -> {
			inventory = (ItemStackHandler) handler;
		});

		// Drill Bit
		this.addSlot(
				new StaticPowerContainerSlot(new ItemStack(ModItems.IronChainsawBlade.get()), 0.3f, inventory, 0, 80, 24) {
					@Override
					public void setChanged() {
						super.setChanged();

						// Update the blade.
						int bladeSlot = getPlayerInventory().player.getInventory().selected;
						if (bladeSlot >= 0) {
							if (!getPlayerInventory().player.level.isClientSide) {
								ServerPlayer serverPlayer = (ServerPlayer) getPlayerInventory().player;
								serverPlayer.containerMenu.broadcastFullState();
							}
						}
					}

					@Override
					public boolean mayPlace(ItemStack stack) {
						return stack.getItem() instanceof ChainsawBlade;
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