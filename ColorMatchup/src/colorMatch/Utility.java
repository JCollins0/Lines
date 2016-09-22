package colorMatch;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class Utility {

	
	private Utility() {
		
	}
	
	public static BufferedImage loadImage(String fileName) {
		try {
			return ImageIO.read(Utility.class.getResource("resources/"+fileName + ".png"));
		} catch (Exception e) {
			return null;
		}
	}
	
	public static ArrayList<BufferedImage> loadBufferedList(String fileName, int w, int h)
	{
		ArrayList<BufferedImage> list= new ArrayList<BufferedImage>();
		BufferedImage b = loadImage(fileName);
		for(int y = 0; y < b.getHeight(); y+=h)
		{
			for(int x = 0; x < b.getWidth(); x+= w)
			{
				list.add(b.getSubimage(x, y, w, h));
			}
		}
		return list;
	}
}
