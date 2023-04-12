package theking530.staticcore.item;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.StaticCoreItemGroup;
import theking530.staticcore.research.gui.ResearchManager;
import theking530.staticcore.teams.ITeam;
import theking530.staticcore.teams.TeamManager;
import theking530.staticcore.utilities.SDColor;

public class ResearchItem extends StaticCoreItem {
	private final SDColor color;
	private final int researchTier;

	public ResearchItem(SDColor color, int tier) {
		super(new Item.Properties().tab(StaticCoreItemGroup.instance()));
		this.color = color;
		this.researchTier = tier;
	}

	public SDColor getColor() {
		return color;
	}

	public int getResearchTier() {
		return researchTier;
	}

	protected InteractionResultHolder<ItemStack> onStaticPowerItemRightClicked(Level world, Player player, InteractionHand hand, ItemStack item) {
		// Can only use to research with tier 1 research. Perform on both client and
		// server. If server is stale, it will get re-synced regardless.
		if (!world.isClientSide()) {
			if (researchTier == 1) {
				ITeam team = TeamManager.get(world).getTeamForPlayer(player);
				if (team != null) {
					ResearchManager manager = team.getResearchManager();
					if (manager.hasSelectedResearch() && !manager.getSelectedResearch().isCompleted()) {
						int index = manager.getSelectedResearch().getRequirementIndexFullfilledByItem(item);
						if (index >= 0) {
							player.startUsingItem(hand);
							return InteractionResultHolder.consume(item);
						}
					}
				}
			}
		}

		return InteractionResultHolder.pass(player.getItemInHand(hand));
	}

	public ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity entityLiving) {
		if (!worldIn.isClientSide()) {
			if (entityLiving instanceof Player) {
				Player player = (Player) entityLiving;
				ITeam team = TeamManager.get(worldIn).getTeamForPlayer(player);
				if (team != null) {
					ResearchManager manager = team.getResearchManager();
					if (manager.hasSelectedResearch() && !manager.getSelectedResearch().isCompleted()) {
						int index = manager.getSelectedResearch().getRequirementIndexFullfilledByItem(stack);
						if (index >= 0) {
							if (!worldIn.isClientSide()) {
								manager.addProgressToSelectedResearch(index, 1);
							}
							stack.shrink(1);
							player.getCooldowns().addCooldown(stack.getItem(), 8);
							worldIn.playSound(null, player.getOnPos(), SoundEvents.BOOK_PAGE_TURN, SoundSource.MASTER, 1.0f, 1.5f);
							return stack;
						}
					}
				}
			}
		}
		return stack;
	}

	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.BOW;
	}

	public int getUseDuration(ItemStack stack) {
		return 20;
	}

	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean isShowingAdvanced) {
		if (researchTier == 1) {
			tooltip.add(Component.translatable("Use to submit research!"));
		}
	}

}
