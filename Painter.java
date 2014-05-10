/*@author:Chandhya Thirugnanasambantham
 *This file has the code changes required for the bonus questions to work fine.
 *Procedure adopted is to create seperate Thread that runs updating the value of suntheta-the angle of rotation by 0.02 
 * The values of sunX and sunY are updated accordingly.
 * Sunphi remains a constant.
 * Rho value is set to 1.
 * Thread has a sleep time of 20 ms
 * gimage and image were used to paint method to obtain desired rotation.
 * This file can take in input dat files and has been tested with 3D figures and the colour varies depending on the degree of rotation.
 * Please Note:
 * ---------------
 * All the required files have been places either in Painter.java and the class Point3D has been placed in Stairs.java.
 * So Stairs.java,painter.java and ZBuffer.java need to be placed in the same path for the program to work
 * 
 * */
// Painter.java: Perspective drawing using an input file that lists
//    vertices and faces. Based on the Painter's algorithm.
// Uses: Fr3D (Section 5.5) and CvPainter (Section 7.3),
//       Point2D (Section 1.5), Point3D (Section 3.9),
//       Obj3D, Polygon3D, Tria, Fr3D, Canvas3D (Section 5.5).

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
public class Painter extends Frame
{ 
	private static final long serialVersionUID = 1L;

public static void main(String[] args)
  {  new Fr3D(args.length>0? args[0] : null, new CvPainter(),
        "Painter");
  }
}



//CvPainter.java: Used in the file Painter.java.
class CvPainter extends Canvas3D implements Runnable
{  
	private static final long serialVersionUID = 1L;
	Image image;
	Graphics gImage;
	double sunTheta = 0;
	double sunphi = 1.3;
	int rho=1;
	float xC,yC;
	Thread thr = new Thread(this);	
	private int maxX, maxY, centerX, centerY;
	float rWidth = 10.0F, rHeight = 10.0F;
private Obj3D obj;
private Point2D imgCenter;
int w, h;

public void update(Graphics g){paint(g);}

Obj3D getObj(){return obj;}
void setObj(Obj3D obj){this.obj = obj;}
int iX(float x){return Math.round(centerX + x - imgCenter.x);}
int iY(float y){return Math.round(centerY - y + imgCenter.y);}

void sort(Tria[] tr, int[] colorCode, float[] zTr, int l, int r)
{  int i = l, j = r,  wInt;
   float x = zTr[(i + j)/2], w;
   Tria wTria;
   do
   {  while (zTr[i] < x) i++;
      while (zTr[j] > x) j--;
      if (i < j)
      {  w = zTr[i]; zTr[i] = zTr[j]; zTr[j] = w;
         wTria = tr[i]; tr[i] = tr[j]; tr[j] = wTria;
         wInt = colorCode[i]; colorCode[i] = colorCode[j];
         colorCode[j] = wInt;
         i++; j--;
      } else
   if (i == j) {i++; j--;}
} while (i <= j);
if (l < j) sort(tr, colorCode, zTr, l, j);
if (i < r) sort(tr, colorCode, zTr, i, r);
}

public void paint(Graphics g)
{  if (obj == null) return;
Vector<Polygon3D> polyList = obj.getPolyList();
if (polyList == null) return;
int nFaces = polyList.size();
if (nFaces == 0) return;

Dimension dim = getSize();
maxX = dim.width - 1; maxY = dim.height - 1;
centerX = maxX/2; centerY = maxY/2;
xC = rWidth/2; yC = rHeight/2;


sunphi = obj.phi;
obj.sunX = obj.rho *Math.sin(sunphi)*Math.cos(sunTheta);
obj.sunY = obj.rho *Math.sin(sunphi)*Math.sin(sunTheta);
obj.sunZ = obj.rho *Math.cos(sunphi);

// ze-axis towards eye, so ze-coordinates of
// object points are all negative.
// obj is a java object that contains all data:
// - Vector w       (world coordinates)
// - Array e        (eye coordinates)
// - Array vScr     (screen coordinates)
// - Vector polyList (Polygon3D objects)

// Every Polygon3D value contains:
// - Array 'nrs' for vertex numbers
// - Values a, b, c, h for the plane ax+by+cz=h.
// - Array t (with nrs.length-2 elements of type Tria)

// Every Tria value consists of the three vertex
// numbers iA, iB and iC.
obj.eyeAndScreen(dim);
      // Computation of eye and screen coordinates.

imgCenter = obj.getImgCenter();
obj.planeCoeff();    // Compute a, b, c and h.

// Construct an array of triangles in
// each polygon and count the total number
// of triangles:
int nTria = 0;
for (int j=0; j<nFaces; j++)
{   Polygon3D pol = (Polygon3D)(polyList.elementAt(j));
      if (pol.getNrs().length < 3 || pol.getH()>=0)
         continue;
      pol.triangulate(obj);
      nTria += pol.getT().length;
   }
   Tria[] tr = new Tria[nTria];
   int[] colorCode = new int[nTria];
   float[] zTr = new float[nTria];
   int iTria = 0;
   Point3D[] e = obj.getE();
   Point2D[] vScr = obj.getVScr();

   for (int j=0; j<nFaces; j++)
   {  Polygon3D pol = (Polygon3D)(polyList.elementAt(j));
      if (pol.getNrs().length < 3 || pol.getH() >= 0) continue;
      int cCode =
         obj.colorCode(pol.getA(), pol.getB(), pol.getC());
      Tria[] t = pol.getT();
      for (int i=0; i< t.length; i++)
      {  Tria tri = t[i];
         tr[iTria] = tri;
         colorCode[iTria] = cCode;
         float zA = e[tri.iA].z, zB = e[tri.iB].z,
            zC = e[tri.iC].z;
         zTr[iTria++] = zA + zB + zC;
      }
   }

   sort(tr, colorCode, zTr, 0, nTria - 1);
   
   if (w != dim.width || h != dim.height)
   {   w = dim.width; h = dim.height;
       image = createImage(w, h);
       gImage = image.getGraphics();      
   }
  
   gImage.clearRect (0, 0, w, h);
   for (iTria=0; iTria<nTria; iTria++)
   {  Tria tri = tr[iTria];
      Point2D a = vScr[tri.iA],
              b = vScr[tri.iB],
              c = vScr[tri.iC];
      int cCode = colorCode[iTria];
      gImage.setColor(new Color(cCode, cCode, 0));
      int[] x = {iX(a.x), iX(b.x), iX(c.x)};
      int[] y = {iY(a.y), iY(b.y), iY(c.y)};
      gImage.fillPolygon(x, y, 3);
   }
  
   g.drawImage(image, 0, 0, null);

      
}
@Override
public void run()
	   {  try
	      { for (;;)
	        {  sunTheta += 0.02;
	      // System.out.println("thread works ok!");
	      // obj =this.getObj();
	       //obj.rho =1;
	           repaint();
	           Thread.sleep (20);
	        }
	      }
	      catch (InterruptedException e){}
	   }
CvPainter(){thr.start();}


}


//Tria.java: Triangle represented by its vertex numbers.
class Tria
{  int iA, iB, iC;
Tria(int i, int j, int k){iA = i; iB = j; iC = k;}
}
//Polygon3D.java: Polygon in 3D, represented by vertex numbers
//referring to coordinates stored in an Obj3D object.

class Polygon3D
{  private int[] nrs;
private double a, b, c, h;
private Tria[] t;
Polygon3D(Vector<Integer> vnrs)
{  int n = vnrs.size();
nrs = new int[n];
for (int i=0; i<n; i++)
nrs[i] = ((Integer)vnrs.elementAt(i)).intValue();
}

int[] getNrs(){return nrs;}
double getA(){return a;}
double getB(){return b;}
double getC(){return c;}
double getH(){return h;}
void setAbch(double a, double b, double c, double h)
{  this.a = a; this.b = b; this.c = c; this.h = h;
}
Tria[] getT(){return t;}

void triangulate(Obj3D obj)
// Successive vertex numbers (CCW) in vector nrs.
// Resulting triangles will be put in array t.
{ int n = nrs.length;          // n > 2 is required
int[] next = new int[n];
t = new Tria[n - 2];
Point2D[] vScr = obj.getVScr();
int iA=0, iB, iC; int j=n-1;
for (int i=0; i<n; i++){next[j] = i; j = i;}
for (int k=0; k<n-2; k++)
{   // Find a suitable triangle, consisting of two edges
// and an internal diagonal:
Point2D a, b, c;
boolean found = false;
int count = 0, nA = -1, nB = 0, nC = 0, nj;
while (!found && ++count < n)
{  iB = next[iA]; iC = next[iB];
nA = Math.abs(nrs[iA]); a = vScr[nA];
nB = Math.abs(nrs[iB]); b = vScr[nB];
nC = Math.abs(nrs[iC]); c = vScr[nC];
if (Tools2D.area2(a, b, c) >= 0)
{  // Edges AB and BC; diagonal AC.
// Test to see if no vertex (other than A,
// B, C) lies within triangle ABC:
j = next[iC]; nj = Math.abs(nrs[j]);
while (j != iA &&
 (nj == nA  ||   nj == nB  ||   nj == nC  ||
 !Tools2D.insideTriangle(a, b, c, vScr[nj])))
{  j = next[j]; nj = Math.abs(nrs[j]);
}
if (j == iA)
{   // Triangle found:
t[k] = new Tria(nA, nB, nC);
next[iA] = iC;
found = true;
}
}
iA = next[iA];
}
if (count == n)
{   // Degenerated polygon, possibly with all
// vertices on one line.
if (nA >= 0) t[k] = new Tria(nA, nB, nC);
else
{ System.out.println("Nonsimple polygon");
System.exit(1);
}
}
}
}
}


//Canvas3D.java: Abstract class.


abstract class Canvas3D extends Canvas
{  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
abstract Obj3D getObj();
abstract void setObj(Obj3D obj);
}


//Point2D.java: Class for points in logical coordinates.
class Point2D
{ float x, y;
Point2D(float x, float y){this.x = x; this.y = y;}
}



//Fr3D.java: Frame class to deal with menu commands and other
//user actions.


class Fr3D extends Frame implements ActionListener
{  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
protected MenuItem open, exit, eyeUp, eyeDown, eyeLeft, eyeRight,
incrDist, decrDist;
protected String sDir;
protected Canvas3D cv;
protected Menu mF, mV;

Fr3D(String argFileName, Canvas3D cv, String textTitle)
{ super(textTitle);
addWindowListener(new WindowAdapter()
{public void windowClosing(WindowEvent e){System.exit(0);}});
this.cv = cv;
MenuBar mBar = new MenuBar();
setMenuBar(mBar);
mF = new Menu("File");
mV = new Menu("View");
mBar.add(mF); mBar.add(mV);

open = new MenuItem("Open",
new MenuShortcut(KeyEvent.VK_O));
eyeDown = new MenuItem("Viewpoint Down",
new MenuShortcut(KeyEvent.VK_DOWN));
eyeUp = new MenuItem("Viewpoint Up",
new MenuShortcut(KeyEvent.VK_UP));
eyeLeft = new MenuItem("Viewpoint to Left",
new MenuShortcut(KeyEvent.VK_LEFT));
eyeRight = new MenuItem("Viewpoint to Right",
new MenuShortcut(KeyEvent.VK_RIGHT));

incrDist = new MenuItem("Increase viewing distance",
new MenuShortcut(KeyEvent.VK_INSERT));
decrDist = new MenuItem("Decrease viewing distance",
new MenuShortcut(KeyEvent.VK_DELETE));
exit = new MenuItem("Exit",
new MenuShortcut(KeyEvent.VK_Q));
mF.add(open); mF.add(exit);
mV.add(eyeDown); mV.add(eyeUp);
mV.add(eyeLeft); mV.add(eyeRight);
mV.add(incrDist); mV.add(decrDist);
open.addActionListener(this);
exit.addActionListener(this);
eyeDown.addActionListener(this);
eyeUp.addActionListener(this);
eyeLeft.addActionListener(this);
eyeRight.addActionListener(this);
incrDist.addActionListener(this);
decrDist.addActionListener(this);
add("Center", cv);
Dimension dim = getToolkit().getScreenSize();
setSize(dim.width/2, dim.height/2);
setLocation(dim.width/4, dim.height/4);
if (argFileName != null)
{  Obj3D obj = new Obj3D();
if (obj.read(argFileName)){cv.setObj(obj); cv.repaint();}
}
cv.setBackground(new Color (180, 180, 255) );
setVisible(true);
}

void vp(float dTheta, float dPhi, float fRho) // Viewpoint
{  Obj3D obj = cv.getObj();
if (obj == null  ||   !obj.vp(cv, dTheta, dPhi, fRho))
Toolkit.getDefaultToolkit().beep();
}

public void actionPerformed(ActionEvent ae)
{  if (ae.getSource() instanceof MenuItem)
{  MenuItem mi = (MenuItem)ae.getSource();
if (mi == open)
{  FileDialog fDia = new FileDialog(Fr3D.this,
   "Open", FileDialog.LOAD);
   fDia.setDirectory(sDir);
   fDia.setFile("*.dat");
   fDia.setVisible(true);
   String sDir1 = fDia.getDirectory();
   String sFile = fDia.getFile();
   String fName = sDir1 + sFile;
   Obj3D obj = new Obj3D();
   if (obj.read(fName))
   {  sDir = sDir1;
      cv.setObj(obj);
      cv.repaint();
   }
}
else
if (mi == exit) System.exit(0); else
if (mi == eyeDown) vp(0, .1F, 1); else
if (mi == eyeUp) vp(0, -.1F, 1); else
if (mi == eyeLeft) vp(-.1F, 0, 1); else
if (mi == eyeRight) vp(.1F, 0, 1); else
if (mi == incrDist) vp (0, 0, 2); else
if (mi == decrDist) vp(0, 0, .5F);
}
}
}
//Obj3D.java: A 3D object and its 2D representation.
//Uses: Point2D (Section 1.5), Point3D (Section 3.9),
//    Polygon3D, Input (Section 5.5).

class Obj3D
{  float rho;
private float d;
private float theta=0.30F;
float phi=1.3F;
private float rhoMin;
private float rhoMax;
private float xMin;
private float xMax;
private float yMin;
private float yMax;
private float zMin;
private float zMax;
private float v11;
private float v12;
private float v13;
private float v21;
private float v22;
private float v23;
private float v32;
private float v33;
private float v43;

private Point2D imgCenter;
double sunZ = 1/Math.sqrt (3);
double sunY = sunZ;
double sunX = -sunZ;
private double inprodMin = 1e30;
private double inprodMax = -1e30;
private double inprodRange;
private Vector<Point3D> w = new Vector<Point3D>();         // World coordinates
private Point3D[] e;                     // Eye coordinates
private Point2D[] vScr;                  // Screen coordinates
private Vector<Polygon3D> polyList = new Vector<Polygon3D>();  // Polygon3D objects 
private String fName = " ";              // File name

boolean read(String fName)
{  Input inp = new Input(fName);
   if (inp.fails())return failing();
   this.fName = fName;
   xMin = yMin = zMin = +1e30F;
   xMax = yMax = zMax = -1e30F;
   return readObject(inp); // Read from inp into obj
}

Vector<Polygon3D> getPolyList(){return polyList;}
String getFName(){return fName;}
Point3D[] getE(){return e;}
Point2D[] getVScr(){return vScr;}
Point2D getImgCenter(){return imgCenter;}
float getRho(){return rho;}
float getD(){return d;}

private boolean failing()
{  Toolkit.getDefaultToolkit().beep();
   return false;
}

private boolean readObject(Input inp)
{  for (;;)
   {  int i = inp.readInt();
      if (inp.fails()){inp.clear(); break;}
      if (i < 0)
      {  System.out.println(
            "Negative vertex number in first part of input file");
         return failing();
      }
      w.ensureCapacity(i + 1);
      float x = inp.readFloat(), y = inp.readFloat(),
            z = inp.readFloat();
      addVertex(i, x, y, z);
   }
   shiftToOrigin(); // Origin in center of object.
   char ch;
   int count = 0;
   do   // Skip the line "Faces:"
   {  ch = inp.readChar(); count++; 
   }  while (!inp.eof() && ch != '\n');
   if (count < 6 || count > 8)
   {  System.out.println("Invalid input file"); return failing();
   }
  //  Build polygon list:
  for (;;)
  {   Vector<Integer> vnrs = new Vector<Integer>();
      for (;;)
      {  int i = inp.readInt();
         if (inp.fails()){inp.clear(); break;}
         int absi = Math.abs(i);
         if (i == 0 || absi >= w.size() ||
            w.elementAt(absi) == null)
         {  System.out.println("Invalid vertex number: " + absi +
            " must be defined, nonzero and less than " + w.size());
            return failing();
         }
         vnrs.addElement(new Integer(i));
      }
      ch = inp.readChar();
      if (ch != '.' && ch != '#') break;
      // Ignore input lines with only one vertex number:
      if (vnrs.size() >= 2)
         polyList.addElement(new Polygon3D(vnrs));
   }
   inp.close();
   return true;
}

private void addVertex(int i, float x, float y, float z)
{  if (x < xMin) xMin = x; if (x > xMax) xMax = x;
   if (y < yMin) yMin = y; if (y > yMax) yMax = y;
   if (z < zMin) zMin = z; if (z > zMax) zMax = z;
   if (i >= w.size()) w.setSize(i + 1);
   w.setElementAt(new Point3D(x, y, z), i);
}

private void shiftToOrigin()
{  float xwC = 0.5F * (xMin + xMax),
         ywC = 0.5F * (yMin + yMax),
         zwC = 0.5F * (zMin + zMax);
   int n = w.size();
   for (int i=1; i<n; i++) 
         if (w.elementAt(i) != null)
         {  ((Point3D)w.elementAt(i)).x -= xwC;
            ((Point3D)w.elementAt(i)).y -= ywC;
            ((Point3D)w.elementAt(i)).z -= zwC;
         }
      float dx = xMax - xMin, dy = yMax - yMin, dz = zMax - zMin;
      rhoMin = 0.6F * (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
      rhoMax = 1000 * rhoMin;
      rho = 3 * rhoMin;
   }

   private void initPersp()
   {  float costh = (float)Math.cos(theta),
            sinth = (float)Math.sin(theta),
            cosph = (float)Math.cos(phi),
            sinph = (float)Math.sin(phi);
      v11 = -sinth; v12 = -cosph * costh; v13 = sinph * costh;
      v21 = costh;  v22 = -cosph * sinth; v23 = sinph * sinth;
                    v32 = sinph;          v33 = cosph;
                                          v43 = -rho;
   }

   float eyeAndScreen(Dimension dim)
      // Called in paint method of Canvas class
   {  initPersp();
      int n = w.size();
      e = new Point3D[n];
      vScr = new Point2D[n];
      float xScrMin=1e30F, xScrMax=-1e30F,
            yScrMin=1e30F, yScrMax=-1e30F;
      for (int i=1; i<n; i++)
      {  Point3D P = (Point3D)(w.elementAt(i));
         if (P == null)
      {  e[i] = null; vScr[i] = null;
      }
      else
      {  float x = v11 * P.x + v21 * P.y;
         float y = v12 * P.x + v22 * P.y + v32 * P.z;
         float z = v13 * P.x + v23 * P.y + v33 * P.z + v43;
         Point3D Pe = e[i] = new Point3D(x, y, z);
         float xScr = -Pe.x/Pe.z, yScr = -Pe.y/Pe.z;
         vScr[i] = new Point2D(xScr, yScr);
         if (xScr < xScrMin) xScrMin = xScr; 
         if (xScr > xScrMax) xScrMax = xScr;
         if (yScr < yScrMin) yScrMin = yScr;
         if (yScr > yScrMax) yScrMax = yScr;
      }
   }
   float rangeX = xScrMax - xScrMin, rangeY = yScrMax - yScrMin;
   d = 0.95F * Math.min(dim.width/rangeX, dim.height/rangeY);
   imgCenter = new Point2D(d * (xScrMin + xScrMax)/2,
                           d * (yScrMin + yScrMax)/2);
   for (int i=1; i<n; i++)
   {  if (vScr[i] != null){vScr[i].x *= d; vScr[i].y *= d;}
   }
   return d * Math.max(rangeX, rangeY);
   // Maximum screen-coordinate range used in CvHLines for HP-GL
}

void planeCoeff()
{  int nFaces = polyList.size();

   for (int j=0; j<nFaces; j++)
   {  Polygon3D pol = (Polygon3D)(polyList.elementAt(j));
      int[] nrs = pol.getNrs();
      if (nrs.length < 3) continue;
      int iA = Math.abs(nrs[0]), // Possibly negative
          iB = Math.abs(nrs[1]), // for HLines.
          iC = Math.abs(nrs[2]);
      Point3D A = e[iA], B = e[iB], C = e[iC];
      double
         u1 = B.x - A.x, u2 = B.y - A.y, u3 = B.z - A.z,
         v1 = C.x - A.x, v2 = C.y - A.y, v3 = C.z - A.z,
         a = u2 * v3 - u3 * v2,
         b = u3 * v1 - u1 * v3,
         c = u1 * v2 - u2 * v1,
         len = Math.sqrt(a * a + b * b + c * c), h;
         a /= len; b /= len; c /= len;
         h = a * A.x + b * A.y + c * A.z;
      pol.setAbch(a, b, c, h);
      Point2D A1 = vScr[iA], B1 = vScr[iB], C1 = vScr[iC];
      u1 = B1.x - A1.x; u2 = B1.y - A1.y;
      v1 = C1.x - A1.x; v2 = C1.y - A1.y;
      if (u1 * v2 - u2 * v1 <= 0) continue; // backface
      double inprod = a * sunX + b * sunY + c * sunZ;
      if (inprod < inprodMin) inprodMin = inprod; 
      if (inprod > inprodMax) inprodMax = inprod;
    }
    inprodRange = inprodMax - inprodMin;
  }

  boolean vp(Canvas cv, float dTheta, float dPhi, float fRho)
  {  theta += dTheta;
     phi += dPhi;
     float rhoNew = fRho * rho;
     if (rhoNew >= rhoMin && rhoNew <= rhoMax)
        rho = rhoNew;
     else
        return false;
     cv.repaint();
     return true;
 }
 int colorCode(double a, double b, double c)
 {  double inprod = a * sunX + b * sunY + c * sunZ;
    return (int)Math.round(
       ((inprod - inprodMin)/inprodRange) * 255);
 }
}




class Input
{  private PushbackInputStream pbis;
   private boolean ok = true;
   private boolean eoFile = false;

   Input(){pbis = new PushbackInputStream(System.in);}

   Input(String fileName)
   {  try
      {   InputStream is = new FileInputStream(fileName);
          pbis = new PushbackInputStream(is);
      }
      catch(IOException ioe){ok = false;}
   }

   int readInt()
   {  boolean neg = false;
      char ch;
      do {ch = readChar();}while (Character.isWhitespace(ch));
      if (ch == '-'){neg = true; ch = readChar();}
      if (!Character.isDigit(ch))
      {  pushBack(ch);
         ok = false;
         return 0;
      }
      int x = ch - '0';
      for (;;)
      {  ch = readChar();
         if (!Character.isDigit(ch)){pushBack(ch); break;}
         x = 10 * x + (ch -  '0');
      }
      return (neg ? -x : x);
    }
float readFloat()
{  char ch;
   int nDec = -1;
   boolean neg = false;
   do
   {  ch = readChar();
   }   while (Character.isWhitespace(ch));
   if (ch == '-'){neg = true; ch = readChar();}
   if (ch == '.'){nDec = 1; ch = readChar();}
   if (!Character.isDigit(ch)){ok = false; pushBack(ch); return 0;}
   float x = ch - '0';
   for (;;)
   {  ch = readChar();
      if (Character.isDigit(ch))
      {  x = 10 * x + (ch - '0');
         if (nDec >= 0) nDec++;
      }
      else
      if (ch == '.' && nDec == -1) nDec = 0;
      else break;
   }
   while (nDec > 0){x *= 0.1; nDec--;}
   if (ch == 'e'  ||   ch == 'E')
   {  int exp = readInt();
      if (!fails())
      {   while (exp < 0){x *= 0.1; exp++;}
          while (exp > 0){x *= 10; exp--;}
      }
   }
   else pushBack(ch);
   return (neg ? -x : x);
}

char readChar()
{  int ch=0;
   try
   {   ch = pbis.read();
       if (ch == -1) {eoFile = true; ok = false;}
   }
   catch(IOException ioe){ok = false;}
   return (char)ch;
}

   String readString()    // Read first string between quotes (").
   {  String str = " ";
      char ch;
      do ch = readChar(); while (!(eof()  || ch == '"'));
                                                   // Initial quote
      for (;;)
      {   ch = readChar();
         if (eof()  ||   ch == '"') // Final quote (end of string)
            break;
         str += ch;
      }
      return str;
   }

   void skipRest()   // Skip rest of line
   {  char ch;
      do ch = readChar(); while (!(eof()  || ch == '\n'));
   }

   boolean fails(){return !ok;}
   boolean eof(){return eoFile;}
   void clear(){ok = true;}

   void close()
   {  if (pbis != null)
      try {pbis.close();}catch(IOException ioe){ok = false;}
   }

   void pushBack(char ch)
   {  try {pbis.unread(ch);}catch(IOException ioe){}
   }
}



//Tools2D.java: Class to be used in other program files.
//Uses: Point2D (Section 1.5) and Triangle (discussed above).

class Tools2D
{  static float area2(Point2D a, Point2D b, Point2D c)
{ return (a.x - c.x) * (b.y - c.y) - (a.y - c.y) * (b.x - c.x);
}

static boolean insideTriangle(Point2D a, Point2D b, Point2D c,
   Point2D p) // ABC is assumed to be counter-clockwise
{ return
    Tools2D.area2(a, b, p) >= 0 &&
    Tools2D.area2(b, c, p) >= 0 &&
    Tools2D.area2(c, a, p) >= 0;
}

static void triangulate(Point2D[] p, Triangle[] tr)
{  // p contains all n polygon vertices in CCW order.
   // The resulting triangles will be stored in array tr.
   // This array tr must have length n - 2.
   int n = p.length, j = n - 1, iA=0, iB, iC;
   int[] next = new int[n];
   for (int i=0; i<n; i++)
   {  next[j] = i;
      j = i;
   }
   for (int k=0; k<n-2; k++)
   {  // Find a suitable triangle, consisting of two edges
      // and an internal diagonal:
      Point2D a, b, c;
      boolean triaFound = false;
      int count = 0;
      while (!triaFound && ++count < n)
      {  iB = next[iA]; iC = next[iB];
         a = p[iA]; b = p[iB]; c = p[iC];
         if (Tools2D.area2(a, b, c) >= 0)
         {  // Edges AB and BC; diagonal AC.
            // Test to see if no other polygon vertex
            // lies within triangle ABC:
            j = next[iC];
            while (j != iA && !insideTriangle(a, b, c, p[j]))
               j = next[j];
            if (j == iA)
            {  // Triangle ABC contains no other vertex:
               tr[k] = new Triangle(a, b, c);
               next[iA] = iC;
               triaFound = true;
            }
         }
         iA = next[iA];
      }
      if (count == n)
      {  System.out.println("Not a simple polygon" +
           " or vertex sequence not counter-clockwise.");
         System.exit(1);
      }
   }
}

static float distance2(Point2D p, Point2D q)
{  float dx = p.x - q.x,
      dy = p.y - q.y;
return dx * dx + dy * dy;
}
}

//Triangle.java: Class to store a triangle;
//vertices in logical coordinates.
//Uses: Point2D (Section 1.5).

class Triangle
{  Point2D a, b, c;
Triangle(Point2D a, Point2D b, Point2D c)
{ this.a = a; this.b = b; this.c = c;
}
}

