package colorMatch;

import java.awt.Color;

public enum ColorID {
	BLUE(0x0000FF),
	GREEN(0x00FF00),
	RED(0xFF0000),
	PURPLE(0x660066),
	YELLOW(0xFFFF00),
	PINK(0xFF00FF),
	ORANGE(0xFF6A00),
	TRANSPARENT(0xFFFFFF);
	
	private Color color;
	ColorID(int color)
	{
		this.color = new Color(color);
	}
	
	public Color getColor()
	{
		return color;
	}
}
