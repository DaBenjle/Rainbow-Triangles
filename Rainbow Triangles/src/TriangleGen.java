import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.util.Collections;
import java.util.Comparator;

public class TriangleGen
{

	private static final int MIN_LINES_PER_POINT = 3;
	private static final int ATTEMPTS_TO_DISTRIBUTE = 15;

	public static BufferedImage[] generate(int width, int height, int numOfPoints, int minDistance, int maxLineDistance)
	{
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = (Graphics2D) img.getGraphics();
		ArrayList<Coordinate> coords = Coordinate.createCoords(width, height, numOfPoints, minDistance);
		ArrayList<Line> lines = new ArrayList<Line>();
		colorCircles(coords, graphics);
		Line.genLines(coords, lines, maxLineDistance);
		BufferedImage b4 = new BufferedImage(img.getColorModel(), img.copyData(null), img.isAlphaPremultiplied(), null);
		Line.drawLines(lines, (Graphics2D)b4.getGraphics());
		Line.removeCrossingLines(lines);
		Line.drawLines(lines, graphics);
		Collections.shuffle(lines);
		BufferedImage[] imgs = {b4, img};
		return imgs;
	}

	private static void colorCircles(ArrayList<Coordinate> coords, Graphics2D graphics)
	{
		Random random = new Random();
		for (int i = 0; i < coords.size(); i++)
		{
			Coordinate curCoord = coords.get(i);
			double darknessMulti = random.nextDouble();
			curCoord.z = (int) (darknessMulti * 100);
			int red = (int) (0xff * darknessMulti);
			int green = 0x0;
			int blue = (int) (0xae * darknessMulti);
			int colorValue = Integer.parseInt(red + String.valueOf(green) + blue, 16);
			int radius = 50;
			graphics.setColor(new Color(colorValue));
			graphics.fillOval(curCoord.x - radius, curCoord.y - radius, radius * 2, radius * 2);
		}
	}

	private static <T> ArrayList<T> shrinkArray(ArrayList<T> input, int index)
	{
		ArrayList<T> list = new ArrayList<>();
		for (int i = 0; i < input.size() - 1; i++)
		{
			if (i < index)
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

	public static <T extends Number> double getHighestValue(ArrayList<T> nums)
	{
		double highest = -1;
		for (int i = 0; i < nums.size(); i++)
		{
			if (nums.get(i).doubleValue() > highest)
			{
				highest = nums.get(i).doubleValue();
			}
		}
		return highest;
	}

	private static class Coordinate
	{
		public int x, y, z;
		public ArrayList<Coordinate> linesTo = new ArrayList<>();

		public Coordinate(int x, int y, int z)
		{
			this.x = x;
			this.y = y;
			this.z = z;
		}

		public String toString()
		{
			String lines = "";
			for (int i = 0; i < linesTo.size(); i++)
			{
				lines += linesTo.get(i).toStringShort() + ' ';
			}
			return "X: " + x + " Y: " + y + " Z: " + z + " (Lines To: " + lines + ")";
		}

		private String toStringShort()
		{
			return "X: " + x + " Y: " + y + " Z: " + z;
		}

		public static Coordinate getHighestPoint(Coordinate... coords)
		{
			//Technically the lowest y value, but this makes it the highest on the screen
			Coordinate highest = new Coordinate(-1, -1, -1);
			for (Coordinate cur : coords)
			{
				if (cur.y > highest.y)
				{
					highest = cur;
				}
			}
			return highest;
		}

		public static ArrayList<Coordinate> getNearestPoints(ArrayList<Coordinate> input, int num, Coordinate from)
		{
			ArrayList<Coordinate> output = new ArrayList<>();
			for (int i = 0; i < input.size(); i++)
			{
				Coordinate inCoord = input.get(i);
				double inDis = distanceBetweenPoints(inCoord, from);
				double maxOutDis = -1;
				ArrayList<Double> outDistances = new ArrayList<>();
				if (output.size() >= num)
				{
					for (int f = 0; f < output.size(); f++)// gets the current lowest distances and adds them to
															// outDistances, also maxDistance which is used to prevent
															// unneccesary tests
					{
						double outDis = distanceBetweenPoints(output.get(f), from);
						outDistances.add(Double.valueOf(outDis));
						if (outDis > maxOutDis)
						{
							maxOutDis = outDis;
						}
					}
				}
				if (inDis < maxOutDis || maxOutDis == -1)// this will inject the out distances with the new lower
															// distance, then remove the highest distance coord from
															// output
				{
					outDistances.add(Double.valueOf(inDis));
					output.add(inCoord);
					if (maxOutDis != -1)
					{
						int index = outDistances.indexOf(getHighestValue(outDistances));
						output.remove(index);
					}
				}
			}
			return output;
		}

		public static double distanceBetweenPoints(Coordinate p1, Coordinate p2)
		{
			double xDis = p1.x - p2.x;
			double yDis = p1.y - p2.y;
			return Math.sqrt(xDis * xDis + yDis * yDis);
		}

		private static double minDistanceBetweenPoints(ArrayList<Coordinate> points, int width, int height, int x,
				int y)
		{
			double minDistance = Integer.MAX_VALUE;
			double dis = Integer.MAX_VALUE - 10;
			for (int i = 0; i < points.size(); i++)
			{
				double xDis = points.get(i).x - x;
				double yDis = points.get(i).y - y;
				dis = Math.sqrt((xDis * xDis) + (yDis * yDis));
				if (dis < minDistance)
				{
					minDistance = dis;
				}
			}
			return minDistance;
		}

		private static ArrayList<Coordinate> createCoords(int width, int height, int numOfPoints, int minDistance)
		{
			ArrayList<Coordinate> coords = new ArrayList<>();
			int totalNumOfPoints = width * height;
			Random random = new Random();
			int attempts = 0;
			for (int f = 0; f < ATTEMPTS_TO_DISTRIBUTE; f++)
			{
				coords = new ArrayList<>();
				for (int i = 0; i < height; i++)
				{
					for (int j = 0; j < width; j++)
					{
						if (coords.size() < numOfPoints)
						{
							int randInt = random.nextInt(totalNumOfPoints);
							if (randInt < numOfPoints)
							{
								double curDis = minDistanceBetweenPoints(coords, width, height, j, i);
								if (curDis >= minDistance)
								{
									coords.add(new Coordinate(j, i, 0));// We add the z and value later.
								}
							}
						}
						else
						{
							break;
						}
					}
					if (coords.size() >= numOfPoints)
					{
						break;
					}
				}
				attempts = f + 1;
				if (coords.size() >= numOfPoints)
				{
					break;
				}
			}
			System.out.println("Number of attempts to distribute points: " + attempts + ". Points plotted: "
					+ coords.size() + ".");
			return coords;
		}
	}

	private static class Triangle
	{

	}

	private static enum Orientation
	{
		CLOCKWISE, COUNTER_CLOCKWISE;
	}

	public static <T> ArrayList<T> deepCopy(ArrayList<T> list)
	{
		ArrayList<T> answer = new ArrayList<T>();
		for (T el : list)
		{
			answer.add(el);
		}
		return answer;
	}

	private static class Line
	{
		private int p1x, p1y, p2x, p2y;
		public Coordinate p1, p2;
		
		public Line(int p1x, int p1y, int p2x, int p2y)
		{
			this.p1x = p1x;
			this.p1y = p1y;
			this.p2x = p2x;
			this.p2y = p2y;
			p1 = new Coordinate(p1x, p1y, 0);
			p2 = new Coordinate(p2x, p2y, 0);
		}

		/*
		 * @returns the orientation of the shape created by @param Orientation being the
		 * direction of the points in the order p1, p2, p3
		 */
		public static Orientation getOrientation(Coordinate p1, Coordinate p2, Coordinate p3)
		{
			Coordinate highestPoint = Coordinate.getHighestPoint(p1, p2, p3);
			if(highestPoint.equals(p1))
			{
				if (p2.x > p3.x)
				{
					return Orientation.CLOCKWISE;
				}
				else
				{
					return Orientation.COUNTER_CLOCKWISE;
				}
			}
			else if (highestPoint.equals(p2))
			{
				return Orientation.CLOCKWISE;
			}
			else if (highestPoint.equals(p3))
			{
				return Orientation.COUNTER_CLOCKWISE;
			}
			return null;
		}

		public static void genLines(ArrayList<Coordinate> coords, ArrayList<Line> lines, int maxLineDistance)
		{
			// draws initial close lines
			for (int i = 0; i < coords.size(); i++)
			{
				Coordinate curCoord = coords.get(i);
				ArrayList<Coordinate> lessCoords = shrinkArray(coords, i);
				for (int j = 0; j < lessCoords.size(); j++)
				{
					Coordinate newCoord = lessCoords.get(j);
					if (Coordinate.distanceBetweenPoints(curCoord, newCoord) < maxLineDistance
							&& !curCoord.linesTo.contains(newCoord))
					{
						curCoord.linesTo.add(newCoord);
						newCoord.linesTo.add(curCoord);
						lines.add(new Line(curCoord.x, curCoord.y, newCoord.x, newCoord.y));
					}
				}
			}

			// if a coord does not have enough lines, then it draws more, may be the cause
			// of abnormally long lines.
			for (int i = 0; i < coords.size(); i++)
			{
				Coordinate curCoord = coords.get(i);
				if (curCoord.linesTo.size() < MIN_LINES_PER_POINT)
				{
					ArrayList<Coordinate> coordsWithoutAttached = new ArrayList<>();
					coordsWithoutAttached = deepCopy(coords);
					coordsWithoutAttached.removeAll(curCoord.linesTo);
					coordsWithoutAttached.remove(curCoord);
					ArrayList<Coordinate> nearPoints = Coordinate.getNearestPoints(coordsWithoutAttached,
							MIN_LINES_PER_POINT - curCoord.linesTo.size(), curCoord);
					for (int f = 0; f < nearPoints.size(); f++)
					{
						Coordinate nearCoord = nearPoints.get(f);
						curCoord.linesTo.add(nearCoord);
						nearCoord.linesTo.add(curCoord);
						lines.add(new Line(curCoord.x, curCoord.y, nearCoord.x, nearCoord.y));
					}
				}
			}
		}

		private static void drawLines(ArrayList<Line> lines, Graphics2D graphics)
		{
			graphics.setColor(Color.WHITE);
			for (Line line : lines)
			{
				graphics.drawLine(line.p1x, line.p1y, line.p2x, line.p2y);
			}
		}

		// see https://www.geeksforgeeks.org/check-if-two-given-line-segments-intersect/
		public boolean crosses(Line line)
		{
			//true if they're the same
			boolean firstDif = getOrientation(p1, p2, line.p1) == getOrientation(p1, p2, line.p2);
			boolean secondDif = getOrientation(line.p1, line.p2, p1) == getOrientation(line.p1, line.p2, p2);
			return !firstDif && !secondDif;
		}

		public static void removeCrossingLines(ArrayList<Line> lines)
		{
			// Instead of just shuffling, i sort it from longest to shortest, that way the
			// longest lines that cross are removed first. Because they are more likely to 
			// cross multiple lines, meaning that they are more valuable to remove because less lines are removed.
			lines.sort(new SortByLength().reversed());
			for (int i = 0; i < lines.size(); i++)
			{
				try
				{
					Line line = lines.get(i);
					//creates another list without the current line
					ArrayList<Line> shorter = shrinkArray(lines, i); 
					for(int j = 0; j < shorter.size(); j++)
					{
						Line curLine = shorter.get(j);
						if(line.crosses(curLine) && !line.connectsTo(curLine))
						{
							lines.remove(line); //not cur line because line is longer than cur line, see above comments
							break;
						}
					}
				}catch(IndexOutOfBoundsException ex){break;}
			}
		}

		private boolean connectsTo(Line curLine)
		{
			return p1.equals(curLine.p1) || p1.equals(curLine.p2) || p2.equals(curLine.p1) || p2.equals(curLine.p2);
		}

		private static class SortByLength implements Comparator<Line>
		{
			public int compare(Line a, Line b)
			{
				double aL = Coordinate.distanceBetweenPoints(new Coordinate(a.p1x, a.p1y, 0),
						new Coordinate(a.p2x, a.p2y, 0));
				double bL = Coordinate.distanceBetweenPoints(new Coordinate(b.p1x, b.p1y, 0),
						new Coordinate(b.p2x, b.p2y, 0));
				if (aL < bL)
				{
					return -1;
				}
				else if (aL == bL)
				{
					return 0;
				}
				else
				{
					return 1;
				}
			}
		}
	}

}
