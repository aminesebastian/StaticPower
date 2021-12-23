package theking530.staticpower.items.upgrades;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraft.world.item.Item.Properties;

public class TeleportUpgrade extends BaseUpgrade {

	public TeleportUpgrade(String name) {
		super(name, new Properties().stacksTo(1));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean showAdvanced) {
		if(showAdvanced) {
			tooltip.add(new TextComponent(ChatFormatting.GREEN + "Allows objects with"));
			tooltip.add(new TextComponent(ChatFormatting.GREEN + "vacuum effects to"));
			tooltip.add(new TextComponent(ChatFormatting.GREEN + "instantly teleport items."));

			tooltip.add(new TextComponent(ChatFormatting.WHITE + "Stacks Up To: " + stack.getMaxStackSize()));	
		}
	}
}