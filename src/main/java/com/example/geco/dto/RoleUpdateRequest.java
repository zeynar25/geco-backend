package com.example.geco.dto;

import com.example.geco.domains.Account.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleUpdateRequest {
	Integer accountId;
	Role role;
}
