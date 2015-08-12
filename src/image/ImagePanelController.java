package image;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class ImagePanelController implements IImageLoadRequestListener {
	private ExecutorService executor;
	private Map<ImagePanel, FutureTask<Object>> oldeRequests = new ConcurrentHashMap<>();

	public ImagePanelController() {
		this.executor = Executors.newWorkStealingPool();
	}

	public void reset() {
		executor.shutdownNow();
		executor = Executors.newWorkStealingPool();
		oldeRequests = new ConcurrentHashMap<>();
	}

	public ImagePanel createImagePanel(File file) {
		ImagePanel imagePanel = new ImagePanel(this);
		imagePanel.setImageFile(file);
		return imagePanel;
	}

	@Override
	public synchronized void requestImageLoad(ImagePanel imagePanel) {
		int x = imagePanel.getWidth();
		int y = imagePanel.getHeight();
		if (x > 0 && y > 0) {
			FutureTask<Object> task = ImageRunner.createImageTask(imagePanel);
			if (oldeRequests.containsKey(imagePanel)) {
				FutureTask<Object> oldeTask = oldeRequests.get(imagePanel);
				if (oldeTask != null && !oldeTask.isDone() && !oldeTask.isCancelled()) {
					oldeTask.cancel(true);
				}
			}
			Future<?> future = executor.submit(task);
			oldeRequests.put(imagePanel, task);
		}

	}
}
