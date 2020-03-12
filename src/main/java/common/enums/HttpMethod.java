package common.enums;

import common.exception.BadRequestException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Http的请求方法
 *
 * @author chen
 * @date 2020/3/8 下午11:58
 */
@Getter
@AllArgsConstructor
public enum HttpMethod {

    /**
     * GET类型的请求用来请求访问资源
     */
    GET("GET"),

    /**
     * POST请求用来传输实体数据，
     * 可以简单理解为，
     * GET和POST请求都可以把请求内容填充到请求体，但是一般来说用POST
     */
    POST("POST"),

    /**
     * PUT请求用来传输文件，但是没有全校校验比较危险
     */
    PUT("PUT"),

    /**
     * HEAD 获得响应首部，HEAD 方法和 GET 方法一样，只是不返回报文主体部分。
     */
    HEAD("HEAD"),

    /**
     * DELETE 删除文件，DELETE 方法用来删除文件，是与 PUT 相反的方法
     */
    DELETE("DELETE"),

    /**
     * OPTIONS 询问支持的方法，OPTIONS 方法用来查询针对请求 URI 指定的资源支持的方法。
     */
    OPTIONS("OPTIONS"),

    /**
     * TRACE 追踪路径，TRACE 方法是让 Web 服务器端将之前的请求通信环回给客户端的方法。
     */
    TRACE("TRACE"),

    /**
     * CONNECT 要求用隧道协议连接代理，CONNECT 方法要求在与代理服务器通信时建立隧道，实现用隧道协议进行 TCP 通信。主要使用 SSL（Secure Sockets Layer，安全套接层）和 TLS（Transport Layer Security，传输层安全）协议把通信内容加 密后经网络隧道传输。
     */
    CONNECT("CONNECT");

    /**
     * 请求码
     */
    private String code;

    /**
     * 将请求码转化为美剧对象
     * @throws BadRequestException {@link BadRequestException} 解析出错的时候排除异常
     */
    public static HttpMethod getHttpMethod(String code) throws BadRequestException {
        if(code == null || code.length() == 0){
            throw new BadRequestException("request method is null");
        }

        for (HttpMethod httpMethod : HttpMethod.values()){
            if(httpMethod.code.equalsIgnoreCase(code)){
                return httpMethod;
            }
        }

        throw new BadRequestException("bad request method : " + code);
    }
}
