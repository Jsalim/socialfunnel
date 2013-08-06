package exceptions;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;
import org.json.JSONException;
import org.json.JSONObject;

import play.libs.Json;


/**
 * This abstract exception may come in handy whenever an error occurs and a json needs to be displayed
 * */
public class GenericException extends Exception{ // Has to extend java.lang.Exception and not RuntimeException 

	private String errorMessage;

	public GenericException(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getErrorMessage() {
		return this.errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public ObjectNode getJson(){
		ObjectNode jsonObject = Json.newObject();
		jsonObject.put("success", false);
		jsonObject.put("Excepation", this.getClass().getSimpleName());
		jsonObject.put("error", this.errorMessage);
		return jsonObject;
	}
}
