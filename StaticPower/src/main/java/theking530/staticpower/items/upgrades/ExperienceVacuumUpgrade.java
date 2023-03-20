package theking530.staticpower.items.upgrades;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.api.IUpgradeItem;
import theking530.staticpower.init.ModUpgradeTypes;

public class ExperienceVacuumUpgrade extends BaseUpgrade implements IUpgradeItem {

	public ExperienceVacuumUpgrade() {
		super(new Properties().stacksTo(1));
		addUpgrade(ModUpgradeTypes.EXPERIENCE_VACUUM.get(), (type, item) -> true);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean showAdvanced) {
		if (showAdvanced) {
			tooltip.add(Component.literal(ChatFormatting.GREEN + "Allows objects with"));
			tooltip.add(Component.literal(ChatFormatting.GREEN + "vacuum effects to"));
			tooltip.add(Component.literal(ChatFormatting.GREEN + "vacuum experience."));

			tooltip.add(Component.literal(ChatFormatting.WHITE + "Stacks Up To: " + stack.getMaxStackSize()));
		}
	}
}
