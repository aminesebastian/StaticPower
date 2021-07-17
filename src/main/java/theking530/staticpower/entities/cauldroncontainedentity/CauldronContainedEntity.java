package theking530.staticpower.entities.cauldroncontainedentity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;
import theking530.staticcore.utilities.SDMath;
import theking530.staticpower.init.ModEntities;
import theking530.staticpower.tileentities.nonpowered.cauldron.BlockCauldron;
import theking530.staticpower.tileentities.nonpowered.cauldron.TileEntityCauldron;

public class CauldronContainedEntity extends ItemEntity {
	private static final DataParameter<Integer> TOTAL_COOKING_TIME = EntityDataManager.createKey(CauldronContainedEntity.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> CURRENT_COOKING_TIME = EntityDataManager.createKey(CauldronContainedEntity.class, DataSerializers.VARINT);

	public CauldronContainedEntity(EntityType<? extends CauldronContainedEntity> p_i50217_1_, World world) {
		super(p_i50217_1_, world);
	}

	public CauldronContainedEntity(World worldIn, double x, double y, double z) {
		this(ModEntities.CauldronContainedEntity.getType(), worldIn);
		this.setPosition(x, y, z);
		this.rotationYaw = this.rand.nextFloat() * 360.0F;
		this.setMotion(this.rand.nextDouble() * 0.2D - 0.1D, 0.2D, this.rand.nextDouble() * 0.2D - 0.1D);
	}

	public CauldronContainedEntity(World worldIn, double x, double y, double z, ItemStack stack, int cookingTime) {
		this(ModEntities.CauldronContainedEntity.getType(), worldIn);
		this.setPosition(x, y, z);
		this.rotationYaw = this.rand.nextFloat() * 360.0F;
		this.setMotion(this.rand.nextDouble() * 0.2D - 0.1D, 0.2D, this.rand.nextDouble() * 0.2D - 0.1D);
		this.setItem(stack);
		this.lifespan = (stack.getItem() == null ? 6000 : stack.getEntityLifespan(worldIn));
		this.getDataManager().set(TOTAL_COOKING_TIME, cookingTime);
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
		if (getEntityWorld().isRemote) {
			return;
		}

		// Render the splash particles every few ticks.
		if (SDMath.diceRoll(0.25)) {
			float randomOffset = this.getEntityWorld().getRandom().nextFloat();
			randomOffset *= 2;
			randomOffset -= 1;
			randomOffset *= 0.0;

			((ServerWorld) getEntityWorld()).spawnParticle(ParticleTypes.SPLASH, getPosX() + randomOffset, getPosY() + 0.8, getPosZ() + randomOffset, 1, 0.0D, 0.0D, 0.0D, 0.0D);
			if (SDMath.diceRoll(0.6)) {
				((ServerWorld) getEntityWorld()).spawnParticle(ParticleTypes.BUBBLE_COLUMN_UP, getPosX() + randomOffset, getPosY() + 0.9, getPosZ() + randomOffset, 1, 0.0D, 1.0D, 0.0D,
						1.0D);
			}
			if (SDMath.diceRoll(0.15)) {
				getEntityWorld().playSound(null, this.getPosition(), SoundEvents.BLOCK_BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundCategory.BLOCKS, 0.5f, 0.75f);
			}
		}

		// Return back to regular item if this flag is true.
		boolean convertBackToItem = false;

		// Get the current blockstate and check if it is a cauldron.
		BlockState currentBlockState = getEntityWorld().getBlockState(this.getPosition());
		if (currentBlockState.getBlock() instanceof BlockCauldron) {
			// Get the tile entity and confirm it is a cauldron.
			TileEntity te = getEntityWorld().getTileEntity(getPosition());
			if (te instanceof TileEntityCauldron) {
				// Get the tile entity for the cauldron.
				TileEntityCauldron cauldron = (TileEntityCauldron) te;
				if (cauldron.getRecipe(getItem()).isPresent()) {
					// See if we aged enough.
					if (getDataManager().get(CURRENT_COOKING_TIME) >= getDataManager().get(TOTAL_COOKING_TIME)) {
						// Reset the time spent cooking.
						getDataManager().set(CURRENT_COOKING_TIME, 0);
						// Attempt to finalize the craft and see how many we can actually craft.
						int craftedAmount = cauldron.completeCooking(this, getItem());

						// If we crafted at least 1, render the effects and modify the stack size of
						// this entity.
						if (craftedAmount > 0) {
							getEntityWorld().playSound(null, this.getPosition(), SoundEvents.ENTITY_GENERIC_SPLASH, SoundCategory.BLOCKS, 0.5f, 1.0f);
							((ServerWorld) getEntityWorld()).spawnParticle(ParticleTypes.SPLASH, getPosX(), getPosY() + 0.8, getPosZ(), 10, 0.0D, 0.0D, 0.0D, 0.0D);

							// Lower the count of item. If the count hits 0, remove the entity.
							getItem().shrink(craftedAmount);
							if (getItem().getCount() == 0) {
								remove();
							}
						}
					} else {
						getDataManager().set(CURRENT_COOKING_TIME, getDataManager().get(CURRENT_COOKING_TIME) + 1);
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
			ItemEntity regularItem = new ItemEntity(this.getEntityWorld(), this.getPosX(), this.getPosY(), this.getPosZ(), getItem().copy());
			getEntityWorld().addEntity(regularItem);

			// Remove ourselves
			remove();
		}
	}

	@Override
	public void onCollideWithPlayer(PlayerEntity entityIn) {
		// If the player is crouching, let them pick it up.
		if (!this.world.isRemote && entityIn.isSneaking()) {
			this.setPickupDelay(0);
		}
		super.onCollideWithPlayer(entityIn);
	}

	public void setCookTime(int time) {
		this.getDataManager().set(TOTAL_COOKING_TIME, time);
	}

	@Override
	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);
		compound.putInt("total_cooking_Time", dataManager.get(TOTAL_COOKING_TIME));
		compound.putInt("current_cooking_Time", dataManager.get(CURRENT_COOKING_TIME));

	}

	@Override
	public void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);
		getDataManager().set(TOTAL_COOKING_TIME, compound.getInt("total_cooking_Time"));
		getDataManager().set(CURRENT_COOKING_TIME, compound.getInt("current_cooking_Time"));
	}

	@Override
	protected void registerData() {
		super.registerData();
		getDataManager().register(TOTAL_COOKING_TIME, 0);
		getDataManager().register(CURRENT_COOKING_TIME, 0);
	}

	@Override
	public IPacket<?> createSpawnPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}
