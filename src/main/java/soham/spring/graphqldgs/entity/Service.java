package soham.spring.graphqldgs.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name="service")
public class Service {

    @Id
    private Integer id;
    private String name;
    private String description;

    @Column(name = "provider_id")
    private Integer providerId;

}
