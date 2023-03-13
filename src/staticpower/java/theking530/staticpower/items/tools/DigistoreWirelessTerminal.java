package theking530.staticpower.items.tools;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.api.energy.StaticPowerVoltage;
import theking530.api.energy.StaticVoltageRange;
import theking530.api.energy.item.EnergyHandlerItemStackUtilities;
import theking530.staticcore.network.NetworkGUI;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blockentities.components.ComponentUtilities;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.attachments.digistore.craftingterminal.ContainerDigistoreCraftingTerminal;
import theking530.staticpower.cables.attachments.digistore.craftingterminal.DigistoreCraftingTerminal;
import theking530.staticpower.cables.digistore.DigistoreCableProviderComponent;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.init.ModCreativeTabs;
import theking530.staticpower.init.ModKeyBindings;
import theking530.staticpower.items.StaticPowerEnergyStoringItem;

public class DigistoreWirelessTerminal extends StaticPowerEnergyStoringItem {
	private static final String TERMINAL_POSITION_KEY = "terminal_position";
	private static final String TERMINAL_SIDE_KEY = "terminal_side";

	public DigistoreWirelessTerminal() {
		super(new Item.Properties().tab(ModCreativeTabs.TOOLS));
	}

	public boolean isBound(Level world, ItemStack wirelessDevice) {
		// Check if we have a tag, if not, not bound.
		if (!wirelessDevice.hasTag()) {
			return false;
		}

		// Ensure the terminal attachmenet is still valid.
		return !getTerminalAttachment(world, wirelessDevice).isEmpty();
	}

	public Direction getTerminalAttachDirection(ItemStack wirelessDevice) {
		return Direction.values()[wirelessDevice.getTag().getInt(TERMINAL_SIDE_KEY)];
	}

	public AbstractCableProviderComponent getCableProvider(Level world, ItemStack wirelessDevice) {
		// Check if we have a tag, if not, not bound.
		if (!wirelessDevice.hasTag()) {
			return null;
		}

		// Get the terminal's position.
		BlockPos terminalPos = BlockPos.of(wirelessDevice.getTag().getLong(TERMINAL_POSITION_KEY));
		BlockEntity te = world.getBlockEntity(terminalPos);
		return ComponentUtilities.getComponent(DigistoreCableProviderComponent.class, te).orElse(null);
	}

	public ItemStack getTerminalAttachment(Level world, ItemStack wirelessDevice) {
		AbstractCableProviderComponent provider = getCableProvider(world, wirelessDevice);
		if (provider != null) {
			Direction attachmentSide = getTerminalAttachDirection(wirelessDevice);
			ItemStack attachmentOnSide = provider.getAttachment(attachmentSide);
			if (!attachmentOnSide.isEmpty() && attachmentOnSide.getItem() instanceof DigistoreCraftingTerminal) {
				return attachmentOnSide;
			}
		}
		return ItemStack.EMPTY;
	}

	public boolean usePower(ItemStack itemstack) {
		// Should move to config, but 10SV per opening.
		if (EnergyHandlerItemStackUtilities.getStoredPower(itemstack) >= StaticPowerConfig.SERVER.digistoreWirelessTerminalPowerUsage.get()) {
			EnergyHandlerItemStackUtilities.drainPower(itemstack, StaticPowerConfig.SERVER.digistoreWirelessTerminalPowerUsage.get(), false);
			return true;
		}
		return false;
	}

	@Override
	public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);

		// If the key is down and we're on the server, open the digitstore terminal so
		// long as the player is NOT in any other UI.
		if (!worldIn.isClientSide && ModKeyBindings.OPEN_PORTABLE_DIGISTORE.wasJustPressed()) {
			if (entityIn instanceof Player) {
				Player player = (Player) entityIn;
				if (player.containerMenu instanceof InventoryMenu) {
					worldIn.playSound(null, entityIn.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.75f, 1.5f);
					openTerminalUI(worldIn, player, stack);
				} else {
					player.closeContainer();
				}
			}
		}
	}

	/**
	 * When right clicked, open the filter UI.
	 */
	@Override
	protected InteractionResultHolder<ItemStack> onStaticPowerItemRightClicked(Level world, Player player, InteractionHand hand, ItemStack item) {
		if (!world.isClientSide && !player.isShiftKeyDown()) {
			if (openTerminalUI(world, player, item)) {
				return InteractionResultHolder.success(item);
			}
		}
		return InteractionResultHolder.pass(item);
	}

	protected boolean openTerminalUI(Level world, Player player, ItemStack item) {
		if (isBound(world, item)) {
			// Check if it has power.
			if (usePower(item)) {
				NetworkGUI.openScreen((ServerPlayer) player, new WirelessDigistoreAccessContainerProvider(item), buff -> {
					buff.writeInt(getTerminalAttachDirection(item).ordinal());
					buff.writeBlockPos(BlockPos.of(item.getTag().getLong(TERMINAL_POSITION_KEY)));
				});
				return true;
			} else {
				player.displayClientMessage(Component.translatable("gui.staticpower.digistore_wireless_terminal_not_enough_power"), true);
				return false;
			}
		} else {
			player.displayClientMessage(Component.translatable("gui.staticpower.digistore_wireless_terminal_not_bound"), true);
			return false;
		}
	}

	protected InteractionResult onStaticPowerItemUsedOnBlock(UseOnContext context, Level world, BlockPos pos, Direction face, Player player, ItemStack item) {
		BlockEntity hitTe = world.getBlockEntity(pos);
		DigistoreCableProviderComponent provider = ComponentUtilities.getComponent(DigistoreCableProviderComponent.class, hitTe).orElse(null);
		if (provider != null) {
			ItemStack attachmentOnSide = provider.getAttachment(face);
			if (!attachmentOnSide.isEmpty() && attachmentOnSide.getItem() instanceof DigistoreCraftingTerminal) {
				CompoundTag itemNBT = item.getOrCreateTag();
				itemNBT.putLong(TERMINAL_POSITION_KEY, pos.asLong());
				itemNBT.putInt(TERMINAL_SIDE_KEY, face.ordinal());
				player.displayClientMessage(Component.translatable("gui.staticpower.digistore_wireless_terminal_bound"), true);
				return InteractionResult.SUCCESS;
			}
		}
		return InteractionResult.PASS;
	}

	@OnlyIn(Dist.CLIENT)
	public void getAdvancedTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip) {
		if (worldIn != null && isBound(worldIn, stack)) {
			tooltip.add(
					Component.translatable("gui.staticpower.digistore_wireless_terminal_advanced_tooltip", BlockPos.of(stack.getTag().getLong(TERMINAL_POSITION_KEY)).toString()));
		}
	}

	public class WirelessDigistoreAccessContainerProvider implements MenuProvider {
		public ItemStack targetItemStack;

		public WirelessDigistoreAccessContainerProvider(ItemStack stack) {
			targetItemStack = stack;
		}

		@Override
		public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
			// Get the item, and open the terminal for that interface.
			DigistoreWirelessTerminal accessItem = (DigistoreWirelessTerminal) targetItemStack.getItem();
			return new ContainerDigistoreCraftingTerminal(windowId, inventory, accessItem.getTerminalAttachment(player.getCommandSenderWorld(), targetItemStack),
					accessItem.getTerminalAttachDirection(targetItemStack), accessItem.getCableProvider(player.getCommandSenderWorld(), targetItemStack));
		}

		@Override
		public Component getDisplayName() {
			return targetItemStack.getHoverName();
		}
	}

	@Override
	public double getCapacity() {
		return StaticPowerConfig.SERVER.digistoreWirelessTerminalPowerCapacity.get();
	}

	@Override
	public StaticVoltageRange getInputVoltageRange() {
		return StaticPowerConfig.getTier(StaticPowerTiers.ADVANCED).powerConfiguration.getPortableBatteryChargingVoltage();
	}

	@Override
	public double getMaximumInputPower() {
		return StaticPowerConfig.getTier(StaticPowerTiers.ADVANCED).powerConfiguration.portableBatteryMaximumPowerInput.get();
	}

	@Override
	public StaticPowerVoltage getOutputVoltage() {
		return StaticPowerConfig.getTier(StaticPowerTiers.ADVANCED).powerConfiguration.portableBatteryOutputVoltage.get();
	}

	@Override
	public double getMaximumOutputPower() {
		return StaticPowerConfig.getTier(StaticPowerTiers.ADVANCED).powerConfiguration.portableBatteryMaximumPowerOutput.get();
	}
}
