package com.tenx.ms.retail.product.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenx.ms.commons.config.Profiles;
import com.tenx.ms.commons.rest.RestConstants;
import com.tenx.ms.commons.rest.dto.ResourceCreated;
import com.tenx.ms.commons.tests.BaseIntegrationTest;
import com.tenx.ms.retail.product.rest.dto.ProductDto;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(Profiles.TEST_NOAUTH)
@SuppressWarnings("PMD")
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, FlywayTestExecutionListener.class})
public class ProductControllerTest extends BaseIntegrationTest {

    private final String API_VERSION = RestConstants.VERSION_ONE;
    private final String REQUEST_URI = "%s" + API_VERSION + "/stores/";
    private final String REQUEST_URI_PRODUCT = "%s" + API_VERSION + "/products/%s";
    private final String REQUEST_DELETE = "%s" + API_VERSION + "/products";
    private static boolean init = false;
    private static String requestProductUrl;

    private static ResourceCreated<Long> storeId;

    @Autowired
    private ObjectMapper mapper;

    @Value("classpath:testJsons/product/create_store.json")
    private File createStore;

    @Value("classpath:testJsons/product/create_product.json")
    private File createProduct;

    @Value("classpath:testJsons/product/create_product_name_null.json")
    private File createProductFailNameNull;

    @Value("classpath:testJsons/product/create_product_name_empty.json")
    private File createProductFailNameEmpty;

    @Value("classpath:testJsons/product/create_product_fail_price.json")
    private File createProductFailPrice;

    @Value("classpath:testJsons/product/create_product_fail_description.json")
    private File createProductFailDescription;

    @Value("classpath:testJsons/product/create_product_fail_sku.json")
    private File createProductFailSku;

    @Value("classpath:testJsons/product/create_product_fail_sku_larger.json")
    private File createProductFailSkuLarger;

    @Before
    public void init() throws IOException {
        ResponseEntity<String> responseEntity = getStringResponse(String.format(REQUEST_URI, getBasePath()), FileUtils.readFileToString(createStore), HttpMethod.POST);
        storeId = mapper.readValue(responseEntity.getBody(), new TypeReference<ResourceCreated<Long>>() {});

        requestProductUrl = String.format(REQUEST_URI_PRODUCT, getBasePath(), storeId.getId());
    }

    @Test
    @FlywayTest
    public void createProductFullSuccess() throws IOException {
        ResponseEntity<String> responseEntity = getStringResponse(requestProductUrl, FileUtils.readFileToString(createProduct), HttpMethod.POST);
        assertNotNull(responseEntity);
        assertEquals("Product created, status ok response", HttpStatus.OK, responseEntity.getStatusCode());

        ResourceCreated<Long> productId = mapper.readValue(responseEntity.getBody(), new TypeReference<ResourceCreated<Long>>() {});
        ResponseEntity<String> responseProduct = getStringResponse(String.format(requestProductUrl + "/" + productId.getId()), null, HttpMethod.GET);
        assertEquals("Get product, ok response", HttpStatus.OK, responseProduct.getStatusCode());

        ProductDto product = mapper.readValue(responseProduct.getBody(), ProductDto.class);
        assertEquals("Product id ok", productId.getId(), product.getProductId());
        assertEquals("Product name ok", "ProductName", product.getName());
    }

    @Test
    public void createProductFailNameNull() throws IOException {
        ResponseEntity<String> responseEntity = getStringResponse(requestProductUrl, FileUtils.readFileToString(createProductFailNameNull), HttpMethod.POST);
        assertEquals("Invalid data", HttpStatus.PRECONDITION_FAILED, responseEntity.getStatusCode());
    }

    @Test
    public void createProductFailNameEmpty() throws IOException {
        ResponseEntity<String> responseEntity = getStringResponse(requestProductUrl, FileUtils.readFileToString(createProductFailNameEmpty), HttpMethod.POST);
        assertEquals("Invalid data", HttpStatus.PRECONDITION_FAILED, responseEntity.getStatusCode());
    }

    @Test
    @FlywayTest
    public void createProductFailInvalidPrice() throws IOException {
        ResponseEntity<String> responseEntity = getStringResponse(requestProductUrl, FileUtils.readFileToString(createProductFailPrice), HttpMethod.POST);
        assertEquals("Invalid data", HttpStatus.PRECONDITION_FAILED, responseEntity.getStatusCode());
    }

    @Test
    @FlywayTest
    public void createProductFailInvalidDescription() throws IOException {
        ResponseEntity<String> responseEntity = getStringResponse(requestProductUrl, FileUtils.readFileToString(createProductFailDescription), HttpMethod.POST);
        assertEquals("Invalid data", HttpStatus.PRECONDITION_FAILED, responseEntity.getStatusCode());
    }

    @Test
    @FlywayTest
    public void createProductFailInvalidSkuShorter() throws IOException {
        ResponseEntity<String> responseEntity = getStringResponse(requestProductUrl, FileUtils.readFileToString(createProductFailSku), HttpMethod.POST);
        assertEquals("Invalid data", HttpStatus.PRECONDITION_FAILED, responseEntity.getStatusCode());
    }

    @Test
    @FlywayTest
    public void createProductFailInvalidSkuLarger() throws IOException {
        ResponseEntity<String> responseEntity = getStringResponse(requestProductUrl, FileUtils.readFileToString(createProductFailSkuLarger), HttpMethod.POST);
        assertEquals("Invalid data", HttpStatus.PRECONDITION_FAILED, responseEntity.getStatusCode());
    }

    @Test
    public void getProductByIdFail() {
        ResponseEntity<String> responseEntity = getStringResponse(requestProductUrl + "/999999999", null, HttpMethod.GET);
        assertEquals("Get a prodyct by Id, not found response", HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void deleteProductNotFound() {
        ResponseEntity<String> responseEntity = getStringResponse(String.format(REQUEST_DELETE, getBasePath()) + "/0000000", null, HttpMethod.DELETE);
        assertEquals("Delete product, not found response", HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    @FlywayTest
    public void deleteProductSuccess() throws IOException {
        ResponseEntity<String> responseEntity = getStringResponse(requestProductUrl, FileUtils.readFileToString(createProduct), HttpMethod.POST);
        ResourceCreated<Long> productId = mapper.readValue(responseEntity.getBody(), new TypeReference<ResourceCreated<Long>>() {});

        ResponseEntity<String> response = getStringResponse(String.format(REQUEST_DELETE, getBasePath()) + "/" + productId.getId(), null, HttpMethod.DELETE);
        assertEquals("Delete product, success response", HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    @FlywayTest
    public void deleteStoreAndProduct() throws IOException {
        ResponseEntity<String> responseEntity = getStringResponse(String.format(REQUEST_URI, getBasePath()), FileUtils.readFileToString(createStore), HttpMethod.POST);
        assertNotNull(responseEntity);
        assertEquals("Product created, status ok response", HttpStatus.OK, responseEntity.getStatusCode());
        ResourceCreated<Long> newStoreId = mapper.readValue(responseEntity.getBody(), new TypeReference<ResourceCreated<Long>>() {});

        ResponseEntity<String> responseProduct = getStringResponse(String.format(REQUEST_URI_PRODUCT, getBasePath(), newStoreId.getId()), FileUtils.readFileToString(createProduct), HttpMethod.POST);
        assertNotNull(responseProduct);
        assertEquals("Product created, status ok response", HttpStatus.OK, responseProduct.getStatusCode());
        ResourceCreated<Long> productId = mapper.readValue(responseProduct.getBody(), new TypeReference<ResourceCreated<Long>>() {});

        ResponseEntity<String> response = getStringResponse(String.format(REQUEST_URI, getBasePath()) + "/" + newStoreId.getId(), null, HttpMethod.DELETE);
        assertEquals("Delete store, success response", HttpStatus.NO_CONTENT, response.getStatusCode());

        ResponseEntity<String> responseGet = getStringResponse(String.format(String.format(REQUEST_URI_PRODUCT, getBasePath(), newStoreId.getId()) + "/" + productId.getId()), null, HttpMethod.GET);
        assertEquals("Get product, not found response", HttpStatus.NOT_FOUND, responseGet.getStatusCode());
    }
}
