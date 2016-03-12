package csula.crawler;

public class Entry {
	private String link;
	private Integer depth;

	public Entry(String link, Integer depth) {
		this.link = link;
		this.depth = depth;

	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public Integer getDepth() {
		return depth;
	}

	public void setDepth(Integer depth) {
		this.depth = depth;
	}

}
