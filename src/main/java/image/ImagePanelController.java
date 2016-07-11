package image;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class ImagePanelController implements IImageLoadRequestListener {
	private ExecutorService executor;
	private Map<ImagePanel, FutureTask<Object>> oldeRequests = new ConcurrentHashMap<>();

	public ImagePanelController() {
		this.executor = Executors.newWorkStealingPool();
	}

	private static FutureTask<Object> createImageTask(ImagePanel imagePanel) {
		ImageRunnerCallable callable = new ImageRunnerCallable(imagePanel);
		return new FutureTask<>(callable);
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
			FutureTask<Object> task = ImagePanelController.createImageTask(imagePanel);
			if (oldeRequests.containsKey(imagePanel)) {
				FutureTask<Object> oldeTask = oldeRequests.get(imagePanel);
				if (oldeTask != null && !oldeTask.isDone() && !oldeTask.isCancelled()) {
					oldeTask.cancel(true);
				}
			}
			executor.submit(task);
			oldeRequests.put(imagePanel, task);
		}

	}
}
