package com.song.server2.storage.menu;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.song.server2.storage.menu.QMenuEntity.menuEntity;

@Repository
public class MenuQueryRepository {

    private final JPAQueryFactory queryFactory;

    public MenuQueryRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public List<MenuEntity> findBySystemCodeId(Long systemCodeId) {
        return queryFactory
                .selectFrom(menuEntity)
                .leftJoin(menuEntity.parent)
                .fetchJoin()
                .where(
                        menuEntity.deleted.isFalse(),
                        menuEntity.systemCodeId.eq(systemCodeId)
                )
                .fetch();
    }

    public Optional<MenuEntity> findByid(Long id){
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(menuEntity)
                        .leftJoin(menuEntity.children)
                        .fetchJoin()
                        .where(
                                menuEntity.id.eq(id),
                                menuEntity.deleted.isFalse()
                        )
                        .fetchOne()
        );
    }
}
