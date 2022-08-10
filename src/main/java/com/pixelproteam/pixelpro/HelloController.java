package com.pixelproteam.pixelpro;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
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
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.35f));
        g.drawImage(base, 0, 0, null);
        g.drawImage(overlay, 0, 0, null);
        g.dispose();
        StackMaintain();
        image = SwingFXUtils.toFXImage(combined, null);
        gamma();

    }
    
    // demo change

}