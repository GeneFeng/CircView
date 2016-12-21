package cn.edu.whu;

public class Exon {
	private long startPoint;
	private long endPoint;

	public Exon(long startPoint, long endPoint) {
		this.startPoint = startPoint;
		this.endPoint = endPoint;
	}

	public long getStartPoint() {
		return startPoint;
	}

	public void setStartPoint(long startPoint) {
		this.startPoint = startPoint;
	}

	public long getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(long endPoint) {
		this.endPoint = endPoint;
	}
}
