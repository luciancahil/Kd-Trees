import java.util.ArrayList;
import java.util.TreeSet;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;

public class PointSET {
		private int size;
		private final TreeSet<Point2D> tree;
		
		public PointSET() {                               // construct an empty set of points
			tree = new TreeSet<Point2D>();
			size = 0;
	   }

	   public boolean isEmpty() {	                   // is the set empty? 
		   	return size == 0;
	   }
	   
	   public int size() {                       // number of points in the set 
		   	return size;
	   }
	   
	   public void insert(Point2D p)    {          // add the point to the set (if it is not already in the set)
		   if(p == null) {
			   throw new IllegalArgumentException();
		   }
		   
		   if(!tree.contains(p)) {
			   tree.add(p);
			   size++;
		   }
	   }
	   
	   
	   public boolean contains(Point2D p) {            // does the set contain point p? 
		   if(p == null) {
			   throw new IllegalArgumentException();
		   }
		   
		   	return tree.contains(p);
	   }
	   
	   
	   public void draw()  {                       // draw all points to standard draw 
		   for(Point2D p: tree) {
			   p.draw();
		   }
	   }
	   
	   public Iterable<Point2D> range(RectHV rect) {            // all points that are inside the rectangle (or on the boundary) 
		   if(rect == null) {
			   throw new IllegalArgumentException();
		   }
		   
		   	ArrayList<Point2D> inRange = new ArrayList<Point2D>();
		   	
		   	for(Point2D p: tree) {
		   		double x = p.x();
		   		double y = p.y();
		   		
		   		if(x >= rect.xmin() && x <= rect.xmax() && y >= rect.ymin() && y <= rect.ymax()) {
		   			inRange.add(p);
		   		}
		   	}
		   	
		   	return inRange;
	   }
	   
	   public Point2D nearest(Point2D p) {            // a nearest neighbor in the set to point p; null if the set is empty 
		   if(p == null) {
			   throw new IllegalArgumentException();
		   }
		   
		   if(size == 0)
			   return null;
		   
		   Point2D closest = tree.first();
		   double distance = Math.pow((p.x() - closest.x()), 2) + Math.pow((p.y() - closest.y()), 2);
		   
		   for(Point2D inSet: tree) {
			   double newDistance = Math.pow((p.x() - inSet.x()), 2) + Math.pow((p.y() - inSet.y()), 2);
			   
			   if(newDistance < distance) {
				   closest = inSet;
				   distance = newDistance;
			   }
		   }
		   
		   return closest;
	   }

	   public static void main(String[] args) {             // unit testing of the methods (optional) 
		   PointSET ps = new PointSET();
		   
		   System.out.println("Is Empty Before: " + ps.isEmpty());
		   ps.nearest(new Point2D(0, 0));
		   for(int i = 0; i < args.length; i+= 2) {
			   double x = Double.parseDouble(args[i]);
			   double y = Double.parseDouble(args[i + 1]);
			   
			   Point2D p = new Point2D(x, y);
			   ps.insert(p);
		   }
		   
		   double x = Double.parseDouble(args[0]);
		   double y = Double.parseDouble(args[1]);
		   
		   Point2D testing = new Point2D(0.47, x);
		   Point2D inside = new Point2D(x, y);
		   
		   
		   System.out.println("Is Empty After: " + ps.isEmpty());
		   
		   System.out.println("Contains testing: " + ps.contains(testing));
		   System.out.println("Contains inside: " + ps.contains(inside));
		   System.out.println("Nearest: " + ps.nearest(testing));
		   System.out.println("Size Before Inserting Inside: " + ps.size());
		   ps.insert(inside);
		   System.out.println("Size After Inserting Inside: " + ps.size());
		   System.out.println("Size Before Inserting testing: " + ps.size());
		   ps.insert(testing);
		   System.out.println("Size After Inserting testing: " + ps.size());
		   
		   RectHV rect = new RectHV(0.2, 0.3, 0.4, 0.6);
		   System.out.println("Within rect: " + ps.range(rect));
	   }
}
