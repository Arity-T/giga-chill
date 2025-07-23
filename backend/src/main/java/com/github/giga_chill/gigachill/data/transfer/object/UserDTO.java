package com.github.giga_chill.gigachill.data.transfer.object;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO{private UUID id; private String login; private String name;}
