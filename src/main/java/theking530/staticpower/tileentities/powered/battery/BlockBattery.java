package theking530.staticpower.tileentities.powered.battery;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelBakeEvent;
import theking530.staticpower.client.rendering.blocks.BatteryBlockedBakedModel;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.initialization.ModTileEntityTypes;
import theking530.staticpower.tileentities.StaticPowerDefaultMachineBlock;

public class BlockBattery extends StaticPowerDefaultMachineBlock {

	public ResourceLocation tier;

	public BlockBattery(String name, ResourceLocation tier) {
		super(name);
		this.tier = tier;
	}

	@Override
	public HasGuiType hasGuiScreen(TileEntity tileEntity, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		return HasGuiType.ALWAYS;
	}

	@Override
	public int getStrongPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		if (blockAccess.getTileEntity(pos) instanceof TileEntityBattery) {
			TileEntityBattery battery = (TileEntityBattery) blockAccess.getTileEntity(pos);
			return battery.shouldOutputRedstoneSignal() ? 15 : 0;
		}
		return 0;
	}

	@Override
	public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		if (blockAccess.getTileEntity(pos) instanceof TileEntityBattery) {
			TileEntityBattery battery = (TileEntityBattery) blockAccess.getTileEntity(pos);
			return battery.shouldOutputRedstoneSignal() ? 15 : 0;
		}
		return 0;
	}

	@Override
	public boolean canProvidePower(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
		if (tier == StaticPowerTiers.BASIC) {
			return ModTileEntityTypes.BATTERY_BASIC.create();
		} else if (tier == StaticPowerTiers.ADVANCED) {
			return ModTileEntityTypes.BATTERY_ADVANCED.create();
		} else if (tier == StaticPowerTiers.STATIC) {
			return ModTileEntityTypes.BATTERY_STATIC.create();
		} else if (tier == StaticPowerTiers.ENERGIZED) {
			return ModTileEntityTypes.BATTERY_ENERGIZED.create();
		} else if (tier == StaticPowerTiers.LUMUM) {
			return ModTileEntityTypes.BATTERY_LUMUM.create();
		} else if (tier == StaticPowerTiers.CREATIVE) {
			return ModTileEntityTypes.BATTERY_CREATIVE.create();
		}
		return null;
	}
	
	@Override
	public IBakedModel getModelOverride(BlockState state, IBakedModel existingModel, ModelBakeEvent event) {
		return new BatteryBlockedBakedModel(existingModel);
	}
}
