package theking530.staticpower.tileentities.nonpowered.randomitem;

import java.util.stream.Collectors;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import theking530.staticcore.initialization.tileentity.TileEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticcore.utilities.SDMath;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.TileEntityConfigurable;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.components.items.InventoryComponent;
import theking530.staticpower.tileentities.components.items.OutputServoComponent;
import theking530.staticpower.utilities.InventoryUtilities;

public class TileEntityRandomItemGenerator extends TileEntityConfigurable implements INamedContainerProvider {
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityRandomItemGenerator> TYPE = new TileEntityTypeAllocator<>((type) -> new TileEntityRandomItemGenerator(),
			ModBlocks.RandomItemGenerator);
	private static final float GENERATION_RATE = 1;
	public final InventoryComponent inventory;
	private float timer;

	public TileEntityRandomItemGenerator() {
		super(TYPE);
		registerComponent(inventory = new InventoryComponent("Inventory", 30, MachineSideMode.Output).setShiftClickEnabled(true));
		registerComponent(new OutputServoComponent("OutputServo", 2, inventory));
	}

	@Override
	public void process() {
		// Handle the upgrade tick on the server.
		if (!world.isRemote) {
			timer++;
			if (timer >= GENERATION_RATE) {
				generateItem();
				timer = 0;
			}
		}
	}

	private void generateItem() {
		try {
			// Pick a random item.
			IForgeRegistry<Item> items = GameRegistry.findRegistry(Item.class);
			int randomIndex = SDMath.getRandomIntInRange(0, items.getValues().size() - 1);
			Item item = items.getValues().stream().collect(Collectors.toList()).get(randomIndex);

			// Make the stack and set its stack size to the max.
			ItemStack stack = new ItemStack(item);
			stack.setCount(stack.getMaxStackSize());

			// Insert it into the inventory.
			InventoryUtilities.insertItemIntoInventory(inventory, stack, false);
		} catch (Exception e) {
		}
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerRandomItemGenerator(windowId, inventory, this);
	}
}
