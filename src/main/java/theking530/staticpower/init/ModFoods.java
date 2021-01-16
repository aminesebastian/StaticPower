package theking530.staticpower.init;

import net.minecraft.item.Food;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

public class ModFoods {
	public static final Food STATIC_CROP = (new Food.Builder()).hunger(3).saturation(0.3F).setAlwaysEdible().effect(() -> new EffectInstance(Effects.SPEED, 80, 2), 1.0f).build();
	public static final Food ENERGIZED_CROP = (new Food.Builder()).hunger(6).saturation(0.5F).setAlwaysEdible().effect(() -> new EffectInstance(Effects.JUMP_BOOST, 80, 2), 1.0f).build();
	public static final Food LUMUM_CROP = (new Food.Builder()).hunger(9).saturation(0.9F).setAlwaysEdible().effect(() -> new EffectInstance(Effects.REGENERATION, 80, 2), 1.0f).build();
	public static final Food STATIC_PIE = (new Food.Builder()).hunger(8).saturation(0.6F).build();
	public static final Food ENERGIZED_PIE = (new Food.Builder()).hunger(16).saturation(0.9F).build();
	public static final Food LUMUM_PIE = (new Food.Builder()).hunger(32).saturation(1.2F).build();
	public static final Food SMUTTON = (new Food.Builder()).hunger(4).saturation(0.4F).meat().build();
	public static final Food COOKED_SMUTTON = (new Food.Builder()).hunger(10).saturation(1.0F).meat().build();
	public static final Food EEEF = (new Food.Builder()).hunger(5).saturation(0.45F).meat().build();
	public static final Food COOKED_EEEF = (new Food.Builder()).hunger(12).saturation(1.2F).meat().build();
}
