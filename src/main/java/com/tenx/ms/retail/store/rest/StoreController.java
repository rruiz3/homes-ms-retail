package com.tenx.ms.retail.store.rest;

import com.google.common.base.Preconditions;
import com.tenx.ms.commons.rest.RestConstants;
import com.tenx.ms.commons.rest.dto.ResourceCreated;
import com.tenx.ms.retail.store.rest.dto.StoreDto;
import com.tenx.ms.retail.store.service.StoreService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(value = "store", description = "Store API")
@RestController("storeControllerV1")
@RequestMapping(RestConstants.VERSION_ONE + "/stores/")
public class StoreController {

    @Autowired
    private StoreService storeSrvc;

    @ApiOperation(value = "Creates a new store with @name", authorizations = { @Authorization("ROLE_ADMIN") })
    @ApiResponses( value = {
        @ApiResponse(code = 200, message = "Success"),
        @ApiResponse(code = 412, message = "Error: Invalid parameter."),
        @ApiResponse(code = 500, message = "Error: Internal server error.")
    })
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResourceCreated<Long> createStore(@ApiParam(name = "store", value = "Store Info", required = true) @Validated @RequestBody StoreDto store){
        Preconditions.checkArgument(StringUtils.isNotBlank(store.getName()));
        Preconditions.checkArgument(StringUtils.isNotEmpty(store.getName()));
        Long storeId = storeSrvc.createStore(store);
        return new ResourceCreated<>(storeId);
    }

    @ApiOperation(value = "Returns all stores.")
    @ApiResponses( value = {
        @ApiResponse(code = 200, message = "Success"),
        @ApiResponse(code = 500, message = "Error: Internal server error.")
    })
    @RequestMapping(method = RequestMethod.GET)
    public List<StoreDto> getStores() {
        return storeSrvc.findAllStores();
    }

    @ApiOperation(value = "Finds a store by id")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success: Store retrieved."),
        @ApiResponse(code = 400, message = "Resource was not found."),
        @ApiResponse(code = 500, message = "Internal server error.")
    })
    @RequestMapping(value = {"{storeId:\\d+}"}, method = RequestMethod.GET)
    public StoreDto getStoreById(@ApiParam(name = "storeId", value = "Id of the store", required = true) @PathVariable Long storeId) {
        return storeSrvc.findStoreById(storeId);
    }

    @ApiOperation(value = "Deletes the given store", authorizations = { @Authorization("ROLE_ADMIN") })
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success: Store retrieved."),
        @ApiResponse(code = 400, message = "Resource was not found."),
        @ApiResponse(code = 500, message = "Error: Internal server error.")
    })
    @RequestMapping(value = {"{storeId:\\d+}"}, method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteStore(@ApiParam(name = "storeId", value = "Id of the store", required = true) @PathVariable Long storeId) {
        storeSrvc.deleteStore(storeId);
    }
}
