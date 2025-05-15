package io.github.gabrielpetry23.ecommerceapi.service;

import io.github.gabrielpetry23.ecommerceapi.controller.dto.DashboardResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final JdbcTemplate jdbcTemplate;

    public DashboardResponseDTO getDashboardMetrics() {
        List<Map<String, Object>> ordersByMonth = getOrdersByMonth();
        List<Map<String, Object>> topSellingProducts = getTopSellingProducts();
        BigDecimal totalSalesValue = getTotalSalesValue();

        return new DashboardResponseDTO(ordersByMonth, topSellingProducts, totalSalesValue);
    }

    private List<Map<String, Object>> getOrdersByMonth() {
        String sql = "SELECT " +
                "EXTRACT(YEAR FROM created_at) AS year, " +
                "EXTRACT(MONTH FROM created_at) AS month, " +
                "COUNT(*) AS order_count " +
                "FROM orders " +
                "GROUP BY EXTRACT(YEAR FROM created_at), EXTRACT(MONTH FROM created_at) " +
                "ORDER BY year DESC, month DESC";
        return jdbcTemplate.queryForList(sql);
    }

    private List<Map<String, Object>> getTopSellingProducts() {
        String sql = "SELECT " +
                "p.name AS product_name, " +
                "SUM(oi.quantity) AS total_quantity_sold " +
                "FROM order_items oi " +
                "JOIN products p ON oi.product_id = p.id " +
                "GROUP BY p.name " +
                "ORDER BY total_quantity_sold DESC " +
                "LIMIT 10"; // Limit to top 10 products
        return jdbcTemplate.queryForList(sql);
    }

    private BigDecimal getTotalSalesValue() {
        String sql = "SELECT SUM(total) FROM orders WHERE status IN ('PAID', 'IN_PREPARATION', 'IN_DELIVERY', 'DELIVERED')";
        return jdbcTemplate.queryForObject(sql, BigDecimal.class);
    }
}