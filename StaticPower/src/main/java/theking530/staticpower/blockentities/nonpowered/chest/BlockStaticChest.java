package theking530.staticpower.blockentities.nonpowered.chest;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.data.StaticCoreTiers;
import theking530.staticpower.blocks.tileentity.StaticPowerRotateableBlockEntityBlock;

public class BlockStaticChest extends StaticPowerRotateableBlockEntityBlock {
	protected static final VoxelShape AABB = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);

	public BlockStaticChest(ResourceLocation tier) {
		super(tier);
	}

	@Override
	public HasGuiType hasGuiScreen(BlockEntity tileEntity, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		return HasGuiType.ALWAYS;
	}

	@Override
	public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
		if (getTier() == StaticCoreTiers.BASIC) {
			return BlockEntityStaticChest.BASIC_TYPE.create(pos, state);
		} else if (getTier() == StaticCoreTiers.ADVANCED) {
			return BlockEntityStaticChest.ADVANCED_TYPE.create(pos, state);
		} else if (getTier() == StaticCoreTiers.STATIC) {
			return BlockEntityStaticChest.STATIC_TYPE.create(pos, state);
		} else if (getTier() == StaticCoreTiers.ENERGIZED) {
			return BlockEntityStaticChest.ENERGIZED_TYPE.create(pos, state);
		} else if (getTier() == StaticCoreTiers.LUMUM) {
			return BlockEntityStaticChest.LUMUM_TYPE.create(pos, state);
		} else {
			throw new RuntimeException("Unsupported tier for Static Chest!");
		}
	}

	@Override
	protected boolean canBeWaterlogged() {
		return true;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
		return AABB;
	}

	@Override
	public RenderShape getRenderShape(BlockState p_49232_) {
		return RenderShape.INVISIBLE;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public RenderType getRenderType() {
		return RenderType.solid();
	}
}
