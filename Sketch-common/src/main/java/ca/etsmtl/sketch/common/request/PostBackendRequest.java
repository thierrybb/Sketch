package ca.etsmtl.sketch.common.request;


class PostBackendRequest implements BackendRequest {
    private String baseUrl;

    PostBackendRequest(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public ParameterRequest action(String action) {
        return new WebParameterRequest(action, new PostStrategy(baseUrl));
    }
}