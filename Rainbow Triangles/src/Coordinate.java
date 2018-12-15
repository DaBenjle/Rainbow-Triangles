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
}
