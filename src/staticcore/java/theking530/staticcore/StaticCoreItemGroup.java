package theking530.staticcore;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.init.StaticCoreItems;

public class StaticCoreItemGroup extends CreativeModeTab {
	private static final StaticCoreItemGroup INSTANCE = new StaticCoreItemGroup();

	private StaticCoreItemGroup() {
		super("staticcore.itemgroup");
	}

	public static StaticCoreItemGroup instance() {
		return INSTANCE;
	}

	@OnlyIn(Dist.CLIENT)
	public ItemStack makeIcon() {
		return new ItemStack(StaticCoreItems.ResearchTier1.get());
	}
}