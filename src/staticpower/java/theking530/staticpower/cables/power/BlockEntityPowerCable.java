package theking530.staticpower.cables.power;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import theking530.api.energy.StaticPowerVoltage;
import theking530.api.energy.utilities.StaticPowerEnergyUtilities;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blockentities.BlockEntityBase;
import theking530.staticpower.init.ModBlocks;

public class BlockEntityPowerCable extends BlockEntityBase {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityPowerCable> TYPE = new BlockEntityTypeAllocator<BlockEntityPowerCable>("cable_power",
			(allocator, pos, state) -> new BlockEntityPowerCable(allocator, pos, state, false),
			Lists.newArrayList(Iterables.unmodifiableIterable(Iterables.concat(ModBlocks.PowerCables.values(), ModBlocks.InsulatedPowerCables.values()))));

	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityPowerCable> TYPE_INDUSTRIAL = new BlockEntityTypeAllocator<BlockEntityPowerCable>("cable_industrial_power",
			(allocator, pos, state) -> new BlockEntityPowerCable(allocator, pos, state, true), ModBlocks.IndustrialPowerCables.values());

	public final PowerCableComponent powerCableComponent;
	public final AABB damageRadius;
	private final boolean isInsulated;

	public BlockEntityPowerCable(BlockEntityTypeAllocator<BlockEntityPowerCable> allocator, BlockPos pos, BlockState state, boolean isIndustrial) {
		super(allocator, pos, state);
		double maxPower = isIndustrial ? getTierObject().cablePowerConfiguration.cableIndustrialPowerMaxPower.get() : getTierObject().cablePowerConfiguration.cableMaxCurrent.get();
		double resistance = isIndustrial ? getTierObject().cablePowerConfiguration.cableIndustrialPowerResistance.get()
				: getTierObject().cablePowerConfiguration.cablePowerResistance.get();
		registerComponent(powerCableComponent = new PowerCableComponent("PowerCableComponent", isIndustrial, StaticPowerVoltage.BONKERS, maxPower, resistance));

		damageRadius = new AABB(pos.getX() + 0.25, pos.getY() + 0.25, pos.getZ() + 0.25, pos.getX() + 0.75, pos.getY() + 0.75, pos.getZ() + 0.75);

		if (state.getBlock() instanceof BlockPowerCable) {
			BlockPowerCable powerCableBlock = (BlockPowerCable) state.getBlock();
			isInsulated = powerCableBlock.isInsulated();
		} else {
			isInsulated = false;
		}
	}

	@Override
	public void process() {
		if (!isInsulated()) {
			if (!getLevel().isClientSide() && powerCableComponent.getEnergyTracker() != null) {
				double averageCurrent = powerCableComponent.getEnergyTracker().getAverageCurrent();
				if (averageCurrent >= StaticPowerConfig.SERVER.electricalDamageThreshold.get()) {
					StaticPowerEnergyUtilities.applyElectricalDamageInArea(LivingEntity.class, level, damageRadius, averageCurrent,
							StaticPowerConfig.SERVER.electricalDamageMultiplier.get());
				}
			}
		}

	}

	public boolean isInsulated() {
		return isInsulated;
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerPowerCable(windowId, inventory, this);
	}
}
