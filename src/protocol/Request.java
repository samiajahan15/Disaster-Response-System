package protocol;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


public class Request implements Serializable {
    private static final long serialVersionUID = 1L;

    private CommandType command;
    private String username;
    private Map<String, String> data;

    public Request(CommandType command) {
        this(command, "");
    }


    public Request(CommandType command, String username) {
        this.command = command;
        this.username = (username == null) ? "" : username;
        this.data = new HashMap<>();
    }


    public Request put(String key, String value) {
        data.put(key, value);
        return this;
    }


    public String get(String key) {
        String value = data.get(key);
        return (value == null) ? "" : value;
    }


    public CommandType getCommand() {
        return command;
    }

    public String getUsername() {
        return username;
    }

    public Map<String, String> getData() {
        return data;
    }

    @Override
    public String toString() {
        return "Request{command=" + command
             + ", username='" + username + "'"
             + ", data=" + data + "}";
    }
}
