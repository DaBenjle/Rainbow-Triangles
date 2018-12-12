import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

public class TriangleGen
{

	private static final int ATTEMPTS_TO_DISTRIBUTE = 15;
	
	public static BufferedImage generate(int width, int height, int numOfPoints, int minDistance, int maxLineDistance)
	{
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = (Graphics2D)img.getGraphics();
		ArrayList<Coordinate> coords = createCoords(width, height, numOfPoints, minDistance);
		Random random = new Random();
		
		int lines = 0;
		for(int i = 0; i < coords.size(); i++)
		{
			Coordinate curCoord = coords.get(i);
			double darknessMulti = random.nextDouble();
			curCoord.z = (int)(darknessMulti * 100);
			int red = (int)(0xff * darknessMulti);
			int green = 0x0;
			int blue = (int)(0xae * darknessMulti);
			int colorValue = Integer.parseInt(red + String.valueOf(green) + blue, 16);
			int radius = 50;
			graphics.setColor(new Color(colorValue));
			graphics.fillOval(curCoord.x - radius, curCoord.y - radius, radius * 2, radius * 2);
		}
		for(int i = 0; i < coords.size(); i++)
		{
			Coordinate curCoord = coords.get(i);
			ArrayList<Coordinate> lessCoords = shrinkArray(coords, i);
			for(int j = 0; j < lessCoords.size(); j++)
			{
				Coordinate newCoord = lessCoords.get(j);
				if(distanceBetweenPoints(curCoord, newCoord) < maxLineDistance)
				{
					graphics.setColor(Color.WHITE);
					graphics.drawLine(curCoord.x, curCoord.y, newCoord.x, newCoord.y);
					lines++;
					System.out.println(i + " " + j);
				}
			}
		}
		System.out.println("===\n"  + lines);
		return img;
	}

	private static <T> ArrayList<T> shrinkArray(ArrayList<T> input, int index)
	{
		ArrayList<T> list = new ArrayList<>();
		for(int i = 0; i < input.size() - 1; i++)
		{
			if(i < index)
			{
				list.add(input.get(i));
			}
			else
			{
				list.add(input.get(i + 1));
			}
		}
		return list;
	}

	private static ArrayList<Coordinate> createCoords(int width, int height, int numOfPoints, int minDistance)
	{
		ArrayList<Coordinate> coords = new ArrayList<>();
		int totalNumOfPoints = width * height;
		Random random = new Random();
		int attempts = 0;
		for(int f = 0; f < ATTEMPTS_TO_DISTRIBUTE; f++)
		{
			coords.clear();
			for(int i = 0; i < height; i++)
			{
				for(int j = 0; j < width; j++)
				{
					int randInt = random.nextInt(totalNumOfPoints);
					if(randInt < numOfPoints && minDistanceBetweenPoints(coords, width, height, j, i) >= minDistance && coords.size() < numOfPoints)
					{
						coords.add(new Coordinate(j, i, 0));//We add the z value later.
					}
				}
			}
			attempts = f + 1;
			if(coords.size() >= numOfPoints)
			{
				break;
			}
		}
		System.out.println("Number of attempts to distribute points: " + attempts + ". Points plotted: " + coords.size() + ".");
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
		if(minDistance == Integer.MAX_VALUE)
		{
			System.out.println("Uh Oh");
		}
		return minDistance;
	}
	
	public static double distanceBetweenPoints(Coordinate p1, Coordinate p2)
	{
		double xDis = p1.x - p2.x;
		double yDis = p1.y - p2.y;
		return Math.sqrt(xDis * xDis - yDis * yDis);
	}

}
