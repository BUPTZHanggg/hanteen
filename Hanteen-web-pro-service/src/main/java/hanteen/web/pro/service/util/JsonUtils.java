package hanteen.web.pro.service.util;

import static com.fasterxml.jackson.core.JsonFactory.Feature.INTERN_FIELD_NAMES;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static java.util.Collections.emptySet;
import static java.util.Optional.ofNullable;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactoryBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;

/**
 * 基于Jackson封装的JSON工具类
 * @author zhaohang <zhaohang06@kuaishou.com>
 * Created on 2022-04-06
 */
public class JsonUtils {

    private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);

    /**
     * disable INTERN_FIELD_NAMES
     * 避免String#intern()方法导致的内存泄露问题
     */
    private static final ObjectMapper MAPPER = new ObjectMapper(
            new JsonFactoryBuilder()
                    .disable(INTERN_FIELD_NAMES)
                    .build()
    );

    static {
        try {
            configCommon();
        } catch (Exception e) {
            logger.error("[Jackson] Jackson config init failed", e);
        }
    }

    private static void configCommon() {
        //避免反序列化时由于未知属性导致的JsonMappingException异常
        MAPPER.disable(FAIL_ON_UNKNOWN_PROPERTIES);
        MAPPER.registerModule(new GuavaModule());
        MAPPER.registerModule(new ParameterNamesModule());
    }

    @Nullable
    public static String toJsonString(@Nullable Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            logger.error("[Jackson] Obj to str failed:{}", obj.getClass().getName(), e);
            return null;
        }
    }

    public static <T> T fromJSON(@Nullable String json, Class<T> valueType) {
        if (json == null) {
            return null;
        }
        try {
            return MAPPER.readValue(json, valueType);
        } catch (IOException e) {
            return null;
        }
    }

    public static final String JSON1 = "{\"bupt\":\"111\",\"dut\":{\"huagong\":\"222\",\"jixie\":\"333\"},"
            + "\"yizhong\":\"{\\\"shuxue\\\":\\\"wlm\\\",\\\"yingyu\\\":123}\"}";

    public static final String JSON2 = "{\"dut\":{\"jixie\":\"3334\"},\"sanzhong\":{\"haxue\":\"ldx\"}}";

    public static final String GLOBAL = "{\"qixiao\":\"xiaoxue\",\"bupt\":2018,\"dut\":{\"jixie\":\"jixiexueyuan\"}}";

    public static final String YZ_JSON = "{\"yuwen\":\"sjy\",\"yingyu\":12345}";

    //利用merge patch，进行复杂结构的合并
    public static void test() {
        try {
            //先构造一个原始的json
            ObjectNode originConfig = JsonNodeFactory.instance.objectNode(); //空json {} ObjectNode可写
            JsonNode jsonNode = fromJSON(JSON1, JsonNode.class); //JsonNode只读
            JsonMergePatch patch = JsonMergePatch.fromJson(jsonNode);
            originConfig = (ObjectNode) patch.apply(originConfig);
            System.out.println(toJsonString(originConfig));
            JsonNode jsonNode2 = fromJSON(JSON2, JsonNode.class);
            JsonMergePatch patch2 = JsonMergePatch.fromJson(jsonNode2);
            originConfig = (ObjectNode) patch2.apply(originConfig);
            System.out.println(toJsonString(originConfig));
            Map<String, Object> stringObjectMap =
                    MAPPER.convertValue(originConfig, new TypeReference<Map<String, Object>>() {
                    }); //一般生产环境中的复杂结构都是Map，这里做下转换
            System.out.println(toJsonString(stringObjectMap));

            //待merge的数据，一般是从一些配置里读出来的，这里手动构造
            ObjectNode patchNode = JsonNodeFactory.instance.objectNode(); //空json {} ObjectNode可写
            JsonNode j1 = fromJSON(GLOBAL, JsonNode.class); //JsonNode只读
            JsonMergePatch j1Patch = JsonMergePatch.fromJson(j1);
            patchNode = (ObjectNode) j1Patch.apply(patchNode);
            //... 这里可以读多个配置进行merge的覆盖
            System.out.println(toJsonString(patchNode));
            //YZ_JSON是一个json转的string，也可以通过patch的方式读配置构造，这里手动写死一个
            patchNode.put("yizhong", YZ_JSON);
            System.out.println(toJsonString(patchNode));
            Map<String, Object> patchMap =
                    MAPPER.convertValue(patchNode, new TypeReference<Map<String, Object>>() {
                    });
            System.out.println(toJsonString(patchMap));

            //merge的两种方式
            //1.两个ObjectNode直接merge
            //注意：yizhong对应的是一个json转的string，直接merge有问题，需要特殊处理
            String yizhong = patchNode.remove("yizhong").asText();
            JsonNode yizhongNode = fromJSON(yizhong, JsonNode.class);
            String originYizhong = originConfig.remove("yizhong").asText();
            JsonNode originYizhongNode = fromJSON(originYizhong, JsonNode.class);
            originYizhongNode = JsonMergePatch.fromJson(yizhongNode).apply(originYizhongNode);
            JsonMergePatch finalPatch = JsonMergePatch.fromJson(patchNode);
            originConfig = (ObjectNode) finalPatch.apply(originConfig);
            originConfig.put("yizhong", toJsonString(originYizhongNode));
            System.out.println(toJsonString(originConfig));
            //2.map merge 注意，putAll方法不能深度merge
        } catch (JsonPatchException e) {
            throw new RuntimeException(e);
        }
    }
}
