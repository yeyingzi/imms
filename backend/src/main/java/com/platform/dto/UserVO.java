package com.platform.dto;

import lombok.Data;
import java.util.List;

@Data
public class UserVO {
    private Long id;
    private String username;
    private String realName;
    private List<String> permissions;
    private List<String> roles;
    private String avatar;
}
