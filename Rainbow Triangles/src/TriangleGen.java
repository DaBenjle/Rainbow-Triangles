import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

public class TriangleGen
{

	private static final int MIN_LINES_PER_POINT = 3;
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
				if(distanceBetweenPoints(curCoord, newCoord) < maxLineDistance && !curCoord.linesTo.contains(newCoord))
				{
					graphics.setColor(Color.WHITE);
					graphics.drawLine(curCoord.x, curCoord.y, newCoord.x, newCoord.y);
					curCoord.linesTo.add(newCoord);
					newCoord.linesTo.add(curCoord);
					lines++;
				}
			}
		}
		for(int i = 0; i < coords.size(); i++)
		{
			Coordinate curCoord = coords.get(i);
			if(curCoord.linesTo.size() < MIN_LINES_PER_POINT)
			{
				ArrayList<Coordinate> coordsWithoutAttached = new ArrayList<>();
				coordsWithoutAttached = deepCopy(coords);
				coordsWithoutAttached.removeAll(curCoord.linesTo);
				coordsWithoutAttached.remove(curCoord);
				ArrayList<Coordinate> nearPoints = getNearestPoints(coordsWithoutAttached, MIN_LINES_PER_POINT - curCoord.linesTo.size(), curCoord);
				for(int f = 0; f < nearPoints.size(); f++)
				{
					Coordinate nearCoord = nearPoints.get(f);
					graphics.setColor(Color.WHITE);
					graphics.drawLine(curCoord.x, curCoord.y, nearCoord.x, nearCoord.y);
					curCoord.linesTo.add(nearCoord);
					nearCoord.linesTo.add(curCoord);
					lines++;
				}
			}
		}
		System.out.println("===\nLines: "  + lines);
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
			coords = new ArrayList<>();
			for(int i = 0; i < height; i++)
			{
				for(int j = 0; j < width; j++)
				{
					if(coords.size() < numOfPoints)
					{
						int randInt = random.nextInt(totalNumOfPoints);
						if(randInt < numOfPoints)
						{
							double curDis = minDistanceBetweenPoints(coords, width, height, j, i);
							if(curDis >= minDistance)
							{
								coords.add(new Coordinate(j, i, 0));//We add the z and value later.
							}
						}
					}
					else
					{
						break;
					}
				}
				if(coords.size() >= numOfPoints)
				{
					break;
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
		double dis = Integer.MAX_VALUE - 10;
		for(int i = 0; i < points.size(); i++)
		{
			double xDis = points.get(i).x - x;
			double yDis = points.get(i).y - y;
			dis = Math.sqrt((xDis * xDis) + (yDis * yDis));
			if(dis < minDistance)
			{
				minDistance = dis;
			}
		}
		return minDistance;
	}
	
	public static double distanceBetweenPoints(Coordinate p1, Coordinate p2)
	{
		double xDis = p1.x - p2.x;
		double yDis = p1.y - p2.y;
		return Math.sqrt(xDis * xDis + yDis * yDis);
	}
	
	public static ArrayList<Coordinate> getNearestPoints(ArrayList<Coordinate> input, int num, Coordinate from)
	{
		ArrayList<Coordinate> output = new ArrayList<>();
		for(int i = 0; i < input.size(); i++)
		{
			Coordinate inCoord = input.get(i);
			double inDis = distanceBetweenPoints(inCoord, from);
			double maxOutDis = -1;
			ArrayList<Double> outDistances = new ArrayList<>();
			if(output.size() >= num)
			{
				for(int f = 0; f < output.size(); f++)//gets the current lowest distances and adds them to outDistances, also maxDistance which is used to prevent unneccesary tests
				{
					double outDis = distanceBetweenPoints(output.get(f), from);
					outDistances.add(Double.valueOf(outDis));
					if(outDis > maxOutDis)
					{
						maxOutDis = outDis;
					}
				}
			}
			if(inDis < maxOutDis || maxOutDis == -1)//this will inject the out distances with the new lower distance, then remove the highest distance coord from output
			{
				outDistances.add(Double.valueOf(inDis));
				output.add(inCoord);
				if(maxOutDis != -1)
				{
					int index = outDistances.indexOf(getHighestValue(outDistances));
					output.remove(index);
				}
			}
		}
		return output;
	}
	
	public static <T extends Number> double getHighestValue(ArrayList<T> nums)
	{
		double highest = -1;
		for(int i = 0; i < nums.size(); i++)
		{
			if(nums.get(i).doubleValue() > highest)
			{
				highest = nums.get(i).doubleValue();
			}
		}
		return highest;
	}
	
	public static <T> ArrayList<T> deepCopy(ArrayList<T> objects)
	{
		ArrayList<T> output = new ArrayList<T>();
		for(int i = 0; i < objects.size(); i++)
		{
			output.add(objects.get(i));
		}
		return output;
	}

}
