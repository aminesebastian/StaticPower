package theking530.staticpower.blocks.tree;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LogBlock;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import theking530.staticpower.blocks.StaticPowerItemBlock;
import theking530.staticpower.blocks.interfaces.IItemBlockProvider;

public class StaticPowerTreeLog extends LogBlock implements IItemBlockProvider {
	private final Block strippedVariant;

	public StaticPowerTreeLog(String name, MaterialColor verticalColorIn, Block strippedVariant, Properties properties) {
		super(verticalColorIn, properties);
		this.setRegistryName(name);
		this.strippedVariant = strippedVariant;
	}

	public StaticPowerTreeLog(String name, MaterialColor verticalColorIn, Properties properties) {
		this(name, verticalColorIn, null, properties);
	}

	@Deprecated
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		// If there is a striped variant defined.
		if (strippedVariant != null) {
			// If the player is holding an axe.
			if (player.getHeldItem(handIn).getToolTypes().contains(ToolType.AXE)) {
				// Update to the stripped variant.
				worldIn.setBlockState(pos, strippedVariant.getDefaultState());

				// Play the strip sound.
				worldIn.playSound(player, pos, SoundEvents.ITEM_AXE_STRIP, SoundCategory.BLOCKS, 1.0F, 1.0F);

				// Damage the held item.
				player.getHeldItem(handIn).damageItem(1, player, (p_220040_1_) -> {
					p_220040_1_.sendBreakAnimation(handIn);
				});

				// Return a success.
				return ActionResultType.SUCCESS;
			}
		}

		return ActionResultType.PASS;
	}

	@Override
	public BlockItem getItemBlock() {
		return new StaticPowerItemBlock(this);
	}
}
