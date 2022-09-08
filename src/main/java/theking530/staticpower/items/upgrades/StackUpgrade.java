package theking530.staticpower.items.upgrades;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.api.upgrades.UpgradeTypes;

public class StackUpgrade extends BaseUpgrade {

	public StackUpgrade() {
		super(new Properties().stacksTo(1), UpgradeTypes.DIGISTORE_ATTACHMENT);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean showAdvanced) {
		tooltip.add(new TextComponent(ChatFormatting.GREEN + "Allows digistore attachments"));
		tooltip.add(new TextComponent(ChatFormatting.GREEN + "to move stacks at a time."));
	}
}