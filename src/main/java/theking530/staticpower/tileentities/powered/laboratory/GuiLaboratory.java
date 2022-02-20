package theking530.staticpower.tileentities.powered.laboratory;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Inventory;
import theking530.staticcore.gui.widgets.button.SpriteButton;
import theking530.staticcore.gui.widgets.button.StandardButton;
import theking530.staticcore.gui.widgets.button.StandardButton.MouseButton;
import theking530.staticcore.gui.widgets.progressbars.ArrowProgressBar;
import theking530.staticcore.gui.widgets.tabs.BaseGuiTab.TabSide;
import theking530.staticcore.gui.widgets.tabs.GuiMachinePowerInfoTab;
import theking530.staticcore.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticcore.gui.widgets.tabs.redstonecontrol.GuiTileEntityRedstoneTab;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarFromEnergyStorage;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;
import theking530.staticpower.network.StaticPowerMessageHandler;
import theking530.staticpower.teams.Team;
import theking530.staticpower.teams.TeamManager;
import theking530.staticpower.tileentities.components.control.RedstoneControlComponent;

public class GuiLaboratory extends StaticPowerTileEntityGui<ContainerLaboratory, TileEntityLaboratory> {

	public GuiLaboratory(ContainerLaboratory container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 166);
	}

	@Override
	public void initializeGui() {
		registerWidget(new GuiPowerBarFromEnergyStorage(getTileEntity().energyStorage.getStorage(), 8, 8, 16, 52));
		registerWidget(new ArrowProgressBar(78, 55).bindToMachineProcessingComponent(getTileEntity().processingComponent));

		getTabManager().registerTab(new GuiTileEntityRedstoneTab(getTileEntity().getComponent(RedstoneControlComponent.class)));
		getTabManager().registerTab(new GuiSideConfigTab(getTileEntity()));
		getTabManager().registerTab(new GuiMachinePowerInfoTab(getTileEntity().energyStorage).setTabSide(TabSide.LEFT), true);

		setOutputSlotSize(20);

		registerWidget(new SpriteButton(153, 71, 10, 10, StaticPowerSprites.TEAM_ICON, null, this::teamSetButtonPressed).setTooltip(new TextComponent("Set Team")));
	}

	public void teamSetButtonPressed(StandardButton button, MouseButton mouseButton) {
		Team localTeam = TeamManager.getLocalTeam();
		if (localTeam != null) {
			StaticPowerMessageHandler.sendToServer(StaticPowerMessageHandler.MAIN_PACKET_CHANNEL, new PacketSetLaboratoryTeam(getTileEntity(), localTeam.getId()));
		}
	}
}
