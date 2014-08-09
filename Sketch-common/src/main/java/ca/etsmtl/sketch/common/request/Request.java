package ca.etsmtl.sketch.common.request;

import org.json.JSONObject;

public interface Request {
    void execute();
    JSONObject executeForAnswer();
}
