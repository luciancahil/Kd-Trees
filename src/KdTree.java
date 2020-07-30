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
		   
		   Point2D p = new Point2D(x,y);
		   ps.insert(p);
	   }
	   
	   Point2D p = new Point2D(0.4375, 0.0);
	   
	   System.out.println(ps.nearest(p));
   }
}





















class TSet implements Iterable<Point2D>{
	private TreeNode root;
	int size = 0;
	
	public Point2D nearest(Point2D p) {
		return root.nearest(p, Double.POSITIVE_INFINITY);
	}

	public Iterable<Point2D> inRange(RectHV rect) {
		if(root == null)
			return new LinkedList<Point2D>();
		
		LinkedList<Point2D> inRange = root.inRange(rect);

				
		
		
		return inRange;
	}

	public boolean add(Point2D p) {
		if(root == null) {
			root = new TreeNode(p, true, 0, 1, 0, 1);
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
	private final double xL, xR, yL, yU;			//stores the limits of the box it is contained in. xL is left, xR is right, yL is lower, yR is upper
	private final double x, y;				//stores the coordinates of the point contained
	
	
	public TreeNode(Point2D val, boolean isVertical, double xL2, double d, double yL2, double yU2){
		this.val = val;
		x = val.x();
		y = val.y();
		this.isVertical = isVertical;
		this.xL = xL2;
		this.xR = d;
		this.yL = yL2;
		this.yU = yU2;
	}
	
	public Point2D nearest(Point2D p, double closestDistance) {
		Point2D closest = val;					//stores the closest point
		double compare = compareTo(p);			//stores the distance from p to the axis this point drew
		double distance = distance(val, p);		//shortest distance we've seen so far
		
		if(distance > closestDistance) 
			distance = closestDistance;
		
		if(compare <= 0) {//point is either below/left of/on the line
			Point2D secondPoint = null;
			double secondDistance = 0;
			
			if(left != null) {		//check the left/below of the line to see if there is a closer point
				secondPoint = left.nearest(p, distance);
				secondDistance = distance(secondPoint, p);
				
				if(distance > secondDistance) {//Point in the left is closer to the new point, so we change closest
					closest = secondPoint;
					distance = secondDistance;
				}
			}
				
			if(right ==  null) {//if right is null, there are no points to check. 
				return closest;							//If distance is less than the distance between p and the line, no points on the other side can exist
			}
			
			
			//check if the line has to be diagonal
			//if Yes, recalculate compare
			if(isVertical) {
				if(p.y() < right.yL()) {			//point is below of right's rectangle
					compare = -Math.sqrt(Math.pow((p.y() - right.yL()), 2) + Math.pow((p.x() - right.xL()), 2));		//compute the distance between p and the bottom left corner of right's rectangle
				
				}else if(p.y() > right.yU()) {		//point is above right's rectangle
					compare = -Math.sqrt(Math.pow((p.y() - right.yU()), 2) + Math.pow((p.x() - right.xL()), 2));		//compute the distance between p and the top left corner of right's rectangle
				
				}//else: point is on the same y as the rectangle
			}else {
				if(p.x() < right.xL()) {			//point is left of the rectangel
					compare = -Math.sqrt(Math.pow((p.y() - right.yL()), 2) + Math.pow((p.x() - right.xL()), 2));		//compute the distance between p and the bottom left corner of right's rectangle
				
				}else if(p.x() > right.xR()) {		//point is right of the rectangle
					compare = -Math.sqrt(Math.pow((p.y() - right.yL()), 2) + Math.pow((p.x() - right.xR()), 2));		//compute the distance between p and the top left corner of right's rectangle
				
				}//else: point is on the same y as the rectangle
			}
			
			if(distance <= -compare)
				return closest;
			
			secondPoint = right.nearest(p, distance);		//see if a point on the right is colser
			secondDistance = distance(secondPoint, p);
			if(distance > secondDistance) {//Point in the right is closer to the new point, so we change closest
				closest = secondPoint;
				distance = secondDistance;
				
			}
		}else if(compare >= 0) {	//point is either above/right/on the line
			Point2D secondPoint = null;
			double secondDistance = 0;
			
			if(right != null) {			//check the above/right to see if a closser point exist
				secondPoint = right.nearest(p, distance);
				secondDistance = distance(secondPoint, p);
				
				if(distance > secondDistance) {//Point in the right is closer to the new point, so we change closest
					closest = secondPoint;
					distance = secondDistance;
				}
			}
				
			if(left == null) {
				return closest;
			}
			
			
			//check if the line has to be diagonal
			//if Yes, recalculate compare
			if(isVertical) {
				if(p.y() < left.yL()) {			//point is below left's rectangle
					compare = Math.sqrt(Math.pow((p.y() - left.yL()), 2) + Math.pow((p.x() - left.xR()), 2));		//compute the distance between p and the bottom left corner of left's rectangle
				
				}else if(p.y() > left.yU()) {		//point is above left's rectangle
					compare = Math.sqrt(Math.pow((p.y() - left.yU()), 2) + Math.pow((p.x() - left.xR()), 2));		//compute the distance between p and the top left corner of left's rectangle
				
				}//else: point is on the same y as the rectangle
			}else {
				if(p.x() < left.xL()) {			//point is left of the rectangel
					compare = Math.sqrt(Math.pow((p.y() - left.yU()), 2) + Math.pow((p.x() - left.xL()), 2));		//compute the distance between p and the bottom left corner of left's rectangle
				
				}else if(p.x() > left.xR()) {		//point is left of the rectangle
					compare = Math.sqrt(Math.pow((p.y() - left.yU()), 2) + Math.pow((p.x() - left.xR()), 2));		//compute the distance between p and the top left corner of left's rectangle
				
				}//else: point is on the same y as the rectangle
			}
			
			if(distance <= compare)
				return closest;
			
			secondPoint = left.nearest(p, distance);
			secondDistance = distance(secondPoint, p);
			if(distance > secondDistance) {//Point in the left is closer to the new point, so we change closest
				closest = secondPoint;
				distance = secondDistance;
			}
		}
		
		
		return closest;
	}
	
	private double distance(Point2D p1, Point2D p2) {
		return Math.pow(Math.pow(p1.x() - p2.x(), 2) + Math.pow(p1.y() - p2.y(), 2), 0.5);
	}

	public LinkedList<Point2D> inRange(RectHV rect) {
		LinkedList<Point2D> inRange = new LinkedList<Point2D>();

		
		if(x >= rect.xmin() && x <= rect.xmax() && y >= rect.ymin() && y <= rect.ymax()) {
   			inRange.add(val);
   		}
		
		
		if(left != null) {
			if(isVertical) {
				if(rect.xmin() <= x) {			//The left of the rectangle is to the left of the point. We must check left
					addPoints(inRange, left.inRange(rect));
				}
			}else {			//this point drew a horizontal line
				if(rect.ymin() <= y) {			//the bottom of the rect is below the line. We must check below
					addPoints(inRange, left.inRange(rect));
				}
			}
		}
		
		if(right != null) {
			if(isVertical) {
				if(rect.xmax() >= x) {			//The right of the rectangle is to the right of the point. We must check left
					addPoints(inRange, right.inRange(rect));
				}
			}else {			//this point drew a horizontal line
				if(rect.ymax() >= y) {			//the top of the rect is above the line. We must check below
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
				if(isVertical) {
					left = new TreeNode(p, !isVertical, xL, x, yL, yU);
				}else {
					left = new TreeNode(p, !isVertical, xL, xR, yL, y);
				}
				
				return true;
			}else {
				return left.add(p);
			}
		}else if(comparision >= 0) {
			if(right == null) {			//set value if null
				if(isVertical) {
					right = new TreeNode(p, !isVertical, x, xR, yL, yU);
				}else {
					right = new TreeNode(p, !isVertical, xL, xR, y, yU);
				}
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
			return p.x() - x;
		
		return p.y() - y;
	}

	public TreeNode left()			{return left;}
	public TreeNode right()			{return right;}
	public boolean vertical()		{return isVertical;}
	public Point2D val()			{return val;}
	public String toString()		{return val.toString() + ", " + isVertical;}
	public double xL()				{return xL;}
	public double xR()				{return xR;}
	public double yL()				{return yL;}
	public double yU()				{return yU;}
	public double y()				{return y;}
	public double x()				{return y;}
}


/*
Test 6b: insert non-degenerate points; check nearest() with random query points
         and check traversal of kd-tree
  * 5 random non-degenerate points in a 8-by-8 grid
  * 10 random non-degenerate points in a 16-by-16 grid
    - student   nearest() = (0.5625, 0.1875)
    - reference nearest() = (0.5625, 0.1875)
    - performs incorrect traversal of kd-tree during call to nearest()
    - query point = (0.4375, 0.0)
    - sequence of points inserted: 
      A  0.1875 0.125
      B  0.125 0.25
      C  0.5625 0.1875
      D  0.0 0.0625
      E  0.625 0.5625
      F  0.3125 0.75
      G  0.375 0.8125
      H  0.8125 0.5
      I  0.9375 0.9375
      J  0.5 0.3125
    - student sequence of kd-tree nodes involved in calls to Point2D methods:
      A C E F J H 
    - reference sequence of kd-tree nodes involved in calls to Point2D methods:
      A C E F J 
    - failed on trial 5 of 1000
*/