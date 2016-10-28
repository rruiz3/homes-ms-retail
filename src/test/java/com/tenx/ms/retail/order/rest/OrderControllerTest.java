package com.tenx.ms.retail.order.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenx.ms.commons.config.Profiles;
import com.tenx.ms.commons.rest.RestConstants;
import com.tenx.ms.commons.rest.dto.ResourceCreated;
import com.tenx.ms.commons.tests.BaseIntegrationTest;
import com.tenx.ms.retail.order.constants.OrderStatus;
import com.tenx.ms.retail.order.rest.dto.OrderCreated;
import com.tenx.ms.retail.stock.rest.dto.StockDto;
import org.apache.commons.io.FileUtils;
import org.flywaydb.test.annotation.FlywayTest;
import org.flywaydb.test.junit.FlywayTestExecutionListener;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(Profiles.TEST_NOAUTH)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, FlywayTestExecutionListener.class})
public class OrderControllerTest extends BaseIntegrationTest {

    private final String API_VERSION = RestConstants.VERSION_ONE;
    private final String REQUEST_URI_STORE = "%s" + API_VERSION + "/stores/";
    private final String REQUEST_URI_PRODUCT = "%s" + API_VERSION + "/products/%s";
    private final String REQUEST_STOCK = "%s" + API_VERSION + "/stocks/%s";
    private final String REQUEST_ORDER = "%s" + API_VERSION + "/orders/%s";
    private static boolean init = false;
    private static String requestUrl;
    private static String requestProductUrl;
    private static String requestOrderUrl;

    private static ResourceCreated<Long> storeId;
    private static ResourceCreated<Long> productId;
    private static ResourceCreated<Long> newProductId;
    private static final Long INVALID_ID = 999999999L;

    private static StockDto stock;
    private static StockDto stock2;

    @Autowired
    private ObjectMapper mapper;

    @Value("classpath:testJsons/store/create_store.json")
    private File createStore;

    @Value("classpath:testJsons/product/create_product.json")
    private File createProduct;

    @Value("classpath:testJsons/stock/create_stock.json")
    private File createStock;

    @Value("classpath:testJsons/order/create_order.json")
    private File createOrder;

    @Value("classpath:testJsons/order/create_order_back_ordered.json")
    private File createOrderBackOrdered;

    @Value("classpath:testJsons/order/create_order_fail_products.json")
    private File createOrderFailProducts;

    @Value("classpath:testJsons/order/create_order_fail_name.json")
    private File createOrderFailName;

    @Value("classpath:testJsons/order/create_order_fail_last_name.json")
    private File createOrderFailLastName;

    @Value("classpath:testJsons/order/create_order_fail_email.json")
    private File createOrderFailEmail;

    @Value("classpath:testJsons/order/create_order_fail_phone.json")
    private File createOrderFailPhone;

    @Before
    public void init() throws IOException {
        requestUrl = String.format(REQUEST_URI_STORE, getBasePath());

        ResponseEntity<String> responseStore = getStringResponse(requestUrl, FileUtils.readFileToString(createStore), HttpMethod.POST);
        storeId = mapper.readValue(responseStore.getBody(), new TypeReference<ResourceCreated<Long>>() {});
        assertEquals("Store created, status ok response", HttpStatus.OK, responseStore.getStatusCode());

        requestProductUrl = String.format(REQUEST_URI_PRODUCT, getBasePath(), storeId.getId());

        ResponseEntity<String> responseProduct = getStringResponse(requestProductUrl, FileUtils.readFileToString(createProduct), HttpMethod.POST);
        productId = mapper.readValue(responseProduct.getBody(), new TypeReference<ResourceCreated<Long>>() {});
        assertEquals("Product created, status ok response", HttpStatus.OK, responseProduct.getStatusCode());

        ResponseEntity<String> responseAnotherProduct = getStringResponse(requestProductUrl, FileUtils.readFileToString(createProduct), HttpMethod.POST);
        newProductId = mapper.readValue(responseAnotherProduct.getBody(), new TypeReference<ResourceCreated<Long>>() {});
        assertEquals("Product created, status ok response", HttpStatus.OK, responseAnotherProduct.getStatusCode());

        ResponseEntity<String> responseStock1 = getStringResponse(String.format(REQUEST_STOCK + "/%s", getBasePath(), storeId.getId(), productId.getId()), FileUtils.readFileToString(createStock), HttpMethod.POST);
        assertEquals("Stock added/updated for product, status ok response", HttpStatus.NO_CONTENT, responseStock1.getStatusCode());
        ResponseEntity<String> responseS = getStringResponse(String.format(REQUEST_STOCK + "/%s", getBasePath(), storeId.getId(), productId.getId()), null, HttpMethod.GET);
        assertEquals("Stock get OK", HttpStatus.OK, responseS.getStatusCode());
        stock = mapper.readValue(responseS.getBody(), StockDto.class);
        assertEquals("Stock count ok", stock.getCount(), Long.valueOf(25));

        ResponseEntity<String> responseStock2 = getStringResponse(String.format(REQUEST_STOCK + "/%s", getBasePath(), storeId.getId(), newProductId.getId()), FileUtils.readFileToString(createStock), HttpMethod.POST);
        assertEquals("Stock added/updated for another product, status ok response", HttpStatus.NO_CONTENT, responseStock2.getStatusCode());
        ResponseEntity<String> responseSd = getStringResponse(String.format(REQUEST_STOCK + "/%s", getBasePath(), storeId.getId(), newProductId.getId()), null, HttpMethod.GET);
        assertEquals("Stock get OK", HttpStatus.OK, responseSd.getStatusCode());
        stock2 = mapper.readValue(responseS.getBody(), StockDto.class);
        assertEquals("Stock count ok", stock2.getCount(), Long.valueOf(25));

        requestOrderUrl = String.format(REQUEST_ORDER, getBasePath(), storeId.getId());
    }

    @Test
    @FlywayTest
    public void createGetOrderSuccess () throws IOException{
        ResponseEntity<String> responseEntity = getStringResponse(requestOrderUrl, FileUtils.readFileToString(createOrder), HttpMethod.POST);
        assertEquals("Order created, status ok response", HttpStatus.OK, responseEntity.getStatusCode());
        OrderCreated order = mapper.readValue(responseEntity.getBody(), OrderCreated.class);
        assertEquals("Order status ok", OrderStatus.ORDERED, order.getStatus());
        assertThat("Backordered Items size ok", order.getBackOrderedItems().size(), is(0));
    }

    @Test
    @FlywayTest
    public void createOrderBackOrdered () throws IOException{
        ResponseEntity<String> response = getStringResponse(requestOrderUrl, FileUtils.readFileToString(createOrderBackOrdered), HttpMethod.POST);
        assertEquals("HTTP Status code incorrect", HttpStatus.OK, response.getStatusCode());
        OrderCreated orderResponse = mapper.readValue(response.getBody(), OrderCreated.class);
        assertEquals("Order Status should be ORDERED", OrderStatus.ORDERED, orderResponse.getStatus());
        assertThat("Backordered Items size ok", orderResponse.getBackOrderedItems().size(), is(1));
    }

    @Test
    @FlywayTest
    public void createOrderInvalidStore () throws IOException{
        ResponseEntity<String> responseEntity = getStringResponse(String.format(REQUEST_ORDER, getBasePath(), INVALID_ID), FileUtils.readFileToString(createOrder), HttpMethod.POST);
        assertEquals("Order not created", HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    @FlywayTest
    public void createOrderInvalidProducts () throws IOException {
         ResponseEntity<String> responseEntity = getStringResponse(requestOrderUrl, FileUtils.readFileToString(createOrderFailProducts), HttpMethod.POST);
         assertEquals("Order not created", HttpStatus.PRECONDITION_FAILED, responseEntity.getStatusCode());
    }

    @Test
    @FlywayTest
    public void createOrderInvalidName () throws IOException {
         ResponseEntity<String> responseEntity = getStringResponse(requestOrderUrl, FileUtils.readFileToString(createOrderFailName), HttpMethod.POST);
         assertEquals("Order not created", HttpStatus.PRECONDITION_FAILED, responseEntity.getStatusCode());
    }

    @Test
    @FlywayTest
    public void createOrderInvalidLastName () throws IOException {
         ResponseEntity<String> responseEntity = getStringResponse(requestOrderUrl, FileUtils.readFileToString(createOrderFailLastName), HttpMethod.POST);
         assertEquals("Order not created", HttpStatus.PRECONDITION_FAILED, responseEntity.getStatusCode());
    }

    @Test
    @FlywayTest
    public void createOrderInvalidEmail () throws IOException {
         ResponseEntity<String> responseEntity = getStringResponse(requestOrderUrl, FileUtils.readFileToString(createOrderFailEmail), HttpMethod.POST);
         assertEquals("Order not created", HttpStatus.PRECONDITION_FAILED, responseEntity.getStatusCode());
    }

    @Test
    @FlywayTest
    public void createOrderInvalidPhone () throws IOException {
         ResponseEntity<String> responseEntity = getStringResponse(requestOrderUrl, FileUtils.readFileToString(createOrderFailPhone), HttpMethod.POST);
         assertEquals("Order not created", HttpStatus.PRECONDITION_FAILED, responseEntity.getStatusCode());
    }
}
