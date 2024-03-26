package theking530.staticpower.blockentities.machines.former;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.blockentity.components.control.processing.ConcretizedProductContainer;
import theking530.staticcore.blockentity.components.control.processing.ProcessingCheckState;
import theking530.staticcore.blockentity.components.control.processing.ProcessingContainer;
import theking530.staticcore.blockentity.components.control.processing.ProcessingContainer.CaptureType;
import theking530.staticcore.blockentity.components.control.processing.recipe.IRecipeProcessor;
import theking530.staticcore.blockentity.components.control.processing.recipe.RecipeProcessingComponent;
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.items.BatteryInventoryComponent;
import theking530.staticcore.blockentity.components.items.InputServoComponent;
import theking530.staticcore.blockentity.components.items.InventoryComponent;
import theking530.staticcore.blockentity.components.items.ItemStackHandlerFilter;
import theking530.staticcore.blockentity.components.items.OutputServoComponent;
import theking530.staticcore.blockentity.components.items.UpgradeInventoryComponent;
import theking530.staticcore.crafting.RecipeMatchParameters;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticcore.utilities.item.InventoryUtilities;
import theking530.staticpower.blockentities.BlockEntityMachine;
import theking530.staticpower.data.crafting.StaticPowerRecipeRegistry;
import theking530.staticpower.data.crafting.wrappers.former.FormerRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModRecipeTypes;

public class BlockEntityFormer extends BlockEntityMachine implements IRecipeProcessor<FormerRecipe> {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityFormer> TYPE = new BlockEntityTypeAllocator<>("former",
			(type, pos, state) -> new BlockEntityFormer(pos, state), ModBlocks.Former);

	public final InventoryComponent inputInventory;
	public final InventoryComponent moldInventory;
	public final InventoryComponent outputInventory;
	public final BatteryInventoryComponent batteryInventory;
	public final UpgradeInventoryComponent upgradesInventory;
	public final RecipeProcessingComponent<FormerRecipe> processingComponent;

	public BlockEntityFormer(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);

		// Setup the input inventories to only accept items that have a valid recipe.
		registerComponent(inputInventory = new InventoryComponent("InputInventory", 1, MachineSideMode.Input)
				.setShiftClickEnabled(true).setFilter(new ItemStackHandlerFilter() {
					public boolean canInsertItem(int slot, ItemStack stack) {
						return true;
					}
				}));
		registerComponent(moldInventory = new InventoryComponent("MoldInputInventory", 1, MachineSideMode.Input)
				.setShiftClickEnabled(true).setFilter(new ItemStackHandlerFilter() {
					public boolean canInsertItem(int slot, ItemStack stack) {
						return StaticPowerRecipeRegistry.isValidFormerMold(stack);
					}
				}));

		// Setup all the other inventories.
		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 1, MachineSideMode.Output));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", powerStorage));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));

		// Setup the processing component to work with the redstone control component,
		// upgrade component and energy component.
		registerComponent(processingComponent = new RecipeProcessingComponent<FormerRecipe>("ProcessingComponent", 0,
				ModRecipeTypes.FORMER_RECIPE_TYPE.get()));

		// Initialize the processing component to work with the redstone control
		// component, upgrade component and energy component.
		processingComponent.setShouldControlOnBlockState(true);
		processingComponent.setUpgradeInventory(upgradesInventory);
		processingComponent.setPowerComponent(powerStorage);
		processingComponent.setRedstoneControlComponent(redstoneControlComponent);

		// Setup the I/O servos.
		registerComponent(new InputServoComponent("InputServo", 4, inputInventory, 0));
		registerComponent(new OutputServoComponent("OutputServo", 4, outputInventory, 0));

		// Set the energy storage upgrade inventory.
		powerStorage.setUpgradeInventory(upgradesInventory);
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerFormer(windowId, inventory, this);
	}

	@Override
	public RecipeMatchParameters getRecipeMatchParameters(RecipeProcessingComponent<FormerRecipe> component) {
		return new RecipeMatchParameters(getTeamComponent().getOwningTeamId(), inputInventory.getStackInSlot(0),
				moldInventory.getStackInSlot(0));
	}

	@Override
	public void captureOutputs(RecipeProcessingComponent<FormerRecipe> component, FormerRecipe recipe,
			ConcretizedProductContainer outputContainer) {
		outputContainer.addItem(recipe.getOutput().calculateOutput(), CaptureType.BOTH);
	}

	@Override
	public ProcessingCheckState canStartProcessingRecipe(RecipeProcessingComponent<FormerRecipe> component,
			FormerRecipe recipe, ConcretizedProductContainer outputContainer) {

		if (!InventoryUtilities.canFullyInsertItemIntoInventory(outputInventory, outputContainer.getItem(0))) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}

		return ProcessingCheckState.ok();
	}

	@Override
	public void captureInputs(RecipeProcessingComponent<FormerRecipe> component, FormerRecipe recipe,
			ProcessingContainer processingContainer, ConcretizedProductContainer inputContainer) {
		inputContainer.addItem(inputInventory.extractItem(0,
				component.getProcessingRecipe().get().getInputIngredient().getCount(), false), CaptureType.BOTH);
		inputContainer.addItem(moldInventory.getStackInSlot(0), CaptureType.NONE, true);

	}

	@Override
	public void onProcessingCompleted(RecipeProcessingComponent<FormerRecipe> component,
			ProcessingContainer processingContainer) {
		outputInventory.insertItem(0, processingContainer.getOutputs().getItem(0).copy(), false);
	}
}