package exhibit.monitor;

public class FileDetails implements Comparable<FileDetails> {
	private String name;
	private String type;
	private String time;
	private int gracePeriod;

	public FileDetails(String name, String type, String time, int gracePeriod) {
		this.name = name;
		this.type = type;
		this.time = time;
		this.gracePeriod = gracePeriod;
	}

	public FileDetails(String name, String type, String time) {
		this.name = name;
		this.type = type;
		this.time = time;
		this.gracePeriod = 0;
	}

	public String getName() {
		return this.name;
	}

	public String getType() {
		return this.type;
	}

	public String getTime() {
		return this.time;
	}

	public int getGracePeriod() {
		return this.gracePeriod;
	}

	@Override
	public String toString() {
		return "FileDetails [" + name + ", " + type + ", " + time + ", " + gracePeriod + "]";
	}

	@Override
	public int compareTo(FileDetails o) {
		if (this.name.compareToIgnoreCase(o.name) != 0) {
			return this.name.compareToIgnoreCase(o.name);
		}
		if (this.type.compareToIgnoreCase(o.type) != 0) {
			return this.type.compareToIgnoreCase(o.type);
		}
		if (this.time.compareToIgnoreCase(o.time) != 0) {
			return this.time.compareToIgnoreCase(o.time);
		}
		return this.gracePeriod - o.gracePeriod;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + gracePeriod;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((time == null) ? 0 : time.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FileDetails other = (FileDetails) obj;
		if (gracePeriod != other.gracePeriod)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (time == null) {
			if (other.time != null)
				return false;
		} else if (!time.equals(other.time))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
}
