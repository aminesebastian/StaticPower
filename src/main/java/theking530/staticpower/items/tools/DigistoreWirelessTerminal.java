package theking530.staticpower.items.tools;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.network.NetworkGUI;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.attachments.digistore.craftingterminal.ContainerDigistoreCraftingTerminal;
import theking530.staticpower.cables.attachments.digistore.craftingterminal.DigistoreCraftingTerminal;
import theking530.staticpower.cables.digistore.DigistoreCableProviderComponent;
import theking530.staticpower.items.StaticPowerItem;
import theking530.staticpower.tileentities.components.ComponentUtilities;

public class DigistoreWirelessTerminal extends StaticPowerItem {
	private static final String TERMINAL_POSITION_KEY = "terminal_position";
	private static final String TERMINAL_SIDE_KEY = "terminal_side";

	public DigistoreWirelessTerminal(String name) {
		super(name, new Properties().maxStackSize(1));
	}

	public boolean isBound(World world, ItemStack wirelessDevice) {
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

	public AbstractCableProviderComponent getCableProvider(World world, ItemStack wirelessDevice) {
		// Check if we have a tag, if not, not bound.
		if (!wirelessDevice.hasTag()) {
			return null;
		}

		// Get the terminal's position.
		BlockPos terminalPos = BlockPos.fromLong(wirelessDevice.getTag().getLong(TERMINAL_POSITION_KEY));
		TileEntity te = world.getTileEntity(terminalPos);
		return ComponentUtilities.getComponent(DigistoreCableProviderComponent.class, te).orElse(null);
	}

	public ItemStack getTerminalAttachment(World world, ItemStack wirelessDevice) {
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

	/**
	 * When right clicked, open the filter UI.
	 */
	@Override
	protected ActionResult<ItemStack> onStaticPowerItemRightClicked(World world, PlayerEntity player, Hand hand, ItemStack item) {
		if (!world.isRemote && !player.isSneaking()) {
			if (isBound(world, item)) {
				NetworkGUI.openGui((ServerPlayerEntity) player, new WirelessDigistoreAccessContainerProvider(item), buff -> {
					buff.writeInt(getTerminalAttachDirection(item).ordinal());
					buff.writeBlockPos(BlockPos.fromLong(item.getTag().getLong(TERMINAL_POSITION_KEY)));
				});
				return ActionResult.resultSuccess(item);
			} else {
				player.sendStatusMessage(new TranslationTextComponent("gui.staticpower.digistore_wireless_terminal_not_bound"), true);
				return ActionResult.resultPass(item);
			}
		}
		return ActionResult.resultPass(item);
	}

	protected ActionResultType onStaticPowerItemUsedOnBlock(ItemUseContext context, World world, BlockPos pos, Direction face, PlayerEntity player, ItemStack item) {
		TileEntity hitTe = world.getTileEntity(pos);
		DigistoreCableProviderComponent provider = ComponentUtilities.getComponent(DigistoreCableProviderComponent.class, hitTe).orElse(null);
		if (provider != null) {
			ItemStack attachmentOnSide = provider.getAttachment(face);
			if (!attachmentOnSide.isEmpty() && attachmentOnSide.getItem() instanceof DigistoreCraftingTerminal) {
				CompoundNBT itemNBT = item.getOrCreateTag();
				itemNBT.putLong(TERMINAL_POSITION_KEY, pos.toLong());
				itemNBT.putInt(TERMINAL_SIDE_KEY, face.ordinal());
				player.sendStatusMessage(new TranslationTextComponent("gui.staticpower.digistore_wireless_terminal_bound"), true);
				return ActionResultType.SUCCESS;
			}
		}
		return ActionResultType.PASS;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, boolean showAdvanced) {

	}

	public class WirelessDigistoreAccessContainerProvider implements INamedContainerProvider {
		public ItemStack targetItemStack;

		public WirelessDigistoreAccessContainerProvider(ItemStack stack) {
			targetItemStack = stack;
		}

		@Override
		public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
			// Get the item, and open the terminal for that interface.
			DigistoreWirelessTerminal accessItem = (DigistoreWirelessTerminal) targetItemStack.getItem();
			return new ContainerDigistoreCraftingTerminal(windowId, inventory, accessItem.getTerminalAttachment(player.getEntityWorld(), targetItemStack),
					accessItem.getTerminalAttachDirection(targetItemStack), accessItem.getCableProvider(player.getEntityWorld(), targetItemStack));
		}

		@Override
		public ITextComponent getDisplayName() {
			return targetItemStack.getDisplayName();
		}
	}
}
