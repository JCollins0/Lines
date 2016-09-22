package colorMatch;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import com.jrc.highscoresapi.Database;

public class Main extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8735603061251310226L;
	public static void main(String[] args) {
		new Main();
	}
	
	public Main()
	{
		super("Lines v3.0");
		setIconImage(Utility.loadImage("icon"));
		setSize(576+16, 576+39+bar.getHeight()+HUD_HEIGHT);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		game = new Game(576,576+bar.getHeight()+HUD_HEIGHT);
		add(game);
		highScoresDataBase = new Database("scores.jrc");
		highScoresDataBase.read(GAME_ID);
		highScoresDataBase.setNumberOfScoresToKeep(GAME_ID, 10);
		highScoresDataBase.write(GAME_ID);
		
		setJMenuBar(bar);
		bar.add(menu);
		menu.add(highScores);
		menu.add(diffLabel);
		menu.add(diffItem);
		menu.add(textLabel);
		menu.add(textItem);
		textLabel.setFont(Game.FONT);
		diffLabel.setFont(Game.FONT);
		difficulty.setFont(Game.FONT);
		textures.setFont(Game.FONT);
		diffItem.add(difficulty);
		textItem.add(textures);
		menu.add(exit);
		exit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				highScoresDataBase.write(GAME_ID);
				System.exit(0);
			}
		});
		
		highScores.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				showHighScores();
			}
		});
		
		difficulty.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JButton b = (JButton)e.getSource();
				String text = b.getText();
				switch(text)
				{
				case "Medium": b.setText("Large");
					break;
				case "Small": b.setText("Medium");
					break;
				case "Large": b.setText("Small");
					break;
				}
				game.updateGrid(b.getText());
			}
			
		});
		
		textures.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JButton b = (JButton)e.getSource();
				String text = b.getText();
				switch(text)
				{
				case "Ball": b.setText("Classic");
					break;
				case "Classic": b.setText("Ball");
					break;
				}
				Ball.IMAGES = Utility.loadBufferedList(b.getText().toLowerCase()+"_a", 64, 64);
			}
			
		});
		setResizable(false);
		pack();
		setVisible(true);
		
		Thread game_thread = new Thread(game);
		game_thread.start();
	}
	
	public void showHighScores()
	{
		highScoresDataBase.displayHighScores(GAME_ID, getBoardSize());
	}
	
	public static int getBoardSize()
	{
		switch(difficulty.getText())
		{
		case "Small" : return 0;
		case "Medium": return 1;
		case "Large": return 2;
		}
		return -1;
	}
	
	private Game game;
	public static final int HUD_HEIGHT = 100;
	public static Database highScoresDataBase;
//	public static final Font font = new Font("Courier New", Font.BOLD, 20);
	public static final String GAME_ID = "lines";
//	public static final String[] options = {"Ok", "Clear Highscores"};
	private JMenuBar bar = new JMenuBar();
	private JMenuItem highScores = new JMenuItem("HighScores");
	private JMenu menu = new JMenu("File");
	private JMenuItem exit = new JMenuItem("Exit");
	private JMenuItem diffItem = new JMenuItem("                                    ");
	private JMenuItem textItem = new JMenuItem("                                    ");
	private static JButton difficulty = new JButton("Medium");
	private JButton textures = new JButton("Classic");
	private JLabel textLabel = new JLabel("Images:");
	private JLabel diffLabel = new JLabel("Board Size:");
}
