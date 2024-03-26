package theking530.staticpower.blockentities.machines.bottler;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import theking530.staticcore.StaticCoreConfig;
import theking530.staticcore.blockentity.components.control.processing.IProcessor;
import theking530.staticcore.blockentity.components.control.processing.ProcessingCheckState;
import theking530.staticcore.blockentity.components.control.processing.ProcessingContainer;
import theking530.staticcore.blockentity.components.control.processing.machine.MachineProcessingComponent;
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.fluids.FluidInputServoComponent;
import theking530.staticcore.blockentity.components.fluids.FluidTankComponent;
import theking530.staticcore.blockentity.components.items.BatteryInventoryComponent;
import theking530.staticcore.blockentity.components.items.FluidContainerInventoryComponent;
import theking530.staticcore.blockentity.components.items.InputServoComponent;
import theking530.staticcore.blockentity.components.items.InventoryComponent;
import theking530.staticcore.blockentity.components.items.ItemStackHandlerFilter;
import theking530.staticcore.blockentity.components.items.OutputServoComponent;
import theking530.staticcore.blockentity.components.items.UpgradeInventoryComponent;
import theking530.staticcore.crafting.CraftingUtilities;
import theking530.staticcore.crafting.RecipeMatchParameters;
import theking530.staticcore.data.StaticCoreTier;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticcore.utilities.FluidUtilities;
import theking530.staticcore.utilities.FluidUtilities.FluidContainerFillResult;
import theking530.staticcore.utilities.item.InventoryUtilities;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blockentities.BlockEntityMachine;
import theking530.staticpower.data.crafting.wrappers.bottler.BottleRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModRecipeTypes;

public class BlockEntityBottler extends BlockEntityMachine implements IProcessor<MachineProcessingComponent> {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityBottler> TYPE = new BlockEntityTypeAllocator<>("bottler",
			(type, pos, state) -> new BlockEntityBottler(pos, state), ModBlocks.Bottler);

	public final InventoryComponent inputInventory;
	public final InventoryComponent outputInventory;
	public final BatteryInventoryComponent batteryInventory;
	public final UpgradeInventoryComponent upgradesInventory;
	public final FluidContainerInventoryComponent fluidContainerComponent;

	public final MachineProcessingComponent processingComponent;

	public final FluidTankComponent fluidTankComponent;

	public BlockEntityBottler(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);

		// Get the tier object.
		StaticCoreTier tierObject = StaticCoreConfig.getTier(getTier());

		// Setup the input inventory to only accept items that have a valid recipe.
		registerComponent(inputInventory = new InventoryComponent("InputInventory", 1, MachineSideMode.Input)
				.setShiftClickEnabled(true).setFilter(new ItemStackHandlerFilter() {
					public boolean canInsertItem(int slot, ItemStack stack) {
						return isValidInput(stack);
					}

				}));

		// Setup all the other inventories.;
		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 1, MachineSideMode.Output));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", powerStorage));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));

		registerComponent(processingComponent = new MachineProcessingComponent("ProcessingComponent",
				StaticPowerConfig.SERVER.bottlerProcessingTime.get()) {

		});
		processingComponent.setShouldControlOnBlockState(true);
		processingComponent.setUpgradeInventory(upgradesInventory);
		processingComponent.setRedstoneControlComponent(redstoneControlComponent);
		processingComponent.setPowerComponent(powerStorage);
		processingComponent.setBasePowerUsage(StaticPowerConfig.SERVER.bottlerPowerUsage.get());

		// Setup the I/O servos.
		registerComponent(new OutputServoComponent("OutputServo", 2, outputInventory));
		registerComponent(new InputServoComponent("InputServo", 2, inputInventory));

		// Setup the fluid tanks and servo.
		registerComponent(fluidTankComponent = new FluidTankComponent("FluidTank", tierObject.defaultTankCapacity.get())
				.setCapabilityExposedModes(MachineSideMode.Input).setUpgradeInventory(upgradesInventory));

		registerComponent(new FluidInputServoComponent("FluidInputServoComponent", 100, fluidTankComponent,
				MachineSideMode.Input));
		registerComponent(fluidContainerComponent = new FluidContainerInventoryComponent("FluidContainerServo",
				fluidTankComponent));

		// Set the energy storage upgrade inventory.
		powerStorage.setUpgradeInventory(upgradesInventory);
	}

	@Override
	public void process() {

	}

	@Override
	public ProcessingCheckState canStartProcessing(MachineProcessingComponent component,
			ProcessingContainer processingContainer) {

		BottleRecipe recipe = getRecipe(inputInventory.getStackInSlot(0));

		ItemStack outputItem = ItemStack.EMPTY;
		int inputAmount = 1;
		int fluidAmount = 0;
		double powerUsage = StaticPowerConfig.SERVER.bottlerPowerUsage.get();
		int processingTime = StaticPowerConfig.SERVER.bottlerProcessingTime.get();

		if (recipe != null) {
			if (recipe.getFluid().getAmount() > fluidTankComponent.getFluidAmount()) {
				return ProcessingCheckState.notEnoughFluid();
			}
			outputItem = recipe.getFilledBottle().calculateOutput();
			inputAmount = recipe.getEmptyBottle().getCount();
			fluidAmount = recipe.getFluid().getAmount();
		} else {
			FluidContainerFillResult result = FluidUtilities.tryFillContainer(inputInventory.getStackInSlot(0),
					fluidTankComponent, Integer.MAX_VALUE, null, false);
			if (!result.isSuccess()) {
				return ProcessingCheckState.skip();
			}

			outputItem = result.filledContainer();
			fluidAmount = result.transferedFluid().getAmount();
		}

		if (!InventoryUtilities.canFullyInsertItemIntoInventory(outputInventory, outputItem)) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}

		processingComponent.setBasePowerUsage(powerUsage);
		processingComponent.setBasePowerUsage(processingTime);

		ItemStack input = inputInventory.getStackInSlot(0).copy();
		input.setCount(inputAmount);
		processingContainer.getInputs().addItem(input);

		processingContainer.getInputs().addFluid(fluidTankComponent.drain(fluidAmount, FluidAction.SIMULATE));

		processingContainer.getOutputs().addItem(outputItem);
		return ProcessingCheckState.ok();
	}

	@Override
	public void onProcessingStarted(MachineProcessingComponent component, ProcessingContainer processingContainer) {
		inputInventory.getStackInSlot(0).shrink(processingContainer.getInputs().getItem(0).getCount());
		fluidTankComponent.drain(processingContainer.getInputs().getFluid(0).getAmount(), FluidAction.EXECUTE);
	}

	@Override
	public ProcessingCheckState canContinueProcessing(MachineProcessingComponent component,
			ProcessingContainer processingContainer) {
		if (!InventoryUtilities.canFullyInsertStackIntoSlot(outputInventory, 0,
				processingContainer.getOutputs().getItem(0))) {
			return ProcessingCheckState.skip();
		}
		return ProcessingCheckState.ok();
	}

	@Override
	public ProcessingCheckState canCompleteProcessing(MachineProcessingComponent component,
			ProcessingContainer processingContainer) {
		if (!InventoryUtilities.canFullyInsertStackIntoSlot(outputInventory, 0,
				processingContainer.getOutputs().getItem(0))) {
			return ProcessingCheckState.skip();
		}
		return ProcessingCheckState.ok();
	}

	@Override
	public void onProcessingCompleted(MachineProcessingComponent component, ProcessingContainer processingContainer) {
		ItemStack output = processingContainer.getOutputs().getItem(0).copy();
		InventoryUtilities.insertItemIntoInventory(outputInventory, output, false);
	}

	protected boolean isValidInput(ItemStack stack) {
		if (getRecipe(stack) != null) {
			return true;
		}

		if (FluidUtil.getFluidHandler(stack).isPresent()) {
			IFluidHandler handler = FluidUtil.getFluidHandler(stack).orElse(null);
			if (handler != null && handler.getFluidInTank(0).getAmount() < handler.getTankCapacity(0)) {
				return true;
			}
		}

		return false;
	}

	protected BottleRecipe getRecipe(ItemStack stack) {
		return CraftingUtilities.getRecipe(ModRecipeTypes.BOTTLER_RECIPE_TYPE.get(),
				new RecipeMatchParameters(getTeamComponent().getOwningTeamId(), fluidTankComponent.getFluid())
						.setItems(stack),
				getLevel()).orElse(null);
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerBottler(windowId, inventory, this);
	}
}
