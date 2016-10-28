package com.tenx.ms.retail.product.rest.dto;

import com.tenx.ms.commons.validation.constraints.DollarAmount;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("Product")
public class ProductDto {

    @ApiModelProperty(value = "Product Id", required = true, readOnly = true)
    private Long productId;

    @ApiModelProperty(value = "Store Id", required = true, readOnly = true)
    private Long storeId;

    @ApiModelProperty(value = "Product Name", required = true)
    @NotNull
    @NotEmpty
    private String name;

    @ApiModelProperty(value = "Product Description")
    @Valid
    @NotEmpty
    private String description;

    @ApiModelProperty(value = "Product SKU")
    @Valid
    @Pattern(regexp = "^[A-Za-z0-9]{5,10}$")
    private String sku;

    @ApiModelProperty(value = "Product Price")
    @Valid
    @DollarAmount
    private BigDecimal price;
}
