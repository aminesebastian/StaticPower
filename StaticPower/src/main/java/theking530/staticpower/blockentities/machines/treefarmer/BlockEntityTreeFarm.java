package theking530.staticpower.blockentities.machines.treefarmer;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import com.mojang.math.Vector3f;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.blockentity.components.control.processing.MachineProcessingComponent;
import theking530.staticcore.blockentity.components.control.processing.ProcessingCheckState;
import theking530.staticcore.blockentity.components.control.processing.ProcessingOutputContainer;
import theking530.staticcore.blockentity.components.control.processing.ProcessingOutputContainer.CaptureType;
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationUtilities;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticcore.blockentity.components.fluids.FluidTankComponent;
import theking530.staticcore.blockentity.components.items.BatteryInventoryComponent;
import theking530.staticcore.blockentity.components.items.FluidContainerInventoryComponent;
import theking530.staticcore.blockentity.components.items.FluidContainerInventoryComponent.FluidContainerInteractionMode;
import theking530.staticcore.blockentity.components.items.InputServoComponent;
import theking530.staticcore.blockentity.components.items.InventoryComponent;
import theking530.staticcore.blockentity.components.items.InventoryComponent.InventoryChangeType;
import theking530.staticcore.blockentity.components.items.ItemStackHandlerFilter;
import theking530.staticcore.blockentity.components.items.OutputServoComponent;
import theking530.staticcore.blockentity.components.items.UpgradeInventoryComponent;
import theking530.staticcore.blockentity.components.serialization.UpdateSerialize;
import theking530.staticcore.crafting.CraftingUtilities;
import theking530.staticcore.crafting.RecipeMatchParameters;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticcore.utilities.SDColor;
import theking530.staticcore.utilities.item.InventoryUtilities;
import theking530.staticcore.utilities.math.SDMath;
import theking530.staticcore.world.WorldUtilities;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blockentities.BlockEntityMachine;
import theking530.staticpower.client.rendering.blockentity.BlockEntityRenderTreeFarmer;
import theking530.staticpower.client.rendering.renderers.RadiusPreviewRenderer;
import theking530.staticpower.data.crafting.wrappers.fertilization.FertalizerRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModRecipeTypes;
import theking530.staticpower.init.tags.ModItemTags;
import theking530.staticpower.items.upgrades.BaseRangeUpgrade;

public class BlockEntityTreeFarm extends BlockEntityMachine {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityTreeFarm> TYPE = new BlockEntityTypeAllocator<BlockEntityTreeFarm>("tree_farm",
			(type, pos, state) -> new BlockEntityTreeFarm(pos, state), ModBlocks.TreeFarmer);

	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setTileEntitySpecialRenderer(BlockEntityRenderTreeFarmer::new);
		}
	}

	public final InventoryComponent inputInventory;
	public final InventoryComponent outputInventory;
	public final FluidContainerInventoryComponent fluidContainerComponent;
	public final BatteryInventoryComponent batteryInventory;
	public final UpgradeInventoryComponent upgradesInventory;

	public final MachineProcessingComponent processingComponent;
	public final FluidTankComponent fluidTankComponent;

	@UpdateSerialize
	private int currentBlockIndex;
	@UpdateSerialize
	private int range;
	private boolean shouldDrawRadiusPreview;

	private final List<BlockPos> blocks;
	private final HashSet<BlockPos> visited;
	private final Ingredient saplingIngredient;

	public BlockEntityTreeFarm(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);

		saplingIngredient = Ingredient.of(ItemTags.SAPLINGS);

		registerComponent(inputInventory = new InventoryComponent("InputInventory", 10, MachineSideMode.Input).setShiftClickEnabled(true).setFilter(new ItemStackHandlerFilter() {
			public boolean canInsertItem(int slot, ItemStack stack) {
				return slot == 0 ? ModItemTags.matches(ModItemTags.FARMING_AXE, stack.getItem()) : saplingIngredient.test(stack);
			}
		}).setSlotsLockable(true));
		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 9, MachineSideMode.Output));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", powerStorage));
		registerComponent(upgradesInventory = (UpgradeInventoryComponent) new UpgradeInventoryComponent("UpgradeInventory", 3)
				.setModifiedCallback(this::onUpgradesInventoryModifiedCallback));

		registerComponent(processingComponent = new MachineProcessingComponent("ProcessingComponent", StaticPowerConfig.SERVER.treeFarmerProcessingTime.get(), this::canProcess,
				this::canProcess, this::processingCompleted, true));
		processingComponent.setUpgradeInventory(upgradesInventory);
		processingComponent.setRedstoneControlComponent(redstoneControlComponent);
		processingComponent.setPowerComponent(powerStorage);
		processingComponent.setProcessingPowerUsage(StaticPowerConfig.SERVER.treeFarmerPowerUsage.get());

		registerComponent(fluidTankComponent = new FluidTankComponent("FluidTank", 5000, (fluid) -> {
			return CraftingUtilities.getRecipe(ModRecipeTypes.FERTALIZER_RECIPE_TYPE.get(), new RecipeMatchParameters(fluid), getLevel()).isPresent();
		}).setCapabilityExposedModes(MachineSideMode.Input).setUpgradeInventory(upgradesInventory).setAutoSyncPacketsEnabled(true));

		currentBlockIndex = 0;
		shouldDrawRadiusPreview = false;
		range = StaticPowerConfig.SERVER.treeFarmerDefaultRange.get();
		blocks = new LinkedList<BlockPos>();
		visited = new HashSet<BlockPos>(StaticPowerConfig.SERVER.treeFarmerMaxTreeRecursion.get());
		registerComponent(fluidContainerComponent = new FluidContainerInventoryComponent("BucketDrain", fluidTankComponent));
		registerComponent(new InputServoComponent("InputServo", 4, inputInventory));
		registerComponent(new OutputServoComponent("OutputServo", 4, outputInventory));

		fluidContainerComponent.setMode(FluidContainerInteractionMode.DRAIN);
		// Set the energy storage upgrade inventory.
		powerStorage.setUpgradeInventory(upgradesInventory);
	}

	public int getRadius() {
		return range;
	}

	public boolean hasAxe() {
		return ModItemTags.matches(ModItemTags.FARMING_AXE, inputInventory.getStackInSlot(0).getItem());
	}

	public float getGrowthBonus() {
		FertalizerRecipe recipe = CraftingUtilities.getRecipe(ModRecipeTypes.FERTALIZER_RECIPE_TYPE.get(), new RecipeMatchParameters(this.fluidTankComponent.getFluid()), getLevel())
				.orElse(null);
		if (recipe != null) {
			return recipe.getFertalizationAmount();
		}
		return 0.0f;
	}

	public BlockPos getCurrentPosition() {
		return blocks.get(currentBlockIndex);
	}

	public boolean getShouldDrawRadiusPreview() {
		return shouldDrawRadiusPreview;
	}

	public void setShouldDrawRadiusPreview(boolean shouldDraw) {
		shouldDrawRadiusPreview = shouldDraw;
		if (shouldDraw) {
			// Set the scale equal to the range * 2 plus 1.
			Vector3f scale = new Vector3f((range * 2) + 1, 1.0f, (range * 2) + 1);
			// Shift over so we center the range around the farmer.
			Vector3f position = new Vector3f(getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ());
			Vec3i offsetDirection = this.getFacingDirection().getOpposite().getNormal();
			position.add(new Vector3f((offsetDirection.getX() * range) - range, 0.0f, (offsetDirection.getZ() * range) - range));
			position.add(new Vector3f(offsetDirection.getX(), 0.0f, offsetDirection.getZ()));
			// Add the entry.
			RadiusPreviewRenderer.addRadiusRenderRequest(this, "range", position, scale, new SDColor(0.1f, 1.0f, 0.2f, 0.25f));
		} else {
			// Remove the entry.
			RadiusPreviewRenderer.removeRadiusRenderer(this, "range");
		}
	}

	@Override
	public void process() {
		if (processingComponent.performedWorkLastTick()) {
			if (!getLevel().isClientSide()) {
				fluidTankComponent.drain(StaticPowerConfig.SERVER.treeFarmerFluidUsage.get(), FluidAction.EXECUTE);
			}
		}
	}

	protected ProcessingCheckState canProcess() {
		if (fluidTankComponent.getFluidAmount() < StaticPowerConfig.SERVER.treeFarmerFluidUsage.get()) {
			return ProcessingCheckState.notEnoughFluid();
		}
		if (!hasAxe()) {
			return ProcessingCheckState.error("Missing Axe!");
		}
		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState processingCompleted() {
		// Edge case where we somehow need to refresh blocks.
		if (blocks.size() == 0) {
			refreshBlocksInRange(range);
		}

		ProcessingOutputContainer outputContainer = processingComponent.getOutputContainer();
		// Only perform a harvest if the internal inventory is TOTALLY empty.
		if (outputContainer.getOutputItems().isEmpty()) {
			processBlock(getCurrentPosition());
			((ServerLevel) getLevel()).sendParticles(ParticleTypes.FALLING_WATER, getCurrentPosition().getX() + 0.5D, getCurrentPosition().getY() + 1.0D,
					getCurrentPosition().getZ() + 0.5D, 1, 0.0D, 0.0D, 0.0D, 0.0D);
			incrementPosition();
		}

		// For each of the farmed stacks, place the harvested stacks into the output
		// inventory. Remove the entry from the farmed stacks if it was fully inserted.
		// Otherwise, update the farmed stack.
		for (int i = 0; i < outputContainer.getOutputItems().size(); i++) {
			ItemStack extractedStack = outputContainer.getOutputItems().get(i).item().copy();
			InventoryComponent targetInventory = outputInventory;
			if (saplingIngredient.test(extractedStack)) {
				if (InventoryUtilities.canPartiallyInsertItemIntoInventory(inputInventory, extractedStack)) {
					targetInventory = inputInventory;
				}
			}
			ItemStack insertedStack = InventoryUtilities.insertItemIntoInventory(targetInventory, extractedStack, false);
			if (!insertedStack.isEmpty()) {
				outputContainer.getOutputItems().get(i).item().setCount(insertedStack.getCount());
			}
		}

		// Return true if we finished clearing the internal inventory.
		if (outputContainer.getOutputItems().isEmpty()) {
			outputContainer.clear();
			return ProcessingCheckState.ok();
		} else {
			return ProcessingCheckState.internalBufferNotEmpty();
		}
	}

	protected void refreshBlocksInRange(int range) {
		if (this.isRemoved()) {
			return;
		}

		// Get the forward and right directions.
		Direction forwardDirection = getFacingDirection();
		Direction rightDirection = SideConfigurationUtilities.getDirectionFromSide(BlockSide.RIGHT, forwardDirection);

		// Create the from Position.
		BlockPos fromPosition = getBlockPos().relative(forwardDirection.getOpposite());
		fromPosition = fromPosition.relative(forwardDirection.getOpposite(), range * 2);
		fromPosition = fromPosition.relative(rightDirection.getOpposite(), range);

		// Create the to Position.
		BlockPos toPosition = getBlockPos();
		toPosition = toPosition.relative(rightDirection, range);
		toPosition = toPosition.relative(forwardDirection.getOpposite(), 1);

		// Get all the blocks in the range from the from position to the to position.
		Stream<BlockPos> blockPos = BlockPos.betweenClosedStream(fromPosition, toPosition);
		Iterator<BlockPos> it = blockPos.iterator();

		// Clear the current blocks array and re-populate it.
		blocks.clear();
		do {
			BlockPos pos = it.next();
			blocks.add(pos.immutable());
		} while (it.hasNext());

		blocks.add(toPosition);

		// If the range has shrunk, correct for that.
		if (currentBlockIndex > blocks.size() - 1) {
			currentBlockIndex = 0;
		}
	}

	protected void incrementPosition() {
		currentBlockIndex = Math.floorMod(currentBlockIndex + 1, blocks.size() - 1);
	}

	protected void useAxe() {
		if (hasAxe()) {
			if (inputInventory.getStackInSlot(0).hurt(StaticPowerConfig.SERVER.treeFarmerToolUsage.get(), getLevel().getRandom(), null)) {
				inputInventory.setStackInSlot(0, ItemStack.EMPTY);
				getLevel().playSound(null, worldPosition, SoundEvents.ITEM_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
			}
		}
	}

	protected void processBlock(BlockPos pos) {
		if (isFarmableBlock(pos)) {
			// Clear the visited and add the current position.
			visited.clear();
			visited.add(pos);

			// Allocate a list for the harvested results and perform the harvest.
			List<ItemStack> harvestResults = new LinkedList<ItemStack>();
			harvestBlock(pos, harvestResults, visited);

			// Insert all the harvested results into the internal inventory.
			ProcessingOutputContainer outputContainer = processingComponent.getOutputContainer();
			outputContainer.open(null);
			for (int i = 0; i < harvestResults.size(); i++) {
				outputContainer.addOutputItem(harvestResults.get(i), CaptureType.COUNT_ONLY);
			}
			outputContainer.close();
		}

		// Plant a sapling in this place if possible and apply the bonemeal.
		plantSapling(pos);
		bonemealSapling(pos);
	}

	protected boolean isFarmableBlock(BlockPos pos) {
		// Get the block at the position.
		BlockState state = getLevel().getBlockState(pos);

		// Perform these sanity checks as a quick optimization (the ingredient test is
		// O(n)).
		if (!state.isAir() && !state.hasBlockEntity() && state.getDestroySpeed(getLevel(), pos) != -1) {
			return state.is(BlockTags.LEAVES) || state.is(BlockTags.LOGS);
		}
		return false;
	}

	protected void harvestBlock(BlockPos pos, List<ItemStack> items, HashSet<BlockPos> hitPositions) {
		// If we've hit our max recursion, stop.
		if (hitPositions.size() >= StaticPowerConfig.SERVER.treeFarmerMaxTreeRecursion.get()) {
			// return;
		}

		// Add the drops for the current block and break it.
		items.addAll(WorldUtilities.getBlockDrops(getLevel(), pos));
		hitPositions.add(pos);
		getLevel().destroyBlock(pos, false);
		powerStorage.drainPower(StaticPowerConfig.SERVER.treeFarmerHarvestPowerUsage.get(), false);
		useAxe();

		// Recurse to any adjacent blocks if they are farm-able.
		for (Direction facing : Direction.values()) {
			BlockPos testPos = pos.relative(facing);
			if (!hitPositions.contains(testPos)) {
				if (isFarmableBlock(testPos)) {
					harvestBlock(testPos, items, hitPositions);
				}
			}

			if (hitPositions.size() >= StaticPowerConfig.SERVER.treeFarmerMaxTreeRecursion.get()) {
				// return;
			}
		}
	}

	protected boolean bonemealSapling(BlockPos pos) {
		Block block = getLevel().getBlockState(pos).getBlock();
		if (block instanceof BonemealableBlock && SDMath.diceRoll(getGrowthBonus())) {
			BonemealableBlock growable = (BonemealableBlock) block;
			if (growable.isBonemealSuccess(getLevel(), getLevel().getRandom(), pos, getLevel().getBlockState(pos))
					&& growable.isValidBonemealTarget(getLevel(), pos, getLevel().getBlockState(pos), false)) {
				growable.performBonemeal((ServerLevel) getLevel(), getLevel().getRandom(), pos, getLevel().getBlockState(pos));
				((ServerLevel) getLevel()).sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, 1, 0.0D, 0.0D, 0.0D, 0.0D);
				return true;
			}
		}
		return false;
	}

	protected boolean plantSapling(BlockPos pos) {
		if (currentBlockIndex % StaticPowerConfig.SERVER.treeFarmerSaplingSpacing.get() == 0) {
			// Get the block space we're trying to plant IN.
			Block block = getLevel().getBlockState(pos).getBlock();
			// Make sure the block is empty.
			if (block == Blocks.AIR) {
				// Pick a random sapling from the input inventory.
				int saplingSlot = InventoryUtilities.getRandomSlotWithItemFromInventory(inputInventory, 1, 8, 1);
				// If no sapling was found, return early.
				if (saplingSlot == -1) {
					return false;
				}

				// Get the pending sapling.
				ItemStack sapling = inputInventory.getStackInSlot(saplingSlot);

				// If it is a valid sapling, attempt to plant it.
				if (!sapling.isEmpty() && saplingIngredient.test(sapling)) {
					// Create a fake player to plant the sapling. This handles all the checks for
					// that particular sapling. Meaning if some mod has saplings that only go on
					// stone, this will support that by deffering the plantable logic to the sapling
					// itsself.
					FakePlayer player = FakePlayerFactory.getMinecraft((ServerLevel) getLevel());
					player.setItemInHand(InteractionHand.MAIN_HAND, sapling.copy());
					InteractionResult placementResult = sapling
							.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, new BlockHitResult(new Vec3(0.0f, 1.0f, 0.0f), Direction.UP, pos, false)));

					if (placementResult.consumesAction()) {
						// Once planted, extract the sapling from the slot.
						inputInventory.extractItem(saplingSlot, 1, false);
						return true;
					} else {
						return false;
					}
				}
			}
		}
		return false;
	}

	protected void onUpgradesInventoryModifiedCallback(InventoryChangeType changeType, ItemStack item, int slot) {
		range = StaticPowerConfig.SERVER.treeFarmerDefaultRange.get();
		for (ItemStack stack : upgradesInventory) {
			if (stack.getItem() instanceof BaseRangeUpgrade) {
				range = (int) Math.max(range,
						StaticPowerConfig.SERVER.treeFarmerDefaultRange.get() * ((BaseRangeUpgrade) stack.getItem()).getTierObject().upgradeConfiguration.rangeUpgrade.get());
			}
		}
		refreshBlocksInRange(range);

		// Refresh the preview if it is currently begin drawn.
		if (getShouldDrawRadiusPreview()) {
			setShouldDrawRadiusPreview(true);
		}
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerTreeFarmer(windowId, inventory, this);
	}
}
