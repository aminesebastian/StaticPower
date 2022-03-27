package theking530.staticpower.blocks.tree;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.BlockHitResult;
import theking530.staticcore.utilities.SDMath;
import theking530.staticpower.blocks.StaticPowerItemBlock;
import theking530.staticpower.blocks.StaticPowerRotatePillarBlock;
import theking530.staticpower.init.ModResearch;
import theking530.staticpower.teams.Team;
import theking530.staticpower.teams.TeamManager;
import theking530.staticpower.utilities.WorldUtilities;

public class StaticPowerTreeLog extends StaticPowerRotatePillarBlock {
	private final Block strippedVariant;
	private final Supplier<Integer> minBark;
	private final Supplier<Integer> maxBark;
	private final Supplier<Item> barkItemSupplier;

	public StaticPowerTreeLog(String name, Block strippedVariant, Properties properties, Supplier<Integer> minBark, Supplier<Integer> maxBark, Supplier<Item> barkItem) {
		super(name, properties);
		this.strippedVariant = strippedVariant;
		this.minBark = minBark;
		this.maxBark = maxBark;
		this.barkItemSupplier = barkItem;
	}

	public StaticPowerTreeLog(String name, MaterialColor verticalColorIn, Block strippedVariant, Properties properties) {
		this(name, strippedVariant, properties, () -> 0, () -> 0, () -> null);
	}

	public StaticPowerTreeLog(String name, MaterialColor verticalColorIn, Properties properties) {
		this(name, null, properties, () -> 0, () -> 0, () -> null);
	}

	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
		// If there is a striped variant defined.
		if (handIn == InteractionHand.MAIN_HAND && strippedVariant != null) {
			Team team = TeamManager.get().getTeamForPlayer(player);
			if (team != null) {
				if (team.getResearchManager().hasCompletedResearch(ModResearch.RUBBER_WOOD_STRIPPING)) {
					// If the player is holding an axe.
					if (player.getItemInHand(handIn).isCorrectToolForDrops(state)) {
						// Update to the stripped variant.
						worldIn.setBlockAndUpdate(pos, strippedVariant.defaultBlockState());

						// Play the strip sound.
						worldIn.playSound(player, pos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0F, 1.0F);

						// Damage the held item.
						player.getItemInHand(handIn).hurtAndBreak(1, player, (p_220040_1_) -> {
							p_220040_1_.broadcastBreakEvent(handIn);
						});

						// Spawn the bark if needed.
						Item barkItem = barkItemSupplier.get();
						if (barkItem != null && !worldIn.isClientSide) {
							// Get the amount to spawn.
							int barkAmount = SDMath.getRandomIntInRange(minBark.get(), maxBark.get());
							if (barkAmount > 0) {
								ItemStack barkStack = new ItemStack(barkItem, barkAmount);
								WorldUtilities.dropItem(worldIn, pos.relative(hit.getDirection()), barkStack);
							}
						}

						// Return a success.
						return InteractionResult.SUCCESS;
					}
				} else {
					if (!worldIn.isClientSide()) {
						player.sendMessage(new TranslatableComponent("gui.missing_research"), player.getUUID());
					}
				}
			}
		}

		return InteractionResult.PASS;
	}

	@Override
	public BlockItem getItemBlock() {
		return new StaticPowerItemBlock(this);
	}
}
