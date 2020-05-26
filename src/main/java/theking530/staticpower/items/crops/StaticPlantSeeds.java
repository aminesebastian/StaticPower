package theking530.staticpower.items.crops;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class StaticPlantSeeds extends CropSeeds {

    public StaticPlantSeeds(String name, Block blockCrop) {
        super(name, blockCrop);
    }
	@Override
	@OnlyIn(Dist.CLIENT)
	protected void getBasicTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
		tooltip.add(new StringTextComponent("These Seeds Radiate with"));
		tooltip.add(new StringTextComponent("a Strange Energy..."));
	}
}