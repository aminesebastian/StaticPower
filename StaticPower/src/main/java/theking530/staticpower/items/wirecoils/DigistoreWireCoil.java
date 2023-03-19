package theking530.staticpower.items.wirecoils;

import java.util.List;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.cablenetwork.modules.CableNetworkModuleType;
import theking530.staticcore.utilities.SDColor;

public class DigistoreWireCoil extends AbstractWireCoil {

	public DigistoreWireCoil(SDColor wireColor, float wireThickness, boolean isInsulated, ResourceLocation tier, Supplier<CableNetworkModuleType> cableModuleType) {
		super(wireColor, wireThickness, isInsulated, tier, cableModuleType);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean showAdvanced) {
		super.getTooltip(stack, worldIn, tooltip, showAdvanced);
	}
}
