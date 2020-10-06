package theking530.staticpower.items.tools.miningdrill;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.data.TierReloadListener;
import theking530.staticpower.items.StaticPowerItem;

public class DrillBit extends StaticPowerItem {
	public final ResourceLocation tier;

	public DrillBit(String name, ResourceLocation tier) {
		super(name, new Item.Properties().maxStackSize(1).maxDamage(1));
		this.tier = tier;
	}

	@Override
	public boolean hasEffect(ItemStack stack) {
		return this.tier == StaticPowerTiers.CREATIVE;
	}

	@Override
	public int getMaxDamage(ItemStack stack) {
		// Extra step here to ensure the correct tier is used.
		return TierReloadListener.getTier(((DrillBit) stack.getItem()).tier).getDrillBitUses();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void getAdvancedTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
		tooltip.add(new StringTextComponent("Max Blocks Drilled: " + getMaxDamage(stack)));
		tooltip.add(new StringTextComponent("Blocks Remaining: " + (getMaxDamage(stack) - getDamage(stack))));
	}
}
