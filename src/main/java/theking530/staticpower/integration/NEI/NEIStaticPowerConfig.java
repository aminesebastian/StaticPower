package theking530.staticpower.integration.NEI;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import theking530.staticpower.assists.Reference;
import theking530.staticpower.machines.cropsqueezer.GuiCropSqueezer;
import theking530.staticpower.machines.fluidinfuser.GuiFluidInfuser;
import theking530.staticpower.machines.fusionfurnace.GuiFusionFurnace;
import theking530.staticpower.machines.poweredgrinder.GuiPoweredGrinder;
import theking530.staticpower.machines.solderingtable.GuiSolderingTable;

public class NEIStaticPowerConfig implements IConfigureNEI{

	@Override
	public String getName() {
		return "Static Power NEI Plugin";
	}
	@Override
	public String getVersion() {
		return Reference.VERSION;
	}
	@Override
	public void loadConfig() {
		API.registerRecipeHandler(new NEICropSqueezerRecipeHandler());
		API.registerUsageHandler(new NEICropSqueezerRecipeHandler());
		API.setGuiOffset(GuiCropSqueezer.class, 0, 10);
		
		API.registerRecipeHandler(new NEIFluidInfuserRecipeHandler());
		API.registerUsageHandler(new NEIFluidInfuserRecipeHandler());
		API.setGuiOffset(GuiFluidInfuser.class, 0, 10);
		
		API.registerRecipeHandler(new NEIPoweredGrinderRecipeHandler());
		API.registerUsageHandler(new NEIPoweredGrinderRecipeHandler());
		API.setGuiOffset(GuiPoweredGrinder.class, 0, 10);
		
		API.registerRecipeHandler(new NEISolderingTableRecipeHandler());
		API.registerUsageHandler(new NEISolderingTableRecipeHandler());
		API.setGuiOffset(GuiSolderingTable.class, 0, 10);
		
		API.registerRecipeHandler(new NEIFusionFurnaceRecipeHandler());
		API.registerUsageHandler(new NEIFusionFurnaceRecipeHandler());
		API.setGuiOffset(GuiFusionFurnace.class, 0, 10);
	}

}
