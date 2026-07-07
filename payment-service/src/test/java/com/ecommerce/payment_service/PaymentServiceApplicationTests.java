package com.ecommerce.payment_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "eureka.client.enabled=false",
        "spring.kafka.listener.auto-startup=false"
})
class PaymentServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
