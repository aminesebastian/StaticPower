package theking530.staticpower.tileentities.nonpowered.conveyors.rampdown;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import theking530.staticpower.blocks.tileentity.StaticPowerMachineBlock;
import theking530.staticpower.blocks.tileentity.StaticPowerTileEntityBlock;
import theking530.staticpower.tileentities.nonpowered.conveyors.rampup.BlockRampUpConveyor;

public class BlockRampDownConveyor extends StaticPowerMachineBlock {
	private static VoxelShape ENTITY_SHAPE;
	private static VoxelShape INTERACTION_SHAPE;

	public BlockRampDownConveyor(String name) {
		super(name, Properties.create(Material.IRON, MaterialColor.BLACK).notSolid());
	}

	@Override
	public HasGuiType hasGuiScreen(TileEntity tileEntity, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		return HasGuiType.NEVER;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		Direction facingDirection = state.get(StaticPowerTileEntityBlock.FACING);
		if (context.getEntity() instanceof PlayerEntity) {
			INTERACTION_SHAPE = BlockRampUpConveyor.generateSlantedBoundingBox(facingDirection, 0.5f, -0.05f, 0.5f, 3.5f, 36.9f, false);
			return INTERACTION_SHAPE;
		} else {
			ENTITY_SHAPE = BlockRampUpConveyor.generateSlantedBoundingBox(facingDirection, 4.0f, -0.05f, 0.5f, 3.5f, 36.9f, false);
			return ENTITY_SHAPE;
		}
	}

	@Override
	public VoxelShape getRaytraceShape(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return ENTITY_SHAPE;
	}

	@Override
	public boolean hasModelOverride(BlockState state) {
		return false;
	}

	@Override
	public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
		return TileEntityRampDownConveyor.TYPE.create();
	}

	@Override
	public void getTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, boolean isShowingAdvanced) {
		if (!isShowingAdvanced) {
			tooltip.add(new TranslationTextComponent("gui.staticpower.experience_hopper_tooltip").mergeStyle(TextFormatting.GREEN));
		}
	}

	@Override
	public void getAdvancedTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
		tooltip.add(new StringTextComponent("• ").append(new TranslationTextComponent("gui.staticpower.experience_hopper_description")).mergeStyle(TextFormatting.BLUE));
	}
}
