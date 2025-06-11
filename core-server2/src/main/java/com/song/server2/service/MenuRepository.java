package com.song.server2.service;

import java.util.List;

public interface MenuRepository {
    List<Menu> findBySystemCodeId(Long systemCodeId);

    Menu findById(Long id);
}
