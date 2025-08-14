package ru.practicum.category.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.dto.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    @Override
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        if (categoryRepository.existsByName(newCategoryDto.getName())) {
            log.warn("Категория - {} уже существует", newCategoryDto.getName());
            throw new ConflictException("Категория - " + newCategoryDto.getName() + " уже существует");
        }

        return CategoryMapper.toCategoryDto(categoryRepository.save(CategoryMapper.toCategory(newCategoryDto)));
    }

    @Transactional
    @Override
    public void deleteCategory(Long id) {
        findCategoryById(id);
        categoryRepository.deleteById(id);
    }

    @Transactional
    @Override
    public CategoryDto updateCategory(Long id, NewCategoryDto newCategoryDto) {
        Category category = findCategoryById(id);

        if (newCategoryDto.getName() != null && !category.getName().equals(newCategoryDto.getName())) {
            categoryRepository.findByName(newCategoryDto.getName())
                    .ifPresent(existing -> {
                        if (!existing.getId().equals(id)) {
                            log.warn("Категория с именем '{}' уже существует", newCategoryDto.getName());
                            throw new ConflictException("Категория с именем '" + newCategoryDto.getName() + "' уже существует");
                        }
                    });

            category.setName(newCategoryDto.getName());
        }

        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id").ascending());

        return categoryRepository.findAll(pageable)
                .stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategory(Long id) {
        return CategoryMapper.toCategoryDto(findCategoryById(id));
    }

    private Category findCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Категория с данным id: " + categoryId + " не найдена"));
    }

    // Продолжить реализацию после написания events
    private void validateCategoryIsEmpty(Long categoryId) {
        log.warn("Категория с данным id: {} используется", categoryId);
        throw new ConflictException("Категория используется");
    }
}
