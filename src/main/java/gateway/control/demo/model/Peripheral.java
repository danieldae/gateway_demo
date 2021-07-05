package gateway.control.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.math.BigInteger;
import java.sql.Date;
import java.util.Objects;

@Entity
public class Peripheral {
    private Integer id;
    private BigInteger uid;
    private Date createdDate;
    private Boolean status;
    private Integer gatewayId;
    private String vendor;
    private Gateway gatewayByGatewayId;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="PERIPHERAL_SEQ")
    @SequenceGenerator(name="PERIPHERAL_SEQ", sequenceName="PERIPHERAL_SEQ",allocationSize=1)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Basic
    @Column(name = "UID")
    public BigInteger getUid() {
        return uid;
    }

    public void setUid(BigInteger uid) {
        this.uid = uid;
    }

    @Basic
    @Column(name = "CREATED_DATE")
    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Basic
    @Column(name = "STATUS")
    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    @Basic
    @Column(name = "GATEWAY_ID")
    public Integer getGatewayId() {
        return gatewayId;
    }

    public void setGatewayId(Integer gatewayId) {
        this.gatewayId = gatewayId;
    }

    @Basic
    @Column(name = "VENDOR")
    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Peripheral that = (Peripheral) o;
        return Objects.equals(id, that.getId()) &&
                Objects.equals(uid, that.getUid()) &&
                Objects.equals(createdDate, that.getCreatedDate()) &&
                Objects.equals(status, that.getStatus()) &&
                Objects.equals(gatewayId, that.getGatewayId()) &&
                Objects.equals(vendor, that.getVendor());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, uid, createdDate, status, gatewayId, vendor);
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gateway_id", referencedColumnName = "id", insertable = false, updatable = false)
    @JsonIgnore
    public Gateway getGatewayByGatewayId() {
        return gatewayByGatewayId;
    }

    public void setGatewayByGatewayId(Gateway gatewayByGatewayId) {
        this.gatewayByGatewayId = gatewayByGatewayId;
    }

    @PrePersist
    public void prePersist() {
        if(status == null)
            status = false;
    }
}
