package bootstrap;

import bio.BioHttpBootStrap;
import lombok.extern.slf4j.Slf4j;
import nio.NioHttpBootStrap;

/**
 * @author chen
 * @date 2020-04-10
 **/
@Slf4j
public class HttpServerBootStrap {
    private static String NIO = "nio";
    private static String BIO = "Bio";
    public static void main(String[] args) throws Exception {
        String type = System.getProperty("type");
        if(type == null){
            log.error("// ======== type is null");
            return;
        }
        if(NIO.equalsIgnoreCase(type)){
            new NioHttpBootStrap().start();
        }else if(BIO.equalsIgnoreCase(type)){
            new BioHttpBootStrap().start();
        }else{
            log.error("// =======  type is error");
        }
    }
}
