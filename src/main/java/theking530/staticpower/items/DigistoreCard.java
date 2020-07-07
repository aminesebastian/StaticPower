package theking530.staticpower.items;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticpower.data.StaticPowerDataRegistry;

public class DigistoreCard extends StaticPowerItem {
	public final ResourceLocation tierType;

	public DigistoreCard(String name, ResourceLocation tierType) {
		super(name);
		this.tierType = tierType;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void getAdvancedTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
		tooltip.add(new StringTextComponent("Stores: ").appendText(String.valueOf(StaticPowerDataRegistry.getTier(tierType).getDigistoreCapacity() / 64)).appendText(" Stacks"));
	}
}
