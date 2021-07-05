package gateway.control.demo.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import gateway.control.demo.constants.Message;
import gateway.control.demo.controller.GatewayController;
import gateway.control.demo.controller.PeripheralController;
import gateway.control.demo.model.Gateway;
import gateway.control.demo.model.Peripheral;
import gateway.control.demo.repository.GatewayRepository;
import gateway.control.demo.repository.PeripheralRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@WebMvcTest(PeripheralController.class)
public class PeripheralControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GatewayRepository gatewayRepository;

    @MockBean
    private PeripheralRepository peripheralRepository;

    @Test
    public void createWithValidDataThenReturnOkWithSameData() throws Exception {

        Peripheral peripheral = new Peripheral();
        peripheral.setGatewayId(1);
        peripheral.setUid(BigInteger.valueOf(1000));
        peripheral.setVendor("Optical");
        Gateway gateway = new Gateway();
        gateway.setId(1);
        Mockito.when(gatewayRepository.findFirstById(1)).thenReturn(gateway);
        Mockito.when(peripheralRepository.save(peripheral)).thenReturn(peripheral);
        Mockito.when(peripheralRepository.countByGatewayId(1)).thenReturn(0);
        Mockito.when(peripheralRepository.findFirstByUid(peripheral.getUid())).thenReturn(null);

        this.mockMvc.perform(post("/peripheral/create")
                .content(asJsonString(peripheral))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.hasError").value(false));
    }

    @Test
    public void createWithInvalidGatewayIdThenReturnBadRequest() throws Exception {

        Peripheral peripheral = new Peripheral();
        peripheral.setUid(BigInteger.valueOf(1000));
        peripheral.setVendor("Optical");

        this.mockMvc.perform(post("/peripheral/create")
                .content(asJsonString(peripheral))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.hasError").value(true));
    }
    @Test
    public void createWithInvalidUidThenReturnBadRequest() throws Exception {

        Peripheral peripheral = new Peripheral();
        peripheral.setGatewayId(1);
        peripheral.setVendor("Optical");

        this.mockMvc.perform(post("/peripheral/create")
                .content(asJsonString(peripheral))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.hasError").value(true));
    }

    @Test
    public void createWithInvalidVendorThenReturnBadRequest() throws Exception {

        Peripheral peripheral = new Peripheral();
        peripheral.setGatewayId(1);
        peripheral.setUid(BigInteger.valueOf(1000));

        this.mockMvc.perform(post("/peripheral/create")
                .content(asJsonString(peripheral))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.hasError").value(true));
    }

    @Test
    public void createWhenGatewayIdNotExistThenReturnBadRequest() throws Exception {

        Peripheral peripheral = new Peripheral();
        peripheral.setGatewayId(1);
        peripheral.setUid(BigInteger.valueOf(1000));
        peripheral.setVendor("Optical");

        Mockito.when(gatewayRepository.findFirstById(1)).thenReturn(null);
        Mockito.when(peripheralRepository.save(peripheral)).thenReturn(peripheral);
        Mockito.when(peripheralRepository.countByGatewayId(1)).thenReturn(0);
        Mockito.when(peripheralRepository.findFirstByUid(peripheral.getUid())).thenReturn(null);

        this.mockMvc.perform(post("/peripheral/create")
                .content(asJsonString(peripheral))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.hasError").value(true))
                .andExpect(jsonPath("$.error").value(Message.GATEWAY_WAS_NOT_FOUND));
    }


    @Test
    public void createWhenGatewayHasAlreadyTenPeripheralsThenReturnBadRequest() throws Exception {

        Peripheral peripheral = new Peripheral();
        peripheral.setGatewayId(1);
        peripheral.setUid(BigInteger.valueOf(1000));
        peripheral.setVendor("Optical");

        Gateway gateway = new Gateway();
        gateway.setId(1);

        Mockito.when(gatewayRepository.findFirstById(1)).thenReturn(gateway);
        Mockito.when(peripheralRepository.save(peripheral)).thenReturn(peripheral);
        Mockito.when(peripheralRepository.countByGatewayId(1)).thenReturn(10);
        Mockito.when(peripheralRepository.findFirstByUid(peripheral.getUid())).thenReturn(null);

        this.mockMvc.perform(post("/peripheral/create")
                .content(asJsonString(peripheral))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.hasError").value(true))
                .andExpect(jsonPath("$.error").value(Message.THIS_GATEWAY_ALREADY_HAS_TEN_PERIPHERALS));
    }

    @Test
    public void createWhenUidIsInUseThenReturnBadRequest() throws Exception {

        Peripheral peripheral = new Peripheral();
        peripheral.setGatewayId(1);
        peripheral.setUid(BigInteger.valueOf(1000));
        peripheral.setVendor("Optical");

        Gateway gateway = new Gateway();
        gateway.setId(1);

        Mockito.when(gatewayRepository.findFirstById(1)).thenReturn(gateway);
        Mockito.when(peripheralRepository.save(peripheral)).thenReturn(peripheral);
        Mockito.when(peripheralRepository.countByGatewayId(1)).thenReturn(0);
        Mockito.when(peripheralRepository.findFirstByUid(peripheral.getUid())).thenReturn(peripheral);

        this.mockMvc.perform(post("/peripheral/create")
                .content(asJsonString(peripheral))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.hasError").value(true))
                .andExpect(jsonPath("$.error").value(Message.UID_IS_IN_USE));
    }
    @Test
    public void deleteWhenOk() throws Exception {
        Peripheral peripheral = new Peripheral();
        peripheral.setGatewayId(1);
        peripheral.setUid(BigInteger.valueOf(1000));
        peripheral.setVendor("Optical");

        this.mockMvc.perform(delete("/peripheral/delete/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void getById() throws Exception {
        Peripheral peripheral = new Peripheral();
        peripheral.setId(1);
        peripheral.setGatewayId(1);
        peripheral.setUid(BigInteger.valueOf(1000));
        peripheral.setVendor("Optical");

        Mockito.when(peripheralRepository.findFirstById(1)).thenReturn(peripheral);

        mockMvc.perform(get("/peripheral/getById/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasError").value(false))
                .andExpect(jsonPath("$.responseBody").exists())
                .andExpect(jsonPath("$.responseBody.id").value(1));
    }

    @Test
    public void findAllWithElement() throws Exception {
        Peripheral peripheral = new Peripheral();
        peripheral.setId(1);
        peripheral.setGatewayId(1);
        peripheral.setUid(BigInteger.valueOf(1000));
        peripheral.setVendor("Optical");

        List<Peripheral> peripherals = Arrays.asList(peripheral);

        Mockito.when(peripheralRepository.findAllByGatewayIdOrderByCreatedDateDesc(1)).thenReturn(peripherals);

        mockMvc.perform(get("/peripheral/byGatewayId/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasError").value(false))
                .andExpect(jsonPath("$.responseBody").exists())
                .andExpect(jsonPath("$.responseBody").isArray());
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
