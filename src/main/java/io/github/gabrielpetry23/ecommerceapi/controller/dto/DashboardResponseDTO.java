package io.github.gabrielpetry23.ecommerceapi.controller.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record DashboardResponseDTO(
        List<Map<String, Object>> ordersByMonth,
        List<Map<String, Object>> topSellingProducts,
        BigDecimal totalSalesValue
) {
}
