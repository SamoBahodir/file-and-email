package com.example.demo.entity.user;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.glassfish.grizzly.http.util.TimeStamp;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Setter
@Getter
@ToString
@Table(name = "telegram_users")
public class User {
    @Id
    private Long chatId;
    private String username;
    private String firstname;
    private String lastname;
    @CreationTimestamp
    private Date registeredAt;

}
