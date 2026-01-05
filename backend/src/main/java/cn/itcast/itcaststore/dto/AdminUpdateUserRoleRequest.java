package cn.itcast.itcaststore.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AdminUpdateUserRoleRequest {

    @NotBlank(message = "角色不能为空")
    private String role;
}
