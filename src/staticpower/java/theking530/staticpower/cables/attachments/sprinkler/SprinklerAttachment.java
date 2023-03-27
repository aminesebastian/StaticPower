package theking530.staticpower.cables.attachments.sprinkler;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import theking530.staticcore.blockentity.components.control.redstonecontrol.RedstoneMode;
import theking530.staticcore.cablenetwork.AbstractCableProviderComponent;
import theking530.staticcore.cablenetwork.attachment.AbstractCableAttachment;
import theking530.staticcore.crafting.RecipeMatchParameters;
import theking530.staticcore.utilities.StaticPowerRarities;
import theking530.staticcore.utilities.math.SDMath;
import theking530.staticcore.utilities.math.Vector3D;
import theking530.staticcore.world.WorldUtilities;
import theking530.staticpower.cables.fluid.FluidCableComponent;
import theking530.staticpower.cables.fluid.FluidNetworkModule;
import theking530.staticpower.data.crafting.wrappers.fertilization.FertalizerRecipe;
import theking530.staticpower.init.ModCreativeTabs;
import theking530.staticpower.init.ModFluids;
import theking530.staticpower.init.ModRecipeTypes;
import theking530.staticpower.init.cables.ModCableModules;
import theking530.staticpower.init.tags.ModFluidTags;

public class SprinklerAttachment extends AbstractCableAttachment {
	private static final Vector3D SPRINKLER_BOUNDS = new Vector3D(2.5f, 2.5f, 2.5f);
	private final ResourceLocation model;
	private final ResourceLocation tierType;

	public SprinklerAttachment(ResourceLocation tierType, ResourceLocation model) {
		super(ModCreativeTabs.CABLES);
		this.model = model;
		this.tierType = tierType;
	}

	@Override
	public void onAddedToCable(ItemStack attachment, Direction side, AbstractCableProviderComponent cable) {
		super.onAddedToCable(attachment, side, cable);
		getAttachmentTag(attachment).putInt("redstone_mode", RedstoneMode.Low.ordinal());
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
		if (ModFluidTags.matches(ModFluids.LiquidExperience.getTag(), fluidContained.getFluid())) {
			return false;
		}

		// Use fluid and spawn the experience orb.
		if (!fluidCable.getLevel().isClientSide()) {
			fluidCable.<FluidNetworkModule>getNetworkModule(ModCableModules.Fluid.get()).ifPresent(network -> {
				int drained = network.supply(fluidCable.getPos(), 5, FluidAction.EXECUTE).getAmount();
				WorldUtilities.dropExperience(fluidCable.getLevel(), side, fluidCable.getPos(), drained);
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
	protected boolean handleFertilization(ItemStack attachment, Direction side, FluidStack fluidContained, FluidCableComponent fluidCable) {
		// Get the fertilization recipe. If one does not exist, return early.
		Level level = fluidCable.getLevel();
		RecipeManager recipeManager = level.getRecipeManager();
		Optional<FertalizerRecipe> recipe = recipeManager.getRecipeFor(ModRecipeTypes.FERTALIZER_RECIPE_TYPE.get(), new RecipeMatchParameters(fluidContained), level);
		if (recipe.isEmpty()) {
			return false;
		}

		// Spawn the particles on the client, fertilize and use the fluid on the server.
		if (fluidCable.getLevel().isClientSide()) {
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
			float growthChange = recipe.get().getFertalizationAmount();
			if (growthChange == 0) {
				growthChange = 0.02f;
			}
			growthChange /= 20;

			// Use fluid.
			fluidCable.<FluidNetworkModule>getNetworkModule(ModCableModules.Fluid.get()).ifPresent(network -> {
				network.supply(fluidCable.getPos(), 1, FluidAction.EXECUTE);
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
						tempCrop.performBonemeal((ServerLevel) fluidCable.getLevel(), fluidCable.getLevel().getRandom(), target, cropState);
						// Spawn some fertilziation particles.
						((ServerLevel) fluidCable.getLevel()).sendParticles(ParticleTypes.HAPPY_VILLAGER, target.getX() + 0.5D, target.getY() + 1.0D, target.getZ() + 0.5D, 1, 0.0D, 0.0D, 0.0D, 0.0D);
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
	public ResourceLocation getModel(ItemStack attachment, BlockAndTintGetter level, BlockPos pos) {
		return model;
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		return StaticPowerRarities.getRarityForTier(this.tierType);
	}

	@Override
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean isShowingAdvanced) {
		if (!isShowingAdvanced) {
			tooltip.add(Component.translatable("gui.staticpower.sprinkler_tooltip").withStyle(ChatFormatting.DARK_AQUA));
		}
	}

	@Override
	public void getAdvancedTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip) {
		tooltip.add(Component.literal("� ").append(Component.translatable("gui.staticpower.sprinkler_description")).withStyle(ChatFormatting.BLUE));
		tooltip.add(Component.literal("� ").append(Component.translatable("gui.staticpower.sprinkler_experience_description")).withStyle(ChatFormatting.GREEN));
		tooltip.add(Component.literal("� ").append(Component.translatable("gui.staticpower.redstone_control_enabled")).withStyle(ChatFormatting.DARK_RED));
	}
}
