package exhibit.monitor;

import java.util.Date;

public class Record {
	private String fileName;
	private Date date;
	private int recordNum;
	private String recordStr;

	public Record(String fileName, Date date, int recordNum, String recordStr) {
		this.fileName = fileName;
		this.date = date;
		this.recordNum = recordNum;
		this.recordStr = recordStr;
	}

	// for checking if the record exist
	public Record(String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getRecordNum() {
		return recordNum;
	}

	public void setRecordNum(int recordNum) {
		this.recordNum = recordNum;
	}

	public String getRecord() {
		return recordStr;
	}

	public void setRecord(String recordStr) {
		this.recordStr = recordStr;
	}

	@Override
	public String toString() {
		return "Record [fileName=" + fileName + ", date=" + date + ", recordNum=" + recordNum + ", record=" + recordStr
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
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
		Record other = (Record) obj;
		if (fileName == null) {
			if (other.fileName != null)
				return false;
		} else if (!fileName.equals(other.fileName))
			return false;
		return true;
	}

}
