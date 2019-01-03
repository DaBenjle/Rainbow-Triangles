import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Triangles
{
	private String fileName;
	private int width, height, numOfPoints, minDistance;
	
	public static void main(String[] args)
	{
		String inputF = args[0];
		int inputW = Integer.parseInt(args[1]);
		int inputH = Integer.parseInt(args[2]);
		int inputNumOfPoints = Integer.parseInt(args[3]);
		int inputMinDistance = Integer.parseInt(args[4]);
		int inputMaxLineDistance = Integer.parseInt(args[5]);
		new Triangles(inputF, inputW, inputH, inputNumOfPoints, inputMinDistance, inputMaxLineDistance);
	}
	
	public Triangles(String inputF, int inputW, int inputH, int inputNumOfPoints, int inputMinDistance, int maxLineDistance)
	{
		fileName = inputF;
		width = inputW;
		height = inputH;
		numOfPoints = inputNumOfPoints;
		minDistance = inputMinDistance;
		BufferedImage[] imgs = TriangleGen.generate(width, height, numOfPoints, minDistance, maxLineDistance);
		BufferedImage img = imgs[1];
		BufferedImage b4 = imgs[0];
		try
		{
			ImageIO.write(img, "png", new File(fileName + ".png"));
			ImageIO.write(b4, "png", new File(fileName + "b4" + ".png"));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

}
