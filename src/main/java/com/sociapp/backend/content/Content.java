package com.sociapp.backend.content;

import com.sociapp.backend.file.FileAttachment;
import com.sociapp.backend.user.User;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Entity
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "{sociapp.constraint.content.NotNull.message}")
    @Column(length = 1000)
    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp = new Date();

    @ManyToOne
    private User user;

    @OneToOne(mappedBy = "content", orphanRemoval = true)
    private FileAttachment fileAttachment;
}
