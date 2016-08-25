package theking530.staticpower.client.render.tileentitys.logicgates;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import theking530.staticpower.assists.Reference;
import theking530.staticpower.client.model.ModelSignalMultiplier;
import theking530.staticpower.tileentity.gates.TileEntityBaseLogicGate;
import theking530.staticpower.utils.SideModeList.Mode;

public class TileEntityRenderPowerCell extends TileEntityRenderLogicGateBase {

    public TileEntityRenderPowerCell() {
        super(new ModelSignalMultiplier(), 
        		new ResourceLocation(Reference.MODID, "textures/models/misc/MultiplierON.png"), 
        		new ResourceLocation(Reference.MODID, "textures/models/misc/MultiplierOFF.png"));
    }
}



