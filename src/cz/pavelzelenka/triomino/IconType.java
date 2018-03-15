package cz.pavelzelenka.triomino;

import javafx.scene.image.Image;

/**
 * Ikony
 * @author Pavel Zelenka A16B0176P
 * @version 2018-03-15
 */

public enum IconType {
	APPLICATION_128(new Image("/icons/triomino-128.png"));
	
	/** ikona */
	Image icon;
	
	/**
	 * Konstruktor
	 * @param icon ikona
	 */
	private IconType(Image icon) {
		this.icon = icon;
	}

	/**
	 * Vrati ikonu
	 * @return ikona
	 */
	public Image get() {
		return icon;
	}
	
}
