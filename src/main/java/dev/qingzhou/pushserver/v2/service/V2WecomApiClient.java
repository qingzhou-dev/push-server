package dev.qingzhou.pushserver.v2.service;

import dev.qingzhou.pushserver.v2.config.V2WecomProperties;
import dev.qingzhou.pushserver.v2.wecom.V2WecomAgentInfo;
import dev.qingzhou.pushserver.v2.wecom.V2WecomSendResponse;
import dev.qingzhou.pushserver.v2.wecom.V2WecomToken;
import dev.qingzhou.pushserver.v2.web.V2Exception;
import dev.qingzhou.pushserver.v2.web.V2Status;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class V2WecomApiClient {

    private final RestClient restClient;

    public V2WecomApiClient(V2WecomProperties properties) {
        this.restClient = RestClient.builder()
                .baseUrl(properties.getBaseUrl())
                .build();
    }

    public V2WecomToken getToken(String corpId, String secret) {
        try {
            V2WecomToken response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/cgi-bin/gettoken")
                            .queryParam("corpid", corpId)
                            .queryParam("corpsecret", secret)
                            .build())
                    .retrieve()
                    .body(V2WecomToken.class);
            return requireSuccess(response, "gettoken");
        } catch (RestClientException ex) {
            throw new V2Exception(V2Status.BAD_GATEWAY, "Failed to call WeCom gettoken", ex);
        }
    }

    public V2WecomAgentInfo getAgentInfo(String accessToken, String agentId) {
        try {
            V2WecomAgentInfo response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/cgi-bin/agent/get")
                            .queryParam("access_token", accessToken)
                            .queryParam("agentid", agentId)
                            .build())
                    .retrieve()
                    .body(V2WecomAgentInfo.class);
            return requireSuccess(response, "agent/get");
        } catch (RestClientException ex) {
            throw new V2Exception(V2Status.BAD_GATEWAY, "Failed to call WeCom agent/get", ex);
        }
    }

    public V2WecomSendResponse sendMessage(String accessToken, Object payload) {
        try {
            V2WecomSendResponse response = restClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/cgi-bin/message/send")
                            .queryParam("access_token", accessToken)
                            .build())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .body(V2WecomSendResponse.class);
            if (response == null) {
                throw new V2Exception(V2Status.BAD_GATEWAY, "Empty response from WeCom message/send");
            }
            return response;
        } catch (RestClientException ex) {
            throw new V2Exception(V2Status.BAD_GATEWAY, "Failed to call WeCom message/send", ex);
        }
    }

    private <T extends dev.qingzhou.pushserver.v2.wecom.V2WecomResponse> T requireSuccess(
            T response,
            String action
    ) {
        if (response == null) {
            throw new V2Exception(V2Status.BAD_GATEWAY, "Empty response from WeCom " + action);
        }
        if (!response.isSuccess()) {
            throw new V2Exception(
                    V2Status.BAD_REQUEST,
                    "WeCom " + action + " failed: " + response.getErrmsg() + " (" + response.getErrcode() + ")"
            );
        }
        return response;
    }
}
