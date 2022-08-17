package theking530.staticpower.tileentities.powered.enchanter;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.initialization.tileentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.wrappers.enchanter.EnchanterRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.TileEntityMachine;
import theking530.staticpower.tileentities.components.control.AbstractProcesingComponent.ProcessingCheckState;
import theking530.staticpower.tileentities.components.control.RecipeProcessingComponent;
import theking530.staticpower.tileentities.components.control.RecipeProcessingComponent.RecipeProcessingLocation;
import theking530.staticpower.tileentities.components.control.sideconfiguration.DefaultSideConfiguration;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.tileentities.components.fluids.FluidInputServoComponent;
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

/**
 * Baseic furnace machine. Same as a Vanila furnace except powered.
 * 
 * @author Amine Sebastian
 *
 */
public class TileEntityEnchanter extends TileEntityMachine {
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityEnchanter> TYPE = new BlockEntityTypeAllocator<>((type, pos, state) -> new TileEntityEnchanter(pos, state), ModBlocks.Enchanter.get());

	public final InventoryComponent inputInventory;
	public final InventoryComponent enchantableInventory;
	public final InventoryComponent outputInventory;
	public final InventoryComponent internalInventory;
	public final FluidTankComponent fluidTankComponent;
	public final FluidContainerInventoryComponent fluidContainerComponent;

	public final BatteryInventoryComponent batteryInventory;
	public final UpgradeInventoryComponent upgradesInventory;
	public final RecipeProcessingComponent<EnchanterRecipe> processingComponent;

	public TileEntityEnchanter(BlockPos pos, BlockState state) {
		super(TYPE, pos, state, StaticPowerTiers.LUMUM);

		// Get the tier object.
		StaticPowerTier tier = StaticPowerConfig.getTier(StaticPowerTiers.LUMUM);

		// Setup the input inventory to only accept items that have a valid recipe.
		registerComponent(inputInventory = new InventoryComponent("InputInventory", 3, MachineSideMode.Input).setShiftClickEnabled(true).setFilter(new ItemStackHandlerFilter() {
			public boolean canInsertItem(int slot, ItemStack stack) {
				return processingComponent.getRecipe(new RecipeMatchParameters(stack).ignoreItemCounts()).isPresent();
			}
		}));

		registerComponent(enchantableInventory = new InventoryComponent("EnchantableInventory", 1, MachineSideMode.Input2).setShiftClickEnabled(true));

		// Setup all the other inventories.
		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 1, MachineSideMode.Output));
		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 4));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", energyStorage.getStorage()));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));

		// Setup the processing component.
		registerComponent(processingComponent = new RecipeProcessingComponent<EnchanterRecipe>("ProcessingComponent", EnchanterRecipe.RECIPE_TYPE, 1, this::getMatchParameters, this::moveInputs,
				this::canStartProcessRecipe, this::processingCompleted).setCanContinueProcessingLambda(this::canContinueProcessingRecipe));

		// Initialize the processing component to work with the redstone control
		// component, upgrade component and energy component.
		processingComponent.setShouldControlBlockState(true);
		processingComponent.setUpgradeInventory(upgradesInventory);
		processingComponent.setEnergyComponent(energyStorage);
		processingComponent.setRedstoneControlComponent(redstoneControlComponent);
		processingComponent.setProcessingPowerUsage(StaticPowerConfig.SERVER.poweredFurnacePowerUsage.get());

		// Setup the I/O servos.
		registerComponent(new InputServoComponent("InputServo", 4, inputInventory, 0, 1, 2));
		registerComponent(new InputServoComponent("EnchantableServo", 4, enchantableInventory, 0));
		registerComponent(new OutputServoComponent("OutputServo", 4, outputInventory, 0));

		// Setup the fluid tanks and servo.
		registerComponent(
				fluidTankComponent = new FluidTankComponent("FluidTank", tier.defaultTankCapacity.get()).setCapabilityExposedModes(MachineSideMode.Input).setUpgradeInventory(upgradesInventory));
		fluidTankComponent.setCanDrain(false);
		fluidTankComponent.setAutoSyncPacketsEnabled(true);

		registerComponent(new FluidInputServoComponent("FluidInputServoComponent", 100, fluidTankComponent, MachineSideMode.Input));

		// Create the fluid container component.
		registerComponent(fluidContainerComponent = new FluidContainerInventoryComponent("FluidContainerServo", fluidTankComponent).setMode(FluidContainerInteractionMode.DRAIN));

		// Set the energy storage upgrade inventory.
		energyStorage.setUpgradeInventory(upgradesInventory);
	}

	protected RecipeMatchParameters getMatchParameters(RecipeProcessingLocation location) {
		if (location == RecipeProcessingLocation.INTERNAL) {
			return new RecipeMatchParameters(internalInventory.getStackInSlot(0), internalInventory.getStackInSlot(1), internalInventory.getStackInSlot(2), internalInventory.getStackInSlot(3))
					.setFluids(fluidTankComponent.getFluid());
		} else {
			return new RecipeMatchParameters(inputInventory.getStackInSlot(0), inputInventory.getStackInSlot(1), inputInventory.getStackInSlot(2), enchantableInventory.getStackInSlot(0))
					.setFluids(fluidTankComponent.getFluid());
		}
	}

	protected ProcessingCheckState moveInputs(EnchanterRecipe recipe) {
		if (!InventoryUtilities.canFullyInsertStackIntoSlot(outputInventory, 0, recipe.getEnchantedVersion(enchantableInventory.getStackInSlot(0)))) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}

		transferItemInternally(inputInventory, 0, internalInventory, 0);
		transferItemInternally(inputInventory, 1, internalInventory, 1);
		transferItemInternally(inputInventory, 2, internalInventory, 2);
		transferItemInternally(enchantableInventory, 0, internalInventory, 3);
		processingComponent.setMaxProcessingTime(recipe.getProcessingTime());
		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState canStartProcessRecipe(EnchanterRecipe recipe) {
		if (!InventoryUtilities.canFullyInsertStackIntoSlot(outputInventory, 0, recipe.getEnchantedVersion(enchantableInventory.getStackInSlot(0)))) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}
		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState canContinueProcessingRecipe(EnchanterRecipe recipe) {
		if (!InventoryUtilities.canFullyInsertStackIntoSlot(outputInventory, 0, recipe.getEnchantedVersion(internalInventory.getStackInSlot(3)))) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}
		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState processingCompleted(EnchanterRecipe recipe) {
		outputInventory.insertItem(0, recipe.getEnchantedVersion(internalInventory.getStackInSlot(3)), false);
		internalInventory.setStackInSlot(0, ItemStack.EMPTY);
		internalInventory.setStackInSlot(1, ItemStack.EMPTY);
		internalInventory.setStackInSlot(2, ItemStack.EMPTY);
		internalInventory.setStackInSlot(3, ItemStack.EMPTY);
		return ProcessingCheckState.ok();
	}

	@Override
	protected boolean isValidSideConfiguration(BlockSide side, MachineSideMode mode) {
		return mode == MachineSideMode.Disabled || mode == MachineSideMode.Output || mode == MachineSideMode.Input || mode == MachineSideMode.Input2;
	}

	@Override
	protected DefaultSideConfiguration getDefaultSideConfiguration() {
		return DEFAULT_NO_FACE_SIDE_CONFIGURATION.copy().setSide(BlockSide.BACK, true, MachineSideMode.Input2).setSide(BlockSide.RIGHT, true, MachineSideMode.Input2);
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerEnchanter(windowId, inventory, this);
	}
}
