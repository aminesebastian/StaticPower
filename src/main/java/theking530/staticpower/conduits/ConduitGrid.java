package theking530.staticpower.conduits;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import theking530.staticpower.StaticPower;

public class ConduitGrid<T extends TileEntityBaseConduit> {

	public HashMap<BlockPos, T> GRID_MAP;
	public HashMap<BlockPos, TileEntity> RECIEVER_STORAGE_MAP;
	
	private World WORLD;
	private int GRID_ID;
	private float[] GRID_COLOR;
	public boolean INVALID;

	public ConduitGrid(World world) {
		WORLD = world;
		GRID_MAP = new HashMap<BlockPos, T>();
		RECIEVER_STORAGE_MAP = new HashMap<BlockPos, TileEntity>();
		Random rand = new Random();
		GRID_ID = rand.nextInt();
		GRID_COLOR = new float[]{rand.nextFloat(), rand.nextFloat(), rand.nextFloat()};
		INVALID = false;
		
		StaticPower.GRIDS.add(this);
	}
	public float[] getColor() {
		return GRID_COLOR;		
	}	
	public void Tick() {
		
	}
	public ConduitPath doBFSShortestPath(BlockPos source, BlockPos dest) {
		ArrayList<BlockPos> shortestPathList = new ArrayList<BlockPos>();
		HashMap<BlockPos, Boolean> visited = new HashMap<BlockPos, Boolean>();

		if (source == dest)
			return null;
		Queue<BlockPos> queue = new LinkedList<BlockPos>();
		Stack<BlockPos> pathStack = new Stack<BlockPos>();

		queue.add(source);
		pathStack.add(source);
		visited.put(source, true);

		while(!queue.isEmpty())
		{
			BlockPos u = queue.poll();
			ArrayList<BlockPos> adjList = GetValidAdjacentTiles(u);

			if(adjList.contains(dest)) {
				break;
			}
			
			for(BlockPos v : adjList)
			{
				if(!visited.containsKey(v))
				{
					queue.add(v);
					visited.put(v, true);
					pathStack.add(v);
					if(u == dest)
						break;
				}
			}
		}


		//To find the path
		BlockPos node, currentSrc=dest;
		shortestPathList.add(dest);
		while(!pathStack.isEmpty())
		{
			node = pathStack.pop();
			if(GetValidAdjacentTiles(currentSrc).contains(node))
			{
				shortestPathList.add(node);
				currentSrc = node;
				if(node == source)
					break;
			}
		}
		Collections.reverse(shortestPathList);
		//System.out.println(shortestPathList);
		return new ConduitPath(shortestPathList);
	}
    public ArrayList<BlockPos> GetValidAdjacentTiles(BlockPos currentPosition) {
    	ArrayList<BlockPos> adjacentList = new ArrayList<BlockPos>();
    	for(int i=0; i<6; i++) {
    		BlockPos test = currentPosition.offset(EnumFacing.values()[i]);
    		if(GRID_MAP.containsKey(test) || RECIEVER_STORAGE_MAP.containsKey(test)) {
    			adjacentList.add(test);
    		}
    	}
    	return adjacentList;
    }

	public List<BlockPos> GatherPath(BlockPos fromPos, BlockPos toPos) {	
		List<BlockPos> path = new ArrayList<BlockPos>();
		path.add(fromPos);
		
		if(fromPos.equals(toPos)) {
			return path;
		}

		List<BlockPos> visited = new ArrayList<BlockPos>();
		visited.add(fromPos);
		
		List<BlockPos> gatherResult = worker_GatherPath(fromPos, toPos, visited);
		
		if(gatherResult != null){
			for(int j=0; j<gatherResult.size(); j++) {
				path.add(gatherResult.get(j));
			}
			return path;
		}

		
		return gatherResult;	
	}
	private List<BlockPos> worker_GatherPath(BlockPos currentBlock, BlockPos toPos, List<BlockPos> visitedBlocks) {
		for(int i=0; i<6; i++) {
			BlockPos child = currentBlock.offset(EnumFacing.values()[i]);

			if(visitedBlocks.contains(child)) {
				continue;
			}		
			
			visitedBlocks.add(child);
			
			if(!(RECIEVER_STORAGE_MAP.containsKey(child)) && !(GRID_MAP.containsKey(child))) {
				continue;	
			}
			
			List<BlockPos> tempPath = new LinkedList<BlockPos>();
			
			if(child.equals(toPos)) {
				tempPath.add(child);
				return tempPath;
			}
			if(WORLD.getTileEntity(child) != null) {
				TileEntity te = WORLD.getTileEntity(child);
				if(te instanceof TileEntityBaseConduit) {
					tempPath.add(child);
					List<BlockPos> recursiveResult = worker_GatherPath(child, toPos, visitedBlocks);
					if(recursiveResult != null){
						for(int j=0; j<recursiveResult.size(); j++) {
							tempPath.add(recursiveResult.get(j));
						}
						return tempPath;
					}
				}
			}
		}
		return null;
	}
	@SuppressWarnings("unchecked")
	public boolean AddEntry(TileEntity entity) {
		if(entity == null) {
			return false;
		}
		if(entity instanceof TileEntityBaseConduit) {
			if(GRID_MAP.containsKey(entity.getPos())) {
				return false;
			}
			GRID_MAP.put(entity.getPos(), (T)entity);
		}else{
			if(RECIEVER_STORAGE_MAP.containsKey(entity.getPos())) {
				return false;
			}
			RECIEVER_STORAGE_MAP.put(entity.getPos(), entity);
		}
		return false;
	}
	public int GetGridSize() {
		return GRID_MAP.size();
	}
	public int GetEnergyStorageMapSize() {
		return RECIEVER_STORAGE_MAP.size();
	}
	@Override
	public String toString() {
		return "Grid Size: " + GetGridSize() + " Reciever Count: " + GetEnergyStorageMapSize() + " Grid ID: " + GRID_ID;	
	}
	
	public class GridReciever {
		private BlockPos Position;
		private EnumFacing Face;
		
		public GridReciever(BlockPos position, EnumFacing face) {
			Position = position;
			Face = face;
		}

		public BlockPos getPOSITION() {
			return Position;
		}
		public EnumFacing getFACE() {
			return Face;
		}
	}
}

