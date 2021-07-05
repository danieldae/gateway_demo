package gateway.control.demo.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.fasterxml.jackson.databind.ObjectMapper;
import gateway.control.demo.constants.Message;
import gateway.control.demo.controller.GatewayController;
import gateway.control.demo.model.Gateway;
import gateway.control.demo.repository.GatewayRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

@ExtendWith(SpringExtension.class)
@WebMvcTest(GatewayController.class)
public class GatewayControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GatewayRepository gatewayRepository;

    @Test
    public void createWithValidDataThenReturnOkWithSameData() throws Exception {

        Gateway gateway = new Gateway();
        gateway.setHumanReadableName("Gateway1");
        gateway.setIpV4("127.0.0.1");
        gateway.setSerialNumber("123");
        Mockito.when(gatewayRepository.findFirstBySerialNumber(gateway.getSerialNumber())).thenReturn(null);
        Mockito.when(gatewayRepository.save(gateway)).thenReturn(gateway);
        this.mockMvc.perform(post("/gateway/create")
                .content(asJsonString(gateway))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.responseBody").exists());
    }

    @Test
    public void createWithInvalidIpThenReturnBadRequest() throws Exception {

        Gateway gateway = new Gateway();
        gateway.setHumanReadableName("Gateway1");
        gateway.setIpV4("127.0.0.");
        gateway.setSerialNumber("123");
        Mockito.when(gatewayRepository.findFirstBySerialNumber(gateway.getSerialNumber())).thenReturn(null);
        Mockito.when(gatewayRepository.save(gateway)).thenReturn(gateway);
        this.mockMvc.perform(post("/gateway/create")
                .content(asJsonString(gateway))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.hasError").value(true))
                .andExpect(jsonPath("$.error").value(Message.IP_V4_IS_NOT_VALID));
    }

    @Test
    public void createWithInvalidNameThenReturnBadRequest() throws Exception {

        Gateway gateway = new Gateway();
        gateway.setIpV4("127.0.0.1");
        gateway.setSerialNumber("123");
        Mockito.when(gatewayRepository.findFirstBySerialNumber(gateway.getSerialNumber())).thenReturn(null);
        Mockito.when(gatewayRepository.save(gateway)).thenReturn(gateway);
        this.mockMvc.perform(post("/gateway/create")
                .content(asJsonString(gateway))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.hasError").value(true))
                .andExpect(jsonPath("$.error").value(Message.ERROR_WITH_PARAMETERS));
    }

    @Test
    public void createWithInvalidSerialThenReturnBadRequest() throws Exception {

        Gateway gateway = new Gateway();
        gateway.setHumanReadableName("Gateway1");
        gateway.setIpV4("127.0.0.1");
        gateway.setSerialNumber("");
        Mockito.when(gatewayRepository.save(gateway)).thenReturn(gateway);
        this.mockMvc.perform(post("/gateway/create")
                .content(asJsonString(gateway))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.hasError").value(true))
                .andExpect(jsonPath("$.error").value(Message.ERROR_WITH_PARAMETERS));
    }

    @Test
    public void createWithExistingSerialThenReturnBadRequest() throws Exception {

        Gateway gateway = new Gateway();
        gateway.setHumanReadableName("Gateway1");
        gateway.setIpV4("127.0.0.1");
        gateway.setSerialNumber("123");
        Mockito.when(gatewayRepository.save(gateway)).thenReturn(gateway);
        Mockito.when(gatewayRepository.findFirstBySerialNumber("123")).thenReturn(gateway);
        this.mockMvc.perform(post("/gateway/create")
                .content(asJsonString(gateway))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.hasError").value(true))
                .andExpect(jsonPath("$.error").value(Message.SERIAL_NUMBER_IS_IN_USE));
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void findAllWithElement() throws Exception {
        Gateway gateway = new Gateway();
        gateway.setHumanReadableName("Gateway1");
        gateway.setIpV4("127.0.0.1");
        gateway.setSerialNumber("123");
        List<Gateway> gateways = Arrays.asList(gateway);

        Mockito.when(gatewayRepository.findAllByOrderByHumanReadableName()).thenReturn(gateways);

        mockMvc.perform(get("/gateway/list"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasError").value(false))
                .andExpect(jsonPath("$.responseBody").exists())
                .andExpect(jsonPath("$.responseBody").isArray());
    }

    @Test
    public void getById() throws Exception {
        Gateway gateway = new Gateway();
        gateway.setHumanReadableName("Gateway1");
        gateway.setId(1);
        gateway.setIpV4("127.0.0.1");
        gateway.setSerialNumber("123");

        Mockito.when(gatewayRepository.findFirstById(1)).thenReturn(gateway);

        mockMvc.perform(get("/gateway/getById/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasError").value(false))
                .andExpect(jsonPath("$.responseBody").exists())
                .andExpect(jsonPath("$.responseBody.id").value(1));
    }

    @Test
    public void invalidIdWhenGetByIdThenBadRequest() throws Exception {
        mockMvc.perform(get("/gateway/getById/null"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenNotFoundInGetByIdThenNotFound() throws Exception {

        Mockito.when(gatewayRepository.findFirstById(10)).thenReturn(null);

        mockMvc.perform(get("/gateway/getById/10"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    //Update
    @Test
    public void updateWithValidDataThenReturnOkWithSameData() throws Exception {

        Gateway existing = new Gateway();
        existing.setId(1);
        existing.setHumanReadableName("Gateway2");
        existing.setIpV4("127.0.0.5");
        existing.setSerialNumber("123");
        Gateway gateway = new Gateway();
        gateway.setId(1);
        gateway.setHumanReadableName("Gateway2");
        gateway.setIpV4("127.0.0.2");
        gateway.setSerialNumber("1234");
        Mockito.when(gatewayRepository.findFirstById(1)).thenReturn(existing);
        Mockito.when(gatewayRepository.findFirstBySerialNumberAndIdNot(gateway.getSerialNumber(),gateway.getId())).thenReturn(null);
        Mockito.when(gatewayRepository.save(gateway)).thenReturn(gateway);
        this.mockMvc.perform(put("/gateway/update/1")
                .content(asJsonString(gateway))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasError").value(false))
                .andExpect(jsonPath("$.responseBody").exists())
                .andExpect(jsonPath("$.responseBody.id").value(1))
                .andExpect(jsonPath("$.responseBody.ipV4").value("127.0.0.2"))
                .andExpect(jsonPath("$.responseBody.serialNumber").value("1234"))
                .andExpect(jsonPath("$.responseBody.humanReadableName").value("Gateway2"));
    }

    @Test
    public void updateWithInvalidBodyThenReturnBadRequest() throws Exception {
        this.mockMvc.perform(put("/gateway/update/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateInvalidGatewayNotExistThenReturnNotFound() throws Exception {
        Gateway existing = new Gateway();
        existing.setId(1);
        existing.setHumanReadableName("Gateway2");
        existing.setIpV4("127.0.0.5");
        existing.setSerialNumber("123");
        Gateway gateway = new Gateway();
        gateway.setId(1);
        gateway.setHumanReadableName("Gateway2");
        gateway.setIpV4("127.0.0.5");
        gateway.setSerialNumber("");
        Mockito.when(gatewayRepository.findFirstById(1)).thenReturn(null);
        this.mockMvc.perform(put("/gateway/update/1")
                .content(asJsonString(existing))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.hasError").value(true))
                .andExpect(jsonPath("$.error").value(Message.GATEWAY_WAS_NOT_FOUND));
    }

    @Test
    public void updateWhenSerialNumberIsInUseThenReturnBadRequest() throws Exception {
        Gateway existing = new Gateway();
        existing.setId(1);
        existing.setHumanReadableName("Gateway2");
        existing.setIpV4("127.0.0.5");
        existing.setSerialNumber("123");
        Gateway gateway = new Gateway();
        gateway.setId(1);
        gateway.setHumanReadableName("Gateway2");
        gateway.setIpV4("127.0.0.5");
        gateway.setSerialNumber("1234");
        Mockito.when(gatewayRepository.findFirstById(1)).thenReturn(existing);
        Mockito.when(gatewayRepository.findFirstBySerialNumberAndIdNot("1234", 1)).thenReturn(existing);
        this.mockMvc.perform(put("/gateway/update/1")
                .content(asJsonString(gateway))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.hasError").value(true))
                .andExpect(jsonPath("$.error").value(Message.SERIAL_NUMBER_IS_IN_USE));
    }

    @Test
    public void updateWhenIpV4IsInvalidThenReturnBadRequest() throws Exception {
        Gateway existing = new Gateway();
        existing.setId(1);
        existing.setHumanReadableName("Gateway2");
        existing.setIpV4("127.0.0.7");
        existing.setSerialNumber("123");
        Gateway gateway = new Gateway();
        gateway.setId(1);
        gateway.setHumanReadableName("Gateway2");
        gateway.setIpV4("127.0.0.259");
        gateway.setSerialNumber("1234");
        Mockito.when(gatewayRepository.findFirstById(1)).thenReturn(existing);
        Mockito.when(gatewayRepository.findFirstBySerialNumberAndIdNot("1234", 1)).thenReturn(null);
        this.mockMvc.perform(put("/gateway/update/1")
                .content(asJsonString(gateway))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.hasError").value(true))
                .andExpect(jsonPath("$.error").value(Message.IP_V4_IS_NOT_VALID));
    }

    @Test
    public void updateWhenNamesIsEmptyThenReturnBadRequest() throws Exception {
        Gateway existing = new Gateway();
        existing.setId(1);
        existing.setHumanReadableName("Gateway2");
        existing.setIpV4("127.0.0.5");
        existing.setSerialNumber("123");
        Gateway gateway = new Gateway();
        gateway.setId(1);
        gateway.setHumanReadableName("");
        gateway.setIpV4("127.0.0.1");
        gateway.setSerialNumber("1234");
        Mockito.when(gatewayRepository.findFirstById(1)).thenReturn(existing);
        Mockito.when(gatewayRepository.findFirstBySerialNumberAndIdNot("1234", 1)).thenReturn(null);
        this.mockMvc.perform(put("/gateway/update/1")
                .content(asJsonString(gateway))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.hasError").value(true))
                .andExpect(jsonPath("$.error").value(Message.GATEWAY_NAME_IS_REQUIRED));
    }

}
