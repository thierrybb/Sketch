package ca.etsmtl.sketch.common.request;

import org.json.JSONObject;

import java.util.Map;

interface RequestStrategy {
    JSONObject execute(String action, Map<String, String> parameter);
}
