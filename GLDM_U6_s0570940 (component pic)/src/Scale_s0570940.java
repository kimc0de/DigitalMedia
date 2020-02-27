
import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.NewImage;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;


public class Scale_s0570940 implements PlugInFilter {
	static ImagePlus imp; // ImagePlus object

	public int setup(String arg, ImagePlus imp) {
		if (arg.equals("about"))
		{showAbout(); return DONE;}
		return DOES_RGB+NO_CHANGES;
		// kann RGB-Bilder und veraendert das Original nicht
	}

	public static void main(String args[]) {

		IJ.open("/users/kimnganledang/Desktop/GLDM/Component.jpg");


		Scale_s0570940 pw = new Scale_s0570940();
		Scale_s0570940.imp = IJ.getImage();
		pw.run(imp.getProcessor());
	}

	public void run(ImageProcessor ip) {

		String[] dropdownmenue = {"Kopie", "Pixelwiederholung", "Bilinear"};

		GenericDialog gd = new GenericDialog("scale");
		gd.addChoice("Methode",dropdownmenue,dropdownmenue[0]);
		gd.addNumericField("Hoehe:",800,0);
		gd.addNumericField("Breite:",700,0);

		gd.showDialog();

		int newHeight = (int)gd.getNextNumber(); // _n fuer das neue skalierte Bild
		int newWidth =  (int)gd.getNextNumber();

		int width  = ip.getWidth();  // Breite bestimmen
		int height = ip.getHeight(); // Hoehe bestimmen
		String choice = gd.getNextChoice();

		//height_n = height;
		//width_n  = width;

		ImagePlus neu = NewImage.createRGBImage("Skaliertes Bild",
				newWidth, newHeight, 1, NewImage.FILL_BLACK);

		ImageProcessor ip_n = neu.getProcessor();

		int[] pix = (int[])ip.getPixels();
		int[] pix_n = (int[])ip_n.getPixels();

		if (choice == "Kopie") {
			
			// Schleife ueber das neue Bild
			for (int newY=0; newY<newHeight; newY++) {
				for (int newX=0; newX<newWidth; newX++) {
					int y = newY;
					int x = newX;

					if (y < height && x < width) {
						int pos_n = newY*newWidth + newX;
						int pos  =  y  *width   + x;

						pix_n[pos_n] = pix[pos];
					}
				}
			}
		}
		else if (choice == "Pixelwiederholung") {
            //nearestNeighbor(pix, width, height, pix_n, newWidth, newHeight);
            // Werte -1, damit nicht über den Rand gelaufen wird
            double oW = (double) (width-1);
            double nW = (double) (newWidth-1);
            double oH = (double) (height-1);
            double nH = (double) (newHeight-1);
            double factorX = oW/nW;
            double factorY = oH/nH;
            
            // Schleife ueber das neue Bild
            for (int yNew=0; yNew<newHeight; yNew++) {
                for (int xNew=0; xNew<newWidth; xNew++) {
                     
                    // nearest neighbor bestimmen
                    int posNew = pos(xNew, yNew, newWidth);
                    
                    int origX = (int) Math.round(factorX * xNew);
                    int origY = (int) Math.round(factorY * yNew);
                    
                    int posOrig = pos(origX, origY, width);
                     
                    pix_n[posNew] = pix[posOrig];
                }
            }
		}
		
		 else if (choice == "Bilinear") {
	           // bilinearInterpolation(pix, width, height, pix_n, newWidth, newHeight);
	        	 // Werte -1, damit nicht über den Rand gelaufen wird
	            double oW = (double) (width-1);
	            double nW = (double) (newWidth-1);
	            double oH = (double) (height-1);
	            double nH = (double) (newHeight-1);
	            double factorX = oW/nW;
	            double factorY = oH/nH;
	           
	     
	            // Schleife ueber das neue Bild
	            for (int newY=0; newY<newHeight; newY++) {
	                for (int newX=0; newX<newWidth; newX++) {
	                    int posNew = pos(newX, newY, newWidth);
	                    // Point P in original picture, the position coordinates have been rounded to an int
	                    double originalX = (factorX * newX);
	                    double originalY = (factorY * newY);
	                    
	                    // surrounding values
	                    int x_floor= (int)Math.floor(originalX);
	                    int y_floor= (int)Math.floor(originalY);
	                    int x_ceil = (int)Math.ceil(originalX)<width? (int)Math.ceil(originalX):(int)Math.floor(originalX);
	                    int y_ceil = (int)Math.ceil(originalY)<width? (int)Math.ceil(originalY):(int)Math.floor(originalY);
	                    
	                    //Find point A, B, C, D
	                    // A(floor x, floor y)
	                    int A = pix[pos(x_floor,y_floor, width)];
	                    
	                    // B (ceil x, floor y)
	                    int B = pix[pos(x_ceil, y_ceil, width)];
	                    
	                    // C (floor x, ceil y)
	                    int C = pix[pos(x_floor, y_ceil, width)];
	                    
	                    // D( ceil x, ceil y)
	                    int D = pix[pos(x_ceil, y_ceil, width)];
	                    
	                    // calculate h and v: distance of original point to new point
	                    double h = originalX - x_floor;
	                    double v = originalY - y_floor;
	                    
	                    // r, g, b extract
	                    int rA = (A << 16) & 0xff;
	                    int gA = (A << 8) & 0xff;
	                    int bA =  A & 0xff;
	                    int rB = (B << 16) & 0xff;
	                    int gB = (B << 8) & 0xff;
	                    int bB =  B & 0xff;
	                    int rC = (C << 16) & 0xff;
	                    int gC = (C << 8) & 0xff;
	                    int bC = C & 0xff;
	                    int rD = (D << 16) & 0xff;
	                    int gD = (D << 8) & 0xff;
	                    int bD = D & 0xff;
	                    
	                    int rNew = (int) (rA*(1-h)*(1-v) + rB*h*(1-v) + rC*(1-h)*v + rD*h*v);
	                    int gNew = (int) (gA*(1-h)*(1-v) + gB*h*(1-v) + gC*(1-h)*v + gD*h*v);
	                    int bNew = (int) (bA*(1-h)*(1-v) + bB*h*(1-v) + bC*(1-h)*v + bD*h*v);
	                    
	                    
	                   // limit the values
	                    rNew = Math.min(255, Math.max(0,rNew));
						bNew = Math.min(255, Math.max(0,bNew));
						gNew = Math.min(255, Math.max(0,gNew));
						
	                    pix_n[posNew] = (0xff<<24) | (rNew<<16) | (gNew<<8) | (bNew);
	     
	                }
	            }
	            
	        }
		
		// neues Bild anzeigen
		neu.show();
		neu.updateAndDraw();
	}

	void showAbout() {
		IJ.showMessage("");
	}

	   private int pos(int x, int y, int width) {
	        int position = y*width +x;
	        return position;
	    }


}

