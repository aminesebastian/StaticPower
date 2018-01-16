package theking530.staticpower.tileentity.gates.timer;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import theking530.staticpower.client.GuiIDRegistry;
import theking530.staticpower.tileentity.gates.BlockLogicGate;

public class BlockTimer extends BlockLogicGate {

	public BlockTimer(String name) {
		super(name, GuiIDRegistry.guiTimer);
	}
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileEntityTimer();
	}
	public String getDescrption(ItemStack stack){
		return "Output a redstone pulse on every configurable amount of ticks.";	
	}
	public String getOutputDescrption(ItemStack stack){
		return "Pulse of strength 15.";
	}
}
