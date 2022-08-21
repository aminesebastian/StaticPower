package theking530.staticpower.cables.attachments.sprinkler;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import theking530.staticcore.utilities.SDMath;
import theking530.staticcore.utilities.StaticPowerRarities;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.attachments.AbstractCableAttachment;
import theking530.staticpower.cables.fluid.FluidCableComponent;
import theking530.staticpower.cables.fluid.FluidNetworkModule;
import theking530.staticpower.cables.network.CableNetworkModuleTypes;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerRecipeRegistry;
import theking530.staticpower.data.crafting.wrappers.fertilization.FertalizerRecipe;
import theking530.staticpower.init.ModFluids;
import theking530.staticpower.tileentities.components.control.redstonecontrol.RedstoneMode;

public class SprinklerAttachment extends AbstractCableAttachment {
	private static final Vector3D SPRINKLER_BOUNDS = new Vector3D(2.5f, 2.5f, 2.5f);
	private final ResourceLocation model;
	private final ResourceLocation tierType;

	public SprinklerAttachment(ResourceLocation tierType, ResourceLocation model) {
		super();
		this.model = model;
		this.tierType = tierType;
	}

	@Override
	public void onAddedToCable(ItemStack attachment, Direction side, AbstractCableProviderComponent cable) {
		super.onAddedToCable(attachment, side, cable);
		attachment.getTag().putInt("redstone_mode", RedstoneMode.Low.ordinal());
	}

	@Override
	public Vector3D getBounds() {
		return SPRINKLER_BOUNDS;
	}

	@Override
	public void attachmentTick(ItemStack attachment, Direction side, AbstractCableProviderComponent cable) {
		// Skip ahead every other tick.
		if (!SDMath.diceRoll(0.5f)) {
			return;
		}

		// Check redstone signal.
		if (!cable.doesAttachmentPassRedstoneTest(attachment)) {
			return;
		}

		// Get the fluid cable and the fluid in the cable. Return early if there is no
		// fluid.
		FluidCableComponent fluidCable = (FluidCableComponent) cable;
		FluidStack fluid = fluidCable.getFluidInTank(0);
		if (fluid.isEmpty()) {
			return;
		}

		boolean actionPerformed = handleFertilization(attachment, side, fluid, fluidCable);
		if (!actionPerformed) {
			actionPerformed = handleExperience(attachment, side, fluid, fluidCable);
		}

	}

	protected boolean handleExperience(ItemStack attachment, Direction side, FluidStack fluidContained, FluidCableComponent fluidCable) {
		// Check to make sure the fluid is experience.
		if (fluidContained.getFluid() != ModFluids.LiquidExperience.source.get()) {
			return false;
		}

		// Use fluid and spawn the experience orb.
		if (!fluidCable.getLevel().isClientSide) {
			fluidCable.<FluidNetworkModule>getNetworkModule(CableNetworkModuleTypes.FLUID_NETWORK_MODULE).ifPresent(network -> {
				int drained = network.getFluidStorage().drain(5, FluidAction.EXECUTE).getAmount();
				Vector3D direction = new Vector3D(side);

				// Create the XP Orb Entity.
				ExperienceOrb orb = new ExperienceOrb(fluidCable.getLevel(), fluidCable.getPos().getX() + 0.5f + direction.getX(),
						fluidCable.getPos().getY() + 0.5f + direction.getY(), fluidCable.getPos().getZ() + 0.5f + direction.getZ(), drained);

				// Set a random X and Z velocity.
				float random = fluidCable.getLevel().getRandom().nextFloat();
				random *= 2;
				random -= 1;
				random *= 0.02;

				// Set the motion.
				orb.setDeltaMovement(random, 0.25, random);

				// Add the entity orb to the world.
				fluidCable.getLevel().addFreshEntity(orb);
			});

		}
		return true;
	}

	/**
	 * 
	 * @param attachment
	 * @param side
	 * @param fluidContained
	 * @param fluidCable
	 * @return True if a fertilization recipe exists, false otherwise.
	 */
	@SuppressWarnings("deprecation")
	protected boolean handleFertilization(ItemStack attachment, Direction side, FluidStack fluidContained, FluidCableComponent fluidCable) {
		// Get the fertilization recipe. If one does not exist, return early.
		FertalizerRecipe recipe = StaticPowerRecipeRegistry.getRecipe(FertalizerRecipe.RECIPE_TYPE, new RecipeMatchParameters(fluidContained)).orElse(null);
		if (recipe == null) {
			return false;
		}

		// Spawn the particles on the client, fertilize and use the fluid on the server.
		if (fluidCable.getLevel().isClientSide) {
			// Only render particles half of the time.
			if (SDMath.diceRoll(0.5f)) {
				// Get a random offset.
				float random = fluidCable.getLevel().getRandom().nextFloat();
				random *= 2;
				random -= 1;
				random /= 5;

				// Set the direction.
				Vector3D direction = new Vector3D(side);
				direction.multiply(0.85f);

				// Set the velocity.
				Vector3D velocity = new Vector3D(side);
				velocity.multiply(0.5f);

				// Spawn the particle.
				fluidCable.getLevel().addParticle(ParticleTypes.FALLING_WATER, fluidCable.getPos().getX() + random + 0.5f + direction.getX(),
						fluidCable.getPos().getY() + random + 0.5f + direction.getY(), fluidCable.getPos().getZ() + random + 0.5f + direction.getZ(), velocity.getX(), velocity.getY(),
						velocity.getZ());
			}
		} else {
			// Get the fertilization chance and divide it by 20. Handle cases where the
			// fertilization chance is == 0;
			float growthChange = recipe.getFertalizationAmount();
			if (growthChange == 0) {
				growthChange = 0.02f;
			}
			growthChange /= 20;

			// Use fluid.
			fluidCable.<FluidNetworkModule>getNetworkModule(CableNetworkModuleTypes.FLUID_NETWORK_MODULE).ifPresent(network -> {
				network.getFluidStorage().drain(1, FluidAction.EXECUTE);
			});

			// Allocate the target position. If it remains null, do nothing.
			BlockPos target = null;

			// Check for the first solid block.
			for (int i = 1; i < 10; i++) {
				BlockPos testTarget = fluidCable.getPos().relative(Direction.DOWN, i);
				if (!fluidCable.getLevel().getBlockState(testTarget).isAir()) {
					target = testTarget;
					break;
				}
			}

			// If there was no target, do nothing.
			if (target == null) {
				return true;
			}

			// Check if we should add moisture to the block or the block below.
			BlockState belowBlock = fluidCable.getLevel().getBlockState(target.relative(Direction.DOWN));
			BlockState cropState = fluidCable.getLevel().getBlockState(target);

			// Perform the moisturization.
			if (belowBlock.hasProperty(FarmBlock.MOISTURE) && belowBlock.getValue(FarmBlock.MOISTURE) < 7) {
				fluidCable.getLevel().setBlock(target.relative(Direction.DOWN), belowBlock.setValue(FarmBlock.MOISTURE, 7), 1 | 2);
			} else if (cropState.hasProperty(FarmBlock.MOISTURE) && cropState.getValue(FarmBlock.MOISTURE) < 7) {
				fluidCable.getLevel().setBlock(target, cropState.setValue(FarmBlock.MOISTURE, 7), 1 | 2);
			}

			// If it passes, determine the farm ground level.
			if (SDMath.diceRoll(growthChange)) {
				// Check to make sure we can grow this block.
				if (cropState != null && cropState.getBlock() instanceof BonemealableBlock) {
					// Get the growable.
					BonemealableBlock tempCrop = (BonemealableBlock) cropState.getBlock();

					// If we can grow this, grow it.
					if (tempCrop.isValidBonemealTarget(fluidCable.getLevel(), target, cropState, false)) {
						tempCrop.performBonemeal((ServerLevel) fluidCable.getLevel(), fluidCable.getLevel().random, target, cropState);
						// Spawn some fertilziation particles.
						((ServerLevel) fluidCable.getLevel()).sendParticles(ParticleTypes.HAPPY_VILLAGER, target.getX() + 0.5D, target.getY() + 1.0D, target.getZ() + 0.5D, 1, 0.0D, 0.0D,
								0.0D, 0.0D);
					}
				}
			}
		}

		return recipe != null;
	}

	@Override
	public boolean hasGui(ItemStack attachment) {
		return false;
	}

	@Override
	public ResourceLocation getModel(ItemStack attachment, AbstractCableProviderComponent cableComponent) {
		return model;
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		return StaticPowerRarities.getRarityForTier(this.tierType);
	}

	@Override
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean isShowingAdvanced) {
		if (!isShowingAdvanced) {
			tooltip.add(new TranslatableComponent("gui.staticpower.sprinkler_tooltip").withStyle(ChatFormatting.DARK_AQUA));
		}
	}

	@Override
	public void getAdvancedTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip) {
		tooltip.add(new TextComponent("� ").append(new TranslatableComponent("gui.staticpower.sprinkler_description")).withStyle(ChatFormatting.BLUE));
		tooltip.add(new TextComponent("� ").append(new TranslatableComponent("gui.staticpower.sprinkler_experience_description")).withStyle(ChatFormatting.GREEN));
		tooltip.add(new TextComponent("� ").append(new TranslatableComponent("gui.staticpower.redstone_control_enabled")).withStyle(ChatFormatting.DARK_RED));
	}
}
