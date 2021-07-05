package gateway.control.demo.wrapper;

import org.springframework.http.HttpStatus;

public class Response {
    private Object responseBody;
    private Boolean hasError;
    private String error;
    private String description;
    private HttpStatus status;

    public Response(HttpStatus status, Object responseBody, String description) {
        this.responseBody = responseBody;
        this.description = description;
        this.status = status;
        this.hasError = false;
    }

    public Response(HttpStatus status, String error) {
        this.error = error;
        this.status = status;
        this.hasError =  true;
    }

    public Object getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(Object responseBody) {
        this.responseBody = responseBody;
    }

    public Boolean getHasError() {
        return hasError;
    }

    public void setHasError(Boolean hasError) {
        this.hasError = hasError;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
