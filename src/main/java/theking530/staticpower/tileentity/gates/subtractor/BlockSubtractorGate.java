package theking530.staticpower.tileentity.gates.subtractor;

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

public class BlockSubtractorGate extends BlockLogicGate {

	public BlockSubtractorGate(String name) {
		super(name, 0);
	}
	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TileEntitySubtractorGate();
	}
	public String getDescrption(){
		return "Subtract one signal from the other.";	
	}
	public String getInputDescrption(){
		return "First Operand.";
	}
	public String getOutputDescrption(){
		return "Difference of the Purple input minus the Blue input.";
	}
	public String getExtraDescrption(){
		return "Second Operand.";
	}
}
