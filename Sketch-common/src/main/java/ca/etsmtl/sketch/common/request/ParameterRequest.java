package ca.etsmtl.sketch.common.request;

public interface ParameterRequest extends Request {
    ParameterRequest addParameter(String name, String value);
}
