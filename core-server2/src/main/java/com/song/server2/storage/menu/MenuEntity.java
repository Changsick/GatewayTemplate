package com.song.server2.storage.menu;

import com.song.server2.service.Menu;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Table(name = "t_menu")
@Entity
public class MenuEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private boolean deleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private MenuEntity parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<MenuEntity> children = new ArrayList<>();

    private Long systemCodeId;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public Long getSystemCodeId() {
        return systemCodeId;
    }

    public MenuEntity getParent() {
        return parent;
    }

    public Menu toMenu(MenuEntity e){
        Menu self = new Menu(
                e.getId(),
                e.getName(),
                e.getDescription(),
                e.isDeleted(),
                e.getSystemCodeId(),
                new ArrayList<>()         // children 빈 리스트
        );

        // 2-2) 상위가 있으면 재귀 호출해서 올라가고, 그 DTO의 children에 지금 DTO 추가
        if (e.getParent() != null) {
            Menu parentTree = toMenu(e.getParent());
            parentTree.menu().add(self);
            return parentTree;       // 최종적으로는 최상위 DTO를 반환
        }

        // 2-3) 상위가 없으면 이게 곧 최상위
        return self;
    }

    public static List<Menu> toTrees(List<MenuEntity> leaves) {
        // LinkedHashMap으로 루트 ID 순서 보존
        Map<Long, Menu> rootMap = new LinkedHashMap<>();

        for (MenuEntity leaf : leaves) {
            // parent deleted 제외
            if (leaf.isDeleted() || (leaf.getParent() != null && leaf.getParent().isDeleted())) {
                continue;
            }

            // leaf → 최상위까지 DTO 트리 생성
            Menu tree = leaf.toMenu(leaf);
            Long rootId = tree.id();

            // 동일한 루트가 이미 있으면, child branches만 합치고
            // 없으면 새로 넣는다
            rootMap.merge(rootId, tree, (existing, incoming) -> {
                existing.menu().addAll(incoming.menu());
                return existing;
            });
        }

        return new ArrayList<>(rootMap.values());
    }

    public Menu toMenuWithChildren(){
        List<Menu> childrenDto = children.stream()
                .filter(c -> !c.isDeleted())
//                .sorted(Comparator.comparing(MenuEntity::getSortOrder))
                .map(MenuEntity::toMenuWithChildren)
                .toList();
        return new Menu(
                id,
                name,
                description,
                deleted,
                systemCodeId,
                childrenDto
        );
    }
}
