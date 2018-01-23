package api;

public class RectangleBounds {

	private int x;
	private int y;
	private int w;
	private int h;
	
	public RectangleBounds(int xPos, int yPos, int width, int height) {
		x = xPos;
		y = yPos;
		w = width;
		h = height;
	}
	
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public int getW() {
		return w;
	}
	public int getH() {
		return h;
	}
}
