import java.util.ArrayList;

public class Coordinate
{
	public int x, y, z;
	public ArrayList<Coordinate> linesTo = new ArrayList<>();

	public Coordinate()
	{
		this(0, 0, 0);
	}

	public Coordinate(int x, int y, int z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public String toString()
	{
		String lines = "";
		for(int i = 0; i < linesTo.size(); i++)
		{
			lines += linesTo.get(i).toStringShort() + ' ';
		}
		return "X: " + x + " Y: " + y + " Z: " + z + " (Lines To: " + lines + ")"; 
	}

	private String toStringShort()
	{
		return "X: " + x + " Y: " + y + " Z: " + z;
	}
}
