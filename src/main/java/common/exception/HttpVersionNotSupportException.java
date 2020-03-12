package common.exception;

import common.enums.HttpStatusCode;

/**
 * {@link HttpStatusCode#HTTP_VERSION_NOT_SUPPORTED}
 *
 * @author chen
 * @date 2020/3/11 下午7:20
 */
public class HttpVersionNotSupportException extends StatusCodeException {
    public HttpVersionNotSupportException(String message) {
        super(message, HttpStatusCode.HTTP_VERSION_NOT_SUPPORTED);
    }
}
