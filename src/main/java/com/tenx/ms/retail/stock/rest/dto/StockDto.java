package com.tenx.ms.retail.stock.rest.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;


@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockDto {

    @ApiModelProperty(value = "Product Id", required = true, readOnly = true)
    private Long productId;

    @ApiModelProperty(value = "Store Id", required = true, readOnly = true)
    private Long storeId;

    @ApiModelProperty(value = "Product count")
    @Valid
    @NotNull
    @Min(0)
    private Long count;
}
