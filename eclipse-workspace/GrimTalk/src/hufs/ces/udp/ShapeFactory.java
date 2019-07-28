package hufs.ces.udp;


import java.util.ArrayList;

import hufs.ces.grimpan.svg.GrimPanModel;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.HLineTo;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.shape.Shape;
import javafx.scene.shape.VLineTo;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class ShapeFactory {
	
	private volatile static ShapeFactory uniqueSFInstance;

	GrimPanModel model = null;

	public ShapeFactory(GrimPanModel model) {
		this.model = model;
	}

	public static ShapeFactory getInstance(GrimPanModel model) {
		if (uniqueSFInstance == null) {
			synchronized (GrimPanModel.class) {
				if (uniqueSFInstance == null) {
					uniqueSFInstance = new ShapeFactory(model);
				}
			}
		}
		return uniqueSFInstance;
	}
	
	Shape createPaintedShape(Shape shape) {

		if (model.isShapeFill()){
			shape.setFill(model.getShapeFillColor());
		}
		else {
			shape.setFill(Color.TRANSPARENT);
		}

		if (model.isShapeStroke()){
			shape.setStrokeWidth(model.getShapeStrokeWidth());
			shape.setStroke(model.getShapeStrokeColor());
		}
		else {
			shape.setStroke(Color.TRANSPARENT);
		}
		return shape;
	}
	
	Ellipse createPaintedEllipse() {
		Ellipse shape = new Ellipse();

		if (model.isShapeFill()){
			shape.setFill(model.getShapeFillColor());
		}
		else {
			shape.setFill(Color.TRANSPARENT);
		}
		if (model.isShapeStroke()){
			shape.setStrokeWidth(model.getShapeStrokeWidth());
			shape.setStroke(model.getShapeStrokeColor());
		}
		else {
			shape.setStroke(Color.TRANSPARENT);
		}
		return shape;
	}
	
	Line createPaintedLine() {
		Line shape = new Line();

		shape.setStrokeWidth(model.getShapeStrokeWidth());
		shape.setStroke(model.getShapeStrokeColor());
		return shape;
	}
	
	Path createPaintedPath() {
		Path shape = new Path();

		if (model.isShapeFill()){
			shape.setFill(model.getShapeFillColor());
		}
		else {
			shape.setFill(Color.TRANSPARENT);
		}

		if (model.isShapeStroke()){
			shape.setStrokeWidth(model.getShapeStrokeWidth());
			shape.setStroke(model.getShapeStrokeColor());
		}
		else {
			shape.setStroke(Color.TRANSPARENT);
		}
		return shape;
	}
	
	public Shape createMousePointedLine() {
		Point2D pstart = model.getStartMousePosition();
		Point2D pend = model.getCurrMousePosition();
		return createPaintedShape(new Line(pstart.getX(), pstart.getY(), pend.getX(), pend.getY()));

	}

	public Shape createPolygonFromClickedPoints(){
		ArrayList<Point2D> points = model.polygonPoints;
		Polygon result = new Polygon();
		if (points != null && points.size() > 2) {
			for (int i=0; i<points.size(); ++i){
				result.getPoints().add(points.get(i).getX());
				result.getPoints().add(points.get(i).getY());
			}
		}
		return createPaintedShape(result);
	}
	
	public Shape createPolylineFromClickedPoints(){
		ArrayList<Point2D> points = model.polygonPoints;
		Polyline result = new Polyline();
		if (points != null && points.size() > 0) {
			for (int i=0; i<points.size(); ++i){
				result.getPoints().add(points.get(i).getX());
				result.getPoints().add(points.get(i).getY());
			}
		}
		return createPaintedShape(result);
	}
	
	public Shape createMousePointedEllipse(){

		Point2D topleft = model.getStartMousePosition();
		Point2D pcurr = model.getCurrMousePosition();

		if (pcurr.distance(topleft) <= Utils.MINPOLYDIST)
			return null;
		double radiusX = (pcurr.getX() - topleft.getX()) / 2;
		double radiusY = (pcurr.getY() - topleft.getY()) / 2;
		double centerX = topleft.getX() + radiusX;
		double centerY = topleft.getY() + radiusY;
		return createPaintedShape(new Ellipse(centerX, centerY, radiusX, radiusY));
	}
	
	public Shape createMousePointedStar(){
		Point2D topleft = model.getStartMousePosition();
		Point2D pcurr = model.getCurrMousePosition();
		if (pcurr.distance(topleft) <= Utils.MINPOLYDIST)
			return null;

		double innerRadius = (pcurr.getX()-topleft.getX()) / 2;
		double outerRadius = (pcurr.getX()-topleft.getX()) * 1.03;
		double centerX = topleft.getX() + (pcurr.getX()-topleft.getX()) / 2;
		double centerY = topleft.getY() + (pcurr.getY()-topleft.getY()) / 2;
		int numRays = 5;
		double startAngleRad = Math.toRadians(-18);
		
		return createPaintedShape(createStar(centerX, centerY, innerRadius, outerRadius, numRays, startAngleRad));
	}
	
	public Shape createStar(double centerX, double centerY, double innerRadius,
							double outerRadius, int numRays, double startAngleRad) {
        Path path = new Path();
        double deltaAngleRad = Math.PI / numRays;
        
        for (int i = 0; i < numRays * 2; i++) {
            double angleRad = startAngleRad + i * deltaAngleRad;
            double relX = Math.cos(angleRad);
            double relY = Math.sin(angleRad);
            
            if ((i & 1) == 0) { 
                relX *= outerRadius;
                relY *= outerRadius;
            }
            else {
                relX *= innerRadius;
                relY *= innerRadius;
            }
            
            if (i == 0) {
            	MoveTo moveTo = new MoveTo();
                moveTo.setX(centerX + relX);
                moveTo.setY(centerY + relY);
                path.getElements().add(moveTo);
            } else {
            	LineTo lineTo = new LineTo();
                lineTo.setX(centerX + relX);
                lineTo.setY(centerY + relY);
                path.getElements().add(lineTo);
            }
	    }
		path.getElements().add(new ClosePath());
		return path;	
	}
	
	public Shape createMousePointedHeart(){
		Point2D topleft = model.getStartMousePosition();
		Point2D pcurr = model.getCurrMousePosition();

		if (pcurr.distance(topleft) <= Utils.MINPOLYDIST)
			return null;

		double width = (pcurr.getX()-topleft.getX());
		double height = (pcurr.getY()-topleft.getY());
		
		return createPaintedShape(createHeart(width, height));
	}

	public Shape createHeart(double width, double height) {
		Point2D topleft = model.getStartMousePosition();
		Point2D pcurr = model.getCurrMousePosition();
		
		Path heart = new Path();
		
		MoveTo start = new MoveTo();
		start.setX(topleft.getX() + width / 2);
		start.setY(topleft.getY() + height / 5);
		
    	CubicCurveTo upperLeft = new CubicCurveTo();
    	upperLeft.setX(pcurr.getX() - 27 * width / 28);
    	upperLeft.setY(topleft.getY() + 2 * height / 5);	
    	upperLeft.setControlX2(pcurr.getX() - width); 
    	upperLeft.setControlY2(topleft.getY() + height / 15);
    	upperLeft.setControlX1(pcurr.getX() - 9 * width / 14); 
    	upperLeft.setControlY1(topleft.getY() - height / 5);
        
    	CubicCurveTo lowerLeft = new CubicCurveTo();
    	lowerLeft.setX(topleft.getX() + width / 2);
    	lowerLeft.setY(topleft.getY() + height);	
    	lowerLeft.setControlX2(pcurr.getX() - 4 * width / 7); 
    	lowerLeft.setControlY2(topleft.getY() + 5 * height / 6);
    	lowerLeft.setControlX1(pcurr.getX() - 13 * width / 14); 
    	lowerLeft.setControlY1(topleft.getY() + 2 * height / 3);
        
    	CubicCurveTo lowerRight = new CubicCurveTo();
    	lowerRight.setX(topleft.getX() + 27 * width / 28);
    	lowerRight.setY(topleft.getY() + 2 * height / 5);	
    	lowerRight.setControlX1(topleft.getX() + 4 * width / 7); 
    	lowerRight.setControlY1(topleft.getY() + 5 * height / 6);
    	lowerRight.setControlX2(topleft.getX() + 13 * width / 14); 
    	lowerRight.setControlY2(topleft.getY() + 2 * height / 3);
        
    	CubicCurveTo upperRight = new CubicCurveTo();
    	upperRight.setX(topleft.getX() + width / 2);
    	upperRight.setY(topleft.getY() + height / 5);	
    	upperRight.setControlX1(topleft.getX() + width); 
    	upperRight.setControlY1(topleft.getY() + height / 15);
    	upperRight.setControlX2(topleft.getX() + 9 * width / 14); 
    	upperRight.setControlY2(topleft.getY() - height / 5);

        heart.getElements().addAll(start, upperLeft, lowerLeft, lowerRight, upperRight);

		heart.getElements().add(new ClosePath());
		return heart;	
	}
	
	public Shape createMousePointedThunder(){
		Point2D topleft = model.getStartMousePosition();
		Point2D pcurr = model.getCurrMousePosition();

		if (pcurr.distance(topleft) <= Utils.MINPOLYDIST)
			return null;

		double width = (pcurr.getX()-topleft.getX());
		double height = (pcurr.getY()-topleft.getY());
		
		return createPaintedShape(createThunder(width, height));
	}
	
	public Shape createThunder(double width, double height) {
		Point2D topleft = model.getStartMousePosition();
		Point2D pcurr = model.getCurrMousePosition();
		
		Path Thunder = new Path();
		
		MoveTo start = new MoveTo();
		start.setX(topleft.getX() + width / 2);
		start.setY(topleft.getY());
		
		LineTo upperLeft = new LineTo();
		upperLeft.setX(topleft.getX());
		upperLeft.setY(topleft.getY() + 10 * height / 16);
		
		LineTo upperLeft2 = new LineTo();
		upperLeft2.setX(topleft.getX() + width / 2);
		upperLeft2.setY(topleft.getY() + 10 * height / 16);
		
		LineTo lowerLeft = new LineTo();
		lowerLeft.setX(topleft.getX() + 6 * width / 16);
		lowerLeft.setY(topleft.getY() + height);
		
		LineTo lowerRight = new LineTo();
		lowerRight.setX(pcurr.getX());
		lowerRight.setY(topleft.getY() + 6 * height / 16);
		
		LineTo upperRight = new LineTo();
		upperRight.setX(topleft.getX() + width / 2);
		upperRight.setY(topleft.getY() + 6 * height / 16);
		
		LineTo upperRight2 = new LineTo();
		upperRight2.setX(topleft.getX() +  width / 2);
		upperRight2.setY(topleft.getY());

        Thunder.getElements().addAll(start, upperLeft, upperLeft2, lowerLeft, lowerRight, upperRight, upperRight2);

		Thunder.getElements().add(new ClosePath());
		return Thunder;	
	}
	
	public Shape createMousePointedSpiral(){
		Point2D topleft = model.getStartMousePosition();
		Point2D pcurr = model.getCurrMousePosition();
		int steps;
		if (pcurr.distance(topleft) <= Utils.MINPOLYDIST)
			return null;

		double alpha = (pcurr.getX()-topleft.getX()) / 50;
		double centerX = topleft.getX() + (pcurr.getX()-topleft.getX()) / 2;
		double centerY = topleft.getY() + (pcurr.getY()-topleft.getY()) / 2;
		if(pcurr.getX() > topleft.getX()) {
			steps = (int)((pcurr.getX()-topleft.getX()) * 50);
		} else {
			steps = (int)((pcurr.getX()-topleft.getX()) * 50) * -1;	
		}
		double totalAngleRad = Math.PI * 10;
		
		return createPaintedShape(createSpiral(centerX, centerY, totalAngleRad, alpha, steps));
	}
	
	public Shape createSpiral(double centerX, double centerY, double totalAngleRad,
			double alpha, int steps) {
		
		Path path = new Path();
		double deltaAngleRad = totalAngleRad / steps;
		
		for (int i = 0; i <= steps ; i += 2) {
		double angleRad0 = i * deltaAngleRad;
		double angleRad1 = i * deltaAngleRad + deltaAngleRad;
		
		double x0 = centerX + Math.sin(angleRad0) * alpha * angleRad0;
		double y0 = centerY + Math.cos(angleRad0) * alpha * angleRad0;
		double x1 = centerX + Math.sin(angleRad1) * alpha * angleRad1;
		double y1 = centerY + Math.cos(angleRad1) * alpha * angleRad1;

		if (i == 0) {
			MoveTo moveTo = new MoveTo();
			moveTo.setX(x0);
			moveTo.setY(y0);
			path.getElements().add(moveTo);
		} else {
			LineTo lineTo = new LineTo();
			lineTo.setX(x1);
			lineTo.setY(y1);
			path.getElements().add(lineTo);
		}
		}
		
		return path;	
	}	
	
	
	public Shape createRegularPolygon(int nvertex){
		Point2D center = model.getStartMousePosition();
		Point2D pi = model.getCurrMousePosition();
		if (pi.distance(center) <= Utils.MINPOLYDIST)
			return new Path();

		double nangle = 360.0/nvertex; // 360/n degree
		Rotate rot = new Rotate(nangle);

		Point2D[] polyPoints = new Point2D[nvertex];
		polyPoints[0] = rot.transform(pi.getX()-center.getX(), pi.getY()-center.getY()); 
		for (int i=1; i<polyPoints.length; ++i){
			polyPoints[i] = rot.transform(polyPoints[i-1]);
		}

		Translate tra = new Translate(center.getX(), center.getY());
		//polyPoints[0] = new Point2D(pi.getX(), pi.getY()); 
		for (int i=0; i<polyPoints.length; ++i){
			polyPoints[i] = tra.transform(polyPoints[i]);
		}
		
		Path polygonPath = new Path();
		polygonPath.getElements().add(new MoveTo(polyPoints[0].getX(), polyPoints[0].getY()));
		for (int i=1; i<polyPoints.length; ++i){
			polygonPath.getElements().add(new LineTo(polyPoints[i].getX(), polyPoints[i].getY()));
		}
		//polygonPath.getElements().add(new LineTo(polyPoints[0].getX(), polyPoints[0].getY()));
		polygonPath.getElements().add(new ClosePath());

		return createPaintedShape(polygonPath);

	}

	static public void translateShape(Shape shape, double dx, double dy) {

		if (shape instanceof Line) {
			Line line = (Line) shape;
			line.setStartX(line.getStartX()+dx);
			line.setStartY(line.getStartY()+dy);
			line.setEndX(line.getEndX()+dx);
			line.setEndY(line.getEndY()+dy);
		}
		else if (shape instanceof Ellipse) {
			Ellipse ellipse = (Ellipse) shape;
			ellipse.setCenterX(ellipse.getCenterX()+dx);
			ellipse.setCenterY(ellipse.getCenterY()+dy);
		}
		else if (shape instanceof Path) {
			Path path =(Path) shape;
			for (PathElement el:path.getElements()) {
				if (el instanceof MoveTo) {
					MoveTo pel = (MoveTo)el;
					pel.setX(pel.getX() + dx);
					pel.setY(pel.getY() + dy);
				}
				else if (el instanceof LineTo) {
					LineTo pel = (LineTo)el;
					pel.setX(pel.getX() + dx);
					pel.setY(pel.getY() + dy);
				}
				else if (el instanceof ArcTo) {
					ArcTo pel = (ArcTo)el;
					pel.setX(pel.getX() + dx);
					pel.setY(pel.getY() + dy);
				}
				else if (el instanceof HLineTo) {
					HLineTo pel = (HLineTo)el;
					pel.setX(pel.getX() + dx);
				}
				else if (el instanceof VLineTo) {
					VLineTo pel = (VLineTo)el;
					pel.setY(pel.getY() + dy);
				}
				else if (el instanceof CubicCurveTo) {
					CubicCurveTo pel = (CubicCurveTo)el;
					pel.setX(pel.getX() + dx);
					pel.setY(pel.getY() + dy);
					pel.setControlX1(pel.getControlX1() + dx);
					pel.setControlY1(pel.getControlY1() + dy);
					pel.setControlX2(pel.getControlX2() + dx);
					pel.setControlY2(pel.getControlY2() + dy);
				}
				else if (el instanceof QuadCurveTo) {
					QuadCurveTo pel = (QuadCurveTo)el;
					pel.setX(pel.getX() + dx);
					pel.setY(pel.getY() + dy);
					pel.setControlX(pel.getControlX() + dx);
					pel.setControlY(pel.getControlY() + dy);
				}
			}
		}
		else if (shape instanceof Polygon) {
			Polygon pol =(Polygon) shape;
			ObservableList<Double> points = pol.getPoints();
			for (int i=0; i<points.size(); i+=2) {
				points.set(i, points.get(i)+dx);
				points.set(i+1, points.get(i+1)+dy);
			}
		}
		else if (shape instanceof Polyline) {
			Polyline pol =(Polyline) shape;
			ObservableList<Double> points = pol.getPoints();
			for (int i=0; i<points.size(); i+=2) {
				points.set(i, points.get(i)+dx);
				points.set(i+1, points.get(i+1)+dy);
			}
		}
	}
	
	
}
