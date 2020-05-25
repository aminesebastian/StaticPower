package theking530.staticpower;

import java.util.HashSet;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import theking530.staticpower.utilities.Reference;

/**
 * Main registry class responsible for preparing entites for registration.
 * 
 * @author Amine Sebastian
 *
 */
@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class Registry {
	private static final HashSet<Item> ITEMS = new HashSet<>();
	private static final HashSet<Block> BLOCKS = new HashSet<>();

	/**
	 * Pre-registers an item for registration through the registry event.
	 * 
	 * @param item The item to pre-register.
	 * @return The item that was passed.
	 */
	public Item PreRegisterItem(Item item) {
		ITEMS.add(item);
		return item;
	}

	/**
	 * Pre-registers a block for initialization through the registry event.
	 * 
	 * @param block The block to pre-register.
	 * @return The block that was passed.
	 */
	public Block PreRegisterBlock(Block block) {
		BLOCKS.add(block);
		return block;
	}

	@SubscribeEvent
	public static void onRegisterItems(RegistryEvent.Register<Item> event) {
		for (Item item : ITEMS) {
			event.getRegistry().register(item);
		}
	}

}
