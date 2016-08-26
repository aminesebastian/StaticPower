package theking530.staticpower.client.render.tileentitys.logicgates;

import net.minecraft.util.ResourceLocation;
import theking530.staticpower.assists.Reference;
import theking530.staticpower.client.model.ModelSignalMultiplier;

public class TileEntityRenderNotGate extends TileEntityRenderLogicGateBase {
	
    public TileEntityRenderNotGate() {
        super(new ModelSignalMultiplier(), 
        		new ResourceLocation(Reference.MODID, "textures/models/misc/MultiplierON.png"), 
        		new ResourceLocation(Reference.MODID, "textures/models/misc/MultiplierOFF.png"));
    }
}



