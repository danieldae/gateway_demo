package gateway.control.demo.model;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

@Entity
public class Gateway {
    private Integer id;
    private String serialNumber;
    private String humanReadableName;
    private String ipV4;

    private Collection<Peripheral> peripheralsById;

    @Id
    @Column(name = "ID", nullable = false, unique = true)
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="GATEWAY_SEQ")
    @SequenceGenerator(name="GATEWAY_SEQ",sequenceName="GATEWAY_SEQ",allocationSize=1)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Basic
    @Column(name = "SERIAL_NUMBER")
    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    @Basic
    @Column(name = "HUMAN_READABLE_NAME")
    public String getHumanReadableName() {
        return humanReadableName;
    }

    public void setHumanReadableName(String humanRedableName) {
        this.humanReadableName = humanRedableName;
    }

    @Basic
    @Column(name = "IP_V4")
    public String getIpV4() {
        return ipV4;
    }

    public void setIpV4(String ipV4) {
        this.ipV4 = ipV4;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Gateway gateway = (Gateway) o;
        return Objects.equals(id, gateway.getId()) &&
                Objects.equals(serialNumber, gateway.getSerialNumber()) &&
                Objects.equals(humanReadableName, gateway.getHumanReadableName()) &&
                Objects.equals(ipV4, gateway.getIpV4());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, serialNumber, humanReadableName, ipV4);
    }

    @OneToMany(mappedBy = "gatewayByGatewayId")
    public Collection<Peripheral> getPeripheralsById() {
        return peripheralsById;
    }

    public void setPeripheralsById(Collection<Peripheral> peripheralsById) {
        this.peripheralsById = peripheralsById;
    }
}
