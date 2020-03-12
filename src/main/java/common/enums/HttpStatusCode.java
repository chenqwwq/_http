package common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * The enum Http status code.
 *
 * @author chen
 * @date 2020 /3/9 上午9:34
 */
@Getter
@AllArgsConstructor
public enum HttpStatusCode {


    /**
     * Ok http status code.
     */
    OK(200, "OK"),

    /**
     * The Bad request.
     */
    BAD_REQUEST(400, "bad request"),

    /**
     * Forbidden http status code.
     */
    FORBIDDEN(403, "Forbidden"),

    /**
     * The Not found.
     */
    NOT_FOUND(404, "Not Found"),

    /**
     * The Internal server error.
     */
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),

    /**
     * The Http version not supported.
     */
    HTTP_VERSION_NOT_SUPPORTED(505, "Version Not Supported");

    private Integer code;

    private String memo;

    /**
     * Gets http status code.
     *
     * @param code the code
     * @return the http status code
     */
    public static HttpStatusCode getHttpStatusCode(int code) {
        for (HttpStatusCode httpStatusCode : HttpStatusCode.values()) {
            if (code == httpStatusCode.code) {
                return httpStatusCode;
            }
        }

        return BAD_REQUEST;
    }
}
