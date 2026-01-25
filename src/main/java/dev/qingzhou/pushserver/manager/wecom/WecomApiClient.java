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
            throw new PortalException(PortalStatus.BAD_GATEWAY, "调用企业微信 gettoken 接口失败", ex);
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
            throw new PortalException(PortalStatus.BAD_GATEWAY, "调用企业微信 agent/get 接口失败", ex);
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
                throw new PortalException(PortalStatus.BAD_GATEWAY, "企业微信 message/send 响应为空");
            }
            return response;
        } catch (RestClientException ex) {
            throw new PortalException(PortalStatus.BAD_GATEWAY, "调用企业微信 message/send 接口失败", ex);
        }
    }

    private <T extends WecomResponse> T requireSuccess(
            T response,
            String action
    ) {
        if (response == null) {
            throw new PortalException(PortalStatus.BAD_GATEWAY, "企业微信 " + action + " 响应为空");
        }
        if (!response.isSuccess()) {
            if (Integer.valueOf(60020).equals(response.getErrcode())) {
                String ip = "unknown";
                String errmsg = response.getErrmsg();
                if (errmsg != null && errmsg.contains("from ip: ")) {
                    int start = errmsg.indexOf("from ip: ") + 9;
                    int end = errmsg.indexOf(",", start);
                    if (end == -1) {
                        end = errmsg.length();
                    }
                    ip = errmsg.substring(start, end).trim();
                }
                throw new PortalException(
                        PortalStatus.BAD_REQUEST,
                        "企业微信 IP 白名单校验失败。请在企业微信应用设置的“企业可信 IP”列表中添加本服务器 IP [" + ip + "]。"
                );
            }
            throw new PortalException(
                    PortalStatus.BAD_REQUEST,
                    "企业微信 " + action + " 失败: " + response.getErrmsg() + " (" + response.getErrcode() + ")"
            );
        }
        return response;
    }
}
