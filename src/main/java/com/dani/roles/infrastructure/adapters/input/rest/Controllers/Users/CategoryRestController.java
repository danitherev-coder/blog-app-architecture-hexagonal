package com.dani.roles.infrastructure.adapters.input.rest.Controllers.Users;


import com.dani.roles.application.ports.input.CategoryServicePort;
import com.dani.roles.infrastructure.adapters.input.rest.mapper.CategoryRestMapper;
import com.dani.roles.infrastructure.adapters.input.rest.model.response.CategoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryRestController {

    private final CategoryServicePort servicePort;
    private final CategoryRestMapper restMapper;

    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryResponse> findAll(){
        return restMapper.toCategoryResponseList(servicePort.findAll());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryResponse findById(@PathVariable Long id){
        return restMapper.toCategoryCreateRequest(servicePort.findById(id));
    }

}
