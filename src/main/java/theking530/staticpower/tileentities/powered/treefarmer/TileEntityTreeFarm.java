package theking530.staticpower.tileentities.powered.treefarmer;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.tileentity.TileEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.SDMath;
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
	public static final TileEntityTypeAllocator<TileEntityTreeFarm> TYPE = new TileEntityTypeAllocator<TileEntityTreeFarm>((type) -> new TileEntityTreeFarm(), ModBlocks.TreeFarmer);

	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setTileEntitySpecialRenderer(TileEntityRenderTreeFarmer::new);
		}
	}

	public static final int DEFAULT_WATER_USAGE = 1;
	public static final int DEFAULT_IDLE_ENERGY_USAGE = 10;
	public static final int DEFAULT_HARVEST_ENERGY_COST = 1000;
	public static final int MAX_WOOD_RECURSIVE_DEPTH = 100;
	public static final int DEFAULT_RANGE = 2;
	public static final int DEFAULT_SAPLING_SPACING = 2;
	public static final int DEFAULT_TOOL_USAGE = 1;
	public static final Random RANDOM = new Random();

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

	public TileEntityTreeFarm() {
		super(TYPE, StaticPowerTiers.STATIC);

		woodIngredient = Ingredient.fromTag(ModTags.LOG);
		leafIngredient = Ingredient.fromTag(ModTags.LEAVES);
		saplingIngredient = Ingredient.fromTag(ModTags.SAPLING);

		registerComponent(inputInventory = new InventoryComponent("InputInventory", 10, MachineSideMode.Input).setShiftClickEnabled(true).setFilter(new ItemStackHandlerFilter() {
			public boolean canInsertItem(int slot, ItemStack stack) {
				return slot == 0 ? ModTags.FARMING_AXE.contains(stack.getItem()) : saplingIngredient.test(stack);
			}
		}).setSlotsLockable(true));

		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 9, MachineSideMode.Output));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", energyStorage.getStorage()));
		registerComponent(
				upgradesInventory = (UpgradeInventoryComponent) new UpgradeInventoryComponent("UpgradeInventory", 3).setModifiedCallback(this::onUpgradesInventoryModifiedCallback));
		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 64));

		registerComponent(processingComponent = new MachineProcessingComponent("ProcessingComponent", 5, this::canProcess, this::canProcess, this::processingCompleted, true));
		processingComponent.setUpgradeInventory(upgradesInventory);
		processingComponent.setRedstoneControlComponent(redstoneControlComponent);
		processingComponent.setEnergyComponent(energyStorage);
		processingComponent.setProcessingPowerUsage(DEFAULT_IDLE_ENERGY_USAGE);

		registerComponent(fluidTankComponent = new FluidTankComponent("FluidTank", 5000, (fluid) -> {
			return StaticPowerRecipeRegistry.getRecipe(FarmingFertalizerRecipe.RECIPE_TYPE, new RecipeMatchParameters(fluid)).isPresent();
		}).setCapabilityExposedModes(MachineSideMode.Input).setUpgradeInventory(upgradesInventory).setAutoSyncPacketsEnabled(true));

		currentBlockIndex = 0;
		shouldDrawRadiusPreview = false;
		range = DEFAULT_RANGE;
		blocks = new LinkedList<BlockPos>();
		registerComponent(fluidContainerComponent = new FluidContainerInventoryComponent("BucketDrain", fluidTankComponent));
		registerComponent(new InputServoComponent("InputServo", 4, inputInventory));
		registerComponent(new OutputServoComponent("OutputServo", 4, outputInventory));

		fluidContainerComponent.setMode(FluidContainerInteractionMode.DRAIN);
		// Set the energy storage upgrade inventory.
		energyStorage.setUpgradeInventory(upgradesInventory);
	}

	@Override
	public void process() {
		if (processingComponent.isPerformingWork()) {
			if (!getWorld().isRemote) {
				fluidTankComponent.drain(DEFAULT_WATER_USAGE, FluidAction.EXECUTE);
			}
		}
	}

	protected ProcessingCheckState canProcess() {
		if (fluidTankComponent.getFluidAmount() < DEFAULT_WATER_USAGE) {
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
			ItemStack insertedStack = InventoryUtilities.insertItemIntoInventory(targetInventory, extractedStack, false);
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
		BlockPos fromPosition = getPos().offset(forwardDirection.getOpposite());
		fromPosition = fromPosition.offset(forwardDirection.getOpposite(), range * 2);
		fromPosition = fromPosition.offset(rightDirection.getOpposite(), range);

		// Create the to Position.
		BlockPos toPosition = getPos();
		toPosition = toPosition.offset(rightDirection, range);
		toPosition = toPosition.offset(forwardDirection.getOpposite(), 1);

		// Get all the blocks in the range from the from position to the to position.
		Stream<BlockPos> blockPos = BlockPos.getAllInBox(fromPosition, toPosition);
		Iterator<BlockPos> it = blockPos.iterator();

		// Clear the current blocks array and re-populate it.
		blocks.clear();
		do {
			BlockPos pos = it.next();
			blocks.add(pos.toImmutable());
		} while (it.hasNext());

		blocks.add(toPosition);

		// If the range has shrunk, correct for that.
		if (currentBlockIndex > blocks.size() - 1) {
			currentBlockIndex = 0;
		}
	}

	private void incrementPosition() {
		if (!getWorld().isRemote) {
			currentBlockIndex = Math.floorMod(currentBlockIndex + 1, blocks.size() - 1);
		}
	}

	public boolean hasAxe() {
		return ModTags.FARMING_AXE.contains(inputInventory.getStackInSlot(0).getItem());
	}

	public void useAxe() {
		if (hasAxe()) {
			if (inputInventory.getStackInSlot(0).attemptDamageItem(DEFAULT_TOOL_USAGE, RANDOM, null)) {
				inputInventory.setStackInSlot(0, ItemStack.EMPTY);
				getWorld().playSound(null, pos, SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);
			}
		}
	}

	public boolean farmTree(BlockPos pos) {
		if (!getWorld().isRemote) {
			if (isFarmableBlock(pos)) {
				getWorld().playSound(null, pos, getWorld().getBlockState(pos).getBlock().getSoundType(getWorld().getBlockState(pos), world, pos, null).getBreakSound(), SoundCategory.BLOCKS,
						0.5F, 1.0F);
				List<ItemStack> harvestResults = new LinkedList<ItemStack>();
				harvestBlock(pos, harvestResults, 0);
				useAxe();
				for (ItemStack drop : harvestResults) {
					InventoryUtilities.insertItemIntoInventory(internalInventory, drop, false);
				}
			}
			plantSapling(pos);
			if (bonemealSapling(pos)) {
				getWorld().addParticle(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
			}
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	public boolean isFarmableBlock(BlockPos pos) {
		// Get the block at the position.
		Block block = getWorld().getBlockState(pos).getBlock();

		// Perform these sanity checks as a quick optimization (the ingredient test is
		// O(n)).
		if (block != Blocks.AIR && !block.hasTileEntity(getWorld().getBlockState(pos)) && getWorld().getBlockState(pos).getBlockHardness(getWorld(), pos) != -1) {
			return woodIngredient.test(new ItemStack(Item.getItemFromBlock(block))) || this.leafIngredient.test(new ItemStack(Item.getItemFromBlock(block)));
		}
		return false;
	}

	private void harvestBlock(BlockPos pos, List<ItemStack> items, int index) {
		// If we've hit our max recursion, stop.
		if (index >= MAX_WOOD_RECURSIVE_DEPTH) {
			return;
		}

		// Add the drops for the current block and break it.
		items.addAll(WorldUtilities.getBlockDrops(getWorld(), pos));
		getWorld().setBlockState(pos, Blocks.AIR.getDefaultState(), 1 | 2);
		energyStorage.useBulkPower(DEFAULT_HARVEST_ENERGY_COST);

		// Recurse to any adjacent blocks if they are farm-able.
		for (Direction facing : Direction.values()) {
			BlockPos testPos = pos.offset(facing);
			if (isFarmableBlock(testPos)) {
				harvestBlock(testPos, items, index + 1);
			}
		}
	}

	public boolean bonemealSapling(BlockPos pos) {
		if (getWorld().isRemote) {
			throw new RuntimeException("This method should only be called on the server!");
		}
		Block block = getWorld().getBlockState(pos).getBlock();
		if (block instanceof IGrowable && SDMath.diceRoll(getGrowthBonus())) {
			IGrowable growable = (IGrowable) block;
			if (growable.canUseBonemeal(getWorld(), RANDOM, pos, getWorld().getBlockState(pos)) && growable.canGrow(getWorld(), pos, getWorld().getBlockState(pos), false)) {
				growable.grow((ServerWorld) getWorld(), RANDOM, pos, getWorld().getBlockState(pos));
				return true;
			}
		}
		return false;
	}

	public boolean plantSapling(BlockPos pos) {
		if (getWorld().isRemote) {
			throw new RuntimeException("This method should only be called on the server!");
		}
		if (currentBlockIndex % DEFAULT_SAPLING_SPACING == 0) {
			// Get the block space we're trying to plant IN.
			Block block = getWorld().getBlockState(pos).getBlock();
			// Make sure the block is empty.
			if (block == Blocks.AIR) {
				// Pick a random sapling from the input inventory.
				int saplingSlot = InventoryUtilities.getRandomSlotWithItemFromInventory(inputInventory, 1, 8, 1, true);
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
					FakePlayer player = FakePlayerFactory.getMinecraft((ServerWorld) getWorld());
					player.setHeldItem(Hand.MAIN_HAND, sapling.copy());
					ActionResultType placementResult = sapling
							.onItemUse(new ItemUseContext(player, Hand.MAIN_HAND, new BlockRayTraceResult(new Vector3d(0.0f, 1.0f, 0.0f), Direction.UP, pos, false)));

					if (placementResult.isSuccess()) {
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
		FarmingFertalizerRecipe recipe = StaticPowerRecipeRegistry.getRecipe(FarmingFertalizerRecipe.RECIPE_TYPE, new RecipeMatchParameters(this.fluidTankComponent.getFluid())).orElse(null);
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
			Vector3f position = new Vector3f(getTileEntity().getPos().getX(), getTileEntity().getPos().getY(), getTileEntity().getPos().getZ());
			Vector3i offsetDirection = this.getFacingDirection().getOpposite().getDirectionVec();
			position.add(new Vector3f((offsetDirection.getX() * range) - range, 0.0f, (offsetDirection.getZ() * range) - range));
			position.add(new Vector3f(offsetDirection.getX(), 0.0f, offsetDirection.getZ()));
			// Add the entry.
			CustomRenderer.addCubeRenderer(getTileEntity(), "range", position, scale, new Color(0.1f, 1.0f, 0.2f, 0.25f));
		} else {
			// Remove the entry.
			CustomRenderer.removeCubeRenderer(getTileEntity(), "range");
		}
	}

	public void onUpgradesInventoryModifiedCallback(InventoryChangeType changeType, ItemStack item, int slot) {
		range = DEFAULT_RANGE;
		for (ItemStack stack : upgradesInventory) {
			if (stack.getItem() instanceof BaseRangeUpgrade) {
				range = (int) Math.max(range, DEFAULT_RANGE * (((BaseRangeUpgrade) stack.getItem()).getTier().rangeUpgrade.get()));
			}
		}
		refreshBlocksInRange(range);
		markTileEntityForSynchronization();

		// Refresh the preview if it is currently begin drawn.
		if (getShouldDrawRadiusPreview()) {
			setShouldDrawRadiusPreview(true);
		}
	}

	public CompoundNBT serializeUpdateNbt(CompoundNBT nbt, boolean fromUpdate) {
		super.serializeUpdateNbt(nbt, fromUpdate);
		nbt.putInt("current_index", currentBlockIndex);
		nbt.putInt("range", range);
		return nbt;
	}

	public void deserializeUpdateNbt(CompoundNBT nbt, boolean fromUpdate) {
		super.deserializeUpdateNbt(nbt, fromUpdate);
		currentBlockIndex = nbt.getInt("current_index");
		range = nbt.getInt("range");
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerTreeFarmer(windowId, inventory, this);
	}
}
