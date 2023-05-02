package theking530.staticpower.init;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class ModFoods {
	public static final FoodProperties STATIC_FRUIT = (new FoodProperties.Builder()).nutrition(3).saturationMod(0.3F)
			.alwaysEat().effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 80, 2), 1.0f).build();
	public static final FoodProperties ENERGIZED_FRUIT = (new FoodProperties.Builder()).nutrition(6).saturationMod(0.5F)
			.alwaysEat().effect(() -> new MobEffectInstance(MobEffects.JUMP, 80, 2), 1.0f).build();
	public static final FoodProperties LUMUM_FRUIT = (new FoodProperties.Builder()).nutrition(9).saturationMod(0.9F)
			.alwaysEat().effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 80, 2), 1.0f).build();
	public static final FoodProperties STATIC_PIE = (new FoodProperties.Builder()).nutrition(8).saturationMod(0.6F)
			.build();
	public static final FoodProperties ENERGIZED_PIE = (new FoodProperties.Builder()).nutrition(16).saturationMod(0.9F)
			.build();
	public static final FoodProperties LUMUM_PIE = (new FoodProperties.Builder()).nutrition(32).saturationMod(1.2F)
			.build();
	public static final FoodProperties SMUTTON = (new FoodProperties.Builder()).nutrition(4).saturationMod(0.4F).meat()
			.build();
	public static final FoodProperties COOKED_SMUTTON = (new FoodProperties.Builder()).nutrition(10).saturationMod(1.0F)
			.meat().build();
	public static final FoodProperties EEEF = (new FoodProperties.Builder()).nutrition(5).saturationMod(0.45F).meat()
			.build();
	public static final FoodProperties COOKED_EEEF = (new FoodProperties.Builder()).nutrition(12).saturationMod(1.2F)
			.meat().build();
}
