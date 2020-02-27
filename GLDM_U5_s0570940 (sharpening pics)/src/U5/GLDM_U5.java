package U5;

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
public class GLDM_U5 implements PlugIn {

	ImagePlus imp; // ImagePlus object
	private int[] origPixels;
	private int width;
	private int height;

	String[] items = {"Original", "Weichgezeichnetes Bild", "Hochpassgefiltertes Bild", "Bild mit verstärkten Kanten"};


	public static void main(String args[]) {

		IJ.open("/users/kimnganledang/Desktop/GLDM/sail.jpg");
		//IJ.open("Z:/Pictures/Beispielbilder/orchid.jpg");

		GLDM_U5 pw = new GLDM_U5();
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
			// start the sum from value 0
			int rn = 0;
			int gn = 0;
			int bn = 0;
			
			
			// Array zum ZurÃ¼ckschreiben der Pixelwerte
			int[] pixels = (int[])ip.getPixels();

			if (method.equals("Original")) {

				for (int y=0; y<height; y++) {
					for (int x=0; x<width; x++) {
						int pos = y*width + x;
						
						pixels[pos] = origPixels[pos];
					}
				}
			}
			if (method.equals("Weichgezeichnetes Bild")) {

				for (int y=0; y<height; y++) {
					for (int x=0; x<width; x++) {
						int pos = y*width + x;
						int argb = origPixels[pos];  // Lesen der Originalwerte 
						
						int r = (argb >> 16) & 0xff;
						int g = (argb >>  8) & 0xff;
						int b =  argb        & 0xff;
						

						// for the pixel in the middle
						 rn = r * 1/9;
						 bn = b * 1/9;
						 gn = g * 1/9;
						
						if (y>0 && x>0 && y<height-1 && x<width-1) {
			 				
							// neighbors pixels
							// 1	2	3
							// 8   pos  4
							// 7	6	5  
							
							int	 pos1 = (y-1)*width + (x-1);
							int	 pos2 = (y-1)*width + x;
							int	 pos3 = (y-1)*width + (x+1);
							int	 pos4 = y*width + (x+1);
							int	 pos5 = (y+1)*width + (x+1);
							int	 pos6 = (y+1)*width + x;
							int	 pos7 = (y+1)*width + (x-1);
							int	 pos8 = y*width + (x-1);
							
							
							int[] positions = {pos1, pos2, pos3, pos4, pos5, pos6, pos7, pos8};
							for (int i =0 ; i <positions.length; i++) { //loop the positions array to change neighbour pixels
								int argb1 = origPixels[positions[i]];
								int r1 = (argb1 >> 16) & 0xff;
								int g1 = (argb1 >>  8) & 0xff;
								int b1 =  argb1        & 0xff;
								
								rn += r1 * 1/9;
								bn += b1 * 1/9;
								gn += g1 * 1/9;
								
								
							}
						
							pixels[pos] = (0xFF<<24) | (rn<<16) | (gn << 8) | bn;
						}
						
					}
				}
			}
			if (method.equals("Hochpassgefiltertes Bild")) {

				for (int y=0; y<height; y++) {
					for (int x=0; x<width; x++) {
						int pos = y*width + x;
						int argb = origPixels[pos];  // Lesen der Originalwerte 
						
						int r = (argb >> 16) & 0xff;
						int g = (argb >>  8) & 0xff;
						int b =  argb        & 0xff;

						// for the pixel in the middle
						 rn = r * 8/9;
						 bn = b * 8/9;
						 gn = g * 8/9;
						
						if (y>0 && x>0 && y<height-1 && x<width-1) {
			 				
							// neighbors pixels
							// 1	2	3
							// 8   pos  4
							// 7	6	5  
							
							int pos1 = (y-1)*width + (x-1);
							int	pos2 = (y-1)*width + x;
							int	pos3 = (y-1)*width + (x+1);
							int pos4 = y*width + (x+1);
							int pos5 = (y+1)*width + (x+1);
							int pos6 = (y+1)*width + x;
							int pos7 = (y+1)*width + (x-1);
							int pos8 = y*width + (x-1);
							
							
							int[] positions = {pos1, pos2, pos3, pos4, pos5, pos6, pos7, pos8};
							for (int i =0 ; i <positions.length; i++) {
								int argb1 = origPixels[positions[i]];
								int r1 = (argb1 >> 16) & 0xff;
								int g1 = (argb1 >>  8) & 0xff;
								int b1 =  argb1        & 0xff;
								
								rn -= r1 * 1/9;
								bn -= b1 * 1/9;
								gn -= g1 * 1/9;
							
								
							}
							// until here the colour of picture is too dark, I added 128.
							rn += 128;
							bn += 128;
							gn += 128;
							pixels[pos] = (0xFF<<24) | (rn<<16) | (gn << 8) | bn;
						}
						
					}
				}
			}
			
			if (method.equals("Bild mit verstärkten Kanten")) {

					for (int y=0; y<height; y++) {
						for (int x=0; x<width; x++) {
							int pos = y*width + x;
							int argb = origPixels[pos];  // Lesen der Originalwerte 
							
							int r = (argb >> 16) & 0xff;
							int g = (argb >>  8) & 0xff;
							int b =  argb        & 0xff;

							// for the pos in the middle
							 rn = r * 17/9;
							 bn = b * 17/9;
							 gn = g * 17/9;
							
							if (y>0 && x>0 && y<height-1 && x<width-1) {
				 				
								// neighbors pixels
								// 1	2	3
								// 8   pos  4
								// 7	6	5  
								
								int	 pos1 = (y-1)*width + (x-1);
								int	 pos2 = (y-1)*width + x;
								int	 pos3 = (y-1)*width + (x+1);
								int	 pos4 = y*width + (x+1);
								int	 pos5 = (y+1)*width + (x+1);
								int	 pos6 = (y+1)*width + x;
								int	 pos7 = (y+1)*width + (x-1);
								int  pos8 = y*width + (x-1);
								
								
								int[] positions = {pos1, pos2, pos3, pos4, pos5, pos6, pos7, pos8};
								for (int i =0 ; i <positions.length; i++) {
									int argb1 = origPixels[positions[i]];
									int r1 = (argb1 >> 16) & 0xff;
									int g1 = (argb1 >>  8) & 0xff;
									int b1 =  argb1        & 0xff;
									
									rn -= r1 * 1/9;
									bn -= b1 * 1/9;
									gn -= g1 * 1/9;
									
								}
								// limit the values
								rn = Math.min(Math.max(rn, 0), 255);
								bn = Math.min(Math.max(bn, 0), 255);
								gn = Math.min(Math.max(gn, 0), 255);
								
								/*
								if(rn > 255) rn= 255; 
								if(rn< 0) rn= 0; 
								if(gn > 255) gn= 255; 
								if(gn < 0) gn= 0; 
								if(bn > 255) bn= 255; 
								if(bn < 0) bn= 0; 
								*/
								pixels[pos] = (0xFF<<24) | (rn<<16) | (gn << 8) | bn;
							}
							
						}
					}
				}
			
			
		}


	} // CustomWindow inner class
} 