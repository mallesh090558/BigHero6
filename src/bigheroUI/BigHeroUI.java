/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bigheroUI;

import bighero6.BigHero6;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class BigHeroUI extends JPanel
{
    private static final String RESOURCE_PATH = "space_2.gif";
    private static final String TITLE_PATH="title.gif";
    private static final String START_BUTTON="start_button.gif";
    private static final String HSCORE_BUTTON="medal.gif";
    private static final String EXIT_BUTTON="exit_image.gif";
    
    private GraphicsDevice gd ;
    private final Image image,title_image,start_button,score_button,exit_button;
    private static int DEFAULT_FPS = 15;
    HighscoreManager hm;
    public int width,height;
    
    private ArrayList<HighScore> ar;
    
    public BigHeroUI(Window window, Image image, Image titleimg) throws Exception
    {
        this.image=image;
        this.title_image=titleimg;
        window.addMouseListener(new MouseAdapter() 
        {
            public void mouseClicked(MouseEvent e) 
            {
                System.out.println("MOUSE CLICKED"+e.getButton()+":"+e.getPoint()+"::"+(width-100)+":"+height);
                if(e.getButton()==MouseEvent.BUTTON1)
                {
                    if((e.getPoint().x>(width-100))&& (e.getPoint().y<100))
                    {
                        System.out.println("REACCHED METHOS");
                        int fps = DEFAULT_FPS;
                        long period = 1000L / fps;
                        System.out.println("fps: " + fps + "; period: " + period + " ms");
                        
                        Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() 
                        {
                            String result = JOptionPane.showInputDialog("Input:", "ENTER YOUR NAME HERE");
                            if ((result != null) && (result.length() > 0)) 
                            {
                                new BigHero6(period * 1000000L,hm,result);  
                            }
                        }
                        });
                        t.start();
                    }
                    if((e.getPoint().x>(width-100))&& (e.getPoint().y>(height-100)))
                    {
                        String result="";
                        try 
                        {
                            result=hm.getHighscoreString();
                            JOptionPane.showMessageDialog(window, result);
                         } 
                         catch (Exception ex) 
                         {
                            Logger.getLogger(BigHeroUI.class.getName()).log(Level.SEVERE, null, ex);
                         }

                    }
                    if((e.getPoint().x<200)&& (e.getPoint().y>(height-100)))
                    {
                        
                        System.out.println("REACHED EXIT BUTTON CONDITION");
                        //restoreScreen();  -- TRY RESTORING IN OPTIMAL WAY
                        System.exit(0);
                    }
                }
            }
        });
        
        hm = new HighscoreManager();
        
        URL startimg = BigHeroUI.class.getResource(START_BUTTON);
        this.start_button = new ImageIcon(startimg).getImage();
        
        URL scoreimg = BigHeroUI.class.getResource(HSCORE_BUTTON);
        this.score_button = new ImageIcon(scoreimg).getImage();
        
        URL exitimg = BigHeroUI.class.getResource(EXIT_BUTTON);
        this.exit_button = new ImageIcon(exitimg).getImage();
        
    }
    
    protected void paintComponent(Graphics g) 
    {
        gd= GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        this.width = gd.getDisplayMode().getWidth();
        this.height = gd.getDisplayMode().getHeight();
        
        
        super.paintComponent(g);
        
        if (image != null) {
            g.drawImage(image, 0, 0, width, height, this);
            g.drawImage(title_image, 0, 0, this);
            g.drawImage(start_button, (width-100), 0, 100,100,this);
            g.drawImage(score_button, (width-100), height-100, 100, 100, this);
            g.drawImage(exit_button, 0, height-100, 200, 100, this);
        }
    }
    
    private static void createAndShowGui() throws Exception 
    {
        JFrame frame = new JFrame();
        //frame.setUndecorated(true);
        URL imgUrl = BigHeroUI.class.getResource(RESOURCE_PATH);
        Image image = new ImageIcon(imgUrl).getImage();
        
        URL ttlurl= BigHeroUI.class.getResource(TITLE_PATH);
        Image titleimg=new ImageIcon(ttlurl).getImage();
        
        BigHeroUI mainPanel = new BigHeroUI(frame, image, titleimg);

        frame.setUndecorated(true);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(mainPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
        
    }
    public static void main(String[] args) 
    {
        SwingUtilities.invokeLater(new Runnable() 
        {
            public void run() 
            {
                try 
                {
                    createAndShowGui();
                } catch (Exception ex) {
                    Logger.getLogger(BigHeroUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
    private void restoreScreen()
    {
        Window w = this.gd.getFullScreenWindow();
	if (w != null)
            w.dispose();
	this.gd.setFullScreenWindow(null);
    }
}
