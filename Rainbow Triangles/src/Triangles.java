import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Triangles
{
	private String fileName;
	private int width;
	private int height;
	
	public static void main(String[] args)
	{
		String inputF = args[0];
		int inputH = Integer.parseInt(args[2]);
		int inputW = Integer.parseInt(args[1]);
		new Triangles(inputF, inputW, inputH);
	}
	
	public Triangles(String inputF, int inputW, int inputH)
	{
		fileName = inputF;
		width = inputW;
		height = inputH;
		BufferedImage img = TriangleGen.generate(fileName, width, height);
		try
		{
			ImageIO.write(img, "png", new File(fileName + ".png"));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

}
