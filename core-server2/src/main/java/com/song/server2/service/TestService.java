package com.song.server2.service;

import com.song.server2.exception.CustomException;
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
        if(true) throw new CustomException("TestService", "E001", "error message");
        return menuRepository.findById(id);
    }
}
