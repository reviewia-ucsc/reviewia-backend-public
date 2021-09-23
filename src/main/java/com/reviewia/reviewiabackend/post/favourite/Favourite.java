package com.reviewia.reviewiabackend.post.favourite;

import com.reviewia.reviewiabackend.post.Post;
import com.reviewia.reviewiabackend.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Favourite {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String createdBy;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name= "fav_posts",
            joinColumns = {@JoinColumn(name = "fk_favourite")},
            inverseJoinColumns = {@JoinColumn(name = "fk_post")}
    )
    private Set<Post> posts = new HashSet<>();

    public Favourite(Post post, User user) {
        this.posts.add(post);
        this.createdBy = user.getEmail();
    }
}
