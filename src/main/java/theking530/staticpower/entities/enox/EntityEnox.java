package theking530.staticpower.entities.enox;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import theking530.staticpower.init.ModEntities;
import theking530.staticpower.init.ModItems;

public class EntityEnox extends Cow {
	public EntityEnox(EntityType<? extends Cow> type, Level worldIn) {
		super(type, worldIn);
	}

	public AttributeMap getAttributes() {
		return new AttributeMap(Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 16.0D).add(Attributes.MOVEMENT_SPEED, (double) 0.30F).build());
	}

	@Override
	public EntityEnox getBreedOffspring(ServerLevel world, AgeableMob parent) {
		EntityEnox child = ModEntities.Enox.getType().create(world);
		return child;
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(3, new TemptGoal(this, 1.1D, Ingredient.of(ModItems.EnergizedCrop), false));
	}

	@Override
	public boolean isFood(ItemStack stack) {
		return stack.getItem() == ModItems.EnergizedCrop;
	}

}
