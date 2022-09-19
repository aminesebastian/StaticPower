package theking530.staticpower.items.backpack;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import theking530.staticcore.item.ItemStackCapabilityInventory;
import theking530.staticcore.item.ItemStackMultiCapabilityProvider;
import theking530.staticcore.network.NetworkGUI;
import theking530.staticpower.items.StaticPowerItem;

public class Backpack extends StaticPowerItem {
	private final int slots;

	public Backpack(int slots) {
		super(new Properties().stacksTo(1).setNoRepair().fireResistant());
		this.slots = slots;
	}

	/**
	 * Add the inventory capability.
	 */
	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
		return new ItemStackMultiCapabilityProvider(stack, nbt).addCapability(new ItemStackCapabilityInventory("default", stack, slots));
	}

	/**
	 * When right clicked, open the backpack UI.
	 */
	@Override
	protected InteractionResultHolder<ItemStack> onStaticPowerItemRightClicked(Level world, Player player, InteractionHand hand, ItemStack item) {
		if (!world.isClientSide && !player.isShiftKeyDown()) {
			NetworkGUI.openGui((ServerPlayer) player, new BackPackContainerProvider(item), buff -> {
				buff.writeInt(player.getInventory().selected);
			});
			return InteractionResultHolder.success(item);
		}
		return InteractionResultHolder.pass(item);
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
