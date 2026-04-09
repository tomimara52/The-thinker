package com.maullu.the_thinker.Controllers;

import com.maullu.the_thinker.Repository.IdeasRepository;
import com.maullu.the_thinker.Model.Idea;
import com.maullu.the_thinker.Model.Visibility;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/ideas")
@Tag(name = "Ideas", description = "Idea management endpoints")
public class IdeasController {

    private final IdeasRepository ideasRepository;

    public IdeasController(IdeasRepository ideasRepository){
        this.ideasRepository = ideasRepository;
    }

    @GetMapping
    @Operation(summary = "Get all ideas with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ideas retrieved successfully")
    })
    public ResponseEntity<List<Idea>> findAll(Pageable pageable){
        Page<Idea> page = ideasRepository.findAll(
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSort()
                )
        );
        return ResponseEntity.ok(page.getContent());
    }

    @GetMapping("{id}")
    @Operation(summary = "Get idea by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Idea found"),
        @ApiResponse(responseCode = "404", description = "Idea not found")
    })
    public ResponseEntity<Idea> findById(@PathVariable Long id){
        Optional<Idea> idea = ideasRepository.findById(id);
        return idea
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("visibility/{visibility}")
    @Operation(summary = "Get ideas by visibility level")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ideas retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid visibility value")
    })
    public ResponseEntity<?> findByVisibility(@PathVariable String visibility, Pageable pageable){
        try {
            Visibility visibilityEnum = Visibility.valueOf(visibility.toUpperCase());
            List<Idea> ideas = ideasRepository.findByVisibility(
                    visibilityEnum,
                    PageRequest.of(
                            pageable.getPageNumber(),
                            pageable.getPageSize(),
                            pageable.getSort()
                    )
            );
            return ResponseEntity.ok(ideas);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid visibility value. Must be one of: " + 
                String.join(", ", getValidVisibilities()));
        }
    }

    @PostMapping
    @Operation(summary = "Create a new idea")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Idea created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid idea data")
    })
    public ResponseEntity<Void> createIdea(@Valid @RequestBody Idea newIdea, UriComponentsBuilder ucb){
        Idea savedIdea = ideasRepository.save(newIdea);
        URI locationOfTheNewIdea = ucb
                .path("/ideas/{id}")
                .buildAndExpand(savedIdea.getId())
                .toUri();
        return ResponseEntity.created(locationOfTheNewIdea).build();
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partially update an idea")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Idea updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid update data"),
        @ApiResponse(responseCode = "404", description = "Idea not found")
    })
    public ResponseEntity<?> patchIdea(@PathVariable Long id, @RequestBody Map<String, Object> updates){
        Optional<Idea> optionalIdea = ideasRepository.findById(id);
        if (optionalIdea.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        try {
            Idea updatedIdea = optionalIdea.get();
            
            if (updates.containsKey("title")) {
                Object titleObj = updates.get("title");
                if (titleObj != null && !(titleObj instanceof String)) {
                    return ResponseEntity.badRequest().body("Title must be a string");
                }
                if (titleObj != null && ((String) titleObj).isBlank()) {
                    return ResponseEntity.badRequest().body("Title cannot be blank");
                }
                updatedIdea.setTitle((String) titleObj);
            }
            
            if (updates.containsKey("description")) {
                Object descObj = updates.get("description");
                if (descObj != null && !(descObj instanceof String)) {
                    return ResponseEntity.badRequest().body("Description must be a string");
                }
                updatedIdea.setDescription((String) descObj);
            }
            
            if (updates.containsKey("visibility")) {
                Object visObj = updates.get("visibility");
                if (visObj == null) {
                    return ResponseEntity.badRequest().body("Visibility cannot be null");
                }
                if (!(visObj instanceof String)) {
                    return ResponseEntity.badRequest().body("Visibility must be a string");
                }
                try {
                    updatedIdea.setVisibility(Visibility.valueOf((String) visObj));
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.badRequest().body("Invalid visibility value. Must be one of: " + 
                        String.join(", ", getValidVisibilities()));
                }
            }

            ideasRepository.save(updatedIdea);
            return ResponseEntity.noContent().build();
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating idea: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete idea by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Idea deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Idea not found")
    })
    public ResponseEntity<Void> deleteById(@PathVariable Long id){
        if(ideasRepository.existsById(id)){
            ideasRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    private String[] getValidVisibilities() {
        return java.util.Arrays.stream(Visibility.values())
            .map(Visibility::name)
            .toArray(String[]::new);
    }
}
