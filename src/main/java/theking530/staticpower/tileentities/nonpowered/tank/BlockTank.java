package theking530.staticpower.tileentities.nonpowered.tank;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.blocks.interfaces.ICustomModelSupplier;
import theking530.staticpower.blocks.tileentity.StaticPowerTileEntityBlock;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.client.rendering.blocks.TankMachineBakedModel;
import theking530.staticpower.data.StaticPowerTiers;

public class BlockTank extends StaticPowerTileEntityBlock implements ICustomModelSupplier {
	private ResourceLocation tier;

	public BlockTank(String name, ResourceLocation tier) {
		super(name, Block.Properties.create(Material.IRON).harvestTool(ToolType.PICKAXE).hardnessAndResistance(3.5f, 5.0f).sound(SoundType.METAL).notSolid());
		this.tier = tier;
	}

	@Override
	public HasGuiType hasGuiScreen(TileEntity tileEntity, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		return HasGuiType.ALWAYS;
	}

	@Override
	public boolean hasModelOverride(BlockState state) {
		return true;
	}

	@Override
	public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityTank) {
			FluidStack fluid = ((TileEntityTank) te).fluidTankComponent.getFluid();
			FluidAttributes attributes = fluid.getFluid().getAttributes();
			return attributes.getLuminosity(fluid);
		}
		return state.getLightValue();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public IBakedModel getModelOverride(BlockState state, IBakedModel existingModel, ModelBakeEvent event) {
		if (tier == StaticPowerTiers.BASIC) {
			return new TankMachineBakedModel(existingModel, StaticPowerSprites.BASIC_TANK);
		} else if (tier == StaticPowerTiers.ADVANCED) {
			return new TankMachineBakedModel(existingModel, StaticPowerSprites.ADVANCED_TANK);
		} else if (tier == StaticPowerTiers.STATIC) {
			return new TankMachineBakedModel(existingModel, StaticPowerSprites.STATIC_TANK);
		} else if (tier == StaticPowerTiers.ENERGIZED) {
			return new TankMachineBakedModel(existingModel, StaticPowerSprites.ENERGIZED_TANK);
		} else if (tier == StaticPowerTiers.LUMUM) {
			return new TankMachineBakedModel(existingModel, StaticPowerSprites.LUMUM_TANK);
		} else if (tier == StaticPowerTiers.CREATIVE) {
			return new TankMachineBakedModel(existingModel, StaticPowerSprites.CREATIVE_TANK);
		}
		return null;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public RenderType getRenderType() {
		return RenderType.getCutout();
	}

	@Override
	public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
		if (tier == StaticPowerTiers.BASIC) {
			return TileEntityTank.TYPE_BASIC.create();
		} else if (tier == StaticPowerTiers.ADVANCED) {
			return TileEntityTank.TYPE_ADVANCED.create();
		} else if (tier == StaticPowerTiers.STATIC) {
			return TileEntityTank.TYPE_STATIC.create();
		} else if (tier == StaticPowerTiers.ENERGIZED) {
			return TileEntityTank.TYPE_ENERGIZED.create();
		} else if (tier == StaticPowerTiers.LUMUM) {
			return TileEntityTank.TYPE_LUMUM.create();
		} else if (tier == StaticPowerTiers.CREATIVE) {
			return TileEntityTank.TYPE_CREATIVE.create();
		}
		return null;
	}
}
