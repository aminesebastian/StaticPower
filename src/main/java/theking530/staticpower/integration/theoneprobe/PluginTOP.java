package theking530.staticpower.integration.theoneprobe;

import javax.annotation.Nullable;

import com.google.common.base.Function;

import mcjty.theoneprobe.api.ElementAlignment;
import mcjty.theoneprobe.api.IProbeConfig;
import mcjty.theoneprobe.api.IProbeConfig.ConfigMode;
import mcjty.theoneprobe.api.IProbeConfigProvider;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeHitEntityData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ITheOneProbe;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.api.TextStyleClass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import theking530.staticpower.assists.Reference;
import theking530.staticpower.assists.utilities.EnumTextFormatting;
import theking530.staticpower.assists.utilities.GuiUtilities;
import theking530.staticpower.assists.utilities.SideModeList.Mode;
import theking530.staticpower.integration.ICompatibilityPlugin;
import theking530.staticpower.machines.BaseMachineWithTank;
import theking530.staticpower.tileentity.ISideConfigurable;
import theking530.staticpower.tileentity.digistorenetwork.BaseDigistoreTileEntity;
import theking530.staticpower.tileentity.digistorenetwork.digistore.TileEntityDigistore;

public class PluginTOP implements ICompatibilityPlugin {

	private static boolean registered = false;
	
	@Override
	public void register() {
		if(isRegistered()) {
			return;
		}
		registered = true;
		FMLInterModComms.sendFunctionMessage("theoneprobe", "getTheOneProbe", OneProbeCompatibility.class.getName());
	}
	@Override
	public boolean isRegistered() {
		return registered;
	}
	@Override
	public boolean shouldRegister() {
		return Loader.isModLoaded("theoneprobe");
	}
	@Override
	public String getPluginName() {
		return "The One Probe";
	}
	
	public static class OneProbeCompatibility implements Function<ITheOneProbe, Void> {
	    @Nullable
		@Override
		public Void apply(@Nullable ITheOneProbe probe) {
			if (probe != null) {
				probe.registerProbeConfigProvider(new IProbeConfigProvider() {
					
					@Override
					public void getProbeConfig(IProbeConfig config, EntityPlayer player, World world, Entity entity, IProbeHitEntityData data) {}
	
					@Override
					public void getProbeConfig(IProbeConfig config, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
						TileEntity tile = world.getTileEntity(data.getPos());
						
						if(tile != null) {
							if(tile instanceof BaseDigistoreTileEntity) {
								config.showCanBeHarvested(ConfigMode.NOT);
							}
							if(tile instanceof BaseMachineWithTank) {
								config.showTankSetting(ConfigMode.NORMAL);
								config.setTankMode(1);
							}
						}
					}
				});
	
				probe.registerProvider(new IProbeInfoProvider() {
					@Override
					public String getID() {
						return Reference.MOD_ID + ".topplugin";
					}
	
					@Override
					public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
	
						TileEntity tile = world.getTileEntity(data.getPos());
						if (tile instanceof TileEntityDigistore) {
							if (mode == ProbeMode.NORMAL) {
								IProbeInfo infoSub = probeInfo.horizontal(probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER).borderColor(GuiUtilities.getColor(50, 120, 180)).spacing(2));
								
								ItemStack stored = ((TileEntityDigistore)tile).getStoredItem();
								infoSub.item(stored, probeInfo.defaultItemStyle().width(16).height(16)).text(TextStyleClass.INFO + stored.getDisplayName() + TextStyleClass.INFO + " (" + ((TileEntityDigistore)tile).getStoredAmount() + ")");
							}
						}
						if(tile instanceof ISideConfigurable) {
							ISideConfigurable configurable = (ISideConfigurable)tile;
							if(configurable.isSideConfigurable() && configurable.getSideConfiguration(data.getSideHit()) != Mode.Regular) {
								IProbeInfo infoSub = probeInfo.horizontal(probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER));
								switch(configurable.getSideConfiguration(data.getSideHit())) {
									case Output:
										infoSub.text("Side: " + EnumTextFormatting.GOLD + configurable.getSideConfiguration(data.getSideHit()));
										break;
									case Input:
										infoSub.text("Side: " + EnumTextFormatting.BLUE + configurable.getSideConfiguration(data.getSideHit()));
										break;
									case Disabled:
										infoSub.text("Side: " + EnumTextFormatting.RED + configurable.getSideConfiguration(data.getSideHit()));
										break;
									default:
										break;
								}	
							}
						}
					}
				});
			}
			return null;
		}
	}
}
