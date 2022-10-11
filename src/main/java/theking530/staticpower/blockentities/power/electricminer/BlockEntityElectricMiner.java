package theking530.staticpower.blockentities.power.electricminer;

import com.mojang.math.Vector3f;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import theking530.api.upgrades.UpgradeTypes;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticcore.utilities.SDMath;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blockentities.components.energy.PowerStorageComponent;
import theking530.staticpower.blockentities.components.items.BatteryInventoryComponent;
import theking530.staticpower.blockentities.components.items.UpgradeInventoryComponent.UpgradeItemWrapper;
import theking530.staticpower.blockentities.nonpowered.miner.AbstractTileEntityMiner;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.init.ModBlocks;

public class BlockEntityElectricMiner extends AbstractTileEntityMiner {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityElectricMiner> TYPE = new BlockEntityTypeAllocator<>("electric_miner",
			(type, pos, state) -> new BlockEntityElectricMiner(pos, state), ModBlocks.ElectricMiner);

	public final PowerStorageComponent powerStorage;
	public final BatteryInventoryComponent batteryInventory;

	public BlockEntityElectricMiner(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);

		registerComponent(powerStorage = new PowerStorageComponent("MainEnergyStorage", getTier()));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", powerStorage));

		// Set the processing parameters.
		processingComponent.setPowerComponent(powerStorage);

		// Expand upgrades.
		this.upgradesInventory.setSize(4);

		// Set the energy storage upgrade inventory.
		powerStorage.setUpgradeInventory(upgradesInventory);
	}

	@Override
	public void process() {
		super.process();

		// Render the particle effects.
		if (processingComponent.getIsOnBlockState()) {
			float randomOffset = (2 * getLevel().random.nextFloat()) - 1.0f;
			randomOffset /= 3.5f;
			float forwardOffset = getFacingDirection().getAxisDirection() == AxisDirection.POSITIVE ? -1.05f : -0.05f;
			Vector3f forwardVector = SDMath.transformVectorByDirection(getFacingDirection(), new Vector3f(randomOffset + 0.5f, 0.32f, forwardOffset));
			getLevel().addParticle(ParticleTypes.SMOKE, getBlockPos().getX() + forwardVector.x(), getBlockPos().getY() + forwardVector.y(),
					getBlockPos().getZ() + forwardVector.z(), 0.0f, 0.01f, 0.0f);
			if (SDMath.diceRoll(0.25f)) {
				getLevel().addParticle(ParticleTypes.LARGE_SMOKE, getBlockPos().getX() + 0.5f + randomOffset, getBlockPos().getY() + 1.0f,
						getBlockPos().getZ() + 0.5f + randomOffset, 0.0f, 0.01f, 0.0f);
			}
		}
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerElectricMiner(windowId, inventory, this);
	}

	@Override
	public int getProcessingTime() {
		return StaticPowerConfig.SERVER.electricMinerProcessingTime.get();
	}

	@Override
	public int getHeatGeneration() {
		return (int) (StaticPowerConfig.SERVER.electricMinerHeatGeneration.get() * processingComponent.getCalculatedPowerUsageMultipler());
	}

	@Override
	public int getRadius() {
		// Get the range upgrade.
		UpgradeItemWrapper upgradeWrapper = upgradesInventory.getMaxTierItemForUpgradeType(UpgradeTypes.RANGE);

		// If there isn't one, return the base level.
		if (upgradeWrapper.isEmpty()) {
			return StaticPowerConfig.SERVER.electricMinerRadius.get();
		}

		// Otherwise, caluclate the new range.
		StaticPowerTier tier = upgradeWrapper.getTier();
		double newRange = tier.upgradeConfiguration.rangeUpgrade.get() * StaticPowerConfig.SERVER.electricMinerRadius.get();
		return (int) newRange;
	}

	@Override
	public double getFuelUsage() {
		return StaticPowerConfig.SERVER.electricMinerPowerUsage.get();
	}
}
