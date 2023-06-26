package hanteen.web.pro.service.util;

import static java.net.InetAddress.getLocalHost;

import java.io.UncheckedIOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhaohang <zhaohang06@kuaishou.com>
 * Created on 2022-04-01
 */
public class LocalHostUtil {

    private static final Logger logger = LoggerFactory.getLogger(LocalHostUtil.class);

    private static final String LOCAL_HOST_NAME;

    static {
        try {
            InetAddress host = getLocalHost();
            LOCAL_HOST_NAME = host != null ? host.getHostName() : null;
        } catch (UnknownHostException uhe) {
            logger.warn("[init host name] error", uhe);
            throw new UncheckedIOException(uhe.getMessage(), uhe);
        }
    }

    public static String getLocalHostName() {
        return LOCAL_HOST_NAME;
    }
}
