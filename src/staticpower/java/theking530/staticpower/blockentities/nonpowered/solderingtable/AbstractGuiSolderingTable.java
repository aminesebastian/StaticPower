package theking530.staticpower.blockentities.nonpowered.solderingtable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import theking530.staticcore.gui.screens.StaticCoreBlockEntityScreen;
import theking530.staticcore.gui.widgets.button.FakeSlotButton;
import theking530.staticcore.gui.widgets.button.StandardButton;
import theking530.staticcore.gui.widgets.button.StandardButton.MouseButton;
import theking530.staticcore.gui.widgets.tabs.GuiInfoTab;
import theking530.staticpower.network.StaticPowerMessageHandler;

public class AbstractGuiSolderingTable<T extends AbstractSolderingTable, K extends AbstractContainerSolderingTable<T>> extends StaticCoreBlockEntityScreen<K, T> {

	protected GuiInfoTab infoTab;
	private List<FakeSlotButton> recipeFakeSlots;

	public AbstractGuiSolderingTable(K container, Inventory invPlayer, Component name, int width, int height) {
		super(container, invPlayer, name, width, height);
	}

	@Override
	public void initializeGui() {
		recipeFakeSlots = new ArrayList<FakeSlotButton>();
		tabManager.registerTab(infoTab = new GuiInfoTab(100));
		setOutputSlotSize(20);

		// Add the pattern slots.
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				// Use wrappers for the lambda
				AtomicInteger xWrap = new AtomicInteger(x);
				AtomicInteger yWrap = new AtomicInteger(y);
				FakeSlotButton slotButton = new FakeSlotButton(getTileEntity().patternInventory.getStackInSlot(x + (y * 3)), getPatternRecipeXOffset() + 62 + x * 18, 20 + y * 18,
						(button, mouse) -> {
							recipeButtonPressed(button, mouse, xWrap.get(), yWrap.get());
						});

				// Add the button to the recipe button array and register it.
				recipeFakeSlots.add(slotButton);
				registerWidget(slotButton);
			}
		}
	}

	@Override
	public void updateData() {
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				ItemStack stack = getTileEntity().patternInventory.getStackInSlot(x + (y * 3));
				stack.setCount(1);
				recipeFakeSlots.get(x + (y * 3)).setItemStack(stack);
			}
		}
	}

	protected int getPatternRecipeXOffset() {
		return 0;
	}

	@SuppressWarnings("resource")
	protected void recipeButtonPressed(StandardButton button, MouseButton mouse, int x, int y) {
		// Update the slot.
		recipeFakeSlots.get(x + (y * 3)).setItemStack(Minecraft.getInstance().player.containerMenu.getCarried());
		getMenu().getTileEntity().patternInventory.setStackInSlot(x + (y * 3), Minecraft.getInstance().player.containerMenu.getCarried());

		// Get all the recipe items.
		ItemStack[] recipeItems = new ItemStack[9];
		for (int i = 0; i < 9; i++) {
			recipeItems[i] = getMenu().getTileEntity().patternInventory.getStackInSlot(i);
		}

		// Send the packet to the server.
		StaticPowerMessageHandler.MAIN_PACKET_CHANNEL.sendToServer(new PacketSyncSolderingFakeSlotRecipe(getMenu().containerId, recipeItems));
	}
}
