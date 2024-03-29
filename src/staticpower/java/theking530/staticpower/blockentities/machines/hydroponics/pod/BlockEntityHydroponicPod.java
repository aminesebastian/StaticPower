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
import theking530.staticcore.blockentity.BlockEntityBase;
import theking530.staticcore.blockentity.components.control.RedstoneControlComponent;
import theking530.staticcore.blockentity.components.control.processing.ConcretizedProductContainer;
import theking530.staticcore.blockentity.components.control.processing.ProcessingCheckState;
import theking530.staticcore.blockentity.components.control.processing.ProcessingContainer;
import theking530.staticcore.blockentity.components.control.processing.ProcessingContainer.CaptureType;
import theking530.staticcore.blockentity.components.control.processing.recipe.IRecipeProcessor;
import theking530.staticcore.blockentity.components.control.processing.recipe.RecipeProcessingComponent;
import theking530.staticcore.blockentity.components.control.redstonecontrol.RedstoneMode;
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticcore.blockentity.components.control.sideconfiguration.presets.BackOutputOnly;
import theking530.staticcore.blockentity.components.items.InventoryComponent;
import theking530.staticcore.blockentity.components.items.ItemStackHandlerFilter;
import theking530.staticcore.blockentity.components.items.OutputServoComponent;
import theking530.staticcore.crafting.CraftingUtilities;
import theking530.staticcore.crafting.RecipeMatchParameters;
import theking530.staticcore.init.StaticCoreProductTypes;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticcore.teams.ServerTeam;
import theking530.staticcore.utilities.item.InventoryUtilities;
import theking530.staticcore.utilities.item.ItemUtilities;
import theking530.staticcore.utilities.math.SDMath;
import theking530.staticcore.world.WorldUtilities;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blockentities.machines.cropfarmer.IFarmerHarvester.HarvestResult;
import theking530.staticpower.blockentities.machines.hydroponics.farmer.BlockEntityHydroponicFarmer;
import theking530.staticpower.client.rendering.blockentity.BlockEntityRenderHydroponicPod;
import theking530.staticpower.data.crafting.wrappers.hydroponicfarming.HydroponicFarmingRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModRecipeTypes;

public class BlockEntityHydroponicPod extends BlockEntityBase implements IRecipeProcessor<HydroponicFarmingRecipe> {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityHydroponicPod> TYPE = new BlockEntityTypeAllocator<>(
			"hydroponic_farmer_pod", (type, pos, state) -> new BlockEntityHydroponicPod(pos, state),
			ModBlocks.HydroponicPod);

	public final InventoryComponent inputInventory;
	public final InventoryComponent outputInventory;
	public final RedstoneControlComponent redstoneControlComponent;
	public final SideConfigurationComponent ioSideConfiguration;
	public final RecipeProcessingComponent<HydroponicFarmingRecipe> processingComponent;

	private BlockEntityHydroponicFarmer owningFarmer;

	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setTileEntitySpecialRenderer(BlockEntityRenderHydroponicPod::new);
		}
	}

	public BlockEntityHydroponicPod(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);
		registerComponent(redstoneControlComponent = new RedstoneControlComponent("RedstoneControlComponent",
				RedstoneMode.Ignore));
		registerComponent(
				ioSideConfiguration = new SideConfigurationComponent("SideConfiguration", BackOutputOnly.INSTANCE));

		// Setup the inventories.
		registerComponent(inputInventory = new InventoryComponent("InputInventory", 1, MachineSideMode.Input)
				.setShiftClickEnabled(true).setFilter(new ItemStackHandlerFilter() {
					public boolean canInsertItem(int slot, ItemStack stack) {
						return CraftingUtilities.getRecipe(ModRecipeTypes.HYDROPONIC_FARMING_RECIPE_TYPE.get(),
								new RecipeMatchParameters(getTeamComponent().getOwningTeamId(), stack), getLevel())
								.isPresent();
					}
				}).setSlotLimit(2));
		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 10, MachineSideMode.Output));
		registerComponent(new OutputServoComponent("OutputServo", 4, outputInventory));

		registerComponent(processingComponent = new RecipeProcessingComponent<HydroponicFarmingRecipe>(
				"ProcessingComponent", StaticPowerConfig.SERVER.hydroponicFarmerProcessingTime.get(),
				ModRecipeTypes.HYDROPONIC_FARMING_RECIPE_TYPE.get()));
		processingComponent.setShouldControlOnBlockState(true);

		owningFarmer = null;
	}

	@SuppressWarnings("resource")
	public void process() {
		if (processingComponent.isBlockStateOn()) {
			if (SDMath.diceRoll(0.01f)) {
				float randomX = ((2 * getLevel().random.nextFloat()) - 1.0f) * 0.25f;
				float randomZ = ((2 * getLevel().random.nextFloat()) - 1.0f) * 0.25f;
				getLevel().addParticle(ParticleTypes.FALLING_DRIPSTONE_WATER, getBlockPos().getX() + randomX + 0.5,
						getBlockPos().getY() + 0.8, getBlockPos().getZ() + randomZ + 0.5, 0.0f, 0.0f, 0.0f);
			}
		}

		if (!getLevel().isClientSide()) {
			// Only do the following if we have a farmer.
			if (hasFarmer() && hasWater() && isGrowing()) {
				// Use fluid if we're growing.
				processingComponent.getProductionToken(StaticCoreProductTypes.Fluid.get()).setConsumptionPerSecond(
						(ServerTeam) getTeamComponent().getOwningTeam(), owningFarmer.fluidTankComponent.getFluid(),
						(2) * 20);
				owningFarmer.fluidTankComponent.drain(2, FluidAction.EXECUTE);
				processingComponent.getProductionToken(StaticCoreProductTypes.Fluid.get()).consumed(
						(ServerTeam) getTeamComponent().getOwningTeam(), owningFarmer.fluidTankComponent.getFluid(), 2);
			}
		}
	}

	public boolean isGrowing() {
		return hasFarmer() && processingComponent.isBlockStateOn();
	}

	public float getGrowthPercentage() {
		return processingComponent.getProcessingTimer().getCompletionRatio();
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

	protected HarvestResult getDrops(ItemStack seed) {
		Optional<BlockState> blockState = getPlantBlockStateForHarvest(seed);
		if (blockState.isEmpty()) {
			return HarvestResult.empty();
		}

		List<ItemStack> outputItems = WorldUtilities.getBlockDrops(level, worldPosition, blockState.get());
		return HarvestResult.noTool(outputItems);
	}

	public Optional<BlockState> getPlantBlockStateForHarvest(ItemStack seed) {
		Optional<Block> block = getPlantBlockFromSeed(seed);
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

	public Optional<BlockState> getPlantBlockStateForDisplay() {
		Optional<Block> block = getPlantBlockForDisplay();
		if (block.isEmpty()) {
			return Optional.empty();
		}

		BlockState result = block.get().defaultBlockState();
		if (block.get() instanceof CropBlock) {
			CropBlock crop = (CropBlock) block.get();
			int age = ((int) (getGrowthPercentage() * crop.getMaxAge())) % crop.getMaxAge();
			result = crop.getStateForAge(age);

		} else if (block.get() instanceof StemBlock) {
			StemBlock stem = (StemBlock) block.get();
			StemGrownBlock fruit = stem.getFruit();
			result = fruit.defaultBlockState();
		}

		return Optional.of(result);
	}

	public Optional<Block> getPlantBlockForDisplay() {
		Optional<Block> block = Optional.empty();
		if (processingComponent.getProcessingInputs().hasItems()) {
			block = getPlantBlockFromSeed(processingComponent.getProcessingInputs().getItem(0));
		}
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
	public RecipeMatchParameters getRecipeMatchParameters(
			RecipeProcessingComponent<HydroponicFarmingRecipe> component) {
		return new RecipeMatchParameters(getTeamComponent().getOwningTeamId(), inputInventory.getStackInSlot(0));
	}

	@Override
	public void captureOutputs(RecipeProcessingComponent<HydroponicFarmingRecipe> component,
			HydroponicFarmingRecipe recipe, ConcretizedProductContainer outputContainer) {

		ItemStack inputSeed = inputInventory.getStackInSlot(0);
		HarvestResult results = getDrops(inputSeed);
		for (ItemStack stack : results.getResults()) {
			outputContainer.addItem(stack, CaptureType.BOTH);
		}
	}

	@Override
	public ProcessingCheckState canStartProcessingRecipe(RecipeProcessingComponent<HydroponicFarmingRecipe> component,
			HydroponicFarmingRecipe recipe, ConcretizedProductContainer outputContainer) {
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

	@Override
	public void captureInputs(RecipeProcessingComponent<HydroponicFarmingRecipe> component,
			HydroponicFarmingRecipe recipe, ProcessingContainer processingContainer,
			ConcretizedProductContainer inputContainer) {
		inputContainer.addItem(inputInventory.extractItem(0, recipe.getInput().getCount(), false));
	}

	@Override
	public void onProcessingCompleted(RecipeProcessingComponent<HydroponicFarmingRecipe> component,
			ProcessingContainer processingContainer) {
		// Iterate through all the drops. If the input is empty, check if any of the
		// drops can be put back into the seed slot.
		// If so, transfer. Then, whatever is left goes into the buffer.
		for (ItemStack stack : processingContainer.getOutputs().getItems()) {
			stack = stack.copy();
			if (CraftingUtilities
					.getRecipe(ModRecipeTypes.HYDROPONIC_FARMING_RECIPE_TYPE.get(),
							new RecipeMatchParameters(getTeamComponent().getOwningTeamId(), stack), getLevel())
					.isPresent()) {
				if (inputInventory.getStackInSlot(0).isEmpty()
						|| ItemUtilities.areItemStacksStackable(inputInventory.getStackInSlot(0), stack)) {
					ItemStack remaining = inputInventory.insertItem(0, stack.copy(), false);
					stack.setCount(remaining.getCount());
				}
			}

			if (!stack.isEmpty()) {
				InventoryUtilities.insertItemIntoInventory(outputInventory, stack, false);
			}
		}
	}
}
