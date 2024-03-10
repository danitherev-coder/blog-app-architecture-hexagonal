package com.dani.roles.infrastructure.adapters.input.rest.Controllers.Users;

import com.dani.roles.application.ports.input.PostServicePort;
import com.dani.roles.infrastructure.adapters.input.rest.mapper.PostRestMapper;
import com.dani.roles.infrastructure.adapters.input.rest.model.request.PostCreateRequest;
import com.dani.roles.infrastructure.adapters.input.rest.model.response.PostResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostRestController {
    private final PostServicePort servicePort;
    private final PostRestMapper restMapper;


    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public List<PostResponse> findAll(){
        return restMapper.toPostResponseList(servicePort.findAll());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PostResponse findById(@PathVariable Long id){
        return restMapper.toPostCreateRequest(servicePort.findById(id));
    }

    @GetMapping("/user/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<PostResponse> findByUserId(@PathVariable Long id){
        return restMapper.toPostResponseList(servicePort.findByUserId(id));
    }

    @GetMapping("/category/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<PostResponse> findByCategoryId(@PathVariable Long id){
        return restMapper.toPostResponseList(servicePort.findByCategoryId(id));
    }

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public PostResponse save(@RequestBody @Valid PostCreateRequest request){
        return restMapper.toPostCreateRequest(servicePort.save(restMapper.toPost(request)));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PostResponse update(@PathVariable Long id, @RequestBody @Valid PostCreateRequest request){
        // obtener los datos del author y categorias
        //System.out.println("request user id: " + request.getAuthorId());
        System.out.println("request category id: " + request.getCatIds());

        return restMapper.toPostCreateRequest(servicePort.update(id, restMapper.toPost(request)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id){
        servicePort.delete(id);
    }
}
