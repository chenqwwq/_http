package common.exception;

import common.enums.HttpStatusCode;
import lombok.Getter;

/**
 * The type Status code exception.
 *
 * @author chen
 * @date 2020 /3/10 下午8:01
 */
@Getter
public class StatusCodeException extends Exception {

    /**
     * The constant code.
     */
    private HttpStatusCode code;

    public StatusCodeException(HttpStatusCode code) {
        this.code = code;
    }

    public StatusCodeException(String message, HttpStatusCode code) {
        super(message);
        this.code = code;
    }
}

