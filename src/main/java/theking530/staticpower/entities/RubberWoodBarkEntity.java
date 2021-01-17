package theking530.staticpower.entities;

import java.io.IOException;
import java.util.UUID;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import theking530.staticcore.utilities.SDMath;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModEntities;
import theking530.staticpower.init.ModItems;

public class RubberWoodBarkEntity extends ItemEntity {

	public RubberWoodBarkEntity(EntityType<? extends RubberWoodBarkEntity> p_i50217_1_, World world) {
		super(p_i50217_1_, world);
	}

	public RubberWoodBarkEntity(World worldIn, double x, double y, double z) {
		this(ModEntities.RubberWoodBark.getType(), worldIn);
		this.setPosition(x, y, z);
		this.rotationYaw = this.rand.nextFloat() * 360.0F;
		this.setMotion(this.rand.nextDouble() * 0.2D - 0.1D, 0.2D, this.rand.nextDouble() * 0.2D - 0.1D);
	}

	public RubberWoodBarkEntity(World worldIn, double x, double y, double z, ItemStack stack) {
		this(ModEntities.RubberWoodBark.getType(), worldIn);
		this.setPosition(x, y, z);
		this.rotationYaw = this.rand.nextFloat() * 360.0F;
		this.setMotion(this.rand.nextDouble() * 0.2D - 0.1D, 0.2D, this.rand.nextDouble() * 0.2D - 0.1D);
		this.setItem(stack);
		this.lifespan = (stack.getItem() == null ? 6000 : stack.getEntityLifespan(worldIn));
	}

	@Override
	public void tick() {
		super.tick();

		// Only perfrom the following on the server.
		if (getEntityWorld().isRemote) {
			return;
		}

		if (SDMath.diceRoll(0.25)) {
			float randomOffset = this.getEntityWorld().getRandom().nextFloat();
			randomOffset *= 2;
			randomOffset -= 1;
			randomOffset *= 0.1;

			((ServerWorld) getEntityWorld()).spawnParticle(ParticleTypes.SPLASH, getPosX() + randomOffset, getPosY() + 0.35, getPosZ() + randomOffset, 1, 0.0D, 0.0D, 0.0D, 0.0D);
			((ServerWorld) getEntityWorld()).spawnParticle(ParticleTypes.BUBBLE_COLUMN_UP, getPosX() + randomOffset, getPosY() + 0.7, getPosZ() + randomOffset, 1, 0.0D, 0.0D, 0.0D, 1.0D);
		}

		BlockState currentBlockState = getEntityWorld().getBlockState(this.getPosition());
		if (currentBlockState.getBlock() == ModBlocks.RustyCauldron) {
			if (getAge() > 100) {
				ItemEntity latex = new ItemEntity(this.getEntityWorld(), getPosX(), getPosY(), getPosZ(), new ItemStack(ModItems.LatexChunk, getItem().getCount()));
				latex.setMotion(0, 0.275, 0);
				getEntityWorld().addEntity(latex);

				getEntityWorld().playSound(null, this.getPosition(), SoundEvents.ENTITY_GENERIC_SPLASH, SoundCategory.BLOCKS, 0.5f, 1.0f);
				((ServerWorld) getEntityWorld()).spawnParticle(ParticleTypes.SPLASH, getPosX(), getPosY() + 0.8, getPosZ(), 10, 0.0D, 0.0D, 0.0D, 0.0D);

				// Remove ourselves
				remove();
			}
		} else {
			ItemEntity regularBark = new ItemEntity(this.getEntityWorld(), this.getPosX(), this.getPosY(), this.getPosZ(), getItem().copy());
			getEntityWorld().addEntity(regularBark);

			// Remove ourselves
			remove();
		}
	}

	@Override
	public void onCollideWithPlayer(PlayerEntity entityIn) {
		// If we actually touch the player, let the player pick it up.
		if (!this.world.isRemote && this.getAge() > 60) {
			this.setPickupDelay(0);
		}
		super.onCollideWithPlayer(entityIn);
	}

	@Override
	public IPacket<?> createSpawnPacket() {
		return new RubberWoodBarkEntitySpawn(this);
	}

	public static class RubberWoodBarkEntityType extends AbstractEntityType<RubberWoodBarkEntity> {

		public RubberWoodBarkEntityType(String name) {
			super(name, EntityType.Builder.<RubberWoodBarkEntity>create(RubberWoodBarkEntity::new, EntityClassification.MISC).size(0.25F, 0.25F).trackingRange(6).func_233608_b_(20));
		}

		@Override
		public void registerAttributes(Register<EntityType<?>> event) {

		}

		@Override
		@OnlyIn(Dist.CLIENT)
		public void registerRenderers(FMLClientSetupEvent event) {
			RenderingRegistry.registerEntityRenderingHandler(ModEntities.RubberWoodBark.getType(), (EntityRendererManager manager) -> {
				return new ItemRenderer(manager, Minecraft.getInstance().getItemRenderer());
			});
		}
	}

	public static class RubberWoodBarkEntitySpawn implements IPacket<IClientPlayNetHandler> {
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
		private int data;

		public RubberWoodBarkEntitySpawn() {
		}

		public RubberWoodBarkEntitySpawn(int entityId, UUID uuid, double xPos, double yPos, double zPos, float pitch, float yaw, EntityType<?> entityType, int entityData,
				Vector3d speedVector) {
			this.entityId = entityId;
			this.uniqueId = uuid;
			this.x = xPos;
			this.y = yPos;
			this.z = zPos;
			this.pitch = MathHelper.floor(pitch * 256.0F / 360.0F);
			this.yaw = MathHelper.floor(yaw * 256.0F / 360.0F);
			this.type = entityType;
			this.data = entityData;
			this.speedX = (int) (MathHelper.clamp(speedVector.x, -3.9D, 3.9D) * 8000.0D);
			this.speedY = (int) (MathHelper.clamp(speedVector.y, -3.9D, 3.9D) * 8000.0D);
			this.speedZ = (int) (MathHelper.clamp(speedVector.z, -3.9D, 3.9D) * 8000.0D);
		}

		public RubberWoodBarkEntitySpawn(Entity entityIn) {
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
			this.data = buf.readInt();
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
			buf.writeInt(this.data);
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
				// Create and add a rubber wood bark entity.
				RubberWoodBarkEntity rubberEntity = new RubberWoodBarkEntity(Minecraft.getInstance().player.world, x, y, z);
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
