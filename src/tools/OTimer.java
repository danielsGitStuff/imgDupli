package tools;


public class OTimer {

	private final String name;

	private long sum;
	private long startTime;
	private long fps = 0;
	private long startCount = 0;

	public OTimer(String name) {
		this.name = name;
	}

	public OTimer start() {
		startTime = System.nanoTime();
		startCount++;
		return this;
	}

	public long fps() {
		long duration = (System.currentTimeMillis() - fps);
		fps = System.currentTimeMillis();
		if (duration > 0)
			return 1000 / duration;
		return 0;
	}

	@Override
	public String toString() {
		return "Timer[" + name + "]: " + getDurationInMS() + "ms";
	}

	public OTimer stop() {
		sum = System.nanoTime() - startTime + sum;
		return this;
	}

	private long getDurationInMS() {
		return (sum / 1000000);
	}

	public long getDurationInNS() {
		return sum;
	}

	public long getDurationInS() {
		return ((System.nanoTime() - startTime) / 1000000000);
	}

	public OTimer print() {
		System.out.println(this.getClass().getSimpleName() + ".'" + name + "'.print: " + sum / 1000000 +" ms");
		return this;
	}

	public OTimer reset() {
		sum = 0;
		startCount = 0;
		return this;
	}

	public long getStartCount() {
		return startCount;
	}

	public long getAverageDuration() {
		return sum / startCount;
	}
}
