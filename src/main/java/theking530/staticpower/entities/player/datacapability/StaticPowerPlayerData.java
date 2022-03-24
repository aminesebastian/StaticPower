package theking530.staticpower.entities.player.datacapability;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
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

public class StaticPowerPlayerData implements IStaticPowerPlayerData, INBTSerializable<CompoundTag> {
	private static final float FLUID_MOVEMENT_SPEED_MULTIPLIER = 0.75f;

	private final IPlayerCapabilityProxy proxy;
	private final DigistoreCraftingTerminalHistory craftingHistory;

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
		// Create the proxies.
		if (FMLEnvironment.dist == Dist.CLIENT) {
			proxy = new ClientPlayerCapabilityProxy();
		} else {
			proxy = new ServerPlayerCapabilityProxy();
		}
	}

	@Override
	public void tick(PlayerTickEvent event) {
		handleCustomFluidSwimming(event);
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
			// If our foot was not underwater last frame, then play a splash.
			if (!wasFootInCustomFluid) {
				event.player.playSound(SoundEvents.PLAYER_SPLASH, 0.05f, getFluidPitch(currentInFluid));
			}

			// If we are fully submerged and have not played our submerge sound, do it.
			if (headInFluid && !fluidSoundsPlayed) {
				proxy.enteredCustomFluid(event, currentInFluid);
				event.player.playSound(SoundEvents.AMBIENT_UNDERWATER_ENTER, 1.0f, getFluidPitch(currentInFluid));
				fluidSoundsPlayed = true;
			}

			// Spawn ambient particles underwater every now and then.
			if (SDMath.diceRoll(0.5f) && headInFluid) {
				event.player.getLevel().addParticle(ParticleTypes.UNDERWATER, event.player.getRandomX(5.0f), event.player.getRandomY() + 1, event.player.getRandomZ(5.0f), 0.0f, 0.1f, 0.0f);
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

	private float getFluidPitch(Fluid fluid) {
		return 1000.0f / fluid.getAttributes().getViscosity();
	}
}