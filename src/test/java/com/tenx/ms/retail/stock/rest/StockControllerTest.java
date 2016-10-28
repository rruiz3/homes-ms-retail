package com.tenx.ms.retail.stock.rest;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenx.ms.commons.config.Profiles;
import com.tenx.ms.commons.rest.RestConstants;
import com.tenx.ms.commons.rest.dto.ResourceCreated;
import com.tenx.ms.commons.tests.BaseIntegrationTest;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(Profiles.TEST_NOAUTH)
@SuppressWarnings("PMD")
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, FlywayTestExecutionListener.class})
public class StockControllerTest extends BaseIntegrationTest {

    private final String API_VERSION = RestConstants.VERSION_ONE;
    private final String REQUEST_URI_STORE = "%s" + API_VERSION + "/stores/";
    private final String REQUEST_URI_PRODUCT = "%s" + API_VERSION + "/products/%s";
    private final String REQUEST_STOCK = "%s" + API_VERSION + "/stocks/%s/%s";
    private static boolean init = false;
    private static String requestUrl;
    private static String requestProductUrl;
    private static String requestStockUrl;

    private static ResourceCreated<Long> storeId;
    private static ResourceCreated<Long> productId;
    private static final Long INVALID_ID = 99999999L;

    @Autowired
    private ObjectMapper mapper;

    @Value("classpath:testJsons/stock/create_store.json")
    private File createStore;

    @Value("classpath:testJsons/stock/create_product.json")
    private File createProduct;

    @Value("classpath:testJsons/stock/create_stock.json")
    private File createStock;

    @Value("classpath:testJsons/stock/create_stock_fail.json")
    private File createStockFail;

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

        requestStockUrl = String.format(REQUEST_STOCK, getBasePath(), storeId.getId(), productId.getId());
    }

    @Test
    @FlywayTest
    public void createGetStockSuccess() throws IOException {
        ResponseEntity<String> responseStock = getStringResponse(requestStockUrl, FileUtils.readFileToString(createStock), HttpMethod.POST);
        assertNotNull(responseStock);
        assertEquals("Stock added, status ok response", HttpStatus.NO_CONTENT, responseStock.getStatusCode());

        ResponseEntity<String> responseS = getStringResponse(requestStockUrl, null, HttpMethod.GET);
        assertEquals("Stock get OK", HttpStatus.OK, responseS.getStatusCode());
        StockDto stock = mapper.readValue(responseS.getBody(), StockDto.class);
        assertEquals("Stock count ok", stock.getCount(), Long.valueOf(25));
    }

    @Test
    public void createStockFailCountNull () throws IOException {
        ResponseEntity<String> responseStock = getStringResponse(requestStockUrl, FileUtils.readFileToString(createStockFail), HttpMethod.POST);
        assertEquals("Stock added fail", HttpStatus.PRECONDITION_FAILED, responseStock.getStatusCode());
    }

    @Test
    public void getStockFailInvalidStore() {
        ResponseEntity<String> responseS = getStringResponse(String.format(REQUEST_STOCK, getBasePath(), INVALID_ID, productId.getId()), null, HttpMethod.GET);
        assertEquals("Stock get not found", HttpStatus.NOT_FOUND, responseS.getStatusCode());
    }

    @Test
    public void getStockFailInvalidParams() {
        ResponseEntity<String> responseS = getStringResponse(String.format(REQUEST_STOCK, getBasePath(), INVALID_ID, INVALID_ID), null, HttpMethod.GET);
        assertEquals("Stock get not found", HttpStatus.NOT_FOUND, responseS.getStatusCode());
    }
}

