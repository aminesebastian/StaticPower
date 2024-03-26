package theking530.staticpower.blockentities.machines.autosolderingtable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.blockentity.components.control.RedstoneControlComponent;
import theking530.staticcore.blockentity.components.control.processing.ConcretizedProductContainer;
import theking530.staticcore.blockentity.components.control.processing.ProcessingCheckState;
import theking530.staticcore.blockentity.components.control.processing.ProcessingContainer;
import theking530.staticcore.blockentity.components.control.processing.ProcessingContainer.CaptureType;
import theking530.staticcore.blockentity.components.control.processing.recipe.IRecipeProcessor;
import theking530.staticcore.blockentity.components.control.processing.recipe.RecipeProcessingComponent;
import theking530.staticcore.blockentity.components.control.redstonecontrol.RedstoneMode;
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.energy.PowerStorageComponent;
import theking530.staticcore.blockentity.components.items.BatteryInventoryComponent;
import theking530.staticcore.blockentity.components.items.InputServoComponent;
import theking530.staticcore.blockentity.components.items.InventoryComponent;
import theking530.staticcore.blockentity.components.items.OutputServoComponent;
import theking530.staticcore.blockentity.components.items.UpgradeInventoryComponent;
import theking530.staticcore.crafting.RecipeMatchParameters;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticcore.utilities.item.InventoryUtilities;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blockentities.components.TieredPowerStorageComponent;
import theking530.staticpower.blockentities.nonpowered.solderingtable.AbstractSolderingTable;
import theking530.staticpower.client.rendering.blockentity.BlockEntityRenderAutoSolderingTable;
import theking530.staticpower.data.crafting.wrappers.soldering.SolderingRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModRecipeTypes;

public class BlockEntityAutoSolderingTable extends AbstractSolderingTable implements IRecipeProcessor<SolderingRecipe> {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityAutoSolderingTable> TYPE = new BlockEntityTypeAllocator<BlockEntityAutoSolderingTable>(
			"soldering_table_auto", (type, pos, state) -> new BlockEntityAutoSolderingTable(pos, state),
			ModBlocks.AutoSolderingTable);

	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setTileEntitySpecialRenderer(BlockEntityRenderAutoSolderingTable::new);
		}
	}

	public final RecipeProcessingComponent<SolderingRecipe> processingComponent;
	public final RedstoneControlComponent redstoneControlComponent;
	public final BatteryInventoryComponent batteryInventory;
	public final UpgradeInventoryComponent upgradesInventory;
	public final InventoryComponent outputInventory;
	public final PowerStorageComponent powerStorage;

	public BlockEntityAutoSolderingTable(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);

		registerComponent(powerStorage = new TieredPowerStorageComponent("MainEnergyStorage", getTier(), true, false));

		// Set the inventory component to the input mode.
		inventory.setMode(MachineSideMode.Input).setSlotsLockable(true);

		registerComponent(redstoneControlComponent = new RedstoneControlComponent("RedstoneControlComponent",
				RedstoneMode.Ignore));
		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 1, MachineSideMode.Output));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", powerStorage));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));

		// Setup the processing component.
		registerComponent(processingComponent = new RecipeProcessingComponent<SolderingRecipe>("ProcessingComponent",
				StaticPowerConfig.SERVER.autoSolderingTableProcessingTime.get(),
				ModRecipeTypes.SOLDERING_RECIPE_TYPE.get()));
		processingComponent.setShouldControlOnBlockState(true);
		processingComponent.setRedstoneControlComponent(redstoneControlComponent);
		processingComponent.setUpgradeInventory(upgradesInventory);
		processingComponent.setPowerComponent(powerStorage);
		processingComponent.setBasePowerUsage(StaticPowerConfig.SERVER.autoSolderingTablePowerUsage.get());

		// Set the energy storage upgrade inventory.
		powerStorage.setUpgradeInventory(upgradesInventory);

		registerComponent(new OutputServoComponent("OutputServo", 2, outputInventory));
		registerComponent(new InputServoComponent("InputServo", 2, inventory));
	}

	@Override
	public RecipeMatchParameters getRecipeMatchParameters(RecipeProcessingComponent<SolderingRecipe> component) {
		ItemStack[] pattern = new ItemStack[patternInventory.getSlots()];
		for (int i = 0; i < patternInventory.getSlots(); i++) {
			pattern[i] = patternInventory.getStackInSlot(i);
		}
		return new RecipeMatchParameters(getTeamComponent().getOwningTeamId(), pattern);
	}

	@Override
	protected boolean requiresSolderingIron() {
		return false;
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerAutoSolderingTable(windowId, inventory, this);
	}

	@Override
	public void captureOutputs(RecipeProcessingComponent<SolderingRecipe> component, SolderingRecipe recipe,
			ConcretizedProductContainer outputContainer) {
		outputContainer.addItem(recipe.getResultItem(), CaptureType.BOTH);
	}

	@Override
	public ProcessingCheckState canStartProcessingRecipe(RecipeProcessingComponent<SolderingRecipe> component,
			SolderingRecipe recipe, ConcretizedProductContainer outputContainer) {
		if (!hasRequiredItems(recipe)) {
			return ProcessingCheckState.error("Missing items in input inventory!");
		}

		if (!InventoryUtilities.canFullyInsertStackIntoSlot(outputInventory, 0, outputContainer.getItem(0))) {
			ProcessingCheckState.outputsCannotTakeRecipe();
		}

		return ProcessingCheckState.ok();
	}

	@Override
	public void captureInputs(RecipeProcessingComponent<SolderingRecipe> component, SolderingRecipe recipe,
			ProcessingContainer processingContainer, ConcretizedProductContainer inputContainer) {
		// If we still have the recipe, and the required items, move the input items
		// into the internal inventory.
		// Transfer the materials into the internal inventory.
		for (int i = 0; i < patternInventory.getSlots(); i++) {
			// Get the used ingredient.
			Ingredient ing = recipe.getIngredients().get(i);

			// Skip holes in the recipe.
			if (ing.equals(Ingredient.EMPTY)) {
				continue;
			}

			// Remove the item.
			for (int j = 0; j < inventory.getSlots(); j++) {
				if (ing.test(inventory.getStackInSlot(j))) {
					ItemStack extracted = inventory.extractItem(j, 1, true);
					inputContainer.addItem(extracted, CaptureType.BOTH);
					break;
				}
			}
		}

	}

	@Override
	public void onProcessingCompleted(RecipeProcessingComponent<SolderingRecipe> component,
			ProcessingContainer processingContainer) {
		outputInventory.insertItem(0, processingContainer.getOutputs().getItem(0).copy(), false);
	}

}
