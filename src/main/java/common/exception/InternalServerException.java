package common.exception;

import common.enums.HttpStatusCode;
import lombok.Getter;

/**
 * {@link HttpStatusCode#INTERNAL_SERVER_ERROR}
 *
 * @author chen
 * @date 2020/3/10 下午7:47
 */
@Getter
public class InternalServerException extends StatusCodeException {

    public InternalServerException(String message) throws Exception {
        super(message, HttpStatusCode.INTERNAL_SERVER_ERROR);
    }

}
