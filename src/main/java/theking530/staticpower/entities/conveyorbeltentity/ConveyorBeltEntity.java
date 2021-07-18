package theking530.staticpower.entities.conveyorbeltentity;

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
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;
import theking530.staticpower.entities.cauldroncontainedentity.CauldronContainedEntity;
import theking530.staticpower.init.ModEntities;
import theking530.staticpower.tileentities.nonpowered.conveyors.IConveyorBlock;

public class ConveyorBeltEntity extends ItemEntity {
	public static final int CHANGE_BACK_TTL = 20;
	private static final DataParameter<Integer> TIME_NOT_ON_CONVEYOR = EntityDataManager.createKey(CauldronContainedEntity.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> TIME_ALIVE = EntityDataManager.createKey(CauldronContainedEntity.class, DataSerializers.VARINT);
	private static final DataParameter<Float> Y_RENDER_OFFSET = EntityDataManager.createKey(CauldronContainedEntity.class, DataSerializers.FLOAT);

	public ConveyorBeltEntity(EntityType<? extends ConveyorBeltEntity> p_i50217_1_, World world) {
		super(p_i50217_1_, world);
	}

	public ConveyorBeltEntity(World worldIn, double x, double y, double z) {
		this(ModEntities.ConveyorBeltEntity.getType(), worldIn);
		this.setPosition(x, y, z);
		this.rotationYaw = this.rand.nextFloat() * 360.0F;
		this.setMotion(this.rand.nextDouble() * 0.2D - 0.1D, 0.2D, this.rand.nextDouble() * 0.2D - 0.1D);
		this.getDataManager().set(Y_RENDER_OFFSET, rand.nextFloat() * 0.01f);
	}

	public ConveyorBeltEntity(World worldIn, double x, double y, double z, ItemStack stack) {
		this(ModEntities.ConveyorBeltEntity.getType(), worldIn);
		this.setPosition(x, y, z);
		this.rotationYaw = this.rand.nextFloat() * 360.0F;
		this.setMotion(this.rand.nextDouble() * 0.2D - 0.1D, 0.2D, this.rand.nextDouble() * 0.2D - 0.1D);
		this.getDataManager().set(Y_RENDER_OFFSET, rand.nextFloat() * 0.01f);
		this.setItem(stack);
		this.lifespan = (stack.getItem() == null ? 6000 : stack.getEntityLifespan(worldIn));
		lifespan *= 10; // Need to expose this to a config.
	}

	@Override
	public void tick() {
		// If the entity is less than a second old, don't stack it up yet. This is to
		// prevent issues where items enter a belt, and then a subsequent item stacks
		// with it, causing the item to appear as though it dissapeared.
		if (getDataManager().get(TIME_ALIVE) < 20) {
			super.tick();
			getDataManager().set(TIME_ALIVE, getDataManager().get(TIME_ALIVE) + 1);
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
		if (getEntityWorld().isRemote) {
			return;
		}

		// Get the current blockstate and check if it is a cauldron.
		BlockState downBlockState = getEntityWorld().getBlockState(getPosition().offset(Direction.DOWN));
		BlockState currentBlockState = getEntityWorld().getBlockState(getPosition());
		if (currentBlockState.getBlock() instanceof IConveyorBlock || downBlockState.getBlock() instanceof IConveyorBlock) {
			getDataManager().set(TIME_NOT_ON_CONVEYOR, 0);
		} else {
			getDataManager().set(TIME_NOT_ON_CONVEYOR, getDataManager().get(TIME_NOT_ON_CONVEYOR) + 1);
			if (getDataManager().get(TIME_NOT_ON_CONVEYOR) >= CHANGE_BACK_TTL) {
				// Replace with standard item.
				ItemEntity regularItem = new ItemEntity(this.getEntityWorld(), this.getPosX(), this.getPosY(), this.getPosZ(), getItem().copy());
				regularItem.setMotion(0, 0, 0);
				getEntityWorld().addEntity(regularItem);
				regularItem.setMotion(getMotion().getX(), getMotion().getY(), getMotion().getZ());

				// Remove ourselves
				setItem(ItemStack.EMPTY);
				remove();
			}
		}
	}

	@Override
	public ActionResultType processInitialInteract(PlayerEntity player, Hand hand) {
		System.out.println("INITIAL_TEST");
		return ActionResultType.PASS;
	}

	@Override
	public ActionResultType applyPlayerInteraction(PlayerEntity player, Vector3d vec, Hand hand) {
		System.out.println("TEST");
		return ActionResultType.PASS;
	}

	@Override
	public void onCollideWithPlayer(PlayerEntity entityIn) {
		if (entityIn.isSneaking()) {
			setPickupDelay(0); // Instant pickup.
			super.onCollideWithPlayer(entityIn);
		}
	}

	public float getYRenderOffset() {
		return getDataManager().get(Y_RENDER_OFFSET);
	}

	@Override
	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);
		compound.putInt("time_not_on_conveyor", dataManager.get(TIME_NOT_ON_CONVEYOR));
		compound.putInt("time_alive", dataManager.get(TIME_ALIVE));
		compound.putFloat("y_render_offset", dataManager.get(Y_RENDER_OFFSET));

	}

	@Override
	public void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);
		getDataManager().set(TIME_NOT_ON_CONVEYOR, compound.getInt("time_not_on_conveyor"));
		getDataManager().set(TIME_ALIVE, compound.getInt("time_alive"));
		getDataManager().set(Y_RENDER_OFFSET, compound.getFloat("y_render_offset"));
	}

	@Override
	protected void registerData() {
		super.registerData();
		getDataManager().register(TIME_NOT_ON_CONVEYOR, 0);
		getDataManager().register(TIME_ALIVE, 0);
		getDataManager().register(Y_RENDER_OFFSET, 0.0f);
	}

	@OnlyIn(Dist.CLIENT)
	public float getItemHover(float partialTicks) {
		return 0;
	}

	@Override
	public IPacket<?> createSpawnPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}
