package common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author chen
 * @date 2020/3/9 上午12:32
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HttpResponse {
    private HttpResponseLine httpResponseLine;

    private HttpHeaders headers;

    private String responseBody;

    @Override
    public String toString() {
        return httpResponseLine.toString() + "\r"
                + (headers == null ? "" : headers.toString())
                + "\n"
                + (responseBody == null ? "" : responseBody);
    }
}
