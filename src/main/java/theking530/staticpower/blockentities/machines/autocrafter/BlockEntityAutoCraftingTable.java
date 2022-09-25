package theking530.staticpower.blockentities.machines.autocrafter;

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
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blockentities.BlockEntityMachine;
import theking530.staticpower.blockentities.components.control.AbstractProcesingComponent.ProcessingCheckState;
import theking530.staticpower.blockentities.components.control.RecipeProcessingComponent;
import theking530.staticpower.blockentities.components.control.RecipeProcessingComponent.RecipeProcessingPhase;
import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.blockentities.components.items.BatteryInventoryComponent;
import theking530.staticpower.blockentities.components.items.InputServoComponent;
import theking530.staticpower.blockentities.components.items.InventoryComponent;
import theking530.staticpower.blockentities.components.items.OutputServoComponent;
import theking530.staticpower.blockentities.components.items.UpgradeInventoryComponent;
import theking530.staticpower.client.rendering.blockentity.BlockEntityRenderAutoCraftingTable;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.utilities.InventoryUtilities;

public class BlockEntityAutoCraftingTable extends BlockEntityMachine {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityAutoCraftingTable> TYPE = new BlockEntityTypeAllocator<BlockEntityAutoCraftingTable>(
			(type, pos, state) -> new BlockEntityAutoCraftingTable(pos, state), ModBlocks.AutoCraftingTable);

	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setTileEntitySpecialRenderer(BlockEntityRenderAutoCraftingTable::new);
		}
	}

	public final RecipeProcessingComponent<CraftingRecipe> processingComponent;
	public final InventoryComponent internalInventory;
	public final InventoryComponent patternInventory;
	public final UpgradeInventoryComponent upgradesInventory;

	public final InventoryComponent inputInventory;
	public final BatteryInventoryComponent batteryInventory;
	public final InventoryComponent outputInventory;

	public BlockEntityAutoCraftingTable(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);
		registerComponent(patternInventory = new InventoryComponent("PatternInventory", 9, MachineSideMode.Never));
		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 9, MachineSideMode.Never));
		registerComponent(inputInventory = new InventoryComponent("InputInventory", 9, MachineSideMode.Input).setSlotsLockable(true).setShiftClickEnabled(true));

		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 1, MachineSideMode.Output));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryInventory", powerStorage));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));

		// Setup the processing component.
		registerComponent(processingComponent = new RecipeProcessingComponent<CraftingRecipe>("ProcessingComponent", StaticPowerConfig.SERVER.autoCrafterProcessingTime.get(),
				RecipeProcessingComponent.MOVE_TIME, RecipeType.CRAFTING, this::getMatchParameters, this::canProcessRecipe, this::moveInputs, this::processingCompleted).setShouldControlBlockState(true)
				.setProcessingPowerUsage(StaticPowerConfig.SERVER.autoCrafterPowerUsage.get()));

		processingComponent.setRedstoneControlComponent(redstoneControlComponent);
		processingComponent.setUpgradeInventory(upgradesInventory);
		processingComponent.setPowerComponent(powerStorage);
		processingComponent.setProcessingPowerUsage(StaticPowerConfig.SERVER.autoCrafterPowerUsage.get());

		registerComponent(new OutputServoComponent("OutputServo", 2, outputInventory));
		registerComponent(new InputServoComponent("InputServo", 2, inputInventory));

		// Set the energy storage upgrade inventory.
		powerStorage.setUpgradeInventory(upgradesInventory);

		patternInventory.setShouldDropContentsOnBreak(false);
	}

	protected RecipeMatchParameters getMatchParameters(RecipeProcessingPhase location) {
		ItemStack[] pattern = new ItemStack[patternInventory.getSlots()];

		if (location == RecipeProcessingPhase.PROCESSING) {
			for (int i = 0; i < internalInventory.getSlots(); i++) {
				pattern[i] = internalInventory.getStackInSlot(i);
			}
		} else {
			for (int i = 0; i < patternInventory.getSlots(); i++) {
				pattern[i] = patternInventory.getStackInSlot(i);
			}
		}

		return new RecipeMatchParameters(pattern);
	}

	public void moveInputs(CraftingRecipe recipe) {
		// If this recipe is shaped, make sure we place the same shaped recipe's items
		// into the internal inventory. If shapeless, just put the items into the
		// internal inv.
		if (recipe instanceof ShapedRecipe) {
			ShapedRecipe sRecipe = (ShapedRecipe) recipe;
			for (int x = 0; x < 3; ++x) {
				for (int y = 0; y < 3; ++y) {
					// Get the recipe index.
					Ingredient ingredient = Ingredient.EMPTY;
					if (x >= 0 && y >= 0 && x < sRecipe.getRecipeWidth() && y < sRecipe.getRecipeHeight()) {
						ingredient = sRecipe.getIngredients().get(sRecipe.getRecipeWidth() - x - 1 + y * sRecipe.getRecipeWidth());
					}

					// Skip empty ingredients.
					if (ingredient.equals(Ingredient.EMPTY)) {
						continue;
					}

					// Capture the item.
					for (int j = 0; j < inputInventory.getSlots(); j++) {
						if (ingredient.test(inputInventory.getStackInSlot(j))) {
							ItemStack extracted = inputInventory.extractItem(j, 1, false);
							internalInventory.setStackInSlot(x + (y * 3), extracted.copy());
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
						internalInventory.setStackInSlot(i, extracted.copy());
						break;
					}
				}
			}
		}
	}

	public ProcessingCheckState canProcessRecipe(CraftingRecipe recipe, RecipeProcessingPhase location) {
		if (location == RecipeProcessingPhase.PRE_PROCESSING) {
			if (!hasRequiredItems(recipe)) {
				return ProcessingCheckState.error("Missing items in input inventory!");
			}
		}

		if (!InventoryUtilities.canFullyInsertStackIntoSlot(outputInventory, 0, recipe.getResultItem())) {
			ProcessingCheckState.outputsCannotTakeRecipe();
		}

		return ProcessingCheckState.ok();
	}

	public void processingCompleted(CraftingRecipe recipe) {
		outputInventory.insertItem(0, recipe.getResultItem().copy(), false);
		InventoryUtilities.clearInventory(internalInventory);
	}

	public boolean hasRequiredItems(CraftingRecipe recipe) {

		// Create a duplicate inventory.
		ItemStackHandler duplicateInventory = InventoryUtilities.duplicateItemStackHandler(inputInventory);

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
}
