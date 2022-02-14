package theking530.staticpower.tileentities.powered.basicfarmer;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import com.mojang.math.Vector3f;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AttachedStemBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.MelonBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.PumpkinBlock;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.tileentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.SDMath;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.client.rendering.CustomRenderer;
import theking530.staticpower.client.rendering.tileentity.TileEntityRenderFarmer;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerRecipeRegistry;
import theking530.staticpower.data.crafting.wrappers.fertilization.FertalizerRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModTags;
import theking530.staticpower.items.upgrades.BaseRangeUpgrade;
import theking530.staticpower.tileentities.TileEntityMachine;
import theking530.staticpower.tileentities.components.control.AbstractProcesingComponent.ProcessingCheckState;
import theking530.staticpower.tileentities.components.control.MachineProcessingComponent;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.components.fluids.FluidTankComponent;
import theking530.staticpower.tileentities.components.items.BatteryInventoryComponent;
import theking530.staticpower.tileentities.components.items.FluidContainerInventoryComponent;
import theking530.staticpower.tileentities.components.items.InputServoComponent;
import theking530.staticpower.tileentities.components.items.InventoryComponent;
import theking530.staticpower.tileentities.components.items.InventoryComponent.InventoryChangeType;
import theking530.staticpower.tileentities.components.items.ItemStackHandlerFilter;
import theking530.staticpower.tileentities.components.items.OutputServoComponent;
import theking530.staticpower.tileentities.components.items.UpgradeInventoryComponent;
import theking530.staticpower.utilities.InventoryUtilities;
import theking530.staticpower.utilities.WorldUtilities;

public class TileEntityBasicFarmer extends TileEntityMachine {
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityBasicFarmer> TYPE = new BlockEntityTypeAllocator<TileEntityBasicFarmer>((allocator, pos, state) -> new TileEntityBasicFarmer(pos, state),
			ModBlocks.BasicFarmer);

	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setTileEntitySpecialRenderer(TileEntityRenderFarmer::new);
		}
	}

	public final InventoryComponent inputInventory;
	public final InventoryComponent outputInventory;
	public final InventoryComponent internalInventory;
	public final FluidContainerInventoryComponent fluidContainerComponent;
	public final BatteryInventoryComponent batteryInventory;
	public final UpgradeInventoryComponent upgradesInventory;
	public final MachineProcessingComponent processingComponent;
	public final FluidTankComponent fluidTankComponent;

	private final HashSet<Class<? extends Block>> validHarvestacbleClasses;

	private final List<BlockPos> blocks;
	private int currentBlockIndex;
	private int range;
	private boolean shouldDrawRadiusPreview;

	public TileEntityBasicFarmer(BlockPos pos, BlockState state) {
		super(TYPE, pos, state, StaticPowerTiers.STATIC);
		disableFaceInteraction();

		registerComponent(inputInventory = new InventoryComponent("InputInventory", 2, MachineSideMode.Input).setFilter(new ItemStackHandlerFilter() {
			public boolean canInsertItem(int slot, ItemStack stack) {
				if (slot == 0) {
					return ModTags.FARMING_HOE.contains(stack.getItem());
				} else {
					return ModTags.FARMING_AXE.contains(stack.getItem());
				}
			}
		}).setSlotsLockable(true));

		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 9, MachineSideMode.Output));
		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 128));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", energyStorage.getStorage()));
		registerComponent(
				upgradesInventory = (UpgradeInventoryComponent) new UpgradeInventoryComponent("UpgradeInventory", 3).setModifiedCallback(this::onUpgradesInventoryModifiedCallback));

		registerComponent(processingComponent = new MachineProcessingComponent("ProcessingComponent", StaticPowerConfig.SERVER.basicFarmerProcessingTime.get(), this::canFarm, this::canFarm,
				this::processingCompleted, true).setUpgradeInventory(upgradesInventory).setRedstoneControlComponent(redstoneControlComponent).setEnergyComponent(energyStorage)
						.setProcessingPowerUsage(StaticPowerConfig.SERVER.basicFarmerPowerUsage.get()).setCompletedPowerUsage(StaticPowerConfig.SERVER.basicFarmerHarvestPowerUsage.get()));
		registerComponent(fluidTankComponent = new FluidTankComponent("FluidTank", 5000, (fluid) -> {
			return StaticPowerRecipeRegistry.getRecipe(FertalizerRecipe.RECIPE_TYPE, new RecipeMatchParameters(fluid)).isPresent();
		}));

		fluidTankComponent.setCapabilityExposedModes(MachineSideMode.Input);
		fluidTankComponent.setUpgradeInventory(upgradesInventory);
		fluidTankComponent.setAutoSyncPacketsEnabled(true);

		registerComponent(new InputServoComponent("InputServo", 2, inputInventory, 0));
		registerComponent(new OutputServoComponent("OutputServo", 1, outputInventory, 0, 1, 2, 3, 4, 5, 6, 7, 8));
		registerComponent(fluidContainerComponent = new FluidContainerInventoryComponent("FluidContainerServo", fluidTankComponent));

		// Set the energy storage upgrade inventory.
		energyStorage.setUpgradeInventory(upgradesInventory);

		// Capture all the harvestable blocks.
		validHarvestacbleClasses = new HashSet<Class<? extends Block>>();
		validHarvestacbleClasses.add(CropBlock.class);
		validHarvestacbleClasses.add(SugarCaneBlock.class);
		validHarvestacbleClasses.add(CactusBlock.class);
		validHarvestacbleClasses.add(NetherWartBlock.class);
		validHarvestacbleClasses.add(MelonBlock.class);
		validHarvestacbleClasses.add(PumpkinBlock.class);
		validHarvestacbleClasses.add(AttachedStemBlock.class);

		range = StaticPowerConfig.SERVER.basicFarmerDefaultRange.get();
		blocks = new LinkedList<BlockPos>();
		shouldDrawRadiusPreview = false;
	}

	@Override
	public void process() {
		if (processingComponent.isPerformingWork()) {
			if (!getLevel().isClientSide) {
				fluidTankComponent.drain(StaticPowerConfig.SERVER.basicFarmerFluidUsage.get(), FluidAction.EXECUTE);

				for (BlockPos blockpos : blocks) {
					BlockPos farmlandPos = blockpos.relative(Direction.DOWN);
					if (getLevel().getBlockState(farmlandPos).getBlock() == Blocks.FARMLAND) {
						getLevel().setBlock(farmlandPos, getLevel().getBlockState(farmlandPos).setValue(FarmBlock.MOISTURE, Integer.valueOf(7)), 2 | 16);
					}
				}
			}
		}
	}

	protected ProcessingCheckState processingCompleted() {
		// Edge case where we somehow need to refresh blocks.
		if (blocks.size() == 0) {
			refreshBlocksInRange(range);
		}

		boolean harvested = false;
		if (InventoryUtilities.isInventoryEmpty(internalInventory)) {
			// Harvest the current block.
			harvested = attemptHarvestPosition(getCurrentPosition());
			// Increment first to ensure we're always harvesting the next block.
			incrementPosition();
		}

		// For each of the farmed stacks, place the harvested stacks into the output
		// inventory. Remove the entry from the farmed stacks if it was fully inserted.
		// Otherwise, update the farmed stack.

		for (int i = 0; i < internalInventory.getSlots(); i++) {
			ItemStack extractedStack = internalInventory.extractItem(i, Integer.MAX_VALUE, false);
			ItemStack insertedStack = InventoryUtilities.insertItemIntoInventory(outputInventory, extractedStack, false);
			if (!insertedStack.isEmpty()) {
				internalInventory.setStackInSlot(i, insertedStack);
			}
		}

		// Return true if we finished clearing the internal inventory.
		if (InventoryUtilities.isInventoryEmpty(internalInventory)) {
			if (harvested) {
				return ProcessingCheckState.ok();
			} else {
				return ProcessingCheckState.cancel();
			}
		} else {
			return ProcessingCheckState.internalInventoryNotEmpty();
		}
	}

	@Override
	public void deserializeUpdateNbt(CompoundTag nbt, boolean fromUpdate) {
		super.deserializeUpdateNbt(nbt, fromUpdate);
		currentBlockIndex = nbt.getInt("current_index");
		range = nbt.getInt("range");
	}

	@Override
	public CompoundTag serializeUpdateNbt(CompoundTag nbt, boolean fromUpdate) {
		super.serializeUpdateNbt(nbt, fromUpdate);
		nbt.putInt("current_index", currentBlockIndex);
		nbt.putInt("range", range);
		return nbt;
	}

	public int getRadius() {
		return range;
	}

	public float getGrowthBonus() {
		FertalizerRecipe recipe = StaticPowerRecipeRegistry.getRecipe(FertalizerRecipe.RECIPE_TYPE, new RecipeMatchParameters(this.fluidTankComponent.getFluid())).orElse(null);
		if (recipe != null) {
			return recipe.getFertalizationAmount();
		}
		return 0.0f;
	}

	public boolean getShouldDrawRadiusPreview() {
		return shouldDrawRadiusPreview;
	}

	public void setShouldDrawRadiusPreview(boolean shouldDraw) {
		if (shouldDraw) {
			// If we were already drawing, remove and re-do it.
			if (shouldDrawRadiusPreview) {
				CustomRenderer.removeCubeRenderer(this, "range");
			}
			// Set the scale equal to the range * 2 plus 1.
			Vector3f scale = new Vector3f((range * 2) + 1, 1.0f, (range * 2) + 1);
			// Shift over so we center the range around the farmer.
			Vector3f position = new Vector3f(getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ());
			position.add(new Vector3f(-range, 0.0f, -range));

			// Add the entry.
			CustomRenderer.addCubeRenderer(this, "range", position, scale, new Color(0.1f, 1.0f, 0.2f, 0.25f));
		} else {
			// Remove the entry.
			CustomRenderer.removeCubeRenderer(this, "range");
		}

		// Update the drawing value.
		shouldDrawRadiusPreview = shouldDraw;
	}

	private void refreshBlocksInRange(int range) {
		blocks.clear();
		for (BlockPos pos : BlockPos.betweenClosed(getBlockPos().offset(-range, 0, -range), getBlockPos().offset(range, 0, range))) {
			if (pos != getBlockPos()) {
				blocks.add(pos.immutable());
			}
		}
		blocks.add(getBlockPos().offset(range, 0, range));

		if (currentBlockIndex > blocks.size() - 1) {
			currentBlockIndex = 0;
		}
	}

	private void incrementPosition() {
		if (!getLevel().isClientSide) {
			currentBlockIndex = Math.floorMod(currentBlockIndex + 1, blocks.size() - 1);
		}
	}

	public BlockPos getCurrentPosition() {
		return blocks.get(currentBlockIndex);
	}

	public ProcessingCheckState canFarm() {
		if (!hasAxe()) {
			return ProcessingCheckState.error("Missing Axe!");
		}
		if (!hasHoe()) {
			return ProcessingCheckState.error("Missing Hoe!");
		}
		if (fluidTankComponent.getFluid().getAmount() < StaticPowerConfig.SERVER.basicFarmerFluidUsage.get()) {
			return ProcessingCheckState.notEnoughFluid();
		}
		return ProcessingCheckState.ok();
	}

	public boolean hasHoe() {
		return ModTags.FARMING_HOE.contains(inputInventory.getStackInSlot(0).getItem());
	}

	public boolean hasAxe() {
		return ModTags.FARMING_AXE.contains(inputInventory.getStackInSlot(1).getItem());
	}

	public void useHoe() {
		// If we have an hoe, and we're on the server, use it.
		if (hasHoe() && !getLevel().isClientSide) {
			if (inputInventory.getStackInSlot(0).hurt(StaticPowerConfig.SERVER.basicFarmerToolUsage.get(), getLevel().random, null)) {
				inputInventory.getStackInSlot(1).shrink(1);
				getLevel().playSound(null, worldPosition, SoundEvents.ITEM_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
			}
		}
	}

	public void useAxe() {
		// If we have an axe, and we're on the server, use it.
		if (hasAxe() && !getLevel().isClientSide) {
			if (inputInventory.getStackInSlot(1).hurt(StaticPowerConfig.SERVER.basicFarmerToolUsage.get(), getLevel().random, null)) {
				inputInventory.getStackInSlot(1).shrink(1);
				getLevel().playSound(null, worldPosition, SoundEvents.ITEM_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
			}
		}
	}

	public boolean attemptHarvestPosition(BlockPos pos) {
		if (getLevel().isClientSide) {
			return false;
		}

		if (getLevel().getBlockState(pos.relative(Direction.DOWN)).getBlock() == Blocks.DIRT || getLevel().getBlockState(pos.relative(Direction.DOWN)).getBlock() == Blocks.GRASS_BLOCK) {
			getLevel().setBlock(pos.relative(Direction.DOWN), Blocks.FARMLAND.defaultBlockState(), 1 | 2);
			getLevel().playSound(null, pos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);

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

		// Grow the crop if we can.
		if (SDMath.diceRoll(getGrowthBonus())) {
			growCrop(pos);
		}

		return farmed;
	}

	public boolean isFarmableBlock(BlockPos pos) {
		if (getLevel().getBlockState(pos) == null) {
			return false;
		}
		for (Class<? extends Block> harvestableClass : validHarvestacbleClasses) {
			if (harvestableClass.isInstance(getLevel().getBlockState(pos).getBlock())) {
				return true;
			}
		}
		return false;
	}

	protected void captureHarvestItems(BlockPos pos) {
		for (ItemStack drop : WorldUtilities.getBlockDrops(getLevel(), pos)) {
			InventoryUtilities.insertItemIntoInventory(internalInventory, drop, false);
		}
		getLevel().playSound(null, pos, getLevel().getBlockState(pos).getBlock().getSoundType(getLevel().getBlockState(pos), level, pos, null).getBreakSound(), SoundSource.BLOCKS, 1.0F,
				1.0F);
		((ServerLevel) getLevel()).sendParticles(ParticleTypes.LARGE_SMOKE, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, 1, 0.0D, 0.0D, 0.0D, 0.0D);
	}

	public boolean harvestGenericCrop(BlockPos pos) {
		// If the current position is an instance of a CropsBlock.
		if (getLevel().getBlockState(pos).getBlock() instanceof CropBlock) {
			// Get the block and check if it is of max age.
			CropBlock tempCrop = (CropBlock) getLevel().getBlockState(pos).getBlock();
			// If the crop is fully grown, harvest it.
			if (tempCrop.isMaxAge(getLevel().getBlockState(pos))) {
				// Harvest the provided position, set the age of the crop back to 0, and use the
				// hoe.
				captureHarvestItems(pos);
				getLevel().setBlock(pos, tempCrop.getStateForAge(0), 1 | 2);
				useHoe();
				return true;
			}
		}
		return false;
	}

	public boolean harvestSugarCane(BlockPos pos) {
		boolean harvested = false;
		for (int i = 1; i < 255; i++) {
			if (getLevel().getBlockState(pos.offset(0, i, 0)).getBlock() instanceof SugarCaneBlock) {
				captureHarvestItems(pos.offset(0, i, 0));
				getLevel().setBlock(pos.offset(0, i, 0), Blocks.AIR.defaultBlockState(), 1 | 2);
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
			if (getLevel().getBlockState(pos.offset(0, i, 0)).getBlock() instanceof CactusBlock) {
				captureHarvestItems(pos.offset(0, i, 0));
				getLevel().setBlock(pos.offset(0, i, 0), Blocks.AIR.defaultBlockState(), 1 | 2);
				useAxe();
				harvested = true;
			} else {
				break;
			}
		}

		return harvested;
	}

	public boolean harvestStem(BlockPos pos) {
		Block block = getLevel().getBlockState(pos).getBlock();
		if (block instanceof AttachedStemBlock) {
			// Check for the melon or pumpkin around the stem.
			for (Direction dir : Direction.values()) {
				if (dir.getAxis() == Direction.Axis.Y) {
					continue;
				}
				if (harvestMelonOrPumpkin(pos.relative(dir))) {
					return true;
				}

			}
		}
		return false;
	}

	public boolean harvestMelonOrPumpkin(BlockPos pos) {
		Block block = getLevel().getBlockState(pos).getBlock();
		if (block instanceof MelonBlock || block instanceof PumpkinBlock) {
			captureHarvestItems(pos);
			getLevel().setBlock(pos, Blocks.AIR.defaultBlockState(), 1 | 2);
			useAxe();
			return true;
		}
		return false;
	}

	public boolean harvestNetherWart(BlockPos pos) {
		if (getLevel().getBlockState(pos).getBlock() instanceof NetherWartBlock) {
			NetherWartBlock tempNetherwart = (NetherWartBlock) getLevel().getBlockState(pos).getBlock();
			if (tempNetherwart.getPlant(getLevel(), pos).getValue(NetherWartBlock.AGE) >= 3) {
				captureHarvestItems(pos);
				getLevel().setBlock(pos, Blocks.NETHER_WART.defaultBlockState(), 1 | 2);
				useHoe();
			}
			return true;
		}
		return false;
	}

	public boolean growCrop(BlockPos pos) {
		for (int i = 0; i < getGrowthBonus() / 100; i++) {
			if (getLevel().random.nextInt(100) < getGrowthBonus()) {
				if (getLevel().getBlockState(pos) != null && getLevel().getBlockState(pos).getBlock() instanceof BonemealableBlock) {
					BonemealableBlock tempCrop = (BonemealableBlock) getLevel().getBlockState(pos).getBlock();
					if (tempCrop.isValidBonemealTarget(getLevel(), pos, getLevel().getBlockState(pos), false)) {
						tempCrop.performBonemeal((ServerLevel) getLevel(), getLevel().random, pos, getLevel().getBlockState(pos));
						((ServerLevel) getLevel()).sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, 1, 0.0D, 0.0D, 0.0D, 0.0D);
					}
				}
			}
		}

		return true;
	}

	public void onUpgradesInventoryModifiedCallback(InventoryChangeType changeType, ItemStack item, int slot) {
		range = StaticPowerConfig.SERVER.basicFarmerDefaultRange.get();
		for (ItemStack stack : upgradesInventory) {
			if (stack.getItem() instanceof BaseRangeUpgrade) {
				range = (int) Math.max(range, StaticPowerConfig.SERVER.basicFarmerDefaultRange.get() * (((BaseRangeUpgrade) stack.getItem()).getTier().rangeUpgrade.get()));
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
		return new ContainerBasicFarmer(windowId, inventory, this);
	}
}
