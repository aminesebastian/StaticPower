package theking530.staticpower.items.tools.chainsaw;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.api.item.compound.capability.CapabilityCompoundItem;
import theking530.api.item.compound.capability.ICompoundItem;
import theking530.staticcore.container.StaticPowerItemContainer;
import theking530.staticcore.container.slots.CompoundItemPartSlot;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;

public class ContainerChainsaw extends StaticPowerItemContainer<Chainsaw> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerChainsaw, GuiChainsaw> TYPE = new ContainerTypeAllocator<>("chainsaw", ContainerChainsaw::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiChainsaw::new);
		}
	}

	private ICompoundItem compoundItem;

	public ContainerChainsaw(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, getHeldItemstack(inv, data));
	}

	public ContainerChainsaw(int windowId, Inventory playerInventory, ItemStack owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		compoundItem = getItemStack().getCapability(CapabilityCompoundItem.CAPABILITY_COMPOUND_ITEM).orElse(null);
		addSlot(new CompoundItemPartSlot(compoundItem, 0, 0, 80, 24));
		addAllPlayerSlots();
	}

	@Override
	public boolean canDragTo(Slot slot) {
		return false;
	}

	@Override
	protected boolean playerItemShiftClicked(ItemStack stack, Player player, Slot slot, int slotIndex) {
		if (slotIndex != 0 && moveItemStackTo(stack, 0, 1, false)) {
			return true;
		}
		return false;
	}
}