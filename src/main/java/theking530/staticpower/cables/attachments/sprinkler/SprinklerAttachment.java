package theking530.staticpower.cables.attachments.sprinkler;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.block.IGrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
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
import theking530.staticpower.data.crafting.wrappers.farmer.FarmingFertalizerRecipe;

public class SprinklerAttachment extends AbstractCableAttachment {
	private static final Vector3D SPRINKLER_BOUNDS = new Vector3D(2.5f, 2.5f, 2.5f);
	private final ResourceLocation model;
	private final ResourceLocation tierType;

	public SprinklerAttachment(String name, ResourceLocation tierType, ResourceLocation model) {
		super(name);
		this.model = model;
		this.tierType = tierType;
	}

	@Override
	public void onAddedToCable(ItemStack attachment, Direction side, AbstractCableProviderComponent cable) {
		super.onAddedToCable(attachment, side, cable);
	}

	@Override
	public Vector3D getBounds() {
		return SPRINKLER_BOUNDS;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void attachmentTick(ItemStack attachment, Direction side, AbstractCableProviderComponent cable) {
		// Get the fluid cable and the fluid in the cable. Return early if there is no
		// fluid.
		FluidCableComponent fluidCable = (FluidCableComponent) cable;
		FluidStack fluid = fluidCable.getFluidInTank(0);
		if (fluid.isEmpty()) {
			return;
		}

		// Get the fertilization recipe. If one does not exist, return early.
		FarmingFertalizerRecipe recipe = StaticPowerRecipeRegistry.getRecipe(FarmingFertalizerRecipe.RECIPE_TYPE, new RecipeMatchParameters(fluid)).orElse(null);
		if (recipe == null) {
			return;
		}

		// Spawn the particles on the client, fertilize and use the fluid on the server.
		if (cable.getWorld().isRemote) {
			// Only do this on occasion for performance reasons.
			if (SDMath.diceRoll(0.4f)) {
				// Get a random offset.
				float random = cable.getWorld().getRandom().nextFloat();
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
				cable.getWorld().addParticle(ParticleTypes.FALLING_WATER, cable.getPos().getX() + random + 0.5f + direction.getX(), cable.getPos().getY() + random + 0.5f + direction.getY(),
						cable.getPos().getZ() + random + 0.5f + direction.getZ(), velocity.getX(), velocity.getY(), velocity.getZ());
			}
		} else {
			// Get the fertilization chance and divide it by 20. Handle cases where the
			// fertilization chance is == 0;
			float growthChange = recipe.getFertalizationAmount();
			if (growthChange == 0) {
				growthChange = 0.025f;
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
				BlockPos testTarget = cable.getPos().offset(Direction.DOWN, i);
				if (!cable.getWorld().getBlockState(testTarget).isAir()) {
					target = testTarget;
					break;
				}
			}

			// If there was no target, do nothing.
			if (target == null) {
				return;
			}

			// Check if we should add moisture to the block or the block below.
			BlockState belowBlock = cable.getWorld().getBlockState(target.offset(Direction.DOWN));
			BlockState cropState = cable.getWorld().getBlockState(target);

			// Perform the moisturization.
			if (belowBlock.hasProperty(FarmlandBlock.MOISTURE) && belowBlock.get(FarmlandBlock.MOISTURE) < 7) {
				cable.getWorld().setBlockState(target.offset(Direction.DOWN), belowBlock.with(FarmlandBlock.MOISTURE, 7), 1 | 2);
			} else if (cropState.hasProperty(FarmlandBlock.MOISTURE) && cropState.get(FarmlandBlock.MOISTURE) < 7) {
				cable.getWorld().setBlockState(target, cropState.with(FarmlandBlock.MOISTURE, 7), 1 | 2);
			}

			// If it passes, determine the farm ground level.
			if (SDMath.diceRoll(growthChange)) {
				// Check to make sure we can grow this block.
				if (cropState != null && cropState.getBlock() instanceof IGrowable) {
					// Get the growable.
					IGrowable tempCrop = (IGrowable) cropState.getBlock();

					// If we can grow this, grow it.
					if (tempCrop.canGrow(cable.getWorld(), target, cropState, false)) {
						tempCrop.grow((ServerWorld) cable.getWorld(), cable.getWorld().rand, target, cropState);
						// Spawn some fertilziation particles.
						((ServerWorld) cable.getWorld()).spawnParticle(ParticleTypes.HAPPY_VILLAGER, target.getX() + 0.5D, target.getY() + 1.0D, target.getZ() + 0.5D, 1, 0.0D, 0.0D, 0.0D,
								0.0D);
					}
				}
			}
		}
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
	public void getTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, boolean isShowingAdvanced) {
		if (!isShowingAdvanced) {
			tooltip.add(new StringTextComponent("Make it rain...indoors!").mergeStyle(TextFormatting.AQUA));
		}
	}

	@Override
	public void getAdvancedTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
		tooltip.add(new StringTextComponent("• ").append(new TranslationTextComponent("gui.staticpower.sprinkler_growth_rate").mergeStyle(TextFormatting.BLUE)));
		tooltip.add(new StringTextComponent("• ").append(new TranslationTextComponent("gui.staticpower.sprinkler_additional_growth_rate").mergeStyle(TextFormatting.GREEN)));
		tooltip.add(new StringTextComponent("• ")
				.append(new TranslationTextComponent("gui.staticpower.sprinkler_water_growth_rate").append(new StringTextComponent(TextFormatting.AQUA.toString() + "2.5%"))));
	}
}
