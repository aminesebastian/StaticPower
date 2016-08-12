package theking530.staticpower;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import theking530.staticpower.items.ModItems;
 
public class CreativeTabStandard extends CreativeTabs {
 
    public CreativeTabStandard(int id, String unlocalizedName) {
 
        super(id, unlocalizedName);
    }
 
    @SideOnly(Side.CLIENT)
    public Item getTabIconItem() {
        return ModItems.EnergizedCrop;
    }
}