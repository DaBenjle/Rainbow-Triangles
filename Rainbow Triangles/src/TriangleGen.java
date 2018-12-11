import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

public class TriangleGen
{

	public static BufferedImage generate(int width, int height, int numOfPoints, int minDistance)
	{
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = (Graphics2D)img.getGraphics();
		//v Sets image to all GREEN. v
		for(int i = 0; i < height; i++)
		{
			for(int j = 0; j < width; j++)
			{
				img.setRGB(j, i, 0x00ff00);
			}
		}
		ArrayList<Coordinate> coords = createCoords(width, height, numOfPoints, minDistance);
		Random random = new Random();
		for(int i = 0; i < coords.size(); i++)
		{
			int randHeight = random.nextInt(16 * 16);//value between 0 and ff
			coords.get(i).z = randHeight;
			String colorValueS = Integer.toHexString(randHeight);
			int colorValue = Integer.parseInt(colorValueS + "00" + colorValueS, 16);
			int radius = 5;
			graphics.setColor(new Color(colorValue));
			graphics.fillOval(coords.get(i).x + radius, coords.get(i).y + radius, radius * 2, radius * 2);
		}
		return img;
	}

	private static ArrayList<Coordinate> createCoords(int width, int height, int numOfPoints, int minDistance)
	{
		ArrayList<Coordinate> coords = new ArrayList<>();
		int totalNumOfPoints = width * height;
		Random random = new Random();
		for(int i = 0; i < height; i++)
		{
			for(int j = 0; j < width; j++)
			{
				int randInt = random.nextInt(totalNumOfPoints);
				if(randInt < numOfPoints && minDistanceBetweenPoints(coords, width, height, j, i) >= minDistance)
				{
					coords.add(new Coordinate(j, i, 0));//We add the z value later.
				}
			}
		}
		return coords;
	}

	private static double minDistanceBetweenPoints(ArrayList<Coordinate> points, int width, int height, int x, int y)
	{
		double minDistance = Integer.MAX_VALUE;
		for(int i = 0; i < points.size(); i++)
		{
			double xDis = points.get(i).x - x;
			double yDis = points.get(i).y - y;
			double dis = Math.sqrt(xDis * xDis - yDis * yDis);
			if(dis < minDistance)
			{
				minDistance = dis;
			}
		}
		return minDistance;
	}

}
