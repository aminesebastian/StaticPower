package theking530.staticpower.tileentities.powered.basicfarmer;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.AttachedStemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.CactusBlock;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.block.IGrowable;
import net.minecraft.block.MelonBlock;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.block.PumpkinBlock;
import net.minecraft.block.SugarCaneBlock;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.AxeItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import theking530.common.utilities.Color;
import theking530.staticpower.client.rendering.CustomRenderer;
import theking530.staticpower.initialization.ModTileEntityTypes;
import theking530.staticpower.items.upgrades.BaseRangeUpgrade;
import theking530.staticpower.tileentities.TileEntityMachine;
import theking530.staticpower.tileentities.components.BatteryComponent;
import theking530.staticpower.tileentities.components.FluidContainerComponent;
import theking530.staticpower.tileentities.components.FluidTankComponent;
import theking530.staticpower.tileentities.components.InputServoComponent;
import theking530.staticpower.tileentities.components.InventoryComponent;
import theking530.staticpower.tileentities.components.InventoryComponent.InventoryChangeType;
import theking530.staticpower.tileentities.components.MachineProcessingComponent;
import theking530.staticpower.tileentities.components.OutputServoComponent;
import theking530.staticpower.tileentities.utilities.MachineSideMode;
import theking530.staticpower.tileentities.utilities.interfaces.ItemStackHandlerFilter;
import theking530.staticpower.utilities.InventoryUtilities;
import theking530.staticpower.utilities.WorldUtilities;

public class TileEntityBasicFarmer extends TileEntityMachine {
	public static final int DEFAULT_WATER_USAGE = 1;
	public static final int DEFAULT_IDLE_ENERGY_USAGE = 20;
	public static final int DEFAULT_HARVEST_ENERGY_COST = 1000;
	public static final int DEFAULT_RANGE = 2;
	public static final int DEFAULT_TOOL_USAGE = 1;
	public static final Random RANDOM = new Random();

	public final InventoryComponent inputInventory;
	public final InventoryComponent outputInventory;
	public final InventoryComponent internalInventory;
	public final InventoryComponent fluidContainerInventory;
	public final InventoryComponent batteryInventory;
	public final InventoryComponent upgradesInventory;
	public final MachineProcessingComponent processingComponent;
	public final FluidTankComponent fluidTankComponent;

	private final HashSet<Class<? extends Block>> validHarvestacbleClasses;

	private final List<BlockPos> blocks;
	private int currentBlockIndex;
	private int range;
	private int growthBonusChance;
	private boolean shouldDrawRadiusPreview;

	public TileEntityBasicFarmer() {
		super(ModTileEntityTypes.BASIC_FARMER);
		disableFaceInteraction();
		registerComponent(inputInventory = new InventoryComponent("InputInventory", 2, MachineSideMode.Input).setFilter(new ItemStackHandlerFilter() {
			public boolean canInsertItem(int slot, ItemStack stack) {
				return !stack.isEmpty() && (stack.getItem() instanceof AxeItem || stack.getItem() instanceof HoeItem);
			}
		}));
		registerComponent(fluidContainerInventory = new InventoryComponent("FluidContainerInventory", 2, MachineSideMode.Never));
		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 9, MachineSideMode.Output));
		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 128, MachineSideMode.Never));
		registerComponent(batteryInventory = new InventoryComponent("BatteryInventory", 1, MachineSideMode.Never));
		registerComponent(upgradesInventory = new InventoryComponent("UpgradeInventory", 3, MachineSideMode.Never).setModifiedCallback(this::onUpgradesInventoryModifiedCallback));

		registerComponent(processingComponent = new MachineProcessingComponent("ProcessingComponent", 5, this::canFarm, this::canFarm, this::processingCompleted, true));
		registerComponent(fluidTankComponent = new FluidTankComponent("FluidTank", 5000, (fluid) -> fluid.getFluid() == Fluids.WATER).setCapabilityExposedModes(MachineSideMode.Input));

		registerComponent(new InputServoComponent("InputServo", 2, inputInventory, 0));
		registerComponent(new OutputServoComponent("OutputServo", 1, outputInventory, 0, 1, 2, 3, 4, 5, 6, 7, 8));
		registerComponent(new FluidContainerComponent("FluidContainerServo", fluidTankComponent, fluidContainerInventory, 0, 1));
		registerComponent(new BatteryComponent("BatteryComponent", batteryInventory, 0, energyStorage.getStorage()));

		// Capture all the harvestable blocks.
		validHarvestacbleClasses = new HashSet<Class<? extends Block>>();
		validHarvestacbleClasses.add(CropsBlock.class);
		validHarvestacbleClasses.add(SugarCaneBlock.class);
		validHarvestacbleClasses.add(CactusBlock.class);
		validHarvestacbleClasses.add(NetherWartBlock.class);
		validHarvestacbleClasses.add(MelonBlock.class);
		validHarvestacbleClasses.add(PumpkinBlock.class);
		validHarvestacbleClasses.add(AttachedStemBlock.class);

		range = DEFAULT_RANGE;
		blocks = new LinkedList<BlockPos>();
		shouldDrawRadiusPreview = false;
		growthBonusChance = 0;
	}

	@Override
	public void process() {
		if (processingComponent.isPerformingWork()) {
			if (!getWorld().isRemote) {
				energyStorage.usePower(DEFAULT_IDLE_ENERGY_USAGE);
				fluidTankComponent.drain(DEFAULT_WATER_USAGE, FluidAction.EXECUTE);

				for (BlockPos blockpos : blocks) {
					BlockPos farmlandPos = blockpos.offset(Direction.DOWN);
					if (getWorld().getBlockState(farmlandPos).getBlock() == Blocks.FARMLAND) {
						getWorld().setBlockState(farmlandPos, getWorld().getBlockState(farmlandPos).with(FarmlandBlock.MOISTURE, Integer.valueOf(7)), 2);
					}
				}
			}
		}
	}

	protected boolean processingCompleted() {
		// Edge case where we somehow need to refresh blocks.
		if (blocks.size() == 0) {
			refreshBlocksInRange(range);
		}

		if (InventoryUtilities.isInventoryEmpty(internalInventory)) {
			// Harvest the current block.
			attemptHarvestPosition(getCurrentPosition());

			// If on the server, use the amount of energy required to harvest a plant.
			if (!world.isRemote) {
				energyStorage.usePower(DEFAULT_HARVEST_ENERGY_COST);
			}

			// Increment first to ensure we're always harvesting the next block.
			incrementPosition();

		}

		// For each of the farmed stacks, place the harvested stacks into the output
		// inventory. Remove the entry from the farmed stacks if it was fully inserted.
		// Otherwise, update the farmed stack.
		if (!getWorld().isRemote) {
			for (int i = 0; i < internalInventory.getSlots(); i++) {
				ItemStack extractedStack = internalInventory.extractItem(i, Integer.MAX_VALUE, false);
				ItemStack insertedStack = InventoryUtilities.insertItemIntoInventory(outputInventory, extractedStack, false);
				if (!insertedStack.isEmpty()) {
					internalInventory.setStackInSlot(i, insertedStack);
				}
			}
		}

		// Return true if we finished clearing the internal inventory.
		return InventoryUtilities.isInventoryEmpty(internalInventory);
	}

	@Override
	public void deserializeUpdateNbt(CompoundNBT nbt, boolean fromUpdate) {
		super.deserializeUpdateNbt(nbt, fromUpdate);
		currentBlockIndex = nbt.getInt("current_index");
		range = nbt.getInt("range");
	}

	@Override
	public CompoundNBT serializeUpdateNbt(CompoundNBT nbt, boolean fromUpdate) {
		super.serializeUpdateNbt(nbt, fromUpdate);
		nbt.putInt("current_index", currentBlockIndex);
		nbt.putInt("range", range);
		return nbt;
	}

	public int getRadius() {
		return range;
	}

	public int getGrowthBonus() {
		return growthBonusChance;
	}

	public boolean getShouldDrawRadiusPreview() {
		return shouldDrawRadiusPreview;
	}

	public void setShouldDrawRadiusPreview(boolean shouldDraw) {
		if (shouldDraw) {
			// If we were already drawing, remove and re-do it.
			if (shouldDrawRadiusPreview) {
				CustomRenderer.removeCubeRenderer(getTileEntity(), "range");
			}
			// Set the scale equal to the range * 2 plus 1.
			Vector3f scale = new Vector3f((range * 2) + 1, 1.0f, (range * 2) + 1);
			// Shift over so we center the range around the farmer.
			Vector3f position = new Vector3f(getTileEntity().getPos().getX(), getTileEntity().getPos().getY(), getTileEntity().getPos().getZ());
			position.add(new Vector3f(-range, 0.0f, -range));

			// Add the entry.
			CustomRenderer.addCubeRenderer(getTileEntity(), "range", position, scale, new Color(0.1f, 1.0f, 0.2f, 0.25f));
		} else {
			// Remove the entry.
			CustomRenderer.removeCubeRenderer(getTileEntity(), "range");
		}

		// Update the drawing value.
		shouldDrawRadiusPreview = shouldDraw;
	}

	private void refreshBlocksInRange(int range) {
		blocks.clear();
		for (BlockPos pos : BlockPos.getAllInBoxMutable(getPos().add(-range, 0, -range), getPos().add(range, 0, range))) {
			if (pos != getPos()) {
				blocks.add(pos.toImmutable());
			}
		}
		blocks.add(getPos().add(range, 0, range));

		if (currentBlockIndex > blocks.size() - 1) {
			currentBlockIndex = 0;
		}
	}

	private void incrementPosition() {
		if (!getWorld().isRemote) {
			currentBlockIndex = Math.floorMod(currentBlockIndex + 1, blocks.size() - 1);
		}
	}

	public BlockPos getCurrentPosition() {
		return blocks.get(currentBlockIndex);
	}

	public boolean canFarm() {
		// Check to see if we have enough power and if we have the axe and hoe
		// populated.
		if (energyStorage.hasEnoughPower(DEFAULT_HARVEST_ENERGY_COST) && hasHoe() && hasAxe()) {
			// If we have enough fluid, return true.
			return fluidTankComponent.getFluid().getAmount() > DEFAULT_WATER_USAGE && fluidTankComponent.getFluid().getFluid() == Fluids.WATER;
		}
		return false;
	}

	public boolean hasHoe() {
		return inputInventory.getStackInSlot(0).getItem() instanceof HoeItem;
	}

	public boolean hasAxe() {
		return inputInventory.getStackInSlot(1).getItem() instanceof AxeItem;
	}

	public void useHoe() {
		if (inputInventory.getStackInSlot(0) != ItemStack.EMPTY && inputInventory.getStackInSlot(0).getItem() instanceof HoeItem) {
			if (inputInventory.getStackInSlot(0).attemptDamageItem(DEFAULT_TOOL_USAGE, RANDOM, null)) {
				inputInventory.setStackInSlot(0, ItemStack.EMPTY);
				getWorld().playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
			}
		}
	}

	public void useAxe() {
		if (inputInventory.getStackInSlot(1) != ItemStack.EMPTY && inputInventory.getStackInSlot(1).getItem() instanceof AxeItem) {
			if (inputInventory.getStackInSlot(1).attemptDamageItem(DEFAULT_TOOL_USAGE, RANDOM, null)) {
				inputInventory.setStackInSlot(1, ItemStack.EMPTY);
				getWorld().playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
			}
		}
	}

	public boolean attemptHarvestPosition(BlockPos pos) {
		if (getWorld().isRemote) {
			return false;
		}

		if (getWorld().getBlockState(pos.offset(Direction.DOWN)).getBlock() == Blocks.DIRT || getWorld().getBlockState(pos.offset(Direction.DOWN)).getBlock() == Blocks.GRASS_BLOCK) {
			getWorld().setBlockState(pos.offset(Direction.DOWN), Blocks.FARMLAND.getDefaultState(), 1 | 2);
			getWorld().playSound(null, pos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);

		}

		boolean farmed = false;

		// Check to see if we're at a farmable block. If we are, harvest it.
		if (isFarmableBlock(pos)) {
			farmed |= harvestGenericCrop(pos);
			farmed |= harvestSugarCane(pos);
			farmed |= harvestCactus(pos);
			farmed |= harvestStem(pos);
			farmed |= harvestNetherWart(pos);
			farmed |= harvestMelonOrPumpkin(pos);
		}
		growCrop(pos);
		return farmed;
	}

	public boolean isFarmableBlock(BlockPos pos) {
		if (getWorld().getBlockState(pos) == null) {
			return false;
		}
		for (Class<? extends Block> harvestableClass : validHarvestacbleClasses) {
			if (harvestableClass.isInstance(getWorld().getBlockState(pos).getBlock())) {
				return true;
			}
		}
		return false;
	}

	protected void captureHarvestItems(BlockPos pos) {
		for (ItemStack drop : WorldUtilities.getBlockDrops(getWorld(), pos)) {
			InventoryUtilities.insertItemIntoInventory(internalInventory, drop, false);
		}
		getWorld().playSound(null, pos, getWorld().getBlockState(pos).getBlock().getSoundType(getWorld().getBlockState(pos), world, pos, null).getBreakSound(), SoundCategory.BLOCKS, 1.0F, 1.0F);
		((ServerWorld) getWorld()).spawnParticle(ParticleTypes.LARGE_SMOKE, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, 1, 0.0D, 0.0D, 0.0D, 0.0D);
	}

	public boolean harvestGenericCrop(BlockPos pos) {
		// If the current position is an instance of a CropsBlock.
		if (getWorld().getBlockState(pos).getBlock() instanceof CropsBlock) {
			// Get the block and check if it is of max age.
			CropsBlock tempCrop = (CropsBlock) getWorld().getBlockState(pos).getBlock();
			// If the crop is fully grown, harvest it.
			if (tempCrop.isMaxAge(getWorld().getBlockState(pos))) {
				// Harvest the provided position, set the age of the crop back to 0, and use the
				// hoe.
				captureHarvestItems(pos);
				getWorld().setBlockState(pos, tempCrop.withAge(0), 1 | 2);
				useHoe();
				return true;
			}
		}
		return false;
	}

	public boolean harvestSugarCane(BlockPos pos) {
		boolean harvested = false;
		for (int i = 1; i < 255; i++) {
			if (getWorld().getBlockState(pos.add(0, i, 0)).getBlock() instanceof SugarCaneBlock) {
				captureHarvestItems(pos.add(0, i, 0));
				getWorld().setBlockState(pos.add(0, i, 0), Blocks.AIR.getDefaultState(), 1 | 2);
				useAxe();
				harvested = true;
			} else {
				break;
			}
		}

		return harvested;
	}

	public boolean harvestCactus(BlockPos pos) {
		boolean harvested = false;
		for (int i = 1; i < 255; i++) {
			if (getWorld().getBlockState(pos.add(0, i, 0)).getBlock() instanceof CactusBlock) {
				captureHarvestItems(pos.add(0, i, 0));
				getWorld().setBlockState(pos.add(0, i, 0), Blocks.AIR.getDefaultState(), 1 | 2);
				useAxe();
				harvested = true;
			} else {
				break;
			}
		}

		return harvested;
	}

	public boolean harvestStem(BlockPos pos) {
		Block block = getWorld().getBlockState(pos).getBlock();
		if (block instanceof AttachedStemBlock) {
			// Check for the melon or pumpkin around the stem.
			for (Direction dir : Direction.values()) {
				if (dir.getAxis() == Direction.Axis.Y) {
					continue;
				}
				if (harvestMelonOrPumpkin(pos.offset(dir))) {
					return true;
				}

			}
		}
		return false;
	}

	public boolean harvestMelonOrPumpkin(BlockPos pos) {
		Block block = getWorld().getBlockState(pos).getBlock();
		if (block instanceof MelonBlock || block instanceof PumpkinBlock) {
			captureHarvestItems(pos);
			getWorld().setBlockState(pos, Blocks.AIR.getDefaultState(), 1 | 2);
			useAxe();
			return true;
		}
		return false;
	}

	public boolean harvestNetherWart(BlockPos pos) {
		if (getWorld().getBlockState(pos).getBlock() instanceof NetherWartBlock) {
			NetherWartBlock tempNetherwart = (NetherWartBlock) getWorld().getBlockState(pos).getBlock();
			if (tempNetherwart.getPlant(getWorld(), pos).get(NetherWartBlock.AGE) >= 3) {
				captureHarvestItems(pos);
				getWorld().setBlockState(pos, Blocks.NETHER_WART.getDefaultState(), 1 | 2);
				useHoe();
			}
			return true;
		}
		return false;
	}

	public boolean growCrop(BlockPos pos) {
		for (int i = 0; i < getGrowthBonus() / 100; i++) {
			if (RANDOM.nextInt(100) < getGrowthBonus()) {
				if (getWorld().getBlockState(pos) != null && getWorld().getBlockState(pos).getBlock() instanceof IGrowable) {
					IGrowable tempCrop = (IGrowable) getWorld().getBlockState(pos).getBlock();
					if (tempCrop.canGrow(getWorld(), pos, getWorld().getBlockState(pos), false)) {
						tempCrop.grow((ServerWorld) getWorld(), RANDOM, pos, getWorld().getBlockState(pos));
						getWorld().notifyBlockUpdate(pos, getWorld().getBlockState(pos), getWorld().getBlockState(pos), 1 | 2);
						((ServerWorld) getWorld()).spawnParticle(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, 1, 0.0D, 0.0D, 0.0D, 0.0D);
					}
				}
			}
		}

		return true;
	}

	public void onUpgradesInventoryModifiedCallback(InventoryChangeType changeType, ItemStack item, InventoryComponent inventory) {
		range = DEFAULT_RANGE;
		for (ItemStack stack : inventory) {
			if (stack.getItem() instanceof BaseRangeUpgrade) {
				range = (int) Math.max(range, DEFAULT_RANGE * (((BaseRangeUpgrade) stack.getItem()).rangeUpgrade));
			}
		}
		refreshBlocksInRange(range);
		markTileEntityForSynchronization();

		// Refresh the preview if it is currently begin drawn.
		if (getShouldDrawRadiusPreview()) {
			setShouldDrawRadiusPreview(true);
		}
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerBasicFarmer(windowId, inventory, this);
	}
}
