package image;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.Callable;

class ImageRunnerCallable implements Callable<Object> {
    private final ImagePanel panel;

    public ImageRunnerCallable(ImagePanel panel) {
        this.panel = panel;
    }

    /**
     * Scales eagerly into a new BufferedImage so the work happens on the calling (worker) thread.
     * (Image.getScaledInstance() is lazy and would do the scaling on the EDT when first painted.)
     * Large reductions are done in halving steps; a single bilinear step from e.g. 6000px to 200px
     * skips most source pixels and looks jagged.
     */
    private static BufferedImage scale(Image image, int maxX, int maxY) {
        int sourceX = image.getWidth(null);
        int sourceY = image.getHeight(null);
        double ratio = Math.min((double) maxX / sourceX, (double) maxY / sourceY);
        int targetX = Math.max(1, (int) (sourceX * ratio));
        int targetY = Math.max(1, (int) (sourceY * ratio));

        Image current = image;
        int x = sourceX;
        int y = sourceY;
        do {
            if (Thread.currentThread().isInterrupted()) {
                return null;
            }
            x = Math.max(x / 2, targetX);
            y = Math.max(y / 2, targetY);
            current = resize(current, x, y);
        } while (x != targetX || y != targetY);
        return (BufferedImage) current;
    }

    private static BufferedImage resize(Image source, int width, int height) {
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = result.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.drawImage(source, 0, 0, width, height, null);
        } finally {
            g.dispose();
        }
        return result;
    }

    /**
     * Runs on a worker thread. Disk I/O, decoding and scaling happen here;
     * only the two panel updates are handed to the EDT.
     */
    @Override
    public Object call() {
        int width = panel.getWidth();
        int height = panel.getHeight();
        if (width == 0 || height == 0) {
            SwingUtilities.invokeLater(() -> panel.onImageProcessed(null));
            return null;
        }
        SwingUtilities.invokeLater(panel::onImageProcessing);
        try {
            byte[] imageByteArray = panel.read();
            if (Thread.currentThread().isInterrupted()) {
                return null;
            }
            ImageIcon icon = new ImageIcon(imageByteArray);
            if (Thread.currentThread().isInterrupted()) {
                return null;
            }
            if (icon.getImageLoadStatus() != MediaTracker.COMPLETE || icon.getIconWidth() <= 0) {
                SwingUtilities.invokeLater(() -> panel.onImageProcessed(null));
                return null;
            }
            Image decoded = icon.getImage();
            BufferedImage scaled = scale(decoded, width, height);
            if (scaled == null || Thread.currentThread().isInterrupted()) {
                return null;
            }
            SwingUtilities.invokeLater(() -> panel.onImageProcessed(scaled));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
