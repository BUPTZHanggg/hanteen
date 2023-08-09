package hanteen.web.pro.web.controller;


import static hanteen.web.pro.service.model.common.CommonThreadLocal.token;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import hanteen.web.pro.service.constant.CommonCode;
import hanteen.web.pro.model.mybatis.model.EmployeeWelfareInfo;
import hanteen.web.pro.service.model.UserProfileView;
import hanteen.web.pro.service.model.exception.HanteenBaseException;
import hanteen.web.pro.service.user.AsyncDataProcessor;
import hanteen.web.pro.service.user.GetUserInfoService;
import hanteen.web.pro.service.user.UserRegisterServiceManager;
import hanteen.web.pro.service.util.CommonAssert;
import hanteen.web.pro.web.model.CommonMessage;
import hanteen.web.pro.web.model.GreetingReq;

/**
 * @author paida 派哒 zeyu.pzy@alibaba-inc.com
 */
@RestController
@RequestMapping("/rest/v1/hanteen/user")
@EnableWebMvc
public class GreetingController {

	private static final Logger logger = LoggerFactory.getLogger(GreetingController.class);

	@Resource
	private AsyncDataProcessor asyncDataProcessor;
	@Resource
	private GetUserInfoService getUserInfoService;
	@Resource
	private UserRegisterServiceManager userRegisterServiceManager;

	//test log
	@RequestMapping(value = "/hello", method = RequestMethod.GET)
	public String testLog(){
		long userId = getUserInfoService.getUserId();
		logger.info("info");
		logger.warn("warn");
		MDC.remove("userId");
		return "helloworld";
	}

	@RequestMapping(value = "/info", method = RequestMethod.GET)
	public Object greeting(@RequestParam(value = "userId", defaultValue = "") String userId) {
		CommonAssert.assertTrue(isNotBlank(userId), CommonCode.INVALID_PARAM);
		Map<String, Object> res = new HashMap<>();
		res.put("name", "haohang");
		res.put("a", new EmployeeWelfareInfo());
		return res;
	}

	@RequestMapping(value = "/post", method = RequestMethod.POST)
	public CommonMessage<Map<String, Object>> greeting(@RequestBody GreetingReq req) {
		Map<String, Object> res = new HashMap<>();
		res.put("name", "haohang");
		logger.warn("tk:{}", token());
		throw new HanteenBaseException(CommonCode.SERVICE_BUSY);
//		return CommonMessage.ok(res);
	}

	//test thread pool
	@RequestMapping(value = "/pool", method = RequestMethod.GET)
	public String testThreadPoll(){
		asyncDataProcessor.executeRunnable();
		return "helloworld";
	}

	@PostMapping("/avatar/update")
	public void updateAvatar(@RequestParam MultipartFile avatar) throws IOException {
		BufferedImage read = ImageIO.read(new ByteArrayInputStream(avatar.getBytes()));
		return;
	}

	@GetMapping("/register")
	public void register(@RequestParam(value = "portal") String portal) {
		userRegisterServiceManager.getRegisterService(portal).register();
	}

	@GetMapping("/profile")
	public CommonMessage<Object> getProfileView() {
		List<UserProfileView> profileView = getUserInfoService.getProfileView();
		return CommonMessage.ok(profileView);
	}
}
