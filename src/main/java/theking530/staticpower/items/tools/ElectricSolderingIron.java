package theking530.staticpower.items.tools;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
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
		// Should move to config, but 10SV per soldering operation.
		if (EnergyHandlerItemStackUtilities.getStoredPower(itemstack) >= 10000) {
			EnergyHandlerItemStackUtilities.drainPower(itemstack, 10000, false);
		}
		return false;
	}

	@Override
	public boolean canSolder(ItemStack itemstack) {
		return EnergyHandlerItemStackUtilities.getStoredPower(itemstack) >= 10000;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean showAdvanced) {
		tooltip.add(new TextComponent("Power per Operation: 10SV"));
		if (showAdvanced) {
			long energyStored = EnergyHandlerItemStackUtilities.getStoredPower(stack);
			long capacity = EnergyHandlerItemStackUtilities.getCapacity(stack);
			tooltip.add(new TextComponent("Power Stored: ").append(GuiTextUtilities.formatEnergyToString(energyStored, capacity)));
		}
	}
}
