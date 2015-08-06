package image;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * this is a visual element which can display pictures
 * 
 * @author deck006
 * 
 */
public class ImagePanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -242497295280375729L;
	private final Image image = null;
	private JLabel canvas;
	private final double random;
	private File imageFile;
	private byte[] imageByteArray;
	private final IImageLoadRequestListener imageLoadRequestListener;

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
//			System.out.println("ImagePanel.resiZE()");
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
		this.random = Math.random();
		this.canvas = new JLabel();
	}

	public void reset() {
		imageByteArray = null;
		imageFile = null;
	}

	byte[] read() {
//		byte[] imageByteArray = null;
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

	private void blank() {
		this.imageFile = null;
		remove(canvas);
		paintAll(getGraphics());
	}

	@Override
	public void repaint() {
		super.repaint();
	}

	@Override
	public void updateUI() {
		super.updateUI();
		// I Guess this method is called when the Panel becomes visible for the
		// first time... seems not to be the case
	}

	@Override
	public void update(Graphics g) {
		// System.out.println("ImagePanel.update()");
		super.update(g);
	}

	@Override
	public void paint(Graphics g) {
		// System.out.println("ImagePanel.paint()"+imageFile.getName());
		super.paint(g);
	}

	private void out(String msg) {
		boolean sysout = false;
		if (sysout) {
			System.out.println("ImagePanel." + msg);
		}
	}

	public double getRandom() {
		return this.random;
	}

	/**
	 * called by ImageRunnable
	 */
	void onImageProcessing() {
		lock();
		canvas = new JLabel();
		canvas.setBackground(Color.GREEN);
		canvas.repaint();
		// setBackground(Color.BLUE);
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
		// canvas.repaint();
//		setBackground(Color.yellow);
		removeAll();
		add(canvas);
		canvas.repaint();
		unlock();
		validate();
		// repaint();
		paintAll(getGraphics());
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (this.image != null) {
			g.drawImage(this.image, 0, 0, getWidth(), getHeight(), null);
		}
	}

	private void lock() {

	}

	private void unlock() {

	}

	public void setImageFile(File file) {
		this.imageFile = file;
	}

	public void forceDraw() {

		byte[] imageByteArray = read();

		Image image = new ImageIcon(imageByteArray).getImage();
		int x = getWidth();
		int y = getHeight();
		image = scale(image, x, y);
		// System.out.println("ImageRunner.run().done");
		onImageProcessed(image);
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

}