package com.tenx.ms.retail.order.domain;

import com.tenx.ms.commons.validation.constraints.Email;
import com.tenx.ms.commons.validation.constraints.PhoneNumber;
import com.tenx.ms.retail.order.constants.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_complete")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @Column(name="order_date", nullable = false)
    private Timestamp orderDate;

    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true, mappedBy = "orderId", fetch = FetchType.LAZY)
    private List<OrderItemEntity> products;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Email
    @Column(name = "email", nullable = false)
    private String email;

    @PhoneNumber
    @Column(name = "phone", nullable = false)
    private String phone;

}
