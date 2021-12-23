package theking530.staticpower.items.tools.miningdrill;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.container.StaticPowerItemContainer;
import theking530.staticpower.container.slots.StaticPowerContainerSlot;
import theking530.staticpower.init.ModItems;

public class ContainerMiningDrill extends StaticPowerItemContainer<MiningDrill> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerMiningDrill, GuiMiningDrill> TYPE = new ContainerTypeAllocator<>("mining_drill", ContainerMiningDrill::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiMiningDrill::new);
		}
	}

	public ItemStackHandler inventory;

	public ContainerMiningDrill(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, getHeldItemstack(inv, data));
	}

	public ContainerMiningDrill(int windowId, Inventory playerInventory, ItemStack owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		// Attempt to get the item filter inventory.
		getItemStack().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent((handler) -> {
			inventory = (ItemStackHandler) handler;
		});

		// Drill Bit
		this.addSlot(new StaticPowerContainerSlot(new ItemStack(ModItems.IronDrillBit), 0.3f, inventory, 0, 80, 24) {
			@Override
			public void setChanged() {
				super.setChanged();

				// Update the drill.
				int drillSlot = getPlayerInventory().player.inventory.selected;
				if (drillSlot >= 0) {
					if (!getPlayerInventory().player.level.isClientSide) {
						ServerPlayer serverPlayer = (ServerPlayer) getPlayerInventory().player;
						serverPlayer.slotChanged(ContainerMiningDrill.this, playerHotbarStart + drillSlot, getItemStack());
					}
				}
			}

			@Override
			public boolean mayPlace(ItemStack stack) {
				return stack.getItem() instanceof DrillBit;
			}
		});

		// Upgrades
//		this.addSlot(new UpgradeItemSlot(inventory, 1, 61, 38));
//		this.addSlot(new UpgradeItemSlot(inventory, 2, 79, 38));
//		this.addSlot(new UpgradeItemSlot(inventory, 3, 97, 38));

		addPlayerHotbar(getPlayerInventory(), 8, 118 + (inventory.getSlots() > 9 ? 12 : 0));
		addPlayerInventory(getPlayerInventory(), 8, 60 + (inventory.getSlots() > 9 ? 12 : 0));
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
	public ItemStack clicked(int slot, int dragType, ClickType clickTypeIn, Player player) {
		if (slot >= 0 && getSlot(slot) != null && getSlot(slot).getItem() == player.getMainHandItem()) {
			return ItemStack.EMPTY;
		}
		return super.clicked(slot, dragType, clickTypeIn, player);
	}
}