package colorMatch;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

public class Game extends Canvas implements MouseListener, Runnable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3229921231783687069L;
	Tile[][] grid;
	Tile from;
	Tile to;
	//Tile temp;
	boolean gameOver;
	BufferedImage buffer;
//	ColorID[] colors = new ColorID[]{ColorID.BLUE,ColorID.GREEN,ColorID.PINK,ColorID.ORANGE,ColorID.RED,ColorID.YELLOW};
	Random random;
	int ballCount = 0;
	Ball[] balls;
	public static int GRID_WIDTH = 9, GRID_HEIGHT = GRID_WIDTH;
	public static int width_ratio, height_ratio;
	private int neededBalls = Math.max(3,(int)(5.0/9.0*GRID_WIDTH));
	public static final Font FONT = new Font("Arial", Font.BOLD, 20);
	private int score;
	public static final Ball[] POSSIBLE_BALLS = {
		new Ball(ColorID.BLUE), new Ball(ColorID.GREEN),
		new Ball(ColorID.PINK), new Ball(ColorID.ORANGE),
		new Ball(ColorID.RED), new Ball(ColorID.YELLOW)};
	private Point ball = new Point();
	private char[][] marker;
	
	
	public Game(int width, int height)
	{
		addMouseListener(this);
		buffer = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
		setSize(width, height);
		adjustSize();
		linkGrid();
		random= new Random();
		balls = new Ball[3];
		marker = new char[GRID_HEIGHT][GRID_WIDTH];
		Arrays.fill(balls, Ball.EMPTY_BALL);
		for(int i = 0; i < marker.length; i++)
			Arrays.fill(marker[i],'0');
		addBalls(true);
	}
	
	public void updateGrid(String text) {
		switch(text)
		{
		case "Medium": GRID_HEIGHT = GRID_WIDTH = 9;
				balls = new Ball[3];
			break;
		case "Small": GRID_HEIGHT = GRID_WIDTH = 5;
				balls = new Ball[3];
			break;
		case "Large": GRID_HEIGHT = GRID_WIDTH = 14;
				balls = new Ball[7];
			break;
		}
		adjustSize();
		linkGrid();
		reset();
	}
	
	public void adjustSize()
	{
		grid = new Tile[GRID_HEIGHT][GRID_WIDTH];
		marker = new char[GRID_HEIGHT][GRID_WIDTH];
		for(int i = 0; i < marker.length; i++)
			Arrays.fill(marker[i],'0');
		width_ratio = getWidth()/GRID_WIDTH;
		height_ratio = (getHeight()-Main.HUD_HEIGHT)/GRID_HEIGHT;
		Tile.H = height_ratio;
		Tile.W = width_ratio;
		neededBalls = Math.max(3,(int)(5.0/9.0*GRID_WIDTH));
	}
	
	public void linkGrid()
	{
		for(int y = 0; y < grid.length; y++)
		{
			for(int x = 0; x < grid[y].length; x++)
			{
				grid[y][x] = new Tile(x * width_ratio,y * height_ratio+Main.HUD_HEIGHT);
				
//				if(y-1 >= 0 && grid[y-1][x] != null)
//				{
//					grid[y][x].setUp(grid[y-1][x]);
//					grid[y-1][x].setDown(grid[y][x]);
//				}
//				if(y+1 < grid.length && grid[y+1][x] != null)
//				{
//					grid[y][x].setDown(grid[y+1][x]);
//					grid[y+1][x].setUp(grid[y][x]);
//				}
//				if(x-1 >= 0 && grid[y][x-1] != null)
//				{
//					grid[y][x].setLeft(grid[y][x-1]);
//					grid[y][x-1].setRight(grid[y][x]);
//				}
//				if(x+1 < grid[y].length && grid[y][x+1] != null)
//				{
//					grid[y][x].setRight(grid[y][x+1]);
//					grid[y][x+1].setLeft(grid[y][x]);
//				}
//				
				
			}
		}
	}
	
	public void addBalls(boolean place)
	{
		
		for(int i = 0; i < balls.length; i++)
		{
			if(balls[i] == Ball.EMPTY_BALL)
				balls[i] = POSSIBLE_BALLS[random.nextInt(POSSIBLE_BALLS.length)];
		}
		
		if(place)
			placeBalls();
		
		if(ballCount >= grid.length * grid[0].length)
		{
			if(!gameOver)
			{
				Main.highScoresDataBase.addScore(Main.GAME_ID, Main.getBoardSize(), score, new Date(System.currentTimeMillis()));//(Main.getBoardSize(),score);
				Main.highScoresDataBase.write(Main.GAME_ID);
			}
			gameOver=true;
		}
	}
	
	public void placeBalls()
	{
		for(int i = 0; i < balls.length && ballCount < grid.length * grid[0].length; i++)
		{
			int x = random.nextInt(grid[0].length);
			int y = random.nextInt(grid.length);

			while(grid[y][x].getBall() != Ball.EMPTY_BALL)
			{
				x = random.nextInt(grid[0].length);
				y = random.nextInt(grid.length);
			}
			grid[y][x].setBall(balls[i]);
			marker[y][x] = grid[y][x].getGridChar();
			balls[i] = Ball.EMPTY_BALL;
			ballCount++;
			checkForFiveOrMore(x, y);
		}
		
		addBalls(false);
	}
	
//	public ColorID getColor()
//	{
//		return colors[random.nextInt(colors.length)];
//	}
	
	public void render()
	{
		BufferStrategy bs = getBufferStrategy();
		
		if(bs == null)
		{
			createBufferStrategy(3);
			return;
		}
		
		Graphics b = buffer.getGraphics();
		b.setColor(Color.white);
		b.fillRect(0, 0, buffer.getWidth(), buffer.getHeight());
		b.setColor(Color.black);
		for(int i = 0; i < GRID_WIDTH+1; i++) {
			b.drawLine(i * width_ratio , Main.HUD_HEIGHT, i * width_ratio, getHeight());
		}
		for(int i = 0; i < GRID_HEIGHT+1; i++) {
			b.drawLine(0, i * height_ratio+Main.HUD_HEIGHT, buffer.getWidth(), i * height_ratio+Main.HUD_HEIGHT);
		}
		
		for(int y = 0; y < grid.length; y++)
		{
			for(int x = 0; y < grid.length && x < grid[y].length; x++)
			{
				if(grid[y][x] != null)
					grid[y][x].render(b);
			}
		}
		
		b.setColor(Color.black);
		b.setFont(FONT);
		b.drawString("Next Balls: ",10,47 );
		
		for(int i = 0; i < balls.length; i++)
		{
			if(balls[i]!= null)
			{
				b.setColor(Color.black);
				b.drawRect(i*32+128+i*2-1,24,32,32);
				b.setColor(balls[i].getColor());
				b.fillRect(i*32+128+i*2,25,32,32);
				b.drawImage(Ball.getImage(0),i*32+128+i*2,25,32,32,null);
			}
		}
		b.setColor(Color.black);
		b.drawString("Score: " + score,getWidth()-175,47 );
		
		if(gameOver)
		{
			b.setColor(new Color(250,250,250,200));
			b.fillRect(getWidth()/4,getHeight()/2, getWidth()/2+ String.valueOf(score).length()*5, 25);
			b.fillRect(getWidth()/4,getHeight()/2+20, "Click anywhere to play again".length() * 10, 25);
			b.setColor(Color.black);
			b.drawString("Game Over. Final Score: " + score, getWidth()/4 ,getHeight()/2+20);
			b.drawString("Click anywhere to play again", getWidth()/4 ,getHeight()/2+40);
		}
		
		Graphics g = bs.getDrawGraphics();
		g.drawImage(buffer, 0, 0, buffer.getWidth(), buffer.getHeight(), null);
		g.dispose();
		bs.show();
		
	}
	
	public Tile getTile(int x, int y)
	{
		if(y < Main.HUD_HEIGHT)
			return null;
		return grid[(y-Main.HUD_HEIGHT)/height_ratio][x/width_ratio];
	}
	
	public Tile getGridTile(int x, int y)
	{
		return grid[y][x];
	}
	
	
	public boolean moveBall2(int x, int y)
	{
		
		if(getGridTile(x, y).equals(to))
		{
			marker[y][x] = from.getGridChar();
			to.setBall(from.getBall());
			from.setBall(Ball.EMPTY_BALL);
			ball.x = x;
			ball.y = y;
			return true;
		}
		else if(!getGridTile(x,y).equals(from) && (Character.isAlphabetic(marker[y][x])|| marker[y][x] == '.'))
		{
			return false;
		}
		else
		{
			ball.x = x;
			ball.y = y;
			marker[y][x] = '.';
			
			if(to.getY() < from.getY())
			{
				if(to.getX() < from.getX())
				{
					if(x-1>=0 && moveBall2(x-1,y))
						return true;
					else if(y-1 >=0 && moveBall2(x,y-1))
						return true;
					else if(x+1 < grid[0].length && moveBall2(x+1,y))
						return true;
					else if(y+1 < grid.length && moveBall2(x,y+1))
						return true;
					else
					{
						ball.x = x;
						ball.y = y;
						return false;
					}
					
				}else
				{
					if(x+1 < grid[0].length && moveBall2(x+1,y))
						return true;
					else if(y-1 >=0 && moveBall2(x,y-1))
						return true;
					else if(x-1>=0 && moveBall2(x-1,y))
						return true;
					else if(y+1 < grid.length && moveBall2(x,y+1))
						return true;
					else
					{
						ball.x = x;
						ball.y = y;
						return false;
					}
				}
			}else
			{
				if(to.getX() < from.getX())
				{
					if(x-1>=0 && moveBall2(x-1,y))
						return true;
					else if(y+1 < grid.length && moveBall2(x,y+1))
						return true;
					else if(x+1 < grid[0].length && moveBall2(x+1,y))
						return true;
					else if(y-1 >=0 && moveBall2(x,y-1))
						return true;
					else
					{
						ball.x = x;
						ball.y = y;
						return false;
					}
				}else
				{
					if(x+1 < grid[0].length && moveBall2(x+1,y))
						return true;
					else if(y+1 < grid.length && moveBall2(x,y+1))
						return true;
					else if(x-1>=0 && moveBall2(x-1,y))
						return true;
					else if(y-1 >=0 && moveBall2(x,y-1))
						return true;
					else
					{
						ball.x = x;
						ball.y = y;
						return false;
					}
				}
			}
			
		}
	}
	
/*	private PriorityQueue<Tile> open= new PriorityQueue<Tile>();;
//	private ArrayList<Tile> closed= new ArrayList<Tile>();
//	
//	public void moveBall()
//	{
//		open.clear();
//		closed.clear();
//		open.add(to);
//		int step = 0;
//		while(open.size() > 0)
//		{
//			Tile current = open.poll();
//			closed.add(current);
//			if(current.equals(from))
//				break;
//			List<Tile> neighbor = getNeighbors(current);
//			//System.out.println("CURRENT: " + current + " " + neighbor.size());
//			
//			for(Tile t : neighbor)
//			{
//			//	System.out.println(closed);
//			//	System.out.println(closed.contains(t) + " " + t);
//				if(!closed.contains(t))
//				{
//					t.setCost(step + getCost(t, from));
//					t.setParent(current);
//					open.add(t);
//				}else if(step + getCost(t,from) < t.getCost())
//				{
//					closed.remove(t);
//					open.add(t);
//				}
//			}
//			step++;
//			//System.out.println("OPENSIZE: " + open.size());
//		}
//		
//		Tile te = from;
////		System.out.println("TO: " + from);
////		System.out.println("FROM_PARENT: " + te.getParent());
//		boolean canReach = false;
//		while(te.getParent() != null)
//		{
//			te.getParent().setBall(te.getBall());
//			te.getParent().setState(0);
//			te.setBall(null);
//			try
//			{
//				Thread.sleep(10);
//			}catch(InterruptedException e)
//			{
//				e.printStackTrace();
//			}
//			te = te.getParent();
////			System.out.println(te);
//			canReach = true;
//		}
//		if(canReach)
//		{
//			if(!checkForFiveOrMore())
//				addBalls(true);
//		}
//	}
	
//	public int getCost(Tile t , Tile to)
//	{
//		return Math.abs(t.getX() / width_ratio - to.getX()/width_ratio) + Math.abs(t.getY()/height_ratio-to.getY()/height_ratio);
//	}
	
//	public List<Tile> getNeighbors(Tile tile)
//	{
//		List<Tile> list = new ArrayList<Tile>();
//		if(tile.getLeft() != null && (tile.getLeft().getBall() == null || tile.getLeft().equals(from)))
//			list.add(tile.getLeft());
//		if(tile.getRight() != null && (tile.getRight().getBall() == null ||tile.getRight().equals(from)))
//			list.add(tile.getRight());
//		if(tile.getDown() != null && (tile.getDown().getBall() == null || tile.getDown().equals(from)))
//			list.add(tile.getDown());
//		if(tile.getUp() != null && (tile.getUp().getBall() == null || tile.getUp().equals(from)))
//			list.add(tile.getUp());
//		return list;
//		
//	}
 * 
 */
	
	
	public boolean checkForFiveOrMore()
	{
		return checkForFiveOrMore(to.getX()/width_ratio,(to.getY()-Main.HUD_HEIGHT)/height_ratio);
	}
	
//	private List<CPoint> pts = new ArrayList<CPoint>();
	public boolean checkForFiveOrMore(int x, int y)
	{
		
		char c = marker[y][x];
//		System.out.println(c);
		
		boolean removed = false;
		int ballNegCount = 0, ballPosCount = 0, totalRemovedCount=0;
		
		for(int t = y; t < grid.length && marker[t][x] == c; t++) // down
		{
			if(t != y && marker[t][x] == c)
				ballPosCount++;
		}
		
		for(int t = y; t >= 0  && marker[t][x] == c; t--) //up
		{
			if(t != y && marker[t][x] == c)
				ballNegCount++;
		}
		
//		System.out.println("Ball count: " + (ballNegCount + ballPosCount + 1) );
		if((ballNegCount + ballPosCount + 1) >= neededBalls)
		{
			removed = true;
			totalRemovedCount+= (ballNegCount + ballPosCount);
			for(int t = y - ballNegCount; t < y + ballPosCount+1; t++)
			{
				if(t != y)
				{
					marker[t][x] = '0';
					grid[t][x].setBall(Ball.EMPTY_BALL);
				}
			}
		}
		
		ballNegCount = 0;
		ballPosCount = 0;
		
		for(int t = x; t < grid[y].length && marker[y][t] == c; t++)
			if(t != x && marker[y][t] == c)
				ballPosCount++;
		for(int t = x; t >= 0 && marker[y][t] == c; t--)
			if(t != x && marker[y][t] == c)
				ballNegCount++;
		
		if((ballNegCount + ballPosCount + 1) >= neededBalls)
		{
			removed = true;
			totalRemovedCount+= (ballNegCount + ballPosCount);
			for(int t = x - ballNegCount; t < x + ballPosCount+1; t++)
			{
				if(t != x)
				{
					marker[y][t] = '0';
					grid[y][t].setBall(Ball.EMPTY_BALL);
				}
			}
		}
		
		ballNegCount = 0;
		ballPosCount = 0;
		
		for(int t = x, r = y; r < grid.length && t < grid[r].length && marker[r][t] == c;r++, t++)
			if( (t != x && r != y )&& marker[r][t] == c)
				ballPosCount++;
		for(int t = x, r = y; r >= 0 && t >= 0 && marker[r][t] == c;r--, t--)
			if( (t != x && r != y )&& marker[r][t] == c)
				ballNegCount++;
		
		if((ballNegCount + ballPosCount + 1) >= neededBalls)
		{
			removed = true;
			totalRemovedCount+= (ballNegCount + ballPosCount);
			for(int t = x - ballNegCount, r = y - ballNegCount; t < x + ballPosCount+1 && r < y + ballPosCount + 1; r++, t++)
			{
				if(t != x && r != y)
				{
					marker[r][t] = '0';
					grid[r][t].setBall(Ball.EMPTY_BALL);
				}
			}
		}
		
		ballNegCount = 0;
		ballPosCount = 0;
		
		for(int t = x, r = y; r < grid.length && t >= 0 && marker[r][t] == c;r++, t--)
			if( (t != x && r != y )&& marker[r][t] == c)
				ballNegCount++;
		for(int t = x, r = y; r >= 0 && t < grid[r].length && marker[r][t] == c;r--, t++)
			if( (t != x && r != y )&& marker[r][t] == c)
				ballPosCount++;
		
		if((ballNegCount + ballPosCount + 1) >= neededBalls)
		{
			removed = true;
			totalRemovedCount+= (ballNegCount + ballPosCount);
			for(int t = x - ballNegCount, r = y + ballNegCount; t < x + ballPosCount+1 && r >= y - ballPosCount - 1; r--, t++)
			{
				if(t != x && r != y)
				{
					marker[r][t] = '0';
					grid[r][t].setBall(Ball.EMPTY_BALL);
				}
			}
		}
		
		if(removed)
		{
			totalRemovedCount+= 1;
			marker[y][x] = '0';
			grid[y][x].setBall(Ball.EMPTY_BALL);
//			System.out.println("Total Removed Count: " + totalRemovedCount);
			remove(totalRemovedCount);
			return true;
		}
//		pts.clear();
//		pts.add(new CPoint(x,y));
//		boolean check = false;
//		check = checkUpDown(x,y,pts) || check;
//		check = checkLeftRight(x,y,pts) || check;
//		check = checkDiagonalLeft(x,y,pts) || check;
//		check = checkDiagonalRight(x,y,pts) || check;
//		if(check)
//		{
//			remove(pts);
//			return true;
//		}
		return false;
	}
	/*
	public boolean checkUpDown(int x, int y, List<CPoint> pts )
	{
		Ball b = grid[y][x].getBall();
		Ball temp = null;
		int count = 1;
		List<CPoint> pt = new ArrayList<CPoint>();
		pt.add(new CPoint(x,y));
		for(int y1 = y; y1 >= 0; y1--)
		{
			temp = grid[y1][x].getBall();
			if(temp == null || !temp.equals(b))
				break;
			if(temp.equals(b) && temp!=b)
			{
				count++;
				pt.add(new CPoint(x,y1));
			}
		}
		for(int y2 = y; y2 < grid.length; y2++)
		{
			temp = grid[y2][x].getBall();
			if(temp == null || !temp.equals(b))
				break;
			if(temp.equals(b) && temp!=b)
			{
				count++;
				pt.add(new CPoint(x,y2));
			}
		}
		
		if(count >= neededBalls)
		{
			pts.addAll(pt);
			return true;
		}
		
		return false;
		
	}
	public boolean checkLeftRight(int x, int y, List<CPoint> pts )
	{
		Ball b = grid[y][x].getBall();
		Ball temp = null;
		int count = 1;
		List<CPoint> pt = new ArrayList<CPoint>();
		pt.add(new CPoint(x,y));
		for(int x1 = x; x1 >= 0; x1--)
		{
			temp = grid[y][x1].getBall();
			if(temp == null || !temp.equals(b))
				break;
			if(temp.equals(b) && temp!=b)
			{
				count++;
				pt.add(new CPoint(x1,y));
			}
		}
		for(int x2 = x; x2 < grid[y].length; x2++)
		{
			temp = grid[y][x2].getBall();
			if(temp == null || !temp.equals(b))
				break;
			if(temp.equals(b) && temp!=b)
			{
				count++;
				pt.add(new CPoint(x2,y));
			}
		}
		if(count >= neededBalls)
		{
			pts.addAll(pt);
			return true;
		}
		
		return false;
	}
	public boolean checkDiagonalLeft(int x, int y, List<CPoint> pts )
	{
		Ball b = grid[y][x].getBall();
		Ball temp = null;
		int count = 1;
		List<CPoint> pt = new ArrayList<CPoint>();
		pt.add(new CPoint(x,y));
		for(int x1 = x, y1 = y; x1 >= 0 && y1 >= 0; x1--, y1--)
		{
			temp = grid[y1][x1].getBall();
			if(temp == null || !temp.equals(b))
				break;
			if(temp.equals(b) && temp!=b)
			{
				count++;
				pt.add(new CPoint(x1,y1));
			}
		}
		for(int x2 = x, y2 = y; y2 < grid.length &&  x2 < grid[y2].length ; x2++, y2++)
		{
			temp = grid[y2][x2].getBall();
			if(temp == null || !temp.equals(b))
				break;
			if(temp.equals(b) && temp!=b)
			{
				count++;
				pt.add(new CPoint(x2,y2));
			}
		}
		if(count >= neededBalls)
		{
			pts.addAll(pt);
			return true;
		}
		
		return false;
	}
	public boolean checkDiagonalRight(int x, int y, List<CPoint> pts )
	{
		Ball b = grid[y][x].getBall();
		Ball temp = null;
		List<CPoint> pt = new ArrayList<CPoint>();
		pt.add(new CPoint(x,y));
		int count = 1;
		for(int x1 = x, y1 = y; x1 >= 0 && y1 < grid.length; x1--, y1++)
		{
			temp = grid[y1][x1].getBall();
			if(temp == null || !temp.equals(b))
				break;
			if(temp.equals(b) && temp!=b)
			{
				count++;
				pt.add(new CPoint(x1,y1));
			}
		}
		for(int x2 = x, y2 = y; y2 >= 0 &&  x2 < grid[y2].length ; x2++, y2--)
		{
			temp = grid[y2][x2].getBall();
			if(temp == null || !temp.equals(b))
				break;
			if(temp.equals(b) && temp!=b)
			{
				count++;
				pt.add(new CPoint(x2,y2));
			}
		}
		if(count >= neededBalls)
		{
			pts.addAll(pt);
			return true;
		}
		
		return false;
	}*/
	public void remove(int numRemoved)
	{ 
		
		switch(numRemoved)
		{
		case 14: score += 38;
		case 13: score += 34;
		case 12: score += 38;
		case 11: score += 26;
		case 10: score += 40;
		case 9: score += 14;
		case 8: score += 10;
		case 7: score += 6;
		case 6: score += 2;
		case 5: score += 2;
		case 4: score += 2;
		case 3: score += 6;
		}
//		Iterator<CPoint> p = pts.iterator();
//		while(p.hasNext())
//		{
//			CPoint pt = p.next();
//			grid[pt.y][pt.x].setBall(null);
//			p.remove();
//			
//		}
		ballCount-=numRemoved;
		if(ballCount == 0)
		{
			score+=100;
			addBalls(true);
		}
	}
	
	public void reset()
	{
		for(int y = 0; y < grid.length; y++)
		{
			for(int x = 0; x < grid[y].length; x++)
			{
				grid[y][x].setBall(Ball.EMPTY_BALL);
				grid[y][x].setState(0);
			}
		}
		
		resetGrid(marker, gameOver);
		
		score = 0;
		gameOver = false;
		
		for(int i = 0; i < balls.length; i++)
			balls[i] = Ball.EMPTY_BALL;
		ballCount = 0;
		addBalls(true);
		
		to = null;
		from = null;		
	}
	
	public void mouseClicked(MouseEvent e) {

	}

	public void mouseEntered(MouseEvent e) {

	}

	public void mouseExited(MouseEvent e) {

	}

	public void mousePressed(MouseEvent e) {
		
		Point p = e.getPoint();
		if(!gameOver)
		{

			Tile t= getTile(p.x, p.y);
			if(t == null)
				return;
			if(from == null || t.getBall()!= Ball.EMPTY_BALL)
			{
				if(from != null)
					from.setState(0);
				//System.out.println("FRONT IS NULL " + (p.x / 64 ) + " " + (p.y /64));
				if(t.getBall() != Ball.EMPTY_BALL)
				{
					from = t;
				}

			}else if(t.getBall() == Ball.EMPTY_BALL)
			{
				to = t;
				//System.out.println("SEARCHING " + (p.x / 64 ) + " " + (p.y /64));
				ball.x = from.getX()/width_ratio;
				ball.y = (from.getY()-Main.HUD_HEIGHT)/height_ratio;
//				System.out.println(to);
//				System.out.println(from);
//				System.out.println(ball.x + " " + ball.y);
				boolean found = false;
				found = moveBall2(ball.x,ball.y);
				if(!found)
				{	
					marker[(from.getY()-Main.HUD_HEIGHT)/height_ratio][from.getX()/width_ratio] = from.getGridChar();
					resetGrid(marker,gameOver);
				}else
				{
//					System.out.println(ball.x + " " + ball.y);
					resetGrid(marker,gameOver);
					if(!checkForFiveOrMore(ball.x, ball.y))
						addBalls(true);
				}
				//moveBall();
				
				
				to = null;
				from = null;
//				output(marker);
//				for(int y = 0; y < grid.length; y++)
//				{
//					for(int x = 0; x < grid[y].length; x++)
//					{
//						grid[y][x].setCost(0);
//						grid[y][x].setParent(null);
//					}
//				}
//				open.clear();
//				closed.clear();
			}else
			{
				to = null;
				from = null;
			}

		}else
		{
			reset();
		}
		
	}

	public void mouseReleased(MouseEvent e) {

	}

	public void output(char[][] g)
	{
		System.out.println("-----------------------");
		for(int y = 0; y < g.length; y++)
		{
			for(int x = 0; x < g[y].length; x++)
			{
				System.out.print(g[y][x]);
			}
			System.out.println();
		}
		System.out.println("-----------------------");
	}
	
	public void resetGrid(char[][] g, boolean gameOver)
	{
		for(int y = 0; y < g.length; y++)
		{
			for(int x = 0; x < g[y].length; x++)
			{
				if(g[y][x] == '.' || gameOver)
					g[y][x] = '0';
			}
		}
	}

	@Override
	public void run() {
		GameTimer gt = new GameTimer();
		while(true)
		{
			render();
			
			if(gt.getElapsedTime() > 60)
			{
				if(from != null)
				{
					from.update();
				}
			}
		}
	}

}
