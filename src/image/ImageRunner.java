package image;

import java.util.concurrent.FutureTask;

class ImageRunner {
	
	public static FutureTask<Object> createImageTask(ImagePanel imagePanel) {
		ImageRunnerCallable callable = new ImageRunnerCallable(imagePanel);
		FutureTask<Object> task = new FutureTask<>(callable);
		callable.setTask(task);
		return task;
	}

}
