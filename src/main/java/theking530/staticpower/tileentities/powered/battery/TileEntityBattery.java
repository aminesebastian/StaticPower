package theking530.staticpower.tileentities.powered.battery;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.tileentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticcore.utilities.SDMath;
import theking530.staticpower.client.rendering.tileentity.TileEntityRenderBatteryBlock;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.items.utilities.EnergyHandlerItemStackUtilities;
import theking530.staticpower.tileentities.TileEntityMachine;
import theking530.staticpower.tileentities.components.control.sideconfiguration.DefaultSideConfiguration;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.tileentities.components.energy.PowerDistributionComponent;
import theking530.staticpower.tileentities.components.items.BatteryInventoryComponent;
import theking530.staticpower.tileentities.components.items.InventoryComponent;

public class TileEntityBattery extends TileEntityMachine {
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityBattery> TYPE_BASIC = new BlockEntityTypeAllocator<TileEntityBattery>(
			(allocator, pos, state) -> new TileEntityBattery(allocator, pos, state, StaticPowerTiers.BASIC), ModBlocks.BatteryBasic);

	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityBattery> TYPE_ADVANCED = new BlockEntityTypeAllocator<TileEntityBattery>(
			(allocator, pos, state) -> new TileEntityBattery(allocator, pos, state, StaticPowerTiers.ADVANCED), ModBlocks.BatteryAdvanced);

	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityBattery> TYPE_STATIC = new BlockEntityTypeAllocator<TileEntityBattery>(
			(allocator, pos, state) -> new TileEntityBattery(allocator, pos, state, StaticPowerTiers.STATIC), ModBlocks.BatteryStatic);

	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityBattery> TYPE_ENERGIZED = new BlockEntityTypeAllocator<TileEntityBattery>(
			(allocator, pos, state) -> new TileEntityBattery(allocator, pos, state, StaticPowerTiers.ENERGIZED), ModBlocks.BatteryEnergized);

	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityBattery> TYPE_LUMUM = new BlockEntityTypeAllocator<TileEntityBattery>(
			(allocator, pos, state) -> new TileEntityBattery(allocator, pos, state, StaticPowerTiers.LUMUM), ModBlocks.BatteryLumum);

	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityBattery> TYPE_CREATIVE = new BlockEntityTypeAllocator<TileEntityBattery>(
			(allocator, pos, state) -> new TileEntityBattery(allocator, pos, state, StaticPowerTiers.CREATIVE), ModBlocks.BatteryCreative);

	public static final DefaultSideConfiguration DEFAULT_SIDE_CONFIGURATION = new DefaultSideConfiguration();

	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE_BASIC.setTileEntitySpecialRenderer(TileEntityRenderBatteryBlock::new);
			TYPE_ADVANCED.setTileEntitySpecialRenderer(TileEntityRenderBatteryBlock::new);
			TYPE_STATIC.setTileEntitySpecialRenderer(TileEntityRenderBatteryBlock::new);
			TYPE_ENERGIZED.setTileEntitySpecialRenderer(TileEntityRenderBatteryBlock::new);
			TYPE_LUMUM.setTileEntitySpecialRenderer(TileEntityRenderBatteryBlock::new);
			TYPE_CREATIVE.setTileEntitySpecialRenderer(TileEntityRenderBatteryBlock::new);
		}
	}

	public final BatteryInventoryComponent batteryInventory;
	public final InventoryComponent chargingInventory;
	protected PowerDistributionComponent powerDistributor;

	private double minimumPossibleOutputCurrent;
	private double maximumOutputVoltage;

	private double minimumPossibleOutputVoltage;
	private double maximumPossibleOutputCurrent;

	public TileEntityBattery(BlockEntityTypeAllocator<TileEntityBattery> allocator, BlockPos pos, BlockState state, ResourceLocation tier) {
		super(allocator, pos, state, tier);
		// Enable face interaction.
		enableFaceInteraction();
		this.ioSideConfiguration.setDefaultConfiguration(SideConfigurationComponent.DEFAULT_SIDE_CONFIGURATION);

		// Add the power distributor.
		registerComponent(powerDistributor = new PowerDistributionComponent("PowerDistributor", powerStorage));
		powerStorage.setSideConfiguration(ioSideConfiguration);
		powerStorage.setOutputVoltage(20);
		powerStorage.setMaximumOutputCurrent(10);

		// Add a battery input.
		registerComponent(chargingInventory = new InventoryComponent("ChargingInventorySlot", 1));

		// Add the charging input.
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", powerStorage));
	}

	@Override
	public void process() {
		if (!getLevel().isClientSide()) {
			// If this is a creative battery, always keep the power at max.
			if (getTier() == StaticPowerTiers.CREATIVE) {
				this.powerStorage.addPowerIgnoringVoltageLimitations(Double.MAX_VALUE);
			}

			// Charge up the item in the input slot.
			if (powerStorage.getStoredPower() > 0) {
				// Get the item to charge.
				ItemStack stack = chargingInventory.getStackInSlot(0);
				// If it's not empty and is an energy storing item.
				if (stack != ItemStack.EMPTY && EnergyHandlerItemStackUtilities.isEnergyContainer(stack)) {
					if (EnergyHandlerItemStackUtilities.getStoredPower(stack) < EnergyHandlerItemStackUtilities.getCapacity(stack)) {
						double maxOutput = (powerStorage.getVoltageOutput() * powerStorage.getMaximumCurrentOutput());
						double charged = EnergyHandlerItemStackUtilities.addPower(stack, powerStorage.getVoltageOutput(), maxOutput, false);
						powerStorage.usePowerIgnoringVoltageLimitations(charged);
					}
				}
			}
		}
	}

	public void setMaximumOutputCurrent(double current) {
		if (current >= minimumPossibleOutputCurrent && current <= maximumPossibleOutputCurrent) {
			powerStorage.setMaximumOutputCurrent(current);
		}
	}

	public void setOutputVoltage(double voltage) {
		if (voltage >= minimumPossibleOutputVoltage && voltage <= maximumOutputVoltage) {
			powerStorage.setOutputVoltage(voltage);
		}
	}

	public void addMaximumOutputCurrent(double deltaCurrent) {
		double newCurrent = SDMath.clamp(powerStorage.getMaximumCurrentOutput() + deltaCurrent, minimumPossibleOutputCurrent, maximumPossibleOutputCurrent);
		powerStorage.setMaximumOutputCurrent(newCurrent);
	}

	public void addOutputVoltage(double deltaVoltage) {
		double newVoltage = SDMath.clamp(powerStorage.getVoltageOutput() + deltaVoltage, minimumPossibleOutputVoltage, maximumOutputVoltage);
		powerStorage.setOutputVoltage(newVoltage);
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerBattery(windowId, inventory, this);
	}

	@Override
	protected boolean isValidSideConfiguration(BlockSide side, MachineSideMode mode) {
		return mode == MachineSideMode.Disabled || mode == MachineSideMode.Output || mode == MachineSideMode.Input;
	}
}
