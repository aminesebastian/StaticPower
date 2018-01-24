package theking530.staticpower.tileentity.chunkloader;

import java.util.UUID;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import theking530.staticpower.tileentity.StaticTileEntity;

public class TileEntityChunkLoader extends TileEntity implements StaticTileEntity{

	private UUID placerUUID;
	
	public void placedByPlayer(EntityLivingBase player) {
		placerUUID = player.getUniqueID();
	}
	@Override
	public UUID getPlacer() {
		return placerUUID;
	}
}
