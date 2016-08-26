package theking530.staticpower.tileentity.gates.or;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import theking530.staticpower.StaticPower;
import theking530.staticpower.client.GuiIDRegistry;
import theking530.staticpower.tileentity.gates.BlockLogicGate;

public class BlockOrGate extends BlockLogicGate {

	public BlockOrGate(String name) {
		super(name, 0);
	}
	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TileEntityOrGate();
	}
	public String getDescrption(){
		return "Emits a redstone signal if any input is true.";	
	}
	public String getInputDescrption(){
		return "Input to evaluate.";
	}
	public String getOutputDescrption(){
		return "Regular OR output.";
	}
	public String getExtraDescrption(){
		return "Exclusive OR (XOR) output.";
	}
}
