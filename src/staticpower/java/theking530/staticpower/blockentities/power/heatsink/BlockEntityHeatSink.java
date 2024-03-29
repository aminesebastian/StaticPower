package theking530.staticpower.blockentities.power.heatsink;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import theking530.api.heat.IHeatStorage;
import theking530.staticcore.StaticCoreConfig;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationPreset;
import theking530.staticcore.blockentity.components.control.sideconfiguration.presets.AllSidesInput;
import theking530.staticcore.blockentity.components.heat.HeatStorageComponent;
import theking530.staticcore.data.StaticCoreTier;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticcore.utilities.math.SDMath;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blockentities.BlockEntityMachine;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.init.ModBlocks;

public class BlockEntityHeatSink extends BlockEntityMachine implements MenuProvider {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityHeatSink> TYPE = new BlockEntityTypeAllocator<BlockEntityHeatSink>(
			"heat_sink",
			(allocator, pos, state) -> new BlockEntityHeatSink(allocator, pos, state, StaticPowerTiers.ALUMINUM),
			ModBlocks.AluminumHeatSink, ModBlocks.CopperHeatSink);

	public final HeatStorageComponent heatStorage;

	public BlockEntityHeatSink(BlockEntityTypeAllocator<BlockEntityHeatSink> allocator, BlockPos pos, BlockState state,
			ResourceLocation heatSinkTier) {
		super(allocator, pos, state);
		StaticCoreTier tier = StaticCoreConfig.getTier(heatSinkTier);
//		registerComponent(heatStorage = new HeatStorageComponent("HeatStorageComponent", tier));
		registerComponent(heatStorage = new HeatStorageComponent("HeatStorageComponent", 100, 400, 1000, 2000, 400));
	}

	@Override
	public void process() {
		if (!level.isClientSide) {
			// Damage entities if too hot.
			if (heatStorage.getTemperature() >= StaticPowerConfig.SERVER.heatSinkTemperatureDamageThreshold
					.get()) {
				AABB aabb = new AABB(this.worldPosition.offset(0.0, 0, 0.0), this.worldPosition.offset(1.0, 2.0, 1.0));
				List<LivingEntity> list = this.level.getEntitiesOfClass(LivingEntity.class, aabb);
				for (LivingEntity entity : list) {
					entity.hurt(DamageSource.HOT_FLOOR, 1.0f);
				}
			}
		}

		// If under water, generate bubbles.
		if (heatStorage.getTemperature() >= IHeatStorage.WATER_BOILING_TEMPERATURE) {
			float randomOffset = (3 * getLevel().getRandom().nextFloat()) - 1.5f;
			if (SDMath.diceRoll(0.25f)
					&& level.getBlockState(getBlockPos().relative(Direction.UP)).getBlock() == Blocks.WATER) {
				randomOffset /= 3.5f;
				getLevel().addParticle(ParticleTypes.BUBBLE, getBlockPos().getX() + 0.5f + randomOffset,
						getBlockPos().getY() + 1.1f, getBlockPos().getZ() + 0.5f + randomOffset, 0.0f, 0.5f, 0.0f);
				getLevel().addParticle(ParticleTypes.BUBBLE_POP, getBlockPos().getX() + 0.5f + randomOffset,
						getBlockPos().getY() + 1.8f, getBlockPos().getZ() + 0.5f + randomOffset, 0.0f, 0.005f, 0.0f);
			}
		}
	}

	@Override
	protected SideConfigurationPreset getDefaultSideConfiguration() {
		return AllSidesInput.INSTANCE;
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerHeatSink(windowId, inventory, this);
	}
}
