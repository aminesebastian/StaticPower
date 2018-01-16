package theking530.staticpower.items.book;

import theking530.staticpower.items.ItemBase;

public class StaticPowerBook extends ItemBase{

	public StaticPowerBook(String name) {
		super(name);
	}
	/**
	@Override
        public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
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
