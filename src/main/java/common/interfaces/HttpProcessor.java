package common.interfaces;

import common.entity.HttpRequest;
import common.entity.HttpResponse;
import common.exception.NotFoundException;

import java.io.IOException;

/**
 * The interface Http handle.
 *
 * @param <T> the type parameter
 * @author chen
 * @date 2020 /3/8 上午10:44
 */
public interface HttpProcessor<T> {

    /**
     * Process.
     *
     * @param t the t
     * @throws Exception the exception
     */
    default void process(T t) throws Exception {
        printResponse(t, doRequest(getRequest(t)));
    }

    /**
     * Handle request http request.
     *
     * @param t the t
     * @return the http request
     * @throws Exception the exception
     */
    HttpRequest getRequest(T t) throws Exception;

    /**
     * 处理请求
     *
     * @param httpRequest the http request
     * @return http response
     * @throws IOException       the io exception
     * @throws NotFoundException the not found exception
     */
    HttpResponse doRequest(HttpRequest httpRequest) throws IOException, NotFoundException;

    /**
     * Print response.
     *
     * @param t            the t
     * @param httpResponse the http response
     */
    void printResponse(T t, HttpResponse httpResponse);

}

