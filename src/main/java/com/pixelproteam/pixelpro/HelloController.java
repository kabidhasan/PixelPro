package com.pixelproteam.pixelpro;

import com.sun.glass.ui.CommonDialogs;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.*;
import java.util.Optional;


import org.opencv.core.Core;
import org.opencv.core.*;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgcodecs.Imgcodecs.*;
import org.opencv.highgui.HighGui;

import javax.imageio.ImageIO;

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

    Image image;


    boolean isImageOpened = false;
    float brightness=0, contrast=1;

    File selectedFile = null;


    public void gamma (){
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        RescaleOp op = new RescaleOp(contrast,brightness,null);
        bufferedImage = op.filter(bufferedImage, null);
        Image image = SwingFXUtils.toFXImage(bufferedImage, null);
        imageView.setImage(image);
    }

    public void setGamma (){
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        RescaleOp op = new RescaleOp(contrast,brightness,null);
        bufferedImage = op.filter(bufferedImage, null);
        Image image = SwingFXUtils.toFXImage(bufferedImage, null);
        imageView.setImage(image);
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
        imageView.setImage(image);

        isImageOpened = true;

        imageView.fitWidthProperty().bind(pane.widthProperty());
        imageView.fitHeightProperty().bind(pane.heightProperty());

        saveImageButton.setDisable(false);
        saveImageAsButton.setDisable(false);
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

//        if(getExtension(saveFile.getName()) == "") {
//            String extension = fileChooser.getSelectedExtensionFilter().getExtensions().get(0).substring(1);
//            saveFile = new File(saveFile.getAbsolutePath() + extension);
//        }

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
        image = SwingFXUtils.toFXImage(bufferedImage, null);
        setGamma();
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
        image = SwingFXUtils.toFXImage(bufferedImage, null);
        setGamma();
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
        image = SwingFXUtils.toFXImage(bufferedImage, null);
        setGamma();

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
        setGamma();
    }



    @FXML
    private void adjustBrightness(){
        brightnessSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                brightness = (float) brightnessSlider.getValue();
                gamma();
            }
        });
    }

    @FXML
    private void adjustContrast(){
        contrastSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                contrast = (float) contrastSlider.getValue();
                gamma();
            }
        });
    }







}