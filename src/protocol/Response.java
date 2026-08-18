package protocol;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


public class Response implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean success;
    private String message;
    private Map<String, String> data;


    public Response(boolean success, String message) {
        this.success = success;
        this.message = (message == null) ? "" : message;
        this.data = new HashMap<>();
    }


    public Response(boolean success, String message, Map<String, String> data) {
        this(success, message);
        if (data != null) {
            this.data.putAll(data);
        }
    }

    public static Response ok(String message) {
        return new Response(true, message);
    }


    public static Response fail(String message) {
        return new Response(false, message);
    }

    public Response put(String key, String value) {
        data.put(key, value);
        return this;
    }


    public String get(String key) {
        String value = data.get(key);
        return (value == null) ? "" : value;
    }


    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, String> getData() {
        return data;
    }

    @Override
    public String toString() {
        return "Response{success=" + success
             + ", message='" + message + "'"
             + ", data=" + data + "}";
    }
}
