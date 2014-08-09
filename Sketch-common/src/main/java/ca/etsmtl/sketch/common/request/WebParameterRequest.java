package ca.etsmtl.sketch.common.request;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

class WebParameterRequest implements ParameterRequest {
    private final String action;
    private final RequestStrategy requestStrategy;

    private Map<String, String> parameters = new HashMap<String, String>();

    public WebParameterRequest(String action, RequestStrategy requestStrategy) {
        this.action = action;
        this.requestStrategy = requestStrategy;
    }

    @Override
    public ParameterRequest addParameter(String name, String value) {
        if (parameters.containsKey(name))
            parameters.remove(name);

        parameters.put(name, value);
        return this;
    }

    @Override
    public void execute() {
        executeForAnswer();
    }

    @Override
    public JSONObject executeForAnswer() {
        return requestStrategy.execute(action, parameters);
    }
}
