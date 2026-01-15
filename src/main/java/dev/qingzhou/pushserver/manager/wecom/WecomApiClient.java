package dev.qingzhou.pushserver.manager.wecom;

import dev.qingzhou.pushserver.config.PortalWecomProperties;
import dev.qingzhou.pushserver.exception.PortalException;
import dev.qingzhou.pushserver.exception.PortalStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class WecomApiClient {

    private final RestClient restClient;

    public WecomApiClient(PortalWecomProperties properties) {
        this.restClient = RestClient.builder()
                .baseUrl(properties.getBaseUrl())
                .build();
    }

    public WecomToken getToken(String corpId, String secret) {
        try {
            WecomToken response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/cgi-bin/gettoken")
                            .queryParam("corpid", corpId)
                            .queryParam("corpsecret", secret)
                            .build())
                    .retrieve()
                    .body(WecomToken.class);
            return requireSuccess(response, "gettoken");
        } catch (RestClientException ex) {
            throw new PortalException(PortalStatus.BAD_GATEWAY, "Failed to call WeCom gettoken", ex);
        }
    }

    public WecomAgentInfo getAgentInfo(String accessToken, String agentId) {
        try {
            WecomAgentInfo response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/cgi-bin/agent/get")
                            .queryParam("access_token", accessToken)
                            .queryParam("agentid", agentId)
                            .build())
                    .retrieve()
                    .body(WecomAgentInfo.class);
            return requireSuccess(response, "agent/get");
        } catch (RestClientException ex) {
            throw new PortalException(PortalStatus.BAD_GATEWAY, "Failed to call WeCom agent/get", ex);
        }
    }

    public WecomSendResponse sendMessage(String accessToken, Object payload) {
        try {
            WecomSendResponse response = restClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/cgi-bin/message/send")
                            .queryParam("access_token", accessToken)
                            .build())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .body(WecomSendResponse.class);
            if (response == null) {
                throw new PortalException(PortalStatus.BAD_GATEWAY, "Empty response from WeCom message/send");
            }
            return response;
        } catch (RestClientException ex) {
            throw new PortalException(PortalStatus.BAD_GATEWAY, "Failed to call WeCom message/send", ex);
        }
    }

    private <T extends WecomResponse> T requireSuccess(
            T response,
            String action
    ) {
        if (response == null) {
            throw new PortalException(PortalStatus.BAD_GATEWAY, "Empty response from WeCom " + action);
        }
        if (!response.isSuccess()) {
            throw new PortalException(
                    PortalStatus.BAD_REQUEST,
                    "WeCom " + action + " failed: " + response.getErrmsg() + " (" + response.getErrcode() + ")"
            );
        }
        return response;
    }
}
