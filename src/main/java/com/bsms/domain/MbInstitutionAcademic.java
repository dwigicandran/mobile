package com.bsms.domain;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "MB_Institutionacademic")
@ToString
@Data
public class MbInstitutionAcademic {

    @Id
    @Column(name = "id")
    private int id;
    @Column(name = "name")
    private String name;
    @Column(name = "prefix")
    private String prefix;
    @Column(name = "type")
    private int type;


}
