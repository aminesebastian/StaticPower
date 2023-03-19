package theking530.staticpower.items.upgrades;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.upgrades.UpgradeTypes;

public class CraftingUpgrade extends BaseUpgrade {

	public CraftingUpgrade() {
		super(new Properties().stacksTo(1), UpgradeTypes.DIGISTORE_ATTACHMENT);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean showAdvanced) {
		tooltip.add(Component.literal(ChatFormatting.GREEN + "Allows digistore attachments"));
		tooltip.add(Component.literal(ChatFormatting.GREEN + "to craft items if needed."));
	}
}