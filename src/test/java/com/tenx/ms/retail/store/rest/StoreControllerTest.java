package com.tenx.ms.retail.store.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenx.ms.commons.config.Profiles;
import com.tenx.ms.commons.rest.RestConstants;
import com.tenx.ms.commons.rest.dto.ResourceCreated;
import com.tenx.ms.commons.tests.BaseIntegrationTest;
import com.tenx.ms.retail.store.rest.dto.StoreDto;
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
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(Profiles.TEST_NOAUTH)
@SuppressWarnings("PMD")
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, FlywayTestExecutionListener.class})
public class StoreControllerTest extends BaseIntegrationTest {

    private final String API_VERSION = RestConstants.VERSION_ONE;
    private final String REQUEST_URI = "%s" + API_VERSION + "/stores/";
    private static boolean init = false;
    private static String requestUrl;

    @Autowired
    private ObjectMapper mapper;

    @Value("classpath:testJsons/store/create_store.json")
    private File createStore;

    @Value("classpath:testJsons/store/create_store_fail.json")
    private File createStoreFail;

    @Value("classpath:testJsons/store/create_store_fail_name_null.json")
    private File createStoreFailNameNull;

    @Value("classpath:testJsons/store/create_store_fail_name_empty.json")
    private File createStoreFailNameEmpty;

    @Before
    public void init() {
        if (!init) {
            requestUrl = String.format(REQUEST_URI, getBasePath());
        }
        init = true;
    }

    @Test
    @FlywayTest
    public void createGetStoreSuccess() throws IOException {
        ResponseEntity<String> responseEntity = getStringResponse(requestUrl, FileUtils.readFileToString(createStore), HttpMethod.POST);
        assertNotNull(responseEntity);
        assertEquals("Store created status ok response", HttpStatus.OK, responseEntity.getStatusCode());

        ResourceCreated<Long> storeId = mapper.readValue(responseEntity.getBody(), new TypeReference<ResourceCreated<Long>>() {});
        ResponseEntity<String> storeCreated = getStringResponse(requestUrl + storeId.getId(), null, HttpMethod.GET);
        assertEquals("Get store, ok response", HttpStatus.OK, responseEntity.getStatusCode());

        StoreDto storeGot = mapper.readValue(storeCreated.getBody(), StoreDto.class);
        assertEquals("Store name ok", "My store", storeGot.getName());
        assertEquals("Store id ok", storeId.getId(), storeGot.getStoreId());
    }

    @Test
    public void createStoreFailNameNull() throws IOException {
        ResponseEntity<String> responseEntity = getStringResponse(requestUrl, FileUtils.readFileToString(createStoreFailNameNull), HttpMethod.POST);
        assertEquals("Invalid data", HttpStatus.PRECONDITION_FAILED, responseEntity.getStatusCode());
    }

    @Test
    public void createStoreFailNameEmpty() throws IOException {
        ResponseEntity<String> responseEntity = getStringResponse(requestUrl, FileUtils.readFileToString(createStoreFailNameEmpty), HttpMethod.POST);
        assertEquals("Invalid data", HttpStatus.PRECONDITION_FAILED, responseEntity.getStatusCode());
    }

    @Test
    @FlywayTest
    public void getStores() {
        ResponseEntity<String> responseEntity = getStringResponse(requestUrl, null, HttpMethod.GET);
        assertEquals("Get all stores, ok response", HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void getStoreByIdFail() {
        ResponseEntity<String> responseEntity = getStringResponse(requestUrl + "/999999999", null, HttpMethod.GET);
        assertEquals("Get a store by Id, not found response", HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void deleteStoreNotFound() {
        ResponseEntity<String> responseEntity = getStringResponse(requestUrl + "/0000", null, HttpMethod.DELETE);
        assertEquals("Delete store, not found response", HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    @FlywayTest
    public void deleteStoreSuccess() throws IOException {
        ResponseEntity<String> responseEntity = getStringResponse(requestUrl, FileUtils.readFileToString(createStore), HttpMethod.POST);
        assertEquals("Store created status ok response", HttpStatus.OK, responseEntity.getStatusCode());

        ResourceCreated<Long> storeId = mapper.readValue(responseEntity.getBody(), new TypeReference<ResourceCreated<Long>>() {});

        ResponseEntity<String> response = getStringResponse(requestUrl + "/" + storeId.getId(), null, HttpMethod.DELETE);
        assertEquals("Delete store, success response", HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}

