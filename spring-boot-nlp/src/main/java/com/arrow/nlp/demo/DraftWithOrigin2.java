package com.arrow.nlp.demo;

import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ClientHandshakeBuilder;

/**
 * @author ren xiao fei
 * @version 1.0.0
 * @description
 * @date 2022-11-10 18:39
 **/
public class DraftWithOrigin2 extends Draft_6455 {

    private String originUrl;

    public DraftWithOrigin2(String originUrl) {
        this.originUrl = originUrl;
    }

    @Override
    public Draft copyInstance() {
        System.out.println(originUrl);
        return new DraftWithOrigin(originUrl);
    }

    @Override
    public ClientHandshakeBuilder postProcessHandshakeRequestAsClient(ClientHandshakeBuilder request) {
        super.postProcessHandshakeRequestAsClient(request);
        request.put("Origin", originUrl);
        return request;
    }
}
