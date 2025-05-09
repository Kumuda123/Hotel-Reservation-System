package hotel;

import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;
import java.net.URL;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URL;
import javax.imageio.ImageIO;

public class CustomPanel extends JPanel {
    private Image backgroundImage;

    public CustomPanel() {
        try {
            // Load the image from the URL
            URL imageUrl = new URL("https://www.1hotels.com/sites/1hotels.com/files/styles/card/public/brandfolder/kkmmbmqpc8gmjtgffsgwnhw/RM655-01-_Ocean_Front_Kingh1320.png?h=22f6ab40&itok=AtB7dU9x");
            InputStream inputStream = imageUrl.openStream();
            backgroundImage = ImageIO.read(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setLayout(new BorderLayout());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Apply the blur effect to the background image
        if (backgroundImage != null) {
            BufferedImage blurredImage = blurImage(backgroundImage);
            g2d.drawImage(blurredImage, 0, 0, getWidth(), getHeight(), null);
        }
    }

    // Apply a blur effect to the image
    private BufferedImage blurImage(Image image) {
        int width = image.getWidth(null);
        int height = image.getHeight(null);
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bufferedImage.createGraphics();
        
        // Draw the image
        g2d.drawImage(image, 0, 0, null);
        
        g2d.dispose();

        return bufferedImage; 
    }
}
