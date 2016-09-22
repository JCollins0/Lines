package colorMatch;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Ball {
	private Color color;
	private ColorID colorID;
	public static ArrayList<BufferedImage> IMAGES = Utility.loadBufferedList("classic_a",64,64);
	
	public Ball(ColorID color) {
		this.colorID = color;
		this.color = colorID.getColor();
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public ColorID getColorID() {
		return colorID;
	}

	public void setColorID(ColorID colorID) {
		this.colorID = colorID;
	}
	
	public static BufferedImage getImage(int state)
	{
		if(state >= IMAGES.size()) return IMAGES.get(0);
		return IMAGES.get(state);
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof Ball)
			return ((Ball) o).getColorID().equals(colorID);
		return false;
	}
	
	public static final Ball EMPTY_BALL = new Ball(ColorID.TRANSPARENT);
}
