package theking530.staticpower.client.model;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.IModelPart;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import theking530.staticpower.assists.Reference;

@SideOnly(Side.CLIENT)
public class ModelSolarPanel
{
    private IModel model;

    public ModelSolarPanel() {
    	try {
			model = ModelLoaderRegistry.getModel(Models.SOLARPANEL);
		} catch (Exception e) {
			model = ModelLoaderRegistry.getMissingModel();
		}
    }

    public void render() {
    	model.getDefaultState();
    }
}