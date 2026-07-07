package com.ecommerce.product_service.service.impl;

import com.ecommerce.common.exception.BusinessException;
import com.ecommerce.product_service.model.dto.request.DiscountCreateRequest;
import com.ecommerce.product_service.model.dto.request.DiscountUpdateRequest;
import com.ecommerce.product_service.model.dto.response.DiscountResponse;
import com.ecommerce.product_service.model.entity.Discount;
import com.ecommerce.product_service.model.entity.Product;
import com.ecommerce.product_service.model.enums.DiscountType;
import com.ecommerce.product_service.repository.DiscountRepository;
import com.ecommerce.product_service.service.DiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class DiscountServiceImpl implements DiscountService {

    private final DiscountRepository discountRepository;

    @Override
    public DiscountResponse createDiscount(DiscountCreateRequest request) {
        if (discountRepository.existsByCodeIgnoreCase(request.getCode())) {
            throw new BusinessException("Discount code already exists");
        }

        Discount discount = new Discount();
        discount.setCode(request.getCode());
        discount.setType(request.getType());
        discount.setValue(request.getValue());
        discount.setProductIds(copyList(request.getProductIds()));
        discount.setCategoryCodes(copyList(request.getCategoryCodes()));
        discount.setStartsAt(request.getStartsAt());
        discount.setEndsAt(request.getEndsAt());
        discount.setActive(request.isActive());

        return mapToResponse(discountRepository.save(discount));
    }

    @Override
    public DiscountResponse updateDiscount(String id, DiscountUpdateRequest request) {
        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Discount not found: " + id, HttpStatus.NOT_FOUND));

        if (!discount.getCode().equalsIgnoreCase(request.getCode())
                && discountRepository.existsByCodeIgnoreCase(request.getCode())) {
            throw new BusinessException("Discount code already exists");
        }

        discount.setCode(request.getCode());
        discount.setType(request.getType());
        discount.setValue(request.getValue());
        discount.setProductIds(copyList(request.getProductIds()));
        discount.setCategoryCodes(copyList(request.getCategoryCodes()));
        discount.setStartsAt(request.getStartsAt());
        discount.setEndsAt(request.getEndsAt());
        discount.setActive(request.isActive());

        return mapToResponse(discountRepository.save(discount));
    }

    @Override
    @Transactional(readOnly = true)
    public DiscountResponse getDiscountById(String id) {
        return discountRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new BusinessException("Discount not found: " + id, HttpStatus.NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiscountResponse> getAllDiscounts() {
        return discountRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public void deleteDiscount(String id) {
        if (!discountRepository.existsById(id)) {
            throw new BusinessException("Discount not found: " + id, HttpStatus.NOT_FOUND);
        }
        discountRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AppliedDiscount> findBestDiscountForProduct(Product product) {
        Instant now = Instant.now();
        return discountRepository.findAll().stream()
                .filter(discount -> isCurrentlyActive(discount, now))
                .filter(discount -> appliesToProduct(discount, product))
                .map(discount -> toAppliedDiscount(discount, product.getBasePrice()))
                .min(Comparator.comparing(AppliedDiscount::discountedPrice));
    }

    private boolean isCurrentlyActive(Discount discount, Instant now) {
        if (!discount.isActive()) {
            return false;
        }
        if (discount.getStartsAt() != null && now.isBefore(discount.getStartsAt())) {
            return false;
        }
        return discount.getEndsAt() == null || !now.isAfter(discount.getEndsAt());
    }

    private boolean appliesToProduct(Discount discount, Product product) {
        if (product.getId() != null && discount.getProductIds() != null
                && discount.getProductIds().contains(product.getId())) {
            return true;
        }
        return product.getCategory() != null
                && product.getCategory().getCode() != null
                && discount.getCategoryCodes() != null
                && discount.getCategoryCodes().contains(product.getCategory().getCode());
    }

    private AppliedDiscount toAppliedDiscount(Discount discount, BigDecimal basePrice) {
        BigDecimal discountedPrice = calculateDiscountedPrice(discount, basePrice);
        return new AppliedDiscount(discountedPrice, buildDiscountLabel(discount));
    }

    private BigDecimal calculateDiscountedPrice(Discount discount, BigDecimal basePrice) {
        BigDecimal discounted = switch (discount.getType()) {
            case PERCENTAGE -> basePrice.subtract(
                    basePrice.multiply(discount.getValue()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
            case FIXED -> basePrice.subtract(discount.getValue());
        };
        return discounted.max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
    }

    private String buildDiscountLabel(Discount discount) {
        if (discount.getType() == DiscountType.PERCENTAGE) {
            return discount.getValue().stripTrailingZeros().toPlainString() + "% OFF";
        }
        return discount.getValue().stripTrailingZeros().toPlainString() + " OFF";
    }

    private List<String> copyList(List<String> values) {
        return values == null ? new ArrayList<>() : new ArrayList<>(values);
    }

    private DiscountResponse mapToResponse(Discount discount) {
        return new DiscountResponse(
                discount.getId(),
                discount.getCode(),
                discount.getType(),
                discount.getValue(),
                discount.getProductIds(),
                discount.getCategoryCodes(),
                discount.getStartsAt(),
                discount.getEndsAt(),
                discount.isActive()
        );
    }
}
