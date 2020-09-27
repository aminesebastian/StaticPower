package theking530.staticcore.gui.widgets.valuebars;

import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.gui.widgets.AbstractGuiWidget;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.tileentities.TileEntityBase;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticpower.tileentities.components.fluids.FluidTankComponent;

@OnlyIn(Dist.CLIENT)
public class GuiFluidBarFromTank extends AbstractGuiWidget {

	private FluidTankComponent tank;
	private MachineSideMode mode;
	private TileEntityBase owningTileEntity;

	public GuiFluidBarFromTank(FluidTankComponent tank, int xPosition, int yPosition, int xSize, int ySize, MachineSideMode mode, TileEntityBase owningTileEntity) {
		super(xPosition, yPosition, xSize, ySize);
		this.tank = tank;
		this.mode = mode;
		this.owningTileEntity = owningTileEntity;
	}

	public GuiFluidBarFromTank(FluidTankComponent tank, int xPosition, int yPosition, int xSize, int ySize) {
		this(tank, xPosition, yPosition, xSize, ySize, null, null);
	}

	@Override
	public void renderBehindItems(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
		tank.updateBeforeRendering(partialTicks);

		Vector2D ownerRelativePosition = getScreenSpacePosition();
		if (owningTileEntity != null) {
			if (!owningTileEntity.hasComponentOfType(SideConfigurationComponent.class)) {
				return;
			}
			SideConfigurationComponent sideComp = owningTileEntity.getComponent(SideConfigurationComponent.class);

			if (sideComp != null && mode != null) {
				if (sideComp.getCountOfSidesWithMode(mode) > 0) {
					GuiFluidBarUtilities.drawFluidBar(tank.getFluid(), tank.getCapacity(), (int) (tank.getVisualFillLevel() * tank.getCapacity()), ownerRelativePosition.getX(),
							ownerRelativePosition.getY() + getSize().getY(), 0.0f, getSize().getX(), getSize().getY(), mode, true);
				} else {
					GuiFluidBarUtilities.drawFluidBar(tank.getFluid(), tank.getCapacity(), (int) (tank.getVisualFillLevel() * tank.getCapacity()), ownerRelativePosition.getX(),
							ownerRelativePosition.getY() + getSize().getY(), 0.0f, getSize().getX(), getSize().getY(), true);
				}
			}
		} else {
			GuiFluidBarUtilities.drawFluidBar(tank.getFluid(), tank.getCapacity(), (int) (tank.getVisualFillLevel() * tank.getCapacity()), ownerRelativePosition.getX(),
					ownerRelativePosition.getY() + getSize().getY(), 0.0f, getSize().getX(), getSize().getY(), true);
		}
	}

	@Override
	public void getTooltips(Vector2D mousePosition, List<ITextComponent> tooltips, boolean showAdvanced) {
		tooltips.addAll(GuiFluidBarUtilities.getTooltip(tank.getFluidAmount(), tank.getCapacity(), tank.getFluid()));
	}
}
