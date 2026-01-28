package dev.qingzhou.pushserver.controller.wecom;

import dev.qingzhou.pushserver.manager.wecom.AesException;
import dev.qingzhou.pushserver.manager.wecom.WXBizMsgCrypt;
import dev.qingzhou.pushserver.manager.wecom.WecomMessageParser;
import dev.qingzhou.pushserver.manager.wecom.WecomMessagePayload;
import dev.qingzhou.pushserver.model.entity.portal.PortalCorpConfig;
import dev.qingzhou.pushserver.model.entity.portal.PortalWecomApp;
import dev.qingzhou.pushserver.service.PortalCorpConfigService;
import dev.qingzhou.pushserver.service.PortalWecomAppService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/v2/wecom/callback/{appId}")
public class WecomCallbackController {

    private final PortalWecomAppService wecomAppService;
    private final PortalCorpConfigService corpConfigService;

    public WecomCallbackController(PortalWecomAppService wecomAppService, PortalCorpConfigService corpConfigService) {
        this.wecomAppService = wecomAppService;
        this.corpConfigService = corpConfigService;
    }

    /**
     * 企业微信回调 URL 验证 (GET)
     */
    @GetMapping
    public String verify(
            @PathVariable("appId") Long appId,
            @RequestParam("msg_signature") String signature,
            @RequestParam("timestamp") String timestamp,
            @RequestParam("nonce") String nonce,
            @RequestParam("echostr") String echostr) {
        
        log.info("Received WeCom callback verification for appId={}: signature={}, timestamp={}, nonce={}, echostr={}",
                appId, signature, timestamp, nonce, echostr);

        try {
            WXBizMsgCrypt wxcpt = getWxCrypt(appId);
            return wxcpt.VerifyURL(signature, timestamp, nonce, echostr);
        } catch (AesException e) {
            log.error("WeCom verification failed", e);
            return "FAILED: " + e.getMessage();
        } catch (Exception e) {
            log.error("System error during verification", e);
            return "ERROR";
        }
    }

    /**
     * 企业微信消息/事件推送 (POST)
     */
    @PostMapping
    public String handleMessage(
            @PathVariable("appId") Long appId,
            @RequestParam("msg_signature") String signature,
            @RequestParam("timestamp") String timestamp,
            @RequestParam("nonce") String nonce,
            @RequestBody String body) {

        log.info("Received WeCom message for appId={}: signature={}, timestamp={}, nonce={}", appId, signature, timestamp, nonce);

        try {
            WXBizMsgCrypt wxcpt = getWxCrypt(appId);
            String decryptedMsg = wxcpt.DecryptMsg(signature, timestamp, nonce, body);
            log.info("Decrypted XML: {}", decryptedMsg);
            
            WecomMessagePayload payload = WecomMessageParser.parse(decryptedMsg);
            log.info("Parsed Payload: {}", payload);
            
            // TODO: 后续可以根据 payload.getMsgType() 或 payload.getEvent() 分发到不同的处理器
            
            return "success";
        } catch (AesException e) {
            log.error("WeCom message decryption failed", e);
            return "FAILED"; // 企业微信要求处理失败不返回 success，会重试
        } catch (Exception e) {
            log.error("System error during message handling", e);
            return "ERROR";
        }
    }

    private WXBizMsgCrypt getWxCrypt(Long appId) throws AesException {
        PortalWecomApp app = wecomAppService.getById(appId);
        if (app == null) {
            throw new AesException(AesException.IllegalAesKey, "App not found");
        }
        
        // 校验配置是否完整
        if (app.getToken() == null || app.getEncodingAesKey() == null) {
             throw new AesException(AesException.IllegalAesKey, "Token or EncodingAESKey not configured for this app");
        }

        PortalCorpConfig corpConfig = corpConfigService.getByUserId(app.getUserId());
        if (corpConfig == null || corpConfig.getCorpId() == null) {
             throw new AesException(AesException.ValidateCorpidError, "CorpConfig not found");
        }

        return new WXBizMsgCrypt(app.getToken(), app.getEncodingAesKey(), corpConfig.getCorpId());
    }
}