package theking530.staticpower.tileentities.powered.treefarmer;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.IGrowable;
import net.minecraft.client.renderer.Vector3f;
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
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import theking530.common.utilities.Color;
import theking530.common.utilities.SDMath;
import theking530.staticpower.client.rendering.CustomRenderer;
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
import theking530.staticpower.tileentities.utilities.interfaces.ItemStackHandlerFilter;
import theking530.staticpower.utilities.InventoryUtilities;
import theking530.staticpower.utilities.WorldUtilities;

public class TileEntityTreeFarm extends TileEntityMachine {
	public static final int DEFAULT_WATER_USAGE = 1;
	public static final int DEFAULT_IDLE_ENERGY_USAGE = 20;
	public static final int DEFAULT_HARVEST_ENERGY_COST = 1000;
	public static final int MAX_WOOD_RECURSIVE_DEPTH = 100;
	public static final int DEFAULT_RANGE = 2;
	public static final int DEFAULT_SAPLING_SPACING = 2;
	public static final int DEFAULT_TOOL_USAGE = 1;
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

	private final List<BlockPos> blocks;
	private int currentBlockIndex;
	private int range;

	private Ingredient woodIngredient;
	private Ingredient leafIngredient;
	private Ingredient saplingIngredient;

	public TileEntityTreeFarm() {
		super(ModTileEntityTypes.TREE_FARM);

		woodIngredient = Ingredient.fromTag(ModTags.LOG);
		leafIngredient = Ingredient.fromTag(ModTags.LEAVES);
		saplingIngredient = Ingredient.fromTag(ModTags.SAPLING);

		registerComponent(inputInventory = new InventoryComponent("InputInventory", 10, MachineSideMode.Input).setFilter(new ItemStackHandlerFilter() {
			public boolean canInsertItem(int slot, ItemStack stack) {
				return slot == 0 ? stack.getItem() instanceof AxeItem : saplingIngredient.test(stack);
			}
		}));
		registerComponent(fluidContainerInventoy = new InventoryComponent("FluidContainerInventoy", 2, MachineSideMode.Never));
		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 9, MachineSideMode.Output));
		registerComponent(batteryInventory = new InventoryComponent("BatteryInventory", 1, MachineSideMode.Never));
		registerComponent(upgradesInventory = new InventoryComponent("UpgradeInventory", 3, MachineSideMode.Never));
		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 100, MachineSideMode.Never));
		registerComponent(processingComponent = new MachineProcessingComponent("ProcessingComponent", 5, this::canProcess, this::canProcess, this::processingCompleted, true));
		registerComponent(fluidTankComponent = new FluidTankComponent("FluidTank", 5000, (fluid) -> fluid.getFluid() == Fluids.WATER).setCapabilityExposedModes(MachineSideMode.Input));

		currentBlockIndex = 0;
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
		if (processingComponent.isProcessing()) {
			if (!getWorld().isRemote) {
				energyStorage.getStorage().extractEnergy(DEFAULT_IDLE_ENERGY_USAGE, false);
				fluidTankComponent.drain(DEFAULT_WATER_USAGE, FluidAction.EXECUTE);
			}
		}
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
		}

		// Sync the tile entity.
		markTileEntityForSynchronization();

		// Return true if we finished clearing the internal inventory.
		return InventoryUtilities.isInventoryEmpty(internalInventory);
	}

	private void refreshBlocksInRange(int range) {
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
		return inputInventory.getStackInSlot(0).getItem() instanceof AxeItem;
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
				getWorld().playSound(null, pos, getWorld().getBlockState(pos).getBlock().getSoundType(getWorld().getBlockState(pos), world, pos, null).getBreakSound(), SoundCategory.BLOCKS, 0.5F, 1.0F);
				List<ItemStack> harvestResults = new LinkedList<ItemStack>();
				harvestBlock(pos, harvestResults, 0);
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
		if (block != Blocks.AIR && !block.hasTileEntity(getWorld().getBlockState(pos)) && block.getBlockHardness(getWorld().getBlockState(pos), getWorld(), pos) != -1) {
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
						break;
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
		if (shouldDraw) {
			// Set the scale equal to the range * 2 plus 1.
			Vector3f scale = new Vector3f((range * 2) + 1, 1.0f, (range * 2) + 1);
			// Shift over so we center the range around the farmer.
			Vector3f position = new Vector3f(getTileEntity().getPos().getX(), getTileEntity().getPos().getY(), getTileEntity().getPos().getZ());
			Vec3i offsetDirection = this.getFacingDirection().getOpposite().getDirectionVec();
			position.add(new Vector3f(offsetDirection.getX() * 3 - range, 0.0f, offsetDirection.getZ() * 3 - range));

			// Add the entry.
			CustomRenderer.addCubeRenderer(getTileEntity(), "range", position, scale, new Color(0.1f, 1.0f, 0.2f, 0.25f));
		} else {
			// Remove the entry.
			CustomRenderer.removeCubeRenderer(getTileEntity(), "range");
		}
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
