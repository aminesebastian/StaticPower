package theking530.staticpower.items.backpack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
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
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.IItemHandler;
import theking530.staticcore.client.ICustomModelProvider;
import theking530.staticcore.gui.text.GuiTextUtilities;
import theking530.staticcore.item.ItemStackCapabilityInventory;
import theking530.staticcore.item.ItemStackMultiCapabilityProvider;
import theking530.staticcore.network.NetworkGUI;
import theking530.staticcore.utilities.item.InventoryUtilities;
import theking530.staticcore.utilities.item.ItemUtilities;
import theking530.staticpower.client.rendering.items.BackpackItemModel;
import theking530.staticpower.init.ModCreativeTabs;
import theking530.staticpower.items.StaticPowerItem;

public class Backpack extends StaticPowerItem implements ICustomModelProvider {
	public enum BackpackMode {
		DEFAULT("gui.staticpower.backpack_mode.default"), REFIL("gui.staticpower.backpack_mode.refill"),
		LOCKED("gui.staticpower.backpack_mode.locked"), RELOAD("gui.staticpower.backpack_mode.reload");

		public final String unlocalizedName;

		private BackpackMode(String unlocalizedName) {
			this.unlocalizedName = unlocalizedName;
		}
	}

	public static final String OPEN_STATE_KEY = "is_open";
	private final ResourceLocation openModel;
	private final int slots;
	private final TagKey<Item> inputTag;
	private Ingredient lazyLoadedIngredient;

	public Backpack(int slots, ResourceLocation openModel, TagKey<Item> tag) {
		super(new Properties().stacksTo(1).setNoRepair().fireResistant().tab(ModCreativeTabs.TOOLS));
		this.openModel = openModel;
		this.slots = slots;
		this.inputTag = tag;
	}

	public Backpack(int slots, ResourceLocation openModel) {
		this(slots, openModel, null);
	}

	/**
	 * Add the inventory capability.
	 */
	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
		return new ItemStackMultiCapabilityProvider(stack, nbt)
				.addCapability(new ItemStackCapabilityInventory("default", stack, slots));
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

		IItemHandler backpackInventory = backpack.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
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
				continue;
			}

			// If after adding, the count of the backpack stack has changed, then we extract
			// that amount from the backpack.
			int initialCount = backpackItem.getCount();
			if (player.getInventory().add(playerSlot, backpackItem)) {
				int inserted = initialCount - backpackItem.getCount();
				backpackInventory.extractItem(backpackSlot, inserted, false);
				break;
			}
		}
	}

	@Override
	protected InteractionResultHolder<ItemStack> onStaticPowerItemRightClicked(Level level, Player player,
			InteractionHand hand, ItemStack item) {
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
	protected InteractionResult onPreStaticPowerItemUsedOnBlock(UseOnContext context, Level level, BlockPos pos,
			Direction face, Player player, ItemStack item) {
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
		return super.shouldCauseBlockBreakReset(oldStack, newStack) && getMode(newStack) != getMode(oldStack);
	}

	protected boolean transferInventoryContentsToBackpack(ItemStack backpack, Player player, BlockPos blockPos,
			Direction face) {
		BlockEntity entity = player.level.getBlockEntity(blockPos);
		if (entity != null) {
			IItemHandler inventory = entity.getCapability(ForgeCapabilities.ITEM_HANDLER, face).orElse(null);
			if (inventory != null) {
				IItemHandler backpackInventory = backpack.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
				for (int i = 0; i < inventory.getSlots(); i++) {
					// Pull an item out of the inventory, if we can't accept it, skip it.
					ItemStack simulatedExtract = inventory.extractItem(i, Integer.MAX_VALUE, true);
					if (!canAcceptItem(backpack, simulatedExtract)) {
						continue;
					}

					// Attempt to insert the item into the backpack.
					ItemStack remaining = InventoryUtilities.insertItemIntoInventory(backpackInventory,
							simulatedExtract, false);
					int transfered = simulatedExtract.getCount() - remaining.getCount();
					inventory.extractItem(i, transfered, false);
				}
				return true;
			}
		}
		return false;
	}

	protected boolean transferBackpackContentsToBlock(ItemStack backpack, Player player, BlockPos blockPos,
			Direction face) {
		BlockEntity entity = player.level.getBlockEntity(blockPos);
		if (entity != null) {
			IItemHandler inventory = entity.getCapability(ForgeCapabilities.ITEM_HANDLER, face).orElse(null);
			if (inventory != null) {
				IItemHandler backpackInventory = backpack.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
				for (int i = 0; i < backpackInventory.getSlots(); i++) {
					ItemStack simulatedExtract = backpackInventory.extractItem(i, Integer.MAX_VALUE, true);
					ItemStack remaining = InventoryUtilities.insertItemIntoInventory(inventory, simulatedExtract,
							false);

					int transfered = simulatedExtract.getCount() - remaining.getCount();
					backpackInventory.extractItem(i, transfered, false);
				}
				return true;
			}
		}
		return false;
	}

	protected boolean openBackpack(ItemStack backpack, Player player) {
		player.level.playSound(null, player.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER, SoundSource.PLAYERS, 0.4f,
				1.0f);
		NetworkGUI.openScreen((ServerPlayer) player, new BackPackContainerProvider(backpack), buff -> {
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
			player.sendSystemMessage(Component.translatable("gui.staticpower.backpack_mode_updated",
					Component.translatable(newMode.unlocalizedName)));
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

	public ItemStack playerPickedUpItem(ItemStack backpack, ItemStack item, Player player) {
		// When in locked mode, accept nothing.
		if (this.getMode(backpack) == BackpackMode.LOCKED) {
			return item;
		}

		// Test to see if any of the lazy loaded ingredients support the picked up item.
		if (canAcceptItem(backpack, item)) {
			// If we have nothing to match against, don't auto pickup anything.
			if (lazyLoadedIngredient == null || lazyLoadedIngredient.isEmpty()) {
				return item;
			}

			IItemHandler backpackInventory = backpack.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);

			ItemStack pickedUpStack = item.copy();

			ItemStack remaining = InventoryUtilities.insertItemIntoInventory(backpackInventory, pickedUpStack, false);
			pickedUpStack.setCount(pickedUpStack.getCount() - remaining.getCount());
			item.setCount(remaining.getCount());
			return item;
		}
		return item;
	}

	@Override
	public boolean hasModelOverride(BlockState state) {
		return true;
	}

	public boolean isOpened(ItemStack backpack) {
		return backpack.getTag().contains(OPEN_STATE_KEY) ? backpack.getTag().getBoolean(OPEN_STATE_KEY) : false;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BakedModel getBlockModeOverride(BlockState state, BakedModel existingModel,
			ModelEvent.BakingCompleted event) {
		return new BackpackItemModel(existingModel, event.getModels().get(openModel));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip,
			boolean isShowingAdvanced) {
		IItemHandler backpackInventory = stack.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
		if (backpackInventory == null) {
			return;
		}

		float occupied = 0;
		float max = 0;
		for (int slot = 0; slot < backpackInventory.getSlots(); slot++) {
			occupied += backpackInventory.getStackInSlot(slot).getCount();
			max += backpackInventory.getSlotLimit(slot);
		}
		if (max == 0) {
			return;
		}

		if (occupied == 0) {
			tooltip.add(Component.translatable("gui.staticcore.empty").withStyle(ChatFormatting.GRAY));
		} else {
			tooltip.add(Component
					.translatable("gui.staticcore.filled_amount",
							GuiTextUtilities.formatNumberAsPercentStringNoDecimal(occupied / max))
					.withStyle(ChatFormatting.GRAY));
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getAdvancedTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip) {
		IItemHandler backpackInventory = stack.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
		if (backpackInventory == null) {
			return;
		}

		Map<Item, Integer> itemMap = new HashMap<>();

		for (int slot = 0; slot < backpackInventory.getSlots(); slot++) {
			ItemStack stackInSlot = backpackInventory.getStackInSlot(slot);
			if (stackInSlot.isEmpty()) {
				continue;
			}

			Item itemInSlot = stackInSlot.getItem();
			if (!itemMap.containsKey(itemInSlot)) {
				itemMap.put(itemInSlot, stackInSlot.getCount());
			} else {
				itemMap.put(itemInSlot, itemMap.get(itemInSlot) + stackInSlot.getCount());
			}
		}

		for (Entry<Item, Integer> entry : itemMap.entrySet()) {
			Component component = GuiTextUtilities
					.createColoredBulletTooltip(entry.getKey().getDescription().getString(), ChatFormatting.GRAY);
			tooltip.add(Component.literal("  ").append(component)
					.append(Component.literal(String.format(" x%1$d", entry.getValue())))
					.withStyle(component.getStyle()));
		}
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
