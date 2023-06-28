package org.lessons.java.springlamiapizzeriacrud.messages;

public class AlertMessage {
    private AlertMessageType type;
    private String message;

    //CONSTRUCTOR
    public AlertMessage(AlertMessageType type, String message) {
        this.type = type;
        this.message = message;
    }

    //GETTERS & SETTERS
    public AlertMessageType getType() {
        return type;
    }

    public void setType(AlertMessageType type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
