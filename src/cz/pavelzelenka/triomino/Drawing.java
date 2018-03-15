package cz.pavelzelenka.triomino;

import java.util.Random;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Kresba
 * @author Pavel Zelenka A16B0176P
 * @version 2018-03-15
 */
public class Drawing {

	/** Vice barev na jeden dil */
	private BooleanProperty multiColourPolynom; 
	
	/** Velikost polynomu */
	private DoubleProperty sizeProperty;
	
	/** Barva dilku */
	private Color aColor = Color.rgb(230, 126, 34);
	private Color bColor = Color.rgb(52, 152, 219);
	private Color cColor = Color.rgb(46, 204, 113);
	private Color dColor = Color.rgb(231, 76, 60);
	
	/** Sirka pera */
	public static final int MAX_PEN_WIDTH = 1;
	/** Ovladaci prvek kresleni */
	private GraphicsContext g;
	/** Platno */
	private Canvas activeCanvas;
	/** Cas animace */
	private int time = 0;
	/** Bezici animace */
	private boolean isRunning = false;

	/**
	 * Vytvoreni instance kresby
	 * @param canvas platno
	 */
	public Drawing(Canvas canvas) {
		this.activeCanvas = canvas;
		this.sizeProperty = new SimpleDoubleProperty(50);
		this.multiColourPolynom = new SimpleBooleanProperty(true);
		g = canvas.getGraphicsContext2D();
		observeCanvasSize();
	}
	
	/**
	 * Pozorovani zmen velikosti platna
	 */
	private void observeCanvasSize() {
		activeCanvas.widthProperty().addListener(event -> {
			redraw();
		});
		
		activeCanvas.heightProperty().addListener(event -> {
			redraw();
		});
	}
		
	/**
	 * Prekresli plochu
	 */
	public void redraw() {
		clear();
		draw();
	}
	
	
	/**
	 * Vycisti plochu
	 */
	public void clear() {
		g.clearRect(0, 0, activeCanvas.getWidth(), activeCanvas.getHeight());
	}
	
	/**
	 * Vykresleni obrazu
	 */
	public void draw() {
		drawTriomino();
	}
	
	/**
	 * Vykresleni triomina
	 */
	private void drawTriomino() {
		
		int line = 0;
		int column = 0;
		
		double size = sizeProperty.get();
		
		int remains_j = (int) (activeCanvas.getHeight() -2*size);
		int remains_i = 0;
		
		while(remains_j >= 0 || remains_i < (int)activeCanvas.getWidth()+size/2) {
			for(int j = remains_j; j >= -((int)size*2); j-=size) {
				for(int i = remains_i; i <= (int)activeCanvas.getWidth()+size/2; i+=size) {
					g.translate(i, j);
				
					// prvni radek bez otoceni :.
					if(column == 0 && line%4 == 0) {
						drawPolygon0(size);
					}
				
					// prvni sloupec bez otoceni :.
					if(column%4 == 0 && line == 0) {
						drawPolygon0(size);
					}
				
					// prvni sloupec otocen :'
					if(column%4 == 2 && line == 0) {
						drawPolygon3(size);
					}
				
					// prvni radek otocen .:
					if(column == 0 && line%4 == 2) {
						drawPolygon1(size);
					}
				
					// druhy radek otocen ': 
					if(column == 1 && line%4 == 0 && line != 0) {
						drawPolygon2(size);
					}
				
					// druhy radek otocen :'
					if(column == 1 && line%4 == 2 && line != 2) {
						drawPolygon3(size);
					}
				
					// druhy sloupec otoceni ':
					if(column%4 == 0 && line == 1 && column != 0) {
						drawPolygon2(size);
					}
				
					// druhy sloupec otoceni .:   TODO: Doresit
					if(column%4 == 2 && line == 1 && column != 2) {
						drawPolygon1(size);
					}
				
					// druhy radek bez otoceni :.
					if(column == 1 && line == 1) {
						drawPolygon0(size);
					}
				
					// treti radek bez otoceni :.
					if(column == 2 && line == 2) {
						drawPolygon0(size);
					}
				
					g.translate(-i, -j);
					line++;
				}
				line = 0;
				column++;
			}
		
			line = 0;
			column = 0;
		
			remains_j -= 3*size;
			remains_i += 3*size;
		}
	}

	/**
	 * x
	 * xx
	 */
	private void drawPolygon0(double size) {
		if(multiColourPolynom.get()) {
			drawPolygon(size);
		} else {
			drawSingleColorPolygon(size, randomColor());
		}
	}
	
	/**
	 *  x
	 * xx
	 */
	private void drawPolygon1(double size) {
		g.save();	       
		g.translate(2*size,0);
		g.scale(-1, 1);
		drawPolygon0(size);
		g.restore();
	}
	
	/**
	 * xx
	 *  x
	 */
	private void drawPolygon2(double size) {
		g.save();	       
		g.translate(2*size,0);
		g.scale(-1, 1);
		drawPolygon3(size);
		g.restore();
	}
	
	/**
	 * xx
	 * x
	 */
	private void drawPolygon3(double size) {
		g.translate(2*size, 0);
		g.rotate(90);
		if(multiColourPolynom.get()) {
			drawPolygon(size);
		} else {
			drawSingleColorPolygon(size, randomColor());
		}
		g.rotate(-90);
		g.translate(-2*size, 0);
	}
	
	/**
	 * Jednobarevny dilek
	 * @param size velikost
	 * @param color barva
	 */
	private void drawSingleColorPolygon(double size, Color color) {
		drawPolygon(size, color, color, color, color, color, color, color, color);
	} 
	
	/**
	 * Vicebarevny dilek
	 * @param size velikost
	 */
	private void drawPolygon(double size) {
		Color aFill = aColor;
		
		Color aStroke = getStrokeColor(aColor);
		Color bFill = bColor;
		Color bStroke = getStrokeColor(bColor);
		Color cFill = cColor;
		Color cStroke = getStrokeColor(cColor);
		Color dFill = dColor;
		Color dStroke = getStrokeColor(dColor);
		drawPolygon(size, aFill, aStroke, bFill, bStroke, cFill, cStroke, dFill, dStroke);
	}
	
	/**
	 * Vrati barvu obrazeni
	 * @param fill barva vyplne
	 * @return barva obtazeni
	 */
	private Color getStrokeColor(Color fill) {
		int r = (int)(fill.getRed()*255)-30 ;
		int g = (int)(fill.getGreen()*255)-30;
		int b = (int)(fill.getBlue()*255)-30;
		if(r < 0) r = 0;
		if(g < 0) g = 0;
		if(b < 0) b = 0;
		return Color.rgb(r, g, b);
	}
	
	/**
	 * Vicebarevny dilek
	 * @param size velikost
	 * @param aFill A vypln
	 * @param aStroke A obtahnuti
	 * @param bFill B vypln
	 * @param bStroke B obtahnuti
	 * @param cFill C vypln
	 * @param cStroke C obtahnuti
	 * @param dFill D vypln
	 * @param dStroke D obtahnuti
	 */
	private void drawPolygon(double size, Color aFill, Color aStroke, Color bFill, Color bStroke, Color cFill, Color cStroke, Color dFill, Color dStroke) {
		double xpoints[] = {0, size/2, size/2, size, size, 0};
		double ypoints[] = {0, 0, size/2, size/2, size, size};
		int npoints = 6;
		
		//A
		drawPolygonA(size, aFill, aStroke, xpoints, ypoints, npoints);
		
		//C
		drawPolygonC(size, cFill, cStroke, xpoints, ypoints, npoints);	
		
		//B
		drawPolygonB(size, bFill, bStroke, xpoints, ypoints, npoints);
		
		//D
		drawPolygonD(size, dFill, dStroke, xpoints, ypoints, npoints);
	}
	
	/**
	 * Vykresleni polynomu A
	 * @param size velikost
	 * @param aFill vypln
	 * @param aStroke obtahnuti
	 * @param xpoints X body
	 * @param ypoints Y body
	 * @param npoints pocet bodu
	 */
	private void drawPolygonA(double size, Color aFill, Color aStroke, double xpoints[], double ypoints[], int npoints) {
		g.translate(0, size);
		g.setFill(aFill);
		g.setStroke(aStroke);
		g.fillPolygon(xpoints, ypoints, npoints);
		g.strokePolygon(xpoints, ypoints, npoints);
		g.translate(0, -size);
	}
	
	/**
	 * Vykresleni polynomu B
	 * @param size velikost
	 * @param bFill vypln
	 * @param bStroke obtahnuti
	 * @param xpoints X body
	 * @param ypoints Y body
	 * @param npoints pocet bodu
	 */
	private void drawPolygonB(double size, Color bFill, Color bStroke, double xpoints[], double ypoints[], int npoints) {
		g.translate(size, 0);
		g.rotate(90);
		g.setFill(bFill);
		g.setStroke(bStroke);
		g.fillPolygon(xpoints, ypoints, npoints);
		g.strokePolygon(xpoints, ypoints, npoints);
		g.rotate(-90);
		g.translate(-size, 0);
	}
	
	/**
	 * Vykresleni polynomu C
	 * @param size velikost
	 * @param cFill vypln
	 * @param cStroke obtahnuti
	 * @param xpoints X body
	 * @param ypoints Y body
	 * @param npoints pocet bodu
	 */
	private void drawPolygonC(double size, Color cFill, Color cStroke, double xpoints[], double ypoints[], int npoints) {
		g.translate(size/2, size/2);
		g.setFill(cFill);
		g.setStroke(cStroke);
		g.fillPolygon(xpoints, ypoints, npoints);
		g.strokePolygon(xpoints, ypoints, npoints);
		g.translate(-size/2, -size/2);
	}
	
	/**
	 * Vykresleni polynomu D
	 * @param size velikost
	 * @param dFill vypln
	 * @param dStroke obtahnuti
	 * @param xpoints X body
	 * @param ypoints Y body
	 * @param npoints pocet bodu
	 */
	private void drawPolygonD(double size, Color dFill, Color dStroke, double xpoints[], double ypoints[], int npoints) {
		g.translate(size, 2*size);
		g.rotate(-90);
		g.setFill(dFill);
		g.setStroke(dStroke);
		g.fillPolygon(xpoints, ypoints, npoints);
		g.strokePolygon(xpoints, ypoints, npoints);
		g.rotate(+90);
		g.translate(-size, -2*size);
	}
	
	/** Vrati nahodnou barvu */
	public Color randomColor() {
		Random rand = new Random();
		int r = rand.nextInt(256);
		int g = rand.nextInt(256);
		int b = rand.nextInt(256);
		if(r < 125 && g < 125 && b < 125) {
			switch(rand.nextInt(3)) {
			case 0: r += 125; g += rand.nextInt(125); b += rand.nextInt(125); break;
			case 1: g += 125; b += rand.nextInt(125); r += rand.nextInt(125); break;
			case 2: b += 125; r += rand.nextInt(125); g += rand.nextInt(125); break;
			}
		}
		return Color.rgb(r, g, b);
	}
	
	/**
	 * Zmensi velikost dilku
	 */
	public void smaller() {
		if(sizeProperty.intValue() > 3) {
			sizeProperty.set(sizeProperty.intValue() - 1);
			redraw();
		}
	}
	
	/**
	 * Zvetsi velikost dilku
	 */
	public void larger() {
		if(sizeProperty.intValue() < 500) {
			sizeProperty.set(sizeProperty.intValue() + 1);
			redraw();
		}
	}
	
	/**
	 * Vice barev na jeden dil
	 * @return vice barev na jeden dil
	 */
	public BooleanProperty multiColourPolynom() {
		return multiColourPolynom;
	}

	/**
	 * Nastavi, zdali je pouzito vice barev na jeden dil
	 * @param multiColourPolynom pouzito vice barev na jeden dil
	 */
	public void setMultiColourPolynom(boolean multiColourPolynom) {
		this.multiColourPolynom.set(multiColourPolynom);
		redraw();
	}

	/**
	 * Velikost dilku
	 * @return velikost dilku
	 */
	public DoubleProperty sizeProperty() {
		return sizeProperty;
	}

	/**
	 * Vrati, zdali bezi animace
	 * @return vrati true, bezi-li v tento moment animace
	 */
    public boolean isRunning() {
		return isRunning;
	}

    /**
     * Vrati barvu A dilku
     * @return barva dilku
     */
	public Color getaColor() {
		return aColor;
	}

	/**
	 * Nastavi barvu A dilku
	 * @param aColor barva dilku
	 */
	public void setaColor(Color aColor) {
		if(aColor != null) {
			this.aColor = aColor;
			redraw();
		}
	}

    /**
     * Vrati barvu B dilku
     * @return barva dilku
     */
	public Color getbColor() {
		return bColor;
	}

	/**
	 * Nastavi barvu B dilku
	 * @param bColor barva dilku
	 */
	public void setbColor(Color bColor) {
		if(aColor != null) {
			this.bColor = bColor;
			redraw();
		}
	}

    /**
     * Vrati barvu C dilku
     * @return barva dilku
     */
	public Color getcColor() {
		return cColor;
	}

	/**
	 * Nastavi barvu C dilku
	 * @param cColor barva dilku
	 */
	public void setcColor(Color cColor) {
		if(aColor != null) {
			this.cColor = cColor;
			redraw();
		}
	}

    /**
     * Vrati barvu D dilku
     * @return barva dilku
     */
	public Color getdColor() {
		return dColor;
	}

	/**
	 * Nastavi barvu D dilku
	 * @param dColor barva dilku
	 */
	public void setdColor(Color dColor) {
		if(aColor != null) {
			this.dColor = dColor;
			redraw();
		}
	}
	
}
