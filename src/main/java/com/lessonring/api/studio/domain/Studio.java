package com.lessonring.api.studio.domain;

import com.lessonring.api.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "studio")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Studio extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String phone;

    private String address;

    private String detailAddress;

    private String timezone;

    private String businessNumber;

    @Enumerated(EnumType.STRING)
    private StudioStatus status;
}
