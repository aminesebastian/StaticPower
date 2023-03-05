package theking530.staticpower.itemgroup;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticpower.data.materials.MaterialTypes;
import theking530.staticpower.init.NewModMaterials;

public class StaticPowerMaterialItemGroup extends CreativeModeTab {
	public StaticPowerMaterialItemGroup() {
		super("staticpower.materials");
	}

	@OnlyIn(Dist.CLIENT)
	public ItemStack makeIcon() {
		return new ItemStack(NewModMaterials.ENERGIZED_METAL.get(MaterialTypes.INGOT).get());
	}
}