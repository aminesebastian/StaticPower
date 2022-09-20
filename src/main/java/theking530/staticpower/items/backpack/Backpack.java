package theking530.staticpower.items.backpack;

import javax.annotation.Nullable;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import theking530.staticcore.item.ICustomModelSupplier;
import theking530.staticcore.item.ItemStackCapabilityInventory;
import theking530.staticcore.item.ItemStackMultiCapabilityProvider;
import theking530.staticcore.network.NetworkGUI;
import theking530.staticpower.client.rendering.items.BackpackItemModel;
import theking530.staticpower.items.StaticPowerItem;
import theking530.staticpower.utilities.InventoryUtilities;
import theking530.staticpower.utilities.ItemUtilities;

public class Backpack extends StaticPowerItem implements ICustomModelSupplier {
	public enum BackpackMode {
		DEFAULT("gui.staticpower.backpack_mode.default"), REFIL("gui.staticpower.backpack_mode.refill"), LOCKED("gui.staticpower.backpack_mode.locked"),
		RELOAD("gui.staticpower.backpack_mode.reload");

		public final String unlocalizedName;

		private BackpackMode(String unlocalizedName) {
			this.unlocalizedName = unlocalizedName;
		}
	}

	private final int slots;
	private final TagKey<Item> inputTag;
	private Ingredient lazyLoadedIngredient;

	public Backpack(int slots, TagKey<Item> tag) {
		super(new Properties().stacksTo(1).setNoRepair().fireResistant());
		this.slots = slots;
		this.inputTag = tag;
	}

	public Backpack(int slots) {
		this(slots, null);
	}

	/**
	 * Add the inventory capability.
	 */
	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
		return new ItemStackMultiCapabilityProvider(stack, nbt).addCapability(new ItemStackCapabilityInventory("default", stack, slots));
	}

	@Override
	public void inventoryTick(ItemStack backpack, Level level, Entity entity, int slot, boolean isSelected) {
		// Only refill if we're in the refill mode.
		if (getMode(backpack) != BackpackMode.REFIL) {
			return;
		}

		// Only do this for players.
		Player player = (Player) entity;
		if (player == null) {
			return;
		}

		IItemHandler backpackInventory = backpack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
		for (int backpackSlot = 0; backpackSlot < backpackInventory.getSlots(); backpackSlot++) {
			// Only extract one at a time. Skip empty slots.
			ItemStack backpackItem = backpackInventory.extractItem(backpackSlot, 1, true);
			if (backpackItem.isEmpty()) {
				continue;
			}

			// Find a slot in the player's inventory that can stack with this item.
			int playerSlot = -1;
			ItemStack playerItem = ItemStack.EMPTY;
			for (int j = 0; j < player.getInventory().getContainerSize(); j++) {
				if (ItemUtilities.areItemStacksStackable(backpackItem, player.getInventory().getItem(j))) {
					playerSlot = j;
					playerItem = player.getInventory().getItem(playerSlot);
					break;
				}
			}

			// Don't do anything if we didn't find the item OR if we did but it's already at
			// a full stack.
			if (playerSlot == -1 || playerItem.getCount() >= playerItem.getMaxStackSize()) {
				break;
			}

			// If after adding, the count of the backpack stack has changed, then we extract
			// that amount from the backpack.
			int initialCount = backpackItem.getCount();
			if (player.getInventory().add(playerSlot, backpackItem)) {
				int inserted = initialCount - backpackItem.getCount();
				backpackInventory.extractItem(backpackSlot, inserted, false);
			}
		}
	}

	@Override
	protected InteractionResultHolder<ItemStack> onStaticPowerItemRightClicked(Level level, Player player, InteractionHand hand, ItemStack item) {
		if (!level.isClientSide()) {
			if (player.isCrouching()) {
				incrementMode(player, item);
				return InteractionResultHolder.consume(item);
			} else if (openBackpack(item, player)) {
				return InteractionResultHolder.consume(item);
			}
		}
		return InteractionResultHolder.pass(item);
	}

	@Override
	protected InteractionResult onPreStaticPowerItemUsedOnBlock(UseOnContext context, Level level, BlockPos pos, Direction face, Player player, ItemStack item) {
		if (player.isCrouching()) {
			if (getMode(item) == BackpackMode.RELOAD) {
				if (transferInventoryContentsToBackpack(item, player, pos, face)) {
					return InteractionResult.CONSUME;
				}
			} else {
				if (transferBackpackContentsToBlock(item, player, pos, face)) {
					return InteractionResult.CONSUME;
				}
			}

			incrementMode(player, item);
			return InteractionResult.CONSUME;
		} else {
			if (!level.isClientSide()) {
				openBackpack(item, player);
			}

			// We pass here to allow other block entities to hijack this.
			// If we open the backpack, but we also used this item on a block with a UI,
			// that UI will open and close the backpack (which is what we want). Backpack
			// takes lowest priority this way.
			return InteractionResult.PASS;
		}
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return getMode(newStack) != getMode(oldStack);
	}

	protected boolean transferInventoryContentsToBackpack(ItemStack backpack, Player player, BlockPos blockPos, Direction face) {
		BlockEntity entity = player.level.getBlockEntity(blockPos);
		if (entity != null) {
			IItemHandler inventory = entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face).orElse(null);
			if (inventory != null) {
				IItemHandler backpackInventory = backpack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
				for (int i = 0; i < inventory.getSlots(); i++) {
					// Pull an item out of the inventory, if we can't accept it, skip it.
					ItemStack simulatedExtract = inventory.extractItem(i, Integer.MAX_VALUE, true);
					if (!canAcceptItem(backpack, simulatedExtract)) {
						continue;
					}

					// Attempt to insert the item into the backpack.
					ItemStack remaining = InventoryUtilities.insertItemIntoInventory(backpackInventory, simulatedExtract, false);
					int transfered = simulatedExtract.getCount() - remaining.getCount();
					inventory.extractItem(i, transfered, false);
				}
				return true;
			}
		}
		return false;
	}

	protected boolean transferBackpackContentsToBlock(ItemStack backpack, Player player, BlockPos blockPos, Direction face) {
		BlockEntity entity = player.level.getBlockEntity(blockPos);
		if (entity != null) {
			IItemHandler inventory = entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face).orElse(null);
			if (inventory != null) {
				IItemHandler backpackInventory = backpack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
				for (int i = 0; i < backpackInventory.getSlots(); i++) {
					ItemStack simulatedExtract = backpackInventory.extractItem(i, Integer.MAX_VALUE, true);
					ItemStack remaining = InventoryUtilities.insertItemIntoInventory(inventory, simulatedExtract, false);

					int transfered = simulatedExtract.getCount() - remaining.getCount();
					backpackInventory.extractItem(i, transfered, false);
				}
				return true;
			}
		}
		return false;
	}

	protected boolean openBackpack(ItemStack backpack, Player player) {
		player.level.playSound(null, player.eyeBlockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER, SoundSource.PLAYERS, 0.4f, 1.0f);
		NetworkGUI.openGui((ServerPlayer) player, new BackPackContainerProvider(backpack), buff -> {
			buff.writeInt(player.getInventory().selected);
		});
		return true;
	}

	public BackpackMode getMode(ItemStack backpack) {
		CompoundTag tag = backpack.getOrCreateTag();
		if (tag.contains("mode")) {
			return BackpackMode.values()[tag.getByte("mode")];
		} else {
			tag.putByte("mode", (byte) BackpackMode.DEFAULT.ordinal());
			return BackpackMode.DEFAULT;
		}
	}

	public void incrementMode(Player player, ItemStack backpack) {
		if (!player.getLevel().isClientSide()) {
			// Update the mode.
			BackpackMode current = getMode(backpack);
			BackpackMode newMode = BackpackMode.values()[((current.ordinal() + 1) % BackpackMode.values().length)];
			backpack.getTag().putByte("mode", (byte) newMode.ordinal());

			// Send a change message.
			player.sendMessage(new TranslatableComponent("gui.staticpower.backpack_mode_updated", new TranslatableComponent(newMode.unlocalizedName)), player.getUUID());
			player.getInventory().setChanged();
		}
	}

	public boolean canAcceptItem(ItemStack backpack, ItemStack item) {
		// We can accept any item if the input tag is empty. DO NOT check for locked
		// mode here as this method also drives in the player can manually place things
		// into the inventory, which we still allow when locked.
		if (inputTag == null) {
			return true;
		}

		// Lazy load the ingredients if need be.
		if (lazyLoadedIngredient == null) {
			lazyLoadedIngredient = Ingredient.of(inputTag);
		}

		return lazyLoadedIngredient.test(item);
	}

	public boolean playerPickedUpItem(ItemStack backpack, ItemEntity item, Player player) {
		// When in locked mode, accept nothing.
		if (this.getMode(backpack) == BackpackMode.LOCKED) {
			return false;
		}
		// Test to see if any of the lazy loaded ingredients support the picked up item.
		if (canAcceptItem(backpack, item.getItem())) {
			// If we have nothing to match against, don't auto pickup anything.
			if (lazyLoadedIngredient == null || lazyLoadedIngredient.isEmpty()) {
				return false;
			}

			IItemHandler backpackInventory = backpack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);

			ItemStack pickedUpStack = item.getItem().copy();

			ItemStack remaining = InventoryUtilities.insertItemIntoInventory(backpackInventory, pickedUpStack, false);
			pickedUpStack.setCount(pickedUpStack.getCount() - remaining.getCount());

			// Raise the item pickup event.
			net.minecraftforge.event.ForgeEventFactory.firePlayerItemPickupEvent(player, item, pickedUpStack);
			item.getItem().setCount(remaining.getCount());
			return true;
		}
		return false;
	}

	@Override
	public boolean hasModelOverride(BlockState state) {
		return true;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BakedModel getModelOverride(BlockState state, BakedModel existingModel, ModelBakeEvent event) {
		return new BackpackItemModel(existingModel);
	}

	public class BackPackContainerProvider implements MenuProvider {
		public ItemStack targetItemStack;

		public BackPackContainerProvider(ItemStack stack) {
			targetItemStack = stack;
		}

		@Override
		public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
			return new ContainerBackpack(windowId, inventory, targetItemStack);
		}

		@Override
		public Component getDisplayName() {
			return targetItemStack.getHoverName();
		}
	}
}
