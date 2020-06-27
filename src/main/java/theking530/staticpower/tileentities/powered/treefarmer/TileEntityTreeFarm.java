package theking530.staticpower.tileentities.powered.treefarmer;

import java.util.ArrayList;
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
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import theking530.api.utilities.SDMath;
import theking530.staticpower.initialization.ModTags;
import theking530.staticpower.initialization.ModTileEntityTypes;
import theking530.staticpower.tileentities.TileEntityMachine;
import theking530.staticpower.tileentities.components.BatteryComponent;
import theking530.staticpower.tileentities.components.FluidContainerComponent;
import theking530.staticpower.tileentities.components.FluidContainerComponent.FluidContainerInteractionMode;
import theking530.staticpower.tileentities.components.FluidTankComponent;
import theking530.staticpower.tileentities.components.InputServoComponent;
import theking530.staticpower.tileentities.components.InventoryComponent;
import theking530.staticpower.tileentities.components.MachineProcessingComponent;
import theking530.staticpower.tileentities.components.OutputServoComponent;
import theking530.staticpower.tileentities.utilities.MachineSideMode;
import theking530.staticpower.tileentities.utilities.SideConfigurationUtilities;
import theking530.staticpower.tileentities.utilities.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.utilities.InventoryUtilities;
import theking530.staticpower.utilities.WorldUtilities;

public class TileEntityTreeFarm extends TileEntityMachine {
	public static final int DEFAULT_WATER_USAGE = 1;
	public static final int DEFAULT_IDLE_ENERGY_USAGE = 20;
	public static final int DEFAULT_HARVEST_ENERGY_COST = 1000;
	public static final int MAX_WOOD_RECURSIVE_DEPTH = 40;
	public static final int MAX_LEAVES_RECURSIVE_DEPTH = 40;
	public static final int DEFAULT_RANGE = 2;
	public static final int DEFAULT_SAPLING_SPACING = 2;
	public static final Random RANDOM = new Random();

	public final InventoryComponent inputInventory;
	public final InventoryComponent outputInventory;
	public final InventoryComponent fluidContainerInventoy;
	public final InventoryComponent batteryInventory;
	public final InventoryComponent upgradesInventory;
	public final InventoryComponent internalInventory;
	public final MachineProcessingComponent processingComponent;
	public final FluidTankComponent fluidTankComponent;

	private FluidContainerComponent fluidInteractionComponent;
	private boolean shouldDrawRadiusPreview;

	private List<BlockPos> blocks;
	private int currentBlockIndex;
	private int range;

	private Ingredient woodIngredient;
	private Ingredient leafIngredient;
	private Ingredient saplingIngredient;

	public TileEntityTreeFarm() {
		super(ModTileEntityTypes.TREE_FARM);
		registerComponent(inputInventory = new InventoryComponent("InputInventory", 10, MachineSideMode.Input));
		registerComponent(fluidContainerInventoy = new InventoryComponent("FluidContainerInventoy", 2, MachineSideMode.Never));
		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 9, MachineSideMode.Output));
		registerComponent(batteryInventory = new InventoryComponent("BatteryInventory", 1, MachineSideMode.Never));
		registerComponent(upgradesInventory = new InventoryComponent("UpgradeInventory", 3, MachineSideMode.Never));
		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 100, MachineSideMode.Never));
		registerComponent(processingComponent = new MachineProcessingComponent("ProcessingComponent", 5, this::processingCompleted, true));
		registerComponent(fluidTankComponent = new FluidTankComponent("FluidTank", 5000, (fluid) -> fluid.getFluid() == Fluids.WATER).setCapabilityExposedModes(MachineSideMode.Input));

		currentBlockIndex = 0;
		woodIngredient = Ingredient.fromTag(ModTags.LOG);
		leafIngredient = Ingredient.fromTag(ModTags.LEAVES);
		saplingIngredient = Ingredient.fromTag(ModTags.SAPLING);
		shouldDrawRadiusPreview = false;
		range = DEFAULT_RANGE;
		blocks = new LinkedList<BlockPos>();
		registerComponent(fluidInteractionComponent = new FluidContainerComponent("BucketDrain", fluidContainerInventoy, fluidTankComponent, 0, 1));
		registerComponent(new InputServoComponent("InputServo", 4, inputInventory));
		registerComponent(new OutputServoComponent("OutputServo", 4, outputInventory));
		registerComponent(new BatteryComponent("BatteryComponent", batteryInventory, 0, energyStorage.getStorage()));

		fluidInteractionComponent.setMode(FluidContainerInteractionMode.DRAIN);
	}

	@Override
	public void validate() {
		super.validate();
	}

	@Override
	public void process() {
		// Draw the idle usage of water and energy per tick.
		if (!world.isRemote) {
			// If the blocks to farm list is empty, refresh it.
			if (blocks.isEmpty()) {
				refreshBlocksInRange(range);
			}

			if (!canProcess()) {
				processingComponent.pauseProcessing();
			} else {
				// Otherwise, continue the processing.
				processingComponent.startProcessing();
				energyStorage.getStorage().extractEnergy(DEFAULT_IDLE_ENERGY_USAGE, false);
				fluidTankComponent.drain(DEFAULT_WATER_USAGE, FluidAction.EXECUTE);
			}
		}
		markTileEntityForSynchronization();
	}

	protected boolean canProcess() {
		return energyStorage.hasEnoughPower(DEFAULT_IDLE_ENERGY_USAGE) && fluidTankComponent.getFluidAmount() >= DEFAULT_WATER_USAGE && hasAxe();
	}

	protected boolean processingCompleted() {
		// Edge case where we somehow need to refresh blocks.
		if (blocks.size() == 0) {
			refreshBlocksInRange(range);
		}

		if (InventoryUtilities.isInventoryEmpty(internalInventory)) {
			if (farmTree(getCurrentPosition())) {
				useAxe();
			}
			incrementPosition();
		} else {
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

			// Sync the tile entity.
			markTileEntityForSynchronization();
		}

		// Return true if we finished clearing the internal inventory.
		return InventoryUtilities.isInventoryEmpty(internalInventory);
	}

	private void refreshBlocksInRange(int range) {
		Direction forwardDirection = getFacingDirection();
		Direction rightDirection = SideConfigurationUtilities.getDirectionFromSide(BlockSide.RIGHT, forwardDirection);

		BlockPos fromPosition = getPos().offset(forwardDirection.getOpposite());
		fromPosition = fromPosition.offset(forwardDirection.getOpposite(), range * 2);
		fromPosition = fromPosition.offset(rightDirection.getOpposite(), range);

		BlockPos toPosition = getPos();
		toPosition = toPosition.offset(rightDirection, range);
		toPosition = toPosition.offset(forwardDirection.getOpposite(), 1);

		Stream<BlockPos> blockPos = BlockPos.getAllInBox(fromPosition, toPosition);
		Iterator<BlockPos> it = blockPos.iterator();

		blocks.clear();
		do {
			BlockPos pos = it.next();
			blocks.add(new BlockPos(pos));
		} while (it.hasNext());

		if (currentBlockIndex > blocks.size() - 1) {
			currentBlockIndex = 0;
		}
	}

	private void incrementPosition() {
		currentBlockIndex++;
		if (currentBlockIndex > blocks.size() - 1) {
			currentBlockIndex = 0;
		}
		BlockPos pos = getCurrentPosition();
		getWorld().addParticle(ParticleTypes.TOTEM_OF_UNDYING, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
	}

	public boolean hasAxe() {
		return inputInventory.getStackInSlot(0).getItem() instanceof AxeItem;
	}

	public void useAxe() {
		if (hasAxe()) {
			if (inputInventory.getStackInSlot(0).attemptDamageItem(1, RANDOM, null)) {
				inputInventory.setStackInSlot(0, ItemStack.EMPTY);
				getWorld().playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
			}
		}
	}

	public boolean farmTree(BlockPos pos) {
		if (!getWorld().isRemote) {
			for (int i = 0; i < getRadius() * 2; i++) {
				if (isFarmableBlock(pos.add(0, i, 0))) {
					List<ItemStack> treeBlocks = harvestTree(pos.add(0, i, 0), 0);
					for (ItemStack drop : treeBlocks) {
						InventoryUtilities.insertItemIntoInventory(internalInventory, drop, false);
					}
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
		Block block = getWorld().getBlockState(pos).getBlock();
		if (block != Blocks.AIR && !block.hasTileEntity(getWorld().getBlockState(pos)) && block.getBlockHardness(getWorld().getBlockState(pos), getWorld(), pos) != -1) {
			return woodIngredient.test(new ItemStack(Item.getItemFromBlock(block)));
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	private List<ItemStack> harvestTree(BlockPos pos, int index) {
		List<ItemStack> wood = new ArrayList<ItemStack>();
		if (index >= MAX_WOOD_RECURSIVE_DEPTH) {
			return wood;
		}
		wood.addAll(WorldUtilities.getBlockDrops(getWorld(), pos));
		getWorld().playSound(null, pos, getWorld().getBlockState(pos).getBlock().getSoundType(getWorld().getBlockState(pos), world, pos, null).getBreakSound(), SoundCategory.BLOCKS, 0.5F, 1.0F);
		getWorld().setBlockState(pos, Blocks.AIR.getDefaultState(), 1 | 2);
		for (Direction facing : Direction.values()) {
			BlockPos testPos = pos.offset(facing);
			if (isFarmableBlock(testPos)) {
				wood.addAll(harvestTree(testPos, index + 1));
			}
		}
		for (Direction facing : Direction.values()) {
			Block block = getWorld().getBlockState(pos.offset(facing)).getBlock();
			if (block != Blocks.AIR && !block.hasTileEntity(getWorld().getBlockState(pos)) && block.getBlockHardness(getWorld().getBlockState(pos), getWorld(), pos) != -1) {
				if (leafIngredient.test(new ItemStack(Item.getItemFromBlock(block)))) {
					wood.addAll(recurseLeaves(pos.offset(facing), 0));
				}
			}
		}
		return wood;
	}

	@SuppressWarnings("deprecation")
	private List<ItemStack> recurseLeaves(BlockPos pos, int index) {
		List<ItemStack> leaves = new ArrayList<ItemStack>();
		if (index >= MAX_LEAVES_RECURSIVE_DEPTH) {
			return leaves;
		}
		leaves.addAll(WorldUtilities.getBlockDrops(getWorld(), pos));
		getWorld().playSound(null, pos, getWorld().getBlockState(pos).getBlock().getSoundType(getWorld().getBlockState(pos), world, pos, null).getBreakSound(), SoundCategory.BLOCKS, 0.5F, 1.0F);
		getWorld().setBlockState(pos, Blocks.AIR.getDefaultState(), 1 | 2);
		for (Direction facing : Direction.values()) {
			BlockPos testPos = pos.offset(facing);
			Block block = getWorld().getBlockState(testPos).getBlock();
			if (block != Blocks.AIR && !block.hasTileEntity(getWorld().getBlockState(testPos)) && block.getBlockHardness(getWorld().getBlockState(testPos), getWorld(), testPos) != -1) {
				if (leafIngredient.test(new ItemStack(Item.getItemFromBlock(block)))) {
					leaves.addAll(recurseLeaves(testPos, index + 1));
				}
			}
		}
		return leaves;
	}

	public boolean bonemealSapling(BlockPos pos) {
		if (getWorld().isRemote) {
			throw new RuntimeException("This method should only be called on the server!");
		}
		Block block = getWorld().getBlockState(pos).getBlock();
		if (block instanceof IGrowable && SDMath.diceRoll(getGrowthBonusChance())) {
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
			Block block = getWorld().getBlockState(pos).getBlock();
			if (block == Blocks.AIR) {
				for (int i = 1; i < 10; i++) {
					ItemStack inputStack = inputInventory.getStackInSlot(i);
					if (!inputStack.isEmpty() && saplingIngredient.test(inputStack)) {
						FakePlayer player = FakePlayerFactory.getMinecraft((ServerWorld) getWorld());
						player.setHeldItem(Hand.MAIN_HAND, inputStack.copy());
						inputStack.onItemUse(new ItemUseContext(player, Hand.MAIN_HAND, new BlockRayTraceResult(new Vec3d(0.0f, 1.0f, 0.0f), Direction.UP, pos, false)));
						inputInventory.extractItem(i, 1, false);
					}
				}
				return true;
			}
		}
		return false;
	}

	public FluidContainerComponent getFluidInteractionComponent() {
		return fluidInteractionComponent;
	}

	public int getRadius() {
		return range;
	}

	public float getGrowthBonusChance() {
		return 0;
	}

	public BlockPos getCurrentPosition() {
		return blocks.get(currentBlockIndex);
	}

	public boolean getShouldDrawRadiusPreview() {
		return shouldDrawRadiusPreview;
	}

	public void setShouldDrawRadiusPreview(boolean shouldDraw) {
		shouldDrawRadiusPreview = shouldDraw;
	}

	public CompoundNBT serializeUpdateNbt(CompoundNBT nbt, boolean fromUpdate) {
		super.serializeUpdateNbt(nbt, fromUpdate);
		nbt.putInt("current_index", currentBlockIndex);
		return nbt;
	}

	public void deserializeUpdateNbt(CompoundNBT nbt, boolean fromUpdate) {
		super.deserializeUpdateNbt(nbt, fromUpdate);
		currentBlockIndex = nbt.getInt("current_index");
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerTreeFarmer(windowId, inventory, this);
	}
}
