package cz.pavelzelenka.triomino;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Rozvrzeni okna aplikace
 * @author Pavel Zelenka A16B0176P
 * @version 2018-03-09
 */
public class WindowLayout {

	/** Hlavni stage aplikace */
	private final Stage stage;
	
	/** Zakladni rozvrzeni okna */
	private BorderPane borderPane;

	/** Kresba */
	private Drawing drawing;
	
	/**
	 * Konstruktor
	 * @param stage (hlavni) stage aplikace
	 */
	public WindowLayout(Stage stage) {
		this.stage = stage;
	}
	
	/**
	 * Vrati zakladni rozvrzeni okna aplikace
	 * @return zakladni rozvrzeni okna aplikace
	 */
	public Parent get() {
		borderPane = new BorderPane();
		borderPane.setCenter(getCanvasPane());
		borderPane.setBottom(getBottomPane());
		return borderPane;
	}
	
	/**
	 * Vrati spodni panel
	 * @return spodni panel
	 */
	public Parent getBottomPane() {
		String multicolorStr = "Multicolor";		
		HBox hBox = new HBox();
		hBox.setPadding(new Insets(5D, 5D, 5D, 5D));
		hBox.setMinHeight(40D);
		hBox.setAlignment(Pos.CENTER);
		hBox.setSpacing(10D);
		//Text randomColor = new Text(randomColorStr);
		
		// vice barev na jeden polynom
		CheckBox multiColor = new CheckBox(multicolorStr);
		multiColor.setSelected(drawing.multiColourPolynom().get());
		
		// volba barev
		HBox colorBox = new HBox();
		ColorPicker aColorPicker = new ColorPicker();
		aColorPicker.setValue(drawing.getaColor());
		aColorPicker.setPrefWidth(50D);
		ColorPicker bColorPicker = new ColorPicker();
		bColorPicker.setValue(drawing.getbColor());
		bColorPicker.setPrefWidth(50D);
		ColorPicker cColorPicker = new ColorPicker();
		cColorPicker.setValue(drawing.getcColor());
		cColorPicker.setPrefWidth(50D);
		ColorPicker dColorPicker = new ColorPicker();
		dColorPicker.setValue(drawing.getdColor());
		dColorPicker.setPrefWidth(50D);
		colorBox.setSpacing(5D);
		colorBox.getChildren().addAll(aColorPicker, bColorPicker, cColorPicker, dColorPicker);
		
		// mezera
		Pane pane = new Pane();
		HBox.setHgrow(pane, Priority.ALWAYS);
		
		// tlacitka vetsi/mensi a text aktualni velikosti
		Button larger = new Button("+");
		StackPane sizePane = new StackPane();
		Text sizeText = new Text(String.format("%.0f", drawing.sizeProperty().get()));
		sizePane.getChildren().add(sizeText);
		sizePane.setMinWidth(30D);
		larger.setMinWidth(30D);
		Button smaller = new Button("-");
		smaller.setMinWidth(30D);
		
		// pridani vseho do panelu
		hBox.getChildren().addAll(multiColor, colorBox, pane, smaller, sizePane, larger);
		
		// akce tlacitek pro zmenu velikosti
		smaller.setOnAction(action -> {
			if(drawing != null) {
				drawing.smaller();
			}
		});
		
		larger.setOnAction(action -> {
			if(drawing != null) {
				drawing.larger();
			}
		});
		
		// zmena barevnosti
		multiColor.selectedProperty().addListener((observable, oldValue, newValue) -> {
			if(newValue != null) {
				drawing.setMultiColourPolynom(newValue);
			}
		});
		
		drawing.multiColourPolynom().addListener((observable, oldValue, newValue) -> {
			if(newValue != null) {
				multiColor.setSelected(newValue);
			}
		});
		
		aColorPicker.valueProperty().addListener((observable, oldValue, newValue) -> {
			if(newValue != null) {
				drawing.setaColor(newValue);
			}
		});
		
		bColorPicker.valueProperty().addListener((observable, oldValue, newValue) -> {
			if(newValue != null) {
				drawing.setbColor(newValue);
			}
		});
		
		cColorPicker.valueProperty().addListener((observable, oldValue, newValue) -> {
			if(newValue != null) {
				drawing.setcColor(newValue);
			}
		});
		
		dColorPicker.valueProperty().addListener((observable, oldValue, newValue) -> {
			if(newValue != null) {
				drawing.setdColor(newValue);
			}
		});
		
		// zmena velikosti
		drawing.sizeProperty().addListener((observable, oldValue, newValue) -> {
			if(newValue != null) {
				sizeText.setText(String.valueOf(newValue.intValue()));
			} else {
				sizeText.setText("none");
			}
		});
		
		return hBox;
	}
	
	/**
	 * Vrati panel kresby
	 * @return panel kresby
	 */
	public Parent getCanvasPane() {
		BorderPane pane = new BorderPane();
		
        Canvas canvas = new Canvas(pane.getWidth(), pane.getHeight());
        
        pane.widthProperty().addListener(event -> canvas.setWidth(pane.getWidth()));
        pane.heightProperty().addListener(event -> canvas.setHeight(pane.getHeight()));
        
        drawing = new Drawing(canvas);
        drawing.draw();
        
        pane.getChildren().add(canvas);
        
		return pane;
	}
	
}
