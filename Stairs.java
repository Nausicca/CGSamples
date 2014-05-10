/*@author: Chandhya Thirugnanasambantham
 * stairs.dat file gets generated depending on the 3 input arguments supplied.
 * The input format is same as that given in the question.
 * java Stairs 25 15 Stairs.dat - Format: java Stairs <number of stairs> <rotation angle> <outputfile.dat>
 * Procedure applied is as follows:
 * Both Beams.java and Cylinder.java were used and integrated together.
 * For the cylinder:
 * -----------------
 * The inner radius is made zero(since it is solid cylinder).The outer radius is kept a constant value.
 * The value of 'n' depends on the input supplied.(same value as the number of stairs)
 * The height of the cylinder is estimated depending on the value of n('heightpm' is the variable used and varies by a factor of 0.6 from the value of n.)
 * The faces are drawn again depending on the value of outer radius.Some constants n2,n3 are used for different faces(bottom and vertical faces)
 * 
 * For the Beams:
 * --------------
 * The values of height,width and length are altered to resemble the given figure.(Value of a is altered to provide a reasonable length)
   And the height given by the Zaxis is also reduced to produce an output close to the figure.
 * The value of angle of rotation depends on the input.
 * For the faces,the indexing of vertex numbers depends on a factor multiplied by 10,since 2 vertex numbers are alloted to the railing.
 * 
 * For the Railing:
 * ----------------
 * The values of the co-ordinates are generated based on the x,y,z axis values of the stairs.
 * And the for face generation,we connect just two vertex numbers to form segments.
 * 
 * I've answered the bonus question as well.Please look at painter.java and the edits in CVPainter.java
 * 
 * */
import java.io.*;

public class Stairs
{
	public static void main(String[] args)throws IOException
   {  if (args.length != 3)
      {  System.out.println(
         "Supply n (> 0), alpha (in degrees)\n" +
         "and a filename as program arguments.\n"); 
         System.exit(1);
      }
      int n = 0; 
      double a = 6.0, alphaDeg = 0;
      try
      {  n = Integer.valueOf(args[0]).intValue();
         //a = Double.valueOf(args[1]).doubleValue();
         alphaDeg = Double.valueOf(args[1]).doubleValue();
         if (n <= 0 || a < 0.5)throw new NumberFormatException();
      }
      catch (NumberFormatException e)
      {  System.out.println("n must be an integer > 0");
        // System.out.println("a must be a real number >= 0.5");
         System.out.println("alpha must be a real number");
         System.exit(1);
      }
      new StairsGen(n, a, alphaDeg * Math.PI / 180, args[2]);
    }
}
//Point3D.java: Representation of a point in 3D space.
class Point3D
{  float x, y, z;
   Point3D(double x, double y, double z)
   {  this.x = (float)x;
      this.y = (float)y;
      this.z = (float)z;
   }
}
//Actual class containing the methods required for stairs generation.
class StairsGen
{  FileWriter fw;
int n2 = 2 * 20, n3 = 3 * 20, n4 = 4 * 20,m,temp=0;float cache =0;
StairsGen(int n, double a, double alpha, String fileName)
      throws IOException
   {  fw = new FileWriter(fileName);
      Point3D[] P = new Point3D[9];
      double b = a - 2.8;
    P[1] = new Point3D(a, -a, 0);
      P[2] = new Point3D(a,  a, 0);
      P[3] = new Point3D(b,  a, 0);
      P[4] = new Point3D(b, -a, 0);
      P[5] = new Point3D(a, -a, 0.2);
      P[6] = new Point3D(a,  a, 0.2);
      P[7] = new Point3D(b,  a, 0.2);
      P[8] = new Point3D(b, -a, 0.2);
    
      //generate vertices for the Beam 
      for (int k=0; k<n; k++)
      {  
         double phi = k * alpha,
            cosPhi = Math.cos(phi), sinPhi = Math.sin(phi),x,y;
         float x1=0,y1=0,z1=0, newx=0,newy=0,newz=0;
		
    
         for (int i=1; i<=8; i++)
         {   
        	  x = P[i].x; y = P[i].y; 
          
        	  x1 = (float)(x * cosPhi - y * sinPhi);
               y1 = (float)(x * sinPhi + y * cosPhi);
               z1 = (float)((P[i].z + k));
               if(i==1||i==2)
             	    {newx =x1+newx;newy=y1+newy;newz=(z1+newz);}
               temp++;  
         fw.write((temp) + " " + x1 + " " + y1 + " " + z1 +
            "\r\n");
       
      }
       //generate vertices for the railing
         temp++;
         fw.write((temp) + " " + newx/2 + " " + newy/2 + " " + newz/2 +
                 "\r\n");
         temp++;
                 if(temp==10)
                 {cache=(newx/2);
                 cache+=2;
                	 fw.write((temp) + " " + newx/2 + " " + newy/2 + " " + (cache) +
                         "\r\n");}
                 else{
                	 cache+=1;
                	 fw.write((temp) + " " + newx/2 + " " + newy/2 + " " + (cache) +
                         "\r\n");}
    }
      temp++;
     //generate vertices for cylinder 
      genCylinder(n, Float.valueOf((float) (0.2/2)), 0);
      fw.write("Faces:\r\n");
      //generate faces for cylinder
      drawFaces(n,n2,n3,n4,0,Float.valueOf((float) (0.2/2)));
      
   
   //generate faces for the beam 
    for (int k=0; k<n; k++)
    {  // Beam k again:
       int m = 10 * k;
       face(m, 1, 2, 6, 5);
       face(m, 4, 8, 7, 3);
       face(m, 5, 6, 7, 8);
       face(m, 1, 4, 3, 2);
       face(m, 2, 3, 7, 6);
       face(m, 1, 5, 8, 4);

    }
    //generate faces for railing
    int railx=9,raily=10;
    fw.write(railx+" "+   
            +(raily)+".\r\n");
    for(int f=1;f<n;f++){
    	railx++;raily+=10;
    	fw.write(railx+" "+
                +(raily)+".\r\n");
    	railx+=9;
    	fw.write(railx+" "+
                +(raily)+".\r\n");
    
    }
    fw.close();
  }
  
void face(int m, int a, int b, int c, int d)throws IOException
  {  a += m; b += m; c += m; d += m;
     fw.write(a + " " + b + " " + c + " " + d + ".\r\n");
  }

//Method to generate vertices for cylinder
  void genCylinder(int n, float rOuter, float rInner)
		   throws IOException
		{  
		   double delta=2* Math.PI / n;
		   double heightpm = (n/25)*15;
		   for (int i=1; i<=n; i++)
		   { 
			   double alpha=i* delta,
		        cosa = Math.cos(alpha), sina = Math.sin(alpha);
		     
		      double r = rOuter ;
		       if (r > 0)
		       for (int bottom=0; bottom<2; bottom++)
		       { int k = (2 * 0 +  bottom)   * n + i;
		          // Vertex numbers for i = 1:
		          // Top:      1 (outer) 2n+1 (inner)
		          // Bottom: n+1 (outer) 3n+1 (inner)
		          wi(k+temp); // w = write, i = int, r = real
		          wr(r * cosa*10); wr(r * sina*10); // x and y
		          if((k+temp>temp+25)&& (k+temp<temp+51))
		        	  wi(0);
		          else
		        	  wi((int) ((2 - bottom)*heightpm)); // bottom: z = 0; top: z = 1
		          fw.write("\r\n");
		       }
		     }
		   }
  //Method to generate faces for cylinder
		private void drawFaces(int n, int n2, int n3,int n4, float rInner, float rOuter) 
		{
			try { 
					for (int i=1; i<=n; i++)
					wi(i+temp);
					fw.write(".\r\n");
					// Bottom boundary face:
					for (int i=n2; i>=n+1; i--) wi(i+temp);
				    fw.write(".\r\n");
				    // Vertical, rectangular faces:
				    for (int i=1; i<=n; i++)
				    { 
				    	int j = i % n + 1;
				    	// Outer rectangle:
				    	wi(j+temp); wi(i+temp); wi(i + n+temp); wi(j + n+temp); fw.write(".\r\n");
				    }
				} catch (IOException e) 
				{
					e.printStackTrace();
				}
		}
		void wi(int x)
		   throws IOException
		{
			fw.write(" " + String.valueOf(x));
		}

		void wr(double x)
		   throws IOException
		{ 
			if (Math.abs(x) < 1e-9) x = 0;
		  fw.write(" " + String.valueOf((float)x));
		 }
	}



