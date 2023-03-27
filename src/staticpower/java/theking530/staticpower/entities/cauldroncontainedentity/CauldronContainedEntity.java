package theking530.staticpower.entities.cauldroncontainedentity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkHooks;
import theking530.staticcore.utilities.math.SDMath;
import theking530.staticpower.blockentities.nonpowered.cauldron.BlockCauldron;
import theking530.staticpower.blockentities.nonpowered.cauldron.BlockEntityCauldron;
import theking530.staticpower.init.ModEntities;

public class CauldronContainedEntity extends ItemEntity {
	private static final EntityDataAccessor<Integer> TOTAL_COOKING_TIME = SynchedEntityData.defineId(CauldronContainedEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> CURRENT_COOKING_TIME = SynchedEntityData.defineId(CauldronContainedEntity.class, EntityDataSerializers.INT);

	public CauldronContainedEntity(EntityType<? extends CauldronContainedEntity> p_i50217_1_, Level world) {
		super(p_i50217_1_, world);
	}

	public CauldronContainedEntity(Level worldIn, double x, double y, double z) {
		this(ModEntities.CauldronContainedEntity.getType(), worldIn);
		this.setPos(x, y, z);
		this.setYRot(this.random.nextFloat() * 360.0F);
		this.setDeltaMovement(this.random.nextDouble() * 0.2D - 0.1D, 0.2D, this.random.nextDouble() * 0.2D - 0.1D);
	}

	public CauldronContainedEntity(Level worldIn, double x, double y, double z, ItemStack stack, int cookingTime) {
		this(ModEntities.CauldronContainedEntity.getType(), worldIn);
		this.setPos(x, y, z);
		this.setYRot(this.random.nextFloat() * 360.0F);
		this.setDeltaMovement(this.random.nextDouble() * 0.2D - 0.1D, 0.2D, this.random.nextDouble() * 0.2D - 0.1D);
		this.setItem(stack);
		this.lifespan = (stack.getItem() == null ? 6000 : stack.getEntityLifespan(worldIn));
		this.getEntityData().set(TOTAL_COOKING_TIME, cookingTime);
	}

	@Override
	public void tick() {
		// We have to do this trick to prevent these entities from combining together
		// since all the methods for handling this are private. We set the stack size to
		// make so it cannot be combined, then we set it back to the original after the
		// super call.
		int originalStackSize = getItem().getCount();
		getItem().setCount(getItem().getMaxStackSize());
		super.tick();
		getItem().setCount(originalStackSize);

		// Only perfrom the following on the server.
		if (getCommandSenderWorld().isClientSide) {
			return;
		}

		// Render the splash particles every few ticks.
		if (SDMath.diceRoll(0.25)) {
			float randomOffset = this.getCommandSenderWorld().getRandom().nextFloat();
			randomOffset *= 2;
			randomOffset -= 1;
			randomOffset *= 0.0;

			((ServerLevel) getCommandSenderWorld()).sendParticles(ParticleTypes.SPLASH, getX() + randomOffset, getY() + 0.8, getZ() + randomOffset, 1, 0.0D, 0.0D, 0.0D, 0.0D);
			if (SDMath.diceRoll(0.6)) {
				((ServerLevel) getCommandSenderWorld()).sendParticles(ParticleTypes.BUBBLE_COLUMN_UP, getX() + randomOffset, getY() + 0.9, getZ() + randomOffset, 1, 0.0D, 1.0D,
						0.0D, 1.0D);
			}
			if (SDMath.diceRoll(0.15)) {
				getCommandSenderWorld().playSound(null, this.blockPosition(), SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundSource.BLOCKS, 0.5f, 0.75f);
			}
		}

		// Return back to regular item if this flag is true.
		boolean convertBackToItem = false;

		// Get the current blockstate and check if it is a cauldron.
		BlockState currentBlockState = getCommandSenderWorld().getBlockState(this.blockPosition());
		if (currentBlockState.getBlock() instanceof BlockCauldron) {
			// Get the tile entity and confirm it is a cauldron.
			BlockEntity te = getCommandSenderWorld().getBlockEntity(blockPosition());
			if (te instanceof BlockEntityCauldron) {
				// Get the tile entity for the cauldron.
				BlockEntityCauldron cauldron = (BlockEntityCauldron) te;
				if (cauldron.getRecipe(getItem()).isPresent()) {
					// See if we aged enough.
					if (getEntityData().get(CURRENT_COOKING_TIME) >= getEntityData().get(TOTAL_COOKING_TIME)) {
						// Reset the time spent cooking.
						getEntityData().set(CURRENT_COOKING_TIME, 0);
						// Attempt to finalize the craft and see how many we can actually craft.
						int craftedAmount = cauldron.completeCooking(this, getItem());

						// If we crafted at least 1, render the effects and modify the stack size of
						// this entity.
						if (craftedAmount > 0) {
							getCommandSenderWorld().playSound(null, this.blockPosition(), SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 0.5f, 1.0f);
							((ServerLevel) getCommandSenderWorld()).sendParticles(ParticleTypes.SPLASH, getX(), getY() + 0.8, getZ(), 10, 0.0D, 0.0D, 0.0D, 0.0D);

							// Lower the count of item. If the count hits 0, remove the entity.
							getItem().shrink(craftedAmount);
							if (getItem().getCount() == 0) {
								remove(RemovalReason.DISCARDED);
							}
						}
					} else {
						getEntityData().set(CURRENT_COOKING_TIME, getEntityData().get(CURRENT_COOKING_TIME) + 1);
					}
				} else {
					convertBackToItem = true; // This means the cauldron's state has changed and can no longer craft this
												// recipe.
				}
			}
		} else {
			convertBackToItem = true; // This means the cauldron is gone.
		}

		// Convert back to a regular item if requested by the above flag. This could
		// happen if the cauldron is no longer filled with the recipe liquid, or broken.
		if (convertBackToItem) {
			// Replace with standard item.
			ItemEntity regularItem = new ItemEntity(this.getCommandSenderWorld(), this.getX(), this.getY(), this.getZ(), getItem().copy());
			getCommandSenderWorld().addFreshEntity(regularItem);

			// Remove ourselves
			remove(RemovalReason.DISCARDED);
		}
	}

	@Override
	public void playerTouch(Player entityIn) {
		// If the player is crouching, let them pick it up.
		if (!this.level.isClientSide && entityIn.isShiftKeyDown()) {
			this.setPickUpDelay(0);
		}
		super.playerTouch(entityIn);
	}

	@Override
	public boolean hurt(DamageSource p_32013_, float p_32014_) {
		return false;
	}

	public void setCookTime(int time) {
		this.getEntityData().set(TOTAL_COOKING_TIME, time);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("total_cooking_Time", entityData.get(TOTAL_COOKING_TIME));
		compound.putInt("current_cooking_Time", entityData.get(CURRENT_COOKING_TIME));

	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		getEntityData().set(TOTAL_COOKING_TIME, compound.getInt("total_cooking_Time"));
		getEntityData().set(CURRENT_COOKING_TIME, compound.getInt("current_cooking_Time"));
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		getEntityData().define(TOTAL_COOKING_TIME, 0);
		getEntityData().define(CURRENT_COOKING_TIME, 0);
	}

	@Override
	public Packet<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}
