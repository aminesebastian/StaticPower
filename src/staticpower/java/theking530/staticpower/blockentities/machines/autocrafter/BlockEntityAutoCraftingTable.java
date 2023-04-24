package theking530.staticpower.blockentities.machines.autocrafter;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.items.ItemStackHandler;
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
import theking530.staticcore.blockentity.components.items.OutputServoComponent;
import theking530.staticcore.blockentity.components.items.UpgradeInventoryComponent;
import theking530.staticcore.crafting.RecipeMatchParameters;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticcore.utilities.item.InventoryUtilities;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blockentities.BlockEntityMachine;
import theking530.staticpower.client.rendering.blockentity.BlockEntityRenderAutoCraftingTable;
import theking530.staticpower.init.ModBlocks;

public class BlockEntityAutoCraftingTable extends BlockEntityMachine implements IRecipeProcessor<CraftingRecipe> {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityAutoCraftingTable> TYPE = new BlockEntityTypeAllocator<BlockEntityAutoCraftingTable>(
			"crafting_table_auto", (type, pos, state) -> new BlockEntityAutoCraftingTable(pos, state),
			ModBlocks.AutoCraftingTable);

	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setTileEntitySpecialRenderer(BlockEntityRenderAutoCraftingTable::new);
		}
	}

	public final RecipeProcessingComponent<CraftingRecipe> processingComponent;
	public final InventoryComponent patternInventory;
	public final UpgradeInventoryComponent upgradesInventory;

	public final InventoryComponent inputInventory;
	public final BatteryInventoryComponent batteryInventory;
	public final InventoryComponent outputInventory;

	public BlockEntityAutoCraftingTable(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);
		registerComponent(patternInventory = new InventoryComponent("PatternInventory", 9, MachineSideMode.Never));
		registerComponent(inputInventory = new InventoryComponent("InputInventory", 9, MachineSideMode.Input)
				.setSlotsLockable(true).setShiftClickEnabled(true));

		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 1, MachineSideMode.Output));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryInventory", powerStorage));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));

		// Setup the processing component.
		registerComponent(processingComponent = new RecipeProcessingComponent<CraftingRecipe>("ProcessingComponent",
				StaticPowerConfig.SERVER.autoCrafterProcessingTime.get(), RecipeType.CRAFTING));
		processingComponent.setShouldControlOnBlockState(true);
		processingComponent.setRedstoneControlComponent(redstoneControlComponent);
		processingComponent.setUpgradeInventory(upgradesInventory);
		processingComponent.setPowerComponent(powerStorage);

		registerComponent(new OutputServoComponent("OutputServo", 2, outputInventory));
		registerComponent(new InputServoComponent("InputServo", 2, inputInventory));

		// Set the energy storage upgrade inventory.
		powerStorage.setUpgradeInventory(upgradesInventory);

		patternInventory.setShouldDropContentsOnBreak(false);
	}

	public boolean hasRequiredItems(CraftingRecipe recipe, List<ItemStack> items) {

		// Create a duplicate inventory.
		ItemStackHandler duplicateInventory = new ItemStackHandler(items.size());
		for (int i = 0; i < items.size(); i++) {
			duplicateInventory.setStackInSlot(i, items.get(i).copy());
		}

		// Allocate a flag to indicate if we found an ingredient match.
		boolean flag = false;

		// Iterate through all the ingredients in the recipe.
		for (int i = 0; i < recipe.getIngredients().size(); i++) {
			// Set the flag to false.
			flag = false;

			// Get the ingredient.
			Ingredient ing = recipe.getIngredients().get(i);

			// Skip empty ingredients.
			if (ing.equals(Ingredient.EMPTY)) {
				continue;
			}

			// Look for an item that matches the ingredient inside the inventory. If we find
			// one, extract the item from the duplicate and continue. If one is not found,
			// we cannot craft this item, return false.
			for (int j = 0; j < duplicateInventory.getSlots(); j++) {
				if (ing.test(duplicateInventory.getStackInSlot(j))) {
					duplicateInventory.extractItem(j, 1, false);
					flag = true;
					break;
				}
			}

			if (!flag) {
				return false;
			}
		}

		// If all the above are met, return true.
		return true;
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerAutoCraftingTable(windowId, inventory, this);
	}

	@Override
	public RecipeMatchParameters getRecipeMatchParameters(RecipeProcessingComponent<CraftingRecipe> component) {
		ItemStack[] pattern = new ItemStack[patternInventory.getSlots()];
		for (int i = 0; i < patternInventory.getSlots(); i++) {
			pattern[i] = patternInventory.getStackInSlot(i);
		}
		return new RecipeMatchParameters(pattern);
	}

	@Override
	public void captureOutputs(RecipeProcessingComponent<CraftingRecipe> component, CraftingRecipe recipe,
			ConcretizedProductContainer outputContainer) {
		outputContainer.addItem(recipe.getResultItem(), CaptureType.BOTH);
	}

	@Override
	public void captureInputs(RecipeProcessingComponent<CraftingRecipe> component, CraftingRecipe recipe,
			ProcessingContainer processingContainer, ConcretizedProductContainer inputContainer) {
		if (recipe instanceof ShapedRecipe) {
			ShapedRecipe sRecipe = (ShapedRecipe) recipe;
			for (int x = 0; x < 3; ++x) {
				for (int y = 0; y < 3; ++y) {
					// Get the recipe index.
					Ingredient ingredient = Ingredient.EMPTY;
					if (x >= 0 && y >= 0 && x < sRecipe.getRecipeWidth() && y < sRecipe.getRecipeHeight()) {
						ingredient = sRecipe.getIngredients()
								.get(sRecipe.getRecipeWidth() - x - 1 + y * sRecipe.getRecipeWidth());
					}

					// Skip empty ingredients.
					if (ingredient.equals(Ingredient.EMPTY)) {
						continue;
					}

					// Capture the item.
					for (int j = 0; j < inputInventory.getSlots(); j++) {
						if (ingredient.test(inputInventory.getStackInSlot(j))) {
							ItemStack extracted = inputInventory.extractItem(j, 1, false);
							inputContainer.addItem(extracted, CaptureType.BOTH);
							break;
						}
					}
				}
			}
		} else {
			// Transfer the materials into the internal inventory.
			for (int i = 0; i < recipe.getIngredients().size(); i++) {
				// Get the used ingredient.
				Ingredient ing = recipe.getIngredients().get(i);

				// Skip holes in the recipe.
				if (ing.equals(Ingredient.EMPTY)) {
					continue;
				}

				// Remove the item.
				for (int j = 0; j < inputInventory.getSlots(); j++) {
					if (ing.test(inputInventory.getStackInSlot(j))) {
						ItemStack extracted = inputInventory.extractItem(j, 1, false);
						inputContainer.addItem(extracted, CaptureType.BOTH);
						break;
					}
				}
			}
		}
	}

	@Override
	public ProcessingCheckState canStartProcessingRecipe(RecipeProcessingComponent<CraftingRecipe> component,
			CraftingRecipe recipe, ConcretizedProductContainer outputContainer) {
		if (!hasRequiredItems(recipe, outputContainer.getItems())) {
			return ProcessingCheckState.error("Missing items in input inventory!");
		}

		if (!InventoryUtilities.canFullyInsertStackIntoSlot(outputInventory, 0, recipe.getResultItem())) {
			ProcessingCheckState.outputsCannotTakeRecipe();
		}

		return ProcessingCheckState.ok();
	}

	@Override
	public void onProcessingCompleted(RecipeProcessingComponent<CraftingRecipe> component,
			ProcessingContainer processingContainer) {
		outputInventory.insertItem(0, processingContainer.getOutputs().getItem(0).copy(), false);
	}

}
