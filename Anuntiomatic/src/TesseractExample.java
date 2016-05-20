import java.awt.AWTException;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import javax.imageio.ImageIO;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;



public class TesseractExample {
    public static Tesseract instance;
    public static FirefoxDriver driver; 
    private static final String rutaFirefox="C:\\Program Files\\Mozilla Firefox\\firefox.exe";
    public static final String nusuario="Dmgito";//"JizzTaste";
    public static final String cusuario="Andorra775";//"AmrTaste2814";   //Adam=//"Mqz4B0#Wh1";//
    public static WebDriverWait wait;
    public static int anuncios;
    public static double bonos=0; 
    public static boolean username=false;
    public static boolean captcha=false;
    public static int errores=0;
    public static String magicnumber = null;
    public static boolean imagen=false;
    public static boolean cobro=false;
    public static boolean r;
    public static int intentoscobro;
    public static double bonosactuales=0;
    
    public static boolean DownloadImage(By by,String loc) throws IOException{
	 WebElement Image=null;
         BufferedImage dest=null;
        try{
            Image=driver.findElement(by);            
        }catch(Exception f){return false;}
	File screen=((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        int width=Image.getSize().getWidth();
        int height=Image.getSize().getHeight();
        BufferedImage img=ImageIO.read(screen);
        try{
            dest=img.getSubimage(Image.getLocation().getX(), Image.getLocation().getY(), width, height);
        }catch(Exception alguna){return false;}
        ImageIO.write(dest, "png", screen);
        File file=new File(loc);
        FileUtils.copyFile(screen,file);
        return true;
    }
   
    public static void resize(String inputImagePath,
            String outputImagePath, int scaledWidth, int scaledHeight)
            throws IOException {
        // reads input image
        File inputFile = new File(inputImagePath);
        BufferedImage inputImage = ImageIO.read(inputFile);
 
        // creates output image
        BufferedImage outputImage = new BufferedImage(scaledWidth,
                scaledHeight, inputImage.getType());
 
        // scales the input image to the output image
        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(inputImage, 0, 0, scaledWidth, scaledHeight, null);
        g2d.dispose();
 
        // extracts extension of output file
        String formatName = outputImagePath.substring(outputImagePath
                .lastIndexOf(".") + 1);
 
        // writes to output file
        ImageIO.write(outputImage, formatName, new File(outputImagePath));
    }
    
    public static void resize(String inputImagePath,String outputImagePath, double percent) throws IOException {
        File inputFile = new File(inputImagePath);
        BufferedImage inputImage = ImageIO.read(inputFile);
        int scaledWidth = (int) (inputImage.getWidth() * percent);
        int scaledHeight = (int) (inputImage.getHeight() * percent);
        resize(inputImagePath, outputImagePath, scaledWidth, scaledHeight);
    }
        
    public static void Loguear() throws InterruptedException{
        do{
                          
        driver.get("http://backoffice.anuntiomatic.com/index.php");
       
        
       try{
           wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
           username=true;
           
       }catch(TimeoutException e){
           System.out.println("Error 1.");
           driver.navigate().refresh();
           username=false;
       }
        }while(!username);
        
         
        //Login
        driver.findElement(By.id("username")).sendKeys(nusuario);
        Thread.sleep(250);
        driver.findElement(By.id("password")).sendKeys(cusuario);
        Thread.sleep(500);
        driver.findElement(By.xpath("/html/body/div[1]/div/div[2]/div/form/fieldset/p[1]/button")).click();
        Thread.sleep(3500);
        
    }
    
    public static boolean Captchear() throws IOException, InterruptedException{
        if(!DownloadImage(By.xpath("//*[@id=\"content\"]/div/div/div[2]/form/div[9]/label/strong/img"),"C:\\tmp\\img.png"))return false;
       
        
                
        BufferedImage imageActual=ImageIO.read(new File("C:\\tmp\\img.png"));

        int mediaPixel,colorSRGB;
        Color colorAux;        
        for( int i = 0; i < imageActual.getWidth(); i++ ){
             for( int j = 0; j < imageActual.getHeight(); j++ ){
                   //Almacenamos el color del píxel
                   colorAux=new Color(imageActual.getRGB(i, j));
                   if(colorAux.getRed()+colorAux.getGreen()+colorAux.getBlue()!=63){
                            imageActual.setRGB(i, j, 0);
                        }                

             }
        }
        

                ImageIO.write(imageActual, "png", new File("C:\\tmp\\procesada.png"));
                
                                
                resize("C:\\tmp\\procesada.png", "C:\\tmp\\ampliada.png", 2.5);
                
                

                BufferedImage ampliada = ImageIO.read(new File("C:\\tmp\\ampliada.png"));

                
                
                
                try {
                    magicnumber = instance.doOCR(ampliada);
                    
                } catch (TesseractException ex) {
                    System.out.println("Error 2");
                    magicnumber="7578";
                }
               
                
                
                try{                    
                    driver.findElement(By.id("respuesta")).sendKeys(magicnumber+Keys.ENTER);
                   }catch(Exception f){
                    System.out.println("Error 3");
                    return false;
                }
               Thread.sleep(1500);
                
                try{
                    driver.findElementByClassName("alert-error");
                    //System.out.println("Error en Captcha!!");
                    errores++;
                    return false;
                    }catch(NoSuchElementException f){
                       
                    return true;
                }
    }
    
    public static void EncontrarImagen() throws InterruptedException{
        try{
                    driver.findElement(By.xpath("//*[@id=\"content\"]/div/div/div[2]/form/div[9]/label/strong/img"));
                    imagen=true;
                }catch(Exception p){
                    System.out.println("No se encuentra la imagen del Captcha");
                    driver.navigate().refresh();
                    Thread.sleep(30000);
                    imagen=false;
                }
    }
    
    public static void cambiarVentana() throws NoSuchWindowException{
       try {
           Set handles = driver.getWindowHandles();
           String current = driver.getWindowHandle();
           handles.remove(current);
           String newTab = (String) handles.iterator().next();
           driver.switchTo().window(newTab);
        } catch( Exception e ) {
           System.out.println("Error Ventana");
        }
}
    
    public static void cerrarVentana(){
     
    cambiarVentana();
    
    String base = driver.getWindowHandle();

    Set <String> set = driver.getWindowHandles();

    set.remove(base);
    assert set.size()==1;

    driver.switchTo().window((String) set.toArray()[0]);

    driver.close();
    driver.switchTo().window(base);
    }
    
    public static void Anuncio() throws IOException, InterruptedException{
        if(driver.getCurrentUrl().equals("http://backoffice.anuntiomatic.com/index.php")){ Loguear();}
        
        driver.get("http://backoffice.anuntiomatic.com/publicar.php?op=1");
        imagen=false;
        Thread.sleep(2500);
        EncontrarImagen();          
                
        if(imagen){
                    do{
                       r = Captchear();
                       Thread.sleep(3000);
                    }while(!r);
                    
                }
      
        WebElement enlaceaver=null;
                try
                {
                    Thread.sleep(2500);
                    enlaceaver = driver.findElement(By.partialLinkText("ad/view/"));
                    enlaceaver.click();
                    anuncios++;                   
                }catch(Exception ff){
                    System.out.println("No se ha generado link para el anuncio.");
                    if(anuncios==2)anuncios--;}
                
        
               
    }


    
    public static void main(String[] args) throws InterruptedException, IOException, AWTException {
        
       
        
       instance = Tesseract.getInstance();
                                
       instance.setTessVariable("tessedit_char_whitelist","0123456789");
                
       instance.setPageSegMode(8);
        
        
        File pathToBinary = new File(TesseractExample.rutaFirefox);
        FirefoxBinary ffBinary = new FirefoxBinary(pathToBinary);
        FirefoxProfile firefoxProfile = new FirefoxProfile();
        String OpeninNewTab = Keys.chord(Keys.CONTROL,Keys.RETURN);
       
      
        System.loadLibrary("liblept168");
        System.loadLibrary("libtesseract302");
       
       driver = new FirefoxDriver(ffBinary,firefoxProfile);
       wait = new WebDriverWait(driver, 80);
      
       driver.manage().window().maximize(); 
       
       
        
         
        long start = System.currentTimeMillis();
        
        int limite=10000;
        
        System.out.println("Limite: "+limite);
          
        
        boolean Loggued=true;
        do{
            
        
        try{
                    Loguear();
                    driver.get("http://backoffice.anuntiomatic.com/admin.php");
                    //bonos=Double.parseDouble(driver.findElementByXPath("//*[@id=\"content\"]/div/div/div[2]/p[4]/strong").getText());
                    //System.out.println("Bonos: "+bonos);
                   }catch(Exception q){
                    q.printStackTrace();
                    Loggued=false;
                }
        }while(!Loggued);
        
                
        int anunciostotales=0;
        while(true){
            
            if(driver.getWindowHandles().size()>3){
                System.out.println("Limite de ventanas");
                System.out.println("Ventanas Actuales: "+driver.getWindowHandles().size());
                for(int ww=0;ww<driver.getWindowHandles().size()-1;ww++){
                System.out.println("Comprobando ventana: "+driver.getCurrentUrl());
               
                
                if(driver.getCurrentUrl().contains("veranuncio.php")||driver.getCurrentUrl().contains("publicar.php")||driver.getCurrentUrl().contains("admin.php")) {
                        System.out.println("Ventana "+(ww+1)+" OK!");
                    }
                    else{                       
                        System.out.println("Ventana Inválida: "+driver.getCurrentUrl());
                        cerrarVentana();
                    }
                 cambiarVentana();
            }
            }
            
                                    
            if(anuncios<1)
            {
                for(int rto=anuncios;rto<2;rto++){
                     Anuncio();                    
                }
            }
            if(anuncios>=1){
                boolean deslog=false;
                for(int jaja=0;jaja<driver.getWindowHandles().size()-1;jaja++){                
                    cambiarVentana();
                    
                    if(driver.getCurrentUrl().equals("http://backoffice.anuntiomatic.com/index.php")){
                            cerrarVentana();
                            anuncios--;
                            deslog=true;
                        }                  
                     
                                     
                    if(!deslog){
                                        
                    try{
                       
                        
                        wait.until(ExpectedConditions.presenceOfElementLocated(By.partialLinkText("Pulse aqui para generar")));
                    
                   
                    WebElement cobro=null;
                    try{
                        cobro = driver.findElement(By.partialLinkText("Pulse aqui para generar"));
                        cobro.click();
                    }catch(Exception kers){System.out.println("No se cobró.");bonosactuales-=2.16;}
                    System.out.println(driver.getCurrentUrl());
                    if(driver.getCurrentUrl().contains("adf.ly")){
                        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("skip_ad_button")));
                        driver.findElement(By.id("skip_ad_button")).click();
                        System.out.println("Cobrando Adf.ly");
                        Thread.sleep(3500);
                    }
                    else if(driver.getCurrentUrl().contains("linkbucks")){
                        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("skip_link")));
                        driver.findElement(By.id("skiplink")).click();
                        System.out.println("Cobrando Linkbucks");
                        Thread.sleep(3500);
                    }
                    else if(driver.getCurrentUrl().contains("adfoc.us")){
                        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("skip")));
                        //driver.findElementByClassName("skip").click();
                        System.out.println("Cobrando adfocus");
                        //Thread.sleep(3500);
                    }
                    else if(driver.getCurrentUrl().contains("generarbono.php")){
                         System.out.println("Cobro normal"); 
                         Thread.sleep(3500);
                    }
                    else{
                        System.out.println("No se reconoce la ventana");
                        bonosactuales-=2.16;    
                    }
                    cerrarVentana();
                    anunciostotales++;
                    anuncios--; 
                    bonosactuales+=2.16;
                    long end = System.currentTimeMillis();
                    long res = end - start;
                    long segundos=res/1000;
                    long am = anunciostotales/(segundos*60);
                    System.out.println("B: "+bonosactuales+" B/min: "+bonosactuales/(segundos/60)+" T: "+segundos/60+" minutos. Anuncios: "+anunciostotales);
                    }catch(Exception drs){
                        System.out.println("Error DRS");
                        drs.printStackTrace();
                        
                        
                    }
                }
                }
            } 
           
                
        }
        
        
                
        
    }
    
}
