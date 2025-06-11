package com.song.server2.storage.menu;

import com.song.server2.service.Menu;
import com.song.server2.service.MenuRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.NoSuchElementException;

@Repository
public class MenuEntityRepository implements MenuRepository {

    private final MenuQueryRepository menuQueryRepository;

    public MenuEntityRepository(MenuQueryRepository menuQueryRepository) {
        this.menuQueryRepository = menuQueryRepository;
    }

    @Override
    public List<Menu> findBySystemCodeId(Long systemCodeId) {
        return MenuEntity.toTrees(menuQueryRepository.findBySystemCodeId(systemCodeId));
    }

    @Override
    public Menu findById(Long id) {
        MenuEntity menu = menuQueryRepository.findByid(id).orElseThrow(() -> new NoSuchElementException("Not found Id : " + id));
        return menu.toMenuWithChildren();
    }
}
