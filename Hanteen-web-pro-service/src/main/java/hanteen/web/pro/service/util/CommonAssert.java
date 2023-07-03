package hanteen.web.pro.service.util;

import hanteen.web.pro.service.constant.CommonCode;
import hanteen.web.pro.service.model.exception.HanteenBaseException;

/**
 * @author zhaohang <zhaohang06@kuaishou.com>
 * Created on 2022-04-02
 */
public class CommonAssert {

    public static void assertTrue(boolean expression, CommonCode code) {
        if (!expression) {
            throw new HanteenBaseException(code);
        }
    }

    public static void assertFalse(boolean expression, CommonCode code) {
        if (expression) {
            throw new HanteenBaseException(code);
        }
    }

    public static void assertNotNull(Object obj, CommonCode code) {
        if (obj == null) {
            throw new HanteenBaseException(code);
        }
    }
}
