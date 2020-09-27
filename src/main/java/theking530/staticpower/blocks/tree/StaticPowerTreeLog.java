package theking530.staticpower.blocks.tree;

import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import theking530.staticcore.utilities.SDMath;
import theking530.staticpower.blocks.StaticPowerItemBlock;
import theking530.staticpower.blocks.StaticPowerRotatePillarBlock;
import theking530.staticpower.utilities.WorldUtilities;

public class StaticPowerTreeLog extends StaticPowerRotatePillarBlock {
	private final Block strippedVariant;
	private final Supplier<Integer> minBark;
	private final Supplier<Integer> maxBark;
	private final Supplier<Item> barkItemSupplier;

	public StaticPowerTreeLog(String name, Block strippedVariant, Properties properties, Supplier<Integer> minBark,
			Supplier<Integer> maxBark, Supplier<Item> barkItem) {
		super(name, properties);
		this.strippedVariant = strippedVariant;
		this.minBark = minBark;
		this.maxBark = maxBark;
		this.barkItemSupplier = barkItem;
	}

	public StaticPowerTreeLog(String name, MaterialColor verticalColorIn, Block strippedVariant,
			Properties properties) {
		this(name, strippedVariant, properties, () -> 0, () -> 0, () -> null);
	}

	public StaticPowerTreeLog(String name, MaterialColor verticalColorIn, Properties properties) {
		this(name, null, properties, () -> 0, () -> 0, () -> null);
	}

	@Deprecated
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player,
			Hand handIn, BlockRayTraceResult hit) {
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

				// Spawn the bark if needed.
				Item barkItem = barkItemSupplier.get();
				if (barkItem != null && !worldIn.isRemote) {
					// Get the amount to spawn.
					int barkAmount = SDMath.getRandomIntInRange(minBark.get(), maxBark.get());
					if (barkAmount > 0) {
						ItemStack barkStack = new ItemStack(barkItem, barkAmount);
						WorldUtilities.dropItem(worldIn, pos.offset(hit.getFace()), barkStack);
					}
				}

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
