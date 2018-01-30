package theking530.staticpower.client.sideSelector;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import com.google.common.collect.Lists;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import theking530.staticpower.assists.utilities.SidePicker;
import theking530.staticpower.assists.utilities.SidePicker.HitCoord;
import theking530.staticpower.assists.utilities.SidePicker.Side;
import theking530.staticpower.client.gui.widgets.Trackball.TrackballWrapper;

public class GuiComponentSideSelector  {

	private static final double SQRT_3 = Math.sqrt(3);

	private final TrackballWrapper trackball = new TrackballWrapper(1, 40);

	private final int diameter;
	private final double scale;
	private EnumFacing lastSideHovered;
	private final Set<EnumFacing> selectedSides = EnumSet.noneOf(EnumFacing.class);
	private boolean highlightSelectedSides = true;

	private boolean isInInitialPosition;

	protected int x;
	protected int y;
	
	private IBlockState blockState;
	private TileEntity te;

	private final FakeBlockAccess access;
	
	public GuiComponentSideSelector(int x, int y, double scale, IBlockState blockState, TileEntity te, boolean highlightSelectedSides) {
		this.x = x;
		this.y = y;
		this.scale = scale;
		this.diameter = MathHelper.ceil(scale * SQRT_3);
		this.blockState = blockState;
		this.te = te;
		this.highlightSelectedSides = highlightSelectedSides;
		this.access = new FakeBlockAccess(blockState, te);
	}
	public static Matrix4f createEntityRotateMatrix(Entity entity) {
		double yaw = Math.toRadians(entity.rotationYaw - 180);
		double pitch = Math.toRadians(entity.rotationPitch);

		Matrix4f initial = new Matrix4f();
		initial.rotate((float)pitch, new Vector3f(1, 0, 0));
		initial.rotate((float)yaw, new Vector3f(0, 1, 0));
		return initial;
	}
	public void render(int offsetX, int offsetY, int mouseX, int mouseY) {
		if (isInInitialPosition == false || Mouse.isButtonDown(2)) {
			final Entity rve = Minecraft.getMinecraft().getRenderViewEntity();
			trackball.setTransform(createEntityRotateMatrix(rve));
			isInInitialPosition = true;
		}

		final int width = getWidth();
		final int height = getWidth();

		// assumption: block is rendered in (0,0,0) - (1,1,1) coordinates
		GL11.glPushMatrix();
		GL11.glTranslatef(offsetX + x + width / 2, offsetY + y + height / 2, diameter);
		GL11.glScaled(scale, -scale, scale);
		trackball.update(mouseX - width, -(mouseY - height));

		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		GlStateManager.enableTexture2D();

		if (te != null) {		
			TileEntityRendererDispatcher.instance.render(te, -0.5, -0.5, -0.5, 0.0F);
		}

		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		if (blockState != null) {
			drawBlock();
		}

		SidePicker picker = new SidePicker(0.5);

		List<Pair<Side, Integer>> selections = Lists.newArrayListWithCapacity(6 + 1);
		final HitCoord coord = picker.getNearestHit();
		if (coord != null) selections.add(Pair.of(coord.side, 0x444444));

		if (highlightSelectedSides) {
			for (EnumFacing dir : selectedSides)
				selections.add(Pair.of(Side.fromForgeDirection(dir), 0xCC0000));
		}

		if (selections != null) drawHighlight(selections);

		lastSideHovered = coord == null? null : coord.side.toForgeDirection();

		GL11.glPopMatrix();
	}

	private void drawBlock() {
		final Tessellator tessellator = Tessellator.getInstance();
		final BufferBuilder wr = tessellator.getBuffer();
		final BlockRendererDispatcher dispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
		for (BlockRenderLayer layer : BlockRenderLayer.values()) {
			if (blockState.getBlock().canRenderInLayer(blockState, layer)) {
				net.minecraftforge.client.ForgeHooksClient.setRenderLayer(layer);
				wr.setTranslation(-0.5, -0.5, -0.5);
				wr.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
				dispatcher.renderBlock(blockState, FakeBlockAccess.ORIGIN, access, wr);
				tessellator.draw();
			}
		}
		wr.setTranslation(0.0D, 0.0D, 0.0D);

		net.minecraftforge.client.ForgeHooksClient.setRenderLayer(null);
	}
	public static void setColor(int rgb, float alpha) {
		final float r = (float)((rgb >> 16) & 0xFF) / 255;
		final float g = (float)((rgb >> 8) & 0xFF) / 255;
		final float b = (float)((rgb >> 0) & 0xFF) / 255;
		GlStateManager.color(r, g, b);
	}
	private static void drawHighlight(List<Pair<Side, Integer>> selections) {
		GlStateManager.disableLighting();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.disableDepth();
		GlStateManager.disableTexture2D();

		GL11.glBegin(GL11.GL_QUADS);
		for (Pair<Side, Integer> p : selections) {
			final Integer color = p.getRight();
			setColor(color, 0.5f);

			switch (p.getLeft()) {
				case XPos:
					GL11.glVertex3d(0.5, -0.5, -0.5);
					GL11.glVertex3d(0.5, 0.5, -0.5);
					GL11.glVertex3d(0.5, 0.5, 0.5);
					GL11.glVertex3d(0.5, -0.5, 0.5);
					break;
				case YPos:
					GL11.glVertex3d(-0.5, 0.5, -0.5);
					GL11.glVertex3d(-0.5, 0.5, 0.5);
					GL11.glVertex3d(0.5, 0.5, 0.5);
					GL11.glVertex3d(0.5, 0.5, -0.5);
					break;
				case ZPos:
					GL11.glVertex3d(-0.5, -0.5, 0.5);
					GL11.glVertex3d(0.5, -0.5, 0.5);
					GL11.glVertex3d(0.5, 0.5, 0.5);
					GL11.glVertex3d(-0.5, 0.5, 0.5);
					break;
				case XNeg:
					GL11.glVertex3d(-0.5, -0.5, -0.5);
					GL11.glVertex3d(-0.5, -0.5, 0.5);
					GL11.glVertex3d(-0.5, 0.5, 0.5);
					GL11.glVertex3d(-0.5, 0.5, -0.5);
					break;
				case YNeg:
					GL11.glVertex3d(-0.5, -0.5, -0.5);
					GL11.glVertex3d(0.5, -0.5, -0.5);
					GL11.glVertex3d(0.5, -0.5, 0.5);
					GL11.glVertex3d(-0.5, -0.5, 0.5);
					break;
				case ZNeg:
					GL11.glVertex3d(-0.5, -0.5, -0.5);
					GL11.glVertex3d(-0.5, 0.5, -0.5);
					GL11.glVertex3d(0.5, 0.5, -0.5);
					GL11.glVertex3d(0.5, -0.5, -0.5);
					break;
				default:
					break;
			}
		}
		GL11.glEnd();

		GlStateManager.disableBlend();
		GlStateManager.enableDepth();
		GlStateManager.enableTexture2D();
	}
	public int getWidth() {
		return diameter;
	}
	public int getHeight() {
		return diameter;
	}
	
	private void toggleSide(EnumFacing side) {
		boolean wasntPresent = !selectedSides.remove(side);
		if (wasntPresent) selectedSides.add(side);
	}
	public void mouseUp(int mouseX, int mouseY, int button) {
		if (button == 0 && lastSideHovered != null) {
			toggleSide(lastSideHovered);
		}
	}
	public void mouseDown(int mouseX, int mouseY, int button) {
		lastSideHovered = null;
	}
}