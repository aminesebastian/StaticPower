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

public class StackUpgrade extends BaseUpgrade {

	public StackUpgrade(String name) {
		super(name, new Properties().stacksTo(1), UpgradeType.DIGISTORE_ATTACHMENT);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean showAdvanced) {
		tooltip.add(new TextComponent(ChatFormatting.GREEN + "Allows digistore attachments"));
		tooltip.add(new TextComponent(ChatFormatting.GREEN + "to move stacks at a time."));
	}
}