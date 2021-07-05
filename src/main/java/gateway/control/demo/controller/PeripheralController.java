package gateway.control.demo.controller;

import gateway.control.demo.constants.Message;
import gateway.control.demo.model.Gateway;
import gateway.control.demo.model.Peripheral;
import gateway.control.demo.repository.GatewayRepository;
import gateway.control.demo.repository.PeripheralRepository;
import gateway.control.demo.util.GeneralUtil;
import gateway.control.demo.wrapper.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(value = "/peripheral")
public class PeripheralController {

    private final PeripheralRepository peripheralRepository;
    private final GatewayRepository gatewayRepository;

    public PeripheralController(PeripheralRepository peripheralRepository, GatewayRepository gatewayRepository) {
        this.peripheralRepository = peripheralRepository;
        this.gatewayRepository = gatewayRepository;
    }


    @GetMapping(value = "/byGatewayId/{gatewayId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> byGatewayId(@PathVariable(value = "gatewayId") Integer gatewayId) {
        try {
            List<Peripheral> list = peripheralRepository.findAllByGatewayIdOrderByCreatedDateDesc(gatewayId);
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
            Peripheral peripheral = peripheralRepository.findFirstById(id);
            if(peripheral != null)
                return ResponseEntity.ok().body(new Response(HttpStatus.OK, peripheral, Message.SUCCESS));
            else return ResponseEntity.ok().body(new Response(HttpStatus.NOT_FOUND, null, Message.SUCCESS));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new Response(HttpStatus.INTERNAL_SERVER_ERROR, Message.AN_ERROR_OCCURRED));
        }
    }

    @DeleteMapping(value = "/delete/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> delete(@PathVariable(value = "id") Integer id) {
        try {

            Peripheral peripheral = peripheralRepository.findFirstById(id);
            if(peripheral != null)
                peripheralRepository.delete(peripheral);

            return ResponseEntity.ok().body(new Response(HttpStatus.OK, peripheral, Message.SUCCESS));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new Response(HttpStatus.INTERNAL_SERVER_ERROR, Message.AN_ERROR_OCCURRED));
        }
    }

    @PostMapping(value = "/create",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> create(@RequestBody Peripheral peripheral) {
        try {
            //pre-conditions
            if(peripheral == null || peripheral.getGatewayId() == null || peripheral.getUid() == null
                    || GeneralUtil.isEmptyString(peripheral.getVendor()) || peripheral.getId() != null)
                return ResponseEntity.badRequest()
                        .body(new Response(HttpStatus.BAD_REQUEST, Message.ERROR_WITH_PARAMETERS));

            //check if gateway with that id exist
            Gateway gatewayExist = gatewayRepository.findFirstById(peripheral.getGatewayId());
            if(gatewayExist == null)
                return ResponseEntity.badRequest()
                        .body(new Response(HttpStatus.BAD_REQUEST, Message.GATEWAY_WAS_NOT_FOUND));

            //Check if gateway has already ten peripherals
            Integer count = peripheralRepository.countByGatewayId(peripheral.getGatewayId());
            if(count != null && count > 9)
                return ResponseEntity.badRequest()
                        .body(new Response(HttpStatus.BAD_REQUEST, Message.THIS_GATEWAY_ALREADY_HAS_TEN_PERIPHERALS));

            //Check if uid is unique
            Peripheral peripheralUid = peripheralRepository.findFirstByUid(peripheral.getUid());
            if(peripheralUid != null)
                return ResponseEntity.badRequest()
                        .body(new Response(HttpStatus.BAD_REQUEST, Message.UID_IS_IN_USE));

            LocalDate today = LocalDate.now(Clock.systemDefaultZone());
            peripheral.setCreatedDate(Date.valueOf(today));
            peripheral.setStatus(true);

            peripheral = peripheralRepository.save(peripheral);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new Response(HttpStatus.CREATED, peripheral, Message.SUCCESS));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new Response(HttpStatus.INTERNAL_SERVER_ERROR, Message.AN_ERROR_OCCURRED));
        }
    }

    @PutMapping(value = "/update/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> update(@RequestBody Peripheral peripheral,
                                         @PathVariable(value = "id") Integer id) {
        try {
            //pre-conditions
            if(peripheral == null || id == null)
                return ResponseEntity.badRequest()
                        .body(new Response(HttpStatus.BAD_REQUEST, Message.ERROR_WITH_PARAMETERS));

            //check if exist
            Peripheral exist =  peripheralRepository.findFirstById(id);
            if(exist == null)
                return ResponseEntity.badRequest()
                        .body(new Response(HttpStatus.BAD_REQUEST, Message.PERIPHERAL_WAS_NOT_FOUND));


            if(peripheral.getGatewayId() != null) {
                //Check if gateway id has change
                if(exist.getGatewayId() != peripheral.getGatewayId()) {
                    //check if gateway id exist
                    Gateway gatewayExist = gatewayRepository.findFirstById(peripheral.getGatewayId());
                    if (gatewayExist == null)
                        return ResponseEntity.badRequest()
                                .body(new Response(HttpStatus.BAD_REQUEST, Message.GATEWAY_WAS_NOT_FOUND));
                    //Check if gateway has already ten peripherals
                    Integer count = peripheralRepository.countByGatewayIdAndIdNot(peripheral.getGatewayId(), id);
                    if (count != null && count > 9)
                        return ResponseEntity.badRequest()
                                .body(new Response(HttpStatus.BAD_REQUEST, Message.THIS_GATEWAY_ALREADY_HAS_TEN_PERIPHERALS));

                }
            }

            //check if uid is in use
            if(peripheral.getUid() != null) {
                Peripheral peripheralUid = peripheralRepository.findFirstByUidAndIdNot(peripheral.getUid(), id);
                if(peripheralUid != null)
                    return ResponseEntity.badRequest()
                            .body(new Response(HttpStatus.BAD_REQUEST, Message.UID_IS_IN_USE));
            }

            //check if vendor is empty
            if(peripheral.getVendor() != null && peripheral.getVendor().isEmpty())
                return ResponseEntity.badRequest()
                        .body(new Response(HttpStatus.BAD_REQUEST, Message.PERIPHERAL_VENDOR_IS_REQUIRED));

            peripheral.setCreatedDate(null);
            peripheral.setId(id);
            GeneralUtil.copyNonNullProperties(peripheral, exist);
            exist = peripheralRepository.save(peripheral);

            return ResponseEntity.ok().body(new Response(HttpStatus.OK, exist, Message.SUCCESS));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new Response(HttpStatus.INTERNAL_SERVER_ERROR, Message.AN_ERROR_OCCURRED));
        }
    }

}
