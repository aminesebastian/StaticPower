package theking530.staticpower.tileentity.digistorenetwork.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import theking530.staticpower.assists.utilities.SideModeList.Mode;
import theking530.staticpower.tileentity.digistorenetwork.BaseDigistoreTileEntity;
import theking530.staticpower.tileentity.digistorenetwork.digistore.TileEntityDigistore;
import theking530.staticpower.tileentity.digistorenetwork.networkextender.BlockDigistoreNetworkExtender;

public class DigistoreNetwork {
	
	private Map<BlockPos, BaseDigistoreTileEntity> masterDigistoreList;
	private List<TileEntityDigistore> digistoreList;
	private List<BlockPos> extenderPositions;
	
	private World world;
	private TileEntityDigistoreManager manager;
	
	public DigistoreNetwork(TileEntityDigistoreManager manager) {
		this.manager = manager;
		this.world = manager.getWorld();
	}
	
	public void updateGrid() {
		masterDigistoreList = createNewGrid();
		digistoreList = new ArrayList<TileEntityDigistore>();
		extenderPositions = new ArrayList<BlockPos>();
		masterDigistoreList.put(getPos(), manager);
		manager.setManager(manager);
		worker_updateGrid(masterDigistoreList, getPos()); 
	}
	private void worker_updateGrid(Map<BlockPos, BaseDigistoreTileEntity> grid, BlockPos currentPos) {
		for(EnumFacing facing : EnumFacing.values()) {
			BlockPos testPos = currentPos.offset(facing);
			TileEntity te = world.getTileEntity(testPos);
			if(te != null && !te.isInvalid() && te instanceof BaseDigistoreTileEntity) {
				if(!grid.containsKey(testPos)) {
					if(te instanceof TileEntityDigistoreManager) {
						TileEntityDigistoreManager oldManager = (TileEntityDigistoreManager)te;
						oldManager.masterDigistoreList = createNewGrid();
						continue;
					}
					BaseDigistoreTileEntity digistore = (BaseDigistoreTileEntity)te;
					digistore.setManager(manager);
					grid.put(testPos, digistore);
					sortTileEntity(te);
					worker_updateGrid(grid, testPos);
				}
			}else if(!extenderPositions.contains(testPos) && world.getBlockState(testPos).getBlock() instanceof BlockDigistoreNetworkExtender) {
				extenderPositions.add(testPos);
				worker_updateGrid(grid, testPos);
			}
		}
	}
	
	private void sortTileEntity(BaseDigistoreTileEntity te) {
		if(te instanceof TileEntityDigistore) {
			digistoreList.add((TileEntityDigistore)te);
		}
	}
}
