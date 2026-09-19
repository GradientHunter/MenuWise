package com.menuwise.domain.menu;

import com.menuwise.domain.category.Category;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(nullable = false)
    private Double costPrice;

    @Column(nullable = false)
    private Double sellingPrice;

    @Column(nullable = false)
    private Integer prepTimeMin;

    @Version
    private Long version;
}
