package theking530.staticpower.entities.enox;

import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import theking530.staticpower.init.ModEntities;
import theking530.staticpower.init.ModItems;

public class EntityEnox extends CowEntity {
	public EntityEnox(EntityType<? extends CowEntity> type, World worldIn) {
		super(type, worldIn);
	}

	public static AttributeModifierMap.MutableAttribute getAttributes() {
		return MobEntity.func_233666_p_().createMutableAttribute(Attributes.MAX_HEALTH, 16.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, (double) 0.30F);
	}

	@Override
	public EntityEnox func_241840_a(ServerWorld world, AgeableEntity parent) {
		EntityEnox child = ModEntities.Enox.getType().create(world);
		return child;
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(3, new TemptGoal(this, 1.1D, Ingredient.fromItems(ModItems.EnergizedCrop), false));
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return stack.getItem() == ModItems.EnergizedCrop;
	}

}
