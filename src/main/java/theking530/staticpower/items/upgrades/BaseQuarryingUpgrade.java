package theking530.staticpower.items.upgrades;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import theking530.staticpower.StaticPower;
import theking530.staticpower.assists.Tier;
import theking530.staticpower.utils.EnumTextFormatting;

public class BaseQuarryingUpgrade extends BaseUpgrade{

	public int BLOCKS_PER_TICK = 1;
	public float POWER_MULT = 1f;
	
	public BaseQuarryingUpgrade(String name, Tier tier){
		setCreativeTab(StaticPower.StaticPower);
		setUnlocalizedName(name);
		setRegistryName(name);	
		switch(tier) {
			case STATIC:
				BLOCKS_PER_TICK = 2;
				POWER_MULT = 2f;
				break;
			case ENERGIZED:
				BLOCKS_PER_TICK = 3;
				POWER_MULT = 3f;
				break;
			case LUMUM:
				BLOCKS_PER_TICK = 5;
				POWER_MULT = 5f;
				break;
		default:
			break;
		}
	}
	
	@Override  
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
		list.add(EnumTextFormatting.WHITE +  "Mines " + BLOCKS_PER_TICK + EnumTextFormatting.GREEN + " Blocks Per Tick");
		list.add(EnumTextFormatting.WHITE + new java.text.DecimalFormat("#").format((POWER_MULT-1)*100) + "%" + EnumTextFormatting.RED + " Power Use");
    }
	
}
