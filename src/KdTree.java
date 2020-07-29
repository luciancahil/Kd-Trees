import java.util.Iterator;
import java.util.LinkedList;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;

public class KdTree {
	private int size;
	private TSet tree;
	
	
	public KdTree() {                               // construct an empty set of points
		size = 0;
		tree = new TSet();
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
	   
	   if(tree.add(p))
		   size++;
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
   
   
   //TODO FIX THE ITERABLE AND NEAREST METHOD
   public Iterable<Point2D> range(RectHV rect) {            // all points that are inside the rectangle (or on the boundary) 
	   if(rect == null) {
		   throw new IllegalArgumentException();
	   }
	   
	   return tree.inRange(rect);
   }
   
   public Point2D nearest(Point2D p) {            // a nearest neighbor in the set to point p; null if the set is empty 
	   if(p == null) {
		   throw new IllegalArgumentException();
	   }
	   
	   if(size == 0)
		   return null;
	   
	   Point2D closest = tree.nearest(p);
	   
	   return closest;
   }

   public static void main(String[] args) {             // unit testing of the methods (optional) 
	   KdTree ps = new KdTree();
	   System.out.println("hi");
	   //RectHV rect = new RectHV(0.2, 0.3, 0.4, 0.6);
	   
	   System.out.println("Is Empty Before: " + ps.isEmpty());
	   for(int i = 0; i < args.length; i+= 2) {
		   double x = Double.parseDouble(args[i]);
		   double y = Double.parseDouble(args[i + 1]);
		   
		   Point2D p = new Point2D(x, y);
		   ps.insert(p);
	   }
	   
	   Point2D p = new Point2D(0.8125, 0.3125);
	   
	   System.out.println(ps.nearest(p));		//should be (0.32, 0.708)
	   /*
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
	   
	   
	   System.out.println("Within rect: " + ps.range(rect));
	   
	   Point2D sameLine = new Point2D(0.47, 0);
	   System.out.println("Sameline contains: "  + ps.contains(sameLine));
	   System.out.println("Size before adding Sameline: " + ps.size());
	   ps.insert(sameLine);
	   System.out.println("Size after adding sameline: " + ps.size());*/
   }
}





















class TSet implements Iterable<Point2D>{
	private TreeNode root;
	int size = 0;
	
	public TSet() {
		
	}
	
	public Point2D nearest(Point2D p) {
		return root.nearest(p);
	}

	public Iterable<Point2D> inRange(RectHV rect) {
		if(root == null)
			return new LinkedList<Point2D>();
		
		LinkedList<Point2D> inRange = root.inRange(rect);

				
		
		
		return inRange;
	}

	public boolean add(Point2D p) {
		if(root == null) {
			root = new TreeNode(p, true);
			return true;
		}
		
		return root.add(p);
	}
	
	public boolean contains(Point2D p) {
		if(root == null) {
			return false;
		}
		
		return root.contains(p);
	}
	
	public Point2D first()		{return root.val();}


	public Iterator<Point2D> iterator() {
		return new inOrderIterator();
	}
	
	private class inOrderIterator implements Iterator<Point2D>{
		LinkedList<TreeNode> queue;
		
		public inOrderIterator() {
			queue = new LinkedList<TreeNode>();
			if(root != null)
				queue.add(root);
		}
		
		@Override
		public Point2D next() {
			TreeNode popped = queue.remove(0);
			
			if(popped.left() != null)		queue.add(popped.left());
			if(popped.right() != null)		queue.add(popped.right());
			
			return popped.val();
		}

		@Override
		public boolean hasNext() {
			return queue.size() != 0;
		}
		
	}
}



//TODO add conatins and add method
class TreeNode{
	private final Point2D val;
	private TreeNode left;
	private TreeNode right;
	private final boolean isVertical;			//true means it draws a vertical line, false means horizontal
	
	public TreeNode(Point2D val, boolean isVertical){
		this.val = val;
		this.isVertical = isVertical;
	}
	
	public Point2D nearest(Point2D p) {
		Point2D closest = val;
		double compare = compareTo(p);
		double distance = distance(val, p);
		
		if(compare <= 0) {//point is either below/left of/on the line
			Point2D secondPoint = null;
			double secondDistance = 0;
			
			if(left != null) {		//check the left/below of the line to see if there is a closer point
				secondPoint = left.nearest(p);
				secondDistance = distance(secondPoint, p);
				
				if(distance > secondDistance) {//Point in the left is closer to the new point, so we change closest
					closest = secondPoint;
					distance = secondDistance;
				}
			}
				
			if(distance <= -compare || right ==  null) {//if right is null, there are no points to check. 
				return closest;							//If distance is less than the distance between p and the line, no points on the other side can exist
			}
			
			
			secondPoint = right.nearest(p);		//see if a point on the right is colser
			secondDistance = distance(secondPoint, p);
			if(distance > secondDistance) {//Point in the right is closer to the new point, so we change closest
				closest = secondPoint;
				distance = secondDistance;
				
			}
		}else if(compare >= 0) {	//point is either above/right/on the line
			Point2D secondPoint = null;
			double secondDistance = 0;
			
			if(right != null) {			//check the above/right to see if a closser point exist
				secondPoint = right.nearest(p);
				secondDistance = distance(secondPoint, p);
				
				if(distance > secondDistance) {//Point in the right is closer to the new point, so we change closest
					closest = secondPoint;
					distance = secondDistance;
				}
			}
				
			if(distance <= -compare || left == null) {
				return closest;
			}
			
			
			secondPoint = left.nearest(p);
			secondDistance = distance(secondPoint, p);
			if(distance > secondDistance) {//Point in the left is closer to the new point, so we change closest
				closest = secondPoint;
				distance = secondDistance;
			}
		}
		
		
		return closest;
	}
	
	private double distance(Point2D val, Point2D p) {
		return Math.pow(Math.pow(val.x() - p.x(), 2) + Math.pow(val.y() - p.y(), 2), 0.5);
	}

	public LinkedList<Point2D> inRange(RectHV rect) {
		LinkedList<Point2D> inRange = new LinkedList<Point2D>();
		double x = val.x();
		double y = val.y();
		
		if(x >= rect.xmin() && x <= rect.xmax() && y >= rect.ymin() && y <= rect.ymax()) {
   			inRange.add(val);
   		}
		
		
		if(left != null) {
			if(isVertical) {
				if(rect.xmin() <= val.x()) {			//The left of the rectangle is to the left of the point. We must check left
					addPoints(inRange, left.inRange(rect));
				}
			}else {			//this point drew a horizontal line
				if(rect.ymin() <= val.y()) {			//the bottom of the rect is below the line. We must check below
					addPoints(inRange, left.inRange(rect));
				}
			}
		}
		
		if(right != null) {
			if(isVertical) {
				if(rect.xmax() >= val.x()) {			//The right of the rectangle is to the right of the point. We must check left
					addPoints(inRange, right.inRange(rect));
				}
			}else {			//this point drew a horizontal line
				if(rect.ymax() >= val.y()) {			//the top of the rect is above the line. We must check below
					addPoints(inRange, right.inRange(rect));
				}
			}
		}
		
		return inRange;
	}
	
	private void addPoints(LinkedList<Point2D> main, LinkedList<Point2D> append) {
		for(Point2D p: append) {
			main.add(p);
		}
	}

	public boolean add(Point2D p) {
		if(val.equals(p))
			return false;
		
		double comparision = compareTo(p);
		
		
		
		
		
		if(comparision < 0) {
			if(left == null) {
				left = new TreeNode(p, !isVertical);
				return true;
			}else {
				return left.add(p);
			}
		}else if(comparision >= 0) {
			if(right == null) {			//set value if null
				right = new TreeNode(p, !isVertical);
			}else {						//keep looking if the tree exists
				return right.add(p);
			}
		}
		
		return true;
	}
	
	public boolean contains(Point2D p) {
		if(val.equals(p))
			return true;
		
		double comparision = compareTo(p);
		
		if(comparision < 0) {
			if(left == null) {
				return false;
			}else {
				return left.contains(p);
			}
		}else if(comparision >= 0) {
			if(right == null) {			//return false if null
				return false;
			}else {						//keep looking if the tree exists
				return right.contains(p);
			}
		}
		
		return right.contains(p);
	}

	
	private double compareTo(Point2D p) {
		if(isVertical)
			return p.x() - val.x();
		
		return p.y() - val.y();
	}

	public TreeNode left()		{return left;}
	public TreeNode right()		{return right;}
	public boolean vertical()		{return isVertical;}
	public Point2D val()					{return val;}
	public String toString()		{return val.toString() + ", " + isVertical;}
}


/*

Test 5b: insert non-degenerate points; check nearest() with random query points
  * 5 random non-degenerate points in a 8-by-8 grid
  * 10 random non-degenerate points in a 16-by-16 grid
    - failed on trial 5 of 10000
    - sequence of points inserted: 
      A  0.75 0.875
      B  0.25 0.625
      C  1.0 0.375
      D  0.3125 0.5
      E  0.5 1.0
      F  0.4375 0.0
      G  0.875 0.4375
      H  0.9375 0.8125
      I  0.125 0.75
      J  0.6875 0.9375
    - query point                   = (0.8125, 0.3125)
    - student   nearest()           = (1.0, 0.375)
    - reference nearest()           = (0.875, 0.4375)
    - student   distanceSquaredTo() = 0.0390625
    - reference distanceSquaredTo() = 0.01953125

*/