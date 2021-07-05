package gateway.control.demo;

import gateway.control.demo.controller.GatewayController;
import gateway.control.demo.controller.PeripheralController;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class DemoApplicationTests {

    @Autowired
    GatewayController gatewayController;
    @Autowired
    PeripheralController peripheralController;

    @Test
    void contextGatewayControllerLoads() {
        Assertions.assertThat(gatewayController).isNotNull();
    }

    @Test
    void contextPeripheralControllerLoads() {
        Assertions.assertThat(peripheralController).isNotNull();
    }

}
