package com.tenx.ms.retail.order.rest.dto;

import com.tenx.ms.commons.validation.constraints.Email;
import com.tenx.ms.commons.validation.constraints.PhoneNumber;
import com.tenx.ms.retail.order.constants.OrderStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("Order")
public class OrderDto {

    @ApiModelProperty(value = "Order id", readOnly = true)
    private Long id;

    @ApiModelProperty(value = "Store id", readOnly = true)
    private Long storeId;

    @ApiModelProperty(value = "Order date")
    private LocalDateTime orderDate;

    @ApiModelProperty(value = "Order status")
    private OrderStatus orderStatus;

    @Size(min = 1)
    @ApiModelProperty(value = "Order items")
    private List<OrderItemDto> products;

    @Valid
    @Pattern(regexp = "^[A-Za-z]+$")
    @ApiModelProperty(value = "Purchaser first name")
    private String firstName;

    @Valid
    @Pattern(regexp = "^[A-Za-z]+$")
    @ApiModelProperty(value = "Purchaser last name")
    private String lastName;

    @ApiModelProperty(value = "Purchaser email")
    @Email
    private String email;

    @ApiModelProperty(value = "Purchaser phone number")
    @PhoneNumber
    private String phone;
}
