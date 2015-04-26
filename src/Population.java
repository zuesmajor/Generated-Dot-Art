import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class Population extends JFrame {

	private static int numPeeps = 1000;
	private static final int SX = 600;
	private static final int SY = 600;
	private static int ADIST = 1; // Acceptable distance
	private static int speed = 5;
	private static boolean clears = false;
	private static boolean RUNNING = false;
	private Random rand = new Random();
	private BufferedImage img;
	private static boolean DIAGANOLS = false;
	public static File file;
	public static BufferedImage fileImage;
	
	//stores the pixels of the loaded image
	public static List<Integer> pixels;
	//stores the points of the pixels
	public static List<Point> pixelPoints;
	
	public static List<Integer> newPixels;
	
	public Peeps[] people;
	private SwarmPanel swarmPanel;

	public Population() {
		super("Populate Pixel");
		setSize(1280, 800);
		people = new Peeps[numPeeps];

		reset();
		createGUI();
		img = new BufferedImage(1280, 800, BufferedImage.TYPE_INT_BGR);
		img.getGraphics().fillRect(0, 0, 1280, 800);
		Thread updater = new Thread(new Runnable() {
			public void run() {
				while (true) {
					if (RUNNING) {
						Graphics g = img.getGraphics();

						if (clears) {
							g.fillRect(0, 0, 1280, 800);
						}
						Random newRand = new Random();
						for (int i = 0; i < numPeeps; i++) {
							int rand = newRand.nextInt(pixels.size());
							people[i].setColor(new Color(pixels.get(rand)));
							people[i].setX(pixelPoints.get(rand).x);
							people[i].setY(pixelPoints.get(rand).y);
							people[i].update();
							people[i].draw(g);
						}
						swarmPanel.repaint();
					}
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						System.out.println("BOOOOOOM");
					}
				}
			}
		});
		updater.start();

		setLocationRelativeTo(null);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void reset() {
		people = new Peeps[numPeeps];

		for (int i = 0; i < numPeeps; i++) {
			people[i] = new Peeps(SX / 2, SY / 2, i);
		}
	}


	@SuppressWarnings("serial")
	private class SwarmPanel extends JPanel {
		public SwarmPanel() {

		}

		public void paint(Graphics g) {
			g.drawImage(img, 0, 0, null);
		}
	}

	public class Peeps {
		private int id;
		private int x, y;
		private int size = 10;
		private Color color;

		public Peeps(int x, int y, int id) {
			this.x = x;
			this.y = y;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public void setY(int y){
			this.y = y;
		}

		public void setX(int x){
			this.x = x;
		}

		public void setColor(Color c) {
			color = c;
		}


		public void update() {
			int delx = 0, dely = 0;
			int dir;
			while (true) {
				dir = rand.nextInt((DIAGANOLS) ? 8 : 4);
				//System.out.println(dir);
				switch (dir) {
				case 0:
					dely = -speed;
					break;
				case 1:
					delx = speed;
					break;
				case 2:
					dely = speed;
					break;
				case 3:
					delx = -speed;
					break;
				}
				//System.out.println("(" + delx + ", " + dely + ")");
				if (checkMove(delx, dely)) {
					System.out.println("(" + delx + ", " + dely + ")");
					x += delx;
					y += dely;
					return;
				}
				return;
				// x+=delx;
				// y+=dely;
				// return;
			}
		}

		private boolean checkMove(int delx, int dely) {
			for (int i = 0; i < numPeeps; i++) {
				if (i == id)
					continue;
				if (Math.abs((x + delx) - people[i].getX()) < ADIST && Math.abs((y + dely) - people[i].getY()) < ADIST) {
//					System.out.println("flies[" + id + "]: #" + i +
//					" is too close");
					return false;
				}
			}
			if (x + delx > 595 || x + delx < 0 || y + dely > 595 || y + dely < 0) {
				return false;
			}
			return true;
		}

		public void draw(Graphics g) {
			g.setColor(color);
			g.fillOval(x, y, size, size);
		}
	}

	public void createGUI() {
		JPanel optionPanel = new JPanel();
		
		//start button
		final JButton start = new JButton("Start");
		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (RUNNING) {
					RUNNING = false;
					start.setText("Start");
				} else {
					RUNNING = true;
					start.setText("Stop");
				}
			}
		});
		start.requestFocus();
		optionPanel.add(start);
	
		//gets the picture to recreate in the swarm
		final JButton open = new JButton("Open");
		
		//opens an image and stores the RGB values and points in 2 separate arraylists
		open.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
		        int returnValue = fileChooser.showOpenDialog(null);
		        if (returnValue == JFileChooser.APPROVE_OPTION) {
		          file = fileChooser.getSelectedFile();
		          try {
					fileImage = ImageIO.read(file); //gets the image you chose from the menu
					pixels = new ArrayList<Integer>(); //creates the Arraylist of pixels from the image
					pixelPoints = new ArrayList<Point>(); //adds each pixel point to an arraylist
					for(int y = 0; y < fileImage.getHeight(); y++){
						for(int x = 0; x < fileImage.getWidth(); x++){
							pixels.add(fileImage.getRGB(x, y));
							pixelPoints.add(new Point(x,  y));
						}
					}
					System.out.println("done loading");
				} catch (IOException c) {
					c.printStackTrace();
					System.out.println("No file yo");
					}
		        }
			}
		});
		optionPanel.add(open);

		swarmPanel = new SwarmPanel();

		setLayout(new BorderLayout());
		add(optionPanel, BorderLayout.NORTH);
		add(swarmPanel, BorderLayout.CENTER);
	}
	

	public static void main(String[] args) {
		new Population();
	}

}
