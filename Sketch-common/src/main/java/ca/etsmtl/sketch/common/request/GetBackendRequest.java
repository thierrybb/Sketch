package ca.etsmtl.sketch.common.request;


class GetBackendRequest implements BackendRequest {
    private String baseUrl;

    GetBackendRequest(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public ParameterRequest action(String action) {

        return new WebParameterRequest(action, new GetStrategy(baseUrl));
    }
}
