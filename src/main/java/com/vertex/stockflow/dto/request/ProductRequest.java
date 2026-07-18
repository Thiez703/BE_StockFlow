package com.vertex.stockflow.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * DỮ LIỆU FE GỬI LÊN khi tạo/sửa sản phẩm (dùng cho cả POST và PUT).
 * Không dùng thẳng Entity để không lộ cấu trúc DB và để đặt ràng buộc kiểm tra ngay tại đây.
 * Các annotation validation tự chạy khi Controller đánh dấu @Valid; sai thì trả lỗi 400.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequest {

    @NotBlank(message = "Barcode không được để trống")
    @Size(max = 20, message = "Barcode tối đa 20 ký tự")
    private String barcode;

    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(max = 255, message = "Tên sản phẩm tối đa 255 ký tự")
    private String name;

    // Danh mục KHÔNG bắt buộc (cột category_id cho phép null) -> để null cũng được.
    private Integer categoryId;

    // Đơn vị cơ bản BẮT BUỘC (cột base_unit_id NOT NULL).
    @NotNull(message = "Đơn vị cơ bản (baseUnitId) không được để trống")
    private Integer baseUnitId;

    @NotNull(message = "minStock không được để trống")
    @Min(value = 0, message = "minStock phải >= 0")
    private Integer minStock;

    @NotNull(message = "maxStock không được để trống")
    @Min(value = 0, message = "maxStock phải >= 0")
    private Integer maxStock;

    // Quy tắc "minStock <= maxStock" không đặt được bằng annotation nên kiểm tra trong Service.
}