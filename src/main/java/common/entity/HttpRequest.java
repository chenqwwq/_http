package common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Http的请求解析对象，
 * Http的整个请求分为三部分内容:起始行，请求头，请求体
 * 起始行包括 请求方法，请求资源地址，http版本 eg. GET /chen/README.html HTTP/1.1
 * 请求头可以理解为一组键值对，包含了类似Connection: keep-alive
 * 整个请求体可以理解为一个大字符串
 *
 * @author chen
 * @date 2020/3/8 下午11:56
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HttpRequest {

    /**
     * 起始行
     */
    private HttpRequestLine httpRequestLine;

    /**
     * 请求头集合
     */
    private HttpHeaders headers;

    /**
     * 请求体
     */
    private String body;
}
