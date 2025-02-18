package com.crewmeister.cmcodingchallenge.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import javax.persistence.Table;

@Data
@Entity
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name =  "currency")
public class Currency {

    @Id
    @Column(name = "currency_code")
    private String currencyCode;
    private String currencyCommonName;
}
