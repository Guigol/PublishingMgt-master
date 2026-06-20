package com.publ.PublishingMgt_master.controllers;

import com.publ.PublishingMgt_master.entities.MonthlySale;
import com.publ.PublishingMgt_master.repositories.MonthlySaleRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MonthlySaleTestController {

    private final MonthlySaleRepository repo;

    public MonthlySaleTestController(MonthlySaleRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/test")
    public List<MonthlySale> test() {
        return repo.findAll();
    }
}