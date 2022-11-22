package com.arrow.forkjoin.start;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ForkJoinApplicationTests {

    @Test
    void contextLoads() {
        TransmittableThreadLocal<String> context = new TransmittableThreadLocal();
        context.set("test");


    }

}
