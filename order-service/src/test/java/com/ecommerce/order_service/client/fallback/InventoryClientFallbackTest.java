package com.ecommerce.order_service.client.fallback;

import com.ecommerce.order_service.model.dto.request.ReserveInventoryRequest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InventoryClientFallbackTest {

    @Test
    void reserve_returnsZeroAvailability() {
        InventoryClientFallback fallback = new InventoryClientFallback();

        var response = fallback.reserve("SKU-1", ReserveInventoryRequest.builder().quantity(2).build());

        assertThat(response.getProductId()).isEqualTo("SKU-1");
        assertThat(response.getReserved()).isZero();
        assertThat(response.getAvailable()).isZero();
    }
}
