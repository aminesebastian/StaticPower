package theking530.staticpower.blockentities.nonpowered.randomitem;

import java.util.stream.Collectors;

import net.minecraft.core.BlockPos;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryManager;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticcore.utilities.SDMath;
import theking530.staticpower.blockentities.BlockEntityConfigurable;
import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.blockentities.components.items.InventoryComponent;
import theking530.staticpower.blockentities.components.items.OutputServoComponent;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.utilities.InventoryUtilities;

public class BlockEntityRandomItemGenerator extends BlockEntityConfigurable implements MenuProvider {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityRandomItemGenerator> TYPE = new BlockEntityTypeAllocator<>(
			(type, pos, state) -> new BlockEntityRandomItemGenerator(pos, state), ModBlocks.RandomItemGenerator);
	private static final float GENERATION_RATE = 1;
	public final InventoryComponent inventory;
	private float timer;

	public BlockEntityRandomItemGenerator(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);
		registerComponent(inventory = new InventoryComponent("Inventory", 30, MachineSideMode.Output).setShiftClickEnabled(true));
		registerComponent(new OutputServoComponent("OutputServo", 2, inventory));
	}

	@Override
	public void process() {
		// Handle the upgrade tick on the server.
		if (!level.isClientSide) {
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
			IForgeRegistry<Item> items = RegistryManager.ACTIVE.getRegistry(Item.class);
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
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerRandomItemGenerator(windowId, inventory, this);
	}
}
