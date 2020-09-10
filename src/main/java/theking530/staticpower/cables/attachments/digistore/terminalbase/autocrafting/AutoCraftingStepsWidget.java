package theking530.staticpower.cables.attachments.digistore.terminalbase.autocrafting;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.text.ITextComponent;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.AbstractGuiWidget;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.SDMath;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.cables.digistore.crafting.CraftingRequestResponse;
import theking530.staticpower.cables.digistore.crafting.RequiredAutoCraftingMaterials;

public class AutoCraftingStepsWidget extends AbstractGuiWidget {
	private CraftingRequestResponse request;
	private int rows;
	private int columns;
	private int scrollPosition;
	private List<AutoCraftingStepWidget> stepRenderers;

	public AutoCraftingStepsWidget(float xPosition, float yPosition, float width, float height, int rows, int columns) {
		super(xPosition, yPosition, width, height);
		this.rows = rows;
		this.columns = columns;
		this.scrollPosition = 0;
		this.stepRenderers = new ArrayList<AutoCraftingStepWidget>();

		// Create the auto crafting renderers.
		for (int i = 0; i < rows * columns; i++) {
			Vector2D position = getStepRenderPosition(i);
			AutoCraftingStepWidget stepRenderer = new AutoCraftingStepWidget(position.getX(), position.getY(), getSize().getX() / columns, 22);
			stepRenderers.add(stepRenderer);
		}
	}

	public void setRequest(CraftingRequestResponse request) {
		this.request = request;
	}

	@Override
	public void updateData() {
		// If there is no request, do nothing.
		if (request == null) {
			return;
		}

		// Get the required materials.
		RequiredAutoCraftingMaterials materials = request.getBillOfMaterials();

		// Capture the min and max indicies.
		int start = 0 + (scrollPosition * columns);
		int end = start + (columns * rows);
		end = Math.min(end, materials.getMaterials().size());

		// Get the tooltips.
		for (AutoCraftingStepWidget widget : stepRenderers) {
			widget.setMaterial(null);
		}

		// Draw the steps.
		for (int i = end - 1; i >= start; i--) {
			int zeroIndex = i - start;
			stepRenderers.get(zeroIndex).setMaterial(materials.getMaterials().get(i));
		}
	}

	@Override
	public void getTooltips(Vector2D mousePosition, List<ITextComponent> tooltips, boolean showAdvanced) {
		// If there is no request, do nothing.
		if (request == null) {
			return;
		}

		// Get the tooltips.
		for (AutoCraftingStepWidget widget : stepRenderers) {
			widget.getTooltips(mousePosition, tooltips, showAdvanced);
		}

	}

	@Override
	public void renderForeground(int mouseX, int mouseY, float partialTicks) {
		// Get the screen space position.
		Vector2D screenSpacePos = getScreenSpacePosition();

		// Draw the background.
		GuiDrawUtilities.drawSlot(screenSpacePos.getX(), screenSpacePos.getY(), getSize().getX(), getSize().getY());

		// If there is no request, do nothing else.
		if (request == null) {
			return;
		}

		// Render the widgets.
		for (AutoCraftingStepWidget widget : stepRenderers) {
			widget.updateBeforeRender(screenSpacePos, getSize(), partialTicks, mouseX, mouseY);
			widget.renderForeground(mouseX, mouseY, partialTicks);
		}

		// Render the vertical dividers.
		int divisionDistance = this.getSize().getXi() / columns;
		for (int i = 1; i < columns; i++) {
			GuiDrawUtilities.drawColoredRectangle(screenSpacePos.getX() + (divisionDistance * i) - 1, screenSpacePos.getY(), 1.0f, getSize().getY(), 1.0f, Color.GREY);
		}
	}

	public Vector2D getStartAndEndMaterialIndicies() {
		// If there is no request, do nothing.
		if (request == null) {
			return new Vector2D(0, 0);
		}

		// Get the required materials.
		RequiredAutoCraftingMaterials materials = request.getBillOfMaterials();

		// Capture the min and max indicies.
		int start = 0 + (scrollPosition * columns);
		int end = start + (columns * rows);
		end = Math.min(end, materials.getMaterials().size());

		return new Vector2D(start, end);
	}

	public int getScrollPosition() {
		return scrollPosition;
	}

	public int getMaxScrollPosition() {
		// If there is no request, do nothing.
		if (request == null) {
			return 0;
		}

		int materialRows = (int) Math.ceil((double) request.getBillOfMaterials().getMaterials().size() / rows);
		return Math.max(0, materialRows - rows);
	}

	public void setScrollPosition(int position) {
		this.scrollPosition = SDMath.clamp(position, 0, getMaxScrollPosition());
	}

	protected Vector2D getStepRenderPosition(int index) {
		int column = Math.floorMod(index, columns);
		int row = index / columns;
		return new Vector2D(column * (getSize().getX() / columns), row * 24);
	}
}
