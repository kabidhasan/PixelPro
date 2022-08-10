package com.pixelproteam.pixelpro;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;


import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.*;
import java.util.Stack;


import javax.imageio.ImageIO;

import static com.sun.java.accessibility.util.AWTEventMonitor.addMouseListener;
import static com.sun.java.accessibility.util.AWTEventMonitor.addMouseMotionListener;
import static java.lang.Math.min;

public class HelloController {
    @FXML
    public BorderPane pane = new BorderPane();
    @FXML
    public ImageView imageView = new ImageView();

    @FXML
    public Slider brightnessSlider;

    @FXML
    public Slider contrastSlider, strokeSlider;
    @FXML
    public Label strokeSliderLabel;

    @FXML
    public MenuItem saveImageButton;

    @FXML
    public MenuItem saveImageAsButton;

    @FXML
    public Button undoButton;

    @FXML
    public Button redoButton;

    @FXML
    public Button cropButton;
    public Button DrawButton;

    @FXML
    public ColorPicker colorPicker;

    Image image, tempImage;


    boolean isImageOpened = false;
    float brightness = 0, contrast = 1;

    File selectedFile = null;

    Stack<Image> back = new Stack<>();
    Stack<Image> front = new Stack<>();


    public void StackMaintain() {
        back.push(image);
        undoButton.setDisable(false);

    }

    public void gamma() {
        front.clear();
        redoButton.setDisable(true);
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        RescaleOp op = new RescaleOp(contrast, brightness, null);
        bufferedImage = op.filter(bufferedImage, null);
        tempImage = SwingFXUtils.toFXImage(bufferedImage, null);
        imageView.setImage(tempImage);
        System.out.println("Gamma Called");
    }


    @FXML
    public void clickUndoButton() {
        front.push(image);
        redoButton.setDisable(false);
        image = back.pop();
        if (back.empty()) undoButton.setDisable(true);
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        RescaleOp op = new RescaleOp(contrast, brightness, null);
        bufferedImage = op.filter(bufferedImage, null);
        tempImage = SwingFXUtils.toFXImage(bufferedImage, null);
        imageView.setImage(tempImage);
    }

    @FXML
    public void clickRedoButton() {
        back.push(image);
        undoButton.setDisable(false);
        image = front.pop();
        if (front.empty()) redoButton.setDisable(true);
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        RescaleOp op = new RescaleOp(contrast, brightness, null);
        bufferedImage = op.filter(bufferedImage, null);
        tempImage = SwingFXUtils.toFXImage(bufferedImage, null);
        imageView.setImage(tempImage);
    }


    public static BufferedImage scale(BufferedImage src, int w, int h){
        int finalw = w;
        int finalh = h;
        double factor = 1.0d;
        if(src.getWidth() > src.getHeight()){
            factor = ((double)src.getHeight()/(double)src.getWidth());
            finalh = (int)(finalw * factor);
        }else{
            factor = ((double)src.getWidth()/(double)src.getHeight());
            finalw = (int)(finalh * factor);
        }

        BufferedImage resizedImg = new BufferedImage(finalw, finalh, BufferedImage.TRANSLUCENT);
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(src, 0, 0, finalw, finalh, null);
        g2.dispose();
        return resizedImg;
    }

    public static BufferedImage scale2(BufferedImage src, int w, int h) {
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
    public void clickOpenImageButton(ActionEvent e) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JPEG Files", "*.png", "*.jpg", "*.jpeg")
        );

        selectedFile = fileChooser.showOpenDialog(null);
        System.out.println(selectedFile.getAbsolutePath());
        Initializer();
        image = new Image(selectedFile.toURI().toString());
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        bufferedImage = scale(bufferedImage, (int) min(bufferedImage.getWidth(), imageView.getFitWidth()), (int) min(bufferedImage.getHeight(), imageView.getFitHeight()));
        image = SwingFXUtils.toFXImage(bufferedImage, null);
        tempImage = image;

        imageView.setImage(tempImage);

        isImageOpened = true;
    }

    public void Initializer() {
        saveImageButton.setDisable(false);
        saveImageAsButton.setDisable(false);
        brightnessSlider.setDisable(false);
        contrastSlider.setDisable(false);
        DrawButton.setDisable(false);
        colorPicker.setDisable(false);
        strokeSlider.setDisable(false);
        back.clear();
        front.clear();
        brightness = 0;
        contrast = 1;
        brightnessSlider.setValue(0);
        contrastSlider.setValue(1);
        image = tempImage = null;
    }

    public String getExtension(String fileName) {
        String extension = "";

        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i + 1);
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
                new FileChooser.ExtensionFilter("JPEG Files (.jpg, .jpeg)", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("PNG File (.png)", "*.png")
        );

        File saveFile = fileChooser.showSaveDialog(null);

        if (getExtension(saveFile.getName()) == "") {
            String extension = fileChooser.getSelectedExtensionFilter().getExtensions().get(0).substring(1);
            saveFile = new File(saveFile.getAbsolutePath() + extension);
        }

        boolean newlyCreated;
        try {
            newlyCreated = saveFile.createNewFile();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        if (newlyCreated == true) System.out.println("Created new file " + saveFile);

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
        if (!isImageOpened) {
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
        if (!isImageOpened) {
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
        if (!isImageOpened) {
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
    private void clickBrightened(ActionEvent e) {
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        contrast *= 1.3f;
        brightness += 50;
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


    @FXML
    private void adjustContrast() {
        contrastSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {

                contrast = (float) (contrastSlider.getValue());
                //System.out.println(newContrast);
                gamma();
            }
        });
    }


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

    private static BufferedImage rotateImageByDegrees(BufferedImage buffImage, double angle) {
        double radian = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(radian));
        double cos = Math.abs(Math.cos(radian));

        int width = buffImage.getWidth();
        int height = buffImage.getHeight();

        int nWidth = (int) Math.floor((double) width * cos + (double) height * sin);
        int nHeight = (int) Math.floor((double) height * cos + (double) width * sin);

        BufferedImage rotatedImage = new BufferedImage(
                nWidth, nHeight, BufferedImage.TYPE_INT_ARGB);

        Graphics2D graphics = rotatedImage.createGraphics();

        graphics.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        graphics.translate((nWidth - width) / 2, (nHeight - height) / 2);
        // rotation around the center point
        graphics.rotate(radian, (double) (width / 2), (double) (height / 2));
        graphics.drawImage(buffImage, 0, 0, null);
        graphics.dispose();

        return rotatedImage;
    }

    @FXML
    public void rotate90(ActionEvent e) {
        BufferedImage simg = SwingFXUtils.fromFXImage(image, null);
        BufferedImage rotated = rotateImageByDegrees(simg, 90);
        StackMaintain();
        image = SwingFXUtils.toFXImage(rotated, null);
        gamma();
    }

    @FXML
    public void rotate180(ActionEvent e) {
        BufferedImage simg = SwingFXUtils.fromFXImage(image, null);
        BufferedImage rotated = rotateImageByDegrees(simg, 180);
        StackMaintain();
        image = SwingFXUtils.toFXImage(rotated, null);
        gamma();
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
        base = scale(base, w, h);
        overlay = scale(overlay, w, h);
        BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = combined.createGraphics();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.35f));
        g.drawImage(base, 0, 0, null);
        g.drawImage(overlay, 0, 0, null);
        g.dispose();
        StackMaintain();
        image = SwingFXUtils.toFXImage(combined, null);
        gamma();

    }
    boolean isDragging = false;
    double startX, startY, endX, endY;

    @FXML
    Rectangle dragBox = new Rectangle(0, 0, 0, 0);

    @FXML
    public void onCropButton(){
        imageView.setOnMouseDragged(e->{
            if(!isDragging){
                startX = e.getX();
                startY = e.getY();
                isDragging = true;
//                System.out.println("Started Dragging: " + startX + " " + startY);
                dragBox.setVisible(true);
                dragBox.setX(imageView.getX() + startX);
                dragBox.setY(imageView.getY() + startY);

                dragBox.setOnMouseClicked(e2->{
                    dragBox.setVisible(false);
                    isDragging = false;
                    dragBox.setWidth(0);
                    dragBox.setHeight(0);
                });
            }
            else{
                endX = e.getX();
                endY = e.getY();
                if (endX > startX) {
                    dragBox.setWidth(endX - startX);
                } else {
                    dragBox.setWidth(startX - endX);
                    dragBox.setX(imageView.getX() + endX);
                }
                if (endY > startY) {
                    dragBox.setHeight(endY - startY);
                } else {
                    dragBox.setHeight(startY - endY);
                    dragBox.setY(imageView.getY() + endY);
                }
            }
        });

        imageView.setOnMouseReleased(e->{
            if(isDragging) {
                isDragging = false;

                endX = e.getX();
                endY = e.getY();

                if(startX > endX){
                    double temp = startX;
                    startX = endX;
                    endX = temp;
                }

                if(startY > endY){
                    double temp = startY;
                    startY = endY;
                    endY = temp;
                }

//                System.out.println("StartX: " + startX + " StartY: " + startY);
//                System.out.println("EndX: " + endX + " EndY: " + endY);

                endX = min(endX, image.getWidth());
                endY = min(endY, image.getHeight());

                System.out.println("EndX: " + endX + " EndY: " + endY);


                dragBox.setX(startX + imageView.getX());
                dragBox.setY(startY + imageView.getY());
                dragBox.setWidth(endX - startX);
                dragBox.setHeight(endY - startY);

//                System.out.println("Released: " + endX + " " + endY);
//                System.out.println("ImageSize: " + imageWidth + " " + imageHeight);


//                TIME TO CROP THE IMAGE

                PixelReader pr = image.getPixelReader();
                WritableImage croppedImage = new WritableImage(pr, (int) startX, (int) startY, (int) (endX - startX), (int) (endY - startY));
                StackMaintain();
                image = croppedImage;
                gamma();
                dragBox.setVisible(false);

//                remove all the listeners
                imageView.setOnMouseDragged(null);
                imageView.setOnMouseReleased(null);
                imageView.setOnMouseClicked(null);
            }
        });

        imageView.setOnMousePressed(e->{
            if(dragBox.isVisible()){
                dragBox.setVisible(false);
                dragBox.setWidth(0);
                dragBox.setHeight(0);
            }
        });
    }

    @FXML
    public void clickDrawButton() {
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        javafx.scene.canvas.Canvas canvas = new Canvas(bufferedImage.getWidth(), bufferedImage.getHeight());
        GraphicsContext gc1 = canvas.getGraphicsContext2D();
        gc1.setStroke(colorPicker.getValue());
        gc1.setLineWidth(1);
        if (!DrawButton.isUnderline()) {

            DrawButton.setUnderline(true);
            imageView.setOnMousePressed(e -> {
                if (!DrawButton.isUnderline()) return;
                StackMaintain();
                gc1.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                gc1.drawImage(image, 0, 0, image.getWidth(), image.getHeight());
                gc1.setLineWidth(strokeSlider.getValue());
                gc1.setStroke(colorPicker.getValue());
                gc1.beginPath();
                gc1.lineTo(e.getX(), e.getY());
                gc1.stroke();
            });

            imageView.setOnMouseDragged(e -> {
                if (!DrawButton.isUnderline()) return;
                System.out.println("Pressed: " + e.getX() + " " + e.getY());
                gc1.lineTo(e.getX(), e.getY());
                gc1.stroke();
                WritableImage wim = canvas.snapshot(null, null);
                BufferedImage bufferedImage1 = SwingFXUtils.fromFXImage(wim, null);
                image = SwingFXUtils.toFXImage(bufferedImage1, null);


                gamma();


            });


        } else {
            DrawButton.setUnderline(false);
            gc1.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            return;
        }
    }
}