package theking530.staticpower.blockentities.machines.hydroponicpod;

import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blockentities.BlockEntityConfigurable;
import theking530.staticpower.blockentities.BlockEntityUpdateRequest;
import theking530.staticpower.blockentities.components.control.AbstractProcesingComponent.ProcessingCheckState;
import theking530.staticpower.blockentities.components.control.RecipeProcessingComponent;
import theking530.staticpower.blockentities.components.control.RecipeProcessingComponent.RecipeProcessingPhase;
import theking530.staticpower.blockentities.components.control.sideconfiguration.DefaultSideConfiguration;
import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.blockentities.components.items.InputServoComponent;
import theking530.staticpower.blockentities.components.items.InventoryComponent;
import theking530.staticpower.blockentities.components.items.ItemStackHandlerFilter;
import theking530.staticpower.blockentities.machines.hydroponicfarmer.BlockEntityHydroponicFarmer;
import theking530.staticpower.client.rendering.blockentity.BlockEntityRenderHydroponicPod;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerRecipeRegistry;
import theking530.staticpower.data.crafting.wrappers.hydroponicfarming.HydroponicFarmingRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.utilities.InventoryUtilities;

public class BlockEntityHydroponicPod extends BlockEntityConfigurable {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityHydroponicPod> TYPE = new BlockEntityTypeAllocator<>((type, pos, state) -> new BlockEntityHydroponicPod(pos, state),
			ModBlocks.HydroponicPod);

	public final InventoryComponent inputInventory;
	public final InventoryComponent internalInventory;
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
		}));
		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 1));

		// Setup the processing component to work with the redstone control component,
		// upgrade component and energy component.
		registerComponent(processingComponent = new RecipeProcessingComponent<HydroponicFarmingRecipe>("ProcessingComponent",
				StaticPowerConfig.SERVER.poweredGrinderProcessingTime.get(), RecipeProcessingComponent.MOVE_TIME, HydroponicFarmingRecipe.RECIPE_TYPE, this::getMatchParameters,
				this::canProcessRecipe, this::moveInputs, this::processingCompleted));

		// Setup the I/O servos.
		registerComponent(new InputServoComponent("InputServo", 4, inputInventory, 0));
		owningFarmer = null;
	}

	public void process() {
		if (hasFarmer() && isGrowing()) {
			owningFarmer.fluidTankComponent.drain(1, FluidAction.EXECUTE);
		}

		processingComponent.setMaxProcessingTime(100);
		this.addUpdateRequest(BlockEntityUpdateRequest.blockUpdate(), true);
	}

	protected RecipeMatchParameters getMatchParameters(RecipeProcessingPhase location) {
		if (location == RecipeProcessingPhase.PROCESSING) {
			return new RecipeMatchParameters(internalInventory.getStackInSlot(0));
		} else {
			return new RecipeMatchParameters(inputInventory.getStackInSlot(0));
		}
	}

	protected ProcessingCheckState canProcessRecipe(HydroponicFarmingRecipe recipe, RecipeProcessingPhase location) {
		if (owningFarmer == null) {
			return ProcessingCheckState.error("Missing farmer!");
		}

		if (owningFarmer.fluidTankComponent.drain(1, FluidAction.SIMULATE).getAmount() < 1) {
			return ProcessingCheckState.notEnoughFluid();
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
		InventoryUtilities.clearInventory(internalInventory);
	}

	public boolean isGrowing() {
		return processingComponent.getIsOnBlockState();
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
	}

	public void farmerBroken() {
		owningFarmer = null;
		processingComponent.setPowerComponent(null);
	}

	public boolean hasFarmer() {
		return owningFarmer != null;
	}

	public Optional<Block> getPlantBlockForRender() {
		ItemStack seeds = inputInventory.getStackInSlot(0);
		if (seeds.isEmpty()) {
			seeds = internalInventory.getStackInSlot(0);
			if (seeds.isEmpty()) {
				return Optional.empty();
			}
		}

		BlockItem seedItem = (BlockItem) seeds.getItem();
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
		return side == BlockSide.BACK ? (mode == MachineSideMode.Input || mode == MachineSideMode.Disabled) : false;
	}

	@Override
	protected DefaultSideConfiguration getDefaultSideConfiguration() {
		return SideConfigurationComponent.BACK_INPUT_ONLY;
	}
}
