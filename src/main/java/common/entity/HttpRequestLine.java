package common.entity;

import common.enums.HttpMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Http的起始行
 *
 * @author chen
 * @date 2020 /3/8 下午11:57
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HttpRequestLine {
    /**
     * 请求方法
     */
    private HttpMethod method;

    /**
     * 请求资源地址
     */
    private String uri;

    /**
     * http版本
     */
    private String version;
}
