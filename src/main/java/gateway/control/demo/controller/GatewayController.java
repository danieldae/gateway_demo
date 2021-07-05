package gateway.control.demo.controller;
import gateway.control.demo.constants.Message;
import gateway.control.demo.model.Gateway;
import gateway.control.demo.repository.GatewayRepository;
import gateway.control.demo.util.GeneralUtil;
import gateway.control.demo.wrapper.Response;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sun.net.util.IPAddressUtil;

import java.util.List;

@RestController
@RequestMapping(value = "/gateway")
public class GatewayController {

    private final GatewayRepository gatewayRepository;

    public GatewayController(GatewayRepository gatewayRepository) {
        this.gatewayRepository = gatewayRepository;
    }

    @GetMapping(value = "/list",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> list() {
        try {
            List<Gateway> list = gatewayRepository.findAllByOrderByHumanReadableName();
            return ResponseEntity.ok().body(new Response(HttpStatus.OK, list, Message.SUCCESS));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new Response(HttpStatus.INTERNAL_SERVER_ERROR, Message.AN_ERROR_OCCURRED));
        }
    }

    @GetMapping(value = "/getById/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getById(@PathVariable(value = "id") Integer id) {
        try {
            if(id == null)
                return ResponseEntity.badRequest()
                        .body(new Response(HttpStatus.BAD_REQUEST, Message.ID_IS_REQUIRED));
            Gateway gateway = gatewayRepository.findFirstById(id);
            if(gateway != null)
                return ResponseEntity.ok().body(new Response(HttpStatus.OK, gateway, Message.SUCCESS));
            else return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response(HttpStatus.NOT_FOUND, null, Message.SUCCESS));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new Response(HttpStatus.INTERNAL_SERVER_ERROR, Message.AN_ERROR_OCCURRED));
        }
    }

    @PostMapping(value = "/create",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> create(@RequestBody Gateway gateway) {
        try {
            //pre-conditions
            if(gateway == null || GeneralUtil.isEmptyString(gateway.getHumanReadableName())
                    || GeneralUtil.isEmptyString(gateway.getSerialNumber()) || gateway.getId() != null)
                return ResponseEntity.badRequest()
                        .body(new Response(HttpStatus.BAD_REQUEST, Message.ERROR_WITH_PARAMETERS));

            //check if serial number is in use for other gateway.
            if(gateway.getSerialNumber() != null) {
                Gateway serialInUse = gatewayRepository.findFirstBySerialNumber(gateway.getSerialNumber());
                if(serialInUse != null)
                    return ResponseEntity.badRequest()
                            .body(new Response(HttpStatus.BAD_REQUEST, Message.SERIAL_NUMBER_IS_IN_USE));
            }

            //check if ipV4 is valid
            if(gateway.getIpV4() != null && !InetAddressValidator.getInstance().isValidInet4Address(gateway.getIpV4()))
                return ResponseEntity.badRequest()
                        .body(new Response(HttpStatus.BAD_REQUEST, Message.IP_V4_IS_NOT_VALID));


            gateway = gatewayRepository.save(gateway);
            return ResponseEntity.status(HttpStatus.CREATED).body(new Response(HttpStatus.CREATED, gateway, Message.SUCCESS));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new Response(HttpStatus.INTERNAL_SERVER_ERROR, Message.AN_ERROR_OCCURRED));
        }
    }

    @PutMapping(value = "/update/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> update(@RequestBody Gateway gateway,
                                         @PathVariable(value = "id") Integer id) {
        try {
            //pre-conditions
            if(id == null || gateway == null)
                return ResponseEntity.badRequest()
                        .body(new Response(HttpStatus.BAD_REQUEST, Message.ERROR_WITH_PARAMETERS));

            Gateway exist = gatewayRepository.findFirstById(id);
            if(exist == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new Response(HttpStatus.NOT_FOUND, Message.GATEWAY_WAS_NOT_FOUND));

            //check if serial number is not empty
            if(gateway.getSerialNumber() != null && gateway.getSerialNumber().isEmpty())
                return ResponseEntity.badRequest()
                        .body(new Response(HttpStatus.BAD_REQUEST, Message.SERIAL_NUMBER_IS_REQUIRED));

            //check if serial number is in use for other gateway.
            if(gateway.getSerialNumber() != null) {
                Gateway serialInUse = gatewayRepository.findFirstBySerialNumberAndIdNot(gateway.getSerialNumber(), id);
                if(serialInUse != null)
                    return ResponseEntity.badRequest()
                            .body(new Response(HttpStatus.BAD_REQUEST, Message.SERIAL_NUMBER_IS_IN_USE));
            }

            //check if ipV4 is valid
            if(gateway.getIpV4() != null && !IPAddressUtil.isIPv4LiteralAddress(gateway.getIpV4()))
                return ResponseEntity.badRequest()
                        .body(new Response(HttpStatus.BAD_REQUEST, Message.IP_V4_IS_NOT_VALID));

            //check if name is empty
            if(gateway.getHumanReadableName() != null && gateway.getHumanReadableName().isEmpty())
                return ResponseEntity.badRequest()
                        .body(new Response(HttpStatus.BAD_REQUEST, Message.GATEWAY_NAME_IS_REQUIRED));

            gateway.setId(id);

            GeneralUtil.copyNonNullProperties(gateway, exist);
            exist = gatewayRepository.save(exist);

            return ResponseEntity.ok().body(new Response(HttpStatus.OK, exist, Message.SUCCESS));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new Response(HttpStatus.INTERNAL_SERVER_ERROR, Message.AN_ERROR_OCCURRED));
        }
    }

}
