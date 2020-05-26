package theking530.staticpower.initialization;

import net.minecraft.item.Food;

public class ModFoods {
	   public static final Food STATIC_CROP = (new Food.Builder()).hunger(4).saturation(0.3F).setAlwaysEdible().build();
	   public static final Food ENERGIZED_CROP = (new Food.Builder()).hunger(8).saturation(0.5F).setAlwaysEdible().build();
	   public static final Food LUMUM_CROP = (new Food.Builder()).hunger(16).saturation(0.9F).setAlwaysEdible().build();
}
