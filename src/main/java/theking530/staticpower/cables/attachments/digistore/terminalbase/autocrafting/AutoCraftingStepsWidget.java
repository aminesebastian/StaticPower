package theking530.staticpower.cables.attachments.digistore.terminalbase.autocrafting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.util.text.ITextComponent;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.AbstractGuiWidget;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.SDMath;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.cables.digistore.crafting.CraftingRequestResponse;
import theking530.staticpower.cables.digistore.crafting.RequiredAutoCraftingMaterials;
import theking530.staticpower.cables.digistore.crafting.RequiredAutoCraftingMaterials.RequiredAutoCraftingMaterial;

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
			stepRenderers.get(zeroIndex).updateData();
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
	public void renderForeground(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
		// Get the screen space position.
		Vector2D screenSpacePos = GuiDrawUtilities.translatePositionByMatrix(matrix, getPosition());

		// Draw the background.
		GuiDrawUtilities.drawSlot(null, screenSpacePos.getX(), screenSpacePos.getY(), getSize().getX(), (rows * 24) - 1);

		// If there is no request, do nothing else.
		if (request == null) {
			return;
		}

		// Translate the matrix to the space of this widget.
		matrix.push();
		matrix.translate(getPosition().getX(), getPosition().getY(), 0);

		// Render the widgets.
		List<RequiredAutoCraftingMaterial> materials = getMaterialsForScrollPosition();
		for (int i = 0; i < stepRenderers.size(); i++) {
			// Get the widget.
			AutoCraftingStepWidget widget = stepRenderers.get(i);

			// If the widget has a cooresponding material, set it and render. Otherwise, set
			// it to null and do nothing.
			if (i < materials.size()) {
				widget.setMaterial(materials.get(i));
				widget.updateBeforeRender(matrix, getSize(), partialTicks, mouseX, mouseY);
				widget.renderForeground(matrix, mouseX, mouseY, partialTicks);
			} else {
				widget.setMaterial(null);
			}
		}

		// Pop the previous translation.
		matrix.pop();

		// Render the vertical dividers.
		int divisionDistance = this.getSize().getXi() / columns;
		for (int i = 1; i < columns; i++) {
			GuiDrawUtilities.drawColoredRectangle(screenSpacePos.getX() + (divisionDistance * i) - 1, screenSpacePos.getY(), 1.0f, (rows * 24) - 1, 1.0f, Color.GREY);
		}
	}

	public List<RequiredAutoCraftingMaterial> getMaterialsForScrollPosition() {
		// If there is no request, do nothing.
		if (request == null) {
			return Collections.emptyList();
		}

		// Get the required materials.
		RequiredAutoCraftingMaterials materials = request.getBillOfMaterials();

		// Capture the min and max indicies.
		int start = 0 + (scrollPosition * columns);
		int end = start + (columns * rows);

		// Make sure we don't go over!
		if (end >= materials.getMaterials().size()) {
			end = materials.getMaterials().size() - 1;
		}

		// Capture the list.
		List<RequiredAutoCraftingMaterial> output = new ArrayList<RequiredAutoCraftingMaterial>();
		for (int i = start; i < end; i++) {
			output.add(materials.getMaterials().get(i));
		}

		// Return the output.
		return output;
	}

	public int getScrollPosition() {
		return scrollPosition;
	}

	public int getMaxScrollPosition() {
		// If there is no request, do nothing.
		if (request == null) {
			return 0;
		}

		int materialRows = (int) Math.ceil((double) request.getBillOfMaterials().getMaterials().size() / columns);
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
