create schema if not exists myblog;

create table myblog.posts
(
    id            serial primary key,
    title         text   not null,
    base_64_image text   not null,
    content       text   not null,
    likes         bigint not null default 0
);

create table myblog.comments
(
    id      serial primary key,
    content text   not null,
    post_id bigint not null references myblog.posts (id)
);

create index myblog_comments_post_id_index on myblog.comments (post_id);

create table myblog.tags
(
    id      serial primary key,
    content text not null
);

create table myblog.posts_tags
(
    post_id bigint not null references myblog.posts (id),
    tag_id  bigint not null references myblog.tags (id)
);

create index myblog_posts_tags_post_id_index on myblog.posts_tags (post_id);
create index myblog_posts_tags_tag_id_index on myblog.posts_tags (tag_id);