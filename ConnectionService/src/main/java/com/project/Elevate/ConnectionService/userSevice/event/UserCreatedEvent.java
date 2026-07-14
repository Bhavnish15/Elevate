package com.project.Elevate.ConnectionService.userSevice.event;

import lombok.Builder;
import lombok.Data;

@Data
public class UserCreatedEvent {

    private Long userId;
    private String name;

}
