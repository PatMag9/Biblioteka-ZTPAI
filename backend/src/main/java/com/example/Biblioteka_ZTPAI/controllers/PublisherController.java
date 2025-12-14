package com.example.Biblioteka_ZTPAI.controllers;

import com.example.Biblioteka_ZTPAI.dto.book.PublisherDTO;
import com.example.Biblioteka_ZTPAI.dto.book.admin.CreatePublisherDTO;
import com.example.Biblioteka_ZTPAI.services.PublisherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/publishers")
public class PublisherController {

    private final PublisherService publisherService;

    @GetMapping
    public ResponseEntity<?> getPublishers(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<PublisherDTO> publishers = publisherService.getPublishers(name, page, size);

        if (publishers.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(publishers);
    }

    @PostMapping
    public ResponseEntity<?> createPublisher(
            @Valid @RequestBody CreatePublisherDTO dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(publisherService.createPublisher(dto));
    }

    @DeleteMapping("/{publisherId}")
    public ResponseEntity<?> deletePublisher(@PathVariable Integer publisherId) {
        publisherService.deletePublisher(publisherId);
        return ResponseEntity.noContent().build();
    }
}
