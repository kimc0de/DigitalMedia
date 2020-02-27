import ij.*;
import ij.io.*;
import ij.process.*;
import ij.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.filter.*;


public class GLDM_U4 implements PlugInFilter {

	protected ImagePlus imp;
	final static String[] choices = {"Wischen", "Weiche Blende", "Overlay A-B","Overlay B-A", "Schieb-Blende","Chroma Key","eigene Überblendung" };

	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return DOES_RGB+STACK_REQUIRED;
	}
	
	public static void main(String args[]) {
		ImageJ ij = new ImageJ(); // neue ImageJ Instanz starten und anzeigen 
		ij.exitWhenQuitting(true);
		
		IJ.open("/Users/kimnganledang/Desktop/GLDM/StackB.zip");
		
		GLDM_U4 sd = new GLDM_U4();
		sd.imp = IJ.getImage();
		ImageProcessor B_ip = sd.imp.getProcessor();
		sd.run(B_ip);
	}

	public void run(ImageProcessor B_ip) {
		// Film B wird uebergeben
		ImageStack stack_B = imp.getStack();
		
		int length = stack_B.getSize();
		int width  = B_ip.getWidth();
		int height = B_ip.getHeight();
		
		// ermoeglicht das Laden eines Bildes / Films
		Opener o = new Opener();
		OpenDialog od_A = new OpenDialog("Choose the second video ...",  "");
				
		// Film A wird dazugeladen
		String dateiA = od_A.getFileName();
		if (dateiA == null) return; // Abbruch
		String pfadA = od_A.getDirectory();
		ImagePlus A = o.openImage(pfadA,dateiA);
		if (A == null) return; // Abbruch

		ImageProcessor A_ip = A.getProcessor();
		ImageStack stack_A  = A.getStack();

		if (A_ip.getWidth() != width || A_ip.getHeight() != height)
		{
			IJ.showMessage("Fehler", "BildgrÃ¶ÃŸen passen nicht zusammen");
			return;
		}
		
		// Neuen Film (Stack) "Erg" mit der kleineren Laenge von beiden erzeugen
		length = Math.min(length,stack_A.getSize());

		ImagePlus Erg = NewImage.createRGBImage("Ergebnis", width, height, length, NewImage.FILL_BLACK);
		ImageStack stack_Erg  = Erg.getStack();

		// Dialog fuer Auswahl des Ueberlagerungsmodus
		GenericDialog gd = new GenericDialog("Überlagerung");
		gd.addChoice("Methode",choices,"");
		gd.showDialog();

		int methode = 0;		
		String s = gd.getNextChoice();
		if (s.equals("Wischen")) methode = 1;
		if (s.equals("Weiche Blende")) methode = 2;
		if (s.equals("Overlay A-B")) methode = 3;
		if (s.equals("Overlay B-A")) methode = 4;
		if (s.equals("Schieb-Blende")) methode = 5;
		if (s.equals("Chroma Key")) methode = 6;
		if (s.equals("eigene Überblendung")) methode = 7;

		// Arrays fuer die einzelnen Bilder
		int[] pixels_B;
		int[] pixels_A;
		int[] pixels_Erg;

		// Schleife ueber alle Bilder
		for (int z=1; z<=length; z++)
		{
			pixels_B   = (int[]) stack_B.getPixels(z);
			pixels_A   = (int[]) stack_A.getPixels(z);
			pixels_Erg = (int[]) stack_Erg.getPixels(z);

			int pos = 0;
			for (int y=0; y<height; y++)
				for (int x=0; x<width; x++, pos++)
				{
					int cA = pixels_A[pos];
					int rA = (cA & 0xff0000) >> 16;
					int gA = (cA & 0x00ff00) >> 8;
					int bA = (cA & 0x0000ff);

					int cB = pixels_B[pos];
					int rB = (cB & 0xff0000) >> 16;
					int gB = (cB & 0x00ff00) >> 8;
					int bB = (cB & 0x0000ff);

					if (methode == 1) // wischen 
					{
				   //if (x > (z-1)*(double)width/(length-1)) -> horizontal verschieben
					if (y > (z-1)*(double)height/(length-1))  // change to vertical
						pixels_Erg[pos] = pixels_B[pos];
					else
						pixels_Erg[pos] = pixels_A[pos];
					}

					
					if (methode == 2) // Weiche Blende
					{
						int alpha = 255 * z / (length-1) ; //0 < alpha < 1
						
						// form in script 
							int r = (rA * alpha + rB*(255-alpha))/255 ;
							int g = (gA * alpha + gB*(255-alpha))/255;
							int b = (bA * alpha + bB*(255-alpha))/255;
						
					pixels_Erg[pos] = 0xFF000000 + ((r & 0xff) << 16) + ((g & 0xff) << 8) + ( b & 0xff);
						
					}
					
					if (methode == 3) { // Overlay (A,B)
						//rA: vordergrund , rB: hintergrund
						int r = rB <= 128 ? rA * rB/128 : 255-((255-rA)*(255-rB)/128);
                        int g = gB <= 128 ? gA * gB/128 : 255-((255-gA)*(255-gB)/128);
                        int b = bB <= 128 ? bA * bB/128 : 255-((255-bA)*(255-bB)/128);
                        pixels_Erg[pos] = 0xFF000000 + ((r & 0xff) << 16) + ((g & 0xff) << 8) + ( b & 0xff);
					}
					if (methode == 4) { // Overlay (B,A)
						//rB: vordergrund , rA: hintergrund
						int r = rA <= 128 ? rA * rB/128 : 255-((255-rB)*(255-rA)/128);
                        int g = gA <= 128 ? gA * gB/128 : 255-((255-gB)*(255-gA)/128);
                        int b = bA <= 128 ? bA * bB/128 : 255-((255-bB)*(255-bA)/128);
                        pixels_Erg[pos] = 0xFF000000 + ((r & 0xff) << 16) + ((g & 0xff) << 8) + ( b & 0xff);
					}
					if (methode == 5) { // Schieb-Blende
						
						int g = (int)((z-1)*(double)width/(length-1));
						
						if (x+1 > g )  
							pixels_Erg[pos] = pixels_B[pos-g];
						else
							pixels_Erg[pos] = pixels_A[pos];
					}
					if (methode == 6) { // Chroma Key
						int distance = 80;
						int colorDistance ;
						// choose the orange (200,150,60)
						 colorDistance =  (int) (Math.sqrt(Math.pow(200 - rA, 2) +Math.pow(150 - gA, 2) +Math.pow(60 - bA, 2))); 
						if (colorDistance < distance)
							pixels_Erg[pos] = pixels_B[pos];
						else
							pixels_Erg[pos] = pixels_A[pos]; 
					}
					if (methode == 7) { // eigene Uberblendung
						
						
						if ((x>50 && x< 100 )|| (y>50 && y<100) ) {
							pixels_Erg[pos] = pixels_A[pos]; 
						}
						else {
							pixels_Erg[pos] = pixels_B[pos]; 
						}
					}
					
				}
		}

		// neues Bild anzeigen
		Erg.show();
		Erg.updateAndDraw();

	}

}
