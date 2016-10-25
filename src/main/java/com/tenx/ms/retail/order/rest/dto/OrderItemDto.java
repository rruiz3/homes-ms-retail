package com.tenx.ms.retail.order.rest.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("Order item")
public class OrderItemDto {

    @ApiModelProperty(value = "Product Id", required = true)
    private Long productId;

    @Min(value = 1)
    @ApiModelProperty(value = "Product count", required = true)
    private Long count;
}
