package exhibit.monitor;

import java.util.Date;

public class Record {
	private String fileName;
	private Date date;
	private int recordNum;
	private String record;

	public Record(String fileName, Date date, int recordNum, String record) {
		this.fileName = fileName;
		this.date = date;
		this.recordNum = recordNum;
		this.record = record;
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
		return record;
	}

	public void setRecord(String record) {
		this.record = record;
	}

	@Override
	public String toString() {
		return "Record [fileName=" + fileName + ", date=" + date + ", recordNum=" + recordNum + ", record=" + record
				+ "]";
	}
}
