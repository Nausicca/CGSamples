/*@author: Chandhya Thirugnanasambantham
 * The following program has two separate set of classes integrated together.
 * Upon load,the Mandelbrot set gets generated.Clicking on a random location captures the point clicked
 * and generates a corresponding Julia set.
 * Please Note:
 * The Julia set window however appears minimized behind the Mandelbrot set window and has to be chosen for the window to appear in the front.
 * It might due to memory issues in my local computer,but may appear better in a different machine. 
 * If the window appears blank for a bit,it might be because of the time taken to load the graphic.
 * Please do allow some time for the graphic to load.
 * */
import java.awt.*;
import java.awt.event.*;

public class mandeljulia extends Frame
{  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
public static void main(String[] args){new mandeljulia();}
mandeljulia()
   {  super("Click left mouse button to generate Julia set " +
            "corresponding to the point clicked!");
      addWindowListener(new WindowAdapter()
         {public void windowClosing(
                 WindowEvent e){System.exit(0);}});
      setSize (800, 600);
      add("Center", new CvMandelbrotZoom());
      setVisible(true);
   }
}
class JuliaSet extends Frame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	JuliaSet(){
		super("Julia Set");
		addWindowListener(new WindowAdapter()
        {public void windowClosing(
                WindowEvent e){
        	try {
        		
        		e.getWindow().dispose();
			} catch (Throwable e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        	}});
		setSize(800,600);
		setLocation(500,100);
	this.setResizable(false);
	
		
	}

	public void init(int px,int py){
		
		
		add("Center",new CvJuliaSet(px,py));
	
	mandeljulia.getWindows()[0].setFocusableWindowState(false);
		this.toFront();
		setVisible(true);
		
	}
}
class CvJuliaSet extends Canvas{

	/**
	 * 
	 * 
	 */
	
	int ptx,pty;
	CvJuliaSet(int ptx,int pty){
		 this.ptx=ptx;
		 this.pty=pty;
		 repaint();
	 }
	private static final long serialVersionUID = 1L;
	public void paint(Graphics g)
	{
		
	
		
		  double w = getSize().width;
		   double h = getSize().height;
		   final double minRe0 = -2.0, maxRe0 = 1.0,
	                minIm0 = -1.0, maxIm0 = 1.0;
	   double minRe = minRe0, maxRe = maxRe0,
	                  minIm = minIm0, maxIm = maxIm0, factor;
		   
		 //  double cRe = -0.76, cIm = 0.084;
		   factor = Math.max((maxRe - minRe)/w, (maxIm - minIm)/h);
		   
		   double cIm = minIm + pty * factor;
		   double cRe = minRe + ptx * factor;
		
		   for(int yPix=0; yPix<h; ++yPix)
		   {  for(int xPix=0; xPix<w; ++xPix)
		      {  double x = minRe + xPix * factor,
		                y = minIm + yPix * factor;
		         int nMax = 100, n;
		         for (n=0; n<nMax; ++n)
		         {  double x2=x*x,y2=y*y;
		            if (x2 + y2 > 4)
		               break;      // Outside
		            y = 2 * x * y + cIm;
		            x = x2 - y2 + cRe;
		         }
		         g.setColor(n == nMax ? Color.black             // Inside
		              : new Color(100 + 155*n/nMax,0,0));       // Outside
		         g.drawLine(xPix, yPix, xPix, yPix);
		       }
		    }
		   g.setColor(Color.black);
		   g.setFont(new Font("Arial", Font.BOLD,14));
	     	g.drawString("Complex value of point clicked is " +cRe+" +i"+cIm,45,500);
	     	mandeljulia.getWindows()[0].setFocusableWindowState(true);            
	}
	
	
}
class CvMandelbrotZoom extends Canvas
{  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
final double minRe0 = -2.0, maxRe0 = 1.0,
                minIm0 = -1.0, maxIm0 = 1.0;
   double minRe = minRe0, maxRe = maxRe0,
                  minIm = minIm0, maxIm = maxIm0, factor, r;
   int n, xs, ys, xe, ye, w, h;
   JuliaSet js = new JuliaSet();
   void drawWhiteRectangle(Graphics g)
   { g.drawRect(Math.min(xs, xe), Math.min(ys, ye), 
                                  Math.abs(xe - xs), Math.abs(ye - ys));
}

boolean isLeftMouseButton(MouseEvent e)
{  return (e.getModifiers() & InputEvent.BUTTON3_MASK) == 0;
}

CvMandelbrotZoom()
{  addMouseListener(new MouseAdapter()
  {  public void mousePressed(MouseEvent e)
      {  if (isLeftMouseButton(e))
         { xs = xe = e.getX(); // Left button
           ys = ye = e.getY();
           
           js.dispose();
           js= new JuliaSet();
        //  js.setX(xs);js.setY(ys);
          js.init(xs,ys);
        //  js.setVisible(true);
          //MandelbrotZoom.getFrames()[0].setAlwaysOnTop(false);
          //js.toFront();
          
         }
         else
         { minRe = minRe0;     // Right button
           maxRe = maxRe0;
           minIm = minIm0;
           maxIm = maxIm0;
          repaint();
         }
       }

       public void mouseReleased(MouseEvent e)
       {  if (isLeftMouseButton(e))
          {  xe = e.getX(); // Left mouse button released
             ye = e.getY(); // Test if points are really distinct:
           
             if (xe != xs && ye != ys)
             { int xS = Math.min(xs, xe), xE = Math.max(xs, xe),
                   yS = Math.min(ys, ye), yE = Math.max(ys, ye),
                   w1 = xE - xS, h1 = yE - yS, a = w1 * h1,
                   h2 = (int)Math.sqrt(a/r), w2 = (int)(r * h2),
                   dx = (w2 - w1)/2, dy = (h2 - h1)/2;
               xS -= dx; xE += dx;
               yS -= dy; yE += dy; // aspect ration corrected
               maxRe = minRe + factor * xE;
               maxIm = minIm + factor * yE;
               minRe += factor * xS;
               minIm += factor * yS;
               repaint();
           }
        }
    } 
        });

        addMouseMotionListener(new MouseMotionAdapter()
        {  public void mouseDragged(MouseEvent e)
           {  if (isLeftMouseButton(e))
              {  Graphics g = getGraphics();
                 g.setXORMode(Color.black);
                 g.setColor(Color.white);
                 if (xe != xs || ye != ys)
                    drawWhiteRectangle(g); // Remove old rectangle:
                 xe = e.getX();
                 ye = e.getY();
                 drawWhiteRectangle(g);    // Draw new rectangle:
              }
            }
        });
      }

      public void paint(Graphics g)
      {  w = getSize().width;
         h = getSize().height;
         r = w/h; // Aspect ratio, used in mouseReleased
         factor = Math.max((maxRe - minRe)/w, (maxIm - minIm)/h);
         for(int yPix=0; yPix<h; ++yPix)
         {  double cIm = minIm + yPix * factor;
         
            for(int xPix=0; xPix<w; ++xPix)
            {  double cRe = minRe + xPix * factor,x=cRe,y=cIm;
            
               int nMax = 100, n;
               for (n=0; n<nMax; ++n)
               {  double x2 = x * x, y2 = y * y;
                  if (x2 + y2 > 4)
                     break;     //  Outside
                  y = 2 * x * y + cIm;
                  x = x2 - y2 + cRe;
               }
               g.setColor(n == nMax ? Color.black            // Inside
                   : new Color(100 + 155 * n / nMax, 0, 0)); // Outside
               g.drawLine(xPix, yPix, xPix, yPix);
            }
        }
         g.setColor(Color.black);
         g.setFont(new Font("Arial", Font.BOLD,14));
     	g.drawString("Click left mouse button to generate Julia set " +
	            "corresponding to the point clicked!",45,500);
     	g.drawString("Julia set will appear in a new window which might be minimized!",45,520);
    }
}

