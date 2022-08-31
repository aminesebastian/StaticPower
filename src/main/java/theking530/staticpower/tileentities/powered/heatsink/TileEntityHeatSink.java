package theking530.staticpower.tileentities.powered.heatsink;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import theking530.api.heat.IHeatStorage;
import theking530.api.heat.IHeatStorage.HeatTransferAction;
import theking530.staticcore.initialization.tileentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticcore.utilities.SDMath;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.TileEntityMachine;
import theking530.staticpower.tileentities.components.heat.HeatStorageComponent;

public class TileEntityHeatSink extends TileEntityMachine implements MenuProvider {
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityHeatSink> TYPE_ALUMINUM = new BlockEntityTypeAllocator<TileEntityHeatSink>(
			(allocator, pos, state) -> new TileEntityHeatSink(allocator, pos, state, StaticPowerTiers.ALUMINUM), ModBlocks.AluminumHeatSink);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityHeatSink> TYPE_COPPER = new BlockEntityTypeAllocator<TileEntityHeatSink>(
			(allocator, pos, state) -> new TileEntityHeatSink(allocator, pos, state, StaticPowerTiers.COPPER), ModBlocks.CopperHeatSink);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityHeatSink> TYPE_TIN = new BlockEntityTypeAllocator<TileEntityHeatSink>(
			(allocator, pos, state) -> new TileEntityHeatSink(allocator, pos, state, StaticPowerTiers.TIN), ModBlocks.TinHeatSink);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityHeatSink> TYPE_SILVER = new BlockEntityTypeAllocator<TileEntityHeatSink>(
			(allocator, pos, state) -> new TileEntityHeatSink(allocator, pos, state, StaticPowerTiers.SILVER), ModBlocks.SilverHeatSink);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityHeatSink> TYPE_GOLD = new BlockEntityTypeAllocator<TileEntityHeatSink>(
			(allocator, pos, state) -> new TileEntityHeatSink(allocator, pos, state, StaticPowerTiers.GOLD), ModBlocks.GoldHeatSink);

	public final HeatStorageComponent heatStorage;
	private final ResourceLocation heatSinkTier;

	public TileEntityHeatSink(BlockEntityTypeAllocator<TileEntityHeatSink> allocator, BlockPos pos, BlockState state, ResourceLocation heatSinkTier) {
		super(allocator, pos, state, StaticPowerTiers.BASIC);
		this.heatSinkTier = heatSinkTier;
		StaticPowerTier tier = StaticPowerConfig.getTier(heatSinkTier);
		registerComponent(
				heatStorage = new HeatStorageComponent("HeatStorageComponent", tier.heatSinkOverheatTemperature.get(), tier.heatSinkMaximumTemperature.get(), 1.0f));
		energyStorage.setMaxInput(tier.heatSinkElectricHeatPowerUsage.get() * 2);
	}

	@Override
	public void process() {
		if (!level.isClientSide) {
			// Use power to generate heat.
			generateHeat();

			// Damage entities if too hot.
			if (heatStorage.getStorage().getCurrentHeat() >= StaticPowerConfig.SERVER.heatSinkTemperatureDamageThreshold.get()) {
				AABB aabb = new AABB(this.worldPosition.offset(0.0, 0, 0.0), this.worldPosition.offset(1.0, 2.0, 1.0));
				List<Entity> list = this.level.getEntitiesOfClass(Entity.class, aabb);
				for (Entity entity : list) {
					if (!(entity instanceof ItemEntity)) {
						entity.hurt(DamageSource.HOT_FLOOR, 1.0f);
					}
				}
			}
		}

		// If under water, generate bubbles.
		// TODO: Tweak this number to == the temp we say water boils.
		if (heatStorage.getStorage().getCurrentHeat() >= IHeatStorage.WATER_BOILING_TEMPERATURE) {
			float randomOffset = (3 * getLevel().random.nextFloat()) - 1.5f;
			if (SDMath.diceRoll(0.25f) && level.getBlockState(getBlockPos().relative(Direction.UP)).getBlock() == Blocks.WATER) {
				randomOffset /= 3.5f;
				getLevel().addParticle(ParticleTypes.BUBBLE, getBlockPos().getX() + 0.5f + randomOffset, getBlockPos().getY() + 1.1f, getBlockPos().getZ() + 0.5f + randomOffset,
						0.0f, 0.5f, 0.0f);
				getLevel().addParticle(ParticleTypes.BUBBLE_POP, getBlockPos().getX() + 0.5f + randomOffset, getBlockPos().getY() + 1.8f,
						getBlockPos().getZ() + 0.5f + randomOffset, 0.0f, 0.005f, 0.0f);
			}
		}
	}

	protected void generateHeat() {
		if (!energyStorage.getStorage().isEmpty()) {
			StaticPowerTier tier = StaticPowerConfig.getTier(heatSinkTier);
			int generation = tier.heatSinkElectricHeatGeneration.get();
			int generationCost = tier.heatSinkElectricHeatPowerUsage.get();

			if (generation > 0) {
				int transferableHeat = heatStorage.getStorage().getOverheatThreshold() - heatStorage.getStorage().getCurrentHeat();
				transferableHeat = Math.min(transferableHeat, generation);

				long maxPowerUsage = Math.min(generationCost, energyStorage.getStorage().getStoredPower());
				long powerUsage = (int) Math.max(1, maxPowerUsage * ((float) transferableHeat / generation));
				if (energyStorage.hasEnoughPower(powerUsage)) {
					energyStorage.useBulkPower(powerUsage);
					heatStorage.getStorage().heat(transferableHeat, HeatTransferAction.EXECUTE);
				}
			}
		}
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerHeatSink(windowId, inventory, this);
	}
}
