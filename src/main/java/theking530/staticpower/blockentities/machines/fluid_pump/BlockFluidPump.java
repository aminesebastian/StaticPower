package theking530.staticpower.blockentities.machines.fluid_pump;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.client.ICustomModelProvider;
import theking530.staticpower.blocks.tileentity.StaticPowerRotateableBlockEntityBlock;
import theking530.staticpower.client.rendering.blocks.DefaultMachineBakedModel;

public class BlockFluidPump extends StaticPowerRotateableBlockEntityBlock implements ICustomModelProvider {

	public BlockFluidPump(ResourceLocation tier) {
		super(tier, Block.Properties.of(Material.METAL).strength(3.5f, 5.0f).sound(SoundType.METAL).noOcclusion());
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean isShowingAdvanced) {

	}

	@Override
	public HasGuiType hasGuiScreen(BlockEntity tileEntity, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		return HasGuiType.ALWAYS;
	}

	@Override
	public boolean hasModelOverride(BlockState state) {
		return true;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		if (state.getValue(StaticPowerRotateableBlockEntityBlock.FACING).getAxis() == Axis.Z) {
			VoxelShape interiorShape = Shapes.join(Block.box(5D, 5D, 0.0D, 11D, 11D, 16D), Block.box(4.5D, 4.5D, 1.5D, 11.5D, 11.5D, 14.5D), BooleanOp.OR);
			interiorShape = Shapes.join(interiorShape, Block.box(3.5D, 3.5D, 2.5D, 12.5D, 12.5D, 13.5D), BooleanOp.OR);
			return interiorShape;
		} else if (state.getValue(StaticPowerRotateableBlockEntityBlock.FACING).getAxis() == Axis.Y) {
			VoxelShape interiorShape = Shapes.join(Block.box(5D, 0D, 5.0D, 11D, 16D, 11D), Block.box(4.5D, 1.5D, 4.5D, 11.5D, 14.5D, 11.5D), BooleanOp.OR);
			interiorShape = Shapes.join(interiorShape, Block.box(3.5D, 2.5D, 3.5D, 12.5D, 13.5D, 12.5D), BooleanOp.OR);
			return interiorShape;
		} else {
			VoxelShape interiorShape = Shapes.join(Block.box(0D, 5D, 5.0D, 16D, 11D, 11D), Block.box(1.5D, 4.5D, 4.5D, 14.5D, 11.5D, 11.5D), BooleanOp.OR);
			interiorShape = Shapes.join(interiorShape, Block.box(2.5D, 3.5D, 3.5D, 13.5D, 12.5D, 12.5D), BooleanOp.OR);
			return interiorShape;
		}
	}

	@Override
	public Component getDisplayName(ItemStack stack) {
		// Check to see if the stack has the serialized nbt.
		if (stack.hasTag() && stack.getTag().contains("SerializableNbt")) {
			// If it does, get the tank tag and then the subsequent fluid tag.
			CompoundTag tankTag = stack.getTag().getCompound("SerializableNbt").getCompound("FluidTank");
			CompoundTag fluidTank = tankTag.getCompound("fluidStorage");
			FluidStack fluid = FluidStack.loadFluidStackFromNBT(fluidTank.getCompound("tank"));
			if (!fluid.isEmpty()) {
				return Component.translatable(getDescriptionId()).append(" (").append(Component.translatable(fluid.getTranslationKey()).append(")"));
			}
		}
		return Component.translatable(getDescriptionId());
	}

	@Override
	public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
		BlockEntity te = world.getBlockEntity(pos);
		if (te instanceof BlockEntityFluidPump) {
			FluidStack fluid = ((BlockEntityFluidPump) te).fluidTankComponent.getFluid();
			return fluid.getFluid().getFluidType().getLightLevel();
		}
		return super.getLightEmission(state, world, pos);
	}

	@Override
	public DirectionProperty getFacingType() {
		return FACING;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState blockstate = super.getStateForPlacement(context);
		return blockstate.setValue(FACING, context.getClickedFace().getOpposite());
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BakedModel getBlockModeOverride(BlockState state, BakedModel existingModel, ModelEvent.BakingCompleted event) {
		return new DefaultMachineBakedModel(existingModel, true);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public RenderType getRenderType() {
		return RenderType.cutout();
	}

	@Override
	public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
		return BlockEntityFluidPump.TYPE.create(pos, state);
	}
}
