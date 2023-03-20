package theking530.staticpower.itemgroup;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.data.StaticCoreTiers;
import theking530.staticpower.init.ModBlocks;

public class StaticPowerCableItemGroup extends CreativeModeTab {
	public StaticPowerCableItemGroup() {
		super("staticpower.cables");
	}

	@OnlyIn(Dist.CLIENT)
	public ItemStack makeIcon() {
		return new ItemStack(ModBlocks.IndustrialPowerCables.get(StaticCoreTiers.BASIC).get());
	}
}