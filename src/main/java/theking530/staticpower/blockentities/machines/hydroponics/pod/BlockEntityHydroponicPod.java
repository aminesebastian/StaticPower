package theking530.staticpower.blockentities.machines.hydroponics.pod;

import java.util.List;
import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.StemGrownBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticcore.utilities.SDMath;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blockentities.BlockEntityConfigurable;
import theking530.staticpower.blockentities.components.control.AbstractProcesingComponent.ProcessingCheckState;
import theking530.staticpower.blockentities.components.control.RecipeProcessingComponent;
import theking530.staticpower.blockentities.components.control.RecipeProcessingComponent.RecipeProcessingPhase;
import theking530.staticpower.blockentities.components.control.sideconfiguration.DefaultSideConfiguration;
import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.blockentities.components.items.InventoryComponent;
import theking530.staticpower.blockentities.components.items.ItemStackHandlerFilter;
import theking530.staticpower.blockentities.components.items.OutputServoComponent;
import theking530.staticpower.blockentities.machines.cropfarmer.IFarmerHarvester.HarvestResult;
import theking530.staticpower.blockentities.machines.hydroponics.farmer.BlockEntityHydroponicFarmer;
import theking530.staticpower.client.rendering.blockentity.BlockEntityRenderHydroponicPod;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerRecipeRegistry;
import theking530.staticpower.data.crafting.wrappers.hydroponicfarming.HydroponicFarmingRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.utilities.InventoryUtilities;
import theking530.staticpower.utilities.ItemUtilities;
import theking530.staticpower.utilities.WorldUtilities;

public class BlockEntityHydroponicPod extends BlockEntityConfigurable {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityHydroponicPod> TYPE = new BlockEntityTypeAllocator<>((type, pos, state) -> new BlockEntityHydroponicPod(pos, state),
			ModBlocks.HydroponicPod);

	public final InventoryComponent inputInventory;
	public final InventoryComponent internalInventory;
	public final InventoryComponent outputInventory;
	public final RecipeProcessingComponent<HydroponicFarmingRecipe> processingComponent;

	private BlockEntityHydroponicFarmer owningFarmer;

	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setTileEntitySpecialRenderer(BlockEntityRenderHydroponicPod::new);
		}
	}

	public BlockEntityHydroponicPod(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);

		// Setup the inventories.
		registerComponent(inputInventory = new InventoryComponent("InputInventory", 1, MachineSideMode.Input).setShiftClickEnabled(true).setFilter(new ItemStackHandlerFilter() {
			public boolean canInsertItem(int slot, ItemStack stack) {
				return StaticPowerRecipeRegistry.getRecipe(HydroponicFarmingRecipe.RECIPE_TYPE, new RecipeMatchParameters(stack)).isPresent();
			}
		}).setSlotLimit(2));
		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 1));
		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 10, MachineSideMode.Output));
		registerComponent(new OutputServoComponent("OutputServo", 4, outputInventory));

		// Setup the processing component to work with the redstone control component,
		// upgrade component and energy component.
		registerComponent(processingComponent = new RecipeProcessingComponent<HydroponicFarmingRecipe>("ProcessingComponent",
				StaticPowerConfig.SERVER.poweredGrinderProcessingTime.get(), RecipeProcessingComponent.MOVE_TIME, HydroponicFarmingRecipe.RECIPE_TYPE, this::getMatchParameters,
				this::canProcessRecipe, this::moveInputs, this::processingCompleted).setShouldControlBlockState(true));

		owningFarmer = null;
	}

	@SuppressWarnings("resource")
	public void process() {
		if (processingComponent.getIsOnBlockState()) {
			if (SDMath.diceRoll(0.01f)) {
				float randomX = ((2 * getLevel().random.nextFloat()) - 1.0f) * 0.25f;
				float randomZ = ((2 * getLevel().random.nextFloat()) - 1.0f) * 0.25f;
				getLevel().addParticle(ParticleTypes.FALLING_DRIPSTONE_WATER, getBlockPos().getX() + randomX + 0.5, getBlockPos().getY() + 0.8,
						getBlockPos().getZ() + randomZ + 0.5, 0.0f, 0.0f, 0.0f);
			}
		}

		if (!getLevel().isClientSide()) {
			// Only do the following if we have a farmer.
			if (hasFarmer()) {
				// Use fluid if we're growing.
				if (isGrowing()) {
					owningFarmer.fluidTankComponent.drain(2, FluidAction.EXECUTE);
				}
			}
		}
	}

	protected RecipeMatchParameters getMatchParameters(RecipeProcessingPhase location) {
		if (location == RecipeProcessingPhase.PROCESSING) {
			return new RecipeMatchParameters(internalInventory.getStackInSlot(0));
		} else {
			return new RecipeMatchParameters(inputInventory.getStackInSlot(0));
		}
	}

	protected ProcessingCheckState canProcessRecipe(HydroponicFarmingRecipe recipe, RecipeProcessingPhase location) {
		if (!hasFarmer()) {
			return ProcessingCheckState.error("Missing farmer!");
		}

		if (!hasWater()) {
			return ProcessingCheckState.notEnoughFluid();
		}

		if (!InventoryUtilities.isInventoryEmpty(outputInventory)) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}

		return ProcessingCheckState.ok();
	}

	protected void moveInputs(HydroponicFarmingRecipe recipe) {
		transferItemInternally(inputInventory, 0, internalInventory, 0);
		processingComponent.setMaxProcessingTime(recipe.getProcessingTime());
		processingComponent.setProcessingPowerUsage(recipe.getPowerCost());
		processingComponent.setMaxProcessingTime(100);
	}

	protected void processingCompleted(HydroponicFarmingRecipe recipe) {
		// Iterate through all the drops. If the input is empty, check if any of the
		// drops can be put back into the seed slot.
		// If so, transfer. Then, whatever is left goes into the buffer.
		HarvestResult results = getDrops();
		for (ItemStack stack : results.getResults()) {
			if (StaticPowerRecipeRegistry.getRecipe(HydroponicFarmingRecipe.RECIPE_TYPE, new RecipeMatchParameters(stack)).isPresent()) {
				if (inputInventory.getStackInSlot(0).isEmpty() || ItemUtilities.areItemStacksStackable(inputInventory.getStackInSlot(0), stack)) {
					ItemStack remaining = inputInventory.insertItem(0, stack.copy(), false);
					stack.setCount(remaining.getCount());
				}
			}

			if (!stack.isEmpty()) {
				InventoryUtilities.insertItemIntoInventory(outputInventory, stack, false);
			}
		}

		// Clear the internal inventory.
		InventoryUtilities.clearInventory(internalInventory);
	}

	public boolean isGrowing() {
		return hasFarmer() && processingComponent.getIsOnBlockState();
	}

	public float getGrowthPercentage() {
		return (float) processingComponent.getCurrentProcessingTime() / processingComponent.getMaxProcessingTime();
	}

	public void onBlockBroken(BlockState state, BlockState newState, boolean isMoving) {
		super.onBlockBroken(state, newState, isMoving);
		if (hasFarmer()) {
			owningFarmer.podBroken(this);
		}
	}

	public void farmerCheckin(BlockEntityHydroponicFarmer farmer) {
		owningFarmer = farmer;
		processingComponent.setPowerComponent(owningFarmer.powerStorage);
		processingComponent.setRedstoneControlComponent(owningFarmer.redstoneControlComponent);
		processingComponent.setUpgradeInventory(owningFarmer.upgradesInventory);
	}

	public void farmerBroken() {
		owningFarmer = null;
		processingComponent.setPowerComponent(null);
	}

	public boolean hasFarmer() {
		return owningFarmer != null;
	}

	public boolean hasWater() {
		if (!hasFarmer()) {
			return false;
		}

		FluidStack simulated = owningFarmer.fluidTankComponent.getStorage().drain(2, FluidAction.SIMULATE);
		return simulated.getAmount() == 2 && simulated.getFluid() == Fluids.WATER;
	}

	protected HarvestResult getDrops() {
		Optional<BlockState> blockState = getPlantBlockStateForHarvest();
		if (blockState.isEmpty()) {
			return HarvestResult.empty();
		}

		List<ItemStack> outputItems = WorldUtilities.getBlockDrops(level, worldPosition, blockState.get());
		return HarvestResult.noTool(outputItems);
	}

	public Optional<BlockState> getPlantBlockStateForHarvest() {
		Optional<Block> block = getPlantBlockFromSeed(internalInventory.getStackInSlot(0));
		if (block.isEmpty()) {
			return Optional.empty();
		}

		BlockState result = block.get().defaultBlockState();
		if (block.get() instanceof CropBlock) {
			CropBlock crop = (CropBlock) block.get();
			result = crop.getStateForAge(crop.getMaxAge());

		} else if (block.get() instanceof StemBlock) {
			StemBlock stem = (StemBlock) block.get();
			StemGrownBlock fruit = stem.getFruit();
			result = fruit.defaultBlockState();
		}

		return Optional.of(result);
	}

	public Optional<Block> getPlantBlockForDisplay() {
		Optional<Block> block = getPlantBlockFromSeed(internalInventory.getStackInSlot(0));
		if (block.isEmpty()) {
			block = getPlantBlockFromSeed(inputInventory.getStackInSlot(0));
		}
		return block;
	}

	public Optional<Block> getPlantBlockFromSeed(ItemStack seed) {
		if (seed.isEmpty()) {
			return Optional.empty();
		}

		BlockItem seedItem = (BlockItem) seed.getItem();
		if (seedItem == null) {
			return Optional.empty();
		}

		if (seedItem.getBlock() instanceof CropBlock) {
			return Optional.of(seedItem.getBlock());
		} else if (seedItem.getBlock() instanceof StemBlock) {
			return Optional.of(seedItem.getBlock());
		}

		return Optional.empty();
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainierHydroponicPod(windowId, inventory, this);
	}

	@Override
	protected boolean isValidSideConfiguration(BlockSide side, MachineSideMode mode) {
		return side == BlockSide.BACK ? (mode == MachineSideMode.Output || mode == MachineSideMode.Disabled) : false;
	}

	@Override
	protected DefaultSideConfiguration getDefaultSideConfiguration() {
		return SideConfigurationComponent.BACK_OUTPUT_ONLY;
	}
}
