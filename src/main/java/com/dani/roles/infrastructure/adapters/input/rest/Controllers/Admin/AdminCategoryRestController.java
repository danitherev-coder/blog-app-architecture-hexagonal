package com.dani.roles.infrastructure.adapters.input.rest.Controllers.Admin;


import com.dani.roles.application.ports.input.CategoryServicePort;
import com.dani.roles.infrastructure.adapters.input.rest.mapper.CategoryRestMapper;
import com.dani.roles.infrastructure.adapters.input.rest.model.request.CategoryCreateRequest;
import com.dani.roles.infrastructure.adapters.input.rest.model.response.CategoryResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryRestController {

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

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse save(@RequestBody @Valid CategoryCreateRequest request){
        return restMapper.toCategoryCreateRequest(servicePort.save(restMapper.toCategory(request)));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryResponse update(@PathVariable Long id, @RequestBody @Valid CategoryCreateRequest request){
        return restMapper.toCategoryCreateRequest(servicePort.update(id, restMapper.toCategory(request)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id){
        servicePort.delete(id);
    }
}
