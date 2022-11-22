package com.arrow.nlp.demo;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

/**
 * @author ren xiao fei
 * @version 1.0.0
 * @description
 * @date 2022-11-10 18:40
 **/
public class AiuiWsDemo {
    // 服务地址
    private static final String BASE_URL = "ws://wsapi.xfyun.cn/v1/aiui";
    private static final String ORIGIN = "http://wsapi.xfyun.cn";

    // 应用ID，见AIUI开放平台
    private static final String APPID = "4654bbbd";
    // APIKEY，见AIUI开放平台
    private static final String APIKEY = "43289070bf71b850d91b2ce2de95415c";
    // 每帧音频数据大小，单位字节
    private static final int CHUNCKED_SIZE = 1280;
    // 音频文件位置
    private static final String FILE_PATH = "D://test/date.pcm";
    // 文本
    private static final String TEXT = "打开百度";
    // 结束数据发送标记（必传）
    private static final String END_FLAG = "--end--";
    // 配置参数
    private static final String param = "{\"auth_id\":\"51f7f50559e29aef49a7c4dd8f181218\",\"data_type\":\"text\",\"scene\":\"main_box\"}";

    // main()方法，直接运行，控制台输出服务端结果
    public static void main(String[] args) throws Exception {
        URI url = new URI(BASE_URL + getHandShakeParams());
        DraftWithOrigin2 draft = new DraftWithOrigin2(ORIGIN);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        MyWebSocketClient client = new MyWebSocketClient(url, draft, countDownLatch);
        client.connect();
        while (!client.getReadyState().equals(WebSocket.READYSTATE.OPEN)) {
            System.out.println("连接中");
            Thread.sleep(1000);
        }
        // 发送音频
        /*byte[] bytes = new byte[CHUNCKED_SIZE];
        try (RandomAccessFile raf = new RandomAccessFile(FILE_PATH, "r")) {
            int len = -1;
            while ((len = raf.read(bytes)) != -1) {
                if (len < CHUNCKED_SIZE) {
                    bytes = Arrays.copyOfRange(bytes, 0, len);
                }
                send(client, bytes);
                Thread.sleep(40);
            }
            send(client, END_FLAG.getBytes());
            System.out.println("发送结束标识完成");
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        // TODO 发送文本
        send(client,TEXT.getBytes(StandardCharsets.UTF_8));
        send(client, END_FLAG.getBytes());
        System.out.println("发送结束标识完成");

        System.out.println("等待连接关闭");
        countDownLatch.await();
    }

    // 拼接握手参数
    private static String getHandShakeParams() {
        String paramBase64 = new String(Base64.encodeBase64(param.getBytes(StandardCharsets.UTF_8)));
        String curtime = System.currentTimeMillis() / 1000L + "";
        String signtype = "sha256";
        String originStr = APIKEY + curtime + paramBase64;
        String checksum = getSHA256Str(originStr);
        String handshakeParam = "?appid=" + APPID + "&checksum=" + checksum + "&curtime=" + curtime + "&param=" + paramBase64 + "&signtype=" + signtype;
        return handshakeParam;
    }

    // 发送数据
    private static void send(WebSocketClient client, byte[] bytes) {
        if (client.isClosed()) {
            throw new RuntimeException("client connect closed!");
        }
        client.send(bytes);
    }

    private static class MyWebSocketClient extends WebSocketClient {

        private CountDownLatch countDownLatch;

        public MyWebSocketClient(URI serverUri, Draft protocolDraft, CountDownLatch countDownLatch) {
            super(serverUri, protocolDraft);
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void onOpen(ServerHandshake handshake) {
            System.out.println("打开连接, code:" + handshake.getHttpStatusMessage());
        }

        @Override
        public void onMessage(String msg) {
            System.out.println(msg);
        }

        @Override
        public void onError(Exception e) {
            System.out.println("连接发生错误：" + e.getMessage() + ", " + new Date());
            e.printStackTrace();
        }

        @Override
        public void onClose(int arg0, String arg1, boolean arg2) {
            System.out.println("链接已关闭" + "," + new Date());
            countDownLatch.countDown();
        }

        @Override
        public void onMessage(ByteBuffer bytes) {
            try {
                System.out.println("服务端返回：" + new String(bytes.array(), "utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    // 利用Apache的工具类实现SHA-256加密
    private static String getSHA256Str(String str) {
        MessageDigest messageDigest;
        String encdeStr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hash = messageDigest.digest(str.getBytes(StandardCharsets.UTF_8));
            encdeStr = Hex.encodeHexString(hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return encdeStr;
    }
}
