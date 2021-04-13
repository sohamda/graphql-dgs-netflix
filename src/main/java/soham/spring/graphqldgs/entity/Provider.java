package soham.spring.graphqldgs.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name= "provider")
public class Provider {

    @Id
    private Integer id;
    private String name;
    private String description;
}
