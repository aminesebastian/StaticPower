package theking530.staticpower.conduits.itemconduit;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import theking530.staticpower.StaticPower;
import theking530.staticpower.conduits.BaseConduit;

public class BlockItemConduit extends BaseConduit {

	float pixel = 1F/16F;
	
	public BlockItemConduit() {
		super("ItemConduit");
		float pixel = 1F/16F;
		//this.setBlockBounds(11*pixel/2, 11*pixel/2, 11*pixel/2, 1-11*pixel/2, 1-11*pixel/2, 1-11*pixel/2);
		this.useNeighborBrightness = true;
	}	
	/**
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		if(te instanceof TileEntityItemConduit) {
			TileEntityItemConduit conduit = (TileEntityItemConduit)world.getTileEntity(x, y, z);
			
			if(conduit != null) {
				float minX = 11*pixel/2-(conduit.connections[5] !=null?(11*pixel/2):0);
				float minY = 11*pixel/2-(conduit.connections[1] !=null?(11*pixel/2):0);
				float minZ = 11*pixel/2-(conduit.connections[2] !=null?(11*pixel/2):0);
				float maxX = 1-11*pixel/2+(conduit.connections[4] !=null?(11*pixel/2):0);
				float maxY = 1-11*pixel/2+(conduit.connections[0] !=null?(11*pixel/2):0);
				float maxZ = 1-11*pixel/2+(conduit.connections[3] !=null?(11*pixel/2):0);
				
				this.setBlockBounds(minX, minY, minZ, maxX, maxY, maxZ);	
			}	
		return AxisAlignedBB.getBoundingBox(x+this.minX, y+this.minY, z+this.minZ, x+this.maxX, y+this.maxY, z+this.maxZ);
		}
		return null;
		
	}	
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		if(te instanceof TileEntityItemConduit) {
			TileEntityItemConduit conduit = (TileEntityItemConduit)world.getTileEntity(x, y, z);
		
			if(conduit != null) {
				float minX = 11*pixel/2-(conduit.connections[5] !=null?(11*pixel/2):0);
				float minY = 11*pixel/2-(conduit.connections[1] !=null?(11*pixel/2):0);
				float minZ = 11*pixel/2-(conduit.connections[2] !=null?(11*pixel/2):0);
				float maxX = 1-11*pixel/2+(conduit.connections[4] !=null?(11*pixel/2):0);
				float maxY = 1-11*pixel/2+(conduit.connections[0] !=null?(11*pixel/2):0);
				float maxZ = 1-11*pixel/2+(conduit.connections[3] !=null?(11*pixel/2):0);
				
				this.setBlockBounds(minX, minY, minZ, maxX, maxY, maxZ);	
			}	
		return AxisAlignedBB.getBoundingBox(x+this.minX, y+this.minY, z+this.minZ, x+this.maxX, y+this.maxY, z+this.maxZ);
		}
		return null;		
	}
	*/
	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TileEntityItemConduit();
	}
}
