
package com.example.distributed_lovable.common_lib.enums;

import static com.example.distributed_lovable.common_lib.enums.ProjectPermission.*;

import java.util.Set;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Getter
public enum ProjectRole {
	EDITOR(VIEW, EDIT, DELETE, VIEW_MEMBERS),
    VIEWER(VIEW, VIEW_MEMBERS),
    OWNER(VIEW, DELETE, EDIT, MANAGE_MEMBERS, VIEW_MEMBERS);

    private final Set<ProjectPermission> permissions;

    ProjectRole(ProjectPermission... permissions) {
        this.permissions = Set.of(permissions);
    }
}
