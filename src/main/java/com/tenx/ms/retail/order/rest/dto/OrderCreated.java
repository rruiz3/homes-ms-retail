package com.tenx.ms.retail.order.rest.dto;


import com.tenx.ms.retail.order.constants.OrderStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("OrderCreated")
public class OrderCreated {

    @ApiModelProperty(value = "Order Id")
    private Long orderId;

    @ApiModelProperty(value = "Order status")
    private OrderStatus status;

    @ApiModelProperty(value = "Backordered items")
    private List<OrderItemDto> backOrderedItems;
}
