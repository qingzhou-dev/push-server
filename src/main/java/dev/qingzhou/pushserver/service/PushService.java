package dev.qingzhou.pushserver.service;

import dev.qingzhou.push.core.model.PushResult;
import dev.qingzhou.pushserver.model.dto.openapi.PushRequest;

public interface PushService {

    PushResult push(PushRequest request);
}
