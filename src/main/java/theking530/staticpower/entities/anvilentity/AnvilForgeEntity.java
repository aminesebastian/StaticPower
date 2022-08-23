package theking530.staticpower.entities.anvilentity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import theking530.staticcore.utilities.SDMath;
import theking530.staticpower.init.ModEntities;
import theking530.staticpower.items.HeatedIngot;

public class AnvilForgeEntity extends ItemEntity {
	private boolean isOnAnvil;

	public AnvilForgeEntity(EntityType<? extends AnvilForgeEntity> p_i50217_1_, Level world) {
		super(p_i50217_1_, world);
		isOnAnvil = false;
		this.setPickUpDelay(20);
	}

	public AnvilForgeEntity(Level worldIn, double x, double y, double z) {
		this(ModEntities.AnvilForgeEntity.getType(), worldIn);
		this.setPos(x, y, z);
		this.setYRot(this.random.nextFloat() * 360.0F);
	}

	public AnvilForgeEntity(Level worldIn, double x, double y, double z, ItemStack stack) {
		this(ModEntities.AnvilForgeEntity.getType(), worldIn);
		this.setPos(x, y, z);
		this.setYRot(this.random.nextFloat() * 360.0F);
		this.setItem(stack);
		this.lifespan = (stack.getItem() == null ? 6000 : stack.getEntityLifespan(worldIn));
		lifespan *= 10;
	}

	@Override
	public void tick() {
		super.tick();

		// If the block its in OR the block below it is an anvil, snap to it.
		if (getLevel().getBlockState(this.getOnPos().below()).getBlock() == Blocks.ANVIL) {
			setDeltaMovement(new Vec3(0, 0, 0));
			setPos(getOnPos().getX() + 0.5, getOnPos().getY(), getOnPos().getZ() + 0.5);
			isOnAnvil = true;
		} else if (getLevel().getBlockState(this.getOnPos()).getBlock() == Blocks.ANVIL) {
			setDeltaMovement(new Vec3(0, 0, 0));
			setPos(getOnPos().getX() + 0.5, getOnPos().getY() + 1, getOnPos().getZ() + 0.5);
			isOnAnvil = true;
		} else {
			isOnAnvil = false;
		}

		// Perform a cooling. If cooled, replace this with a regular item.
		if (getItem().getItem() instanceof HeatedIngot) {
			HeatedIngot ingot = (HeatedIngot) getItem().getItem();

			// If cooled, remove this entity and replace it with a regular item entity.
			boolean cooled = ingot.cooldownIngot(getItem());
			if (cooled) {
				remove(RemovalReason.KILLED);
				if (!getLevel().isClientSide()) {
					getLevel().playSound(null, getOnPos(), SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 0.5f, 1.0f);
					((ServerLevel) getLevel()).sendParticles(ParticleTypes.LARGE_SMOKE, getX(), getY(), getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
					getLevel().addFreshEntity(new ItemEntity(getLevel(), getX(), getY(), getZ(), ingot.getCooledVariant(getItem())));
				}
			} else {
				if (!getLevel().isClientSide() && SDMath.diceRoll(0.1f)) {
					((ServerLevel) getLevel()).sendParticles(ParticleTypes.LAVA, getX(), getY(), getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);

				}
			}
		}
	}

	@Override
	public boolean hurt(DamageSource p_32013_, float p_32014_) {
		return false;
	}

	public boolean isOnAnvil() {
		return isOnAnvil;
	}

	@Override
	public InteractionResult interact(Player player, InteractionHand hand) {
		return InteractionResult.PASS;
	}

	@Override
	public InteractionResult interactAt(Player player, Vec3 vec, InteractionHand hand) {
		return InteractionResult.PASS;
	}

	@Override
	public void playerTouch(Player entityIn) {
		if (isOnAnvil()) {
			// Make it impossible to pick up.
			setPickUpDelay(1000);

			if (entityIn.isShiftKeyDown()) {
				setPickUpDelay(0);
				super.playerTouch(entityIn);
			}
		} else {
			super.playerTouch(entityIn);
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);

	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
	}

	@OnlyIn(Dist.CLIENT)
	public float getSpin(float partialTicks) {
		return super.getSpin(partialTicks);
	}

	@Override
	public Packet<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}
