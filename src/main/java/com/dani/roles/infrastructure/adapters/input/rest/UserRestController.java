package com.dani.roles.infrastructure.adapters.input.rest;

import com.dani.roles.application.ports.input.UserServicePort;
import com.dani.roles.infrastructure.adapters.input.rest.mapper.UserRestMapper;
import com.dani.roles.infrastructure.adapters.input.rest.model.request.UserCreateRequest;
import com.dani.roles.infrastructure.adapters.input.rest.model.response.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserRestController {

    private final UserServicePort servicePort;
    private final UserRestMapper restMapper;


    @GetMapping("/")
    public List<UserResponse> findAll(){
        return restMapper.toUserResponseList(servicePort.findAll());
    }

    @GetMapping("/{id}")
    public UserResponse findById(@PathVariable Long id){
        return restMapper.toUserCreateRequest(servicePort.findById(id));
    }


    @PostMapping(value = "/")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse save(@RequestBody @Valid UserCreateRequest request) {
        return restMapper.toUserCreateRequest(servicePort.save(restMapper.toUser(request)));
    }

    @PutMapping("/{id}")
    public UserResponse update(@PathVariable Long id, @RequestBody @Valid UserCreateRequest request){
        return restMapper.toUserCreateRequest(servicePort.update(id, restMapper.toUser(request)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id){
        servicePort.delete(id);
    }

}
