package bootstrap;

import bio.BioHttpBootStrap;
import common.exception.SystemException;
import lombok.extern.slf4j.Slf4j;
import nio.NioHttpBootStrap;

/**
 * @author chen
 * @date 2020/4/11 上午10:37
 */
@Slf4j
public class HttpServer {

    private static final String NIO = "nio";

    private static final String BIO = "bio";

    public static void main(String[] args) throws Exception {
        final String type = System.getProperty("type");

        if (type == null || NIO.equalsIgnoreCase(type)) {
            log.info("nio server");
            NioHttpBootStrap.getINSTANCE().start();
        } else if (BIO.equalsIgnoreCase(type)) {
            log.info("bio server");
            BioHttpBootStrap.getINSTANCE().start();
        } else {
            throw new SystemException("type is error");
        }
    }
}
