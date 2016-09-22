package colorMatch;

import java.awt.Graphics;
import java.awt.Rectangle;

public class Tile{
	
	public static int H , W;
	Ball ball;
	Rectangle bounds;
	private int state, count;
	
//	private Tile left,right,up,down;
//	private Tile parent;
//	private int cost;
	
	static
	{
		H = Game.height_ratio;
		W = Game.width_ratio;
	}
	
	public Tile(int x, int y)
	{
		bounds = new Rectangle(x, y, W, H);
		ball = Ball.EMPTY_BALL;
	}
	
	public void render(Graphics g)
	{
		if(ball != null && ball != Ball.EMPTY_BALL)
		{
			g.setColor(ball.getColor());
			g.fillRect(bounds.x, bounds.y, W, H);
			g.drawImage(Ball.getImage(state), bounds.x, bounds.y, W, H, null);
		}
//		g.setColor(Color.black);
//		g.drawString(getCost()+"",bounds.x,bounds.y+bounds.height);
//		
//		if(parent != null)
//		{
//			g.setColor(Color.red);
//			g.drawLine((int)bounds.getCenterX(), (int)bounds.getCenterY(), (int)parent.getBounds().getCenterX(), (int)parent.getBounds().getCenterY());
//		}
	}
	
	public void update()
	{
		count++;
		if(count >= 90)
		{
			state++;
			if(state >= Ball.IMAGES.size())
				state = 0;
			count =0;
		}
	}

	public void setState(int state)
	{
		this.state = state;
	}
	
	public Ball getBall() {
		return ball;
	}

	public void setBall(Ball ball) {
		this.ball = ball;
	}

	public Rectangle getBounds() {
		return bounds;
	}

	public void setBounds(Rectangle bounds) {
		this.bounds = bounds;
	}
	
	public int getX()
	{
		return bounds.x;
	}
	
	public int getY()
	{
		return bounds.y;
	}

//	public Tile getLeft() {
//		return left;
//	}
//
//	public void setLeft(Tile left) {
//		this.left = left;
//	}
//
//	public Tile getRight() {
//		return right;
//	}
//
//	public void setRight(Tile right) {
//		this.right = right;
//	}
//
//	public Tile getUp() {
//		return up;
//	}
//
//	public void setUp(Tile up) {
//		this.up = up;
//	}
//
//	public Tile getDown() {
//		return down;
//	}
//
//	public void setDown(Tile down) {
//		this.down = down;
//	}
//
//	public Tile getParent() {
//		return parent;
//	}
//
//	public void setParent(Tile parent) {
//		this.parent = parent;
//	}
//
//	public int getCost() {
//		return cost;
//	}
//
//	public void setCost(int cost) {
//		this.cost = cost;
//	}
//
//	@Override
//	public int compareTo(Tile t) {
//		return cost-t.cost;
//	}
	
	public char getGridChar()
	{
		if(ball != null)
			switch(ball.getColorID())
			{
			case BLUE: return 'b';
			case GREEN: return 'g';
			case ORANGE: return 'o';
			case PINK: return 'p';
			case PURPLE: return 'P';
			case RED: return 'r';
			case YELLOW: return 'y';
			case TRANSPARENT: return '0';
			}
		return '0';
	}
	
	@Override
	public String toString() {
		return String.format("%d,%d",bounds.x/64,bounds.y/64);
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof Tile)
		{
			Tile t = (Tile)o;
			return t.getX()==getX() && t.getY() == getY();
		}
		return false;
	}
}
