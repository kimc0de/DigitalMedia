import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.NewImage;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

//erste Uebung (elementare Bilderzeugung)

public class GLDM_U1 implements PlugIn {

	final static String[] choices = {
			"Schwarzes Bild",
			"Gelbes Bild",
			"belgischen Fahne",
			"Fahne der USA",
			"Horiz. Schwarz/Rot vert. Schwarz/Blau Verlauf",
			"Ombre",
			"Ombre gelb, blau",
			"tschechiche Fahne",
			"bangladeschische Fahne"
	};

	private String choice;



	public static void main(String args[]) {
		ImageJ ij = new ImageJ(); // neue ImageJ Instanz starten und anzeigen 
		ij.exitWhenQuitting(true);

		GLDM_U1 imageGeneration = new GLDM_U1();
		imageGeneration.run("");
	}

	public void run(String arg) {

		int width  = 566;  // Breite
		int height = 400;  // Hoehe

		// RGB-Bild erzeugen
		ImagePlus imagePlus = NewImage.createRGBImage("GLDM_U1", width, height, 1, NewImage.FILL_BLACK);
		ImageProcessor ip = imagePlus.getProcessor();

		// Arrays fuer den Zugriff auf die Pixelwerte
		int[] pixels = (int[])ip.getPixels();

		dialog();

		////////////////////////////////////////////////////////////////
		// Hier bitte Ihre Aenderungen / Erweiterungen

		if ( choice.equals("Schwarzes Bild") ) {
			generateBlackImage(width, height, pixels);
		}
		//choice of eines gelbes Bildes
		if (choice.equals("Gelbes Bild")) {
			generateYellowImage(width, height, pixels);
		}
		//choice of einer belgischen Fahne
		if (choice.equals("belgischen Fahne")) {
			generateBelgischeFahneImage(width, height, pixels);
		}
		//choice of Fahne der USA
		if (choice.equals("Fahne der USA")) {
			generateUSAFahneImage(width, height, pixels);
		}
		//choice of Horiz. Schwarz/Rot vert. Schwarz/Blau Verlauf
		if (choice.equals("Horiz. Schwarz/Rot vert. Schwarz/Blau Verlauf")) {
			generateOmbreImage(width, height, pixels);
		}
		//choice of "tschechiche Fahne"
		if (choice.equals("tschechiche Fahne")) {
			generateTschechicheFahneImage(width, height, pixels);
		}//choice of "bangladeschische Fahne"
		if (choice.equals("bangladeschische Fahne")) {
			generateBaladeschischenImage(width, height, pixels);
		}
		if (choice.equals("Ombre weiss, gelb, rot")) {
			weiss_gelb_rot(width, height, pixels);
		}
		if (choice.equals("Ombre gelb, blau")) {
			gelb_blau(width, height, pixels);
		}

		////////////////////////////////////////////////////////////////////

		// neues Bild anzeigen
		imagePlus.show();
		imagePlus.updateAndDraw();
	}

	private void generateBlackImage(int width, int height, int[] pixels) {
		// Schleife ueber die y-Werte
		for (int y=0; y<height; y++) {
			// Schleife ueber die x-Werte
			for (int x=0; x<width; x++) {
				int pos = y*width + x; // Arrayposition bestimmen

				int r = 0;
				int g = 0;
				int b = 0;

				// Werte zurueckschreiben
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;
			}
		}
	}


	private void generateYellowImage(int width, int height, int[] pixels) {
		// Schleife ueber die y-Werte
		for (int y=0; y<height; y++) {
			// Schleife ueber die x-Werte
			for (int x=0; x<width; x++) {
				int pos = y*width + x; // Arrayposition bestimmen

				int r = 255;
				int g = 255;
				int b = 0;

				// Werte zurueckschreiben
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;
			}
		}
	}

	private void generateBelgischeFahneImage(int width, int height, int[] pixels) {
		// Schleife ueber die y-Werte
		for (int y=0; y<height; y++) {
			// Schleife ueber die x-Werte
			for (int x=0; x<width; x++) {
				int pos = y*width + x; // Arrayposition bestimmen

				int r = 0;
				int g = 0;
				int b = 0;
				if (x > width/3 && x < width/3*2) {
					r = 255;
					g = 255;
					b = 0;
				}
				else if (x > width/3*2 && x < width) {
					r = 255;
					g = 0;
					b = 0;
				}
				// Werte zurueckschreiben
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;
			}
		}
	}
	private void generateUSAFahneImage(int width, int height, int[] pixels) {
		// Schleife ueber die y-Werte
		for (int y=0; y<height; y++) {
			// Schleife ueber die x-Werte
			for (int x=0; x<width; x++) {
				int pos = y*width + x; // Arrayposition bestimmen

				int r = 255;
				int g = 255;
				int b = 255;
				// Werte zurueckschreiben
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;
			}
		}
		boolean red = true;
		int colorCount = 31;// width (400)/ Number of stripes (13) =  31

		//Schleife ueber die y-Werte(rote Streifen)
		for(int y=0; y<height; y++) {
			if (y>colorCount) {
				red = !red;
				colorCount +=31;
			}

			//Schleife ueber die x-Werte
			if (red ==true) {
				for (int x=0; x<width; x++) {
					int pos = y *width + x;// Array position bestimmen

					int r = 178;
					int g=34;
					int b=52;
					// Werte zurueckschreiben
					pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;
				}
			}
		}

		int canton = (height/2) +17; // the height spans 7 stripes (31*7=217)
		//Schleife ueber die y-Werte(blau)
		for (int y = 0; y<canton ; y++) {
			//Schleife ueber die x-Werte
			for (int x= 0; x<canton ; x++) {
				//x /width/3
				int pos = y*width +x;// Array position bestimmen
				int r = 60;
				int g=59;
				int b=110;
				// Werte zurueckschreiben
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;
			}
		}
	}
	private void generateOmbreImage(int width, int height, int[] pixels) {

		// Schleife ueber die y-Werte
		for (int y=0; y<height; y++) {
			// Schleife ueber die x-Werte
			for (int x=0; x<width; x++) {
				int pos = y*width + x; // Arrayposition bestimmen
				
				int r = x * 255 / width ; //  0 < r  <1... orig. value = 0, start increasing from x=0, 255 to keep positive, grow how much: til the end of width
				int g = 0;
				int b = y * 255 / height; //  0 < b  <1... start increasing from y=0, 255 to keep positive, grow til the end of width
				
				
				// Werte zurueckschreiben
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;
			}
		}
	}
	private void weiss_gelb_rot(int width, int height, int[] pixels) {

		// Schleife ueber die y-Werte
		for (int y=0; y<height; y++) {
			// Schleife ueber die x-Werte
			for (int x=0; x<width; x++) {
				int pos = y*width + x; // Arrayposition bestimmen
				
				int r = 255 ;
				int g = Math.min(255, Math.max(0, 255 + (x-width/2)*(-255)/(width/4))); 
				// orig.value =255, start dropping from x-width/2, -255 to keep positive, drop how much: width/4
				int b = Math.min(255, Math.max(0, 255+(x - width/4)* (-255)/(width/4)));
				// orig.value =255, start dropping from x-width/4, -255 to keep positive, drop how much: width/4
			
				
				// Werte zurueckschreiben
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;
			}
		}
	}
	private void gelb_blau(int width, int height, int[] pixels) {

		// Schleife ueber die y-Werte
		for (int y=0; y<height; y++) {
			// Schleife ueber die x-Werte
			for (int x=0; x<width; x++) {
				int pos = y*width + x; // Arrayposition bestimmen
				
				int r = Math.min(255,  Math.max(0, 255 + (x-width/4)*(-255)/(width/2)));
				// orig.value = 255, start dropping from x-width/4, -255 to keep positive, drop how much: width/2
				int g = r;
				int b = Math.min(255, Math.max(0, (x-width/4)*255/(width/2)));
				// orig.value =255, start dropping from x-width/4, -255 to keep positive, drop how much: width/2
				
				// Werte zurueckschreiben
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;
			}
		}
	}
	private void generateTschechicheFahneImage(int width, int height, int[] pixels) {
		// Schleife ueber die y-Werte
		for (int y=0; y<height; y++) {
			// Schleife ueber die x-Werte
			for (int x=0; x<width; x++) {
				int pos = y*width + x; // Arrayposition bestimmen
				// white part
				int r = 255;
				int g = 255;
				int b = 255;

				//half upper part of the triangle
				if(x*height/width<y && y<=height/2) {
					b=255;
					r = 0;
					g = 0;
				}
				//half lower part of the triangle
				else if(x*height/width<height-y && y>=height/2) {
					b=255;
					r = 0;
					g = 0;

				}
				//red part
				else if(y>200){
					r=255;
					b=0;
					g=0;
				}						

				// Werte zurueckschreiben
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;
			}
		}
	}

	private void generateBaladeschischenImage(int width, int height, int[] pixels) {
		int radius = width / 5;
		int bx = width / 20 * 9; //x-  position of the middle point of circle
		int by = height / 2;  // y- position of the middle point of circle
		int wlength;
		int hlength;

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int pos = y * width + x;
				// green background
				int r = 0;
				int g = 106;
				int b = 78;

				wlength = x - bx;
				hlength = y - by;

				int wl = Math.abs(wlength);// absolute value of wlength
				int hl = Math.abs(hlength);// absolute value of hlength
				double length = Math.sqrt(wl * wl + hl * hl);
				if (length <= radius) {
					// red circle
					r = 244;
					g = 42;
					b = 65;
				}
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
			}
		}

	}
	private void dialog() {
		// Dialog fuer Auswahl der Bilderzeugung
		GenericDialog gd = new GenericDialog("Bildart");

		gd.addChoice("Bildtyp", choices, choices[0]);


		gd.showDialog();	// generiere Eingabefenster

		choice = gd.getNextChoice(); // Auswahl uebernehmen

		if (gd.wasCanceled())
			System.exit(0);
	}
}