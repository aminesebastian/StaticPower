package theking530.staticpower.blockentities.power.battery;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.api.energy.CurrentType;
import theking530.api.energy.PowerStack;
import theking530.api.energy.StaticPowerVoltage;
import theking530.api.energy.item.EnergyHandlerItemStackUtilities;
import theking530.api.energy.utilities.StaticPowerEnergyUtilities;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.blockentities.BlockEntityMachine;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationPreset;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationPresets;
import theking530.staticpower.blockentities.components.energy.PowerDistributionComponent;
import theking530.staticpower.blockentities.components.energy.PowerStorageComponent;
import theking530.staticpower.blockentities.components.items.BatteryInventoryComponent;
import theking530.staticpower.blockentities.components.items.InventoryComponent;
import theking530.staticpower.client.rendering.blockentity.BlockEntityRenderBatteryBlock;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.init.ModBlocks;

public class BlockEntityBattery extends BlockEntityMachine {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityBattery> TYPE_BASIC = new BlockEntityTypeAllocator<BlockEntityBattery>("battery_block_basic",
			(allocator, pos, state) -> new BlockEntityBattery(allocator, pos, state), ModBlocks.BatteryBasic);

	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityBattery> TYPE_ADVANCED = new BlockEntityTypeAllocator<BlockEntityBattery>("battery_block_advanced",
			(allocator, pos, state) -> new BlockEntityBattery(allocator, pos, state), ModBlocks.BatteryAdvanced);

	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityBattery> TYPE_STATIC = new BlockEntityTypeAllocator<BlockEntityBattery>("battery_block_static",
			(allocator, pos, state) -> new BlockEntityBattery(allocator, pos, state), ModBlocks.BatteryStatic);

	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityBattery> TYPE_ENERGIZED = new BlockEntityTypeAllocator<BlockEntityBattery>("battery_block_energized",
			(allocator, pos, state) -> new BlockEntityBattery(allocator, pos, state), ModBlocks.BatteryEnergized);

	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityBattery> TYPE_LUMUM = new BlockEntityTypeAllocator<BlockEntityBattery>("battery_block_lumum",
			(allocator, pos, state) -> new BlockEntityBattery(allocator, pos, state), ModBlocks.BatteryLumum);

	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityBattery> TYPE_CREATIVE = new BlockEntityTypeAllocator<BlockEntityBattery>("battery_block_creative",
			(allocator, pos, state) -> new BlockEntityBattery(allocator, pos, state), ModBlocks.BatteryCreative);

	public static final SideConfigurationPreset DEFAULT_SIDE_CONFIGURATION = new SideConfigurationPreset();

	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE_BASIC.setTileEntitySpecialRenderer(BlockEntityRenderBatteryBlock::new);
			TYPE_ADVANCED.setTileEntitySpecialRenderer(BlockEntityRenderBatteryBlock::new);
			TYPE_STATIC.setTileEntitySpecialRenderer(BlockEntityRenderBatteryBlock::new);
			TYPE_ENERGIZED.setTileEntitySpecialRenderer(BlockEntityRenderBatteryBlock::new);
			TYPE_LUMUM.setTileEntitySpecialRenderer(BlockEntityRenderBatteryBlock::new);
			TYPE_CREATIVE.setTileEntitySpecialRenderer(BlockEntityRenderBatteryBlock::new);
		}
	}

	public final PowerStorageComponent powerStorage;
	public final BatteryInventoryComponent batteryInventory;
	public final InventoryComponent chargingInventory;
	protected final PowerDistributionComponent powerDistributor;

	public BlockEntityBattery(BlockEntityTypeAllocator<BlockEntityBattery> allocator, BlockPos pos, BlockState state) {
		super(allocator, pos, state);
		enableFaceInteraction();
		ioSideConfiguration.setPreset(SideConfigurationPresets.DEFAULT_SIDE_CONFIGURATION, true);

		registerComponent(powerStorage = new PowerStorageComponent("MainEnergyStorage", getTier(), true, true) {
			@Override
			public double addPower(PowerStack stack, boolean simulate) {
				// Creative batteries will always accept the full incoming stack.
				// Useful for debugging purposes and not having batteries fill up on you while
				// testing.
				if (getTier() == StaticPowerTiers.CREATIVE) {
					super.addPower(stack, simulate);
					return stack.getPower();
				}
				return super.addPower(stack, simulate);
			}
		});

		// Add the power distributor.
		powerStorage.setSideConfiguration(ioSideConfiguration);
		powerStorage.setCapacity(getTierObject().powerConfiguration.batteryCapacity.get());
		powerStorage.setInputVoltageRange(getTierObject().powerConfiguration.getMaximumBatteryInputVoltage());
		powerStorage.setMaximumInputPower(getTierObject().powerConfiguration.batteryMaximumPowerInput.get());
		powerStorage.setOutputVoltage(getTierObject().powerConfiguration.batteryOutputVoltage.get());
		powerStorage.setMaximumOutputPower(getTierObject().powerConfiguration.batteryMaximumPowerOutput.get());
		powerStorage.setInputCurrentTypes(CurrentType.DIRECT, CurrentType.ALTERNATING);
		powerStorage.setCanAcceptExternalPower(true);
		powerStorage.setCanOutputExternalPower(true);

		registerComponent(powerDistributor = new PowerDistributionComponent("PowerDistributor", powerStorage));

		if (this.getTier() == StaticPowerTiers.CREATIVE) {
			powerStorage.setMaximumInputPower(StaticPowerEnergyUtilities.getMaximumPower());
			powerStorage.setMaximumOutputPower(StaticPowerEnergyUtilities.getMaximumPower());
		}

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
				powerStorage.addPower(new PowerStack(StaticPowerEnergyUtilities.getMaximumPower(), StaticPowerVoltage.LOW), false);
			}

			// Charge up the item in the input slot.
			if (powerStorage.getStoredPower() > 0) {
				// Get the item to charge.
				ItemStack stack = chargingInventory.getStackInSlot(0);
				// If it's not empty and is an energy storing item.
				if (stack != ItemStack.EMPTY && EnergyHandlerItemStackUtilities.isEnergyContainer(stack)) {
					if (EnergyHandlerItemStackUtilities.getStoredPower(stack) < EnergyHandlerItemStackUtilities.getCapacity(stack)) {
						PowerStack maxOutput = powerStorage.drainPower(StaticPowerEnergyUtilities.getMaximumPower(), true);
						double charged = EnergyHandlerItemStackUtilities.addPower(stack, maxOutput, false);
						powerStorage.drainPower(charged, false);
					}
				}
			}
		}
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerBattery(windowId, inventory, this);
	}
}
