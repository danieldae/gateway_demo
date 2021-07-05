package gateway.control.demo.repository;

import gateway.control.demo.model.Gateway;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface GatewayRepository extends PagingAndSortingRepository<Gateway, Integer> {
    Gateway findFirstById(Integer id);
    Gateway findFirstBySerialNumberAndIdNot(String serialNumber, Integer id);
    Gateway findFirstBySerialNumber(String serialNumber);
    List<Gateway> findAllByOrderByHumanReadableName();
}
