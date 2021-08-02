package one.digitalinnovation.mynaturastock.controller;

import one.digitalinnovation.mynaturastock.builder.ProductDTOBuilder;
import one.digitalinnovation.mynaturastock.dto.ProductDTO;
import one.digitalinnovation.mynaturastock.dto.QuantityDTO;
import one.digitalinnovation.mynaturastock.exception.ProductNotFoundException;
import one.digitalinnovation.mynaturastock.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.Collections;

import static one.digitalinnovation.mynaturastock.utils.JsonConvertionUtils.asJsonString;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ProductControllerTest {

    private static final String PRODUCT_API_URL_PATH = "/api/v1/products";
    private static final long VALID_PRODUCT_ID = 1L;
    private static final long INVALID_PRODUCT_ID = 2L;
    private static final String PRODUCT_API_SUBPATH_INCREMENT_URL = "/increment";
    private static final String PRODUCT_API_SUBPATH_DECREMENT_URL = "/decrement";

    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setViewResolvers((s, locale) -> new MappingJackson2JsonView())
                .build();
    }

    @Test
    void whenPOSTIsCalledThenAProductIsCreated() throws Exception {
        // given
        ProductDTO productDTO = ProductDTOBuilder.builder().build().toProductDTO();

        // when
        when(productService.createProduct(productDTO)).thenReturn(productDTO);

        // then
        mockMvc.perform(post(PRODUCT_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(productDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(productDTO.getName())))
                .andExpect(jsonPath("$.type", is(productDTO.getType().toString())));
    }

    @Test
    void whenPOSTIsCalledWithoutRequiredFieldThenAnErrorIsReturned() throws Exception {
        // given
        ProductDTO productDTO = ProductDTOBuilder.builder().build().toProductDTO();
        productDTO.setName(null);

        // then
        mockMvc.perform(post(PRODUCT_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(productDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenGETIsCalledWithValidNameThenOkStatusIsReturned() throws Exception {
        // given
        ProductDTO productDTO = ProductDTOBuilder.builder().build().toProductDTO();

        //when
        when(productService.findByName(productDTO.getName())).thenReturn(productDTO);

        // then
        mockMvc.perform(MockMvcRequestBuilders.get(PRODUCT_API_URL_PATH + "/" + productDTO.getName())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(productDTO.getName())))
                .andExpect(jsonPath("$.type", is(productDTO.getType().toString())));
    }

    @Test
    void whenGETIsCalledWithoutRegisteredNameThenNotFoundStatusIsReturned() throws Exception {
        // given
        ProductDTO productDTO = ProductDTOBuilder.builder().build().toProductDTO();

        //when
        when(productService.findByName(productDTO.getName())).thenThrow(ProductNotFoundException.class);

        // then
        mockMvc.perform(MockMvcRequestBuilders.get(PRODUCT_API_URL_PATH + "/" + productDTO.getName())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenGETListWithProductsIsCalledThenOkStatusIsReturned() throws Exception {
        // given
        ProductDTO productDTO = ProductDTOBuilder.builder().build().toProductDTO();

        //when
        when(productService.listAll()).thenReturn(Collections.singletonList(productDTO));

        // then
        mockMvc.perform(MockMvcRequestBuilders.get(PRODUCT_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is(productDTO.getName())))
                .andExpect(jsonPath("$[0].type", is(productDTO.getType().toString())));
    }

    @Test
    void whenGETListWithoutProductsIsCalledThenOkStatusIsReturned() throws Exception {
        // given
        ProductDTO productDTO = ProductDTOBuilder.builder().build().toProductDTO();

        //when
        when(productService.listAll()).thenReturn(Collections.singletonList(productDTO));

        // then
        mockMvc.perform(MockMvcRequestBuilders.get(PRODUCT_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void whenDELETEIsCalledWithValidIdThenNoContentStatusIsReturned() throws Exception {
        // given
        ProductDTO productDTO = ProductDTOBuilder.builder().build().toProductDTO();

        //when
        doNothing().when(productService).deleteById(productDTO.getId());

        // then
        mockMvc.perform(MockMvcRequestBuilders.delete(PRODUCT_API_URL_PATH + "/" + productDTO.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void whenDELETEIsCalledWithInvalidIdThenNotFoundStatusIsReturned() throws Exception {
        //when
        doThrow(ProductNotFoundException.class).when(productService).deleteById(INVALID_PRODUCT_ID);

        // then
        mockMvc.perform(MockMvcRequestBuilders.delete(PRODUCT_API_URL_PATH + "/" + INVALID_PRODUCT_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenPATCHIsCalledToDecrementDiscountThenOKstatusIsReturned() throws Exception {
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(10)
                .build();

        ProductDTO productDTO = ProductDTOBuilder.builder().build().toProductDTO();
        productDTO.setQuantity(productDTO.getQuantity() - quantityDTO.getQuantity());

        when(productService.decrement(VALID_PRODUCT_ID, quantityDTO.getQuantity())).thenReturn(productDTO);

        mockMvc.perform(MockMvcRequestBuilders.patch(PRODUCT_API_URL_PATH + "/" + VALID_PRODUCT_ID + PRODUCT_API_SUBPATH_DECREMENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDTO))).andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(productDTO.getName())))
                .andExpect(jsonPath("$.type", is(productDTO.getType().toString())))
                .andExpect(jsonPath("$.quantity", is(productDTO.getQuantity())));
    }
}
