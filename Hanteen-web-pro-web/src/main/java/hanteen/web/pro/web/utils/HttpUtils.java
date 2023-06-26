package hanteen.web.pro.web.utils;

import static org.apache.commons.lang3.StringUtils.firstNonBlank;

import javax.servlet.http.HttpServletRequest;


/**
 * @author zhaohang <zhaohang06@kuaishou.com>
 * Created on 2022-06-06
 */
public class HttpUtils {

    public static String getCommonParamFromRequest(HttpServletRequest request, String key) {
        return firstNonBlank(request.getHeader(key), request.getParameter(key));
    }
}
