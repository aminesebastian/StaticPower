package theking530.staticpower.items.book;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import theking530.staticpower.StaticPower;
import theking530.staticpower.client.GuiIDRegistry;
import theking530.staticpower.items.ItemBase;

public class StaticPowerBook extends ItemBase{

	public StaticPowerBook(String name) {
		super(name);
	}
	/**
	@Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStack, World world, EntityPlayer player, EnumHand hand) {
		if (!world.isRemote) {
			return new ActionResult(EnumActionResult.PASS, itemStack);
		}else if (!player.isSneaking()) {
			FMLNetworkHandler.openGui(player, StaticPower.staticpower, GuiIDRegistry.guiIDStaticBook, world, 0, 0, 0);
			return new ActionResult(EnumActionResult.SUCCESS, itemStack);
		}else{
			return new ActionResult(EnumActionResult.FAIL, itemStack);
		}       
    }
    */
}
