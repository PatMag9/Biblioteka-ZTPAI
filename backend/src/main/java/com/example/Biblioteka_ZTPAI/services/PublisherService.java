package com.example.Biblioteka_ZTPAI.services;

import com.example.Biblioteka_ZTPAI.dto.book.PublisherDTO;
import com.example.Biblioteka_ZTPAI.dto.book.admin.CreatePublisherDTO;
import com.example.Biblioteka_ZTPAI.mappers.PublisherMapper;
import com.example.Biblioteka_ZTPAI.models.Publisher;
import com.example.Biblioteka_ZTPAI.repositories.PublisherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class PublisherService {

    private final PublisherRepository publisherRepository;
    private final PublisherMapper publisherMapper;

    public Page<PublisherDTO> getPublishers(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());

        Page<Publisher> pageResult;
        if (name != null && !name.isBlank()) {
            pageResult = publisherRepository.findByNameContainingIgnoreCase(name, pageable);
        } else {
            pageResult = publisherRepository.findAll(pageable);
        }

        return pageResult.map(publisherMapper::toDTO);
    }

    public PublisherDTO createPublisher(CreatePublisherDTO dto) {
        if (publisherRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new IllegalArgumentException("Wydawnictwo już istnieje");
        }

        Publisher publisher = Publisher.builder()
                .name(dto.getName().trim())
                .build();

        return publisherMapper.toDTO(publisherRepository.save(publisher));
    }

    public void deletePublisher(Integer publisherId) {
        Publisher publisher = publisherRepository.findById(publisherId)
                .orElseThrow(() -> new NoSuchElementException("Wydawnictwo nie istnieje"));

        if (publisher.getBookCopies() != null && !publisher.getBookCopies().isEmpty()) {
            throw new IllegalStateException("Nie można usunąć wydawnictwa przypisanego do egzemplarzy");
        }

        publisherRepository.delete(publisher);
    }
}
