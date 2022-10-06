package theking530.staticpower.blockentities.nonpowered.tank;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticcore.utilities.SDMath;
import theking530.staticpower.blockentities.BlockEntityBase;
import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticpower.blockentities.components.fluids.FluidInputServoComponent;
import theking530.staticpower.blockentities.components.fluids.FluidOutputServoComponent;
import theking530.staticpower.blockentities.components.fluids.FluidTankComponent;
import theking530.staticpower.blockentities.components.items.FluidContainerInventoryComponent;
import theking530.staticpower.blockentities.components.items.FluidContainerInventoryComponent.FluidContainerInteractionMode;
import theking530.staticpower.blockentities.components.items.ItemStackHandlerFilter;
import theking530.staticpower.blockentities.components.items.UpgradeInventoryComponent;
import theking530.staticpower.client.rendering.blockentity.BlockEntityRenderTank;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.items.upgrades.VoidUpgrade;

public class BlockEntityTank extends BlockEntityBase {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityTank> TYPE_IRON = new BlockEntityTypeAllocator<BlockEntityTank>(
			(type, pos, state) -> new BlockEntityTank(type, pos, state), ModBlocks.IronTank);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityTank> TYPE_BASIC = new BlockEntityTypeAllocator<BlockEntityTank>(
			(type, pos, state) -> new BlockEntityTank(type, pos, state), ModBlocks.BasicTank);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityTank> TYPE_ADVANCED = new BlockEntityTypeAllocator<BlockEntityTank>(
			(type, pos, state) -> new BlockEntityTank(type, pos, state), ModBlocks.AdvancedTank);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityTank> TYPE_STATIC = new BlockEntityTypeAllocator<BlockEntityTank>(
			(type, pos, state) -> new BlockEntityTank(type, pos, state), ModBlocks.StaticTank);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityTank> TYPE_ENERGIZED = new BlockEntityTypeAllocator<BlockEntityTank>(
			(type, pos, state) -> new BlockEntityTank(type, pos, state), ModBlocks.EnergizedTank);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityTank> TYPE_LUMUM = new BlockEntityTypeAllocator<BlockEntityTank>(
			(type, pos, state) -> new BlockEntityTank(type, pos, state), ModBlocks.LumumTank);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityTank> TYPE_CREATIVE = new BlockEntityTypeAllocator<BlockEntityTank>(
			(type, pos, state) -> new BlockEntityTank(type, pos, state), ModBlocks.CreativeTank);

	public static final int MACHINE_TANK_CAPACITY_MULTIPLIER = 4;

	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE_IRON.setTileEntitySpecialRenderer(BlockEntityRenderTank::new);
			TYPE_BASIC.setTileEntitySpecialRenderer(BlockEntityRenderTank::new);
			TYPE_ADVANCED.setTileEntitySpecialRenderer(BlockEntityRenderTank::new);
			TYPE_STATIC.setTileEntitySpecialRenderer(BlockEntityRenderTank::new);
			TYPE_ENERGIZED.setTileEntitySpecialRenderer(BlockEntityRenderTank::new);
			TYPE_LUMUM.setTileEntitySpecialRenderer(BlockEntityRenderTank::new);
			TYPE_CREATIVE.setTileEntitySpecialRenderer(BlockEntityRenderTank::new);
		}
	}

	public final FluidContainerInventoryComponent inputFluidContainerComponent;
	public final FluidContainerInventoryComponent outputFluidContainerComponent;
	public final FluidTankComponent fluidTankComponent;
	public final UpgradeInventoryComponent voidUpgradeInventory;
	public final SideConfigurationComponent ioSideConfiguration;

	public BlockEntityTank(BlockEntityTypeAllocator<BlockEntityTank> allocator, BlockPos pos, BlockState state) {
		super(allocator, pos, state);

		// Get the tier.
		StaticPowerTier tierObject = getTierObject();

		// Add the void upgrade component.
		registerComponent(voidUpgradeInventory = new UpgradeInventoryComponent("VoidUpgradeInventory", 1));
		voidUpgradeInventory.setShiftClickEnabled(true);
		voidUpgradeInventory.setFilter(new ItemStackHandlerFilter() {
			@Override
			public boolean canInsertItem(int slot, ItemStack stack) {
				return stack.getItem() instanceof VoidUpgrade;
			}
		});

		// Add the tank component.
		int capacity = SDMath.multiplyRespectingOverflow(tierObject.defaultTankCapacity.get(), MACHINE_TANK_CAPACITY_MULTIPLIER);
		registerComponent(fluidTankComponent = new FluidTankComponent("FluidTank", capacity).setCapabilityExposedModes(MachineSideMode.Input, MachineSideMode.Output));
		fluidTankComponent.setCanFill(true);
		fluidTankComponent.setAutoSyncPacketsEnabled(true);
		fluidTankComponent.setUpgradeInventory(voidUpgradeInventory);

		// Add the side configuration component.
		registerComponent(ioSideConfiguration = new SideConfigurationComponent("SideConfiguration", (side, mode) -> {
		}, (side, mode) -> {
			return mode == MachineSideMode.Input || mode == MachineSideMode.Output || mode == MachineSideMode.Disabled;
		}));

		// Add the inventory for the fluid containers.
		registerComponent(inputFluidContainerComponent = new FluidContainerInventoryComponent("FluidFillContainerServo", fluidTankComponent));
		registerComponent(
				outputFluidContainerComponent = new FluidContainerInventoryComponent("FluidDrainContainerServo", fluidTankComponent).setMode(FluidContainerInteractionMode.FILL));

		// Add the two components to auto input and output fluids.
		registerComponent(new FluidInputServoComponent("FluidInputServoComponent", fluidTankComponent.getCapacity() / 100, fluidTankComponent, MachineSideMode.Input));
		registerComponent(new FluidOutputServoComponent("FluidOutputServoComponent", fluidTankComponent.getCapacity() / 100, fluidTankComponent, MachineSideMode.Output));
	}

	@Override
	public void process() {
		// Creative tanks should automatically fill up with the first provided fluid.
		if (getType() == TYPE_CREATIVE.getType() && fluidTankComponent.getFluidAmount() > 0) {
			FluidStack existingStack = fluidTankComponent.getFluid();
			existingStack.setAmount(fluidTankComponent.getCapacity() / 2);
		}
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerTank(windowId, inventory, this);
	}

	@Override
	public boolean shouldSerializeWhenBroken() {
		return true;
	}

	@Override
	public boolean shouldDeserializeWhenPlaced(CompoundTag nbt, Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		return true;
	}
}
