package theking530.staticpower.items.tools;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.items.StaticPowerEnergyStoringItem;
import theking530.staticpower.items.utilities.EnergyHandlerItemStackUtilities;

public class ElectricSolderingIron extends StaticPowerEnergyStoringItem implements ISolderingIron {

	public ElectricSolderingIron(String name, int capacity) {
		super(name, capacity);
	}

	@Override
	public boolean useSolderingItem(ItemStack itemstack) {
		if (EnergyHandlerItemStackUtilities.getEnergyStored(itemstack) >= 100) {
			EnergyHandlerItemStackUtilities.useEnergyFromItemstack(itemstack, 100, false);
		}
		return false;
	}

	@Override
	public boolean canSolder(ItemStack itemstack) {
		return EnergyHandlerItemStackUtilities.getEnergyStored(itemstack) >= 100;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void getBasicTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
		tooltip.add(new StringTextComponent("Power per Operation: 100RF"));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void getAdvancedTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
		int energyStored = EnergyHandlerItemStackUtilities.getEnergyStored(stack);
		int capacity = EnergyHandlerItemStackUtilities.getEnergyStorageCapacity(stack);
		tooltip.add(new StringTextComponent("Power Stored: " + GuiTextUtilities.formatEnergyToString(energyStored, capacity)));
	}
}
