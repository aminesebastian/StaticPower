package theking530.staticpower.client.gui.widgets;
import java.util.EnumSet;
import java.util.Set;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import theking530.staticpower.client.BaseSideSelectorComponent;
import theking530.staticpower.client.gui.widgets.Trackball.TrackballWrapper;
import theking530.staticpower.utils.OldSidePicker;

public class GuiComponentSideSelector extends BaseSideSelectorComponent {

	private static final double SQRT_3 = Math.sqrt(3);

	private final TrackballWrapper trackball = new TrackballWrapper(1, 40);

	private final int diameter;
	private final double scale;
	private EnumFacing lastSideHovered;
	private final Set<EnumFacing> selectedSides = EnumSet.noneOf(EnumFacing.class);
	private boolean highlightSelectedSides = true;

	private boolean isInInitialPosition;

	private int meta;
	private TileEntity te;

	public GuiComponentSideSelector(int x, int y, double scale, int meta, TileEntity te, boolean highlightSelectedSides) {
		super(x, y);
		this.scale = scale;
		this.diameter = MathHelper.ceiling_double_int(scale * SQRT_3);
		this.meta = meta;
		this.te = te;
		this.highlightSelectedSides = highlightSelectedSides;
	}

	@Override
	public void render(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		if (isInInitialPosition == false || Mouse.isButtonDown(2)) {
			trackball.setTransform(createEntityRotateMatrix(minecraft.getRenderViewEntity()));
			isInInitialPosition = true;
		}

		final int width = getWidth();
		final int height = getWidth();
		// assumption: block is rendered in (0,0,0) - (1,1,1) coordinates
		GL11.glPushMatrix();
		Tessellator tessellator = Tessellator.getInstance();
		GL11.glTranslatef(offsetX + x + width / 2, offsetY + y + height / 2, diameter);
		GL11.glScaled(scale, -scale, scale);
		trackball.update(mouseX - width, -(mouseY - height));
		if (te != null) TileEntityRendererDispatcher.instance.renderTileEntityAt(te, -0.5, -0.5, -0.5, 0.0F);
		/**
		OldSidePicker picker = new OldSidePicker();

		theking530.staticpower.utils.SidePicker.HitCoord coord = picker.getNearestHit();

		if (coord != null) drawHighlight(tessellator, coord.side, 0x444444);

		if (highlightSelectedSides) {
			for (EnumFacing dir : selectedSides) {
				drawHighlight(tessellator, Side.fromEnumFacing(dir), 0xCC0000);
			}
		}

		lastSideHovered = coord == null? null : coord.side.toEnumFacing(coord.side);

		GL11.glPopMatrix();
		*/
	}

	@Override
	public void renderOverlay(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {}

	private static void drawHighlight(Tessellator t, OldSidePicker.Side side, int color) {
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer tes = tessellator.getBuffer();
        tes.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        //tes.setColorRGBA_I(color, 64);
		switch (side) {
			case XPos:
				tes.pos(0.5, -0.5, -0.5);
				tes.pos(0.5, 0.5, -0.5);
				tes.pos(0.5, 0.5, 0.5);
				tes.pos(0.5, -0.5, 0.5);
				break;
			case YPos:
				tes.pos(-0.5, 0.5, -0.5);
				tes.pos(-0.5, 0.5, 0.5);
				tes.pos(0.5, 0.5, 0.5);
				tes.pos(0.5, 0.5, -0.5);
				break;
			case ZPos:
				tes.pos(-0.5, -0.5, 0.5);
				tes.pos(0.5, -0.5, 0.5);
				tes.pos(0.5, 0.5, 0.5);
				tes.pos(-0.5, 0.5, 0.5);
				break;
			case XNeg:
				tes.pos(-0.5, -0.5, -0.5);
				tes.pos(-0.5, -0.5, 0.5);
				tes.pos(-0.5, 0.5, 0.5);
				tes.pos(-0.5, 0.5, -0.5);
				break;
			case YNeg:
				tes.pos(-0.5, -0.5, -0.5);
				tes.pos(0.5, -0.5, -0.5);
				tes.pos(0.5, -0.5, 0.5);
				tes.pos(-0.5, -0.5, 0.5);
				break;
			case ZNeg:
				tes.pos(-0.5, -0.5, -0.5);
				tes.pos(-0.5, 0.5, -0.5);
				tes.pos(0.5, 0.5, -0.5);
				tes.pos(0.5, -0.5, -0.5);
				break;
			default:
				break;
		}
		t.draw();
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
	}
	private void toggleSide(EnumFacing side) {
		boolean wasntPresent = !selectedSides.remove(side);
		if (wasntPresent) selectedSides.add(side);
		System.out.println(side);
	}
	public void mouseUp(int mouseX, int mouseY, int button) {
		if (button == 0 && lastSideHovered != null && lastSideHovered != null) {
			toggleSide(lastSideHovered);
		}
	}
	public void mouseDown(int mouseX, int mouseY, int button) {
		lastSideHovered = null;
	}
	@Override
	public int getWidth() {
		return diameter;
	}
	@Override
	public int getHeight() {
		return diameter;
	}
	public static Matrix4f createEntityRotateMatrix(Entity entity) {
		double yaw = Math.toRadians(entity.rotationYaw - 180);
		double pitch = Math.toRadians(entity.rotationPitch);

		Matrix4f initial = new Matrix4f();
		initial.rotate((float)pitch, new Vector3f(1, 0, 0));
		initial.rotate((float)yaw, new Vector3f(0, 1, 0));
		return initial;
	}
}