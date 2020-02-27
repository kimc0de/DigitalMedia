
import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.NewImage;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
//import gdmvalidation.ScaleValidate;
 
public class U6_A2 implements PlugInFilter {
    protected ImagePlus imp;
 
    public static void main(String args[]) {
        ImageJ ij = new ImageJ(); // neue ImageJ Instanz starten und anzeigen
        ij.exitWhenQuitting(true);
 
        IJ.open("/users/kimnganledang/Downloads/component.jpg");
      //  IJ.open("/users/jakob/ImageJ/images/component.jpg");
 
        U6_A2 sd = new U6_A2();
        sd.imp = IJ.getImage();
        ImageProcessor ip = sd.imp.getProcessor();
        sd.run(ip);
    }
 
    public int setup(String arg, ImagePlus imp) {
        if (arg.equals("about"))
        {showAbout(); return DONE;}
        return DOES_RGB+NO_CHANGES;
        // kann RGB-Bilder und veraendert das Original nicht
    }
  
 
    public void run(ImageProcessor ip) {
 
        String[] dropdownmenue = {"Kopie", "Pixelwiederholung", "Bilinear", "Test"};
        GenericDialog gd = new GenericDialog("scale");
        gd.addChoice("Methode",dropdownmenue,dropdownmenue[0]);
        gd.addNumericField("Breite:",800,0);
        gd.addNumericField("Hoehe:",700,0);
 
        gd.showDialog();
 
        int newWidth =  (int)gd.getNextNumber();
        int newHeight = (int)gd.getNextNumber(); // _n fuer das neue skalierte Bild
        String choice = gd.getNextChoice();
 
        int width  = ip.getWidth();  // Breite bestimmen
        int height = ip.getHeight(); // Hoehe bestimmen
         
         
        ImagePlus scaledImage = NewImage.createRGBImage("Skaliertes Bild",
                newWidth, newHeight, 1, NewImage.FILL_BLACK);
 
        ImageProcessor ip_n = scaledImage.getProcessor();
 
        int[] pix = (int[])ip.getPixels();
        int[] pix_n = (int[])ip_n.getPixels();
      
        if (choice == "Kopie") {
            copyImage(pix, width, height, pix_n, newWidth, newHeight);
            
         
        } else if (choice == "Pixelwiederholung") {
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
            
            
            
        } else if (choice == "Bilinear") {
           // bilinearInterpolation(pix, width, height, pix_n, newWidth, newHeight);
        	 // Werte -1, damit nicht über den Rand gelaufen wird
            double oW = (double) (width-1);
            double nW = (double) (newWidth-1);
            double oH = (double) (height-1);
            double nH = (double) (newHeight-1);
            double factorX = width/newWidth;
            double factorY = height/newHeight;
            // color of 4 pixels
            int rgbA = 0;
            int rgbB = 0;
            int rgbC = 0;
            int rgbD = 0;
     
            // Schleife ueber das neue Bild
            for (int newY=0; newY<newHeight; newY++) {
                for (int newX=0; newX<newWidth; newX++) {
                    int posNew = pos(newX, newY, newWidth);
                    // Point A in original picture, the position coordinates have been rounded to an int
                    int aX = (int) Math.round(factorX * newX);
                    int aY = (int) Math.round(factorY * newY);
                    
                    // calculate h and v: distance of original point to new point
                    double h = factorX * newX - aX;
                    double v = factorY * newY - aY;
                    
                    // Farbwerte rgb auslesen
                    rgbA = pix[pos(aX, aY, width)];
                    if (aX == width-1) {
                        rgbB = pix[pos(aX, aY, width)];
                    } else {
                        rgbB = pix[pos(aX+1, aY, width)];
                    }
                    if (aY == height-1) {
                        rgbC = pix[pos(aX, aY, width)];
                    } else {
                        rgbC = pix[pos(aX, aY+1, width)];
                    }
                    if (aX == width-1 && aY == height-1) {
                        rgbD = pix[pos(aX, aY, width)];
                    }
                    else if (aX == width-1 && aY != height-1) {
                        rgbD = pix[pos(aX, aY+1, width)];
                    }
                    else if (aX != width-1 && aY == height-1) {
                        rgbD = pix[pos(aX+1, aY, width)];
                    } else {
                        rgbD = pix[pos(aX+1, aY+1, width)];
                    }
                    // r, g, b extract
                    int rA = (rgbA >> 16) & 0xff;
                    int gA = (rgbA >> 8) & 0xff;
                    int bA = rgbA & 0xff;
                    int rB = (rgbB >> 16) & 0xff;
                    int gB = (rgbB >> 8) & 0xff;
                    int bB = rgbB & 0xff;
                    int rC = (rgbC >> 16) & 0xff;
                    int gC = (rgbC >> 8) & 0xff;
                    int bC = rgbC & 0xff;
                    int rD = (rgbD >> 16) & 0xff;
                    int gD = (rgbD >> 8) & 0xff;
                    int bD = rgbD & 0xff;
                    
                    int rNew = (int) Math.round(rA*(1-h)*(1-v) + rB*h*(1-v) + rC*(1-h)*v + rD*h*v);
                    int gNew = (int) Math.round(gA*(1-h)*(1-v) + gB*h*(1-v) + gC*(1-h)*v + gD*h*v);
                    int bNew = (int) Math.round(bA*(1-h)*(1-v) + bB*h*(1-v) + bC*(1-h)*v + bD*h*v);
                    
                    
                   // limit the values
                    
                    /* did not work!!!!
                    rNew = Math.max(Math.min(rNew, 0), 255);
					gNew = Math.max(Math.min(gNew, 0), 255);
					bNew = Math.max(Math.min(bNew, 0), 255);
                     */
					   if (rNew > 255) {
		                    rNew = 255;
		                }
		                else if (rNew < 0) {
		                    rNew = 0;
		                }
		                if (gNew > 255) {
		                    gNew = 255;
		                }
		                else if (gNew < 0) {
		                    gNew = 0;
		                }
		                if (bNew > 255) {
		                    bNew = 255;
		                }
		                else if (bNew < 0) {
		                    bNew = 0;
		                }
					
					
					
                    pix_n[posNew] = (0xff<<24) | (rNew<<16) | (gNew<<8) | (bNew);
     
                }
            }
            
        }
        // neues Bild anzeigen
        
            scaledImage.show();
            scaledImage.updateAndDraw();
        
    }
     
    private int pos(int x, int y, int width) {
        int position = y*width +x;
        return position;
    }
   
    private void bilinearInterpolation(int[] pix, int width,
            int height, int[] pix_n, int newWidth, int newHeight) {
        // Werte -1, damit nicht über den Rand gelaufen wird
        double oW = (double) (width-1);
        double nW = (double) (newWidth-1);
        double oH = (double) (height-1);
        double nH = (double) (newHeight-1);
        double ratioX = oW/nW;
        double ratioY = oH/nH;
        int rgbA = 0;
        int rgbB = 0;
        int rgbC = 0;
        int rgbD = 0;
 
        // Schleife ueber das neue Bild
        for (int yNew=0; yNew<newHeight; yNew++) {
            for (int xNew=0; xNew<newWidth; xNew++) {
                int posNew = pos(xNew, yNew, newWidth);
                // Punkt A im Originalbild
                int aX = (int) Math.round(ratioX * xNew);
                int aY = (int) Math.round(ratioY * yNew);
                // h und v berechnen
                double h = ratioX * xNew - aX;
                double v = ratioY * yNew -aY;
                // Farbwerte rgb auslesen
                rgbA = pix[pos(aX, aY, width)];
                if (aX == width-1) {
                    rgbB = pix[pos(aX, aY, width)];
                } else {
                    rgbB = pix[pos(aX+1, aY, width)];
                }
                if (aY == height-1) {
                    rgbC = pix[pos(aX, aY, width)];
                } else {
                    rgbC = pix[pos(aX, aY+1, width)];
                }
                if (aX == width-1 && aY == height-1) {
                    rgbD = pix[pos(aX, aY, width)];
                }
                else if (aX == width-1 && aY != height-1) {
                    rgbD = pix[pos(aX, aY+1, width)];
                }
                else if (aX != width-1 && aY == height-1) {
                    rgbD = pix[pos(aX+1, aY, width)];
                } else {
                    rgbD = pix[pos(aX+1, aY+1, width)];
                }
                // r, g, b extrahieren
                int rA = (rgbA >> 16) & 0xff;
                int gA = (rgbA >> 8) & 0xff;
                int bA = rgbA & 0xff;
                int rB = (rgbB >> 16) & 0xff;
                int gB = (rgbB >> 8) & 0xff;
                int bB = rgbB & 0xff;
                int rC = (rgbC >> 16) & 0xff;
                int gC = (rgbC >> 8) & 0xff;
                int bC = rgbC & 0xff;
                int rD = (rgbD >> 16) & 0xff;
                int gD = (rgbD >> 8) & 0xff;
                int bD = rgbD & 0xff;
                int rNew = (int) Math.round(rA*(1-h)*(1-v) + rB*h*(1-v) + rC*(1-h)*v + rD*h*v);
                int gNew = (int) Math.round(gA*(1-h)*(1-v) + gB*h*(1-v) + gC*(1-h)*v + gD*h*v);
                int bNew = (int) Math.round(bA*(1-h)*(1-v) + bB*h*(1-v) + bC*(1-h)*v + bD*h*v);
                // Werte korrigieren
                if (rNew > 255) {
                    rNew = 255;
                }
                else if (rNew < 0) {
                    rNew = 0;
                }
                if (gNew > 255) {
                    gNew = 255;
                }
                else if (gNew < 0) {
                    gNew = 0;
                }
                if (bNew > 255) {
                    bNew = 255;
                }
                else if (bNew < 0) {
                    bNew = 0;
                }
                 
                pix_n[posNew] = (0xff<<24) | (rNew<<16) | (gNew<<8) | (bNew);
 
            }
        }
    }
 
   
 
    private void copyImage(int[] origPix, int origWidth, int origHeight,
            int[] newPix, int newWidth, int newHeight) {
        for (int yNew=0; yNew<newHeight; yNew++) {
            for (int xNew=0; xNew<newWidth; xNew++) {
                int y = yNew;
                int x = xNew;
 
                if (y < origHeight && x < origWidth) {
                    int posNew = yNew*newWidth + xNew;
                    int pos  =  y  *origWidth   + x;
 
                    newPix[posNew] = origPix[pos];
                }
             }
            }
    }
 
    void showAbout() {
        IJ.showMessage("");
    }
}