package theking530.staticpower.entities;

import java.io.IOException;
import java.util.UUID;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import theking530.staticpower.init.ModEntities;
import theking530.staticpower.tileentities.nonpowered.conveyors.IConveyorBlock;

public class ConveyorBeltEntity extends ItemEntity {
	public static final int CHANGE_BACK_TTL = 20;
	private int timeNotOnConveyor;

	public ConveyorBeltEntity(EntityType<? extends ConveyorBeltEntity> p_i50217_1_, World world) {
		super(p_i50217_1_, world);
	}

	public ConveyorBeltEntity(World worldIn, double x, double y, double z) {
		this(ModEntities.ConveyorBeltEntity.getType(), worldIn);
		this.setPosition(x, y, z);
		this.rotationYaw = this.rand.nextFloat() * 360.0F;
		this.setMotion(this.rand.nextDouble() * 0.2D - 0.1D, 0.2D, this.rand.nextDouble() * 0.2D - 0.1D);
	}

	public ConveyorBeltEntity(World worldIn, double x, double y, double z, ItemStack stack) {
		this(ModEntities.ConveyorBeltEntity.getType(), worldIn);
		this.setPosition(x, y, z);
		this.rotationYaw = this.rand.nextFloat() * 360.0F;
		this.setMotion(this.rand.nextDouble() * 0.2D - 0.1D, 0.2D, this.rand.nextDouble() * 0.2D - 0.1D);
		this.setItem(stack);
		this.lifespan = (stack.getItem() == null ? 6000 : stack.getEntityLifespan(worldIn));
		lifespan *= 10; // Need to expose this to a config.
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

		// Get the current blockstate and check if it is a cauldron.
		BlockState downBlockState = getEntityWorld().getBlockState(getPosition().offset(Direction.DOWN));
		BlockState currentBlockState = getEntityWorld().getBlockState(getPosition());
		if (currentBlockState.getBlock() instanceof IConveyorBlock || downBlockState.getBlock() instanceof IConveyorBlock) {
			timeNotOnConveyor = 0;
		} else {
			timeNotOnConveyor++;
			if (timeNotOnConveyor >= CHANGE_BACK_TTL) {
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

	@Override
	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);

	}

	@Override
	public void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);
	}

	@OnlyIn(Dist.CLIENT)
	public float getItemHover(float partialTicks) {
		return 0;
	}

	@Override
	public IPacket<?> createSpawnPacket() {
		return new ConveyorBeltEntitySpawnPacket(this);
	}

	public static class ConveyorBeltEntityType extends AbstractEntityType<ConveyorBeltEntity> {

		public ConveyorBeltEntityType(String name) {
			super(name, EntityType.Builder.<ConveyorBeltEntity>create(ConveyorBeltEntity::new, EntityClassification.MISC).size(0.25F, 0.25F).trackingRange(6).func_233608_b_(20));
		}

		@Override
		public void registerAttributes(Register<EntityType<?>> event) {

		}

		@Override
		@OnlyIn(Dist.CLIENT)
		public void registerRenderers(FMLClientSetupEvent event) {
			RenderingRegistry.registerEntityRenderingHandler(ModEntities.ConveyorBeltEntity.getType(), (EntityRendererManager manager) -> {
				return new ConveyorBeltEntityRenderer(manager, Minecraft.getInstance().getItemRenderer());
			});
		}
	}

	public static class ConveyorBeltEntitySpawnPacket implements IPacket<IClientPlayNetHandler> {
		private int entityId;
		private UUID uniqueId;
		private double x;
		private double y;
		private double z;
		private int speedX;
		private int speedY;
		private int speedZ;
		private int pitch;
		private int yaw;
		private EntityType<?> type;
		private int cookingTime;

		public ConveyorBeltEntitySpawnPacket() {
		}

		public ConveyorBeltEntitySpawnPacket(int entityId, UUID uuid, double xPos, double yPos, double zPos, float pitch, float yaw, EntityType<?> entityType, int entityData,
				Vector3d speedVector) {
			this.entityId = entityId;
			this.uniqueId = uuid;
			this.x = xPos;
			this.y = yPos;
			this.z = zPos;
			this.pitch = MathHelper.floor(pitch * 256.0F / 360.0F);
			this.yaw = MathHelper.floor(yaw * 256.0F / 360.0F);
			this.type = entityType;
			this.cookingTime = entityData;
			this.speedX = (int) (MathHelper.clamp(speedVector.x, -3.9D, 3.9D) * 8000.0D);
			this.speedY = (int) (MathHelper.clamp(speedVector.y, -3.9D, 3.9D) * 8000.0D);
			this.speedZ = (int) (MathHelper.clamp(speedVector.z, -3.9D, 3.9D) * 8000.0D);
		}

		public ConveyorBeltEntitySpawnPacket(Entity entityIn) {
			this(entityIn.getEntityId(), entityIn.getUniqueID(), entityIn.getPosX(), entityIn.getPosY(), entityIn.getPosZ(), entityIn.rotationPitch, entityIn.rotationYaw, entityIn.getType(),
					0, entityIn.getMotion());
		}

		/**
		 * Reads the raw packet data from the data stream.
		 */
		@SuppressWarnings("deprecation")
		public void readPacketData(PacketBuffer buf) throws IOException {
			this.entityId = buf.readVarInt();
			this.uniqueId = buf.readUniqueId();
			this.type = Registry.ENTITY_TYPE.getByValue(buf.readVarInt());
			this.x = buf.readDouble();
			this.y = buf.readDouble();
			this.z = buf.readDouble();
			this.pitch = buf.readByte();
			this.yaw = buf.readByte();
			this.cookingTime = buf.readInt();
			this.speedX = buf.readShort();
			this.speedY = buf.readShort();
			this.speedZ = buf.readShort();
		}

		/**
		 * Writes the raw packet data to the data stream.
		 */
		@SuppressWarnings("deprecation")
		public void writePacketData(PacketBuffer buf) throws IOException {
			buf.writeVarInt(this.entityId);
			buf.writeUniqueId(this.uniqueId);
			buf.writeVarInt(Registry.ENTITY_TYPE.getId(this.type));
			buf.writeDouble(this.x);
			buf.writeDouble(this.y);
			buf.writeDouble(this.z);
			buf.writeByte(this.pitch);
			buf.writeByte(this.yaw);
			buf.writeInt(this.cookingTime);
			buf.writeShort(this.speedX);
			buf.writeShort(this.speedY);
			buf.writeShort(this.speedZ);
		}

		/**
		 * Passes this Packet on to the NetHandler for processing.
		 */
		public void processPacket(IClientPlayNetHandler handler) {
			PacketThreadUtil.checkThreadAndEnqueue(this, handler, Minecraft.getInstance());
			if (Minecraft.getInstance().player.world.isAreaLoaded(new BlockPos(x, y, z), 1)) {
				// Create and add a cauldron contained entity.
				ConveyorBeltEntity rubberEntity = new ConveyorBeltEntity(Minecraft.getInstance().player.world, x, y, z);
				rubberEntity.setPacketCoordinates(x, y, z);
				rubberEntity.moveForced(x, y, z);
				rubberEntity.rotationPitch = (float) (pitch * 360) / 256.0F;
				rubberEntity.rotationYaw = (float) (yaw * 360) / 256.0F;
				rubberEntity.setEntityId(entityId);
				rubberEntity.setUniqueId(uniqueId);
				((ClientWorld) Minecraft.getInstance().player.world).addEntity(entityId, rubberEntity);
			}
		}
	}
}
