public class YUV {
    private double y;
    private double u;
    private double v;
    
    public YUV() {
        // TODO Auto-generated constructor stub
    }
     
    public YUV(double y,double u, double v){
        this.setY(y);
        this.setU(u);
        this.setV(v);
    }
     //Y – luminance (brightness)

    public YUV changeBrightness(double value){
         
        this.setY(getY()+value);
        return this;
    }
     
    public YUV changeContrast(double value){
    	
         
        this.setY(value*(getY()-128) + 128);
       
         
        return this;
    }
     
    public YUV changeSaturation(double value){
    	
         
        
        this.setU(getU()*value);
        this.setV(getV()*value);
         
        return this;
    }
     
    public YUV changeHue(double value){
    	
         
       // tmp.setY(getY());
        this.setU(getU()* ( Math.cos(Math.toRadians(value)) - Math.sin(Math.toRadians(value))));
        this.setV(getV()* ( Math.sin(Math.toRadians(value)) + Math.cos(Math.toRadians(value))));
         
        return this;
    }
 
    // Convert YUV value to RGB value
    public RGB YUV_to_RGB(){
        int r=(int)(getY()  + getV()/0.877);
        int g=(int)(getY() - 0.395*getU() - 0.581 * getV());
        int b= (int)( getY() + getU()/0.493);
         
        return new RGB(r,g,b);
    }
     
    public double getY() {
        return y;
    }
    public void setY(double y) {
        this.y = y;
    }
    public double getU() {
        return u;
    }
    public void setU(double u) {
        this.u = u;
    }
    public double getV() {
        return v;
    }
    public void setV(double v) {
        this.v = v;
    }
     
}