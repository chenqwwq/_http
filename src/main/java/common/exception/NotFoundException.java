package common.exception;

import common.enums.HttpStatusCode;

/**
 * {@link HttpStatusCode#NOT_FOUND}
 *
 * @author chen
 * @date 2020/3/11 下午7:19
 */
public class NotFoundException extends StatusCodeException {

    public NotFoundException(String message) {
        super(message, HttpStatusCode.NOT_FOUND);
    }

}
