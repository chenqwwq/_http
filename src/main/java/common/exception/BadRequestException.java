package common.exception;

import common.enums.HttpStatusCode;

/**
 * {@link HttpStatusCode#BAD_REQUEST}
 *
 * @author chenqwwq
 * @date 2020/3/9 上午12:06
 */
public class BadRequestException extends StatusCodeException {

    public BadRequestException(String message) {
        super(message,HttpStatusCode.BAD_REQUEST);
    }
}
