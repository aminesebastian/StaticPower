package theking530.staticcore.init;

import java.util.function.Supplier;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import theking530.staticcore.StaticCore;
import theking530.staticcore.item.ResearchItem;
import theking530.staticcore.utilities.MinecraftColor;

public class StaticCoreItems {

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
			StaticCore.MOD_ID);

	public static final RegistryObject<ResearchItem> ResearchTier1 = ITEMS.register("research_tier_1",
			() -> new ResearchItem(MinecraftColor.RED.getColor(), 1));
	public static final RegistryObject<ResearchItem> ResearchTier2 = ITEMS.register("research_tier_2",
			() -> new ResearchItem(MinecraftColor.WHITE.getColor(), 2));
	public static final RegistryObject<ResearchItem> ResearchTier3 = ITEMS.register("research_tier_3",
			() -> new ResearchItem(MinecraftColor.YELLOW.getColor(), 3));
	public static final RegistryObject<ResearchItem> ResearchTier4 = ITEMS.register("research_tier_4",
			() -> new ResearchItem(MinecraftColor.LIME.getColor(), 4));
	public static final RegistryObject<ResearchItem> ResearchTier5 = ITEMS.register("research_tier_5",
			() -> new ResearchItem(MinecraftColor.CYAN.getColor(), 5));
	public static final RegistryObject<ResearchItem> ResearchTier6 = ITEMS.register("research_tier_6",
			() -> new ResearchItem(MinecraftColor.MAGENTA.getColor(), 6));
	public static final RegistryObject<ResearchItem> ResearchTier7 = ITEMS.register("research_tier_7",
			() -> new ResearchItem(MinecraftColor.BLACK.getColor().copy().lighten(0.1f, 0.1f, 0.1f, 0.0f), 7));

	public static void init(IEventBus eventBus) {
		ITEMS.register(eventBus);
	}

	public static <T extends Item> RegistryObject<T> registerItem(String name, Supplier<T> block) {
		return ITEMS.register(name, block);
	}
}
