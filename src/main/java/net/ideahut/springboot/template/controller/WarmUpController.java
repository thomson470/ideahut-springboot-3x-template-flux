package net.ideahut.springboot.template.controller;


import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.UUID;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import net.ideahut.springboot.annotation.Public;
import net.ideahut.springboot.init.InitRequest;
import net.ideahut.springboot.object.Result;

@Public
@RestController
@RequestMapping(
    path = "/warmup",
    consumes = APPLICATION_JSON_VALUE
)
class WarmUpController {

    @PostMapping
    protected Result post(@RequestBody @Valid InitRequest initRequest) {
    	return Result.success(initRequest).setInfo("uuid", UUID.randomUUID().toString());
    }
}