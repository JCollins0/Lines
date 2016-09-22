package colorMatch;

public class CPoint implements Comparable<CPoint> {

	public int x;
	public int y;
	
	public CPoint(int x, int y)
	{
		this.x =x;
		this.y =y;
	}
	
	
	
	@Override
	public int compareTo(CPoint cp) {
		int c = cp.x - x;
		if(c == 0)
		{
			c = cp.y - y;
			return c;
		}
		return c;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof CPoint)
		{
			CPoint c = (CPoint)o;
			return x == c.x && y == c.y;
		}
		return false;
	}

}
