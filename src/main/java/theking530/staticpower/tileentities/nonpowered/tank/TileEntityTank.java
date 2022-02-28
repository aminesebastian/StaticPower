package theking530.staticpower.tileentities.nonpowered.tank;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
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
import theking530.staticcore.initialization.tileentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticcore.utilities.SDMath;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.client.rendering.tileentity.TileEntityRenderTank;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.items.upgrades.VoidUpgrade;
import theking530.staticpower.tileentities.TileEntityBase;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticpower.tileentities.components.fluids.FluidInputServoComponent;
import theking530.staticpower.tileentities.components.fluids.FluidOutputServoComponent;
import theking530.staticpower.tileentities.components.fluids.FluidTankComponent;
import theking530.staticpower.tileentities.components.items.FluidContainerInventoryComponent;
import theking530.staticpower.tileentities.components.items.FluidContainerInventoryComponent.FluidContainerInteractionMode;
import theking530.staticpower.tileentities.components.items.ItemStackHandlerFilter;
import theking530.staticpower.tileentities.components.items.UpgradeInventoryComponent;

public class TileEntityTank extends TileEntityBase {
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityTank> TYPE_IRON = new BlockEntityTypeAllocator<TileEntityTank>(
			(type, pos, state) -> new TileEntityTank(type, pos, state, StaticPowerTiers.IRON), ModBlocks.IronTank);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityTank> TYPE_BASIC = new BlockEntityTypeAllocator<TileEntityTank>(
			(type, pos, state) -> new TileEntityTank(type, pos, state, StaticPowerTiers.BASIC), ModBlocks.BasicTank);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityTank> TYPE_ADVANCED = new BlockEntityTypeAllocator<TileEntityTank>(
			(type, pos, state) -> new TileEntityTank(type, pos, state, StaticPowerTiers.ADVANCED), ModBlocks.AdvancedTank);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityTank> TYPE_STATIC = new BlockEntityTypeAllocator<TileEntityTank>(
			(type, pos, state) -> new TileEntityTank(type, pos, state, StaticPowerTiers.STATIC), ModBlocks.StaticTank);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityTank> TYPE_ENERGIZED = new BlockEntityTypeAllocator<TileEntityTank>(
			(type, pos, state) -> new TileEntityTank(type, pos, state, StaticPowerTiers.ENERGIZED), ModBlocks.EnergizedTank);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityTank> TYPE_LUMUM = new BlockEntityTypeAllocator<TileEntityTank>(
			(type, pos, state) -> new TileEntityTank(type, pos, state, StaticPowerTiers.LUMUM), ModBlocks.LumumTank);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityTank> TYPE_CREATIVE = new BlockEntityTypeAllocator<TileEntityTank>(
			(type, pos, state) -> new TileEntityTank(type, pos, state, StaticPowerTiers.CREATIVE), ModBlocks.CreativeTank);

	public static final int MACHINE_TANK_CAPACITY_MULTIPLIER = 4;

	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE_IRON.setTileEntitySpecialRenderer(TileEntityRenderTank::new);
			TYPE_BASIC.setTileEntitySpecialRenderer(TileEntityRenderTank::new);
			TYPE_ADVANCED.setTileEntitySpecialRenderer(TileEntityRenderTank::new);
			TYPE_STATIC.setTileEntitySpecialRenderer(TileEntityRenderTank::new);
			TYPE_ENERGIZED.setTileEntitySpecialRenderer(TileEntityRenderTank::new);
			TYPE_LUMUM.setTileEntitySpecialRenderer(TileEntityRenderTank::new);
			TYPE_CREATIVE.setTileEntitySpecialRenderer(TileEntityRenderTank::new);
		}
	}

	public final FluidContainerInventoryComponent inputFluidContainerComponent;
	public final FluidContainerInventoryComponent outputFluidContainerComponent;
	public final FluidTankComponent fluidTankComponent;
	public final UpgradeInventoryComponent voidUpgradeInventory;
	public final SideConfigurationComponent ioSideConfiguration;

	public TileEntityTank(BlockEntityTypeAllocator<TileEntityTank> allocator, BlockPos pos, BlockState state, ResourceLocation tier) {
		super(allocator, pos, state);

		// Get the tier.
		StaticPowerTier tierObject = StaticPowerConfig.getTier(tier);

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
		registerComponent(outputFluidContainerComponent = new FluidContainerInventoryComponent("FluidDrainContainerServo", fluidTankComponent).setMode(FluidContainerInteractionMode.FILL));

		// Add the two components to auto input and output fluids.
		registerComponent(new FluidInputServoComponent("FluidInputServoComponent", fluidTankComponent.getCapacity() / 100, fluidTankComponent, MachineSideMode.Input));
		registerComponent(new FluidOutputServoComponent("FluidOutputServoComponent", fluidTankComponent.getCapacity() / 100, fluidTankComponent, MachineSideMode.Output));
	}

	@Override
	public void process() {
		// Creative tanks should automatically fill up with the first provided fluid.
		if (getType() == TYPE_CREATIVE.getType() && fluidTankComponent.getFluidAmount() > 0) {
			FluidStack existingStack = fluidTankComponent.getFluid();
			existingStack.setAmount(fluidTankComponent.getCapacity());
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
