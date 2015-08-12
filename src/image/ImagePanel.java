package image;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;

/**
 * this is a visual element which can display pictures
 *
 * @author xor
 */
public class ImagePanel extends JPanel {

    /**
     *
     */
    private static final long serialVersionUID = -242497295280375729L;
    private final Image image = null;
    private final IImageLoadRequestListener imageLoadRequestListener;
    private JLabel canvas;
    private File imageFile;
    private byte[] imageByteArray;

    ImagePanel(IImageLoadRequestListener imageLoadRequestListener) {
        super();
        this.imageLoadRequestListener = imageLoadRequestListener;
        ComponentListener imagePanelComponentListener = new ComponentListener() {

            @Override
            public void componentShown(ComponentEvent e) {
                System.out.println("ImagePanelController.shown()");
            }

            @Override
            public void componentResized(ComponentEvent e) {
                ImagePanel.this.imageLoadRequestListener.requestImageLoad(ImagePanel.this);
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                ImagePanel imagePanel = (ImagePanel) e.getSource();
            }

            @Override
            public void componentHidden(ComponentEvent e) {
                System.out.println("ImagePanelController.hidden()");
            }
        };
        this.addComponentListener(imagePanelComponentListener);
        setLayout(new BorderLayout());
        this.canvas = new JLabel();
    }

    public void reset() {
        imageByteArray = null;
        imageFile = null;
    }

    byte[] read() {

        if (imageByteArray == null) {
            try {
                imageByteArray = new byte[(int) imageFile.length()];
                DataInputStream dis = new DataInputStream(new FileInputStream(imageFile));
                dis.readFully(imageByteArray);
                dis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return imageByteArray;

    }

    /**
     * called by ImageRunnable
     */
    void onImageProcessing() {
        canvas = new JLabel();
        canvas.setBackground(Color.GREEN);
        canvas.repaint();
        removeAll();
        add(canvas);
    }

    /**
     * called by ImageRunnable
     */
    void onImageProcessed(Image image) {
        ImageIcon icon = new ImageIcon(image);
        canvas = new JLabel(icon);
        canvas.setBackground(Color.PINK);
        removeAll();
        add(canvas);
        canvas.repaint();
        validate();
        paintAll(getGraphics());
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (this.image != null) {
            g.drawImage(this.image, 0, 0, getWidth(), getHeight(), null);
        }
    }
    public void setImageFile(File file) {
        this.imageFile = file;
    }
}