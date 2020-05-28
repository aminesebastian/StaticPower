package theking530.staticpower.items.upgrades;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticpower.utilities.Tier;

public class TeleportUpgrade  extends BaseUpgrade implements IMachineUpgrade{

	public  TeleportUpgrade(String name){
		super(name, Tier.ENERGIZED, new Properties().maxStackSize(1));
	}
	@Override
	public float getUpgradeValueAtIndex(ItemStack stack, int upgradeNumber) {
		return 0.0f;
	}
	@Override
	@OnlyIn(Dist.CLIENT)
	protected void getAdvancedTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
		tooltip.add(new StringTextComponent(TextFormatting.GREEN + "Allows objects with"));
		tooltip.add(new StringTextComponent(TextFormatting.GREEN + "vacuum effects to"));
		tooltip.add(new StringTextComponent(TextFormatting.GREEN + "instantly teleport items."));
		
		tooltip.add(new StringTextComponent(TextFormatting.WHITE + "Stacks Up To: " + stack.getMaxStackSize()));
	}
}