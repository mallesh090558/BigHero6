package bighero6;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Obstacles
{
  ArrayList boxesEnxt;
  ArrayList EDirect;
  ArrayList<Integer> EnemyManage;
  
  private BigHero6 wormChase;
  private Image enemy,explosion,gen_explosion;
  private int[] probsForOffset;
  private Point2D.Double[] incrs;
  private int pWidth;
  private int pHeight;
  public int ScoreFinal;
  
  public static int GENLENGTH=20;
  
  public Obstacles(BigHero6 wc, int pw, int ph)
  {
    this.pWidth = pw;
    this.pHeight = ph;
    this.ScoreFinal += 1;
    this.wormChase = wc;
    this.enemy = Toolkit.getDefaultToolkit().getImage(getClass().getResource("fireball.gif"));
    this.explosion = Toolkit.getDefaultToolkit().getImage(getClass().getResource("explosion.gif"));
    this.gen_explosion = Toolkit.getDefaultToolkit().getImage(getClass().getResource("gen_explode.gif"));

    this.probsForOffset = new int[9];
    this.probsForOffset[0] = 0;
    this.probsForOffset[1] = 1;
    this.probsForOffset[2] = -1;
    this.probsForOffset[3] = 1;
    this.probsForOffset[4] = 1;
    this.probsForOffset[5] = 2;
    this.probsForOffset[6] = -1;
    this.probsForOffset[7] = -1;
    this.probsForOffset[8] = -2;

    this.incrs = new Point2D.Double[11];
    this.incrs[0] = new Point2D.Double(0.0D, -1.0D);
    this.incrs[1] = new Point2D.Double(0.7D, -0.7D);
    this.incrs[2] = new Point2D.Double(1.0D, 0.0D);
    this.incrs[3] = new Point2D.Double(0.7D, 0.7D);
    this.incrs[4] = new Point2D.Double(0.0D, 1.0D);
    this.incrs[5] = new Point2D.Double(-0.7D, 0.7D);
    this.incrs[6] = new Point2D.Double(-1.0D, 0.0D);
    this.incrs[7] = new Point2D.Double(-0.7D, -0.7D);
    this.incrs[8] = new Point2D.Double(0.0D, 0.0D);
    this.incrs[9] = new Point2D.Double(0.0D, 0.0D);
    this.incrs[10] = new Point2D.Double(0.0D, 0.0D);

    this.boxesEnxt = new ArrayList(10);
    this.EDirect = new ArrayList(10);
    this.EnemyManage = new ArrayList(10);
  }

  public synchronized void addEnemy(boolean addE)
  {
    if ((this.boxesEnxt.isEmpty()) || (addE))
    {
      System.out.println("BOX:" + this.boxesEnxt.isEmpty() + "\t" + "REGEN :" + addE);
      int newBearing = (int)(Math.random() * 8.0D);
      Point ranPt = RandomPoint();
      if (this.boxesEnxt.isEmpty())
      {
        this.boxesEnxt.add(new Rectangle(ranPt.x, ranPt.y, 60, 60));
        this.EnemyManage.add(0, 0);
        this.EDirect.add(0, Integer.valueOf(newBearing));
        this.ScoreFinal += 1;
      }
      else
      {
        System.out.println("LENGHT:" + this.EDirect.size());
        this.boxesEnxt.add(new Rectangle(ranPt.x, ranPt.y, 60, 60));

        this.EnemyManage.add(this.EnemyManage.size(), 0);
        this.EDirect.add(this.EDirect.size(), Integer.valueOf(newBearing));
        this.ScoreFinal += 1;
      }
      this.wormChase.setBoxNumber(this.boxesEnxt.size());
    }
    else
    {
      for (int i = 0; i < this.boxesEnxt.size(); i++)
      {
        Point newPt = nextPoint(i, ((Integer)this.EDirect.get(i)).intValue());
        boolean collided = enemyCollision(newPt, i);
        if (!collided)
        {
          this.boxesEnxt.set(i, new Rectangle(newPt.x, newPt.y, 60, 60));
        }
        else 
        {
           if(EnemyManage.get(i)>GENLENGTH)
           {
                int newBearing = (int)(Math.random() * 8.0D);
                this.EDirect.set(i, Integer.valueOf(newBearing));
                i--;
           }
        }
      }
    }
  }

  private Point nextPoint(int prevPosn, int bearing)
  {
    Point2D.Double incr = this.incrs[bearing];
    System.out.println(" IN NEXT POINT :" + prevPosn);
    Rectangle r = (Rectangle)this.boxesEnxt.get(prevPosn);
    
    int newX = r.x;
    int newY = r.y;
    
    if(this.EnemyManage.get(prevPosn)>=GENLENGTH)
    {
        newX = r.x + (int)(10.0D * incr.x);
        newY = r.y + (int)(10.0D * incr.y);
    }
    
    if (newX + 60 < 0)
      newX += this.pWidth;
    else if (newX > this.pWidth) {
      newX -= this.pWidth;
    }
    if (newY + 60 < 0)
      newY += this.pHeight;
    else if (newY > this.pHeight) {
      newY -= this.pHeight;
    }
    return new Point(newX, newY);
  }

  public synchronized void draw(Graphics g)
  {
    g.setColor(Color.blue);
    System.out.println("INDEX:"+EnemyManage.size()+" & SIZE:" + this.boxesEnxt.size());
    for (int i = 0; i < this.boxesEnxt.size(); i++)
    {
        System.out.println("MANAGE :"+EnemyManage.get(i)+"\t"+"DIRECT :"+EDirect.get(i));
        if((Integer)this.EDirect.get(i)<8 && this.EnemyManage.get(i)>GENLENGTH)
        {
            Rectangle box = (Rectangle)this.boxesEnxt.get(i);
            boolean res = g.drawImage(this.enemy, box.x, box.y, box.width, box.height, null);
            if (res == true)
                System.out.println("Image loaded success");
        }
        else
        {
            Rectangle box = (Rectangle)this.boxesEnxt.get(i);
            if(this.EnemyManage.get(i)<=GENLENGTH)
            {
              g.drawImage(this.gen_explosion, box.x, box.y, box.width, box.height, null);
              this.EnemyManage.set(i,(this.EnemyManage.get(i))+1);
            }
            else if((Integer)this.EDirect.get(i)>7)
            {
              g.drawImage(this.explosion, box.x, box.y, box.width, box.height, null);
              if((Integer)this.EDirect.get(i)<10)
              this.EDirect.set(i,((Integer)this.EDirect.get(i))+1);
            }
        }
    }
  }

  public Point RandomPoint()
  {
    Point RanNewPoint = new Point();
    RanNewPoint.x = (int)(Math.random() * this.pWidth);
    RanNewPoint.y = (int)(Math.random() * this.pHeight);
    return RanNewPoint;
  }
  public synchronized int getNumObstacles() {
    return this.boxesEnxt.size();
  }

  private boolean enemyCollision(Point newPt, int i) {
    boolean collided = false;
    for (int k = 0; k < this.boxesEnxt.size(); k++)
    {
      if (k == i)
        continue;
      Rectangle objR = (Rectangle)this.boxesEnxt.get(k);
      Rectangle objE = new Rectangle(newPt.x, newPt.y, 60, 60);
      if (objR.intersects(objE)) {
        collided = true;
      }
    }
    System.out.println("COLLISION STATUS:"+collided);
    return collided;
  }
}