package theking530.staticpower.entities.player.datacapability;

import java.util.List;
import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.utilities.SDMath;
import theking530.staticpower.cables.attachments.digistore.craftingterminal.DigistoreCraftingTerminalHistory;
import theking530.staticpower.cables.attachments.digistore.craftingterminal.DigistoreCraftingTerminalHistory.DigistoreCraftingTerminalHistoryEntry;
import theking530.staticpower.entities.player.datacapability.proxies.ClientPlayerCapabilityProxy;
import theking530.staticpower.entities.player.datacapability.proxies.IPlayerCapabilityProxy;
import theking530.staticpower.entities.player.datacapability.proxies.ServerPlayerCapabilityProxy;
import theking530.staticpower.fluid.AbstractStaticPowerFluid;

/**
 * Hate that we have to do fluid movement logic in here. All that code is
 * temporary and going away with the update to 1.19.2.
 * 
 * @author amine
 *
 */
public class StaticPowerPlayerData implements IStaticPowerPlayerData, INBTSerializable<CompoundTag> {
	private static final float FLUID_MOVEMENT_SPEED_MULTIPLIER = 0.75f;

	protected boolean firstTick = true;
	protected boolean isInFluid = false;

	private final IPlayerCapabilityProxy proxy;
	private final DigistoreCraftingTerminalHistory craftingHistory;
	private final Random random;

	private boolean wasFootInCustomFluid;
	private boolean wasHeadInCustomFluid;
	private boolean fluidSoundsPlayed;
	private Fluid currentInFluid;

	public StaticPowerPlayerData() {
		craftingHistory = new DigistoreCraftingTerminalHistory(4);
		wasFootInCustomFluid = false;
		wasHeadInCustomFluid = false;
		fluidSoundsPlayed = false;
		currentInFluid = Fluids.EMPTY;
		random = new Random();
		// Create the proxies.
		if (FMLEnvironment.dist == Dist.CLIENT) {
			proxy = new ClientPlayerCapabilityProxy();
		} else {
			proxy = new ServerPlayerCapabilityProxy();
		}
	}

	@Override
	public void tick(PlayerTickEvent event) {
		updateInWaterStateAndDoWaterCurrentPushing(event.player);
		handleCustomFluidSwimming(event);
		firstTick = false;
	}

	@Override
	public void addToCraftingHistory(ItemStack crafting, Container craftMatrix) {
		if (craftMatrix != null) {
			ItemStack[] recipe = new ItemStack[craftMatrix.getContainerSize()];
			for (int i = 0; i < recipe.length; i++) {
				recipe[i] = craftMatrix.getItem(i).copy();
			}
			craftingHistory.addCraft(recipe, crafting.copy());
		}
	}

	public List<DigistoreCraftingTerminalHistoryEntry> getCraftingHistory() {
		return craftingHistory.getHistory();
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag output = new CompoundTag();
		output.put("crafting_history", craftingHistory.serializeNBT());
		return output;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		craftingHistory.deserializeNBT(nbt.getCompound("crafting_history"));
	}

	protected void handleCustomFluidSwimming(PlayerTickEvent event) {
		// Capture the fluids of both the head and the feet.
		Fluid headFluid = event.player.getLevel().getFluidState(new BlockPos(event.player.getEyePosition().subtract(0, 0, 0))).getType();
		Fluid feetFluid = event.player.getLevel().getFluidState(new BlockPos(event.player.getEyePosition().subtract(0, 1, 0))).getType();

		boolean headInFluid = (headFluid instanceof AbstractStaticPowerFluid);
		boolean footInFluid = (feetFluid instanceof AbstractStaticPowerFluid);

		// Set the current in fluid to either of the head of feet fluids if they are
		// valid.
		currentInFluid = headInFluid && currentInFluid == Fluids.EMPTY ? headFluid : currentInFluid;
		currentInFluid = footInFluid && currentInFluid == Fluids.EMPTY ? feetFluid : currentInFluid;

		// This branch is taken if we're underwater for any reason.
		if (headInFluid || footInFluid) {
			// If we are fully submerged and have not played our submerge sound, do it.
			if (headInFluid && !fluidSoundsPlayed) {
				proxy.enteredCustomFluid(event, currentInFluid);
				event.player.playSound(SoundEvents.AMBIENT_UNDERWATER_ENTER, 1.0f, getFluidPitch(currentInFluid));
				fluidSoundsPlayed = true;
			}

			// Spawn ambient particles underwater every now and then.
			if (SDMath.diceRoll(0.5f) && headInFluid) {
				event.player.getLevel().addParticle(ParticleTypes.UNDERWATER, event.player.getRandomX(5.0f), event.player.getRandomY() + 1, event.player.getRandomZ(5.0f), 0.0f,
						0.1f, 0.0f);
			}

			// First, we force the player into flying mode.
			event.player.getAbilities().flying = true;

			// Then, capture the desired delta movement and slow it down on the x and z
			// axis.
			// On the y axis, if going down, slow down the rate of falling. Since we're
			// flying and by default hover, apply a small downwards velocity.
			// If the desired velocity is > 0, move upwards but even faster so as to boost
			// out of the fluid.
			Vec3 deltaMovement = event.player.getDeltaMovement();
			float movementModifier = FLUID_MOVEMENT_SPEED_MULTIPLIER;

			double yMovement = deltaMovement.y <= 0 ? (deltaMovement.y * FLUID_MOVEMENT_SPEED_MULTIPLIER) - 0.025f : deltaMovement.y * 0.3f;
			if (!headInFluid && yMovement > 0) {
				yMovement *= 5.5f;
			}

			// Apply the movement.
			event.player.setDeltaMovement(deltaMovement.x * movementModifier, yMovement, deltaMovement.z * movementModifier);
		} else {
			// Once we leave the fluid, trigger the leave sound and signal to the proxy to
			// perform any sided logic.
			// Stop the player's flight (this will get annoying if they were flying before
			// entering).
			if (wasHeadInCustomFluid || wasFootInCustomFluid) {
				if (fluidSoundsPlayed) {
					event.player.playSound(SoundEvents.AMBIENT_UNDERWATER_EXIT, 1.0f, getFluidPitch(currentInFluid));
				}
				proxy.leftCustomFluid(event, currentInFluid);
				event.player.getAbilities().flying = false;
				currentInFluid = Fluids.EMPTY;
				fluidSoundsPlayed = false;
			}
		}
		// Update the state for last frame.
		this.wasHeadInCustomFluid = headInFluid;
		this.wasFootInCustomFluid = footInFluid;

	}

	void updateInWaterStateAndDoWaterCurrentPushing(Entity player) {
		Fluid feetFluid = player.getLevel().getFluidState(new BlockPos(player.getEyePosition().subtract(0, 1, 0))).getType();
		TagKey<Fluid> tag = feetFluid.builtInRegistryHolder().getTagKeys().findFirst().orElse(null);

		if (tag == null || !(feetFluid instanceof AbstractStaticPowerFluid)) {
			isInFluid = false;
			return;
		}

		if (player.getVehicle() instanceof Boat) {
			isInFluid = false;
		} else if (player.updateFluidHeightAndDoFluidPushing(tag, 0.014D)) {
			if (!isInFluid && !firstTick) {
				doWaterSplashEffect(player);
			}

			player.resetFallDistance();
			isInFluid = true;
			player.clearFire();
		} else {
			isInFluid = false;
		}
	}

	protected void doWaterSplashEffect(Entity player) {
		Entity entity = player.isVehicle() && player.getControllingPassenger() != null ? player.getControllingPassenger() : player;
		float f = entity == player ? 0.2F : 0.9F;
		Vec3 vec3 = entity.getDeltaMovement();
		float f1 = Math.min(1.0F, (float) Math.sqrt(vec3.x * vec3.x * (double) 0.2F + vec3.y * vec3.y + vec3.z * vec3.z * (double) 0.2F) * f);
		if (f1 < 0.25F) {
			player.playSound(this.getSwimSplashSound(), f1, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
		} else {
			player.playSound(this.getSwimHighSpeedSplashSound(), f1, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
		}

		float f2 = (float) Mth.floor(player.getY());

		for (int i = 0; (float) i < 1.0F + player.getDimensions(null).width * 20.0F; ++i) {
			double d0 = (this.random.nextDouble() * 2.0D - 1.0D) * (double) player.getDimensions(null).width;
			double d1 = (this.random.nextDouble() * 2.0D - 1.0D) * (double) player.getDimensions(null).width;
			player.level.addParticle(ParticleTypes.BUBBLE, player.getX() + d0, (double) (f2 + 1.0F), player.getZ() + d1, vec3.x, vec3.y - this.random.nextDouble() * (double) 0.2F,
					vec3.z);
		}

		for (int j = 0; (float) j < 1.0F + player.getDimensions(null).width * 20.0F; ++j) {
			double d2 = (this.random.nextDouble() * 2.0D - 1.0D) * (double) player.getDimensions(null).width;
			double d3 = (this.random.nextDouble() * 2.0D - 1.0D) * (double) player.getDimensions(null).width;
			player.level.addParticle(ParticleTypes.SPLASH, player.getX() + d2, (double) (f2 + 1.0F), player.getZ() + d3, vec3.x, vec3.y, vec3.z);
		}

		player.gameEvent(GameEvent.SPLASH);
	}

	protected SoundEvent getSwimSplashSound() {
		return SoundEvents.GENERIC_SPLASH;
	}

	protected SoundEvent getSwimHighSpeedSplashSound() {
		return SoundEvents.GENERIC_SPLASH;
	}

	private float getFluidPitch(Fluid fluid) {
		return 1000.0f / fluid.getAttributes().getViscosity();
	}
}