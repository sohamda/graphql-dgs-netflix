package soham.spring.graphqldgs.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

@Data
@Entity
@Table(name= "provider")
public class Provider {

    @Id
    private Integer id;
    private String name;
    private String description;

    @Transient
    private List<Service> services;
}
