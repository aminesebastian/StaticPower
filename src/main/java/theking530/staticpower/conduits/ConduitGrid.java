package theking530.staticpower.conduits;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ConduitGrid {

	private HashMap<BlockPos, TileEntity> GRID_MAP;
	private World WORLD;
	private int GRID_ID;
	private float[] GRID_COLOR;
	
	public ConduitGrid(World world) {
		WORLD = world;
		GRID_MAP = new HashMap<BlockPos, TileEntity>();
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
	public List<BlockPos> GatherPath(BlockPos fromPos, BlockPos toPos) {
		return null;	
	}
	public boolean AddEntry(TileEntity entity) {
		if(entity == null) {
			return false;
		}
		if(GRID_MAP.containsKey(entity.getPos())) {
			return false;
		}
		GRID_MAP.put(entity.getPos(), entity);
		return true;
	}
	public int GetGridSize() {
		return GRID_MAP.size();
	}
	@Override
	public String toString() {
		return "Grid Size: " + GetGridSize() + "  Grid ID: " + GRID_ID;	
	}
}
