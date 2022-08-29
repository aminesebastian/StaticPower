package theking530.staticpower.tileentities.nonpowered.cauldron;

import java.util.List;
import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.tileentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticcore.utilities.SDMath;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.client.rendering.tileentity.TileEntityRenderCauldron;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerRecipeRegistry;
import theking530.staticpower.data.crafting.wrappers.cauldron.CauldronRecipe;
import theking530.staticpower.entities.cauldroncontainedentity.CauldronContainedEntity;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.TileEntityBase;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.components.fluids.FluidTankComponent;
import theking530.staticpower.tileentities.components.heat.HeatStorageComponent;
import theking530.staticpower.tileentities.components.heat.HeatStorageComponent.HeatManipulationAction;

public class TileEntityCauldron extends TileEntityBase {
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityCauldron> RUSTY = new BlockEntityTypeAllocator<>(
			(type, pos, state) -> new TileEntityCauldron(type, pos, state), ModBlocks.RustyCauldron);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityCauldron> CLEAN = new BlockEntityTypeAllocator<>(
			(type, pos, state) -> new TileEntityCauldron(type, pos, state), ModBlocks.CleanCauldron);
	public static final int BOILING_TEMP = 100;

	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			RUSTY.setTileEntitySpecialRenderer(TileEntityRenderCauldron::new);
			CLEAN.setTileEntitySpecialRenderer(TileEntityRenderCauldron::new);
		}
	}

	public final FluidTankComponent internalTank;
	public final HeatStorageComponent heatStorage;

	public TileEntityCauldron(BlockEntityTypeAllocator<TileEntityCauldron> allocator, BlockPos pos, BlockState state) {
		super(allocator, pos, state);
		registerComponent(internalTank = new FluidTankComponent("InputFluidTank", 1000).setCanFill(true)
				.setCapabilityExposedModes(MachineSideMode.Output).setAutoSyncPacketsEnabled(true));

		// Only allow this to be heated by other sources.
		registerComponent(heatStorage = new HeatStorageComponent("HeatStorageComponent", 200, 1.0f)
				.setCapabiltiyFilter((amount, direction, action) -> action == HeatManipulationAction.HEAT));
	}

	@Override
	public void process() {
		// Handle the upgrade tick on the server.
		if (!level.isClientSide) {
			// If boiling, handle recipes.
			if (isBoiling()) {
				// Create the AABB to search within.
				AABB aabb = new AABB(worldPosition.getX() + 0.125, worldPosition.getY() + 0.1875,
						worldPosition.getZ() + 0.125, worldPosition.getX() + 0.875, worldPosition.getY() + 1.0,
						worldPosition.getZ() + 0.875);
				handleRecipes(aabb);

				// Render effects.
				if (SDMath.diceRoll(0.5) && internalTank.isFull()) {
					// Generate a random XZ Pos.
					Vector2D offset = new Vector2D(getLevel().getRandom().nextFloat(),
							getLevel().getRandom().nextFloat());
					offset.multiply(2.0f);
					offset.subtract(new Vector2D(1, 1));
					offset.multiply(0.35f);

					// Render boiling bubbles.
					SimpleParticleType bubbleParticle = internalTank.getFluid().getFluid().getAttributes()
							.getTemperature() > 500 ? ParticleTypes.FALLING_LAVA : ParticleTypes.BUBBLE_POP;
					((ServerLevel) getLevel()).sendParticles(bubbleParticle,
							getBlockPos().getX() + 0.5f + offset.getX(), getBlockPos().getY() + 0.87,
							getBlockPos().getZ() + 0.5f + offset.getY(), 1, 0.0D, 0.0D, 0.0D, 0.01D);

					// Render a splash half of the time.
					SimpleParticleType splashParticle = internalTank.getFluid().getFluid().getAttributes()
							.getTemperature() > 500 ? ParticleTypes.LAVA : ParticleTypes.SPLASH;
					if (SDMath.diceRoll(0.5)) {
						((ServerLevel) getLevel()).sendParticles(splashParticle,
								getBlockPos().getX() + 0.5f + offset.getX(), getBlockPos().getY() + 0.5,
								getBlockPos().getZ() + 0.5f + offset.getY(), 1, 0.0D, 0.0D, 0.0D, 0.5D);
					}
				}
			}
		}
	}

	public boolean isBoiling() {
		return heatStorage.getStorage().getCurrentHeat() >= BOILING_TEMP;
	}

	/**
	 * Attempts to complete the crafting from a cauldron. Returns the amount of
	 * items that were crafted.
	 * 
	 * @param entity
	 * @param item
	 * @return The number of items that were crafted.
	 */
	public int completeCooking(CauldronContainedEntity entity, ItemStack item) {
		// If there is no recipe, return false.
		CauldronRecipe recipe = getRecipe(item).orElse(null);
		if (recipe == null) {
			return 0;
		}

		// Capture the max craftable.
		int maxCraftable = recipe.shouldDrainCauldron() ? 1 : item.getCount();
		if (!recipe.getOutputFluid().isEmpty()) {
			int remainingTankSpace = internalTank.getCapacity() - internalTank.getFluidAmount();
			maxCraftable = Math.min(item.getCount(), remainingTankSpace / recipe.getOutputFluid().getAmount());
		}

		// IF the max craftable is 0, return early.
		if (maxCraftable <= 0) {
			return 0;
		}

		// If there is an item output, perform the change.
		if (!recipe.getOutput().isEmpty()) {
			// Get the output item and grow the stack size in relation to the input stack
			// size.
			ItemStack outputItem = recipe.getOutput().calculateOutput();
			outputItem.setCount(outputItem.getCount() * maxCraftable);

			// Create the entity and make it bounce up.
			ItemEntity outputItemEntity = new ItemEntity(getLevel(), entity.getX(), entity.getY(), entity.getZ(),
					outputItem);
			outputItemEntity.setDeltaMovement(0, 0.275, 0);
			getLevel().addFreshEntity(outputItemEntity);
		}

		// Drain the cauldron if we should.
		if (recipe.shouldDrainCauldron()) {
			internalTank.drain(Integer.MAX_VALUE, FluidAction.EXECUTE);
		}

		// Change/add the fluid.
		if (!recipe.getOutputFluid().isEmpty()) {
			// Create the fluid stack to fill with.
			FluidStack fillFluid = recipe.getOutputFluid().copy();
			fillFluid.setAmount(recipe.getOutputFluid().getAmount() * maxCraftable);
			internalTank.fill(fillFluid, FluidAction.EXECUTE);
		}

		// If we made it this far, return the amount we crafted.
		return maxCraftable;
	}

	protected void handleRecipes(AABB bounds) {
		List<ItemEntity> items = getLevel().getEntitiesOfClass(ItemEntity.class, bounds);
		for (ItemEntity item : items) {
			// Since RubberWoodBarkEntity inherits from ItemEntity, we must check in order
			// to avoid a loop.
			if (item instanceof CauldronContainedEntity) {
				continue;
			}

			// Check to see if we have a recipe to produce.
			getRecipe(item.getItem()).ifPresent(recipe -> {
				// Make sure we can take the output fluid if the recipe has it.
				if (!recipe.getOutputFluid().isEmpty()) {
					int filled = internalTank.fill(recipe.getOutputFluid(), FluidAction.SIMULATE);
					if (recipe.getOutputFluid().getAmount() != filled) {
						return;
					}
				}

				// Double time for clean cauldron.
				int procesingTime = recipe.getRequiredTimeInCauldron();
				if (getType() == CLEAN.getType()) {
					Math.max(1, procesingTime /= 2);
				}

				// Create the new entity and add it to the world. Remove the incoming item
				// stack.
				CauldronContainedEntity entity = new CauldronContainedEntity(this.getLevel(), item.getX(), item.getY(),
						item.getZ(), item.getItem().copy(), procesingTime);
				entity.setDeltaMovement(item.getDeltaMovement());
				entity.setPickUpDelay(80); // Set this value initially a little high!
				getLevel().addFreshEntity(entity);

				item.remove(RemovalReason.DISCARDED);
			});
		}
	}

	public Optional<CauldronRecipe> getRecipe(ItemStack input) {
		return StaticPowerRecipeRegistry.getRecipe(CauldronRecipe.RECIPE_TYPE,
				new RecipeMatchParameters(input).setFluids(internalTank.getFluid()));
	}
}
