package com.tenx.ms.retail.store.rest.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("Store")
public class StoreDto {

    @ApiModelProperty(value = "Store Id", required = true, readOnly = true)
    private Long storeId;

    @ApiModelProperty(value = "Store Name", required = true)
    @NotNull
    @NotEmpty
    private String name;
}
