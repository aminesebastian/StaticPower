package theking530.staticpower.entities.smeep;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModEntities;
import theking530.staticpower.init.ModItems;

public class EntitySmeep extends Sheep {
	public EntitySmeep(EntityType<? extends Sheep> type, Level worldIn) {
		super(type, worldIn);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0D).add(Attributes.MOVEMENT_SPEED, (double) 0.33F);
	}

	@Override
	public EntitySmeep getBreedOffspring(ServerLevel world, AgeableMob parent) {
		EntitySmeep child = ModEntities.Smeep.getType().create(world);
		return child;
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(3, new TemptGoal(this, 1.1D, Ingredient.of(ModItems.StaticCrop), false));
	}

	@Override
	public ResourceLocation getDefaultLootTable() {
		return getType().getDefaultLootTable();
	}

	@javax.annotation.Nonnull
	@Override
	public java.util.List<ItemStack> onSheared(@Nullable Player player, @javax.annotation.Nonnull ItemStack item, Level world, BlockPos pos, int fortune) {
		world.playSound(null, this, SoundEvents.SHEEP_SHEAR, player == null ? SoundSource.BLOCKS : SoundSource.PLAYERS, 1.0F, 1.0F);
		if (!world.isClientSide) {
			this.setSheared(true);
			int i = 1 + this.random.nextInt(3);

			java.util.List<ItemStack> items = new java.util.ArrayList<>();
			for (int j = 0; j < i; ++j) {
				items.add(new ItemStack(ModBlocks.SmeepWool));
			}
			return items;
		}
		return java.util.Collections.emptyList();
	}

	@Override
	public boolean isFood(ItemStack stack) {
		return stack.getItem() == ModItems.StaticCrop;
	}

}
