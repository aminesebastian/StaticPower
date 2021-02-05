package theking530.staticpower.tileentities.nonpowered.conveyors.rampup;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import theking530.staticpower.tileentities.nonpowered.conveyors.AbstractConveyorBlock;

public class BlockRampUpConveyor extends AbstractConveyorBlock {
	public BlockRampUpConveyor(String name) {
		super(name, Properties.create(Material.IRON, MaterialColor.BLACK).notSolid());
	}

	@Override
	public void cacheVoxelShapes() {
		if (INTERACTION_SHAPES.isEmpty() || ENTITY_SHAPES.isEmpty()) {
			for (Direction dir : Direction.values()) {
				INTERACTION_SHAPES.put(dir, generateSlantedBoundingBox(dir, 0.15f, 16f, -4.35f, 3.5f, 45f, true));
				ENTITY_SHAPES.put(dir, generateSlantedBoundingBox(dir, 1.0f, 16f, -3.5f, 3.5f, 45f, true));
			}
		}
	}

	@Override
	public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
		return TileEntityRampUpConveyor.TYPE.create();
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
