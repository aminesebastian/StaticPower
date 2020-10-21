package theking530.staticpower.items.tools;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.api.ISolderingIron;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.items.StaticPowerEnergyStoringItem;
import theking530.staticpower.items.utilities.EnergyHandlerItemStackUtilities;

public class ElectricSolderingIron extends StaticPowerEnergyStoringItem implements ISolderingIron {

	public ElectricSolderingIron(String name, int capacity) {
		super(name, capacity);
	}

	@Override
	public boolean useSolderingItem(ItemStack itemstack) {
		if (EnergyHandlerItemStackUtilities.getStoredPower(itemstack) >= 10) {
			EnergyHandlerItemStackUtilities.drainPower(itemstack, 10, false);
		}
		return false;
	}

	@Override
	public boolean canSolder(ItemStack itemstack) {
		return EnergyHandlerItemStackUtilities.getStoredPower(itemstack) >= 10;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void getTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, boolean showAdvanced) {
		tooltip.add(new StringTextComponent("Power per Operation: 100V"));
		if (showAdvanced) {
			int energyStored = EnergyHandlerItemStackUtilities.getStoredPower(stack);
			int capacity = EnergyHandlerItemStackUtilities.getCapacity(stack);
			tooltip.add(new StringTextComponent("Power Stored: ").append(GuiTextUtilities.formatEnergyToString(energyStored, capacity)));
		}
	}
}
