package com.tenx.ms.retail.product.rest;

import com.tenx.ms.commons.rest.RestConstants;
import com.tenx.ms.commons.rest.dto.ResourceCreated;
import com.tenx.ms.retail.product.rest.dto.ProductDto;
import com.tenx.ms.retail.product.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
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

@Api(value = "product", description = "Product API")
@RestController("productControllerV1")
@RequestMapping(RestConstants.VERSION_ONE + "/products/")
public class ProductController {

    @Autowired
    private ProductService productSrvc;

    @ApiOperation(value = "Creates a new product with @name", authorizations = { @Authorization("ROLE_ADMIN") })
    @ApiResponses( value = {
        @ApiResponse(code = 200, message = "Success"),
        @ApiResponse(code = 412, message = "Error: Invalid parameter."),
        @ApiResponse(code = 500, message = "Error: Internal server error.")
    })
    @RequestMapping(value = "{storeId:\\d+}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResourceCreated<Long> createProduct(@ApiParam(name = "storeId", value = "Store Id", required = true) @PathVariable Long storeId,
        @Validated @RequestBody ProductDto product){
        product.setStoreId(storeId);
        Long productId = productSrvc.createProduct(product);
        return new ResourceCreated<>(productId);
    }

    @ApiOperation(value = "Returns all products.")
    @ApiResponses( value = {
        @ApiResponse(code = 200, message = "Success"),
        @ApiResponse(code = 500, message = "Error: Internal server error.")
    })
    @RequestMapping(value = "{storeId:\\d+}", method = RequestMethod.GET)
    public List<ProductDto> getProducts(@ApiParam(name = "storeId", value = "Store Id", required = true) @PathVariable Long storeId) {
        return productSrvc.findAllProductsInStore(storeId);
    }

    @ApiOperation(value = "Finds a product by id")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success: Product retrieved."),
        @ApiResponse(code = 400, message = "Resource was not found."),
        @ApiResponse(code = 500, message = "Internal server error.")
    })
    @RequestMapping(value = {"{storeId:\\d+}/{productId:\\d+}"}, method = RequestMethod.GET)
    public ProductDto getProductById(@PathVariable Long storeId,
        @PathVariable Long productId) {
        return productSrvc.findProductInStore(storeId, productId);
    }

    @ApiOperation(value = "Deletes the given product", authorizations = { @Authorization("ROLE_ADMIN") })
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success: Product retrieved."),
        @ApiResponse(code = 400, message = "Resource was not found."),
        @ApiResponse(code = 500, message = "Error: Internal server error.")
    })
    @RequestMapping(value = {"{productId:\\d+}"}, method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteProduct(@ApiParam(name = "productId", value = "Id of the product", required = true) @PathVariable Long productId){
        productSrvc.deleteProduct(productId);
    }
}
