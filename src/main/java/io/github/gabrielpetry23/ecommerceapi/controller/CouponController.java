package io.github.gabrielpetry23.ecommerceapi.controller;

import io.github.gabrielpetry23.ecommerceapi.controller.dto.CouponDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.mappers.CouponMapper;
import io.github.gabrielpetry23.ecommerceapi.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/coupons")
@RequiredArgsConstructor
@Tag(name = "Coupon", description = "Endpoints for managing coupons")
public class CouponController implements GenericController{

    private final CouponService service;
    private final CouponMapper mapper;

    @Operation(summary = "Create a new coupon", description = "Endpoint to create a new coupon. Requires ADMIN or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Coupon created successfully",
                    headers = @Header(name = "Location", description = "URI of the created coupon", schema = @Schema(type = "string", format = "uri"))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Object> create(
            @Parameter(description = "Coupon data to be created", required = true, schema = @Schema(implementation = CouponDTO.class))
            @RequestBody CouponDTO couponDTO) {
        var coupon = mapper.toEntity(couponDTO);
        service.save(coupon);
        URI location = generateHeaderLocation(coupon.getId());
        return ResponseEntity.created(location).build();
    }

    @Operation(summary = "List all coupons", description = "Endpoint to retrieve a paginated list of all coupons. Requires ADMIN or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paginated list of coupons retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Page<CouponDTO>> findAll(
            @Parameter(description = "Page number", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        Page<CouponDTO> coupons = service.findAll(page, size);
        return ResponseEntity.ok(coupons);
    }
}
