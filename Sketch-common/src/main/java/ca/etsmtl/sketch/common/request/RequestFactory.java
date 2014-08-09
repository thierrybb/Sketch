package ca.etsmtl.sketch.common.request;

public class RequestFactory {
    public static BackendRequest newGet(String baseUrl) {
        return new GetBackendRequest(baseUrl);
    }

    public static BackendRequest newPost(String baseUrl) {
        return new PostBackendRequest(baseUrl);
    }
}
