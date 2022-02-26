package theking530.staticpower.tileentities.powered.solidgenerator;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.container.StaticPowerTileEntityContainer;
import theking530.staticpower.container.slots.StaticPowerContainerSlot;
import theking530.staticpower.container.slots.UpgradeItemSlot;

public class ContainerSolidGenerator extends StaticPowerTileEntityContainer<TileEntitySolidGenerator> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerSolidGenerator, GuiSolidGenerator> TYPE = new ContainerTypeAllocator<>("machine_solid_generator", ContainerSolidGenerator::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiSolidGenerator::new);
		}
	}

	public ContainerSolidGenerator(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (TileEntitySolidGenerator) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerSolidGenerator(int windowId, Inventory playerInventory, TileEntitySolidGenerator owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		// Input
		this.addSlot(new StaticPowerContainerSlot(getTileEntity().inputInventory, 0, 80, 32));

		// Upgrades
		this.addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 0, 152, 16));
		this.addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 1, 152, 36));
		this.addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 2, 152, 56));

		addAllPlayerSlots();
	}

	@Override
	protected boolean playerItemShiftClicked(ItemStack stack, Player player, Slot slot, int slotIndex) {
		if (!moveItemStackTo(stack, 0, 1, false)) {
			return true;
		}
		return false;
	}
}
