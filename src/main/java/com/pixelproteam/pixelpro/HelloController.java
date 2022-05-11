package com.pixelproteam.pixelpro;

import com.sun.glass.ui.CommonDialogs;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class HelloController {
    @FXML
    public BorderPane pane = new BorderPane();
    @FXML
    public ImageView imageView = new ImageView();
    Image image;


    boolean isImageOpened = false;


    @FXML
    public void clickOpenImageButton(ActionEvent e) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JPEG Files", "*.png", "*.jpg", "*.jpeg")
        );
        File selectedFile = fileChooser.showOpenDialog(null);
        System.out.println(selectedFile.getAbsolutePath());

        image = new Image(selectedFile.toURI().toString());
        imageView.setImage(image);
        isImageOpened = true;

        imageView.fitWidthProperty().bind(pane.widthProperty());
        imageView.fitHeightProperty().bind(pane.heightProperty());
    }

//    public static BufferedImage toBufferedImage(Image img) {
//
//
//        // Create a buffered image with transparency
//        BufferedImage bimage = new BufferedImage((int)img.getWidth(), (int)img.getHeight(), BufferedImage.TYPE_INT_ARGB);
//
//        // Draw the image on to the buffered image
//        Graphics2D bGr = bimage.createGraphics();
//        bGr.drawImage(img, 0, 0, null);
//        bGr.dispose();
//
//        // Return the buffered image
//        return bimage;
//    }

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
        System.out.println("Success");
        image = SwingFXUtils.toFXImage(bufferedImage,null);
        imageView.setImage(image);
    }
}