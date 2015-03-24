
/* Result.java */
/* 21/05/11 */

package it.ismb.automotive;

/**
 * Class that describes the result of an operation, indicating the presence of an error and
 * optional description.
 */
public class Result {
	private boolean errorFound = false;
	private String errorDescription = "";
	
	public Result() {
	}
	
	public Result(boolean errorFound, String errorDescription) {
		this.errorFound = errorFound;
		this.errorDescription = errorDescription;
	}
	
	public boolean isErrorFound() {
		return errorFound;
	}
	
	public void setErrorFound(boolean errorFound) {
		this.errorFound = errorFound;
	}
	
	public String getErrorDescription() {
		return errorDescription;
	}
	
	public void setErrorDescription(String errorDescription) {
		this.errorDescription = errorDescription;
	}
}
