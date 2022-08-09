package com.pixelproteam.pixelpro;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.*;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicReference;

import javax.imageio.ImageIO;

import static java.lang.Math.min;

public class HelloController {
    @FXML
    public BorderPane pane = new BorderPane();
    @FXML
    public ImageView imageView = new ImageView();

    @FXML
    public Slider brightnessSlider;

    @FXML
    public Slider contrastSlider;

    @FXML
    public MenuItem saveImageButton;

    @FXML
    public MenuItem saveImageAsButton;

    @FXML
    public Button undoButton;

    @FXML
    public Button redoButton;

    @FXML
    public Button DragTestButton;

    Image image, tempImage;


    boolean isImageOpened = false;
    float brightness=0, contrast=1;
    float newBrightness=0, newContrast=1;
    float prevBrightness=0, prevContrast=1;

    File selectedFile = null;

    Stack<Image> back = new Stack<>();
    Stack<Image> front = new Stack<>();

    Stack<Float> backContrast = new Stack<>();
    Stack<Float> frontContrast = new Stack<>();

    Stack<Float> backBrightness = new Stack<>();
    Stack<Float> frontBrightness = new Stack<>();


    public void StackMaintain (){
        back.push(image);
//        backContrast.push(prevContrast);
//        backBrightness.push(prevBrightness);
        undoButton.setDisable(false);
        //imageView.setImage(image);
    }
    public void gamma (){
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        RescaleOp op = new RescaleOp(contrast,brightness,null);
        bufferedImage = op.filter(bufferedImage, null);
        tempImage = SwingFXUtils.toFXImage(bufferedImage, null);
        imageView.setImage(tempImage);
        System.out.println("Gamma Called");

    }

    public void setGamma (){
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        RescaleOp op = new RescaleOp(contrast,brightness,null);
        bufferedImage = op.filter(bufferedImage, null);
        image = SwingFXUtils.toFXImage(bufferedImage, null);
        back.push(imageView.getImage());
        undoButton.setDisable(false);
        imageView.setImage(image);

    }


    @FXML
    public void clickUndoButton(){
        front.push(image);
//        frontContrast.push(newContrast);
//        frontBrightness.push(newBrightness);
        redoButton.setDisable(false);
        image = back.pop();
//        contrast = backContrast.pop();
//        brightness = backBrightness.pop();
//        contrastSlider.setValue(contrast);
//        brightnessSlider.setValue(brightness);
        if(back.empty()) undoButton.setDisable(true);
        gamma();
    }
    @FXML
    public void clickRedoButton(){
        back.push(image);
        //backContrast.push(newContrast);
        //backBrightness.push(newBrightness);
        undoButton.setDisable(false);
        image = front.pop();
//        contrast = frontContrast.pop();
//        brightness =frontBrightness.pop();
//        contrastSlider.setValue(contrast);
//        brightnessSlider.setValue(brightness);
        if(front.empty())redoButton.setDisable(true);
        gamma();
    }
    @FXML
    public void clickOpenImageButton(ActionEvent e) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JPEG Files", "*.png", "*.jpg", "*.jpeg")
        );

        selectedFile = fileChooser.showOpenDialog(null);
        System.out.println(selectedFile.getAbsolutePath());

        image = new Image(selectedFile.toURI().toString());
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image,null);
        bufferedImage= scale(bufferedImage, (int) min(bufferedImage.getWidth(),imageView.getFitWidth() ), (int) min(bufferedImage.getHeight(),imageView.getFitHeight()));
        image = SwingFXUtils.toFXImage(bufferedImage,null);
        tempImage = image;

        imageView.setImage(tempImage);

        isImageOpened = true;

//        imageView.fitWidthProperty().bind(pane.widthProperty());
//        imageView.fitHeightProperty().bind(pane.heightProperty());


        saveImageButton.setDisable(false);
        saveImageAsButton.setDisable(false);
        brightnessSlider.setDisable(false);
        contrastSlider.setDisable(false);
    }

    public String getExtension(String fileName) {
        String extension = "";

        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i+1);
        }
        return extension;
    }

    @FXML
    public void clickSaveImageButton(ActionEvent e) {
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(imageView.getImage(), null);

        try {
            ImageIO.write(bufferedImage, getExtension(selectedFile.getName()), selectedFile);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        System.out.println("Saved updated image to " + selectedFile);
    }

    @FXML
    public void clickSaveImageAsButton(ActionEvent e) {
        System.out.println("Clicked Save Image as Button");
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JPEG Files (.jpg, .jpeg)",  "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("PNG File (.png)", "*.png")
        );

        File saveFile = fileChooser.showSaveDialog(null);

        if(getExtension(saveFile.getName()) == "") {
            String extension = fileChooser.getSelectedExtensionFilter().getExtensions().get(0).substring(1);
            saveFile = new File(saveFile.getAbsolutePath() + extension);
        }

        boolean newlyCreated;
        try {
            newlyCreated = saveFile.createNewFile();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        if(newlyCreated == true) System.out.println("Created new file " + saveFile);

        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(imageView.getImage(), null);

        try {
            ImageIO.write(bufferedImage, getExtension(saveFile.getName()), saveFile);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        System.out.println("Saved updated image to " + saveFile);
    }

    @FXML
    private void ClickGrayscaleFilter(ActionEvent e) {
        if (isImageOpened == false) {
            return;
        }
        int height = (int) image.getHeight();
        int width = (int) image.getWidth();
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                // Here (x,y)denotes the coordinate of image
                // for modifying the pixel value.
                int p = bufferedImage.getRGB(x, y);

                int a = (p >> 24) & 0xff;
                int r = (p >> 16) & 0xff;
                int g = (p >> 8) & 0xff;
                int b = p & 0xff;

                // calculate average
                int avg = (r + g + b) / 3;

                // replace RGB value with avg
                p = (a << 24) | (avg << 16) | (avg << 8)
                        | avg;

                bufferedImage.setRGB(x, y, p);
            }
        }
        System.out.println("Success1");
        StackMaintain();
        image = SwingFXUtils.toFXImage(bufferedImage, null);
        gamma();

    }

    @FXML
    private void clickSepiaFilter(ActionEvent e) {
        if (isImageOpened == false) {
            return;
        }
        int height = (int) image.getHeight();
        int width = (int) image.getWidth();
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int p = bufferedImage.getRGB(x, y);

                int a = (p >> 24) & 0xff;
                int R = (p >> 16) & 0xff;
                int G = (p >> 8) & 0xff;
                int B = p & 0xff;

                int newRed = (int) (0.393 * R + 0.769 * G
                        + 0.189 * B);
                int newGreen = (int) (0.349 * R + 0.686 * G
                        + 0.168 * B);
                int newBlue = (int) (0.272 * R + 0.534 * G
                        + 0.131 * B);

                if (newRed > 255)
                    R = 255;
                else
                    R = newRed;

                if (newGreen > 255)
                    G = 255;
                else
                    G = newGreen;

                if (newBlue > 255)
                    B = 255;
                else
                    B = newBlue;

                p = (a << 24) | (R << 16) | (G << 8) | B;

                bufferedImage.setRGB(x, y, p);
            }
        }
        System.out.println("Success2");
        StackMaintain();
        image = SwingFXUtils.toFXImage(bufferedImage, null);
        gamma();

    }

    @FXML
    private void clickDeemedFilter(ActionEvent e) {
        if (isImageOpened == false) {
            return;
        }
        int height = (int) image.getHeight();
        int width = (int) image.getWidth();
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int p = bufferedImage.getRGB(x, y);
                int a = (p >> 24) & 0xff;
                int R = (p >> 16) & 0xff;
                int G = (p >> 8) & 0xff;
                int B = p & 0xff;

                int newRed = (int) (1 * R);
                int newGreen = (int) (0.349 * R + 0.686 * G
                        + 0.168 * B);
                int newBlue = (int) (0.272 * R + 0.534 * G
                        + 0.131 * B);

                if (newRed > 255)
                    R = 255;
                else
                    R = newRed;

                if (newGreen > 255)
                    G = 255;
                else
                    G = newGreen;

                if (newBlue > 255)
                    B = 255;
                else
                    B = newBlue;

                p = (a << 24) | (R << 16) | (G << 8) | B;

                bufferedImage.setRGB(x, y, p);
            }
        }
        System.out.println("Success3");
        StackMaintain();
        image = SwingFXUtils.toFXImage(bufferedImage, null);
        gamma();

    }


    @FXML
    private  void clickBrightened(ActionEvent e){
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        contrast *= 1.3f;
        brightness+= 50;
//        RescaleOp op = new RescaleOp(contrast,brightness,null);
//        bufferedImage = op.filter(bufferedImage, null);
//        image = SwingFXUtils.toFXImage(bufferedImage, null);
//        imageView.setImage(image);
        StackMaintain();
        image = SwingFXUtils.toFXImage(bufferedImage, null);
        imageView.setImage(image);
    }



    @FXML
    private void adjustBrightness(){
        brightnessSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                brightness = (float) (brightnessSlider.getValue());
                gamma();
            }
        });
    }

    //    @FXML
//    private void setBrightness(){
//        brightness= (float) (newBrightness - prevBrightness);
//        System.out.println("Released " + " "+ prevBrightness+ " "+ newBrightness+ " "+ brightness);
//        prevBrightness = newBrightness;
//
//        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
//        RescaleOp op = new RescaleOp(1,brightness,null);
//        bufferedImage = op.filter(bufferedImage, null);
//        //contrastSlider.setValue(1.00);
//        image = SwingFXUtils.toFXImage(bufferedImage, null);
//        imageView.setImage(image);
//
//    }
    @FXML
    private void adjustContrast(){
        contrastSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {

                contrast = (float) (contrastSlider.getValue());
                //System.out.println(newContrast);
                gamma();
            }
        });
    }
//    float mul = 1;
//    @FXML
//    private void setContrast(){
//        contrast= (float) (newContrast/ prevContrast);
//        prevContrast = newContrast;
//        //mul*= contrast;
//        System.out.println("Released " + " "+ prevContrast+ " "+ newContrast+ " "+ contrast+ " "+ mul);
//        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
//        RescaleOp op = new RescaleOp(contrast, 0,null);
//        bufferedImage = op.filter(bufferedImage, null);
//        image = SwingFXUtils.toFXImage(bufferedImage, null);
//        imageView.setImage(image);
//
//    }



//    EDIT

    @FXML
    public void mirrorHorizontal(ActionEvent e) {

        BufferedImage simg = SwingFXUtils.fromFXImage(image, null);

        int width = simg.getWidth();
        int height = simg.getHeight();

        // BufferedImage for mirror image
        BufferedImage mimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        // Create mirror image pixel by pixel
        for (int y = 0; y < height; y++) {
            for (int lx = 0, rx = width - 1; lx < width; lx++, rx--) {

                // lx starts from the left side of the image
                // rx starts from the right side of the
                // image lx is used since we are getting
                // pixel from left side rx is used to set
                // from right side get source pixel value
                int p = simg.getRGB(lx, y);

                // set mirror image pixel value
                mimg.setRGB(rx, y, p);
            }
        }
        StackMaintain();
        image = SwingFXUtils.toFXImage(mimg, null);
        gamma();

    }

    @FXML
    public void mirrorVertical(ActionEvent e) {

        BufferedImage simg = SwingFXUtils.fromFXImage(image, null);

        int width = simg.getWidth();
        int height = simg.getHeight();

        // BufferedImage for mirror image
        BufferedImage mimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        // Create mirror image pixel by pixel
        for (int x = 0; x < width; x++) {
            for (int ly = 0, ry = height - 1; ly < height; ly++, ry--) {

                // lx starts from the left side of the image
                // rx starts from the right side of the
                // image lx is used since we are getting
                // pixel from left side rx is used to set
                // from right side get source pixel value
                int p = simg.getRGB(x, ly);

                // set mirror image pixel value
                mimg.setRGB(x, ry, p);
            }
        }
        StackMaintain();
        image = SwingFXUtils.toFXImage(mimg, null);
        gamma();


    }

    @FXML
    public void rotate90(ActionEvent e) {
        imageView.setRotate(imageView.getRotate() + 90.0);

    }

    @FXML
    public void rotate180(ActionEvent e) {
        imageView.setRotate(imageView.getRotate() + 180.0);

    }

    @FXML
    public void rotateCustom(ActionEvent e) {
        TextInputDialog inputDialog = new TextInputDialog("0.00");
        inputDialog.setContentText("Rotation Angle: ");
        inputDialog.setTitle("Custom Rotation");
        inputDialog.setHeaderText("Rotate by angle");

        inputDialog.showAndWait();
        double angle = Double.parseDouble(inputDialog.getResult());

        imageView.setRotate(angle);
    }

    public static BufferedImage scale(BufferedImage src, int w, int h)
    {
        BufferedImage img =
                new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        int x, y;
        int ww = src.getWidth();
        int hh = src.getHeight();
        int[] ys = new int[h];
        for (y = 0; y < h; y++)
            ys[y] = y * hh / h;
        for (x = 0; x < w; x++) {
            int newX = x * ww / w;
            for (y = 0; y < h; y++) {
                int col = src.getRGB(newX, ys[y]);
                img.setRGB(x, y, col);
            }
        }
        return img;
    }
    @FXML
    public void blend(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JPEG Files", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(null);
        System.out.println(selectedFile.getAbsolutePath());
        Image second = new Image(selectedFile.toURI().toString());
        BufferedImage base = SwingFXUtils.fromFXImage(image, null);
        BufferedImage overlay = SwingFXUtils.fromFXImage(second, null);
        int w = Math.max(base.getWidth(), overlay.getWidth());
        int h = Math.max(base.getHeight(), overlay.getHeight());
        base = scale(base,w,h);
        overlay = scale(overlay,w,h);
        BufferedImage combined = new BufferedImage(w, h,BufferedImage.TYPE_INT_RGB);
        Graphics2D g = combined.createGraphics();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        g.drawImage(base, 0, 0, null);
        g.drawImage(overlay, 0, 0, null);
        g.dispose();
        StackMaintain();
        image = SwingFXUtils.toFXImage(combined, null);
        gamma();

    }
    int isDragDone=0;
    @FXML
    public void onClickDragTestButton(){
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(tempImage,null);
        Canvas canvas = new Canvas(bufferedImage.getWidth(),bufferedImage.getHeight());
        GraphicsContext gc1 = canvas.getGraphicsContext2D();
        gc1.drawImage(tempImage,0,0,bufferedImage.getWidth(), bufferedImage.getHeight());
        gc1.setStroke(Color.BLACK);
        gc1.setLineWidth(1);
        imageView.setOnMousePressed(e->{
            gc1.beginPath();
            gc1.lineTo(e.getX(),e.getY());
            gc1.stroke();
        });
        imageView.setOnDragDetected(e->{
            double heightOfWindow = imageView.getScene().getWindow().getHeight();
            double widthOfWindow = imageView.getScene().getWindow().getWidth();
            //double resizedImagesize = image.getWidth()/heightOfWindow *
            if(e.getX()<= image.getWidth() && e.getY()<=image.getHeight()){
                System.out.println("Start: "+e.getX()+" "+e.getY());
                isDragDone=1;
            }

        });
        imageView.setOnMouseDragged(e->{
            double px = e.getX();
            double py = e.getY();
            System.out.println("Pressed: "+e.getX()+" "+e.getY());
            gc1.lineTo(e.getX(),e.getY());
            gc1.stroke();
            WritableImage wim = canvas.snapshot(null, null);
            BufferedImage bufferedImage1= SwingFXUtils.fromFXImage(wim,null);
            tempImage= SwingFXUtils.toFXImage(bufferedImage1,null);
            imageView.setImage(tempImage);

        });
        AtomicReference<Double> x = new AtomicReference<>((double) 0);
        AtomicReference<Double> y = new AtomicReference<>((double) 0);
        imageView.setOnMouseReleased(e->{
            if(isDragDone==1){
                if(e.getX()<= image.getWidth() && e.getY()<=image.getHeight()) {
                    x.set(e.getX());
                    y.set(e.getY());
                }
                else{
                    x.set(image.getWidth());
                    y.set(image.getHeight());
                }
                System.out.println("End: "+e.getX()+" "+e.getY());
            }
            System.out.println("Image Pixels: "+imageView.getImage().getWidth()+" "+imageView.getImage().getHeight());

            double heightOfWindow = imageView.getScene().getWindow().getHeight();
            double widthOfWindow = imageView.getScene().getWindow().getWidth();
            System.out.println("Image Pixels: "+ widthOfWindow +" "+heightOfWindow);
            isDragDone=0;

        });
        //double a = Scene.getHeight();


    }



}