package ru.itis.producer;

import lombok.Builder;

@Builder
public class Email {
    private String email;
    private String userData;
}
