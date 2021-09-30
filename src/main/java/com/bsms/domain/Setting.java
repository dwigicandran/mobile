package com.bsms.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "Setting")
public class Setting {

    @Id
    @Column(name="name")
    private String name;
    @Column(name="value")
    private String value;
    @Column(name="desc")
    private String desc;
}
