public class RGB {
    private int r;
    private int g;
    private int b;
     
    public RGB(double r, double g, double b){
        this.setR((int)Math.round(r));
        this.setG((int)Math.round(g));
        this.setB((int)Math.round(b));
    }
     
    public RGB(int r, int g, int b){
        this.setR(r);
        this.setG(g);
        this.setB(b);
    }
     
    // Convert YUV value to RGB value
    public YUV RGB_to_YUV(){
        double Y= 0.299 * getR() + 0.587 * getG() + 0.114 * getB();
        double U = (getB() - Y) * 0.493;;
        double V = (getR() - Y) * 0.877;
         
        return new YUV(Y,U,V);
    }
    
    //Check for Underflow and Overflow
    public int checkValue(int x){
        int tmp=x;
        if(x<0){
            tmp=0;
        }
        if(x>255){
            tmp=255;
        }
         
        return tmp; 
    }

     
     
    public int getR() {
        return r;
    }
    public void setR(int r) {
        this.r = checkValue(r);
    }
    public int getG() {
        return g;
    }
    public void setG(int g) {
        this.g = checkValue(g);
    }
    public int getB() {
        return b;
    }
    public void setB(int b) {
        this.b = checkValue(b);
    }
}
