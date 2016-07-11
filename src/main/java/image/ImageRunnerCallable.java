package image;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.Callable;

class ImageRunnerCallable implements Callable<Object> {
	private final ImagePanel panel;

	public ImageRunnerCallable(ImagePanel panel) {
		this.panel = panel;
	}


	private Image scale(Image image, int maxX, int maxY) {
		if (image == null) {
			return null;
		}
		int x = image.getWidth(null);
		int y = image.getHeight(null);

		float ratio = (float) x / (float) y;
		y = maxY;
		x = (int) (ratio * y);
		if (x > maxX) { // falls zu breit
			x = maxX;
			y = (int) (x / ratio);
		}
		return image.getScaledInstance(x, y, Image.SCALE_SMOOTH);
	}

	@Override
	public Object call() throws Exception {
		if (panel.getWidth() == 0 || panel.getHeight() == 0) {
			panel.onImageProcessed(null);
			return null;
		}
		panel.onImageProcessing();
		try {
			if (Thread.currentThread().isInterrupted()) {
				System.out.println("canceled");
				return null;
			}
			byte[] imageByteArray = panel.read();
			if (Thread.currentThread().isInterrupted()) {
				System.out.println("canceled");
				return null;
			}
			Image image = new ImageIcon(imageByteArray).getImage();
			if (Thread.currentThread().isInterrupted()) {
				System.out.println("canceled");
				return null;
			}
			int x = panel.getWidth();
			int y = panel.getHeight();
			image = scale(image, x, y);
			if (Thread.currentThread().isInterrupted()) {
				System.out.println("canceled");
				return null;
			}
			panel.onImageProcessed(image);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
