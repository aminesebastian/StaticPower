package theking530.staticcore.utilities;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface ITooltipProvider {
	/**
	 * Gets the tooltip that is displayed when hovered by the user.
	 * 
	 * @param stack             The item stack hovered by the user.
	 * @param worldIn           The world the player was in when hovering the item.
	 * @param tooltip           The list of {@link ITextComponent} to add to the
	 *                          tooltip.
	 * @param isShowingAdvanced True if advanced tooltips are requested.
	 */
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean isShowingAdvanced);

	@OnlyIn(Dist.CLIENT)
	public void getAdvancedTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip);
}
