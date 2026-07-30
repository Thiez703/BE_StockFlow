package com.vertex.stockflow.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SupplierRequest {
    @NotBlank(message = "Tên nhà cung cấp không được để trống")
    @Size(max = 100, message = "Tên nhà cung cấp tối đa 100 ký tự")
    private String name;

    @Size(max = 20, message = "Mã số thuế tối đa 20 ký tự")
    private String taxCode;

    @Size(max = 100, message = "Tên người liên hệ tối đa 100 ký tự")
    private String contactPerson;

    @Pattern(regexp = "^[0-9]{8,15}$", message = "Số điện thoại không hợp lệ")
    private String phone;

    @Email(message = "Email không hợp lệ")
    @Size(max = 100, message = "Email tối đa 100 ký tự")
    private String email;

    @Size(max = 255, message = "Địa chỉ tối đa 255 ký tự")
    private String address;

    @Size(max = 255, message = "Ghi chú tối đa 255 ký tự")
    private String note;
}
