package com.crewmeister.cmcodingchallenge.Repository;

import com.crewmeister.cmcodingchallenge.entity.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate,Long> {
}
