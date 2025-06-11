package com.song.server2.service;

import java.util.List;

public record Menu(
        Long id,
        String name,
        String description,
        boolean deleted,
        Long systemCodeId,
        List<Menu> menu
) {
}
