package theking530.staticpower.tileentities.powered.treefarmer;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import com.mojang.math.Vector3f;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
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
import theking530.staticcore.initialization.tileentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.SDMath;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.client.rendering.CustomRenderer;
import theking530.staticpower.client.rendering.tileentity.TileEntityRenderTreeFarmer;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerRecipeRegistry;
import theking530.staticpower.data.crafting.wrappers.farmer.FarmingFertalizerRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModTags;
import theking530.staticpower.items.upgrades.BaseRangeUpgrade;
import theking530.staticpower.tileentities.TileEntityMachine;
import theking530.staticpower.tileentities.components.control.AbstractProcesingComponent.ProcessingCheckState;
import theking530.staticpower.tileentities.components.control.MachineProcessingComponent;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationUtilities;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.tileentities.components.fluids.FluidTankComponent;
import theking530.staticpower.tileentities.components.items.BatteryInventoryComponent;
import theking530.staticpower.tileentities.components.items.FluidContainerInventoryComponent;
import theking530.staticpower.tileentities.components.items.FluidContainerInventoryComponent.FluidContainerInteractionMode;
import theking530.staticpower.tileentities.components.items.InputServoComponent;
import theking530.staticpower.tileentities.components.items.InventoryComponent;
import theking530.staticpower.tileentities.components.items.InventoryComponent.InventoryChangeType;
import theking530.staticpower.tileentities.components.items.ItemStackHandlerFilter;
import theking530.staticpower.tileentities.components.items.OutputServoComponent;
import theking530.staticpower.tileentities.components.items.UpgradeInventoryComponent;
import theking530.staticpower.utilities.InventoryUtilities;
import theking530.staticpower.utilities.WorldUtilities;

public class TileEntityTreeFarm extends TileEntityMachine {
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityTreeFarm> TYPE = new BlockEntityTypeAllocator<TileEntityTreeFarm>(
			(type, pos, state) -> new TileEntityTreeFarm(pos, state), ModBlocks.TreeFarmer);

	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setTileEntitySpecialRenderer(TileEntityRenderTreeFarmer::new);
		}
	}

	public final InventoryComponent inputInventory;
	public final InventoryComponent outputInventory;
	public final FluidContainerInventoryComponent fluidContainerComponent;
	public final BatteryInventoryComponent batteryInventory;
	public final UpgradeInventoryComponent upgradesInventory;

	public final InventoryComponent internalInventory;
	public final MachineProcessingComponent processingComponent;
	public final FluidTankComponent fluidTankComponent;

	private boolean shouldDrawRadiusPreview;

	private final List<BlockPos> blocks;
	private int currentBlockIndex;
	private int range;

	private final Ingredient woodIngredient;
	private final Ingredient leafIngredient;
	private final Ingredient saplingIngredient;

	public TileEntityTreeFarm(BlockPos pos, BlockState state) {
		super(TYPE, pos, state, StaticPowerTiers.STATIC);

		woodIngredient = Ingredient.of(ModTags.LOG);
		leafIngredient = Ingredient.of(ModTags.LEAVES);
		saplingIngredient = Ingredient.of(ModTags.SAPLING);

		registerComponent(inputInventory = new InventoryComponent("InputInventory", 10, MachineSideMode.Input)
				.setShiftClickEnabled(true).setFilter(new ItemStackHandlerFilter() {
					public boolean canInsertItem(int slot, ItemStack stack) {
						return slot == 0 ? ModTags.FARMING_AXE.contains(stack.getItem())
								: saplingIngredient.test(stack);
					}
				}).setSlotsLockable(true));

		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 9, MachineSideMode.Output));
		registerComponent(
				batteryInventory = new BatteryInventoryComponent("BatteryComponent", energyStorage.getStorage()));
		registerComponent(
				upgradesInventory = (UpgradeInventoryComponent) new UpgradeInventoryComponent("UpgradeInventory", 3)
						.setModifiedCallback(this::onUpgradesInventoryModifiedCallback));
		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 64));

		registerComponent(processingComponent = new MachineProcessingComponent("ProcessingComponent",
				StaticPowerConfig.SERVER.treeFarmerProcessingTime.get(), this::canProcess, this::canProcess,
				this::processingCompleted, true));
		processingComponent.setUpgradeInventory(upgradesInventory);
		processingComponent.setRedstoneControlComponent(redstoneControlComponent);
		processingComponent.setEnergyComponent(energyStorage);
		processingComponent.setProcessingPowerUsage(StaticPowerConfig.SERVER.treeFarmerPowerUsage.get());

		registerComponent(fluidTankComponent = new FluidTankComponent("FluidTank", 5000, (fluid) -> {
			return StaticPowerRecipeRegistry
					.getRecipe(FarmingFertalizerRecipe.RECIPE_TYPE, new RecipeMatchParameters(fluid)).isPresent();
		}).setCapabilityExposedModes(MachineSideMode.Input).setUpgradeInventory(upgradesInventory)
				.setAutoSyncPacketsEnabled(true));

		currentBlockIndex = 0;
		shouldDrawRadiusPreview = false;
		range = StaticPowerConfig.SERVER.treeFarmerDefaultRange.get();
		blocks = new LinkedList<BlockPos>();
		registerComponent(
				fluidContainerComponent = new FluidContainerInventoryComponent("BucketDrain", fluidTankComponent));
		registerComponent(new InputServoComponent("InputServo", 4, inputInventory));
		registerComponent(new OutputServoComponent("OutputServo", 4, outputInventory));

		fluidContainerComponent.setMode(FluidContainerInteractionMode.DRAIN);
		// Set the energy storage upgrade inventory.
		energyStorage.setUpgradeInventory(upgradesInventory);
	}

	@Override
	public void process() {
		if (processingComponent.isPerformingWork()) {
			if (!getLevel().isClientSide) {
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

		if (InventoryUtilities.isInventoryEmpty(internalInventory)) {
			if (farmTree(getCurrentPosition())) {
				useAxe();
			}
			incrementPosition();
		}

		// For each of the farmed stacks, place the harvested stacks into the output
		// inventory. Remove the entry from the farmed stacks if it was fully inserted.
		// Otherwise, update the farmed stack.
		for (int i = 0; i < internalInventory.getSlots(); i++) {
			ItemStack extractedStack = internalInventory.extractItem(i, Integer.MAX_VALUE, false);
			InventoryComponent targetInventory = outputInventory;
			if (saplingIngredient.test(extractedStack)) {
				if (InventoryUtilities.canPartiallyInsertItemIntoInventory(inputInventory, extractedStack)) {
					targetInventory = inputInventory;
				}
			}
			ItemStack insertedStack = InventoryUtilities.insertItemIntoInventory(targetInventory, extractedStack,
					false);
			if (!insertedStack.isEmpty()) {
				internalInventory.setStackInSlot(i, insertedStack);
			}
		}

		// Return true if we finished clearing the internal inventory.
		if (InventoryUtilities.isInventoryEmpty(internalInventory)) {
			return ProcessingCheckState.ok();
		} else {
			return ProcessingCheckState.internalInventoryNotEmpty();
		}
	}

	private void refreshBlocksInRange(int range) {
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

	private void incrementPosition() {
		if (!getLevel().isClientSide) {
			currentBlockIndex = Math.floorMod(currentBlockIndex + 1, blocks.size() - 1);
		}
	}

	public boolean hasAxe() {
		return ModTags.FARMING_AXE.contains(inputInventory.getStackInSlot(0).getItem());
	}

	public void useAxe() {
		if (hasAxe()) {
			if (inputInventory.getStackInSlot(0).hurt(StaticPowerConfig.SERVER.treeFarmerToolUsage.get(),
					getLevel().random, null)) {
				inputInventory.setStackInSlot(0, ItemStack.EMPTY);
				getLevel().playSound(null, worldPosition, SoundEvents.ITEM_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
			}
		}
	}

	public boolean farmTree(BlockPos pos) {
		if (!getLevel().isClientSide) {
			if (isFarmableBlock(pos)) {
				getLevel().playSound(null, pos,
						getLevel().getBlockState(pos).getBlock()
								.getSoundType(getLevel().getBlockState(pos), level, pos, null).getBreakSound(),
						SoundSource.BLOCKS, 0.5F, 1.0F);
				List<ItemStack> harvestResults = new LinkedList<ItemStack>();
				harvestBlock(pos, harvestResults, 0);
				useAxe();
				for (ItemStack drop : harvestResults) {
					InventoryUtilities.insertItemIntoInventory(internalInventory, drop, false);
				}
			}
			plantSapling(pos);
			if (bonemealSapling(pos)) {
				getLevel().addParticle(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5D, pos.getY() + 1.0D,
						pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
			}
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	public boolean isFarmableBlock(BlockPos pos) {
		// Get the block at the position.
		Block block = getLevel().getBlockState(pos).getBlock();

		// Perform these sanity checks as a quick optimization (the ingredient test is
		// O(n)).
		if (block != Blocks.AIR && !getLevel().getBlockState(pos).hasBlockEntity()
				&& getLevel().getBlockState(pos).getDestroySpeed(getLevel(), pos) != -1) {
			return woodIngredient.test(new ItemStack(Item.byBlock(block)))
					|| this.leafIngredient.test(new ItemStack(Item.byBlock(block)));
		}
		return false;
	}

	private void harvestBlock(BlockPos pos, List<ItemStack> items, int index) {
		// If we've hit our max recursion, stop.
		if (index >= StaticPowerConfig.SERVER.treeFarmerMaxTreeRecursion.get()) {
			return;
		}

		// Add the drops for the current block and break it.
		items.addAll(WorldUtilities.getBlockDrops(getLevel(), pos));
		getLevel().setBlock(pos, Blocks.AIR.defaultBlockState(), 1 | 2);
		energyStorage.useBulkPower(StaticPowerConfig.SERVER.treeFarmerHarvestPowerUsage.get());

		// Recurse to any adjacent blocks if they are farm-able.
		for (Direction facing : Direction.values()) {
			BlockPos testPos = pos.relative(facing);
			if (isFarmableBlock(testPos)) {
				harvestBlock(testPos, items, index + 1);
			}
		}
	}

	public boolean bonemealSapling(BlockPos pos) {
		if (getLevel().isClientSide) {
			throw new RuntimeException("This method should only be called on the server!");
		}
		Block block = getLevel().getBlockState(pos).getBlock();
		if (block instanceof BonemealableBlock && SDMath.diceRoll(getGrowthBonus())) {
			BonemealableBlock growable = (BonemealableBlock) block;
			if (growable.isBonemealSuccess(getLevel(), getLevel().random, pos, getLevel().getBlockState(pos))
					&& growable.isValidBonemealTarget(getLevel(), pos, getLevel().getBlockState(pos), false)) {
				growable.performBonemeal((ServerLevel) getLevel(), getLevel().random, pos,
						getLevel().getBlockState(pos));
				return true;
			}
		}
		return false;
	}

	public boolean plantSapling(BlockPos pos) {
		if (getLevel().isClientSide) {
			throw new RuntimeException("This method should only be called on the server!");
		}
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
							.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND,
									new BlockHitResult(new Vec3(0.0f, 1.0f, 0.0f), Direction.UP, pos, false)));

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

	public int getRadius() {
		return range;
	}

	public float getGrowthBonus() {
		FarmingFertalizerRecipe recipe = StaticPowerRecipeRegistry.getRecipe(FarmingFertalizerRecipe.RECIPE_TYPE,
				new RecipeMatchParameters(this.fluidTankComponent.getFluid())).orElse(null);
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
			position.add(new Vector3f((offsetDirection.getX() * range) - range, 0.0f,
					(offsetDirection.getZ() * range) - range));
			position.add(new Vector3f(offsetDirection.getX(), 0.0f, offsetDirection.getZ()));
			// Add the entry.
			CustomRenderer.addCubeRenderer(this, "range", position, scale, new Color(0.1f, 1.0f, 0.2f, 0.25f));
		} else {
			// Remove the entry.
			CustomRenderer.removeCubeRenderer(this, "range");
		}
	}

	public void onUpgradesInventoryModifiedCallback(InventoryChangeType changeType, ItemStack item, int slot) {
		range = StaticPowerConfig.SERVER.treeFarmerDefaultRange.get();
		for (ItemStack stack : upgradesInventory) {
			if (stack.getItem() instanceof BaseRangeUpgrade) {
				range = (int) Math.max(range, StaticPowerConfig.SERVER.treeFarmerDefaultRange.get()
						* (((BaseRangeUpgrade) stack.getItem()).getTier().rangeUpgrade.get()));
			}
		}
		refreshBlocksInRange(range);

		// Refresh the preview if it is currently begin drawn.
		if (getShouldDrawRadiusPreview()) {
			setShouldDrawRadiusPreview(true);
		}
	}

	public CompoundTag serializeUpdateNbt(CompoundTag nbt, boolean fromUpdate) {
		super.serializeUpdateNbt(nbt, fromUpdate);
		nbt.putInt("current_index", currentBlockIndex);
		nbt.putInt("range", range);
		return nbt;
	}

	public void deserializeUpdateNbt(CompoundTag nbt, boolean fromUpdate) {
		super.deserializeUpdateNbt(nbt, fromUpdate);
		currentBlockIndex = nbt.getInt("current_index");
		range = nbt.getInt("range");
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerTreeFarmer(windowId, inventory, this);
	}
}
