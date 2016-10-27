package com.tenx.ms.retail.order.rest;

import com.tenx.ms.commons.rest.RestConstants;
import com.tenx.ms.retail.order.rest.dto.OrderCreated;
import com.tenx.ms.retail.order.rest.dto.OrderDto;
import com.tenx.ms.retail.order.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "orders", description = "Order API")
@RestController("orderControllerV1")
@RequestMapping(RestConstants.VERSION_ONE + "/orders/")
public class OrderController {

    @Autowired
    private OrderService orderSrvc;

    @ApiOperation(value = "Create a new Order", authorizations = { @Authorization("ROLE_ADMIN") })
    @ApiResponses(
        value = {
            @ApiResponse(code = 201, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 412, message = "Validation error"),
            @ApiResponse(code = 500, message = "Internal server error"),
        }
    )
    @RequestMapping(value = "{storeId:\\d+}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public OrderCreated createNewOrder(
        @ApiParam(name = "storeId", value = "Store id", required = true) @PathVariable() Long storeId,
        @ApiParam(name = "order", value = "Order information", required = true) @RequestBody @Validated OrderDto order) {
        order.setStoreId(storeId);
        return orderSrvc.createOrder(order);
    }
}
