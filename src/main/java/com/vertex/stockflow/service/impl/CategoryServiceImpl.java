package com.vertex.stockflow.service.impl;

import com.vertex.stockflow.common.enums.StatusEnum;
import com.vertex.stockflow.dto.request.CategoryRequest;
import com.vertex.stockflow.dto.response.CategoryResponse;
import com.vertex.stockflow.entity.CategoryEntity;
import com.vertex.stockflow.entity.UserEntity;
import com.vertex.stockflow.exception.BadRequestException;
import com.vertex.stockflow.exception.ConflictException;
import com.vertex.stockflow.exception.ResourceNotFoundException;
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

    @Override
    public CategoryResponse create(CategoryRequest request) {
        CategoryEntity parent = resolveParent(request.getParentId());

        // Ràng buộc nghiệp vụ: không cho phép 2 category cùng 1 cha có cùng tên
        // (khác cha thì vẫn được trùng tên, nên phải check theo đúng phạm vi parentId).
        if (isDuplicateName(request.getParentId(), request.getName(), null)) {
            throw new ConflictException("Tên category đã tồn tại trong cùng danh mục cha");
        }

        UserEntity currentUser = getCurrentUser();

        CategoryEntity entity = CategoryEntity.builder()
                .name(request.getName())
                .parent(parent)
                .status(request.getStatus() != null ? request.getStatus() : StatusEnum.ACTIVE)
                .createdBy(currentUser)
                .updatedBy(currentUser)
                .build();

        return mapToResponse(categoryRepository.save(entity));
    }

    @Override
    public CategoryResponse update(Integer id, CategoryRequest request) {
        CategoryEntity entity = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category không tồn tại"));

        CategoryEntity newParent = resolveParent(request.getParentId());

        // Chỉ kiểm tra vòng lặp khi category thực sự có cha mới (parentId != null),
        // vì đưa parent về null (thành category gốc) không bao giờ có thể tạo vòng lặp.
        if (newParent != null) {
            validateNoCycle(id, newParent);
        }

        // Loại trừ chính category đang sửa (id) khỏi check trùng tên, nếu không giữ nguyên tên
        // cũ cũng sẽ bị báo trùng với chính nó.
        if (isDuplicateName(request.getParentId(), request.getName(), id)) {
            throw new ConflictException("Tên category đã tồn tại trong cùng danh mục cha");
        }

        entity.setName(request.getName());
        entity.setParent(newParent);
        if (request.getStatus() != null) {
            entity.setStatus(request.getStatus());
        }
        entity.setUpdatedBy(getCurrentUser());

        return mapToResponse(categoryRepository.save(entity));
    }

    @Override
    public void delete(Integer id) {
        CategoryEntity entity = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category không tồn tại"));

        // Ràng buộc nghiệp vụ (cây cha-con): không cho xóa nếu category này đang là cha
        // của category khác. Nếu bỏ check này, thao tác xóa vẫn sẽ bị chặn nhờ constraint
        // ON DELETE RESTRICT khai báo ở CategoryEntity.parent, nhưng lỗi trả về sẽ là lỗi
        // SQL constraint thô (khó hiểu với client) thay vì thông báo nghiệp vụ rõ ràng.
        if (categoryRepository.existsByParentId(id)) {
            throw new ConflictException("Không thể xóa: category còn danh mục con");
        }

        // Ràng buộc nghiệp vụ: không cho xóa nếu còn Product đang tham chiếu category này.
        // Khác với quan hệ cha-con, Product.category_id không có ON DELETE RESTRICT tường minh
        // ở tầng DB, nên bắt buộc phải tự kiểm tra ở đây - nếu bỏ qua, Product sẽ bị "mồ côi"
        // (category_id trỏ tới một category đã không còn tồn tại).
        if (productRepository.existsByCategoryId(id)) {
            throw new ConflictException("Không thể xóa: category còn sản phẩm đang sử dụng");
        }

        categoryRepository.delete(entity);
    }

    @Override
    public CategoryResponse getById(Integer id) {
        return categoryRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Category không tồn tại"));
    }

    @Override
    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<CategoryResponse> getTree() {
        // Chỉ lấy category gốc (parent = null) rồi đệ quy load con theo từng cấp,
        // thay vì load toàn bộ bảng rồi tự ghép cây bằng tay trong memory.
        return categoryRepository.findByParentIsNull().stream()
                .map(this::mapToResponseWithChildren)
                .toList();
    }

    private CategoryEntity resolveParent(Integer parentId) {
        if (parentId == null) {
            return null; // không có cha -> category gốc
        }
        return categoryRepository.findById(parentId)
                .orElseThrow(() -> new BadRequestException("Category cha không tồn tại"));
    }

    // parentId = null (root) không thể dùng chung 1 query với parentId khác null vì SQL
    // "parent_id = NULL" luôn false - phải tách 2 nhánh IS NULL / = giá trị cụ thể.
    // excludeId = null khi tạo mới (chưa có id để loại trừ), khác null khi đang update.
    private boolean isDuplicateName(Integer parentId, String name, Integer excludeId) {
        if (parentId == null) {
            return excludeId == null
                    ? categoryRepository.existsByParentIsNullAndName(name)
                    : categoryRepository.existsByParentIsNullAndNameAndIdNot(name, excludeId);
        }
        return excludeId == null
                ? categoryRepository.existsByParentIdAndName(parentId, name)
                : categoryRepository.existsByParentIdAndNameAndIdNot(parentId, name, excludeId);
    }

    // Lấy user đang đăng nhập từ JWT (SecurityContext lưu email làm principal name - xem
    // CustomUserDetailsService) để gán vào createdBy/updatedBy. Không set trong @PrePersist
    // của entity vì entity không nên phụ thuộc trực tiếp vào Spring Security.
    private UserEntity getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User hiện tại không tồn tại"));
    }

    // Duyệt ngược lên từ parent mới theo chuỗi cha (newParent -> cha của nó -> ...).
    // Nếu trong chuỗi đó gặp lại chính category đang sửa (categoryId), nghĩa là category đó
    // đang cố nhận một trong các con/cháu của mình làm cha mới -> tạo vòng lặp vô hạn trong cây.
    // Đi từ dưới lên (O(độ sâu cây)) nhẹ hơn nhiều so với duyệt toàn bộ cây con từ trên xuống.
    private void validateNoCycle(Integer categoryId, CategoryEntity newParent) {
        CategoryEntity current = newParent;
        while (current != null) {
            if (current.getId().equals(categoryId)) {
                throw new BadRequestException("Không thể đặt category cha là chính nó hoặc là con cháu của nó");
            }
            current = current.getParent();
        }
    }

    private CategoryResponse mapToResponse(CategoryEntity entity) {
        return CategoryResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .parentId(entity.getParent() != null ? entity.getParent().getId() : null)
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy() != null ? entity.getCreatedBy().getEmail() : null)
                .updatedBy(entity.getUpdatedBy() != null ? entity.getUpdatedBy().getEmail() : null)
                .build();
    }

    // Map kèm children, gọi đệ quy để dựng cây đầy đủ nhiều cấp - chỉ dùng cho API lấy cây (getTree).
    private CategoryResponse mapToResponseWithChildren(CategoryEntity entity) {
        List<CategoryResponse> children = categoryRepository.findByParentId(entity.getId()).stream()
                .map(this::mapToResponseWithChildren)
                .toList();

        CategoryResponse response = mapToResponse(entity);
        response.setChildren(children);
        return response;
    }
}
