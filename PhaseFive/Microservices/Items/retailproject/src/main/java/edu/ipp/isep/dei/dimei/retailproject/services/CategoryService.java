package edu.ipp.isep.dei.dimei.retailproject.services;

import edu.ipp.isep.dei.dimei.retailproject.common.dto.gets.CategoryDTO;
import edu.ipp.isep.dei.dimei.retailproject.config.MessageBroker.Publisher;
import edu.ipp.isep.dei.dimei.retailproject.domain.model.Category;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.BadPayloadException;
import edu.ipp.isep.dei.dimei.retailproject.exceptions.NotFoundException;
import edu.ipp.isep.dei.dimei.retailproject.repositories.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final Publisher publisher;

    public List<CategoryDTO> getAllCategories() {
        List<CategoryDTO> categories = new ArrayList<>();

        this.categoryRepository.findAll()
                .forEach(category -> categories.add(new CategoryDTO(category)));

        return categories;
    }

    public CategoryDTO getCategory(int id) throws NotFoundException {
        Category category = getCategoryById(id);
        return new CategoryDTO(category);
    }

    private Category getCategoryById(int id) throws NotFoundException {
        return this.categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found"));
    }

    @Transactional(rollbackOn = Exception.class)
    public CategoryDTO createCategory(CategoryDTO categoryDTO) throws Exception {
        Category category = new Category(categoryDTO.getName(), categoryDTO.getDescription());

        category = this.categoryRepository.save(category);

        return new CategoryDTO(category);
    }

    public CategoryDTO updateCategory(int id, CategoryDTO categoryDTO) throws NotFoundException, BadPayloadException {
        Category category = getCategoryById(id);

        if (category.getId() != categoryDTO.getId()) {
            throw new BadPayloadException("Wrong category payload.");
        }

        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());

        category = this.categoryRepository.save(category);

        return new CategoryDTO(category);
    }

    public CategoryDTO deleteCategory(int id) throws NotFoundException {
        Category category = getCategoryById(id);

        this.categoryRepository.delete(category);

        return new CategoryDTO(category);
    }
}
