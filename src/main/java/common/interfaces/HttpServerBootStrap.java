package common.interfaces;

import common.entity.HttpConfig;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * The interface Http boot strap.
 * 因为包含很多重量级集合对象，建议单例实现
 *
 * @author chen
 * @date 2020 /3/8 上午10:45
 */
public interface HttpServerBootStrap {

    /**
     * The constant configFilePath.
     */
    String configFilePath = "src/main/resources/http.yml";

    /**
     * Start.
     *
     * @param config the config
     * @throws Exception the exception
     */
    default void start(HttpConfig config) throws Exception {};

    /**
     * Start.
     *
     * @throws Exception the exception
     */
    default void start() throws Exception {
        start(loadConfig());
    }

    /**
     * Load config http config.
     *
     * @return the http config
     * @throws IOException the io exception
     */
    default HttpConfig loadConfig() throws IOException {
        final File file = new File(configFilePath);
        if (!file.isFile() || !file.canRead()) {
            throw new FileNotFoundException("config file error,please confirm directory, path : "  + file.getPath());
        }
        try (final FileInputStream input = new FileInputStream(file);) {
            return new Yaml().loadAs(input, HttpConfig.class);
        }
    }
}
