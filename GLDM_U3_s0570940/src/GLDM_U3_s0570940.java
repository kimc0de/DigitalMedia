import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
     Opens an image window and adds a panel below the image
 */
public class GLDM_U3_s0570940 implements PlugIn {
	private static final RGB[] Colors = null;
	ImagePlus imp; // ImagePlus object
	private int[] origPixels;
	private int width;
	private int height;

	String[] items = {"Original", "Rot-Kanal", "Negativ", "Graustufen","Binarbild S/W",  
			"Binar 5 Stufen","Binar 10 Stufen","Binarbild mit hori. Fehlerdiffusion", "Sepia-Farbung", "sechs Farben"};


	public static void main(String args[]) {

		
		IJ.open("/Users/kimnganledang/Desktop/GLDM/Bear.jpg");

		GLDM_U3_s0570940 pw = new GLDM_U3_s0570940();
		pw.imp = IJ.getImage();
		pw.run("");
	}

	public void run(String arg) {
		if (imp==null) 
			imp = WindowManager.getCurrentImage();
		if (imp==null) {
			return;
		}
		CustomCanvas cc = new CustomCanvas(imp);

		storePixelValues(imp.getProcessor());

		new CustomWindow(imp, cc);
	}


	private void storePixelValues(ImageProcessor ip) {
		width = ip.getWidth();
		height = ip.getHeight();

		origPixels = ((int []) ip.getPixels()).clone();
	}


	class CustomCanvas extends ImageCanvas {

		CustomCanvas(ImagePlus imp) {
			super(imp);
		}

	} // CustomCanvas inner class


	class CustomWindow extends ImageWindow implements ItemListener {

		
		private String method;
		
		CustomWindow(ImagePlus imp, ImageCanvas ic) {
			super(imp, ic);
			addPanel();
		}

		void addPanel() {
			//JPanel panel = new JPanel();
			Panel panel = new Panel();

			JComboBox cb = new JComboBox(items);
			panel.add(cb);
			cb.addItemListener(this);

			add(panel);
			pack();
		}

		public void itemStateChanged(ItemEvent evt) {

			// Get the affected item
			Object item = evt.getItem();

			if (evt.getStateChange() == ItemEvent.SELECTED) {
				System.out.println("Selected: " + item.toString());
				method = item.toString();
				changePixelValues(imp.getProcessor());
				imp.updateAndDraw();
			} 

		}


		private void changePixelValues(ImageProcessor ip) {

			// Array zum Zur�ckschreiben der Pixelwerte
			int[] pixels = (int[])ip.getPixels();

			if (method.equals("Original")) {

				for (int y=0; y<height; y++) {
					for (int x=0; x<width; x++) {
						int pos = y*width + x;
						
						pixels[pos] = origPixels[pos];
					}
				}
			}
			
			if (method.equals("Rot-Kanal")) {

				for (int y=0; y<height; y++) {
					for (int x=0; x<width; x++) {
						int pos = y*width + x;
						int argb = origPixels[pos];  // Lesen der Originalwerte 

						int r = (argb >> 16) & 0xff;
						//int g = (argb >>  8) & 0xff;
						//int b =  argb        & 0xff;

						int rn = r;
						int gn = 0;
						int bn = 0;

						// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

						pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
					}
				}
			}
			
			if (method.equals("Negativ")) {

				for (int y=0; y<height; y++) {
					for (int x=0; x<width; x++) {
						int pos = y*width + x;
						int argb = origPixels[pos];  // Lesen der Originalwerte 

						int r = (argb >> 16) & 0xff;
						int g = (argb >>  8) & 0xff;
						int b =  argb        & 0xff;
						// Formel auf Skript
						int rn = 255-r;
						int gn = 255-g;
						int bn = 255-b;

						// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

						pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
					}
				}
			}
			if (method.equals("Graustufen")) {

				for (int y=0; y<height; y++) {
					for (int x=0; x<width; x++) {
						int pos = y*width + x;
						int argb = origPixels[pos];  // Lesen der Originalwerte 

						int r = (argb >> 16) & 0xff;
						int g = (argb >>  8) & 0xff;
						int b =  argb        & 0xff;
						
						//Formel auf Wikipedia
						//Grauwert = 0,299 × Rotanteil + 0,587 × Grünanteil + 0,114 × Blauanteil
						int grauwert = (int)(0.299*r + 0.587*g + 0.114*b);
					
						int rn = grauwert;
						int gn = grauwert;
						int bn = grauwert;

						// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

						pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
					}
				}
			}
			if (method.equals("Binarbild S/W")) {

				for (int y=0; y<height; y++) {
					for (int x=0; x<width; x++) {
						int pos = y*width + x;
						int argb = origPixels[pos];  // Lesen der Originalwerte 

						int r = (argb >> 16) & 0xff;
						int g = (argb >>  8) & 0xff;
						int b =  argb        & 0xff;
						
						int grauwert = (int)(0.299*r + 0.587*g + 0.114*b);
						
						if(grauwert>=128) { // value >= 128 will be white
						int rn = 255;
						int gn = 255;
						int bn = 255;
						pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
						}
						else { // value < 128 will be black
						int rn = 0;
						int gn = 0;
						int bn = 0;
						pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
						}
						// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

						//pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
					}
				}
			}
				if (method.equals("Binar 5 Stufen")) {

					for (int y=0; y<height; y++) {
						for (int x=0; x<width; x++) {
							int pos = y*width + x;
							int argb = origPixels[pos];  // Lesen der Originalwerte 

							int r = (argb >> 16) & 0xff;
							int g = (argb >>  8) & 0xff;
							int b =  argb        & 0xff;
							
							int grauwert = (int)(0.299*r + 0.587*g + 0.114*b);
							
							int numberOfBin = 5; 
							int binNumber = grauwert/(255/numberOfBin); // zu welchem Bin gehört der Wert
							
							int newValue = (255/(numberOfBin-1)) * binNumber;// neuer Grauwert rechnen
							
							int rn = newValue;
							int gn = newValue;
							int bn = newValue;
							
							
							// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

							pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
						}
					}
			}
				if (method.equals("Binar 10 Stufen")) {

					for (int y=0; y<height; y++) {
						for (int x=0; x<width; x++) {
							int pos = y*width + x;
							int argb = origPixels[pos];  // Lesen der Originalwerte 

							int r = (argb >> 16) & 0xff;
							int g = (argb >>  8) & 0xff;
							int b =  argb        & 0xff;
							
							int grauwert = (int)(0.299*r + 0.587*g + 0.114*b);
							int numberOfBin = 10;
							int binNumber = grauwert/(255/numberOfBin);
							int newValue = (255/(numberOfBin-1)) * binNumber;
							int rn = newValue;
							int gn = newValue;
							int bn = newValue;
							
							
							// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

							pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
						}
					}
			}
				if (method.equals("Binarbild mit hori. Fehlerdiffusion")) {

					int schwellwert = 128;
		            
					
					for (int y=0; y<height; y++) {
						int error=0;
						for (int x=0; x<width; x++) {
							int pos = y*width + x;
							int argb = origPixels[pos];  // Lesen der Originalwerte 

							int r = (argb >> 16) & 0xff;
							int g = (argb >>  8) & 0xff;
							int b =  argb        & 0xff;
							 
							int quant = 0;
							int grauwert = ((r+g+b)/3 +error);
		                    if (grauwert >128) {
		                    	quant = 255;
		                    }
		                    error = grauwert - quant;
		                    // now the error will either be negative or positive, when we add it to the grauwert, it
		                    // already can correct the color.
		                   
							// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

							pixels[pos] = (0xFF<<24) | (quant<<16) | (quant<<8) | quant;
						}
						 //error=0;
					}
			}
				
			if (method.equals("Sepia-Farbung")) {

				for (int y=0; y<height; y++) {
					for (int x=0; x<width; x++) {
							int pos = y*width + x;
							int argb = origPixels[pos];  // Lesen der Originalwerte 

							int r = (argb >> 16) & 0xff;
							int g = (argb >>  8) & 0xff;
							int b =  argb        & 0xff;
							
							// https://www.dyclassroom.com/image-processing-project/how-to-convert-a-color-image-into-sepia-image
							// red value changed to match the goal picture
							int rn = (int)(0.5*r + 0.769*g + 0.189*b);
							int gn = (int)(0.3*r + 0.686*g + 0.168*b);
							int bn = (int)(0.2*r + 0.534*g + 0.131*b);
							// rn > gn> bn
							if (rn > 255) { //prevent Overflow
								r = 255; 
							}
							else {
								r = rn;
							}
							if(gn > 255){
								  g = 255;
							}
							else{
								  g = gn;
							}
							if(bn > 255){
								  b = 255;
							}
							else {
								  b = bn;
							}
							// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

							pixels[pos] = (0xFF<<24) | (r<<16) | (g<<8) | b; // 
						}
					}
			}
			if (method.equals("sechs Farben")) {
				 /*With the help of a Website for colour picker, I chose 6 colours below:
                 * 
                 * dark blue (46,100,136)
                 * light blue (104,136,161)
                 * 
                 * dark brown (102,89,80)
                 * light brown (157,143,134)
                 * 
                 * nearly white (209,207,208)
                 * nearly black (35,37,36) 
                 * 
                 */
				RGB [] Colors = {
						new RGB(46,100,136),
						new RGB(104,136,161),
						new RGB(102,89,80),
						new RGB(157,143,134),
						new RGB(209,207,208),
						new RGB(35,37,36)};
						
				
				for (int y=0; y<height; y++) {
					for (int x=0; x<width; x++) {
							int pos = y*width + x;
							int argb = origPixels[pos];  // Lesen der Originalwerte 

							int r = (argb >> 16) & 0xff;
							int g = (argb >>  8) & 0xff;
							int b =  argb        & 0xff;
						
							
			                 double distance = 500;
			                 double newDistance;
			                 RGB closeColor = Colors[0];
			                 for (RGB E : Colors) {
			                	 newDistance = Math.sqrt(Math.pow(r- E.getR(),2)+ Math.pow(g- E.getG(),2)+ Math.pow(b- E.getB(),2));
			                	 if(newDistance<distance) {
			                		 distance = newDistance; 
			                		 closeColor = E;
			                	 }
			                 }
			                 
			                 
							// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

							pixels[pos] = (0xFF<<24) | (closeColor.getR()<<16) | (closeColor.getG()<<8) | closeColor.getB() ;
						}
					}
			}
				
		}
			
	}
		


} // CustomWindow inner class
