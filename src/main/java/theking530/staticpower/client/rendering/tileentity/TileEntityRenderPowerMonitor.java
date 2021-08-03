package theking530.staticpower.client.rendering.tileentity;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.DataGraphWidget;
import theking530.staticcore.gui.widgets.DataGraphWidget.ListGraphDataSet;
import theking530.staticcore.utilities.Color;
import theking530.staticpower.tileentities.powered.powermonitor.TileEntityPowerMonitor;

@OnlyIn(Dist.CLIENT)
public class TileEntityRenderPowerMonitor extends StaticPowerTileEntitySpecialRenderer<TileEntityPowerMonitor> {
	private DataGraphWidget dataGraph;

	public TileEntityRenderPowerMonitor(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);

		dataGraph = new DataGraphWidget(0, 0, 100, 100);
		List<Double> data = new ArrayList<Double>();
		data.add(0.0);
		data.add(10.0);
		data.add(20.0);
		data.add(30.0);
		data.add(0.0);
		data.add(10.0);
		data.add(20.0);
		data.add(30.0);
		data.add(0.0);
		data.add(10.0);
		data.add(20.0);
		data.add(30.0);
		data.add(0.0);
		dataGraph.setDataSet("Input", new ListGraphDataSet(new Color(0, 1.0f, 0.2f, 0.75f), data));
	}

	@Override
	public void renderTileEntityBase(TileEntityPowerMonitor tileEntity, BlockPos pos, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight,
			int combinedOverlay) {

		GuiDrawUtilities.drawColoredRectangle(matrixStack, tileEntity.getPos().getX(), tileEntity.getPos().getY(), 100, 50f, 1, Color.GREY);
		
		// Draw the graph.
		matrixStack.push();
		matrixStack.translate(0, 1, 1.01);
		matrixStack.scale(0.01f, -0.01f, 1.0f);
		dataGraph.renderBehindItems(matrixStack, 0, 0, partialTicks);
		
		
		matrixStack.pop();
	}
}
