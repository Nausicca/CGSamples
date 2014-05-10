/*@author:Chandhya thirugnanasambantham
 * The graphic editor has all the features of the described problem.
 * There is a dropdown menu to select options.
 * Pythogoras:
 * ------------
 * If Pythogoras is chosen, a pythogoras tree is drawn,with the given number of steps.
 * There is a default value for the number of steps -10
 * Graph Mode:
 * ----------
 * If graph is chosen,we need to click on a random point in the panel area and then click on 'node' button.
 * This will generate a node in the selected position.
 * Now same process can be repeated to draw another node.
 * Once nodes are drawn,'link' button has to be selected.
 * Now,in connection mode, if we drag the mouse from source to destination node ,making sure that we click within the nodes,a link
 * will be drawn.
 * An error message will be displayed if clicked anywhere else.
 * 
 * A 'Clear' button is provided to clear screen contents.Quit will exit and close the window.
 * 
 * Implementation:
 * 5 Separate panels were used.Action listeners were used for each button and combo boxes.
 * The images files for node and link have to placed in the same path.
 * Pythogoras tree is drawn with the standard algorithm,based on height and width and the 3 co-ordinates are computed recursively
   depending on the step size.
 * For drawing nodes and interconnecting them,mouse position is captured through different mouse events and is used to draw
   for the appropriate co-ordinates. 
 * 
 * */
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
public class GraphicEditor extends Frame {
	
	static GraphicEditor ge;
	private static final long serialVersionUID = 1L;
	private static final int WIDE = 640;
    private static final int HIGH = 480;
    private int height = 600;
    private int width = 600;
    private static final int RADIUS = 35;
    JSpinner js = new JSpinner();
    int n1=10;int temp=10;
    private int radius = RADIUS;
	
	private Action newNode = new NewNodeAction("Node");
	private Action linkNode = new ConnectAction("link");
	Action ClearButtonAction = new ClearAction("Clear");
	private boolean linkMode=false;
	private Action comboAction = new SelectOption("Select an option:");
	
    private JLabel title = new JLabel("Graphic Editor");
    
    private JComboBox<Options> combo = new JComboBox<Options>();
    
    private JLabel graphLabel1 = new JLabel("1.Please click on a random Point to mark the position where you want a node to appear");
    private JLabel graphLabel2 = new JLabel("2.Click on 'Node' button and a node appears in the marked position");
    private JTextArea graphLabel3 = new JTextArea("3.Click on 'link' button to switch to connection mode (or) click 'Node' button to add more nodes as per the stated procedure!");
    private JTextArea graphLabel4 = new JTextArea("4.Now,drag from start node to destination node to connect the nodes!(or) click 'Node' button to add more nodes!");
    JLabel graphLabel5 = new JLabel("Note: Please click within two nodes to connect them!");
    private JButton node = new JButton(new ImageIcon("node.gif"));
    
    private JButton link = new JButton(new ImageIcon("link.gif"));
    private JPanel sidePanel;
    private JPanel topPanel;
    private JPanel btnPanel;
    private JPanel graphPanel;
   
    private List<Node> nodes = new ArrayList<Node>();
    private List<Node> selected = new ArrayList<Node>();
    private List<Edge> edges = new ArrayList<Edge>();
    private Point mousePt = new Point(WIDE / 2, HIGH / 2);
    private Point mouseEndPt = new Point(WIDE / 2, HIGH / 2);
    private Point mouseStartPt = new Point(WIDE / 2, HIGH / 2);
    private boolean selecting = false;
    private Rectangle mouseRect = new Rectangle();
	public static void main(String args[]){   
	ge = new GraphicEditor();
	ge.init();
	} 
	void init(){
		//super("GraphicEditor");
			addWindowListener(new WindowAdapter()
	       {
				public void windowClosing(WindowEvent e){System.exit(0);}});
			
			node.setText("Node");
			link.setText("Link");
			link.setMargin(new Insets(8,1,12,10));
			node.addActionListener(newNode);
			link.addActionListener(linkNode);
			setSize (800,800);
			topPanel = new JPanel();
			sidePanel = new JPanel();
			sidePanel.setLayout(new GridLayout(9,1));
			node.setPreferredSize(new Dimension(new ImageIcon("node.gif").getIconWidth(), new ImageIcon("node.gif").getIconWidth()));
			sidePanel.add(node);
			link.setLocation(1, 7);
			sidePanel.add(link);
			Font curFont = title.getFont();
			title.setFont(new Font(curFont.getFontName(), curFont.getStyle(), 24));
			title.setSize(80, 160);
			for (Options op : Options.values()) {
            combo.addItem(op);
          }
		 
		combo.addActionListener(comboAction);
          
	        btnPanel = new JPanel() {

				private static final long serialVersionUID = 1L;
				@Override
            public void paintComponent(Graphics g) {
			
				g.setColor(Color.white);
				g.fillRect(0, 0, width, height);
				g.setColor(Color.black);
				g.setFont(new Font("Arial",Font.BOLD,14));
	             int x1, x2, x3, y1, y2, y3;
				 int base = width/7;
	             g.drawString("Please Select number of steps - ", 110, 20);
	             g.drawString("The default is 10!", 160, 35);
	             x1 = (width/2)-(base/2);
	             x2 = (width/2)+(base/2);
	             x3 = width/2;
	             y1 = (height-(height/15))-base;
	             y2 = height-(height/15);
	             y3 = (height-(height/15))-(base+(base/2));
	             g.drawPolygon(new int[]{x1, x1, x2, x2, x1}, new int[]{y1, y2, y2, y1, y1}, 5);
	        	 n1 = temp;
	              if(--n1 > 0){
	            	  g.drawPolygon(new int[] {x1, x3, x2}, new int[] {y1, y3, y1}, 3);
	                 paintMore(n1, g, x1, x3, x2, y1, y3, y1);
	                 paintMore(n1, g, x2, x3, x1, y1, y3, y1);
	             }
	            
			}
			public void paintMore(int n1, Graphics g, double x1_1, double x2_1, double x3_1, double y1_1, double y2_1, double y3_1){
		        int x1, x2, x3, y1, y2, y3;

		        x1 = (int)(x1_1 + (x2_1-x3_1));
		        x2 = (int)(x2_1 + (x2_1-x3_1));
		        x3 = (int)(((x2_1 + (x2_1-x3_1)) + ((x2_1-x3_1)/2)) + ((x1_1-x2_1)/2));
		        y1 = (int)(y1_1 + (y2_1-y3_1));
		        y2 = (int)(y2_1 + (y2_1-y3_1));
		        y3 = (int)(((y1_1 + (y2_1-y3_1)) + ((y2_1-y1_1)/2)) + ((y2_1-y3_1)/2));

		        g.setColor(Color.green);
		        g.drawPolygon(new int[] {x1, x2, (int)x2_1, x1}, new int[] {y1, y2, (int)y2_1, y1}, 4);
		        g.drawLine((int)x1, (int)y1, (int)x1_1, (int)y1_1);
		        g.drawLine((int)x2_1, (int)y2_1, (int)x2, (int)y2);
		        g.drawLine((int)x1, (int)y1, (int)x2, (int)y2);

		        if(--n1 > 0){
		            g.drawLine((int)x1, (int)y1, (int)x3, (int)y3);
		            g.drawLine((int)x2, (int)y2, (int)x3, (int)y3);
		            paintMore(n1, g, x1, x3, x2, y1, y3, y2);
		            paintMore(n1, g, x2, x3, x1, y2, y3, y1);
		        }
		    }
	        };
			  js.setModel(new SpinnerNumberModel(10, 3,20,2));
			  js.addChangeListener(new ChangeListener(){

				@Override
				public void stateChanged(ChangeEvent e) {
					 JSpinner s = (JSpinner) e.getSource();
	                 temp = (Integer) s.getValue();
	                 System.out.println(temp);
	                 if(temp!=n1)
	                 { n1=temp;
	                    btnPanel.repaint();
	                 }
					
				}});

	        js.setLocation(300, 600);
	        btnPanel.add(js,BorderLayout.EAST);
	        
	        graphPanel = new JPanel(){

	        	
	        	private static final long serialVersionUID = 1L;
	        	 @Override
	        	    public Dimension getPreferredSize() {
	        	        return new Dimension(WIDE, HIGH);
	        	    }
				@Override
			    public void paintComponent(Graphics g) {
					
			        g.setColor(new Color(0x00f0f0f0));
			        g.fillRect(0, 0, getWidth(), getHeight());
			        for (Edge e : edges) {
			            e.draw(g);
			        }
			        for (Node n : nodes) {
			            n.draw(g);
			        }
			        if (selecting) {
			            g.setColor(Color.GREEN);
			            g.drawRect(mouseRect.x, mouseRect.y,
			                mouseRect.width, mouseRect.height);
			        }
			    }
			};
	        graphPanel.add(graphLabel1,BorderLayout.NORTH);
	        graphPanel.add(graphLabel2,BorderLayout.AFTER_LAST_LINE);
	        graphPanel.add(graphLabel3,BorderLayout.AFTER_LAST_LINE);
	        graphPanel.add(graphLabel4,BorderLayout.AFTER_LAST_LINE);
	        graphLabel3.setSize(450, 50);
	        graphLabel3.setFont(graphLabel2.getFont());
	        graphLabel3.setBackground(graphLabel2.getBackground());
	        graphLabel3.setLineWrap(true);
	        graphLabel4.setSize(450, 50);
	        graphLabel4.setFont(graphLabel2.getFont());
	        graphLabel4.setBackground(graphLabel2.getBackground());
	        graphLabel4.setLineWrap(true);
	        graphPanel.setVisible(false);
	        btnPanel.setVisible(true);
	        Dimension titled = new Dimension();
	        titled.height = 500;
	        titled.width = 800; 
	        title.setSize(titled);
	        
	        topPanel.setLayout(new GridBagLayout());
	         
	        GridBagConstraints c = new GridBagConstraints();
	        
	   
	        c.weightx = 0.5;
	        c.gridwidth = 3;
	        c.gridx = 2;
	        c.gridy = 0;
	        
	        topPanel.add(title, c);
	        
	        c.gridwidth = 3;
	        c.gridx = 1;
	        c.gridy = 1;
	        
	        
	        topPanel.add(new JLabel("Select an option from the dropdown:"), c);
	      
	        
	        c.gridx = 2;
	        c.gridy = 1;
	        
		    topPanel.add(combo, c);
		    
		     
	        c.gridx = 4;
	        c.gridy = 1;
	        JButton Clearall = new JButton(ClearButtonAction);
	        topPanel.add(Clearall, c);
	        add(topPanel, BorderLayout.NORTH);
	        
	       
	       add(btnPanel,BorderLayout.CENTER);
	        add(sidePanel,BorderLayout.WEST);
	        btnPanel.setVisible(false);
	        setVisible(true);
	     }
	private class ClearAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public ClearAction(String name) {
            super(name);
        }

        public void actionPerformed(ActionEvent e) {
        	btnPanel.repaint();
        	graphPanel.repaint();
        	edges.clear();
        	nodes.clear();
        	btnPanel.setVisible(false);
        	graphPanel.setVisible(false);
        	combo.setSelectedItem(Options.Options);
        }
    }
	private class MouseHandler extends MouseAdapter {

        @Override
        public void mouseReleased(MouseEvent e) {
            selecting = false;
           
            mouseEndPt = e.getPoint(); 
            Node n1=null;
            Node n2 = null;
            graphPanel.add(graphLabel5,BorderLayout.PAGE_END);
            graphLabel5.setVisible(false);
            mouseRect.setBounds(0, 0, 0, 0);
            if(linkMode)
            	if (nodes.size() > 1) {
	                for (int i = 0; i < nodes.size(); ++i) {
	                	
	                	if(nodes.get(i).contains(mouseStartPt))
	                	{
	                		
	                     n1 = nodes.get(i);
	                   
	                	}
	                	else if(nodes.get(i).contains(mouseEndPt))
	                    {
	                		n2 = nodes.get(i );
	                		
	                    }
	                   
	                	graphLabel5.setVisible(false); 
	                }
	                
					if(n1!=null && n2!=null)
	                {edges.add(new Edge(n1, n2));
					graphLabel5.setVisible(false);
	                }
	                else
	                	{graphLabel5.setForeground(Color.RED);
	                	graphLabel5.setVisible(true);
	                	}
	                
	            }
	           
           
            e.getComponent().repaint();
        }

        @Override
        public void mousePressed(MouseEvent e) {
            mousePt = e.getPoint();
            mouseStartPt = e.getPoint();
            graphLabel5.setVisible(false);
            if (e.isShiftDown()) {
                Node.selectToggle(nodes, mousePt);
            }  else if (Node.selectOne(nodes, mousePt)) {
                selecting = false;
            } else {
                Node.selectNone(nodes);
                selecting = true;
            }
            e.getComponent().repaint();
        }

       
    }
	 private class MouseMotionHandler extends MouseMotionAdapter {

	        Point delta = new Point();

	        @Override
	        
	        public void mouseDragged(MouseEvent e) {
	        	if(!linkMode){
	            if (selecting) {
	                mouseRect.setBounds(
	                    Math.min(mousePt.x, e.getX()),
	                    Math.min(mousePt.y, e.getY()),
	                    Math.abs(mousePt.x - e.getX()),
	                    Math.abs(mousePt.y - e.getY()));
	                Node.selectRect(nodes, mouseRect);
	            } else {
	                delta.setLocation(
	                    e.getX() - mousePt.x,
	                    e.getY() - mousePt.y);
	                Node.updatePosition(nodes, delta);
	                mousePt = e.getPoint();
	            }
	            e.getComponent().repaint();
	        }
	        }
	    }

	    
	private class NewNodeAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public NewNodeAction(String name) {
            super(name);
        }

        public void actionPerformed(ActionEvent e) {
        	linkMode =false;
            Node.selectNone(nodes);
           
	      
            Point p = mousePt.getLocation();
            
          
            Node n = new Node(p, radius);
            n.setSelected(true);
            nodes.add(n);
            graphPanel.repaint();
            graphLabel3.setVisible(true);
            graphLabel1.setVisible(false);
            graphLabel2.setVisible(false);
            graphLabel4.setVisible(false);
        }
    }
	private  enum Options {

		Options,Pythogoras,Graph,Quit;
    }
	
	 /**
     * An Edge is a pair of Nodes.
     */
    private static class Edge {

        private Node n1;
        private Node n2;

        public Edge(Node n1, Node n2) {
            this.n1 = n1;
            this.n2 = n2;
        }

        public void draw(Graphics g) {
            Point p1 = n1.getLocation();
            Point p2 = n2.getLocation();
            g.setColor(Color.darkGray);
            g.drawLine(p1.x, p1.y, p2.x, p2.y);
        }
    }
	 private static class Node {

	        private Point p;
	        private int r;
	        
	        
	        private boolean selected = false;
	        private Rectangle b = new Rectangle();

	        /**
	         * Construct a new node.
	         */
	        public Node(Point p, int r ) {
	            this.p = p;
	            this.r = r;
	            
	            
	            setBoundary(b);
	        }

	        /**
	         * Calculate this node's rectangular boundary.
	         */
	        private void setBoundary(Rectangle b) {
	            b.setBounds(p.x - r, p.y - r, 2 * r, 2 * r);
	        }

	        /**
	         * Draw this node.
	         */
	        public void draw(Graphics g) {
	            g.setColor(Color.red);
	            
	                g.fillOval(b.x, b.y, b.width, b.height);
	              //  g.fillRect(b.x, b.y, b.width, b.height);
	           
	            if (selected) {
	                g.setColor(Color.blue);
	                g.drawRect(b.x, b.y, b.width, b.height);
	            }
	        }

	        /**
	         * Return this node's location.
	         */
	        public Point getLocation() {
	            return p;
	        }

	        /**
	         * Return true if this node contains p.
	         */
	        public boolean contains(Point p) {
	            return b.contains(p);
	        }

	        /**
	         * Return true if this node is selected.
	         */
	        public boolean isSelected() {
	            return selected;
	        }

	        /**
	         * Mark this node as selected.
	         */
	        public void setSelected(boolean selected) {
	            this.selected = selected;
	        }

	        /**
	         * Collected all the selected nodes in list.
	         */
	        public static void getSelected(List<Node> list, List<Node> selected) {
	            selected.clear();
	            for (Node n : list) {
	                if (n.isSelected()) {
	                    selected.add(n);
	                }
	            }
	        }

	        /**
	         * Select no nodes.
	         */
	        public static void selectNone(List<Node> list) {
	            for (Node n : list) {
	                n.setSelected(false);
	            }
	        }

	        /**
	         * Select a single node; return true if not already selected.
	         */
	        public static boolean selectOne(List<Node> list, Point p) {
	            for (Node n : list) {
	                if (n.contains(p)) {
	                    if (!n.isSelected()) {
	                        Node.selectNone(list);
	                        n.setSelected(true);
	                    }
	                    return true;
	                }
	            }
	            return false;
	        }

	        /**
	         * Select each node in r.
	         */
	        public static void selectRect(List<Node> list, Rectangle r) {
	            for (Node n : list) {
	                n.setSelected(r.contains(n.p));
	            }
	        }

	        /**
	         * Toggle selected state of each node containing p.
	         */
	        public static void selectToggle(List<Node> list, Point p) {
	            for (Node n : list) {
	                if (n.contains(p)) {
	                    n.setSelected(!n.isSelected());
	                }
	            }
	        }

	        /**
	         * Update each node's position by d (delta).
	         */
	        public static void updatePosition(List<Node> list, Point d) {
	            for (Node n : list) {
	                if (n.isSelected()) {
	                    n.p.x += d.x;
	                    n.p.y += d.y;
	                    n.setBoundary(n.b);
	                }
	            }
	        }
	    	    }
	 private class ConnectAction extends AbstractAction {
			private static final long serialVersionUID = 1L;

			public ConnectAction(String name) {
	            super(name);
	        }

	        public void actionPerformed(ActionEvent e) {
	        	linkMode = true;
	            Node.getSelected(nodes, selected);
	            
	            if (selected.size() > 1) {
	                for (int i = 0; i < selected.size() - 1; ++i) {
	                    Node n1 = selected.get(i);
	                    Node n2 = selected.get(i + 1);
	                    edges.add(new Edge(n1, n2));
	                }
	            }
	            graphLabel4.setVisible(true);
	            graphLabel1.setVisible(false);
	            graphLabel2.setVisible(false);
	            graphLabel3.setVisible(false);
	            
	            repaint();
	        }
	    }


	 private class SelectOption extends AbstractAction {

			private static final long serialVersionUID = 1L;

			public SelectOption(String name) {
	            super(name);
	        }
			
	        public void actionPerformed(ActionEvent e) {
	        	Options selected=null;
	        	 @SuppressWarnings("unchecked")
				JComboBox<Options> combo = (JComboBox<Options>) e.getSource();
	        	 
	        	 selected =  (Options)combo.getSelectedItem();
	        	
	        	 if(selected!=null && selected.equals(Options.Pythogoras))
	        		 {
	        		 graphPanel.setVisible(false);
	        		 btnPanel.repaint();
	 	        	btnPanel.setVisible(true);
	 	        	ge.add(btnPanel,BorderLayout.CENTER);
	        		
	        		 }
	        		 else if(selected!=null && selected.equals(Options.Graph))
	        		 {
	        			 	btnPanel.setVisible(false);
	        	        	ge.add(graphPanel,BorderLayout.CENTER);
	        	        	//graphPanel.repaint();
	        	        	graphPanel.setVisible(true);
	        	        	graphLabel1.setVisible(true);
	        	        	graphLabel2.setVisible(true);
	        	        	graphLabel3.setVisible(false);
	        	        	graphLabel4.setVisible(false);
	        	        	graphPanel.addMouseListener(new MouseHandler());
	        	        	graphPanel.addMouseMotionListener(new MouseMotionHandler());
	        	            
	        		 }
	        		 else if(selected!=null && selected.equals(Options.Quit))
	        			 {  
	     	        	ge.dispose(); 
	    	            }
	        }
	    }
	   }
