package common.exception;

import common.enums.HttpStatusCode;

/**
 * {@link HttpStatusCode#FORBIDDEN}
 *
 * @author chen
 * @date 2020/3/11 下午7:36
 */
public class ForbiddenException extends StatusCodeException {

    public ForbiddenException(String message) {
        super(message, HttpStatusCode.FORBIDDEN);
    }

}
