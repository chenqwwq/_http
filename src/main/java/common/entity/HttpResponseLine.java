package common.entity;

import common.enums.HttpStatusCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The type Http response line.
 *
 * @author chen
 * @date 2020 /3/9 上午9:33
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HttpResponseLine {
    private String version;

    private HttpStatusCode httpStatusCode;

    @Override
    public String toString() {
        return version + " " + httpStatusCode.getCode() + " " + httpStatusCode.getMemo();
    }
}
