package theking530.staticpower.items.tools.basictools;

import java.util.List;
import java.util.Set;

import org.lwjgl.input.Keyboard;

import com.google.common.collect.Sets;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import theking530.staticpower.StaticPower;
import theking530.staticpower.utils.EnumTextFormatting;


public class BaseAxe extends ItemTool {

    private static final Set<Block> EFFECTIVE_ON = Sets.newHashSet(new Block[] {Blocks.PLANKS, Blocks.BOOKSHELF, Blocks.LOG, Blocks.LOG2, Blocks.CHEST, Blocks.PUMPKIN, Blocks.LIT_PUMPKIN, Blocks.MELON_BLOCK, Blocks.LADDER, Blocks.WOODEN_BUTTON, Blocks.WOODEN_PRESSURE_PLATE});

	public String NAME = "";
	public ToolMaterial MATERIAL;
	
	public BaseAxe(ToolMaterial material, String unlocalizedName) {
        super(material, EFFECTIVE_ON);
		NAME= unlocalizedName;
		setUnlocalizedName(unlocalizedName);
		setRegistryName(unlocalizedName);
		setCreativeTab(StaticPower.StaticPower);
		
		MATERIAL = material;
        this.damageVsEntity = material.getDamageVsEntity();
        this.attackSpeed = -3.0F;
	}
	
	@Override  
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean par4) {
    	if(showHiddenTooltips()) {
    		String tempLevel = "Mining Level: " +  MATERIAL.getHarvestLevel();
    		String tempSpeed = "Speed: " +  MATERIAL.getEfficiencyOnProperMaterial();
    		String tempDamage = "Damage: " + (Math.round(MATERIAL.getDamageVsEntity()) + 1); 
    		String tempDurability = "Durability: " + (MATERIAL.getMaxUses()-itemstack.getMetadata()) + "/" + MATERIAL.getMaxUses();
    		
    		list.add(tempLevel);
    		list.add(tempSpeed);
    		list.add(tempDurability);
    		list.add(tempDamage);
    	}else{
    		list.add(EnumTextFormatting.ITALIC + "Hold Shift");
	    }
	}
	public boolean showHiddenTooltips() {
		return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
	}
	
    public float getStrVsBlock(ItemStack stack, IBlockState state) {
        Material material = state.getMaterial();
        return material != Material.WOOD && material != Material.PLANTS && material != Material.VINE ? super.getStrVsBlock(stack, state) : this.efficiencyOnProperMaterial;
    }
}