package ashok.ids.dataapps.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

import ashok.ids.dataapps.common.CommonBase;

import org.springframework.beans.factory.annotation.Value;

import javax.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

@SpringBootApplication
@RestController
public class IDSDataAppSample extends CommonBase {

	@Value("${app.name}")
	private String appName;

	@Value("${app.type}")
	private String appType;

	@Value("${msg.help}")
	private String msgHelp;

	@Value("${msg.provider}")
	private String msgProvider;

	@Value("${msg.consumer}")
	private String msgConsumer;

	@Value("${msg.error}")
	private String msgError;

	private static final String APP_NAME = "\\$app.name";
	private static final String APP_TYPE = "\\$app.type";
	private static final String UNIQ_ID = "\\$uuid";
	private static final String TIME = "\\$time";
	private static final String SECRET = "\\$secret";

	private static final String APP_PIPELINE_PARAM = "_PROCESSED_THROUGH_";

	private static final String PROVIDER = "provider";
	private static final String CONSUMER = "consumer";
	private static final String PROCESSOR = "processor";

	private static Map<String, String> _CONSUMER_STORE = new HashMap<>();

	@GetMapping(path = { "/help" }, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> help() {
		return ok(parseAll(msgHelp));
	}

	@GetMapping(path = { "/**" }, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> provide(HttpServletRequest request) {

		if (!appType.contains(PROVIDER)) {
			return error();
		}

		String path = request.getRequestURI();
		logger.debug("Received request for: {}", path);

		return ok(parseAll(msgProvider));
	}

	@PostMapping(path = { "/**" }, consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> consume(HttpServletRequest request, @RequestParam Map<String, Object> params,
			@RequestBody String input, ModelMap model) {

		if (!appType.contains(CONSUMER)) {
			return error();
		}

		String path = request.getRequestURI();

		logger.debug("Received request for: {}", path);
		logger.debug("With parameters: {}", params);
		logger.debug("With message: {}", input);

		String uniqId = getUniqID();
		_CONSUMER_STORE.put(uniqId, input);
		String parsedMsg = parse(msgConsumer, UNIQ_ID, uniqId);
		parsedMsg = parse(parsedMsg, UNIQ_ID, uniqId);

		parsedMsg = parseAll(parsedMsg);

		ResponseEntity<String> response = null;

		logger.debug("Creating response");
		response = ok(parsedMsg, input);

		logger.debug("Completed processing.");
		logger.debug("Response: {}", response);
		return response;
	}

	@PutMapping(path = { "/**" }, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> process(HttpServletRequest request, @RequestParam Map<String, Object> params,
			@RequestBody String input, ModelMap model) {

		if (!appType.contains(PROCESSOR)) {
			return error();
		}

		String path = request.getRequestURI();

		logger.debug("Received request for: {}", path);
		logger.debug("With parameters: {}", params);
		logger.debug("With message: {}", input);

		logger.debug("Processing...");
		ResponseEntity<String> response = ok(input);
		;
		logger.debug("Completed processing.");

		logger.debug("Response: {}", response);
		return response;
	}

	private String parse(final String msgTpl, String key, String val) {
		return msgTpl.replaceAll(key, val);
	}

	private String parseAll(final String msgTpl) {

		String parsedMsg = parse(msgTpl, APP_NAME, appName);
		parsedMsg = parse(parsedMsg, APP_TYPE, appType);
		parsedMsg = parse(parsedMsg, TIME, getTime());
		parsedMsg = parse(parsedMsg, UNIQ_ID, getUniqID());
		parsedMsg = parse(parsedMsg, SECRET, getSecret());
		return parsedMsg;
	}

	private String addTrail(String responseMsg, String inputMsg) {

		logger.debug("Converting to json: {}", responseMsg);
		JSONObject responseJson = new JSONObject(responseMsg);

		if (null != inputMsg) {
			try {
				JSONObject inputJson = new JSONObject(inputMsg);
				responseJson.put(APP_PIPELINE_PARAM, inputJson.getJSONArray(APP_PIPELINE_PARAM));
				logger.debug("Message after adding previous trail: {}", responseJson);
			} catch (JSONException jex) {
				logger.debug("No previous trail...");
			}
		}

		logger.debug("Adding trail...");
		responseJson.append(APP_PIPELINE_PARAM, appName);
		logger.debug("Message after adding trail: {}", responseJson);
		return responseJson.toString();
	}

	private ResponseEntity<String> ok(String responseMsg, String inputMsg) {

		logger.debug("Creating response");
		return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(addTrail(responseMsg, inputMsg));
	}

	private ResponseEntity<String> ok(String responseMsg) {

		logger.debug("Creating response");
		return ok(responseMsg, null);
	}

	private ResponseEntity<String> error(String inputMsg) {

		return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(addTrail(msgError, inputMsg));
	}

	private ResponseEntity<String> error() {

		return error(null);
	}

	public static void main(String[] args) {
		SpringApplication.run(IDSDataAppSample.class, args);
	}

}
