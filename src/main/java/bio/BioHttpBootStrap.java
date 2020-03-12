package bio;

import common.entity.HttpConfig;
import common.interfaces.HttpServerBootStrap;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author chen
 * @date 2020/3/7 下午10:30
 */
@Slf4j
public class BioHttpBootStrap implements HttpServerBootStrap {

    @Override
    public void start(HttpConfig config) throws Exception {
        // 创建对应的socket
        BioHttpProcessor bioHttpProcessor = new BioHttpProcessor(config);
        ServerSocket socket = new ServerSocket(config.getPort());

        log.info("// ==========  start bio http socket");
        while (true) {
            try (Socket accept = socket.accept()) {
                log.info("// ========== accept server");
                bioHttpProcessor.process(accept);
            } catch (IOException e) {
                log.info("accept error ,msg :" + e.getMessage());
            }
        }

    }
}
