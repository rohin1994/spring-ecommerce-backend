package com.ecommerce.order_service;

import com.ecommerce.order_service.client.InventoryClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
class OrderServiceApplicationTests {

	@MockitoBean
	private InventoryClient inventoryClient;

	@Test
	void contextLoads() {
	}

}
