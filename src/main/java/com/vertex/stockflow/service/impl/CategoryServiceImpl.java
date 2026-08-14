package com.vertex.stockflow.service.impl;

import com.vertex.stockflow.common.enums.StatusEnum;
import com.vertex.stockflow.dto.request.CategoryRequest;
import com.vertex.stockflow.dto.response.CategoryResponse;
import com.vertex.stockflow.entity.CategoryEntity;
import com.vertex.stockflow.entity.UserEntity;
import com.vertex.stockflow.exception.DuplicateResourceException;
import com.vertex.stockflow.exception.IllegalOperationException;
import com.vertex.stockflow.exception.ResourceNotFoundException;
import com.vertex.stockflow.mapper.CategoryMapper;
import com.vertex.stockflow.repository.CategoryRepository;
import com.vertex.stockflow.repository.ProductRepository;
import com.vertex.stockflow.repository.UserRepository;
import com.vertex.stockflow.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryResponse create(CategoryRequest request) {
        CategoryEntity parent = null;
        if (request.getParentId() != null) {
            parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new IllegalOperationException("Không tìm thấy danh mục cha (ID: " + request.getParentId() + "). Vui lòng chọn một danh mục cha khác hợp lệ."));
        }

        boolean duplicateName = (request.getParentId() == null)
                ? categoryRepository.existsByParentIsNullAndName(request.getName())
                : categoryRepository.existsByParentIdAndName(request.getParentId(), request.getName());
        if (duplicateName) {
            throw new DuplicateResourceException("Tên danh mục '" + request.getName() + "' đã tồn tại trong cùng cấp. Vui lòng chọn tên khác để tránh trùng lặp.");
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin tài khoản của bạn để xác thực phiên làm việc. Vui lòng đăng nhập lại."));

        CategoryEntity entity = CategoryEntity.builder()
                .name(request.getName())
                .parent(parent)
                .status(request.getStatus() != null ? request.getStatus() : StatusEnum.ACTIVE)
                .createdBy(currentUser)
                .updatedBy(currentUser)
                .build();

        return categoryMapper.toResponse(categoryRepository.save(entity));
    }

    @Override
    public CategoryResponse update(Integer id, CategoryRequest request) {
        CategoryEntity entity = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục (ID: " + id + "). Danh mục này có thể đã bị xóa."));

        CategoryEntity newParent = null;
        if (request.getParentId() != null) {
            newParent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new IllegalOperationException("Không tìm thấy danh mục cha (ID: " + request.getParentId() + "). Vui lòng chọn một danh mục cha khác hợp lệ."));

            // Kiểm tra vòng lặp: duyệt ngược lên từ parent mới, nếu gặp lại chính mình thì là cycle
            CategoryEntity current = newParent;
            while (current != null) {
                if (current.getId().equals(id)) {
                    throw new IllegalOperationException("Không thể thiết lập danh mục cha là chính nó hoặc là danh mục con/cháu của nó. Điều này sẽ gây lỗi vòng lặp.");
                }
                current = current.getParent();
            }
        }

        boolean duplicateName = (request.getParentId() == null)
                ? categoryRepository.existsByParentIsNullAndNameAndIdNot(request.getName(), id)
                : categoryRepository.existsByParentIdAndNameAndIdNot(request.getParentId(), request.getName(), id);
        if (duplicateName) {
            throw new DuplicateResourceException("Tên danh mục '" + request.getName() + "' đã tồn tại trong cùng cấp. Vui lòng chọn tên khác để tránh trùng lặp.");
        }

        entity.setName(request.getName());
        entity.setParent(newParent);
        if (request.getStatus() != null) {
            entity.setStatus(request.getStatus());
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        entity.setUpdatedBy(userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin tài khoản của bạn để xác thực phiên làm việc. Vui lòng đăng nhập lại.")));

        return categoryMapper.toResponse(categoryRepository.save(entity));
    }

    @Override
    public void delete(Integer id) {
        CategoryEntity entity = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục (ID: " + id + "). Danh mục này có thể đã bị xóa trước đó."));

        if (categoryRepository.existsByParentId(id)) {
            throw new IllegalOperationException("Không thể xóa danh mục này vì nó vẫn còn chứa các danh mục con bên trong. Vui lòng xóa danh mục con trước.");
        }
        if (productRepository.existsByCategoryId(id)) {
            throw new IllegalOperationException("Không thể xóa danh mục này vì đang có sản phẩm thuộc danh mục này. Vui lòng cập nhật sản phẩm sang danh mục khác trước khi xóa.");
        }

        categoryRepository.delete(entity);
    }

    @Override
    public CategoryResponse deactivate(Integer id) {
        CategoryEntity entity = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục (ID: " + id + "). Danh mục này có thể đã bị xóa."));
        entity.setStatus(StatusEnum.INACTIVE);
        return categoryMapper.toResponse(categoryRepository.save(entity));
    }

    @Override
    public CategoryResponse activate(Integer id) {
        CategoryEntity entity = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục (ID: " + id + "). Danh mục này có thể đã bị xóa."));
        entity.setStatus(StatusEnum.ACTIVE);
        return categoryMapper.toResponse(categoryRepository.save(entity));
    }

    @Override
    public CategoryResponse getById(Integer id) {
        CategoryEntity entity = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục (ID: " + id + "). Danh mục này có thể đã bị xóa."));
        return categoryMapper.toResponse(entity);
    }

    @Override
    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    @Override
    public List<CategoryResponse> getTree() {
        return categoryRepository.findByParentIsNull().stream()
                .map(this::buildTree)
                .toList();
    }

    // Đệ quy dựng cây — cần giữ riêng vì gọi lại chính nó
    private CategoryResponse buildTree(CategoryEntity entity) {
        List<CategoryResponse> children = categoryRepository.findByParentId(entity.getId()).stream()
                .map(this::buildTree)
                .toList();

        CategoryResponse response = categoryMapper.toResponse(entity);
        response.setChildren(children);
        return response;
    }
}
