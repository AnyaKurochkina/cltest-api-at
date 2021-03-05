package ru.vtb.test.api.helper;

public class CheckingResult {
    private boolean result;
    private String message;

    CheckingResult(boolean result, String error) {
        this.result = result;
        this.message = error;
    }

    boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "ExecResult{" +
                "result=" + result +
                ", message='" + message + '\'' +
                '}';
    }
}
