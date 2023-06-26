package hanteen.web.pro.web.controller;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.Base64;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.EnumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonIgnore;

import hanteen.web.pro.service.util.JsonUtils;

/**
 * @author zhaohang <zhaohang06@kuaishou.com>
 * Created on 2022-05-26
 */
@RestController
@RequestMapping("/services/product/api")
public class TestController {

    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @RequestMapping(value = "/meetingCallback", method = RequestMethod.POST)
    public EventNotificationResp<?> testLog(@RequestBody EventNotificationReq req, HttpServletRequest request){
        String data = req.getData();
        byte[] decode = Base64.getDecoder().decode(data);
        MeetingEventInfo meetingEventInfo = JsonUtils.fromJSON(new String(decode), MeetingEventInfo.class);
        Enumeration<String> headerNames = request.getHeaderNames();
        Set<String> sortedHeadNames = new TreeSet<>();
        while (headerNames.hasMoreElements()) {
            String curr = headerNames.nextElement();
            sortedHeadNames.add(curr);
        }
        StringBuilder builder = new StringBuilder();
        sortedHeadNames.forEach(headName -> {
            if (isNotBlank(request.getHeader(headName))) {
                builder.append(headName).append("=")
                        .append(request.getHeader(headName)).append("&");
            }
        });
        logger.info("{}", builder.toString());
        logger.info("{}", JsonUtils.toJsonString(meetingEventInfo));
        return new EventNotificationResp<>(1, "错误");
    }

    public static class EventNotificationReq {

        private String data; //com.kuaishou.meeting.qarth.component.model.openapi.MeetingEventInfo base64编码

        public EventNotificationReq() {
        }

        public EventNotificationReq(String data) {
            this.data = data;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }

    public static class EventNotificationResp<T> {

        private int code;
        private String message;
        private T data;

        public EventNotificationResp() {
        }

        public EventNotificationResp(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }
    }

    public static class MeetingEventInfo {

        private String event;                               //事件名
        private String creator;                             //会议拥有者uid，调用方用于标示用户的唯一ID
        private String operator;                            //事件操作者uid
        private String meetingId;                           //会议唯一ID
        private String meetingCode;                         //9位会议码
        private Integer meetingType;                        //会议类型
        private String topic;                               //会议主题
        private Long timestamp;                             //毫秒级时间戳
        private Integer endType;                            // 0-主持人主动结束 1-所有人离会或达到结束时间

        public MeetingEventInfo() {
        }

        public String getEvent() {
            return event;
        }

        public void setEvent(String event) {
            this.event = event;
        }

        public String getCreator() {
            return creator;
        }

        public void setCreator(String creator) {
            this.creator = creator;
        }

        public String getOperator() {
            return operator;
        }

        public void setOperator(String operator) {
            this.operator = operator;
        }

        public String getMeetingId() {
            return meetingId;
        }

        public void setMeetingId(String meetingId) {
            this.meetingId = meetingId;
        }

        public String getMeetingCode() {
            return meetingCode;
        }

        public void setMeetingCode(String meetingCode) {
            this.meetingCode = meetingCode;
        }

        public Integer getMeetingType() {
            return meetingType;
        }

        public void setMeetingType(Integer meetingType) {
            this.meetingType = meetingType;
        }

        public String getTopic() {
            return topic;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }

        public Long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Long timestamp) {
            this.timestamp = timestamp;
        }

        public Integer getEndType() {
            return endType;
        }

        public void setEndType(Integer endType) {
            this.endType = endType;
        }
    }

}
