package theking530.staticpower.blockentities.nonpowered.randomitem;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import theking530.staticcore.blockentity.BlockEntityBase;
import theking530.staticcore.blockentity.components.control.RedstoneControlComponent;
import theking530.staticcore.blockentity.components.control.redstonecontrol.RedstoneMode;
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.items.InventoryComponent;
import theking530.staticcore.blockentity.components.items.OutputServoComponent;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticcore.utilities.item.InventoryUtilities;
import theking530.staticcore.utilities.math.SDMath;
import theking530.staticpower.init.ModBlocks;

public class BlockEntityRandomItemGenerator extends BlockEntityBase implements MenuProvider {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityRandomItemGenerator> ANY_ITEM = new BlockEntityTypeAllocator<>("random_item_generator",
			(type, pos, state) -> new BlockEntityRandomItemGenerator(type, pos, state, null), ModBlocks.RandomItemGenerator);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityRandomItemGenerator> ORE = new BlockEntityTypeAllocator<>("random_ore_generator",
			(type, pos, state) -> new BlockEntityRandomItemGenerator(type, pos, state, Tags.Items.ORES), ModBlocks.RandomOreGenerator);

	private static final float GENERATION_RATE = 4;

	public final RedstoneControlComponent redstoneControlComponent;
	public final InventoryComponent inventory;

	private final TagKey<Item> itemTag;
	private float timer;

	public BlockEntityRandomItemGenerator(BlockEntityTypeAllocator<BlockEntityRandomItemGenerator> type, BlockPos pos, BlockState state, TagKey<Item> itemTag) {
		super(type, pos, state);
		this.itemTag = itemTag;
		registerComponent(redstoneControlComponent = new RedstoneControlComponent("RedstoneControlComponent", RedstoneMode.Ignore));
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
			if (itemTag != null) {
				ItemStack[] items = Ingredient.of(itemTag).getItems();
				int randomIndex = SDMath.getRandomIntInRange(0, items.length - 1);
				ItemStack stack = items[randomIndex].copy();
				stack.setCount(stack.getMaxStackSize());
				InventoryUtilities.insertItemIntoInventory(inventory, stack, false);
			} else {
				int randomIndex = SDMath.getRandomIntInRange(0, ForgeRegistries.ITEMS.getValues().size() - 1);
				Item item = ForgeRegistries.ITEMS.getValues().stream().toList().get(randomIndex);
				ItemStack stack = new ItemStack(item);
				stack.setCount(stack.getMaxStackSize());
				InventoryUtilities.insertItemIntoInventory(inventory, stack, false);
			}
		} catch (Exception e) {
		}
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerRandomItemGenerator(windowId, inventory, this);
	}
}
