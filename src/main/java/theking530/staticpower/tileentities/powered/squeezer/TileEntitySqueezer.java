package theking530.staticpower.tileentities.powered.squeezer;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.tileentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.client.rendering.tileentity.TileEntityRenderSqueezer;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.wrappers.squeezer.SqueezerRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.TileEntityMachine;
import theking530.staticpower.tileentities.components.control.AbstractProcesingComponent.ProcessingCheckState;
import theking530.staticpower.tileentities.components.control.RecipeProcessingComponent;
import theking530.staticpower.tileentities.components.control.RecipeProcessingComponent.RecipeProcessingLocation;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.components.fluids.FluidOutputServoComponent;
import theking530.staticpower.tileentities.components.fluids.FluidTankComponent;
import theking530.staticpower.tileentities.components.items.BatteryInventoryComponent;
import theking530.staticpower.tileentities.components.items.FluidContainerInventoryComponent;
import theking530.staticpower.tileentities.components.items.FluidContainerInventoryComponent.FluidContainerInteractionMode;
import theking530.staticpower.tileentities.components.items.InputServoComponent;
import theking530.staticpower.tileentities.components.items.InventoryComponent;
import theking530.staticpower.tileentities.components.items.ItemStackHandlerFilter;
import theking530.staticpower.tileentities.components.items.OutputServoComponent;
import theking530.staticpower.tileentities.components.items.UpgradeInventoryComponent;
import theking530.staticpower.utilities.InventoryUtilities;

public class TileEntitySqueezer extends TileEntityMachine {
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntitySqueezer> TYPE = new BlockEntityTypeAllocator<TileEntitySqueezer>((type, pos, state) -> new TileEntitySqueezer(pos, state), ModBlocks.Squeezer);

	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setTileEntitySpecialRenderer(TileEntityRenderSqueezer::new);
		}
	}

	public final InventoryComponent inputInventory;
	public final InventoryComponent internalInventory;
	public final InventoryComponent outputInventory;
	public final InventoryComponent batteryInventory;
	public final UpgradeInventoryComponent upgradesInventory;
	public final FluidContainerInventoryComponent fluidContainerComponent;

	public final RecipeProcessingComponent<SqueezerRecipe> processingComponent;
	public final FluidTankComponent fluidTankComponent;

	public TileEntitySqueezer(BlockPos pos, BlockState state) {
		super(TYPE, pos, state, StaticPowerTiers.BASIC);

		// Get the tier.
		StaticPowerTier tier = StaticPowerConfig.getTier(StaticPowerTiers.BASIC);

		// Setup the input inventory to only accept items that have a valid recipe.
		registerComponent(inputInventory = new InventoryComponent("InputInventory", 1, MachineSideMode.Input).setShiftClickEnabled(true).setFilter(new ItemStackHandlerFilter() {
			public boolean canInsertItem(int slot, ItemStack stack) {
				return processingComponent.getRecipe(new RecipeMatchParameters(stack).ignoreItemCounts()).isPresent();
			}

		}));

		// Setup all the other inventories.
		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 1));
		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 1, MachineSideMode.Output));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", energyStorage.getStorage()));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));

		// Setup the processing component.
		registerComponent(processingComponent = new RecipeProcessingComponent<SqueezerRecipe>("ProcessingComponent", SqueezerRecipe.RECIPE_TYPE,
				StaticPowerConfig.SERVER.squeezerProcessingTime.get(), this::getMatchParameters, this::moveInputs, this::canProcessRecipe, this::processingCompleted));

		// Initialize the processing component to work with the redstone control
		// component, upgrade component and energy component.
		processingComponent.setShouldControlBlockState(true);
		processingComponent.setUpgradeInventory(upgradesInventory);
		processingComponent.setEnergyComponent(energyStorage);
		processingComponent.setRedstoneControlComponent(redstoneControlComponent);

		// Setup the I/O servos.
		registerComponent(new OutputServoComponent("OutputServo", 2, outputInventory));
		registerComponent(new InputServoComponent("InputServo", 2, inputInventory));

		// Setup the fluid tank and fluid output servo.
		registerComponent(fluidTankComponent = new FluidTankComponent("FluidTank", tier.defaultTankCapacity.get()).setCapabilityExposedModes(MachineSideMode.Output)
				.setUpgradeInventory(upgradesInventory));
		fluidTankComponent.setCanFill(false);
		fluidTankComponent.setAutoSyncPacketsEnabled(true);
		registerComponent(new FluidOutputServoComponent("FluidOutputServoComponent", 100, fluidTankComponent, MachineSideMode.Output));
		registerComponent(fluidContainerComponent = new FluidContainerInventoryComponent("FluidContainerServo", fluidTankComponent).setMode(FluidContainerInteractionMode.FILL));

		// Set the energy storage upgrade inventory.
		energyStorage.setUpgradeInventory(upgradesInventory);
	}

	protected RecipeMatchParameters getMatchParameters(RecipeProcessingLocation location) {
		if (location == RecipeProcessingLocation.INTERNAL) {
			return new RecipeMatchParameters(internalInventory.getStackInSlot(0));
		} else {
			return new RecipeMatchParameters(inputInventory.getStackInSlot(0));
		}
	}

	protected ProcessingCheckState moveInputs(SqueezerRecipe recipe) {
		// If this recipe has an item output that we cannot put into the output slot,
		// continue waiting.
		if (recipe.hasItemOutput() && !InventoryUtilities.canFullyInsertStackIntoSlot(outputInventory, 0, recipe.getOutput().getItem())) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}

		// If this recipe has a fluid output that we cannot put into the output tank,
		// continue waiting.
		if (recipe.hasOutputFluid() && fluidTankComponent.fill(recipe.getOutputFluid(), FluidAction.SIMULATE) != recipe.getOutputFluid().getAmount()) {
			return ProcessingCheckState.outputTankCannotTakeFluid();
		}

		// Transfer the items to the internal inventory.
		transferItemInternally(inputInventory, 0, internalInventory, 0);

		// Set the power usage.
		processingComponent.setProcessingPowerUsage(recipe.getPowerCost());
		processingComponent.setMaxProcessingTime(recipe.getProcessingTime());
		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState canProcessRecipe(SqueezerRecipe recipe) {
		// If this recipe has an item output that we cannot put into the output slot,
		// continue waiting.
		if (recipe.hasItemOutput() && !InventoryUtilities.canFullyInsertStackIntoSlot(outputInventory, 0, recipe.getOutput().getItem())) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}

		// If this recipe has a fluid output that we cannot put into the output tank,
		// continue waiting.
		if (recipe.hasOutputFluid() && fluidTankComponent.fill(recipe.getOutputFluid(), FluidAction.SIMULATE) != recipe.getOutputFluid().getAmount()) {
			if (!fluidTankComponent.getFluid().isEmpty() && fluidTankComponent.getFluid().getFluid() != recipe.getOutputFluid().getFluid()) {
				return ProcessingCheckState.outputFluidDoesNotMatch();
			} else {
				return ProcessingCheckState.outputTankCannotTakeFluid();
			}
		}
		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState processingCompleted(SqueezerRecipe recipe) {

		// Insert the outputs
		// Check the dice roll for the output.
		if (recipe.hasItemOutput()) {
			ItemStack outputItem = recipe.getOutput().calculateOutput();
			outputInventory.insertItem(0, outputItem, false);
		}

		// Fill the output tank.
		if (recipe.hasOutputFluid()) {
			fluidTankComponent.fill(recipe.getOutputFluid(), FluidAction.EXECUTE);
		}

		// Clear the internal inventory.
		internalInventory.setStackInSlot(0, ItemStack.EMPTY);
		return ProcessingCheckState.ok();
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerSqueezer(windowId, inventory, this);
	}
}
