package theking530.staticcore.gui.widgets.valuebars;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.IFluidTank;
import theking530.staticcore.blockentity.BlockEntityBase;
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticcore.blockentity.components.fluids.FluidTankComponent;
import theking530.staticcore.gui.widgets.AbstractGuiWidget;
import theking530.staticcore.utilities.math.Vector2D;

@OnlyIn(Dist.CLIENT)
public class GuiFluidBarFromTank extends AbstractGuiWidget<GuiFluidBarFromTank> {

	private IFluidTank tank;
	private MachineSideMode mode;
	private BlockEntityBase owningTileEntity;

	public GuiFluidBarFromTank(IFluidTank tank, int xPosition, int yPosition, int xSize, int ySize, MachineSideMode mode, BlockEntityBase owningTileEntity) {
		super(xPosition, yPosition, xSize, ySize);
		this.tank = tank;
		this.mode = mode;
		this.owningTileEntity = owningTileEntity;
	}

	public GuiFluidBarFromTank(IFluidTank tank, int xPosition, int yPosition, int xSize, int ySize) {
		this(tank, xPosition, yPosition, xSize, ySize, null, null);
	}

	@Override
	public void renderWidgetBehindItems(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
		if (tank instanceof FluidTankComponent) {
			((FluidTankComponent) tank).updateBeforeRendering(partialTicks);
		}

		if (owningTileEntity != null) {
			if (!owningTileEntity.hasComponentOfType(SideConfigurationComponent.class)) {
				return;
			}
			SideConfigurationComponent sideComp = owningTileEntity.getComponent(SideConfigurationComponent.class);

			if (sideComp != null && mode != null) {
				if (sideComp.getCountOfSidesWithMode(mode) > 0) {
					GuiFluidBarUtilities.drawFluidBar(matrix, tank.getFluid(), tank.getCapacity(), (int) (getVisualFillLevel() * tank.getCapacity()), 0, getSize().getY(), 0.0f,
							getSize().getX(), getSize().getY(), mode, true);
				} else {
					GuiFluidBarUtilities.drawFluidBar(matrix, tank.getFluid(), tank.getCapacity(), (int) (getVisualFillLevel() * tank.getCapacity()), 0, getSize().getY(), 0.0f,
							getSize().getX(), getSize().getY(), true);
				}
			}
		} else {
			GuiFluidBarUtilities.drawFluidBar(matrix, tank.getFluid(), tank.getCapacity(), (int) (getVisualFillLevel() * tank.getCapacity()), 0, getSize().getY(), 0.0f,
					getSize().getX(), getSize().getY(), true);
		}
	}

	protected float getVisualFillLevel() {
		if (tank instanceof FluidTankComponent) {
			return ((FluidTankComponent) tank).getVisualFillLevel();
		}
		return (float)tank.getFluidAmount() / tank.getCapacity();
	}

	@Override
	public void getWidgetTooltips(Vector2D mousePosition, List<Component> tooltips, boolean showAdvanced) {
		tooltips.addAll(GuiFluidBarUtilities.getTooltip(tank.getFluidAmount(), tank.getCapacity(), tank.getFluid()));
	}
}
