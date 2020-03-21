package common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * 保存所有请求头部，因为请求头除了规范中的还可以自定义，Name就不用枚举类了。
 *
 * @author chenqwwq
 * @date 2020 /3/9 上午12:19
 */
@Data
@AllArgsConstructor
public class HttpHeaders {

    private static final String SPLIT_HEADER = "&";

    private static final String SPLIT_KAT_VALUE = ":";

    /**
     * 以HashMap保存所有的请求头
     */
    private Map<String, String> headers = new HashMap<>();

    /**
     * Instantiates a new Http headers.
     *
     * @param headerStr the header str
     */
    public HttpHeaders(String headerStr) {
        for (String a : headerStr.split(SPLIT_HEADER)) {
            final String[] split = a.split(SPLIT_KAT_VALUE);
            if (split.length != 2) {
                continue;
            }
            // 全部以小写保存
            headers.put(split[0].trim().toLowerCase(), split[1].trim().toLowerCase());
        }
    }

    /**
     * Instantiates a new Http headers.
     */
    public HttpHeaders() {
    }

    /**
     * 增加请求头信息
     *
     * @param key   the key
     * @param value the value
     */
    public void addHeader(String key, String value) {
        headers.put(key.trim(), value.trim());
    }

    public void addHeader(String str) {
        final String[] split = str.split(SPLIT_KAT_VALUE);
        if (split.length != 2) {
            return;
        }

        headers.put(split[0].trim(), split[1].trim());

    }

    /**
     * Gets header.
     *
     * @param key the key
     * @return the header
     */
    public String getHeader(String key) {
        return headers.get(key);
    }

    @Override
    public String toString() {
        StringBuilder headerStr = new StringBuilder();

        for (HashMap.Entry entry : headers.entrySet()) {
            headerStr.append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue())
                    .append("\n");
        }
        return headerStr.toString();
    }
}
