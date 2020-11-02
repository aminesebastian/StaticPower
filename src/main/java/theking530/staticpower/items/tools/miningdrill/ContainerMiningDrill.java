package theking530.staticpower.items.tools.miningdrill;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
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

	public ContainerMiningDrill(int windowId, PlayerInventory inv, PacketBuffer data) {
		this(windowId, inv, getHeldItemstack(inv, data));
	}

	public ContainerMiningDrill(int windowId, PlayerInventory playerInventory, ItemStack owner) {
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
			public void onSlotChanged() {
				super.onSlotChanged();

				// Update the drill.
				int drillSlot = getPlayerInventory().player.inventory.currentItem;
				if (drillSlot >= 0) {
					if (!getPlayerInventory().player.world.isRemote) {
						ServerPlayerEntity serverPlayer = (ServerPlayerEntity) getPlayerInventory().player;
						serverPlayer.sendSlotContents(ContainerMiningDrill.this, playerHotbarStart + drillSlot, getItemStack());
					}
				}
			}

			@Override
			public boolean isItemValid(ItemStack stack) {
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
	public boolean canDragIntoSlot(Slot slot) {
		return false;
	}

	@Override
	protected boolean playerItemShiftClicked(ItemStack stack, PlayerEntity player, Slot slot, int slotIndex) {
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
		if (!alreadyExists && !mergeItemStack(stack, firstEmptySlot, firstEmptySlot + 1, false)) {
			return true;
		}
		return false;
	}

	@Override
	public ItemStack slotClick(int slot, int dragType, ClickType clickTypeIn, PlayerEntity player) {
		if (slot >= 0 && getSlot(slot) != null && getSlot(slot).getStack() == player.getHeldItemMainhand()) {
			return ItemStack.EMPTY;
		}
		return super.slotClick(slot, dragType, clickTypeIn, player);
	}
}