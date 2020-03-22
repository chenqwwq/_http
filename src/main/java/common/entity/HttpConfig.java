package common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The type Http config.
 *
 * @author chen
 * @date 2020 /3/11 下午11:30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HttpConfig {
    private String dir;

    private Integer port;

    private String filePre;

    private Integer maxWait;

    private Integer maxChannel;
}
