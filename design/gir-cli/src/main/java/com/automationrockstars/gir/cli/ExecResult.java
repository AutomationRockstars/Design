package com.automationrockstars.gir.cli;

public class ExecResult {

	private int status;
	private String output;
	private String err;
	private long time;
	public ExecResult(String result, String error, int exit) {
		this.output = result;
		this.setErr(error);
		this.status = exit;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getOutput() {
		return output;
	}
	public void setOutput(String output) {
		this.output = output;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public String getErr() {
		return err;
	}
	public void setErr(String err) {
		this.err = err;
	}
}
