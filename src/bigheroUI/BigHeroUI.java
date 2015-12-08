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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;

/**
 *
 * @author Madhuri
 */
public class BigHeroUI extends JPanel
{
    private static final String RESOURCE_PATH = "space_2.gif";
    private static final String TITLE_PATH="title.gif";
    private static final String START_BUTTON="start_button.gif";
    private static final String HSCORE_BUTTON="medal.gif";
    
    private final Window window;
    private final Image image,title_image,start_button,score_button;
    private static int DEFAULT_FPS = 15;
    private DBConnection conn;
    public int width,height;
    
    private ArrayList<HighScore> ar;
    
    public BigHeroUI(Window window, Image image, Image titleimg) throws SQLException 
    {
        this.window=window;
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
                                new BigHero6(period * 1000000L,conn,result);  
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
                            ar=conn.getHighScrores();
                            for(int i=0;i<ar.size();i++)
                            {
                                HighScore h=(HighScore)ar.get(i);
                                System.out.println(h.getName()+"-->"+h.getScore());
                                result+=h.getName()+"\t\t-->\t\t"+h.getScore()+"\n";
                             }
                            JOptionPane.showMessageDialog(window, result);
                         } 
                         catch (SQLException ex) 
                         {
                            Logger.getLogger(BigHeroUI.class.getName()).log(Level.SEVERE, null, ex);
                         }

                    }
                }
            }
        });
        
        conn=new DBConnection();
        
        URL startimg = BigHeroUI.class.getResource(START_BUTTON);
        this.start_button = new ImageIcon(startimg).getImage();
        
        URL scoreimg = BigHeroUI.class.getResource(HSCORE_BUTTON);
        this.score_button = new ImageIcon(scoreimg).getImage();
    }
    
    protected void paintComponent(Graphics g) 
    {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        this.width = gd.getDisplayMode().getWidth();
        this.height = gd.getDisplayMode().getHeight();
        
        
        super.paintComponent(g);
        
        if (image != null) {
            g.drawImage(image, 0, 0, width, height, this);
            g.drawImage(title_image, 0, 0, this);
            g.drawImage(start_button, (width-100), 0, 100,100,this);
            g.drawImage(score_button, (width-100), height-100, 100, 100, this);
        }
    }
    
    private static void createAndShowGui() throws SQLException 
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
                } catch (SQLException ex) {
                    Logger.getLogger(BigHeroUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
}
