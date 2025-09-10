package ru.yandex.practicum.dto.warehouse;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressDto {
    private String country;
    private String city;
    private String street;
    private String house;
    private String flat;
}

