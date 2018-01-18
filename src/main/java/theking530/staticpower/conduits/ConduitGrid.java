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

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import theking530.staticpower.conduits.staticconduit.TileEntityStaticConduit;

public class ConduitGrid {

	public HashMap<BlockPos, TileEntity> GRID_MAP;
	public HashMap<BlockPos, TileEntity> ENERGY_STORAGE_MAP;
	
	private World WORLD;
	private int GRID_ID;
	private float[] GRID_COLOR;
	

	public ConduitGrid(World world) {
		WORLD = world;
		GRID_MAP = new HashMap<BlockPos, TileEntity>();
		ENERGY_STORAGE_MAP = new HashMap<BlockPos, TileEntity>();
		Random rand = new Random();
		GRID_ID = rand.nextInt();
		GRID_COLOR = new float[]{rand.nextFloat(), rand.nextFloat(), rand.nextFloat()};
	}
	public float[] getColor() {
		return GRID_COLOR;		
	}	
	public NBTTagCompound Serialize(){
		NBTTagCompound parent = new NBTTagCompound();
		int count = 0;
	    Iterator<Entry<BlockPos, TileEntity>> it = GRID_MAP.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<BlockPos, TileEntity> pair = (Map.Entry<BlockPos, TileEntity>)it.next();
	        
	        NBTTagCompound temp = new NBTTagCompound();
	        temp.setIntArray("POS", new int[]{pair.getKey().getX(), pair.getKey().getY(), pair.getKey().getZ()});
	        
	        System.out.println(pair.getKey() + " = " + pair.getValue());
	        parent.setTag("BLOCK_" + count, temp);
	        count++;
	    }    
	    parent.setInteger("COUNT", count);
	    parent.setInteger("GRID_ID", GRID_ID);	   
	    
	    return parent;
	}
	public void Deserialize(NBTTagCompound tag) {
		int originalCount = tag.getInteger("COUNT");

		for(int i=0; i<originalCount; i++) {
			NBTTagCompound blockTag = tag.getCompoundTag("BLOCK_" + i);
			int[] posArray = blockTag.getIntArray("POS");
			BlockPos tempPos = new BlockPos(posArray[0], posArray[1], posArray[2]);
			if(WORLD.getTileEntity(tempPos) != null) {
				GRID_MAP.put(tempPos, WORLD.getTileEntity(tempPos));
			}
		}
		GRID_ID = tag.getInteger("GRID_ID");
	}
    public List<BlockPos> GatherShortestPath(BlockPos fromPos, BlockPos toPos) {
        LinkedList<BlockPos> bfsList = new LinkedList<>();
        Queue<BlockPos> queue = new LinkedList<>();

        List<BlockPos> visited = new ArrayList<BlockPos>();
        BlockPos current = fromPos;

        List<BlockPos> prev = new ArrayList<BlockPos>();
        
        queue.add(current);
        visited.add(current);

        while (!queue.isEmpty()) {

            current = queue.remove();

            if (current.equals(toPos)) {
                break;
            } else {
                LinkedList<BlockPos> currentFriends = GetValidAdjacentTiles(current);
                for (BlockPos currentFriend : currentFriends) {
                    if (!visited.contains(currentFriend)) {
                        queue.add(currentFriend);
                        visited.add(currentFriend);
                        prev.add(current);
                    }
                }
            }
        }

        if (!current.equals(toPos)) {
            System.out.println("\nThere is no path between " + current + " and " + toPos);
            return Collections.emptyList();
        }

        Collections.reverse(bfsList);

        return prev;

    }
    public LinkedList<BlockPos> GetValidAdjacentTiles(BlockPos currentPosition) {
    	LinkedList<BlockPos> adjacentList = new LinkedList<BlockPos>();
    	for(int i=0; i<6; i++) {
    		BlockPos test = currentPosition.offset(EnumFacing.values()[i]);
    		if(GRID_MAP.containsKey(test) || ENERGY_STORAGE_MAP.containsKey(test)) {
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

		List<BlockPos> checked = new ArrayList<BlockPos>();
		checked.add(fromPos);
		
		List<BlockPos> gatherResult = worker_GatherPath(fromPos, toPos, checked);
		
		if(gatherResult != null){
			for(int j=0; j<gatherResult.size(); j++) {
				path.add(gatherResult.get(j));
			}
			return path;
		}

		
		return  worker_GatherPath(fromPos, toPos, checked);	
	}
	private List<BlockPos> worker_GatherPath(BlockPos currentBlock, BlockPos toPos, List<BlockPos> checkedBlocks) {
		for(int i=0; i<6; i++) {
			BlockPos child = currentBlock.offset(EnumFacing.values()[i]);

			if(checkedBlocks.contains(child)) {
				continue;
			}		
			
			checkedBlocks.add(child);
			
			if(!(ENERGY_STORAGE_MAP.containsKey(child)) && !(GRID_MAP.containsKey(child))) {
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
					List<BlockPos> recursiveResult = worker_GatherPath(child, toPos, checkedBlocks);
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
	public boolean AddEntry(TileEntity entity) {
		if(entity == null) {
			return false;
		}
		if(entity instanceof TileEntityBaseConduit) {
			if(GRID_MAP.containsKey(entity.getPos())) {
				return false;
			}
			GRID_MAP.put(entity.getPos(), entity);
		}else{
			if(ENERGY_STORAGE_MAP.containsKey(entity.getPos())) {
				return false;
			}
			ENERGY_STORAGE_MAP.put(entity.getPos(), entity);
		}
		return false;
	}
	public int GetGridSize() {
		return GRID_MAP.size();
	}
	public int GetEnergyStorageMapSize() {
		return ENERGY_STORAGE_MAP.size();
	}
	@Override
	public String toString() {
		return "Grid Size: " + GetGridSize() + "  Energy Storage Size: " + GetEnergyStorageMapSize() + " Grid ID: " + GRID_ID;	
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

