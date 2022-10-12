package theking530.staticpower.blockentities.power.lightsocket;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelEvent;
import theking530.staticcore.item.ICustomModelSupplier;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationUtilities;
import theking530.staticpower.blocks.tileentity.StaticPowerBlockEntityBlock;
import theking530.staticpower.client.rendering.blocks.LightSocketModel;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.init.ModTags;
import theking530.staticpower.utilities.RaytracingUtilities;
import theking530.staticpower.utilities.RaytracingUtilities.AdvancedRayTraceResult;
import theking530.staticpower.utilities.WorldUtilities;

public class BlockLightSocket extends StaticPowerBlockEntityBlock implements ICustomModelSupplier {
	public static final Map<Direction, VoxelShape> SHAPES = new HashMap<>();
	public static final Map<Direction, VoxelShape> BULB_SHAPES = new HashMap<>();
	static {
		for (Direction shape : Direction.values()) {
			VoxelShape result = null;
			VoxelShape bulbShape = null;
			if (shape == Direction.DOWN) {
				result = Shapes.join(Block.box(5D, 14D, 5D, 11D, 16D, 11D), Block.box(6.5D, 13D, 6.5D, 9.5D, 14D, 9.5D), BooleanOp.OR);
				bulbShape = Block.box(6.25D, 7.75D, 6.25D, 9.75D, 16D, 9.75D);
			} else if (shape == Direction.UP) {
				result = Shapes.join(Block.box(5D, 0D, 5D, 11D, 2D, 11D), Block.box(6.5D, 2D, 6.5D, 9.5D, 3D, 9.5D), BooleanOp.OR);
				bulbShape = Block.box(6.25D, 0.0D, 6.25D, 9.75D, 8.25D, 9.75D);
			} else if (shape == Direction.WEST) {
				result = Shapes.join(Block.box(14D, 5D, 5D, 16D, 11D, 11D), Block.box(13D, 6.5D, 6.5D, 14D, 9.5D, 9.5D), BooleanOp.OR);
				bulbShape = Block.box(7.75D, 6.25D, 6.25D, 16D, 9.75D, 9.75D);
			} else if (shape == Direction.EAST) {
				result = Shapes.join(Block.box(0D, 5D, 5D, 2D, 11D, 11D), Block.box(2D, 6.5D, 6.5D, 3D, 9.5D, 9.5D), BooleanOp.OR);
				bulbShape = Block.box(0D, 6.25D, 6.25D, 8.25D, 9.75D, 9.75D);
			} else if (shape == Direction.NORTH) {
				result = Shapes.join(Block.box(5D, 5D, 14D, 11D, 11D, 16D), Block.box(6.5D, 6.5D, 13D, 9.5D, 9.5D, 14D), BooleanOp.OR);
				bulbShape = Block.box(6.25D, 6.25D, 7.75D, 9.75D, 9.75D, 16D);
			} else if (shape == Direction.SOUTH) {
				result = Shapes.join(Block.box(5D, 5D, 0.0D, 11D, 11D, 2D), Block.box(6.5D, 6.5D, 2D, 9.5D, 9.5D, 3D), BooleanOp.OR);
				bulbShape = Block.box(6.25D, 6.25D, 0D, 9.75D, 9.75D, 8.25D);
			}
			BULB_SHAPES.put(shape, bulbShape);
			SHAPES.put(shape, result);
		}
	}

	public BlockLightSocket() {
		super(StaticPowerTiers.BASIC);
	}

	@Override
	public HasGuiType hasGuiScreen(BlockEntity tileEntity, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		return HasGuiType.NEVER;
	}

	@Override
	public InteractionResult onStaticPowerBlockActivated(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		BlockEntityLightSocket socket = (BlockEntityLightSocket) world.getBlockEntity(pos);
		Direction facingDirection = state.getValue(FACING);
		
		if (socket.hasLightBulb()) {
			ItemStack removed = socket.removeLightbulb();
			if (!world.isClientSide() && !player.isCreative()) {
				WorldUtilities.dropItem(world, pos, removed);
			}
			world.playSound(player, pos, SoundEvents.SPYGLASS_USE, SoundSource.BLOCKS, 0.5f, 1.0f);
			world.playSound(player, pos, SoundEvents.LAVA_POP, SoundSource.BLOCKS, 0.25f, 1.5f);
			return InteractionResult.SUCCESS;
		} else {
			ItemStack heldItem = player.getItemInHand(hand);
			if (Ingredient.of(ModTags.LIGHTBULB).test(heldItem)) {
				boolean lightAdded = socket.addLightBulb(heldItem);
				if (lightAdded && !world.isClientSide() && !player.isCreative()) {
					heldItem.shrink(1);
				}
				world.playSound(player, pos, SoundEvents.SPYGLASS_USE, SoundSource.BLOCKS, 0.5f, 1.5f);
				world.playSound(player, pos, SoundEvents.AMETHYST_BLOCK_PLACE, SoundSource.BLOCKS, 0.25f, 1.5f);
				return InteractionResult.SUCCESS;
			}
		}

		return InteractionResult.PASS;

	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean isShowingAdvanced) {
		super.getTooltip(stack, worldIn, tooltip, isShowingAdvanced);

	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void getAdvancedTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip) {
		super.getAdvancedTooltip(stack, worldIn, tooltip);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		Direction facingDirection = state.getValue(FACING);
		BlockEntityLightSocket socketBe = (BlockEntityLightSocket) worldIn.getBlockEntity(pos);
		if (socketBe != null && socketBe.hasLightBulb()) {
			return Shapes.join(BULB_SHAPES.get(facingDirection), SHAPES.get(facingDirection), BooleanOp.OR);
		} else {
			return SHAPES.get(facingDirection);
		}
	}

	@Override
	public BlockState updateShape(BlockState p_153483_, Direction p_153484_, BlockState p_153485_, LevelAccessor p_153486_, BlockPos p_153487_, BlockPos p_153488_) {
		return !p_153483_.canSurvive(p_153486_, p_153487_) ? Blocks.AIR.defaultBlockState() : super.updateShape(p_153483_, p_153484_, p_153485_, p_153486_, p_153487_, p_153488_);
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState p_153494_) {
		return PushReaction.DESTROY;
	}

	@Override
	public boolean canSurvive(BlockState p_57499_, LevelReader p_57500_, BlockPos p_57501_) {
		Direction mountedSide = p_57499_.getValue(FACING).getOpposite();
		return !p_57500_.getBlockState(p_57501_.relative(mountedSide)).isAir();
	}

	@Override
	public DirectionProperty getFacingType() {
		return FACING;
	}

	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
		BlockEntityLightSocket socketBe = (BlockEntityLightSocket) world.getBlockEntity(pos);
		if (socketBe != null && socketBe.hasLightBulb()) {
			Direction facingDirection = state.getValue(FACING);
			AABB bulbShape = BULB_SHAPES.get(facingDirection).bounds();
			AABB socketShape = SHAPES.get(facingDirection).bounds();
			Pair<Vec3, Vec3> vec = RaytracingUtilities.getVectors(player);
			AdvancedRayTraceResult<BlockHitResult> result = RaytracingUtilities.collisionRayTrace(pos, vec.getLeft(), vec.getRight(), List.of(bulbShape, socketShape));
			if (result.getType() == HitResult.Type.BLOCK && result.bounds == bulbShape) {
				return socketBe.getLightbulb().copy();
			}
		}
		return super.getCloneItemStack(state, target, world, pos, player);
	}

	@Override
	public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
		BlockEntity te = world.getBlockEntity(pos);
		if (te instanceof BlockEntityLightSocket) {
			return ((BlockEntityLightSocket) te).isLit() ? 15 : 0;
		}
		return super.getLightEmission(state, world, pos);
	}

	@Override
	public boolean hasModelOverride(BlockState state) {
		return true;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BakedModel getModelOverride(BlockState state, @Nullable BakedModel existingModel, ModelEvent.BakingCompleted event) {
		return new LightSocketModel(existingModel);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public RenderType getRenderType() {
		return RenderType.translucent();
	}

	@Override
	public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
		return BlockEntityLightSocket.TYPE.create(pos, state);
	}
}
