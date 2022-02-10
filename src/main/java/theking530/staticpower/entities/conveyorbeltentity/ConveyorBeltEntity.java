package theking530.staticpower.entities.conveyorbeltentity;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import theking530.staticpower.entities.cauldroncontainedentity.CauldronContainedEntity;
import theking530.staticpower.init.ModEntities;
import theking530.staticpower.tileentities.nonpowered.conveyors.IConveyorBlock;

public class ConveyorBeltEntity extends ItemEntity {
	public static final int CHANGE_BACK_TTL = 20;
	private static final EntityDataAccessor<Integer> TIME_NOT_ON_CONVEYOR = SynchedEntityData.defineId(CauldronContainedEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> TIME_ALIVE = SynchedEntityData.defineId(CauldronContainedEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Float> Y_RENDER_OFFSET = SynchedEntityData.defineId(CauldronContainedEntity.class, EntityDataSerializers.FLOAT);

	public ConveyorBeltEntity(EntityType<? extends ConveyorBeltEntity> p_i50217_1_, Level world) {
		super(p_i50217_1_, world);
	}

	public ConveyorBeltEntity(Level worldIn, double x, double y, double z) {
		this(ModEntities.ConveyorBeltEntity.getType(), worldIn);
		this.setPos(x, y, z);
		this.setYRot(this.random.nextFloat() * 360.0F);
		this.setDeltaMovement(this.random.nextDouble() * 0.2D - 0.1D, 0.2D, this.random.nextDouble() * 0.2D - 0.1D);
		this.getEntityData().set(Y_RENDER_OFFSET, random.nextFloat() * 0.01f);
	}

	public ConveyorBeltEntity(Level worldIn, double x, double y, double z, ItemStack stack) {
		this(ModEntities.ConveyorBeltEntity.getType(), worldIn);
		this.setPos(x, y, z);
		this.setYRot(this.random.nextFloat() * 360.0F);
		this.setDeltaMovement(this.random.nextDouble() * 0.2D - 0.1D, 0.2D, this.random.nextDouble() * 0.2D - 0.1D);
		this.getEntityData().set(Y_RENDER_OFFSET, random.nextFloat() * 0.01f);
		this.setItem(stack);
		this.lifespan = (stack.getItem() == null ? 6000 : stack.getEntityLifespan(worldIn));
		lifespan *= 10; // Need to expose this to a config.
	}

	@Override
	public void tick() {
		// If the entity is less than a second old, don't stack it up yet. This is to
		// prevent issues where items enter a belt, and then a subsequent item stacks
		// with it, causing the item to appear as though it dissapeared.
		if (getEntityData().get(TIME_ALIVE) < 20) {
			super.tick();
			getEntityData().set(TIME_ALIVE, getEntityData().get(TIME_ALIVE) + 1);
		} else {
			// We have to do this trick to prevent these entities from combining together
			// since all the methods for handling this are private. We set the stack size to
			// make so it cannot be combined, then we set it back to the original after the
			// super call.
			int originalStackSize = getItem().getCount();
			getItem().setCount(getItem().getMaxStackSize());
			super.tick();
			getItem().setCount(originalStackSize);
		}

		// Only perfrom the following on the server.
		if (getCommandSenderWorld().isClientSide) {
			return;
		}

		// Get the current blockstate and check if it is a cauldron.
		BlockState downBlockState = getCommandSenderWorld().getBlockState(blockPosition().relative(Direction.DOWN));
		BlockState currentBlockState = getCommandSenderWorld().getBlockState(blockPosition());
		if (currentBlockState.getBlock() instanceof IConveyorBlock || downBlockState.getBlock() instanceof IConveyorBlock) {
			getEntityData().set(TIME_NOT_ON_CONVEYOR, 0);
		} else {
			getEntityData().set(TIME_NOT_ON_CONVEYOR, getEntityData().get(TIME_NOT_ON_CONVEYOR) + 1);
			if (getEntityData().get(TIME_NOT_ON_CONVEYOR) >= CHANGE_BACK_TTL) {
				// Replace with standard item.
				ItemEntity regularItem = new ItemEntity(this.getCommandSenderWorld(), this.getX(), this.getY(), this.getZ(), getItem().copy());
				regularItem.setDeltaMovement(0, 0, 0);
				getCommandSenderWorld().addFreshEntity(regularItem);
				regularItem.setDeltaMovement(getDeltaMovement().x(), getDeltaMovement().y(), getDeltaMovement().z());

				// Remove ourselves
				setItem(ItemStack.EMPTY);
				remove(RemovalReason.DISCARDED);
			}
		}
	}

	@Override
	public InteractionResult interact(Player player, InteractionHand hand) {
		System.out.println("INITIAL_TEST");
		return InteractionResult.PASS;
	}

	@Override
	public InteractionResult interactAt(Player player, Vec3 vec, InteractionHand hand) {
		System.out.println("TEST");
		return InteractionResult.PASS;
	}

	@Override
	public void playerTouch(Player entityIn) {
		if (entityIn.isShiftKeyDown()) {
			setPickUpDelay(0); // Instant pickup.
			super.playerTouch(entityIn);
		}
	}

	public float getYRenderOffset() {
		return getEntityData().get(Y_RENDER_OFFSET);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("time_not_on_conveyor", entityData.get(TIME_NOT_ON_CONVEYOR));
		compound.putInt("time_alive", entityData.get(TIME_ALIVE));
		compound.putFloat("y_render_offset", entityData.get(Y_RENDER_OFFSET));

	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		getEntityData().set(TIME_NOT_ON_CONVEYOR, compound.getInt("time_not_on_conveyor"));
		getEntityData().set(TIME_ALIVE, compound.getInt("time_alive"));
		getEntityData().set(Y_RENDER_OFFSET, compound.getFloat("y_render_offset"));
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		getEntityData().define(TIME_NOT_ON_CONVEYOR, 0);
		getEntityData().define(TIME_ALIVE, 0);
		getEntityData().define(Y_RENDER_OFFSET, 0.0f);
	}

	@OnlyIn(Dist.CLIENT)
	public float getSpin(float partialTicks) {
		return 0;
	}

	@Override
	public Packet<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}
