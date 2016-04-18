package bighero6;

import bigheroUI.HighscoreManager;
import com.sun.j3d.utils.timer.J3DTimer;
import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferStrategy;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.input.KeyCode;
import javax.swing.JFrame;

public class BigHero6 extends JFrame implements Runnable
{
	private static final long MAX_STATS_INTERVAL = 1000000000L;
	private static final int MAX_FRAME_SKIPS = 5;

	private static final int NUM_FPS = 10;
	private int pWidth;
	private int pHeight;
	private long statsInterval = 0L;
	private long prevStatsTime;
	private long totalElapsedTime = 0L;
	private long gameStartTime;
	private int timeSpentInGame = 0;

	private long frameCount = 0L;
	private final double[] fpsStore;
	private long statsCount = 0L;
	private double averageFPS = 0.0D;
	
	private long framesSkipped = 0L;
	private long totalFramesSkipped = 0L;
	private final double[] upsStore;
	private double averageUPS = 0.0D;

	private DecimalFormat df = new DecimalFormat("0.##");
	private DecimalFormat timedf = new DecimalFormat("0.####");
	private Thread animator;
	private volatile boolean running = false;
	private final long period;
	private final BayMax fred;
	private final Obstacles obs;
	private int boxesUsed = 0;

	private volatile boolean gameOver = false;
	private int score = 0;
	private Font font;
	private FontMetrics metrics;
	private boolean finishedOff = false;
	
	private volatile boolean isOverQuitButton = false;
	private Rectangle quitArea;
	private volatile boolean isOverPauseButton = false;
	private Rectangle pauseArea;
	private volatile boolean isPaused = false;
	
	private boolean genE = true;
	private GraphicsDevice gd;
	private Graphics gScr;
	private BufferStrategy bufferStrategy;
	private int direction_hero = 0;
        private boolean stored_data=false;
        private boolean level_msg_displayed=false;
        private String name;
        
        private HighscoreManager hm;
        private static int LEVEL_NOW=1;
        private static int ENEMY_KILL=0;
        private static final int LEVEL_LENGTH=10;
	
	public BigHero6(long period, HighscoreManager hm , String name)
	{
                super("Big Hero 6 GAME");
		this.name=name;
                this.period = period;
		initFullScreen();
	
		readyForTermination();
	
		this.obs = new Obstacles(this, this.pWidth, this.pHeight);
		this.fred = new BayMax(this.pWidth, this.pHeight, this.obs);
	
		this.hm=hm;
		
                addMouseListener(new MouseAdapter() 
		{
			public void mousePressed(MouseEvent e) 
			{
				testPress(e.getX(), e.getY());
			}
		});
		addMouseMotionListener(new MouseMotionAdapter() 
		{
			public void mouseMoved(MouseEvent e) 
			{
				testMove(e.getX(), e.getY());
			}
		});
	
		this.font = new Font("SansSerif", 1, 24);
		this.metrics = getFontMetrics(this.font);
	
		this.pauseArea = new Rectangle(this.pWidth - 100, this.pHeight - 45, 70, 15);
		this.quitArea = new Rectangle(this.pWidth - 100, this.pHeight - 20, 70, 15);
	
		this.fpsStore = new double[NUM_FPS];
		this.upsStore = new double[NUM_FPS];
		for (int i = 0; i < NUM_FPS; i++) 
		{
			this.fpsStore[i] = 0.0D;
			this.upsStore[i] = 0.0D;
		}
	
		gameStart();
	}
	
	private void initFullScreen()
	{
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		this.gd = ge.getDefaultScreenDevice();
	
		setUndecorated(true);
		setIgnoreRepaint(true);
		setResizable(false);
	
		if (!this.gd.isFullScreenSupported()) 
		{
			System.out.println("Full-screen exclusive mode not supported");
			System.exit(0);
		}
		this.gd.setFullScreenWindow(this);
	
		showCurrentMode();
	
		this.pWidth = getBounds().width;
		this.pHeight = getBounds().height;
	
		setBufferStrategy();
	}
	private void setBufferStrategy()
	{
		try
		{
			EventQueue.invokeAndWait(new Runnable() 
			{
				public void run() 
				{
					createBufferStrategy(10);
				}	 
			} );
		} 
		catch (Exception e) 
		{
			System.out.println("Error while creating buffer strategy");
			System.exit(0);
		}
		try
		{
			Thread.sleep(500L);
		}
		catch (InterruptedException ex) 
		{
		}
		this.bufferStrategy = getBufferStrategy();
	}
	
	private void readyForTermination()
	{
		addKeyListener(new KeyAdapter()
		{
			public void keyPressed(KeyEvent e)
			{
				int keyCode = e.getKeyCode();
				if ((keyCode == 27) || (keyCode == 81) || (keyCode == 35) || ((keyCode == 67) && (e.isControlDown())))
				{
					//BigHero6.access$202(BigHero6.this, false);
					running = false;
				}
				if (e.getKeyChar() == '9')
				{
					//BigHero6.access$302(BigHero6.this, 1);
					direction_hero=1;
				}
				if (e.getKeyChar() == '7')
				{
					//BigHero6.access$302(BigHero6.this, 7);
					direction_hero=7;
				}
				if (e.getKeyChar() == '1')
				{
					//BigHero6.access$302(BigHero6.this, 5);
					direction_hero=5;
				}
				if (e.getKeyChar() == '3')
				{
					//BigHero6.access$302(BigHero6.this, 3);
					direction_hero=3;
				}
				if (e.getKeyChar() == '4')
				{
					//BigHero6.access$302(BigHero6.this, 6);
					direction_hero=6;
				}
				if (e.getKeyChar() == '6')
				{
					//BigHero6.access$302(BigHero6.this, 2);
					direction_hero=2;
				}
				if (e.getKeyChar() == '8')
				{
					//BigHero6.access$302(BigHero6.this, 0);
					direction_hero=0;
				}
				if (e.getKeyChar() == '2')
				{
					//BigHero6.access$302(BigHero6.this, 4);
					direction_hero=4;
				}
				if (e.getKeyChar() == '5')
				{
					fred.fireBullet();
				}
                                if(e.getKeyCode() == KeyEvent.VK_ENTER)
                                {
                                       level_msg_displayed=false;
                                }
			}
		});
		Runtime.getRuntime().addShutdownHook(new Thread() 
		{
			public void run() 
			{
				//BigHero6.access$202(BigHero6.this, false);
				running=false;
				finishOff();
			}
		});
	}
	
	private void gameStart()
	{
		if ((this.animator == null) || (!this.running)) 
		{
			this.animator = new Thread(this);
			this.animator.start();
		}
	}
	
	private void testPress(int x, int y)
	{
		if (this.isOverPauseButton)
			this.isPaused = (!this.isPaused);
		else if (this.isOverQuitButton)
			this.running = false;
	}
	
	private void testMove(int x, int y)
	{
		if (this.running)
		{
			this.isOverPauseButton = (this.pauseArea.contains(x, y));
			this.isOverQuitButton = (this.quitArea.contains(x, y));
		}
	}
	
	public void setBoxNumber(int no)
	{
		this.boxesUsed = no;
	}
	
	@Override
	public void run()
	{
		long overSleepTime = 0L;
		int noDelays = 0;
		long excess = 0L;
	
		this.gameStartTime = J3DTimer.getValue();
		this.prevStatsTime = this.gameStartTime;
		long beforeTime = this.gameStartTime;
	
		this.running = true;
	
		while (this.running)
		{
                    try {
                        gameUpdate();
                        screenUpdate();
                        
                        long afterTime = J3DTimer.getValue();
                        long timeDiff = afterTime - beforeTime;
                        long sleepTime = this.period - timeDiff - overSleepTime;
                        
                        if (sleepTime > 0L)
                        {
                            try
                            {
                                Thread.sleep(sleepTime / 1000000L);
                            }
                            catch (InterruptedException ex)
                            {
                            }
                            overSleepTime = J3DTimer.getValue() - afterTime - sleepTime;
                        }
                        else
                        {
                            excess -= sleepTime;
                            overSleepTime = 0L;
                            noDelays++;
                            if (noDelays >= 16)
                            {
                                Thread.yield();
                                noDelays = 0;
                            }
                        }
                        beforeTime = J3DTimer.getValue();
                        
                        int skips = 0;
                        while ((excess > this.period) && (skips < MAX_FRAME_SKIPS))
                        {
                            excess -= this.period;
                            gameUpdate();
                            skips++;
                        }
                        this.framesSkipped += skips;
                        storeStats();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(BigHero6.class.getName()).log(Level.SEVERE, null, ex);
                    }
		}
		finishOff();
	}
	
	private void gameUpdate() throws InterruptedException
	{
            int TimeE = (int)(this.frameCount / 15L);
            if ((!this.isPaused) && (!this.gameOver))
            {
			this.obs.addEnemy(false);
			System.out.println("DIRECTION:"+this.direction_hero);
			this.gameOver = this.fred.NxtMove(this.direction_hero);
			System.out.println("TIMESPENT" + this.frameCount / 15L);
			
			if (((TimeE > 10) && (TimeE % 10 == 0))  && this.genE == true)
                        {
                            System.out.println("TIMESPENT RRRRRRRRRRRRRRRRRRRRRRRRR");
                            for(int i=0;i<LEVEL_NOW;i++)
                            {
                                this.obs.addEnemy(true);
                            }
                            this.genE = false;
                            ENEMY_KILL++;
                        }
            }
		
            if (TimeE % 10 == 1)
            {
		this.genE = true;
            }
            
            if(ENEMY_KILL==LEVEL_LENGTH)
            {
                BigHero6.LEVEL_NOW++;
		this.level_msg_displayed=true;
            }
    }
	
	private void screenUpdate()
	{
		try
		{
			this.gScr = this.bufferStrategy.getDrawGraphics();
			gameRender(this.gScr);
			this.gScr.dispose();
			if (!this.bufferStrategy.contentsLost())
				this.bufferStrategy.show();
			else 
			{
				System.out.println("Contents Lost");
			}
	
			Toolkit.getDefaultToolkit().sync();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			this.running = false;
		}
	}
	
	private void gameRender(Graphics gScr) throws InterruptedException
	{
		if(!level_msg_displayed)
		{
			gScr.setColor(Color.white);
			gScr.fillRect(0, 0, this.pWidth, this.pHeight);
			
			gScr.setColor(Color.blue);
			gScr.setFont(this.font);
			
			gScr.drawString("Average FPS/UPS: " + this.df.format(this.averageFPS) + ", " + this.df.format(this.averageUPS), 20, 25);
			
			gScr.drawString("Time Spent: " + this.timeSpentInGame + " secs", 10, this.pHeight - 15);
			gScr.drawString("Boxes used: " + this.boxesUsed, 260, this.pHeight - 15);
			
			gScr.setColor(Color.black);
			
			this.fred.draw(gScr);
			this.obs.draw(gScr);
			
        	        drawButtons(gScr);
			
        	        if (this.gameOver)
				gameOverMessage(gScr);
		}
		else if(ENEMY_KILL==LEVEL_LENGTH)
		{
                    String msg = "NOW THE LEVEL IS " + LEVEL_NOW;
   	            int x = (this.pWidth - this.metrics.stringWidth(msg)) / 2;
   	            int y = (this.pHeight - this.metrics.getHeight()) / 2;
   	            gScr.setColor(Color.red);
   	            gScr.setFont(this.font);
                    
   	            gScr.drawString(msg, x, y);
                    gScr.setColor(Color.green);
                    gScr.drawString("Press Enter to Continue . . .", x-10, y+40);
   	            level_msg_displayed=true;
   	            ENEMY_KILL=0;
   	        }
	}
	
	private void drawButtons(Graphics g)
	{
		g.setColor(Color.black);
		
		if (this.isOverPauseButton) 
		{
			g.setColor(Color.green);
		}
		
		g.drawOval(this.pauseArea.x, this.pauseArea.y, this.pauseArea.width, this.pauseArea.height);
		
		if (this.isPaused)
                {
			g.drawString("Paused", this.pauseArea.x, this.pauseArea.y + 10);
                }
		else 
		{
			g.drawString("Pause", this.pauseArea.x + 5, this.pauseArea.y + 10);
		}
		
		if (this.isOverPauseButton) 
		{
			g.setColor(Color.black);
		}
		
		if (this.isOverQuitButton) 
		{
			g.setColor(Color.green);
		}
		
		g.drawOval(this.quitArea.x, this.quitArea.y, this.quitArea.width, this.quitArea.height);
		g.drawString("Quit", this.quitArea.x + 15, this.quitArea.y + 10);
		
		if (this.isOverQuitButton)
			g.setColor(Color.black);
	}
		
	private void gameOverMessage(Graphics g)
	{
            try {
                this.score = (this.obs.ScoreFinal * 10);
                String msg = "Game Over. Your Score: " + this.score;
                int x = (this.pWidth - this.metrics.stringWidth(msg)) / 2;
                int y = (this.pHeight - this.metrics.getHeight()) / 2;
                g.setColor(Color.red);
                g.setFont(this.font);
                g.drawString(msg, x, y);
                if(stored_data==false)
                {
                    int scored=this.score;
                    hm.addScore(this.name,scored);
                    this.stored_data=true;
                }
                LEVEL_NOW=1;
                
            } catch (Exception ex) 
            {
                Logger.getLogger(BigHero6.class.getName()).log(Level.SEVERE, null, ex);
            }
	}
	
	private void storeStats()
	{
		this.frameCount += 1L;
		this.statsInterval += this.period;
		
		if (this.statsInterval >= MAX_STATS_INTERVAL) 
		{
			long timeNow = J3DTimer.getValue();
			this.timeSpentInGame = (int)((timeNow - this.gameStartTime) / 1000000000L);
	
			long realElapsedTime = timeNow - this.prevStatsTime;
			this.totalElapsedTime += realElapsedTime;
	
			double timingError = (realElapsedTime - this.statsInterval) / this.statsInterval * 100.0D;
	
			this.totalFramesSkipped += this.framesSkipped;
	
			double actualFPS = 0.0D;
			double actualUPS = 0.0D;
			if (this.totalElapsedTime > 0L) 
			{
				actualFPS = this.frameCount / this.totalElapsedTime * 1000000000.0D;
				actualUPS = (this.frameCount + this.totalFramesSkipped) / this.totalElapsedTime * 1000000000.0D;
			}
	
			this.fpsStore[((int)this.statsCount % NUM_FPS)] = actualFPS;
			this.upsStore[((int)this.statsCount % NUM_FPS)] = actualUPS;
			this.statsCount += 1L;
			
			double totalFPS = 0.0D;
			double totalUPS = 0.0D;
			for (int i = 0; i < NUM_FPS; i++) 
			{
				totalFPS += this.fpsStore[i];
				totalUPS += this.upsStore[i];
			}
	
			if (this.statsCount < NUM_FPS) 
			{
				this.averageFPS = (totalFPS / this.statsCount);
				this.averageUPS = (totalUPS / this.statsCount);
			}
			else 
			{
				this.averageFPS = (totalFPS / NUM_FPS);
				this.averageUPS = (totalUPS / NUM_FPS);
			}
	
			this.framesSkipped = 0L;
			this.prevStatsTime = timeNow;
			this.statsInterval = 0L;
		}
	}
	
	private void finishOff()
	{
		if (!this.finishedOff) 
		{
			this.finishedOff = true;
			printStats();
			restoreScreen();
                        
			//System.exit(0);
		}
	}
	
	private void printStats()
	{
		System.out.println("Frame Count/Loss: " + this.frameCount + " / " + this.totalFramesSkipped);
		System.out.println("Average FPS: " + this.df.format(this.averageFPS));
		System.out.println("Average UPS: " + this.df.format(this.averageUPS));
		System.out.println("Time Spent: " + this.timeSpentInGame + " secs");
		System.out.println("Boxes used: " + this.boxesUsed);
	}
	
	private void restoreScreen()
	{
		Window w = this.gd.getFullScreenWindow();
		if (w != null)
		w.dispose();
		this.gd.setFullScreenWindow(null);
	}
	
	private void setDisplayMode(int width, int height, int bitDepth)
	{
		if (!this.gd.isDisplayChangeSupported()) 
		{
			System.out.println("Display mode changing not supported");
			return;
		}
	
		if (!isDisplayModeAvailable(width, height, bitDepth)) 
		{
			System.out.println("Display mode (" + width + "," + height + "," + bitDepth + ") not available");
			return;
		}
	
		DisplayMode dm = new DisplayMode(width, height, bitDepth, 0);
		try
		{
			this.gd.setDisplayMode(dm);
			System.out.println("Display mode set to: (" + width + "," + height + "," + bitDepth + ")");
		}
		catch (IllegalArgumentException e)
		{
			System.out.println("Error setting Display mode (" + width + "," + height + "," + bitDepth + ")");
		}
		try
		{
			Thread.sleep(1000L);
		}
		catch (InterruptedException ex)
		{
		}
	}
	
	private boolean isDisplayModeAvailable(int width, int height, int bitDepth)
	{
		DisplayMode[] modes = this.gd.getDisplayModes();
		showModes(modes);
	
		for (int i = 0; i < modes.length; i++)
		if ((width == modes[i].getWidth()) && (height == modes[i].getHeight()) && (bitDepth == modes[i].getBitDepth()))
		{
			return true;
		}
		return false;
	}
	
	private void showModes(DisplayMode[] modes)
	{
		System.out.println("Modes");
		for (int i = 0; i < modes.length; i++) 
		{
			System.out.print("(" + modes[i].getWidth() + "," + modes[i].getHeight() + "," + modes[i].getBitDepth() + "," + modes[i].getRefreshRate() + ")  ");
	
			if ((i + 1) % 4 == 0)
				System.out.println();
		}
		System.out.println();
	}
	
	private void showCurrentMode()
	{
	DisplayMode dm = this.gd.getDisplayMode();
	System.out.println("Current Display Mode: (" + dm.getWidth() + "," + dm.getHeight() + "," + dm.getBitDepth() + "," + dm.getRefreshRate() + ")  ");
	}
}