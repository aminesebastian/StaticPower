package theking530.staticpower.tileentities.nonpowered.cauldron;

import java.util.List;
import java.util.Optional;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.tileentity.TileEntityTypeAllocator;
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
	public static final TileEntityTypeAllocator<TileEntityCauldron> RUSTY = new TileEntityTypeAllocator<>((type) -> new TileEntityCauldron(type), ModBlocks.RustyCauldron);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityCauldron> CLEAN = new TileEntityTypeAllocator<>((type) -> new TileEntityCauldron(type), ModBlocks.CleanCauldron);
	public static final int BOILING_TEMP = 100;

	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			RUSTY.setTileEntitySpecialRenderer(TileEntityRenderCauldron::new);
			CLEAN.setTileEntitySpecialRenderer(TileEntityRenderCauldron::new);
		}
	}

	public final FluidTankComponent internalTank;
	public final HeatStorageComponent heatStorage;

	public TileEntityCauldron(TileEntityTypeAllocator<TileEntityCauldron> allocator) {
		super(allocator);
		registerComponent(internalTank = new FluidTankComponent("InputFluidTank", 1000).setCanFill(true).setCapabilityExposedModes(MachineSideMode.Output).setAutoSyncPacketsEnabled(true));
		registerComponent(
				heatStorage = new HeatStorageComponent("HeatStorageComponent", 200.0f, 1.0f).setCapabiltiyFilter((amount, direction, action) -> action == HeatManipulationAction.COOL));
	}

	@Override
	public void process() {
		// Handle the upgrade tick on the server.
		if (!world.isRemote) {
			// If boiling, handle recipes.
			if (isBoiling()) {
				// Create the AABB to search within.
				AxisAlignedBB aabb = new AxisAlignedBB(pos.getX() + 0.125, pos.getY() + 0.1875, pos.getZ() + 0.125, pos.getX() + 0.875, pos.getY() + 1.0, pos.getZ() + 0.875);
				handleRecipes(aabb);

				// Render effects.
				if (SDMath.diceRoll(0.5) && internalTank.isFull()) {
					// Generate a random XZ Pos.
					Vector2D offset = new Vector2D(getWorld().getRandom().nextFloat(), getWorld().getRandom().nextFloat());
					offset.multiply(2.0f);
					offset.subtract(new Vector2D(1, 1));
					offset.multiply(0.35f);

					// Render boiling bubbles.
					BasicParticleType bubbleParticle = internalTank.getFluid().getFluid().getAttributes().getTemperature() > 500 ? ParticleTypes.FALLING_LAVA : ParticleTypes.BUBBLE_POP;
					((ServerWorld) getWorld()).spawnParticle(bubbleParticle, getPos().getX() + 0.5f + offset.getX(), getPos().getY() + 0.87, getPos().getZ() + 0.5f + offset.getY(), 1, 0.0D,
							0.0D, 0.0D, 0.01D);

					// Render a splash half of the time.
					BasicParticleType splashParticle = internalTank.getFluid().getFluid().getAttributes().getTemperature() > 500 ? ParticleTypes.LAVA : ParticleTypes.SPLASH;
					if (SDMath.diceRoll(0.5)) {
						((ServerWorld) getWorld()).spawnParticle(splashParticle, getPos().getX() + 0.5f + offset.getX(), getPos().getY() + 0.5, getPos().getZ() + 0.5f + offset.getY(), 1,
								0.0D, 0.0D, 0.0D, 0.5D);
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
			ItemEntity outputItemEntity = new ItemEntity(getWorld(), entity.getPosX(), entity.getPosY(), entity.getPosZ(), outputItem);
			outputItemEntity.setMotion(0, 0.275, 0);
			getWorld().addEntity(outputItemEntity);
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

	protected void handleRecipes(AxisAlignedBB bounds) {
		List<ItemEntity> items = getWorld().getEntitiesWithinAABB(ItemEntity.class, bounds);
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
				if(getType() == CLEAN.getType()) {
					Math.max(1, procesingTime /= 2);
				}
				
				// Create the new entity and add it to the world. Remove the incoming item stack.
				CauldronContainedEntity entity = new CauldronContainedEntity(this.getWorld(), item.getPosX(), item.getPosY(), item.getPosZ(), item.getItem().copy(),
						procesingTime);
				entity.setMotion(item.getMotion());
				entity.setPickupDelay(80); // Set this value initially a little high!
				getWorld().addEntity(entity);

				item.remove();
			});
		}
	}

	public Optional<CauldronRecipe> getRecipe(ItemStack input) {
		return StaticPowerRecipeRegistry.getRecipe(CauldronRecipe.RECIPE_TYPE, new RecipeMatchParameters(input).setFluids(internalTank.getFluid()));
	}
}
