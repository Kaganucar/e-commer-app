package com.kagan.ecommerce.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record CustomerRequest(
         String id,
         @NotNull(message = "Customer firstname is required")
         String firstname,
         @NotNull(message = "Customer lastname is required")
         String lastname,
         @NotNull(message = "Customer email is required")
         @Email(message = "Customer email is not a valid email adress")
         String email,
         @NotNull(message = "Customer lastname is required")
         Adress adress
) {
}
