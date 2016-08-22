package theking530.staticpower.blocks;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import theking530.staticpower.StaticPower;

public class StaticGrass extends Block{
	
	public StaticGrass(Material material) {
		super(material.GRASS);
		this.setSoundType(SoundType.GROUND);
		this.setLightLevel(0.5F);
		this.setTickRandomly(true);
		this.setHardness(0.6F);
		this.setCreativeTab(StaticPower.StaticPower);
		this.setUnlocalizedName("StaticGrass");	
		setRegistryName("StaticGrass");
	}
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
	    list.add("Take a Leap.");
	    list.add("Take Flight.");
	}
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Blocks.DIRT.getItemDropped(state, rand, fortune);
    }		
	public void onEntityWalking(World world, int par2, int par3, int par4, Entity entity) {
		if(entity instanceof EntityLivingBase){
			//((EntityLivingBase) entity).addPotionEffect(new PotionEffect(Potion.jump.getId(), 20, 2));
		}
	}
}