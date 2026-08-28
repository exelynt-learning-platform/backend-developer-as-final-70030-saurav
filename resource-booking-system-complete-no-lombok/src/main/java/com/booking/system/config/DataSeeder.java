package com.booking.system.config;

import com.booking.system.entity.Resource;
import com.booking.system.entity.User;
import com.booking.system.enums.Role;
import com.booking.system.repository.ResourceRepository;
import com.booking.system.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@Configuration
public class DataSeeder {

	@Bean
	CommandLineRunner seedData(UserRepository userRepository, ResourceRepository resourceRepository,
			PasswordEncoder passwordEncoder) {
		return args -> {
			if (userRepository.findByEmail("admin@booking.com").isEmpty()) {
				userRepository.save(
						new User("Admin User", "admin@booking.com", passwordEncoder.encode("Admin@123"), Role.ADMIN));
			}

			if (userRepository.findByEmail("user@booking.com").isEmpty()) {
				userRepository.save(
						new User("Normal User", "user@booking.com", passwordEncoder.encode("User@123"), Role.USER));
			}

			if (resourceRepository.count() == 0) {
				Resource r1 = new Resource();
				r1.setName("Conference Room");
				r1.setDescription("Large meeting room");
				r1.setAvailable(true);
				r1.setPrice(new BigDecimal("1500.00"));
				resourceRepository.save(r1);

				Resource r2 = new Resource();
				r2.setName("Company Vehicle");
				r2.setDescription("Vehicle for official travel");
				r2.setAvailable(true);
				r2.setPrice(new BigDecimal("2500.00"));
				resourceRepository.save(r2);
			}
		};
	}
}
