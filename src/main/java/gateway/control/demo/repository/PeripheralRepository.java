package gateway.control.demo.repository;

import gateway.control.demo.model.Peripheral;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.math.BigInteger;
import java.util.List;

public interface PeripheralRepository extends PagingAndSortingRepository<Peripheral, Integer> {
    Peripheral findFirstById(Integer id);
    Peripheral findFirstByUid(BigInteger uid);
    Peripheral findFirstByUidAndIdNot(BigInteger uid, Integer id);
    Integer countByGatewayId(Integer gatewayId);
    Integer countByGatewayIdAndIdNot(Integer gatewayId, Integer id);
    List<Peripheral> findAllByGatewayIdOrderByCreatedDateDesc(Integer gatewayId);
}
