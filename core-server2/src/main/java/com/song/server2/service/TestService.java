package com.song.server2.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestService {

    private final MenuRepository menuRepository;

    public TestService(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    public List<Menu> getBySystemCodeId(Long systemCodeId) {
        return menuRepository.findBySystemCodeId(systemCodeId);
    }

    public Menu get(Long id) {
        return menuRepository.findById(id);
    }
}
