package theking530.staticpower.tileentities.powered.heatsink;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import theking530.staticcore.initialization.tileentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticcore.utilities.SDMath;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.cables.heat.HeatCableComponent;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.TileEntityMachine;

public class TileEntityHeatSink extends TileEntityMachine implements MenuProvider {
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityHeatSink> TYPE_ALUMINUM = new BlockEntityTypeAllocator<TileEntityHeatSink>(
			(allocator, pos, state) -> new TileEntityHeatSink(allocator, pos, state, StaticPowerTiers.ALUMINUM), ModBlocks.AluminumHeatSink.get());
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityHeatSink> TYPE_COPPER = new BlockEntityTypeAllocator<TileEntityHeatSink>(
			(allocator, pos, state) -> new TileEntityHeatSink(allocator, pos, state, StaticPowerTiers.COPPER), ModBlocks.CopperHeatSink.get());
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityHeatSink> TYPE_TIN = new BlockEntityTypeAllocator<TileEntityHeatSink>(
			(allocator, pos, state) -> new TileEntityHeatSink(allocator, pos, state, StaticPowerTiers.TIN), ModBlocks.TinHeatSink.get());
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityHeatSink> TYPE_SILVER = new BlockEntityTypeAllocator<TileEntityHeatSink>(
			(allocator, pos, state) -> new TileEntityHeatSink(allocator, pos, state, StaticPowerTiers.SILVER), ModBlocks.SilverHeatSink.get());
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityHeatSink> TYPE_GOLD = new BlockEntityTypeAllocator<TileEntityHeatSink>(
			(allocator, pos, state) -> new TileEntityHeatSink(allocator, pos, state, StaticPowerTiers.GOLD), ModBlocks.GoldHeatSink.get());

	public final HeatCableComponent cableComponent;

	public TileEntityHeatSink(BlockEntityTypeAllocator<TileEntityHeatSink> allocator, BlockPos pos, BlockState state, ResourceLocation tierName) {
		super(allocator, pos, state);
		StaticPowerTier tier = StaticPowerConfig.getTier(tierName);
		registerComponent(cableComponent = new HeatCableComponent("HeatCableComponent", tier.heatSinkCapacity.get(), tier.heatSinkConductivity.get(), tier.heatSinkElectricHeatGeneration.get(),
				tier.heatSinkElectricHeatPowerUsage.get()).setEnergyStorageComponent(energyStorage));
		energyStorage.setMaxInput(tier.heatSinkElectricHeatPowerUsage.get() * 2);
	}

	@Override
	public void process() {
		if (!level.isClientSide) {
			cableComponent.getHeatNetworkModule().ifPresent(module -> {
				// Apply damage to any entities that are on top of this block if the heat is >
				// the damage threshold.
				if (module.getHeatPerCable() >= StaticPowerConfig.SERVER.heatSinkTemperatureDamageThreshold.get()) {
					AABB aabb = new AABB(this.worldPosition.offset(0.0, 0, 0.0), this.worldPosition.offset(1.0, 2.0, 1.0));
					List<Entity> list = this.level.getEntitiesOfClass(Entity.class, aabb);
					for (Entity entity : list) {
						entity.hurt(DamageSource.HOT_FLOOR, 1.0f);
					}
				}
			});
		}

		// If under water, generate bubbles.
		float randomOffset = (3 * getLevel().random.nextFloat()) - 1.5f;
		if (SDMath.diceRoll(0.25f) && level.getBlockState(getBlockPos().relative(Direction.UP)).getBlock() == Blocks.WATER) {
			randomOffset /= 3.5f;
			getLevel().addParticle(ParticleTypes.BUBBLE, getBlockPos().getX() + 0.5f + randomOffset, getBlockPos().getY() + 1.1f, getBlockPos().getZ() + 0.5f + randomOffset, 0.0f, 0.5f, 0.0f);
			getLevel().addParticle(ParticleTypes.BUBBLE_POP, getBlockPos().getX() + 0.5f + randomOffset, getBlockPos().getY() + 1.8f, getBlockPos().getZ() + 0.5f + randomOffset, 0.0f, 0.005f, 0.0f);
		}
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerHeatSink(windowId, inventory, this);
	}
}
