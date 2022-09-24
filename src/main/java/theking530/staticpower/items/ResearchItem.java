package theking530.staticpower.items;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.utilities.Color;
import theking530.staticpower.teams.Team;
import theking530.staticpower.teams.TeamManager;
import theking530.staticpower.teams.research.ResearchManager;

public class ResearchItem extends StaticPowerItem {
	private final Color color;
	private final int researchTier;

	public ResearchItem(Color color, int tier) {
		super();
		this.color = color;
		this.researchTier = tier;
	}

	public Color getColor() {
		return color;
	}

	public int getResearchTier() {
		return researchTier;
	}

	protected InteractionResultHolder<ItemStack> onStaticPowerItemRightClicked(Level world, Player player, InteractionHand hand, ItemStack item) {
		// Can only use to research with tier 1 research. Perform on both client and
		// server. If server is stale, it will get re-synced regardless.
		if (researchTier == 1) {
			Team team = TeamManager.get().getTeamForPlayer(player);
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

		return InteractionResultHolder.pass(player.getItemInHand(hand));
	}

	public ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity entityLiving) {
		if (entityLiving instanceof Player) {
			Player player = (Player) entityLiving;
			Team team = TeamManager.get().getTeamForPlayer(player);
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
			tooltip.add(new TranslatableComponent("Use to submit research!"));
		}
	}

}
