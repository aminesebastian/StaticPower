package theking530.staticpower.tileentity.gates;

import java.util.List;

import org.lwjgl.input.Keyboard;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import theking530.staticpower.blocks.BaseItemBlock;
import theking530.staticpower.utils.EnumTextFormatting;

public class ItemLogicGate extends BaseItemBlock{

	public String DESC;
	public String IN;
	public String OUT;
	public String EXTRA;
	
	public ItemLogicGate(Block block, String name) {
		super(block, name);
		BlockLogicGate tempGate = (BlockLogicGate)block;
		DESC = tempGate.getDescrption();
		IN = tempGate.getInputDescrption();
		OUT = tempGate.getOutputDescrption();
		EXTRA = tempGate.getExtraDescrption();
	}
	public boolean showHiddenTooltips() {
		return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
	}
	@Override  
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean par4) {
		String input = EnumTextFormatting.BOLD + "" + EnumTextFormatting.BLUE + "Input: " + EnumTextFormatting.REGULAR + "" + EnumTextFormatting.WHITE;
		String output = EnumTextFormatting.BOLD + "" + EnumTextFormatting.GOLD + "Output: " + EnumTextFormatting.REGULAR + "" + EnumTextFormatting.WHITE;
		String extra = EnumTextFormatting.BOLD + "" + EnumTextFormatting.DARK_PURPLE + "Extra Output: " + EnumTextFormatting.REGULAR + "" + EnumTextFormatting.WHITE;
		if(DESC != null) {
    		list.add(EnumTextFormatting.WHITE + DESC);	
		}
		if(showHiddenTooltips()) {
    		if(IN != null) {
	    		list.add(input + IN);	
    		}
    		if(OUT != null) {
	    		list.add(output + OUT);	
    		}
    		if(EXTRA != null) {
	    		list.add(extra + EXTRA);	
    		}
    	}else{
    		list.add(EnumTextFormatting.ITALIC +  "Hold Shift");
	    }
	}
}
