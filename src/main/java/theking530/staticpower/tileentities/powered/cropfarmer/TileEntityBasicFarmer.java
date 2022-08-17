package theking530.staticpower.tileentities.powered.cropfarmer;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.mojang.math.Vector3f;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.FarmlandWaterManager;
import net.minecraftforge.common.ticket.AABBTicket;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.tileentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.SDMath;
import theking530.staticpower.StaticPower;
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
import theking530.staticpower.tileentities.components.serialization.UpdateSerialize;
import theking530.staticpower.tileentities.powered.cropfarmer.IFarmerHarvester.HarvestResult;
import theking530.staticpower.utilities.InventoryUtilities;

public class TileEntityBasicFarmer extends TileEntityMachine {
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityBasicFarmer> TYPE = new BlockEntityTypeAllocator<TileEntityBasicFarmer>(
			(allocator, pos, state) -> new TileEntityBasicFarmer(pos, state), ModBlocks.BasicFarmer.get());
	private static final Map<Class<?>, IFarmerHarvester> HARVETERS = new LinkedHashMap<Class<?>, IFarmerHarvester>();

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

	private final List<BlockPos> blocks;
	@UpdateSerialize
	private int currentBlockIndex;
	@UpdateSerialize
	private int range;
	private boolean shouldDrawRadiusPreview;
	private AABBTicket wateringTicket;

	public TileEntityBasicFarmer(BlockPos pos, BlockState state) {
		super(TYPE, pos, state, StaticPowerTiers.STATIC);

		registerComponent(inputInventory = new InventoryComponent("InputInventory", 2, MachineSideMode.Input)
				.setFilter(new ItemStackHandlerFilter() {
					public boolean canInsertItem(int slot, ItemStack stack) {
						if (slot == 0) {
							return ModTags.tagContainsItemStack(ModTags.FARMING_HOE, stack);
						} else {
							return ModTags.tagContainsItemStack(ModTags.FARMING_HOE, stack);
						}
					}
				}).setSlotsLockable(true));

		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 9, MachineSideMode.Output));
		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 128));
		registerComponent(
				batteryInventory = new BatteryInventoryComponent("BatteryComponent", energyStorage.getStorage()));
		registerComponent(
				upgradesInventory = (UpgradeInventoryComponent) new UpgradeInventoryComponent("UpgradeInventory", 3)
						.setModifiedCallback(this::onUpgradesInventoryModifiedCallback));

		registerComponent(processingComponent = new MachineProcessingComponent("ProcessingComponent",
				StaticPowerConfig.SERVER.basicFarmerProcessingTime.get(), this::canFarm, this::canFarm,
				this::processingCompleted, true).setUpgradeInventory(upgradesInventory)
				.setRedstoneControlComponent(redstoneControlComponent).setEnergyComponent(energyStorage)
				.setProcessingPowerUsage(StaticPowerConfig.SERVER.basicFarmerPowerUsage.get())
				.setCompletedPowerUsage(StaticPowerConfig.SERVER.basicFarmerHarvestPowerUsage.get()));
		registerComponent(fluidTankComponent = new FluidTankComponent("FluidTank", 5000, (fluid) -> {
			return StaticPowerRecipeRegistry.getRecipe(FertalizerRecipe.RECIPE_TYPE, new RecipeMatchParameters(fluid))
					.isPresent();
		}));

		fluidTankComponent.setCapabilityExposedModes(MachineSideMode.Input);
		fluidTankComponent.setUpgradeInventory(upgradesInventory);
		fluidTankComponent.setAutoSyncPacketsEnabled(true);

		registerComponent(new InputServoComponent("InputServo", inputInventory));
		registerComponent(new OutputServoComponent("OutputServo", outputInventory));
		registerComponent(fluidContainerComponent = new FluidContainerInventoryComponent("FluidContainerServo",
				fluidTankComponent));

		// Set the energy storage upgrade inventory.
		energyStorage.setUpgradeInventory(upgradesInventory);
		range = StaticPowerConfig.SERVER.basicFarmerDefaultRange.get();
		blocks = new LinkedList<BlockPos>();
		shouldDrawRadiusPreview = false;

	}

	public static <T extends IFarmerHarvester> void registerHarvester(T instance) {
		HARVETERS.put(instance.getClass(), instance);
	}

	public BlockPos getCurrentPosition() {
		return blocks.get(currentBlockIndex);
	}

	public boolean hasHoe() {
		return ModTags.tagContainsItem(ModTags.FARMING_HOE, inputInventory.getStackInSlot(0).getItem());
	}

	public boolean hasAxe() {
		return ModTags.tagContainsItem(ModTags.FARMING_HOE, inputInventory.getStackInSlot(1).getItem());
	}

	public int getRadius() {
		return range;
	}

	public float getGrowthBonus() {
		FertalizerRecipe recipe = StaticPowerRecipeRegistry
				.getRecipe(FertalizerRecipe.RECIPE_TYPE, new RecipeMatchParameters(this.fluidTankComponent.getFluid()))
				.orElse(null);
		if (recipe != null) {
			return recipe.getFertalizationAmount();
		}
		return 0.0f;
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

	@Override
	public void process() {
		if (processingComponent.isPerformingWork()) {
			if (!getLevel().isClientSide()) {
				// If we're processing but somehow the watering ticket is not valid, create one.
				if (wateringTicket == null || !wateringTicket.isValid()) {
					captureWateringTicket();
				}

				// Use fluid.
				fluidTankComponent.drain(StaticPowerConfig.SERVER.basicFarmerFluidUsage.get(), FluidAction.EXECUTE);
			}
		}

		// If we ahve no fluid, remove the watering ticket.
		if (this.fluidTankComponent.isEmpty()) {
			invalidateWateringTicket();
		}
	}

	@Override
	public void onChunkUnloaded() {
		super.onChunkUnloaded();
		invalidateWateringTicket();
	}

	protected void captureWateringTicket() {
		if (!getLevel().isClientSide()) {
			if (wateringTicket != null) {
				wateringTicket.invalidate();
			}

			AABB rangeBounds = new AABB(getBlockPos().getX() - range - 1, getBlockPos().getY() - 1,
					getBlockPos().getZ() - range - 1, getBlockPos().getX() + range + 1, getBlockPos().getY(),
					getBlockPos().getZ() + range + 1);
			wateringTicket = FarmlandWaterManager.addAABBTicket(getLevel(), rangeBounds);
			StaticPower.LOGGER.debug(String.format("Adding farmland watering ticket for farmer at position: %1$s.",
					getBlockPos().toString()));
		}
	}

	protected void invalidateWateringTicket() {
		if (!getLevel().isClientSide() && wateringTicket != null) {
			wateringTicket.invalidate();
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
			currentBlockIndex = Math.floorMod(currentBlockIndex + 1, blocks.size() - 1);
		}

		// For each of the farmed stacks, place the harvested stacks into the output
		// inventory. Remove the entry from the farmed stacks if it was fully inserted.
		// Otherwise, update the farmed stack.

		for (int i = 0; i < internalInventory.getSlots(); i++) {
			ItemStack extractedStack = internalInventory.extractItem(i, Integer.MAX_VALUE, false);
			ItemStack insertedStack = InventoryUtilities.insertItemIntoInventory(outputInventory, extractedStack,
					false);
			if (!insertedStack.isEmpty()) {
				internalInventory.setStackInSlot(i, insertedStack);
			}
		}

		// Return true if we finished clearing the internal inventory.
		if (InventoryUtilities.isInventoryEmpty(internalInventory)) {
			if (harvested) {
				((ServerLevel) getLevel()).sendParticles(ParticleTypes.FALLING_WATER,
						getCurrentPosition().getX() + 0.5D, getCurrentPosition().getY() + 1.0D,
						getCurrentPosition().getZ() + 0.5D, 1, 0.0D, 0.0D, 0.0D, 0.0D);
				return ProcessingCheckState.ok();
			} else {
				return ProcessingCheckState.cancel();
			}
		} else {
			return ProcessingCheckState.internalInventoryNotEmpty();
		}
	}

	protected ProcessingCheckState canFarm() {
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

	protected boolean getShouldDrawRadiusPreview() {
		return shouldDrawRadiusPreview;
	}

	private void refreshBlocksInRange(int range) {
		StaticPower.LOGGER.debug(
				String.format("Farmer at position: %1$s refershing eligible blocks..", getBlockPos().toString()));
		blocks.clear();
		for (BlockPos pos : BlockPos.betweenClosed(getBlockPos().offset(-range, 0, -range),
				getBlockPos().offset(range, 0, range))) {
			if (pos != getBlockPos()) {
				blocks.add(pos.immutable());
			}
		}
		blocks.add(getBlockPos().offset(range, 0, range));

		if (currentBlockIndex > blocks.size() - 1) {
			currentBlockIndex = 0;
		}

		// Update the watering ticket just in case.
		captureWateringTicket();
	}

	@SuppressWarnings("resource")
	protected void useHoe() {
		// If we have an hoe, and we're on the server, use it.
		if (hasHoe() && !getLevel().isClientSide) {
			if (inputInventory.getStackInSlot(0).hurt(StaticPowerConfig.SERVER.basicFarmerToolUsage.get(),
					getLevel().random, null)) {
				inputInventory.getStackInSlot(1).shrink(1);
				getLevel().playSound(null, worldPosition, SoundEvents.ITEM_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
			}
		}
	}

	@SuppressWarnings("resource")
	protected void useAxe() {
		// If we have an axe, and we're on the server, use it.
		if (hasAxe() && !getLevel().isClientSide) {
			if (inputInventory.getStackInSlot(1).hurt(StaticPowerConfig.SERVER.basicFarmerToolUsage.get(),
					getLevel().random, null)) {
				inputInventory.getStackInSlot(1).shrink(1);
				getLevel().playSound(null, worldPosition, SoundEvents.ITEM_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
			}
		}
	}

	protected boolean attemptHarvestPosition(BlockPos pos) {
		// Till the spot if we can and return.
		if (ModTags.tagContainsItem(ModTags.TILLABLE,
				getLevel().getBlockState(pos.relative(Direction.DOWN)).getBlock().asItem())) {
			getLevel().destroyBlock(pos.relative(Direction.DOWN), false);
			getLevel().setBlockAndUpdate(pos.relative(Direction.DOWN), Blocks.FARMLAND.defaultBlockState());
			getLevel().playSound(null, pos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
			return false;
		}

		boolean farmed = false;

		// Check to see if we're at a farmable block. If we are, harvest it.
		for (IFarmerHarvester harvester : HARVETERS.values()) {
			HarvestResult result = harvester.harvest(getLevel(), pos);
			if (!result.isEmpty()) {
				for (ItemStack stack : result.getResults()) {
					InventoryUtilities.insertItemIntoInventory(internalInventory, stack, false);
				}
				farmed = true;
				break;
			}
		}

		// Grow the crop if we can.
		if (SDMath.diceRoll(getGrowthBonus())) {
			growCrop(pos);
		}

		return farmed;
	}

	@SuppressWarnings("resource")
	protected boolean growCrop(BlockPos pos) {
		if (getLevel().getBlockState(pos) != null
				&& getLevel().getBlockState(pos).getBlock() instanceof BonemealableBlock) {
			BonemealableBlock tempCrop = (BonemealableBlock) getLevel().getBlockState(pos).getBlock();
			if (tempCrop.isValidBonemealTarget(getLevel(), pos, getLevel().getBlockState(pos), false)) {
				tempCrop.performBonemeal((ServerLevel) getLevel(), getLevel().random, pos,
						getLevel().getBlockState(pos));
				((ServerLevel) getLevel()).sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5D,
						pos.getY() + 1.0D, pos.getZ() + 0.5D, 1, 0.0D, 0.0D, 0.0D, 0.0D);
			}
		}
		return true;
	}

	protected void onUpgradesInventoryModifiedCallback(InventoryChangeType changeType, ItemStack item, int slot) {
		range = StaticPowerConfig.SERVER.basicFarmerDefaultRange.get();
		for (ItemStack stack : upgradesInventory) {
			if (stack.getItem() instanceof BaseRangeUpgrade) {
				range = (int) Math.max(range, StaticPowerConfig.SERVER.basicFarmerDefaultRange.get()
						* (((BaseRangeUpgrade) stack.getItem()).getTier().rangeUpgrade.get()));
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
