package bighero6;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.geom.Point2D;

public class BayMax
{
  private boolean colllision = false;
  Point2D.Double[] incrs;
  private Point cellHeroCur;
  private Point cellHeroNxt;
  private Point fireHeroCur = null; private Point fireHeroNxt = null;
  private int pWidth;
  private int pHeight;
  private int H_DIRECT;
  private int F_DIRECT;
  private Obstacles obs;
  private Image hero;
  private Image back;
  private Image fire;

  public BayMax(int pW, int pH, Obstacles os)
  {
    this.pWidth = pW; this.pHeight = pH;
    this.obs = os;
    this.cellHeroCur = new Point(pW / 2, pH);
    this.cellHeroNxt = new Point(pW / 2, pH);

    this.incrs = new Point2D.Double[8];
    this.incrs[0] = new Point2D.Double(0.0D, -1.0D);
    this.incrs[1] = new Point2D.Double(0.7D, -0.7D);
    this.incrs[2] = new Point2D.Double(1.0D, 0.0D);
    this.incrs[3] = new Point2D.Double(0.7D, 0.7D);
    this.incrs[4] = new Point2D.Double(0.0D, 1.0D);
    this.incrs[5] = new Point2D.Double(-0.7D, 0.7D);
    this.incrs[6] = new Point2D.Double(-1.0D, 0.0D);
    this.incrs[7] = new Point2D.Double(-0.7D, -0.7D);

    this.hero = Toolkit.getDefaultToolkit().getImage(getClass().getResource("newhero.gif"));
    this.back = Toolkit.getDefaultToolkit().getImage(getClass().getResource("bg.jpg"));
    this.fire = Toolkit.getDefaultToolkit().getImage(getClass().getResource("star1.png"));
  }

  public boolean NxtMove(int dir)
  {
    this.H_DIRECT = dir;
    this.cellHeroNxt = nextPoint(this.cellHeroCur, dir);
    if (this.fireHeroCur != null)
    {
      this.fireHeroNxt = nextPoint(this.fireHeroCur, this.F_DIRECT);
      fireBlast();
    }
    collided();

    return this.colllision;
  }

  private Point nextPoint(Point prevPosn, int bearing)
  {
    Point2D.Double incr = this.incrs[bearing];

    int newX = prevPosn.x + (int)(20.0D * incr.x);
    int newY = prevPosn.y + (int)(20.0D * incr.y);

    if (newX + 100 < 0)
      newX += this.pWidth;
    else if (newX > this.pWidth) {
      newX -= this.pWidth;
    }
    if (newY + 100 < 0)
      newY += this.pHeight;
    else if (newY > this.pHeight) {
      newY -= this.pHeight;
    }
    
    return new Point(newX, newY);
  }

  public void draw(Graphics g)
  {
    this.cellHeroCur = this.cellHeroNxt;
    g.drawImage(this.back, 0, 0, this.pWidth, this.pHeight, null);
    Rectangle box = new Rectangle(this.cellHeroCur.x, this.cellHeroCur.y, 100, 100);
    g.drawImage(this.hero, box.x, box.y, box.width, box.height, null);

    
    if (this.fireHeroNxt != null)
    {
      this.fireHeroCur = this.fireHeroNxt;
      Rectangle firebomb = new Rectangle(this.fireHeroNxt.x, this.fireHeroNxt.y, 100, 100);
      g.drawImage(this.fire, firebomb.x, firebomb.y, 100, 100, null);
    }
  }

  public void collided()
  {
    Rectangle hero = new Rectangle(this.cellHeroCur.x, this.cellHeroCur.y, 100, 100);
    for (int i = 0; i < this.obs.boxesEnxt.size(); i++)
    {
        if((int)this.obs.EDirect.get(i)<8 && this.obs.EnemyManage.get(i)>obs.GENLENGTH)
        {
            Rectangle enemy = (Rectangle)(Rectangle)this.obs.boxesEnxt.get(i);
            if (hero.intersects(enemy))
                this.colllision = true;
        }
    }
  }

  void fireBullet()
  {
    if (this.fireHeroCur == null)
    {
      this.fireHeroCur = new Point(this.cellHeroCur);
      this.F_DIRECT = this.H_DIRECT;
    }
  }

  private void fireBlast()
  {
    boolean fired = false;
    int deadenemy = 0;
    int i;
    Rectangle fire = new Rectangle(this.fireHeroCur.x, this.fireHeroCur.y, 100, 100);
    for (i = 0; i < this.obs.boxesEnxt.size(); i++)
    {
      Rectangle enemy = (Rectangle)(Rectangle)this.obs.boxesEnxt.get(i);
      if (fire.intersects(enemy) && (Integer)obs.EDirect.get(i)<8 && obs.EnemyManage.get(i)>obs.GENLENGTH)
      {
        fired = true;
        deadenemy = i;
      }
      if((Integer)obs.EDirect.get(i)>=10)
      {
        this.obs.boxesEnxt.remove(deadenemy);
        this.obs.EDirect.remove(deadenemy);
      }
    }
    if (fired == true)
    {
      this.fireHeroCur = null;
      this.fireHeroNxt = null;
      
      obs.EDirect.set(deadenemy, 8);
    }
  }
}