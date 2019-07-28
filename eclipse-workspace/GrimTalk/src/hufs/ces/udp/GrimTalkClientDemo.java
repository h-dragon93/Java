/**
 * Created on Dec 3, 2015
 * @author cskim -- hufs.ac.kr, Dept of CSE
 * Copy Right -- Free for Educational Purpose
 */
package hufs.ces.udp;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import javax.swing.JColorChooser;
import javax.swing.JOptionPane;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;

import hufs.ces.grimpan.svg.GrimPanModel;
import hufs.ces.grimpan.svg.SVGGrimEllipse;
import hufs.ces.grimpan.svg.SVGGrimLine;
import hufs.ces.grimpan.svg.SVGGrimPath;
import hufs.ces.grimpan.svg.SVGGrimPolygon;
import hufs.ces.grimpan.svg.SVGGrimPolyline;
import hufs.ces.grimpan.svg.SVGGrimShape;
import hufs.ces.grimpan.svg.SVGUtils;
import hufs.ces.grimpan.svg.SaxSVGPathParseHandler;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Shape;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.effect.DropShadow;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class GrimTalkClientDemo extends VBox {
	public Stage parentStage;
	private DrawPane drawPane; 
	private GrimPanModel model;
	private ShapeFactory sf;

	private static final int SCREEN_WIDTH = 400;
	private static final int SCREEN_HEIGHT = 600;
	
	public volatile boolean stopped = false;
	
	Socket theSocket = null;
	BufferedReader userIn = null;
	
	final static int DEFAULT_PORT = 7770;
	//final static String DEFAULT_HOST = "220.67.121.119";
	final static String DEFAULT_HOST = "localhost";
	
	GrimTalkClientDemo thisClass =  this;
	
	private static String hostname = DEFAULT_HOST;
	private static int port = DEFAULT_PORT;
	
	private InetAddress server;
	private Sender sender = null;
	private ReceiverThread receiver = null;
	
    public GrimTalkClientDemo() {
    	
		model = new GrimPanModel();
		sf = ShapeFactory.getInstance(model);
		
    	FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/grimpan.fxml"));
    	fxmlLoader.setRoot(this);
    	fxmlLoader.setController(this);
    	
        try {
        	fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
		drawPane = new DrawPane(model);
		drawPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		drawPane.setOnMousePressed(e->handleMousePressed(e));
		drawPane.setOnMouseReleased(e->handleMouseReleased(e));
		drawPane.setOnMouseDragged(e->handleMouseDragged(e));
        //System.out.println("this.getChildren()="+this.getChildren());
       // System.out.println("contentPane.getChildren()="+contentPane.getChildren());
        contentPaneMiddle.getChildren().add(drawPane);
        
        initDrawPane();
	}

	void initDrawPane() {
		model.shapeList.clear();
		model.curDrawShape = null;
		model.polygonPoints.clear();
		// Add Dummy Shape for Mouse Event occur
		model.shapeList.add(new SVGGrimEllipse(new Ellipse(2000, 2000, 3, 3)));
		drawPane.redraw();
	}
	
    @FXML // fx:id="menuNew"
    private MenuItem menuNew; // Value injected by FXMLLoader

    @FXML // fx:id="menuOpen"
    private MenuItem menuOpen; // Value injected by FXMLLoader

    @FXML // fx:id="menuSave"
    private MenuItem menuSave; // Value injected by FXMLLoader

    @FXML // fx:id="menuSaveAs"
    private MenuItem menuSaveAs; // Value injected by FXMLLoader

    @FXML // fx:id="menuExit"
    private MenuItem menuExit; // Value injected by FXMLLoader
    
    @FXML
    private ToggleGroup toggleGroup1;

    @FXML // fx:id="menuLine"
    private RadioMenuItem menuLine; // Value injected by FXMLLoader

    @FXML // fx:id="menuPencil"
    private RadioMenuItem menuPencil; // Value injected by FXMLLoader

    @FXML // fx:id="menuPolygon"
    private RadioMenuItem menuPolygon; // Value injected by FXMLLoader

    @FXML // fx:id="menuRegular"
    private RadioMenuItem menuRegular; // Value injected by FXMLLoader

    @FXML // fx:id="menuOval"
    private RadioMenuItem menuOval; // Value injected by FXMLLoader
    
    @FXML // fx:id="menuStar"
    private RadioMenuItem menuStar; // Value injected by FXMLLoader
    
    @FXML // fx:id="menuHeart"
    private RadioMenuItem menuHeart; // Value injected by FXMLLoader
    
    @FXML // fx:id="menuThunder"
    private RadioMenuItem menuThunder; // Value injected by FXMLLoader
    
    @FXML // fx:id="menuSpiral"
    private RadioMenuItem menuSpiral; // Value injected by FXMLLoader

    @FXML // fx:id="menuMove"
    private MenuItem menuMove; // Value injected by FXMLLoader

    @FXML // fx:id="menuDelete"
    private MenuItem menuDelete; // Value injected by FXMLLoader

    @FXML // fx:id="menuStrokeWidth"
    private MenuItem menuStrokeWidth; // Value injected by FXMLLoader

    @FXML // fx:id="menuStrokeColor"
    private MenuItem menuStrokeColor; // Value injected by FXMLLoader

    @FXML // fx:id="menuFillColor"
    private MenuItem menuFillColor; // Value injected by FXMLLoader

    @FXML // fx:id="menuCheckFill"
    private CheckMenuItem menuCheckFill; // Value injected by FXMLLoader
 
    @FXML // fx:id="menuCheckStroke"
    private CheckMenuItem menuCheckStroke; // Value injected by FXMLLoader
 
    @FXML // fx:id="menuShadow"
    private MenuItem menuShadow; // Value injected by FXMLLoader    
    
    @FXML // fx:id="menuRotate"
    private MenuItem menuRotate; // Value injected by FXMLLoader    
    
    @FXML // fx:id="menuAbout"
    private MenuItem menuAbout; // Value injected by FXMLLoader
    
    @FXML
    private Pane contentPane;
    
    @FXML
    private Pane contentPaneMiddle;

    @FXML
    private Button btnConnection;

    @FXML
    private Button btnSend;

    @FXML
    private Label lblMessage;

    @FXML
    void handleBtnConnection(ActionEvent event) {
    	try {
			server = InetAddress.getByName(hostname);
			//System.out.println(server);
			sender = new Sender(server, port);
			//System.out.println(sender);
			
			lblMessage.setText(hostname + ":" + port +" connected\n");
			btnConnection.setDisable(true);

			//System.out.println(sender.getSocket());
			receiver = new ReceiverThread(sender.getSocket());
			receiver.start();

		} 
		catch (SocketException ex) {
			System.err.println(ex);
		} catch (IOException ex) {
			System.err.println(ex);
		}

    }

    @FXML
    void handleBtnSend(ActionEvent event) {
    	sendGrimAction();
    }

    @FXML
    void handleMenuAbout(ActionEvent event) {
    	Alert alert = new Alert(AlertType.INFORMATION);
    	alert.setTitle("About");
    	alert.setHeaderText("Application Ver 18/06/25");
    	alert.setContentText("Programmed by 201400492_KIM SUNG JE \nOOP class(18-1)");

    	alert.showAndWait();
    }
    
    @FXML
    void handleMenuShadow(ActionEvent event) {
		model.setEditState(Utils.EFFECT_SHADOW);
		drawPane.redraw();
    }
    
    @FXML
    void handleMenuRotate(ActionEvent event) {
		model.setEditState(Utils.EFFECT_ROTATE);
		drawPane.redraw();
    }

    @FXML
    void handleMenuCheckFill(ActionEvent event) {
		CheckMenuItem checkFill = (CheckMenuItem)event.getSource();
		if (checkFill.isSelected())
			model.setShapeFill(true);
		else
			model.setShapeFill(false);
    }
    
    @FXML
    void handleMenuFillColor(ActionEvent event) {
		//model.setShapeFillColor(fillColorPicker.getValue());
		java.awt.Color awtColor = 
				JColorChooser.showDialog(null, "Choose a color", java.awt.Color.BLACK);
		Color jxColor = Color.BLACK;
		if (awtColor!=null){
			jxColor = new Color(awtColor.getRed()/255.0, awtColor.getGreen()/255.0, awtColor.getBlue()/255.0, 1);
		}
		model.setShapeFillColor(jxColor);
    }
    
    @FXML
    void handleMenuStrokeColor(ActionEvent event) {
		//model.setShapeStrokeColor(strokeColorPicker.getValue());
		//System.out.println("stroke color="+strokeColorPicker.getValue());
		java.awt.Color awtColor = 
				JColorChooser.showDialog(null, "Choose a color", java.awt.Color.BLACK);
		Color jxColor = Color.BLACK;
		if (awtColor!=null){
			jxColor = new Color(awtColor.getRed()/255.0, awtColor.getGreen()/255.0, awtColor.getBlue()/255.0, 1);
		}
		model.setShapeStrokeColor(jxColor);
    }

    @FXML
    void handleMenuStrokeWidth(ActionEvent event) {

		String inputVal = JOptionPane.showInputDialog("Stroke Width", "1");
		if (inputVal!=null){
			model.setShapeStrokeWidth(Float.parseFloat(inputVal));
		}
		else {
			model.setShapeStrokeWidth(1f);
		}
    }
    
    @FXML
	void handleMenuCheckStroke(ActionEvent event) {
		CheckMenuItem checkStroke = (CheckMenuItem)event.getSource();
		if (checkStroke.isSelected())
			model.setShapeStroke(true);
		else
			model.setShapeStroke(false);
	}

    @FXML
    void handleMenuDelete(ActionEvent event) {
		model.setEditState(Utils.EDIT_DELETE);
		drawPane.redraw();
    }

    @FXML
    void handleMenuMove(ActionEvent event) {
		model.setEditState(Utils.EDIT_MOVE);
		if (model.curDrawShape != null){
			model.shapeList.add(model.curDrawShape);
			model.curDrawShape = null;
		}
		drawPane.redraw();
    }

    @FXML
    void handleMenuLine(ActionEvent event) {
		model.setEditState(Utils.SHAPE_LINE);
		drawPane.redraw();
    }
  
    @FXML
    void handleMenuThunder(ActionEvent event) {
		model.setEditState(Utils.SHAPE_THUNDER);
		drawPane.redraw();
    }  
    
    @FXML
    void handleMenuHeart(ActionEvent event) {
		model.setEditState(Utils.SHAPE_HEART);
		drawPane.redraw();
    }    
    
    @FXML
    void handleMenuSpiral(ActionEvent event) {
		model.setEditState(Utils.SHAPE_SPIRAL);
		drawPane.redraw();
    }  
    
    @FXML
    void handleMenuStar(ActionEvent event) {
		model.setEditState(Utils.SHAPE_STAR);
		drawPane.redraw();
    }    
    
    @FXML
    void handleMenuOval(ActionEvent event) {
		model.setEditState(Utils.SHAPE_OVAL);
		drawPane.redraw();
    }

    @FXML
    void handleMenuPencil(ActionEvent event) {
		model.setEditState(Utils.SHAPE_PENCIL);
		drawPane.redraw();
    }

    @FXML
    void handleMenuPolygon(ActionEvent event) {
		model.setEditState(Utils.SHAPE_POLYGON);
		drawPane.redraw();
    }

    @FXML
    void handleMenuRegular(ActionEvent event) {
		model.setEditState(Utils.SHAPE_REGULAR);
		Object[] possibleValues = { 
				"3", "4", "5", "6", "7",
				"8", "9", "10", "11", "12"
		};
		Object selectedValue = JOptionPane.showInputDialog(null,
				"Choose one", "Input",
				JOptionPane.INFORMATION_MESSAGE, null,
				possibleValues, possibleValues[0]);
		model.setNPolygon(Integer.parseInt((String)selectedValue));

		drawPane.redraw();
    }

    @FXML
    void handleMenuNew(ActionEvent event) {
    	initDrawPane();
    }

    @FXML
    void handleMenuOpen(ActionEvent event) {
    	openAction();
    }
   
    @FXML
    void handleMenuSave(ActionEvent event) {
		saveAction();
    }
    
    @FXML
    void handleMenuExit(ActionEvent event) {
		System.exit(0);
    }

    @FXML
    void handleMenusaveAs(ActionEvent event) {
    	saveAsAction();
    }

    @FXML
    void handleMousePressed(MouseEvent event) {
		Point2D p1 = new Point2D(Math.max(0, event.getX()), Math.max(0, event.getY()));
		//Point2D pscr = new Point2D(event.getScreenX(), event.getScreenY());
		//System.out.println("Mouse Pressed at "+p1+ " scr="+pscr);

		if (event.getButton()==MouseButton.PRIMARY){
			model.setStartMousePosition(p1);
			model.setCurrMousePosition(p1);
			model.setPrevMousePosition(p1);				
			switch (model.getEditState()){
			case Utils.SHAPE_LINE:
				model.curDrawShape = new SVGGrimLine((Line)(sf.createMousePointedLine()));
				break;
			case Utils.SHAPE_PENCIL:
				model.curDrawShape = new SVGGrimPath((Path)(sf.createPaintedShape(new Path(new MoveTo(p1.getX(), p1.getY())))));
				break;
			case Utils.SHAPE_POLYGON:
				model.polygonPoints.add(model.getCurrMousePosition());
				if (event.isShiftDown()) {
					//((Path)model.curDrawShape).getElements().add(new ClosePath());
					model.curDrawShape = new SVGGrimPolygon((Polygon)(sf.createPolygonFromClickedPoints()));
					model.polygonPoints.clear();
					model.shapeList.add(model.curDrawShape);
					model.curDrawShape = null;
				}
				else {
					model.curDrawShape = new SVGGrimPolyline((Polyline)(sf.createPolylineFromClickedPoints()));
				}
				break;
			case Utils.SHAPE_REGULAR:
				model.curDrawShape = new SVGGrimPath((Path)(sf.createRegularPolygon(model.getNPolygon())));
				break;
			case Utils.SHAPE_OVAL:
				model.curDrawShape = new SVGGrimEllipse((Ellipse)(sf.createMousePointedEllipse()));
				break;
			case Utils.SHAPE_STAR:
				model.curDrawShape = new SVGGrimPath((Path)(sf.createMousePointedStar()));
				break;
			case Utils.SHAPE_HEART:
				model.curDrawShape = new SVGGrimPath((Path)(sf.createMousePointedHeart()));
				break;
			case Utils.SHAPE_THUNDER:
				model.curDrawShape = new SVGGrimPath((Path)(sf.createMousePointedThunder()));
				break;
			case Utils.SHAPE_SPIRAL:
				model.curDrawShape = new SVGGrimPath((Path)(sf.createMousePointedSpiral()));
				break;
			case Utils.EDIT_MOVE:
				model.getSelectedShape();
				break;
			case Utils.EDIT_DELETE:
				model.getSelectedShape();
				break;
			case Utils.EFFECT_ROTATE:
				model.getSelectedShape();
				break;
			case Utils.EFFECT_SHADOW:
				model.getSelectedShape();
				break;				
			}
		}
		drawPane.redraw();
    }
    
    @FXML
    void handleMouseDragged(MouseEvent event) {
		Point2D p1 = new Point2D(Math.max(0, event.getX()), Math.max(0, event.getY()));

		if (event.getButton()==MouseButton.PRIMARY){
			model.setPrevMousePosition(model.getCurrMousePosition());
			model.setCurrMousePosition(p1);

			switch (model.getEditState()){
			case Utils.SHAPE_LINE:
				model.curDrawShape = new SVGGrimLine((Line)(sf.createMousePointedLine()));
				break;
			case Utils.SHAPE_PENCIL:
				((Path)model.curDrawShape.getShape()).getElements().add(new LineTo(p1.getX(), p1.getY()));
				break;
			case Utils.SHAPE_POLYGON:
				break;
			case Utils.SHAPE_REGULAR:
				model.curDrawShape = new SVGGrimPath((Path)(sf.createRegularPolygon(model.getNPolygon())));
				break;
			case Utils.SHAPE_OVAL:
				model.curDrawShape = new SVGGrimEllipse((Ellipse)(sf.createMousePointedEllipse()));
				break;
			case Utils.SHAPE_STAR:
				model.curDrawShape = new SVGGrimPath((Path)(sf.createMousePointedStar()));
				break;
			case Utils.SHAPE_HEART:
				model.curDrawShape = new SVGGrimPath((Path)(sf.createMousePointedHeart()));
				break;
			case Utils.SHAPE_THUNDER:
				model.curDrawShape = new SVGGrimPath((Path)(sf.createMousePointedThunder()));
				break;
			case Utils.SHAPE_SPIRAL:
				model.curDrawShape = new SVGGrimPath((Path)(sf.createMousePointedSpiral()));
				break;
			case Utils.EDIT_MOVE:
				moveShapeByMouse();
				break;

			}
		}
		drawPane.redraw();
    }

    @FXML
    void handleMouseReleased(MouseEvent event) {
    	Point2D p1 = new Point2D(Math.max(0, event.getX()), Math.max(0, event.getY()));
		//System.out.println("Mouse Released at "+p1);

		if (event.getButton()==MouseButton.PRIMARY){
			model.setPrevMousePosition(model.getCurrMousePosition());
			model.setCurrMousePosition(p1);

			switch (model.getEditState()){
			case Utils.SHAPE_LINE:
				model.curDrawShape = new SVGGrimLine((Line)(sf.createMousePointedLine()));
				if (model.curDrawShape != null){
					model.shapeList.add(model.curDrawShape);
					model.curDrawShape = null;
				}
				break;
			case Utils.SHAPE_PENCIL:
				((Path)model.curDrawShape.getShape()).getElements().add(new LineTo(p1.getX(), p1.getY()));
				if (model.curDrawShape != null){
					model.shapeList.add(model.curDrawShape);
					model.curDrawShape = null;
				}
				break;
			case Utils.SHAPE_POLYGON:
				if (event.isShiftDown()) {
					//((Path)model.curDrawShape).getElements().add(new ClosePath());
					model.curDrawShape = new SVGGrimPolygon((Polygon)(sf.createPolygonFromClickedPoints()));
					model.polygonPoints.clear();
					model.shapeList.add(model.curDrawShape);
					model.curDrawShape = null;
				}
				else {
					model.curDrawShape = new SVGGrimPolyline((Polyline)(sf.createPolylineFromClickedPoints()));
				}
				break;
			case Utils.SHAPE_REGULAR:
				model.curDrawShape = new SVGGrimPath((Path)(sf.createRegularPolygon(model.getNPolygon())));
				if (model.curDrawShape != null){
					model.shapeList.add(model.curDrawShape);
					model.curDrawShape = null;
				}
				break;
			case Utils.SHAPE_OVAL:
				model.curDrawShape = new SVGGrimEllipse((Ellipse)(sf.createMousePointedEllipse()));
				if (model.curDrawShape != null){
					model.shapeList.add(model.curDrawShape);
					model.curDrawShape = null;
				}
				break;
			case Utils.SHAPE_STAR:
				model.curDrawShape = new SVGGrimPath((Path)(sf.createMousePointedStar()));
				if (model.curDrawShape != null){
					model.shapeList.add(model.curDrawShape);
					model.curDrawShape = null;
				}
				break;
			case Utils.SHAPE_HEART:
				model.curDrawShape = new SVGGrimPath((Path)(sf.createMousePointedHeart()));
				if (model.curDrawShape != null){
					model.shapeList.add(model.curDrawShape);
					model.curDrawShape = null;
				}
				break;
			case Utils.SHAPE_THUNDER:
				model.curDrawShape = new SVGGrimPath((Path)(sf.createMousePointedThunder()));
				if (model.curDrawShape != null){
					model.shapeList.add(model.curDrawShape);
					model.curDrawShape = null;
				}
				break;
			case Utils.SHAPE_SPIRAL:
				model.curDrawShape = new SVGGrimPath((Path)(sf.createMousePointedSpiral()));
				if (model.curDrawShape != null){
					model.shapeList.add(model.curDrawShape);
					model.curDrawShape = null;
				}
				break;
			case Utils.EDIT_MOVE:
				if (model.getSelectedShapeIndex()!=-1) {
					endShapeMove();
				}
				break;
			case Utils.EDIT_DELETE:
				if (model.getSelectedShapeIndex()!=-1) {
					deleteShape();
				}
				break;
			case Utils.EFFECT_ROTATE:
				if (model.getSelectedShapeIndex()!=-1) {
					rotateShape();
				}
				break;
			case Utils.EFFECT_SHADOW:
				if (model.getSelectedShapeIndex()!=-1) {
					shadowShape();
				}
				break;
			}
		}
		drawPane.redraw();
    }
 
    @FXML
	private void getSelectedShape(){
		int selIndex = -1;
		Shape shape = null;
		for (int i=model.shapeList.size()-1; i >= 0; --i){
			shape = model.shapeList.get(i).getShape();
			if (shape.contains(model.getStartMousePosition().getX(), model.getStartMousePosition().getY())){
				selIndex = i;
				break;
			}
		}
		if (selIndex != -1){
			Color scolor = (Color)shape.getStroke();
			Color fcolor = (Color)shape.getFill();
			if (shape.getStroke()!=Color.TRANSPARENT){
				shape.setStroke(new Color (scolor.getRed(), scolor.getGreen(), scolor.getBlue(), 0.5));
			}
			if (shape.getFill()!=Color.TRANSPARENT){
				shape.setFill(new Color (fcolor.getRed(), fcolor.getGreen(), fcolor.getBlue(), 0.5));
			}
		}
		model.setSelectedShapeIndex(selIndex);
	}
    
    @FXML
	private void moveShapeByMouse(){
		int selIndex = model.getSelectedShapeIndex();
		Shape shape = null;
		if (selIndex != -1){
			shape = model.shapeList.get(selIndex).getShape();
			double dx = model.getCurrMousePosition().getX() - model.getPrevMousePosition().getX();
			double dy = model.getCurrMousePosition().getY() - model.getPrevMousePosition().getY();

			ShapeFactory.translateShape(shape, dx, dy);
		}
	}
    
    @FXML
	private void endShapeMove(){
		int selIndex = model.getSelectedShapeIndex();
		Shape shape = null;
		if (selIndex != -1){
			shape = model.shapeList.get(selIndex).getShape();
			Color scolor = (Color)shape.getStroke();
			Color fcolor = (Color)shape.getFill();
			if (shape.getStroke()!=Color.TRANSPARENT){
				shape.setStroke(new Color (scolor.getRed(), scolor.getGreen(), scolor.getBlue(), 1));
			}
			if (shape.getFill()!=Color.TRANSPARENT){
				shape.setFill(new Color (fcolor.getRed(), fcolor.getGreen(), fcolor.getBlue(), 1));
			}
			double dx = model.getCurrMousePosition().getX() - model.getPrevMousePosition().getX();
			double dy = model.getCurrMousePosition().getY() - model.getPrevMousePosition().getY();

			ShapeFactory.translateShape(shape, dx, dy);
		}
	}
	
	private void deleteShape(){
		int selIndex = model.getSelectedShapeIndex();
		Shape shape = null;
		if (selIndex != -1){
			shape = model.shapeList.get(selIndex).getShape();
			Color scolor = (Color)shape.getStroke();
			Color fcolor = (Color)shape.getFill();
			if (shape.getStroke()!=Color.TRANSPARENT){
				shape.setStroke(new Color (scolor.getRed(), scolor.getGreen(), scolor.getBlue(), 1));
			}
			if (shape.getFill()!=Color.TRANSPARENT){
				shape.setFill(new Color (fcolor.getRed(), fcolor.getGreen(), fcolor.getBlue(), 1));
			}
			model.shapeList.remove(selIndex);
		}
	}
	
	private void rotateShape(){
		int selIndex = model.getSelectedShapeIndex();
		Shape shape = null;
		if (selIndex != -1){
			shape = model.shapeList.get(selIndex).getShape();
			Color scolor = (Color)shape.getStroke();
			Color fcolor = (Color)shape.getFill();
			if (shape.getStroke()!=Color.TRANSPARENT){
				shape.setStroke(new Color (scolor.getRed(), scolor.getGreen(), scolor.getBlue(), 1));
			}
			if (shape.getFill()!=Color.TRANSPARENT){
				shape.setFill(new Color (fcolor.getRed(), fcolor.getGreen(), fcolor.getBlue(), 1));
			}
			
			shape.setRotate(shape.getRotate() + 30);
		}
	}	
	
	private void shadowShape(){
		int selIndex = model.getSelectedShapeIndex();
		Shape shape = null;
		if (selIndex != -1){
			shape = model.shapeList.get(selIndex).getShape();
			Color scolor = (Color)shape.getStroke();
			Color fcolor = (Color)shape.getFill();
			if (shape.getStroke()!=Color.TRANSPARENT){
				shape.setStroke(new Color (scolor.getRed(), scolor.getGreen(), scolor.getBlue(), 1));
			}
			if (shape.getFill()!=Color.TRANSPARENT){
				shape.setFill(new Color (fcolor.getRed(), fcolor.getGreen(), fcolor.getBlue(), 1));
			}
			DropShadow e = new DropShadow();
		    e.setWidth(10);
		    e.setHeight(10);
		    e.setOffsetX(5);
		    e.setOffsetY(5);
		    e.setRadius(10);
			shape.setEffect(e);
		}
	}		
	
	void saveAction() {
		if (model.getSaveFile()==null){
			model.setSaveFile(new File(Utils.DEFAULT_DIR+"deault_file.svg"));
			this.parentStage.setTitle("GrimPan - "+Utils.DEFAULT_DIR+"default_file.svg");
		}
		File selFile = model.getSaveFile();
		saveGrimPanSVGShapes(selFile);	
	}   
	
	void saveAsAction() {
		FileChooser fileChooser2 = new FileChooser();
		fileChooser2.setTitle("Save As ...");
		fileChooser2.setInitialDirectory(new File(Utils.DEFAULT_DIR));
		fileChooser2.getExtensionFilters().add(new ExtensionFilter("SVG File (*.svg)", "*.svg", "*.SVG"));
		File selFile = fileChooser2.showSaveDialog(this.parentStage);

		model.setSaveFile(selFile);
		this.parentStage.setTitle("GrimPan - "+selFile.getName());

		saveGrimPanSVGShapes(selFile);	
	}
	
	void saveGrimPanSVGShapes(File saveFile){
		PrintWriter svgout = null;
		try {
			svgout = new PrintWriter(saveFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		svgout.println("<?xml version='1.0' encoding='utf-8' standalone='no'?>");
		//svgout.println("<!DOCTYPE svg PUBLIC '-//W3C//DTD SVG 1.0//EN' 'http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd'>");
		svgout.print("<svg xmlns:svg='http://www.w3.org/2000/svg' ");
		svgout.println("     xmlns='http://www.w3.org/2000/svg' ");
		svgout.print("width='"+this.getWidth()+"' ");
		svgout.print("height='"+this.getHeight()+"' ");
		svgout.println("overflow='visible' xml:space='preserve'>");
		for (SVGGrimShape gs:model.shapeList){
			svgout.println("    "+gs.getSVGShapeString());
		}
		svgout.println("</svg>");
		svgout.close();
	}
	
	void openAction() {

		FileChooser fileChooser1 = new FileChooser();
		fileChooser1.setTitle("Please, open the saved GrimPan file!");
		fileChooser1.setInitialDirectory(new File(Utils.DEFAULT_DIR));
		fileChooser1.getExtensionFilters().add(new ExtensionFilter("SVG File (*.svg)", "*.svg", "*.SVG"));
		File selFile = fileChooser1.showOpenDialog(this.parentStage);

		if (selFile == null) return;

		String fileName = selFile.getName();

		model.setSaveFile(selFile);
		readShapeFromSVGSaveFile(selFile);
		this.parentStage.setTitle("GrimPan - "+fileName);
		this.drawPane.redraw();
	}

	void readShapeFromSVGSaveFile(File saveFile) {

		SaxSVGPathParseHandler saxTreeHandler = new SaxSVGPathParseHandler(); 

		try {
			SAXParserFactory saxf = SAXParserFactory.newInstance();
			SAXParser saxParser = saxf.newSAXParser();
			saxParser.parse(new InputSource(new FileInputStream(saveFile)), saxTreeHandler);
		}
		catch(Exception e){
			e.printStackTrace();
		}

		this.initDrawPane();

		ObservableList<SVGGrimShape> gshapeList = saxTreeHandler.getPathList();
		for (SVGGrimShape gsh:gshapeList) {
			model.shapeList.add(gsh);
		}
	}
	
	void sendGrimAction(){
		StringBuilder sb = new StringBuilder();

		//sb.append("<?xml version='1.0' encoding='utf-8' standalone='no'?> \n");
		sb.append("<svg xmlns:svg='http://www.w3.org/2000/svg' ");
		sb.append("     xmlns='http://www.w3.org/2000/svg' \n");
		sb.append("width='"+SCREEN_WIDTH+"' ");
		sb.append("height='"+SCREEN_HEIGHT+"' ");
		sb.append("overflow='visible' xml:space='preserve'> \n");
		
		for (SVGGrimShape gs:model.shapeList){
			sb.append(SVGUtils.getSVGElementFromSVGPath(SVGUtils.convertShapeToSVGPath(gs)));
		}
		sb.append("</svg>\n");

		String theLines = sb.toString();
		//System.out.println(theLines);

		sender.send(theLines);
	}
	
	class ReceiverThread extends Thread {
		
		DatagramSocket socket;

		public ReceiverThread(DatagramSocket ds) throws SocketException {
			this.socket = ds;
			socket.setSoTimeout(10000); // 10 sec.
		}

		public void halt() {
			stopped = true; 
		}

		public void run() {

			byte[] buffer = new byte[65507];
			
			//System.out.println(buffer.length);
			while (true) {
				if (stopped) return;
				DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
				try {
					socket.receive(dp);
					String theLines = new String(dp.getData(), 0, dp.getLength());
					//System.out.println(theLines);

					if (theLines==null || theLines.equals(".") || theLines.equals("END")) {
						stopped = true;
						lblMessage.setText("partner disconnected.");
						break;
					}
					//System.out.println("received svg="+theLines);
					InputStream grimStream = new ByteArrayInputStream(theLines.getBytes());
					SaxSVGPathParseHandler saxTreeHandler = new SaxSVGPathParseHandler(); 

					try {
						SAXParserFactory saxf = SAXParserFactory.newInstance();
						SAXParser saxParser = saxf.newSAXParser();
						saxParser.parse(new InputSource(grimStream), saxTreeHandler);
					}

					catch(Exception e){			
						System.out.println("error");
						e.printStackTrace();
						JOptionPane.showInputDialog(thisClass, e);

					}
					//System.out.println("map siz="+model.attsMapList.size());
					ObservableList<SVGGrimShape> gshapeList = saxTreeHandler.getPathList();
					
					//for (SVGGrimShape gsh:gshapeList) {

						if (gshapeList != null && gshapeList.size()!=0){
							synchronized(model.shapeList){
								model.shapeList.addAll(gshapeList);
							}
							drawPane.redraw();
						}
					//}
					
					drawPane.redraw();
					
					Thread.yield();
				
				}
				catch (SocketTimeoutException ex) {
					//System.err.println(ex);
				} 
				catch (IOException ex) {
					System.err.println(ex);
				} 

			}

		}
	}
}
