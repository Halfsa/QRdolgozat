package com.example.qrdolgozat;

public class Response {
    private int repsponseCode;
    private String responseMessage;

    public Response(int repsponseCode, String responseMessage) {
        this.repsponseCode = repsponseCode;
        this.responseMessage = responseMessage;
    }

    public int getRepsponseCode() {
        return repsponseCode;
    }

    public void setRepsponseCode(int repsponseCode) {
        this.repsponseCode = repsponseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }
}
